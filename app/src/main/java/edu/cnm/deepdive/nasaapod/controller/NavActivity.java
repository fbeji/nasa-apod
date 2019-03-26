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
        finish(); // TODO Something better!
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
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  /**
   * Returns a reference to the {@link BottomNavigationView} of this activity, allowing hosted
   * fragments to get/set the selected item.
   *
   * @return main navigation view of this activity.
   */
  public BottomNavigationView getNavigation() {
    return navigation;
  }

  /**
   * Returns a reference to the {@link ProgressBar} of this activity, allowing hosted fragments to
   * hide/show it.
   *
   * @return indeterminate progress spinner.
   */
  public ProgressBar getLoading() {
    return loading;
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

  private void checkPermissions() {
    boolean needPermission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED ;
    boolean needRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE);
    if (needPermission) {
      if (needRationale) {
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
      } else {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(this, new String[] {WRITE_EXTERNAL_STORAGE},
            REQUEST_WRITE_EXTERNAL_PERMISSION);
      }
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
        .setOnChangeListener((cal) -> imageFragment.loadApod(Date.fromCalendar(cal)))
        .show(getSupportFragmentManager(), DateTimePickerFragment.class.getSimpleName());
  }

  public void downloadApod(Apod apod) {
    getLoading().setVisibility(View.VISIBLE);
    new BaseFluentAsyncTask<Void, Void, Void, Void>()
        .setPerformer((v) -> {
          String url = (apod.getHdUrl() != null) ? apod.getHdUrl() : apod.getUrl();
          FileStorageService.getInstance().downloadToFile(url, false);
          return null;
        })
        .setSuccessListener((v) -> {
          getLoading().setVisibility(View.GONE);
        })
        .execute();
  }

}
