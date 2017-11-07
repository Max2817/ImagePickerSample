package co.samsao.imagepicker;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;

import timber.log.Timber;

public final class ImagePicker {

  private static final int PICK_FROM_LIBRARY = 0;
  public static final int RESULT_OK = -1;
  public static final int REQUEST_CAMERA = 10;
  public static final int REQUEST_GALLERY = 20;
  private static final String CAPTURE_IMAGE_FILE_PROVIDER = BuildConfig.APPLICATION_ID + ".fileprovider";
  private static final String IMAGE_FILE_TYPE = "image/*";

  public static Uri startImagePicker(Activity activity, String fileName) {
    final CharSequence[] items = {
      activity.getString(R.string.choose_from_gallery), activity.getString(R.string.take_a_picture)
    };
    Uri currentPhotoPath = getOutputMediaFileUri(activity, fileName);
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle(activity.getString(R.string.choose_an_option));
    builder.setItems(items, (dialog, item) -> {
      if (item == PICK_FROM_LIBRARY) {
        takePictureFromLibrary(activity);
      } else {
        takePictureFromCamera(activity, currentPhotoPath);
      }
    });
    builder.show();
    return currentPhotoPath;
  }

  private static void takePictureFromCamera(Activity activity, Uri currentPhotoPath) {
    if (!Intents.isIntentAvailable(activity, MediaStore.ACTION_IMAGE_CAPTURE)) {
      Toast.makeText(activity, activity.getString(R.string.camera_not_available), Toast.LENGTH_LONG).show();
    } else {
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoPath);
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
        takePictureIntent.setClipData(ClipData.newRawUri("", currentPhotoPath));
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
      }
      activity.startActivityForResult(takePictureIntent, REQUEST_CAMERA);
    }
  }

  private static void takePictureFromLibrary(Activity activity) {
    Intent intent = new Intent();
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      intent.setAction(Intent.ACTION_GET_CONTENT);
    } else {
      intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
    }
    intent.setType(IMAGE_FILE_TYPE);
    activity.startActivityForResult(intent, REQUEST_GALLERY);
  }

  private static Uri getOutputMediaFileUri(Activity activity, String fileName) {
    return FileProvider.getUriForFile(activity, CAPTURE_IMAGE_FILE_PROVIDER, getOutputMediaFile(activity, fileName));
  }

  /** Create a File for saving an image */
  private static File getOutputMediaFile(Activity activity, String fileName) {
    File mediaStorageDir = new File(activity.getFilesDir(), "vehicle/image");

    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Timber.d("Failed to create image directory.");
        return null;
      }
    }

    return new File(mediaStorageDir.getPath() + "/" + fileName + ".jpg");

  }
}
