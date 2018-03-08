package com.filelug.android.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.ui.widget.NoScrollViewPager;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Chang on 2017/1/9.
 * Copyright (c) 2017 Filelug. All rights reserved.
 */
public class AddNewComputerActivity extends BaseConfigureActivity {

    private static final String TAG = AddNewComputerActivity.class.getSimpleName();

    private static final String ARG_POSITION = "position";

    private Toolbar mToolbar;
    private ViewGroup mLayout;
    private PagerAdapter mAdapter;
    private NoScrollViewPager mPager;
    private TextView mPreviousButton;
    private TextView mNextButton;
    private TextView mStepText;
    private int mCurrentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Constants.DEBUG) Log.d(TAG, "onCreate(): savedInstanceState=" + MiscUtils.convertBundleToString(savedInstanceState));
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        setContentView(R.layout.layout_add_new_computer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setTitle(R.string.page_add_new_computer);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentItem = 0;

        mLayout = (ViewGroup) findViewById(R.id.add_new_computer_layout);
        mPager = (NoScrollViewPager) findViewById(R.id.add_step_pager);
        mPreviousButton = (TextView) findViewById(R.id.previous_button);
        mNextButton = (TextView) findViewById(R.id.next_button);
        mStepText = (TextView) findViewById(R.id.step_text);

        mAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(mCurrentItem);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
//                if (Constants.DEBUG) Log.d(TAG, "onPageSelected(): position=" + position);
                mCurrentItem = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int position) {
                setNavigator();
            }

        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPager.getCurrentItem() > 0) {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                }
                setNavigator();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPager.getCurrentItem() != (mPager.getAdapter().getCount() - 1)) {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
                setNavigator();
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setNavigator();
        checkCameraPermission();
    }

    public void setNavigator() {
        String text = null;
        int position = mPager.getCurrentItem();
        String label_step1 = getResources().getString(R.string.label_add_computer_step_1_install);
        String label_step2 = getResources().getString(R.string.label_add_computer_step_2_startup);
        String label_step3 = getResources().getString(R.string.label_add_computer_step_3_scan);
        String styled_text = getResources().getString(R.string.const_styled_text_add_computer_step);
        if ( position == 0 ) {
            text = label_step1 + " " + label_step2 + " " + label_step3;
        } else if ( position == 1 ) {
            text = String.format(styled_text, label_step1) + " " + label_step2 + " " + label_step3;
        } else if ( position == 2 ) {
            text = label_step1 + " " + String.format(styled_text, label_step2) + " " + label_step3;
        } else if ( position == 3 ) {
            text = label_step1 + " " + label_step2 + " " + String.format(styled_text, label_step3);
        }
        mStepText.setText(text == null ? "" : Html.fromHtml(text));

        boolean canMoveToPrevious = position > 0;
        boolean canMoveToNext = position < (mAdapter.getCount()-1);
//        if (Constants.DEBUG) Log.d(TAG, "setNavigator(): position=" + position + ", canMoveToPrevious=" + canMoveToPrevious + ", canMoveToNext=" + canMoveToNext);
        mPreviousButton.setVisibility(canMoveToPrevious ? View.VISIBLE : View.INVISIBLE);
        mPreviousButton.setEnabled(canMoveToPrevious);
        mNextButton.setVisibility(canMoveToNext ? View.VISIBLE : View.INVISIBLE);
        mNextButton.setEnabled(canMoveToNext);
    }

    private void checkCameraPermission() {
        PackageManager pm = this.getPackageManager();
        if ( !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ) {
            mNextButton.setVisibility(View.INVISIBLE);
            mNextButton.setEnabled(false);
            MsgUtils.showWarningMessage(this, R.string.message_can_not_use_cameras);
            return;
        }

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if ( permission != PackageManager.PERMISSION_GRANTED ) {
            mNextButton.setVisibility(View.INVISIBLE);
            mNextButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, Constants.REQUEST_PERMISSION_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if ( requestCode == Constants.REQUEST_PERMISSION_CAMERA ) {
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                mNextButton.setVisibility(View.VISIBLE);
                mNextButton.setEnabled(true);
            } else {
                MsgUtils.showWarningMessage(this, R.string.message_can_not_use_cameras);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION, this.mCurrentItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPager.setCurrentItem(savedInstanceState.getInt(ARG_POSITION, 0));
        setNavigator();
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        private Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
//            if (Constants.DEBUG) Log.d(TAG, "getItem(): position=" + position);
            Fragment fragment = null;
            if ( fragmentMap.containsKey(position) ) {
                fragment = fragmentMap.get(position);
            } else {
                if ( position == 3 ) {
                    fragment = ScannerFragment.newInstance();
                } else {
                    fragment = AddComputerStepFragment.newInstance(position);
                }
                fragmentMap.put(position, fragment);
            }
            return fragment;
        }

    }

    @Override
    public void onBackPressed() {
        int currentIndex = mPager.getCurrentItem();
        if ( currentIndex > 0 ) {
            mPager.setCurrentItem(currentIndex-1);
            return;
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    public static class AddComputerStepFragment extends Fragment {

        private int position;
        private ImageView mStepImage;
        private LinearLayout mDescriptionLayout;
        private TextView mStepNumber;
        private TextView mStepMessage;

        private int scannedCount = 0;

        public static AddComputerStepFragment newInstance(int position) {
//            if (Constants.DEBUG) Log.d(TAG, "AddComputerStepFragment.newInstance(): position=" + position);
            AddComputerStepFragment f = new AddComputerStepFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments().getInt(ARG_POSITION);
//            if (Constants.DEBUG) Log.d(TAG, "AddComputerStepFragment.onCreate(): position=" + position);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            if (Constants.DEBUG) Log.d(TAG, "AddComputerStepFragment.onCreateView(): savedInstanceState=" + MiscUtils.convertBundleToString(savedInstanceState));
            View rootView = inflater.inflate(R.layout.fragment_add_new_computer_step, container, false);
            mStepImage = (ImageView) rootView.findViewById(R.id.step_image);
            mDescriptionLayout = (LinearLayout) rootView.findViewById(R.id.layout_description);
            mStepNumber = (TextView) rootView.findViewById(R.id.step_number);
            mStepMessage = (TextView) rootView.findViewById(R.id.step_message);

            int imageRes;
            String stepNumber;
            int stepMessageRes;

            if (position == 0) {
                imageRes = R.drawable.header_ic_add_computer;
                stepNumber = "";
                stepMessageRes = R.string.message_add_new_computer_step_0;
            } else if (position == 1) {
                imageRes = R.drawable.header_ic_download_desktop_installer;
                stepNumber = "1";
                stepMessageRes = R.string.message_add_new_computer_step_1;
            } else {
                imageRes = R.drawable.header_ic_run_desktop_installer;
                stepNumber = "2";
                stepMessageRes = R.string.message_add_new_computer_step_2;
            }

            mStepImage.setImageResource(imageRes);

//            if (Constants.DEBUG) Log.d(TAG, "AddComputerStepFragment.onCreateView(), position=" + position + ", stepMessage=" + stepNumber + stepMessage);

            mStepNumber.setText(stepNumber);
            mStepNumber.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            mStepMessage.setText(stepMessageRes);

            return rootView;
        }

    }

    public static class ScannerFragment extends Fragment {

        private DecoratedBarcodeView mQRCodeScannerView;
        private BeepManager beepManager;

        private boolean isSendingToRepo = false;

        public static ScannerFragment newInstance() {
//            if (Constants.DEBUG) Log.d(TAG, "ScannerFragment.newInstance()");
            return new ScannerFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            if (Constants.DEBUG) Log.d(TAG, "ScannerFragment.onCreate(): savedInstanceState=" + MiscUtils.convertBundleToString(savedInstanceState));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            if (Constants.DEBUG) Log.d(TAG, "ScannerFragment.onCreateView(): savedInstanceState=" + MiscUtils.convertBundleToString(savedInstanceState));
            View rootView = inflater.inflate(R.layout.fragment_qrcode_scanner, container, false);
            mQRCodeScannerView = (DecoratedBarcodeView) rootView.findViewById(R.id.qrcodeScanner);

            Activity activity = getActivity();
            String promptMessage = activity.getResources().getString(R.string.message_add_new_computer_step_3);

            Intent intent = activity.getIntent();
            intent.putExtra(Intents.Scan.MODE, Intents.Scan.QR_CODE_MODE);
            intent.putExtra(Intents.Scan.ORIENTATION_LOCKED, Boolean.TRUE);
            intent.putExtra(Intents.Scan.BEEP_ENABLED, Boolean.TRUE);
            intent.putExtra(Intents.Scan.PROMPT_MESSAGE, promptMessage);
            mQRCodeScannerView.initializeFromIntent(intent);
            mQRCodeScannerView.decodeContinuous(continuousCallback);
            beepManager = new BeepManager(getActivity());

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
//            if (Constants.DEBUG) Log.d(TAG, "ScannerFragment.onResume()");
            mQRCodeScannerView.resume();
        }

        @Override
        public void onPause() {
            super.onPause();
//            if (Constants.DEBUG) Log.d(TAG, "ScannerFragment.onPause()");
            mQRCodeScannerView.pause();
        }

        private BarcodeCallback continuousCallback = new BarcodeCallback() {

            @Override
            public void barcodeResult(BarcodeResult result) {
//                if (Constants.DEBUG) Log.d(TAG, "ScannerFragment.BarcodeCallback.barcodeResult(): result=" + result == null ? "" : result.getText());
                if ( result.getText() == null || isSendingToRepo ) {
                    // Prevent duplicate scans
                    return;
                }
                String scannedText = result.getText();
                if ( !scannedText.startsWith("FILELUG_") ) {
                    return;
                }

                getAuthToken(scannedText);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
//                if (Constants.DEBUG) Log.d(TAG, "ScannerFragment.BarcodeCallback.possibleResultPoints()");
            }

            private void getAuthToken(final String qrcode) {
                final Activity activity = getActivity();
                if ( !NetworkUtils.isNetworkAvailable(activity) ) {
                    return;
                }
                AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
                    @Override
                    public void onError(String errorMessage) {
                        MsgUtils.showWarningMessage(activity, errorMessage);
                    }
                    @Override
                    public void onSuccess(String authToken) {
                        sendQRCodeToRepo(activity, authToken, qrcode);
                    }
                };
                AccountUtils.getAuthToken(activity, callback);
            }

            private void sendQRCodeToRepo(final Activity activity, String authToken, String qrcode) {

                isSendingToRepo = true;
                String locale = activity.getResources().getConfiguration().locale.toString();
                beepManager.playBeepSoundAndVibrate();

                RepositoryClient.getInstance().createComputerWithQRCode(
                    authToken,
                    qrcode,
                    locale,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String userId = response.optString(Constants.PARAM_USER_ID);
                            int computerId = response.optInt(Constants.PARAM_COMPUTER_ID, -1);
                            String computerName = response.optString(Constants.PARAM_COMPUTER_NAME, null);
                            String computerGroup = response.optString(Constants.PARAM_COMPUTER_GROUP, null);
                            String lugServerId = response.optString(Constants.PARAM_LUG_SERVER_ID, null);
                            String userComputerId = response.optString(Constants.PARAM_USER_COMPUTER_ID);
                            String computerAdminId = response.optString(Constants.PARAM_COMPUTER_ADMIN_ID);

                            Intent intent = new Intent();
                            intent.putExtra(Constants.PARAM_USER_ID, userId);
                            intent.putExtra(Constants.PARAM_COMPUTER_ID, computerId);
                            intent.putExtra(Constants.PARAM_COMPUTER_NAME, computerName);
                            intent.putExtra(Constants.PARAM_COMPUTER_GROUP, computerGroup);
                            intent.putExtra(Constants.PARAM_USER_COMPUTER_ID, userComputerId);
                            intent.putExtra(Constants.PARAM_COMPUTER_ADMIN_ID, computerAdminId);
                            intent.putExtra(Constants.PARAM_LUG_SERVER_ID, lugServerId);

                            activity.setResult(RESULT_OK, intent);
                            activity.finish();
                            activity.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                        }
                    },
                    new BaseResponseError(true, activity) {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            int statusCode = MiscUtils.getStatusCode(volleyError);
                            if ( statusCode == Constants.HTTP_STATUS_CODE_NOT_IMPLEMENTED ) {
                                MsgUtils.showWarningMessage(context, R.string.message_expired_qrcode_and_need_refresh, new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        isSendingToRepo = false;
                                    }
                                });
                                return;
                            }
                            super.onErrorResponse(volleyError);
                        }

                        protected void afterShowErrorMessage(VolleyError volleyError) {
                            isSendingToRepo = false;
                        }
                    }
                );
            }

        };

    }

}
