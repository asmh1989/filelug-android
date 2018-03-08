package com.filelug.android.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.filelug.android.R;

/**
 * Created by Vincent Chang on 2015/12/28.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class WizardFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;
    private ImageView mImage;
    private TextView mText;

    public static WizardFragment newInstance(int position) {
        WizardFragment f = new WizardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wizard_getting_started, container, false);
        mImage = (ImageView) rootView.findViewById(R.id.wizard_image);
        mText = (TextView) rootView.findViewById(R.id.wizard_text);

        int imageRes;
        int textRes;

        if (position == 0) {
            imageRes = R.drawable.img_wizard_register;
            textRes = R.string.wizard_register;
        } else if (position == 1) {
            imageRes = R.drawable.img_wizard_download_desktop;
            textRes = R.string.wizard_download_desktop;
        } else if (position == 2) {
            imageRes = R.drawable.img_wizard_setup_desktop;
            textRes = R.string.wizard_setup_desktop;
        } else if (position == 3) {
            imageRes = R.drawable.img_wizard_connect_to_desktop;
            textRes = R.string.wizard_connect_to_desktop;
        } else {
            imageRes = R.drawable.img_wizard_start_to_transfer;
            textRes = R.string.wizard_start_to_transfer;
        }

        mImage.setImageResource(imageRes);
        mText.setText(textRes);

        return rootView;
    }

}
