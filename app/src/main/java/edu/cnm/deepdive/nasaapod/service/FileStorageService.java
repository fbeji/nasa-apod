package edu.cnm.deepdive.nasaapod.service;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import edu.cnm.deepdive.nasaapod.ApodApplication;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Encapsulates several operations on files in internal and external storage, including writing
 * data from remote URLs to local files.
 */
public class FileStorageService {

  private static final String NO_PRIVATE_STORAGE_ERROR = "Unable to access private file storage.";
  private static final String SAVE_ERROR_LOG_MESSAGE = "Unable to save image";

  private static final int BUFFER_SIZE = 4096;

  private FileStorageService() {
  }

  /**
   * Returns the single instance of {@link FileStorageService}. Operations on external files use
   * public storage in the {@link Environment#DIRECTORY_PICTURES} directory.
   *
   * @return instance.
   */
  public static FileStorageService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  private OutputStream openInternalFile(Context context, String filename)
      throws FileNotFoundException {
    return context.openFileOutput(filename, Context.MODE_PRIVATE);
  }

  private OutputStream openExternalFile(String filename) throws FileNotFoundException {
    return new FileOutputStream(getExternalPicturePath(filename), false);
  }

  private File getExternalPicturePath(String filename) {
    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File file = new File(path, filename);
    path.mkdirs();
    return file;
  }

  /**
   * Constructs a <code>file://</code> URL from a file name, presumed relative to the base directory
   * of the app's internal storage. No check of the specified file's existence is performed.
   *
   * @param context runtime app context used for internal storage resolution.
   * @param filename name of the file for which a URL is to be constructed.
   * @return <code>file://</code>-scheme URL.
   */
  @Nullable
  public String internalUrlFromFilename(Context context, String filename) {
    try {
      return "file://" + new File(context.getFilesDir(), filename).toString();
    } catch (NullPointerException e) {
      Log.e(getClass().getSimpleName(), NO_PRIVATE_STORAGE_ERROR, e);
      return null;
    }
  }

  /**
   * Checks for the existence of the specified file, relative to the base directory of the app's
   * internal storage.
   *
   * @param context runtime app context used for internal storage resolution.
   * @param filename name of file to check for.
   * @return <code>true</code> if a file by the specified name exists, <code>false</code> otherwise.
   */
  public boolean internalFileExists(Context context, String filename) {
    try {
      return new File(context.getFilesDir(), filename).exists();
    } catch (NullPointerException e) {
      Log.e(getClass().getSimpleName(), NO_PRIVATE_STORAGE_ERROR, e);
      return false;
    }
  }

  /**
   * Deletes the specified file from the base directory of the app's internal storage.
   *
   * @param context runtime app context used for internal storage resolution.
   * @param filename name of file to delete.
   */
  public void deleteInternalFile(Context context, String filename) {
    context.deleteFile(filename);
  }

  /**
   * Extracts and returns the final component of the path portion of a URL, regardless of scheme.
   * Note that if the path portion of <code>url</code> ends with the forward slash character ("/"),
   * an empty string is returned.
   *
   * @param url URL from which filename is to be extracted.
   * @return base filename.
   */
  public String filenameFromUrl(String url) {
    String[] parts = url.split("[?#]")[0].split("/");
    return parts[parts.length - 1];
  }

  /**
   * Reads content from a URL, writing it to a file in internal or external storage. No attempt is
   * made to validate or otherwise process the content. The name of the file created is extracted
   * from the URL, using {@link #filenameFromUrl(String)}.
   *
   * @param url content source.
   * @param context runtime app context used for internal storage; <code>null</code> if external
   * storage should be used.
   */
  public void downloadToFile(String url, Context context) {
    try {
      String filename = filenameFromUrl(url);
      URLConnection connection = new URL(url).openConnection();
      try (
          OutputStream output = (context != null)
              ? openInternalFile(context, filename)
              : openExternalFile(filename);
          InputStream input = connection.getInputStream()
      ) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) > -1) {
          output.write(buffer, 0, bytesRead);
        }
        if (context != null) {
          File file = getExternalPicturePath(filename);
          MediaScannerConnection.scanFile(ApodApplication.getInstance(),
              new String[]{file.toString()}, null, (path, uri) -> {
              });
        }
      }
    } catch (IOException e) {
      Log.e(getClass().getSimpleName(), SAVE_ERROR_LOG_MESSAGE, e);
    }
  }

  private static class InstanceHolder {

    private static final FileStorageService INSTANCE = new FileStorageService();

  }

}
