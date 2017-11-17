package co.samsao.imagepickersample

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import co.samsao.imagepicker.ImagePicker
import co.samsao.imagepicker.Permissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CAMERA = 42
    }

    private lateinit var vehicleImageURI: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image_picker_pick_image.setOnClickListener({
            pickImage()
        })
    }

    private fun pickImage() = if (Permissions.hasPermission(this, Manifest.permission.CAMERA)) {
        vehicleImageURI = ImagePicker.startImagePicker(this, "myImage")
        image_picker_uri.setText(vehicleImageURI.toString())
    } else {
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            // TODO what do we do here?
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Toast.makeText(this, R.string.toast_camera_permission_previously_denied_redirect_settings, Toast.LENGTH_SHORT).show()
        } else {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.

                Toast.makeText(this, R.string.toast_permission_camera_features_disabled, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
