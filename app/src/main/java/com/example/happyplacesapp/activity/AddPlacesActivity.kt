package com.example.happyplacesapp.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.example.happyplacesapp.R
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.databinding.ActivityAddPlacesBinding
import com.example.happyplacesapp.model.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddPlacesActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val CAMERA = 1
        private const val PICK_FROM_GALLERY = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }

    private var binding: ActivityAddPlacesBinding? = null
    private var cal: Calendar? = null
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var imageURI: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlacesBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setToolbar()

        cal = Calendar.getInstance()
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal?.set(Calendar.YEAR, year)
            cal?.set(Calendar.MONTH, month)
            cal?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateToView()
        }
        updateDateToView()
        binding?.edtActivityAddPlacesDate?.setOnClickListener(this)
        binding?.tvActivityAddPlacesAddImage?.setOnClickListener(this)
        binding?.btnActivityAddPlacesSave?.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_activity_add_places_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> {
                            choosePhotoFromGallery()
                        }
                        1 -> {
                            capturePhotoFromCamera()
                        }
                    }
                }
                pictureDialog.show()
            }
            R.id.edt_activity_add_places_date -> {
                DatePickerDialog(
                    this,
                    dateSetListener,
                    cal!!.get(Calendar.YEAR),
                    cal!!.get(Calendar.MONTH),
                    cal!!.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.btn_activity_add_places_save -> {
                when {
                    binding?.edtActivityAddPlacesTitle?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a title!", Toast.LENGTH_SHORT).show()
                    }
                    binding?.edtActivityAddPlacesDescription?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a description!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    binding?.edtActivityAddPlacesLocation?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a location!", Toast.LENGTH_SHORT).show()
                    }
                    imageURI == null -> {
                        Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            0,
                            binding?.edtActivityAddPlacesTitle?.text.toString(),
                            imageURI.toString(),
                            binding?.edtActivityAddPlacesDescription?.text.toString(),
                            binding?.edtActivityAddPlacesDate?.text.toString(),
                            binding?.edtActivityAddPlacesLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val databaseHandler = DatabaseHandler(this)
                        val result = databaseHandler.addHappyPlace(happyPlaceModel)
                        if (result > 0) {
                            Toast.makeText(
                                this, "Data has been saved succesfully!", Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun updateDateToView() {
        val format = "MMMM dd, yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        binding?.edtActivityAddPlacesDate?.setText(sdf.format(cal!!.time).toString())
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report!!.areAllPermissionsGranted()) {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, PICK_FROM_GALLERY)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: MutableList<PermissionRequest>?, token: PermissionToken?
            ) {
                showRationaleDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun capturePhotoFromCamera() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report!!.areAllPermissionsGranted()) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: MutableList<PermissionRequest>?, token: PermissionToken?
            ) {
                showRationaleDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this).setMessage(
            "It looks like you have turned off permission required for this features. " + "It can be enabled under Applications Setting"
        ).setPositiveButton("GO TO SETTINGS") { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                imageURI = saveImagetoInternalStorage(thumbnail)
                binding?.ivActivityAddPlacesThumbnail?.setImageBitmap(thumbnail)
            } else if (requestCode == PICK_FROM_GALLERY) {
                val contentURI = data!!.data
                try {
                    val selectedImageBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    imageURI = saveImagetoInternalStorage(selectedImageBitmap)
                    binding?.ivActivityAddPlacesThumbnail?.setImageBitmap(selectedImageBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun saveImagetoInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(this)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
}
