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
package edu.cnm.deepdive.nasaapod.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import edu.cnm.deepdive.android.BaseFluentAsyncTask;
import edu.cnm.deepdive.nasaapod.R;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.nasaapod.service.ApodDBService.DeleteApodTask;
import edu.cnm.deepdive.nasaapod.service.ApodDBService.InsertAccessTask;
import edu.cnm.deepdive.nasaapod.service.ApodDBService.SelectMRUApodTask;
import edu.cnm.deepdive.nasaapod.service.FileStorageService;
import java.util.HashSet;
import java.util.Set;

/**
 * Populates a {@link WebView} with the image or video URL of the APOD for the currently rselected
 * date. If the {@link Apod} instance for the selected date is not in the local database, a request
 * is made to retrieve it from the NASA APOD web service.
 */
public class ImageFragment extends Fragment {

  private static final String APOD_KEY = "apod";

  private WebView webView;
  private Apod apod;
  private Apod savedApod;
  private HistoryFragment historyFragment;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_image, container, false);
    setupWebView(view);
    if (savedInstanceState != null) {
      savedApod = (Apod) savedInstanceState.getSerializable(APOD_KEY);
      if (savedApod != null) {
        setApod(savedApod);
      }
    }
    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.image_options, menu);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    menu.findItem(R.id.image_download).setVisible(
        apod != null && apod.isMediaImage() && getNavActivity().hasDownloadPermission());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.image_info:
        getNavActivity().showFullInfo(apod);
        break;
      case R.id.image_download:
        getNavActivity().downloadApod(apod);
        break;
      case R.id.image_delete:
        deleteApod(apod);
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(APOD_KEY, apod);
  }

  /**
   * Returns the currently displayed {@link Apod} instance.
   *
   * @return current {@link Apod} instance.
   */
  public Apod getApod() {
    return apod;
  }

  /**
   * Sets the {@link Apod} instance to be displayed.
   *
   * @param apod current {@link Apod} instance.
   */
  public void setApod(Apod apod) {
    this.apod = apod;
    getNavActivity().showLoading(true);
    new BaseFluentAsyncTask<Void, Void, String, String>()
        .setPerformer((ignore) -> locate(apod))
        .setSuccessListener((url) -> webView.loadUrl(url))
        .execute();
    if (!isHidden() && apod != savedApod) {
      new InsertAccessTask()
          .setTransformer((access) -> {
            keepMRU();
            return access;
          })
          .execute(apod);
    }
    getActivity().invalidateOptionsMenu();
  }

  /**
   * Sets the {@link HistoryFragment} to be refreshed on successful retrieval of an {@link Apod}
   * instance from the NASA APOD web service.
   *
   * @param historyFragment host {@link HistoryFragment} for list of {@link Apod} instances in local
   * database.
   */
  public void setHistoryFragment(HistoryFragment historyFragment) {
    this.historyFragment = historyFragment;
  }

  @SuppressLint("SetJavaScriptEnabled")
  private void setupWebView(View view) {
    webView = view.findViewById(R.id.web_view);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        getNavActivity().showLoading(false);
        if (isVisible()) {
          showInfo();
        }
      }
    });
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setSupportZoom(true);
    settings.setBuiltInZoomControls(true);
    settings.setDisplayZoomControls(false);
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);
  }

  private NavActivity getNavActivity() {
    return (NavActivity) getActivity();
  }

  private void showInfo() {
    if (apod != null && isVisible()) {
      Toast.makeText(getContext(), apod.getTitle(), Toast.LENGTH_LONG).show();
    }
  }

  private void showFailure() {
    getNavActivity().showLoading(false);
    Toast.makeText(getContext(), R.string.error_message, Toast.LENGTH_LONG).show();
  }

  private void deleteApod(Apod apod) {
    FileStorageService service = FileStorageService.getInstance();
    new DeleteApodTask()
        .setTransformer((v) -> {
          service.deleteFile(getContext(), service.filenameFromUrl(apod.getUrl()));
          return null;
        })
        .setSuccessListener((v) -> {
          setApod(null);
        })
        .execute(apod);
  }

  private String locate(Apod apod) {
    String url = apod.getUrl();
    if (apod.isMediaImage()) {
      Context context = getContext();
      FileStorageService service = FileStorageService.getInstance();
      String filename = service.filenameFromUrl(apod.getUrl());
      if (!service.fileExists(context, filename)) {
        url = service.download(apod.getUrl(), context);
      }
    }
    return url;
  }

  private void keepMRU() {
    SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
    int mruCacheSize = settings.getInt(SettingsFragment.MRU_CACHE_SIZE_KEY, 0);
    if (mruCacheSize > 0) {
      Context context = getContext();
      FileStorageService service = FileStorageService.getInstance();
      new SelectMRUApodTask()
          .setTransformer((mru) -> {
            Set<String> filenames = new HashSet<>();
            for (Apod a : mru) {
              filenames.add(service.filenameFromUrl(a.getUrl()));
            }
            service.keepInternalFiles(context, filenames);
            return mru;
          })
          .execute(mruCacheSize);
    }
  }

}
