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

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;
import edu.cnm.deepdive.nasaapod.R;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;

public class InfoFragment extends DialogFragment {

  private static final String APOD_KEY = "apod";

  /**
   * Creates and returns an instance of {@link InfoFragment}, with <code>apod</code> included in an
   * attached {@link Bundle} of arguments.
   *
   * @param apod instance of {@link Apod} to pass to the {@link InfoFragment}.
   * @return new instance of {@link InfoFragment}.
   */
  public static InfoFragment newInstance(Apod apod) {
    InfoFragment fragment = new InfoFragment();
    Bundle args = new Bundle();
    args.putSerializable(APOD_KEY, apod);
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    Apod apod = (Apod) getArguments().getSerializable(APOD_KEY);
    View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_info, null);
    TextView explanation = view.findViewById(R.id.explanation);
    TextView url = view.findViewById(R.id.url);
    TextView hdUrl = view.findViewById(R.id.hd_url);
    explanation.setText(apod.getExplanation());
    url.setText(apod.getUrl());
    if (apod.getHdUrl() != null && !apod.getUrl().equals(apod.getHdUrl())) {
      hdUrl.setText(apod.getHdUrl());
    } else {
      hdUrl.setVisibility(View.GONE);
      view.findViewById(R.id.hd_url_label).setVisibility(View.GONE);
    }
    return new AlertDialog.Builder(getActivity())
        .setIcon(R.drawable.ic_info_filled)
        .setTitle(apod.getTitle())
        .setView(view)
        .setPositiveButton(R.string.positive_label, (dialog, which) -> {})
        .create();
  }

}






