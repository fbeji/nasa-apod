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

import static android.Manifest.permission.*;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import edu.cnm.deepdive.android.BaseFluentAsyncTask;
import edu.cnm.deepdive.android.DateTimePickerFragment;
import edu.cnm.deepdive.android.DateTimePickerFragment.Mode;
import edu.cnm.deepdive.nasaapod.R;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.nasaapod.service.ApodDBService;
import edu.cnm.deepdive.nasaapod.service.ApodWebService;
import edu.cnm.deepdive.nasaapod.service.FileStorageService;
import edu.cnm.deepdive.nasaapod.service.FragmentService;
import edu.cnm.deepdive.util.Date;
import java.util.Calendar;

/**
 * Primary controller class of the NASA APOD client app. This activity configures and then responds
 * to clicks in a {@link BottomNavigationView} to hide and show one of 2 main {@link
 * android.support.v4.app.Fragment} instances. It also responds to clicks on a single options {@link
 * MenuItem} (the fragments add more items to the options menu), to display a date picker for
 * selecting an APOD.
 */
public class NavActivity extends AppCompatActivity
    implements OnNavigationItemSelectedListener {

  private static final int REQUEST_WRITE_EXTERNAL_PERMISSION = 1000;

  private ImageFragment imageFragment;
  private HistoryFragment historyFragment;
  private ProgressBar loading;
  private BottomNavigationView navigation;
  private boolean downloadPermission;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nav);
    loading = findViewById(R.id.loading);
    setupFragments(savedInstanceState);
    checkPermissions();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_WRITE_EXTERNAL_PERMISSION) {
      if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        boolean needRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE);
        // TODO Present rationale.
        downloadPermission = false;
        invalidateOptionsMenu();
      } else {
        downloadPermission = true;
      }
    }
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
    FragmentService fragmentService = FragmentService.getInstance();
    boolean handled = true;
    switch (menuItem.getItemId()) {
      case R.id.navigation_image:
        fragmentService.showFragment(this, R.id.fragment_container, imageFragment);
        break;
      case R.id.navigation_history:
        fragmentService.showFragment(this, R.id.fragment_container, historyFragment);
        break;
      default:
        handled = false;
    }
    return handled;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.calendar:
        pickApodDate();
        break;
      case R.id.settings:
        editSettings();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  /**
   * Returns a flag indicating whether permission to write to external storage was granted, whether
   * implicitly (pre-API 26) or explicitly (API 26+).
   *
   * @return flag indicating permission to download to external storage.
   */
  public boolean hasDownloadPermission() {
    return downloadPermission;
  }

  /**
   * Loads the current day's APOD.
   */
  public void resetApod() {
    loadApod(Date.today());
  }

  /**
   * Switches the currently highlighted item in the bottom navigation view to the specified item.
   *
   * @param id navigation menu item ID to be selected.
   */
  public void setSelectedNavigationItemId(int id) {
    navigation.setSelectedItemId(id);
  }

  /**
   * Displays or hides the loading progress indicator.
   *
   * @param visible flag to display (<code>true</code>) or hide (<code>false</code>) the loading
   * indicator.
   */
  public void showLoading(boolean visible) {
    loading.setVisibility(visible ? View.VISIBLE : View.GONE);
  }

  /**
   * Instantiates {@link InfoFragment} to create and display an {@link
   * android.support.v7.app.AlertDialog}, showing information from the specified {@link Apod}
   * instance.
   *
   * @param apod instance of {@link Apod} for which information is to be displayed.
   */
  public void showFullInfo(Apod apod) {
    InfoFragment fragment = InfoFragment.newInstance(apod);
    fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
  }

  /**
   * Downloads the media content of an {@link Apod} instance, saving it to external storage.
   *
   * @param apod instance with URL referencing media content.
   */
  public void downloadApod(Apod apod) {
    showLoading(true);
    new BaseFluentAsyncTask<Void, Void, Void, Void>()
        .setPerformer((v) -> {
          String url = (apod.getHdUrl() != null) ? apod.getHdUrl() : apod.getUrl();
          FileStorageService.getInstance().download(url, null);
          return null;
        })
        .setSuccessListener((v) -> {
          showLoading(false);
        })
        .execute();
  }

  private void checkPermissions() {
    if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},
          REQUEST_WRITE_EXTERNAL_PERMISSION);
    } else {
      downloadPermission = true;
    }
  }

  private void setupFragments(Bundle savedInstanceState) {
    navigation = findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(this);
    FragmentService fragmentService = FragmentService.getInstance();
    if (savedInstanceState == null) {
      imageFragment = new ImageFragment();
      fragmentService.loadFragment(this, R.id.fragment_container, imageFragment,
          imageFragment.getClass().getSimpleName(), true);
      historyFragment = new HistoryFragment();
      fragmentService.loadFragment(this, R.id.fragment_container, historyFragment,
          historyFragment.getClass().getSimpleName(), false);
      resetApod();
    } else {
      imageFragment = (ImageFragment) fragmentService.findFragment(
          this, R.id.fragment_container, ImageFragment.class.getSimpleName());
      historyFragment = (HistoryFragment) fragmentService.findFragment(
          this, R.id.fragment_container, HistoryFragment.class.getSimpleName());
    }
    imageFragment.setHistoryFragment(historyFragment);
    historyFragment.setImageFragment(imageFragment);
  }

  private void pickApodDate() {
    Calendar calendar = Calendar.getInstance();
    if (imageFragment.isVisible() && imageFragment.getApod() != null) {
      calendar = imageFragment.getApod().getDate().toCalendar();
    }
    new DateTimePickerFragment()
        .setMode(Mode.DATE)
        .setCalendar(calendar)
        .setOnChangeListener((cal) -> loadApod(Date.fromCalendar(cal)))
        .show(getSupportFragmentManager(), DateTimePickerFragment.class.getSimpleName());
  }

  private void loadApod(Date date) {
    showLoading(true);
    new ApodDBService.SelectApodTask()
        .setSuccessListener(this::refresh)
        .setFailureListener((ignore) -> {
          new ApodWebService.GetFromNasaTask()
              .setTransformer((apod) -> {
                new ApodDBService.InsertApodTask()
                    .setSuccessListener(this::refresh)
                    .execute(apod);
                return apod;
              })
              .execute(date);
        })
        .execute(date);
  }

  private void refresh(Apod apod) {
    imageFragment.setApod(apod);
    historyFragment.refresh();
  }

  private void editSettings() {
    SettingsFragment fragment = new SettingsFragment();
    fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
  }

}
