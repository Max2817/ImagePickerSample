package co.samsao.imagepicker

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.widget.Toast
import timber.log.Timber
import java.io.File

object ImagePicker {

    private val PICK_FROM_LIBRARY = 0
    private val REQUEST_CAMERA = 10
    private val REQUEST_GALLERY = 20
    private val CAPTURE_IMAGE_FILE_PROVIDER = BuildConfig.APPLICATION_ID + ".fileprovider"
    private val IMAGE_FILE_TYPE = "image/*"
    private val FILE_PROVIDER_FOLDER = "images"

    fun startImagePicker(activity: Activity, fileName: String): Uri {
        val items = arrayOf<CharSequence>(activity.getString(R.string.choose_from_gallery), activity.getString(R.string.take_a_picture))
        val currentPhotoPath = getOutputMediaFileUri(activity, fileName)
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.choose_an_option))
        builder.setItems(items) { _, item ->
            if (item == PICK_FROM_LIBRARY) {
                takePictureFromLibrary(activity)
            } else {
                takePictureFromCamera(activity, currentPhotoPath)
            }
        }
        builder.show()
        return currentPhotoPath
    }

    private fun takePictureFromCamera(activity: Activity, currentPhotoPath: Uri) {
        if (!Intents.isIntentAvailable(activity, MediaStore.ACTION_IMAGE_CAPTURE)) {
            Toast.makeText(activity, activity.getString(R.string.camera_not_available), Toast.LENGTH_LONG).show()
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoPath)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                takePictureIntent.clipData = ClipData.newRawUri("", currentPhotoPath)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            activity.startActivityForResult(takePictureIntent, REQUEST_CAMERA)
        }
    }

    private fun takePictureFromLibrary(activity: Activity) {
        val intent = Intent()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.action = Intent.ACTION_GET_CONTENT
        } else {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
        }
        intent.type = IMAGE_FILE_TYPE
        activity.startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun getOutputMediaFileUri(activity: Activity, fileName: String): Uri =
            FileProvider.getUriForFile(activity, CAPTURE_IMAGE_FILE_PROVIDER, getOutputMediaFile(activity, fileName))

    /** Create a File for saving an image  */
    private fun getOutputMediaFile(activity: Activity, fileName: String): File? {
        val mediaStorageDir = File(activity.filesDir, FILE_PROVIDER_FOLDER)

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Timber.d("Failed to create image directory.")
                return null
            }
        }

        return File(mediaStorageDir.path + "/" + fileName + ".jpg")

    }
}
