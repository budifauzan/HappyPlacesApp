package com.example.happyplacesapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.happyplacesapp.R
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.databinding.ActivityAddPlacesBinding
import com.example.happyplacesapp.model.HappyPlaceModel
import com.example.happyplacesapp.utils.GetAddressFromLatLong
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
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
        private const val PLACE_AUTO_COMPLETE_REQ_CODE = 3
    }

    private var binding: ActivityAddPlacesBinding? = null
    private var cal: Calendar? = null
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var mFusedLocationProvider: FusedLocationProviderClient
    private var imageURI: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0
    private var mHappyPlaceModel: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlacesBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setToolbar()

        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        cal = Calendar.getInstance()
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal?.set(Calendar.YEAR, year)
            cal?.set(Calendar.MONTH, month)
            cal?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateToView()
        }

        if (!Places.isInitialized()) {
            Places.initialize(this, resources.getString(R.string.google_maps_api_key))
        }

        updateDateToView()
        setViewFromIntent()
        binding?.edtDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
        binding?.edtLocation?.setOnClickListener(this)
        binding?.btnSelectCurrentLoc?.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
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
                            capturePhotoFromCamera()
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
            R.id.btn_save -> {
                when {
                    binding?.edtTitle?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a title!", Toast.LENGTH_SHORT).show()
                    }
                    binding?.edtDescription?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a description!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    binding?.edtLocation?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a location!", Toast.LENGTH_SHORT).show()
                    }
                    imageURI == null -> {
                        Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            if (mHappyPlaceModel == null) 0 else mHappyPlaceModel!!.id,
                            binding?.edtTitle?.text.toString(),
                            imageURI.toString(),
                            binding?.edtDescription?.text.toString(),
                            binding?.edtDate?.text.toString(),
                            binding?.edtLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val databaseHandler = DatabaseHandler(this)
                        if (mHappyPlaceModel == null) {
                            val result = databaseHandler.addHappyPlace(happyPlaceModel)
                            if (result > 0) {
                                Toast.makeText(
                                    this, "Data has been saved succesfully!", Toast.LENGTH_SHORT
                                ).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        } else {
                            val result = databaseHandler.editHappyPlace(happyPlaceModel)
                            if (result > 0) {
                                Toast.makeText(
                                    this, "Data has been saved succesfully!", Toast.LENGTH_SHORT
                                ).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }

                    }
                }
            }
            R.id.edt_location -> {
                try {
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
                    )
                    val intent =
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(this)
                    startActivityForResult(intent, PLACE_AUTO_COMPLETE_REQ_CODE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.btn_select_current_loc -> {
                if (!isLocationEnabled()) {
                    Toast.makeText(this, "Location permission is turned off", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                } else {
                    Dexter.withContext(this).withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            if (report!!.areAllPermissionsGranted()) {
                                getCurrentLocation()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: MutableList<PermissionRequest>?, token: PermissionToken?
                        ) {
                            showRationaleDialogForPermissions()
                        }
                    }).onSameThread().check()
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
        binding?.edtDate?.setText(sdf.format(cal!!.time).toString())
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
                binding?.ivThumbnail?.setImageBitmap(thumbnail)
            } else if (requestCode == PICK_FROM_GALLERY) {
                val contentURI = data!!.data
                try {
                    val selectedImageBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    imageURI = saveImagetoInternalStorage(selectedImageBitmap)
                    binding?.ivThumbnail?.setImageBitmap(selectedImageBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (requestCode == PLACE_AUTO_COMPLETE_REQ_CODE) {
                val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                binding?.edtLocation?.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
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

    private fun setViewFromIntent() {
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mHappyPlaceModel = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }
        if (mHappyPlaceModel != null) {
            supportActionBar?.title = "Edit Happy Place"
            binding?.edtTitle?.setText(mHappyPlaceModel?.title)
            binding?.edtDescription?.setText(mHappyPlaceModel?.description)
            binding?.edtDate?.setText(mHappyPlaceModel?.date)
            binding?.edtLocation?.setText(mHappyPlaceModel?.location)
            mLatitude = mHappyPlaceModel!!.latitude
            mLongitude = mHappyPlaceModel!!.longitude
            imageURI = Uri.parse(mHappyPlaceModel?.image)
            binding?.ivThumbnail?.setImageURI(imageURI)
            binding?.btnSave?.text = getString(R.string.add_places_activity_button_update)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private val mLocationCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            mLatitude = mLastLocation!!.latitude
            mLongitude = mLastLocation.longitude

            val addressTask = GetAddressFromLatLong(this@AddPlacesActivity, mLatitude, mLongitude)
            addressTask.setAdressListener(object : GetAddressFromLatLong.AddressListener {
                override fun onAddressFound(address: String?) {
                    binding?.edtLocation?.setText(address)
                }

                override fun onError() {
                    Log.e("Get address : ", "Something went wrong")
                }
            })
            addressTask.getAddress()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        locationRequest.numUpdates = 1

        mFusedLocationProvider.requestLocationUpdates(
            locationRequest, mLocationCallBack, Looper.myLooper()
        )
    }
}
