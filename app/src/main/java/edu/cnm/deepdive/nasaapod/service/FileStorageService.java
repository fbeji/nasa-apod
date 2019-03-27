/*
 *  Copyright 2019 Nicholas Bennett & Deep Dive Coding
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.nasaapod.service;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
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
import java.util.Set;

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

  /**
   * Checks for the existence of the specified file, relative to the base directory of the app's
   * internal storage.
   *
   * @param context runtime app context used for internal storage resolution.
   * @param filename name of file to check for.
   * @return <code>true</code> if a file by the specified name exists, <code>false</code> otherwise.
   */
  public boolean fileExists(Context context, String filename) {
    try {
      return getFile(context, filename).exists();
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
  public void deleteFile(Context context, String filename) {
    if (context == null) {
      getFile(null, filename).delete();
    } else {
      context.deleteFile(filename);
    }
  }

  /**
   * Deletes all but the specified set of internal files. Filenames are assumed to be relative to
   * the base directory of the app's internal storage.
   *
   * @param context runtime app context used for internal storage resolution.
   * @param filenames MRU set of filenames to skip when deleting files from local storage cache.
   */
  public void keepInternalFiles(Context context, Set<String> filenames) {
    for (File file : context.getFilesDir().listFiles()) {
      if (!filenames.contains(file.getName())) {
        context.deleteFile(file.getName());
      }
    }
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
   * Reads content from an image URL, writing it (if possible) to a file in internal or external
   * storage. Non-image media content is ignored. No attempt is made to validate or otherwise
   * process the content. The name of the file created is extracted from the URL, using {@link
   * #filenameFromUrl(String)}.
   *
   * @param url content source.
   * @param context runtime app context used for internal storage; <code>null</code> if external
   * storage should be used.
   * @return resolved (local or remote) URL for media content.
   */
  public String download(String url, Context context) {
    String localUrl = url;
    try {
      String filename = filenameFromUrl(url);
      URLConnection connection = new URL(url).openConnection();
      File file = getFile(context, filename);
      try (
          OutputStream output = openFile(context, filename);
          InputStream input = connection.getInputStream();
      ) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) > -1) {
          output.write(buffer, 0, bytesRead);
        }
        localUrl = getUrlFromFile(file);
        if (context == null) {
          MediaScannerConnection.scanFile(ApodApplication.getInstance(),
              new String[]{file.toString()}, null, (path, uri) -> {});
        }
      }
    } catch (IOException e) {
      Log.e(getClass().getSimpleName(), SAVE_ERROR_LOG_MESSAGE, e);
    }
    return localUrl;
  }

  private OutputStream openFile(Context context, String filename) throws FileNotFoundException {
    return (context == null)
        ? new FileOutputStream(getFile(null, filename), false)
        : context.openFileOutput(filename, Context.MODE_PRIVATE);
  }

  private File getFile(Context context, String filename) {
    File directory;
    if (context == null) {
      directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
      directory.mkdirs();
    } else {
      directory = context.getFilesDir();
    }
    return new File(directory, filename);
  }

  private String getUrlFromFile(File file) {
    return String.format("file://%s", file);
  }

  private static class InstanceHolder {

    private static final FileStorageService INSTANCE = new FileStorageService();

  }

}
