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
 *
 */
public class FileStorageService {

  private static final String NO_PRIVATE_STORAGE_ERROR = "Unable to access private file storage.";
  private static final String SAVE_ERROR_LOG_MESSAGE = "Unable to save image";

  private static final int BUFFER_SIZE = 4096;

  private FileStorageService() {
  }

  /**
   * Returns the single instance of {@link FileStorageService}.
   *
   * @return instance.
   */
  public static FileStorageService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  private OutputStream openInternalFile(String filename) throws FileNotFoundException {
    return ApodApplication.getInstance().openFileOutput(filename, Context.MODE_PRIVATE);
  }

  private OutputStream openExternalFile(String filename) throws FileNotFoundException {
    return new FileOutputStream(getExternalPicturePath(filename));
  }

  private File getExternalPicturePath(String filename) {
    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File file = new File(path, filename);
    path.mkdirs();
    return file;
  }

  @Nullable
  public String internalUrlFromFilename(String filename) {
    try {
      return "file://" + new File(ApodApplication.getInstance().getFilesDir(), filename).toString();
    } catch (NullPointerException e) {
      Log.e(getClass().getSimpleName(), NO_PRIVATE_STORAGE_ERROR, e);
      return null;
    }
  }

  /**
   *
   * @param filename
   * @return
   */
  public boolean internalFileExists(String filename) {
    try {
      return new File(ApodApplication.getInstance().getFilesDir(), filename).exists();
    } catch (NullPointerException e) {
      Log.e(getClass().getSimpleName(), NO_PRIVATE_STORAGE_ERROR, e);
      return false;
    }
  }

  /**
   *
   * @param filename
   */
  public void deleteInternalFile(String filename) {
    ApodApplication.getInstance().deleteFile(filename);
  }

  /**
   *
   * @param url
   * @return
   */
  public String filenameFromUrl(String url) {
    String[] parts = url.split("\\?")[0].split("/");
    return parts[parts.length - 1];
  }

  /**
   *
   * @param url
   * @param internal
   */
  public void downloadToFile(String url, boolean internal) {
    try {
      String filename = filenameFromUrl(url);
      URLConnection connection = new URL(url).openConnection();
      try (
          OutputStream output = internal ? openInternalFile(filename) : openExternalFile(filename);
          InputStream input = connection.getInputStream()
      ) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) > -1) {
          output.write(buffer, 0, bytesRead);
        }
        if (!internal) {
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
