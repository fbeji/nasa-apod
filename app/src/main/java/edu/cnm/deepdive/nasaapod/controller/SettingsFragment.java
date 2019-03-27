package edu.cnm.deepdive.nasaapod.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import edu.cnm.deepdive.nasaapod.R;

public class SettingsFragment extends DialogFragment implements OnSeekBarChangeListener {

  public static final String MRU_CACHE_SIZE_KEY = "mru_cache_size";

  private SharedPreferences settings;
  private SeekBar mruCacheSize;
  private TextView mruCacheSizeText;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings, null);
    settings = getActivity().getPreferences(Context.MODE_PRIVATE);
    mruCacheSizeText = view.findViewById(R.id.mru_cache_size_text);
    mruCacheSize = view.findViewById(R.id.mru_cache_size);
    mruCacheSize.setOnSeekBarChangeListener(this);
    int size = getMruCacheSize();
    mruCacheSize.setProgress(mruCacheSize.getMax());
    mruCacheSize.setProgress((size + mruCacheSize.getMax()) % (mruCacheSize.getMax() + 1));
    return new AlertDialog.Builder(getActivity())
        .setTitle(R.string.title_settings)
        .setView(view)
        .setPositiveButton(R.string.positive_label, (dialog, which) -> {
          putMruCacheSize((mruCacheSize.getProgress() + 1) % (mruCacheSize.getMax() + 1));
        })
        .setNegativeButton(R.string.negative_label, null)
        .create();
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    if (progress >= seekBar.getMax()) {
      mruCacheSizeText.setText(getString(R.string.unlimited));
    } else if (progress <= 0) {
      mruCacheSizeText.setText(getString(R.string.current));
    } else {
      mruCacheSizeText.setText(Integer.toString(progress + 1));
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {}

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {}

  private int getMruCacheSize() {
    return settings.getInt(MRU_CACHE_SIZE_KEY, 0);
  }

  private void putMruCacheSize(int size) {
    SharedPreferences.Editor editor = settings.edit();
    editor.putInt(MRU_CACHE_SIZE_KEY, size);
    editor.apply();
  }

}
