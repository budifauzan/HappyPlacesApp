package com.example.happyplacesapp

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.example.happyplacesapp.databinding.ActivityAddPlacesBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*

class AddPlacesActivity : AppCompatActivity(), View.OnClickListener {
    private var mActivity: ActivityAddPlacesBinding? = null
    private var cal: Calendar? = null
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = ActivityAddPlacesBinding.inflate(layoutInflater)
        setContentView(mActivity?.root)
        setToolbar()

        cal = Calendar.getInstance()
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal?.set(Calendar.YEAR, year)
            cal?.set(Calendar.MONTH, month)
            cal?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateToView()
        }
        mActivity?.edtDate?.setOnClickListener(this)
        mActivity?.tvAddImage?.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_add_image -> {
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
                            
                        }
                    }
                }
                pictureDialog.show()
            }
            R.id.edt_date -> {
                DatePickerDialog(
                    this,
                    dateSetListener,
                    cal!!.get(Calendar.YEAR),
                    cal!!.get(Calendar.MONTH),
                    cal!!.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        }
    }

    private fun setToolbar() {
        setSupportActionBar(mActivity?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun updateDateToView() {
        val format = "MMMM dd, yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        mActivity?.edtDate?.setText(sdf.format(cal!!.time).toString())
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report!!.areAllPermissionsGranted()) {
                    Toast.makeText(
                        this@AddPlacesActivity, "Storage READ/WRITE is granted", Toast.LENGTH_SHORT
                    ).show()
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
}
