package com.filelug.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.filelug.android.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vincent Chang on 2016/1/4.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class PermissionsHelper {

    private static final String TAG = PermissionsHelper.class.getSimpleName();

    private static final List<String> ALL_PERMISSIONS = Arrays.asList(
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    );
    private static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.READ_EXTERNAL_STORAGE
    );
    private static final int MAX_PERMISSION_LABEL_LENGTH = 20;

    static List<String> getPermissionConstants(Context context) {
        return ALL_PERMISSIONS;
    }

    static List<String> getRequiredPermissionConstants(Context context) {
        return REQUIRED_PERMISSIONS;
    }

    public static List<PermissionInfo> getPermissions(Context context) {

        List<PermissionInfo> permissionInfoList = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        for (String permission : getPermissionConstants(context)) {
            PermissionInfo pinfo = null;
            try {
                pinfo = pm.getPermissionInfo(permission, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "getPermissions(), Name \'" + permission + "\' not found!");
                continue;
            }

            permissionInfoList.add(pinfo);
        }
        return permissionInfoList;
    }

    public static List<PermissionInfo> getRequiredPermissions(Context context) {

        List<PermissionInfo> permissionInfoList = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        for (String permission : getRequiredPermissionConstants(context)) {
            PermissionInfo pinfo = null;
            try {
                pinfo = pm.getPermissionInfo(permission, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "getRequiredPermissions(), Name \'" + permission + "\' not found!");
                continue;
            }

            permissionInfoList.add(pinfo);
        }
        return permissionInfoList;
    }

    public static PermissionGroupInfo getPermissionGroupInfo(Context context, String permissionGroup) {
        PackageManager pm = context.getPackageManager();
        PermissionGroupInfo permissionGroupInfo = null;
        try {
            permissionGroupInfo = pm.getPermissionGroupInfo(permissionGroup, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getPermissionGroupInfo(), Group name \'" + permissionGroup + "\' not found!");
        }
        return permissionGroupInfo;
    }

    private static CharSequence[] getPermissionNames(Context context) {
        PackageManager pm = context.getPackageManager();
        CharSequence[] names = new CharSequence[getPermissions(context).size()];
        int i = 0;
        for (PermissionInfo permissionInfo : getPermissions(context)) {
            CharSequence label = permissionInfo.loadLabel(pm);
            if (label.length() > MAX_PERMISSION_LABEL_LENGTH) {
                label = label.subSequence(0, MAX_PERMISSION_LABEL_LENGTH);
            }
            names[i] = label;
            i++;
        }
        return names;
    }

    private static CharSequence[] getRequiredPermissionNames(Context context) {
        PackageManager pm = context.getPackageManager();
        CharSequence[] names = new CharSequence[getRequiredPermissions(context).size()];
        int i = 0;
        for (PermissionInfo permissionInfo : getRequiredPermissions(context)) {
            CharSequence label = permissionInfo.loadLabel(pm);
            if (label.length() > MAX_PERMISSION_LABEL_LENGTH) {
                label = label.subSequence(0, MAX_PERMISSION_LABEL_LENGTH);
            }
            names[i] = label;
            i++;
        }
        return names;
    }

    private static boolean[] getPermissionsState(Context context) {
        boolean[] states = new boolean[getPermissionConstants(context).size()];
        int i = 0;
        for (String permission : getPermissionConstants(context)) {
            states[i] = isPermissionGranted(context, permission);
            i++;
        }
        return states;
    }

    private static boolean[] getRequiredPermissionsState(Context context) {
        boolean[] states = new boolean[getRequiredPermissionConstants(context).size()];
        int i = 0;
        for (String permission : getRequiredPermissionConstants(context)) {
            states[i] = isPermissionGranted(context, permission);
            i++;
        }
        return states;
    }

    public static void show(final Context context, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMultiChoiceItems(getPermissionNames(context),
                getPermissionsState(context),
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        ActivityCompat.requestPermissions(scanForActivity(context),
                                new String[]{getPermissionConstants(context).get(which)}, 23);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    public static boolean isAllFilelugPermissionGranted(Context context) {
        boolean result = true;
        boolean[] states = getPermissionsState(context);
        for ( boolean state : states ) {
            if ( !state ) {
                result = false;
                break;
            }
        }
        return result;
    }

    public static boolean isFilelugRequiredPermissionGranted(Context context) {
        boolean result = true;
        boolean[] states = getRequiredPermissionsState(context);
        for ( boolean state : states ) {
            if ( !state ) {
                result = false;
                break;
            }
        }
        return result;
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isPermissionGetAccountsGranted(Context context) {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isPermissionReceiveSMSGranted(Context context) {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isPermissionCameraGranted(Context context) {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isPermissionReadExternalStorageGranted(Context context) {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean areExplicitPermissionsRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static void show(final Context context) {
        show(context, null);
    }

    public static void refreshPermissionsState(Context context, MaterialDialog dialog, boolean closeWhenAllAllowed) {
//        if (Constants.DEBUG) Log.d(TAG, "refreshPermissionsState(), dialog=" + dialog + ", closeWhenAllAllowed=" + closeWhenAllAllowed);
        if (dialog == null) {
            return;
        }

        boolean isGetAccountsGranted = isPermissionGetAccountsGranted(context);
        boolean isReceiveSMSGranted = isPermissionReceiveSMSGranted(context);
        boolean isCameraGranted = isPermissionCameraGranted(context);
        boolean isReadExternalStorageGranted = isPermissionReadExternalStorageGranted(context);

        // Permission State
        TextView tvStateContacts = (TextView) dialog.getCustomView().findViewById(R.id.perm_state_contacts);
        TextView tvStateStorage = (TextView) dialog.getCustomView().findViewById(R.id.perm_state_storage);
        TextView tvStateSMS = (TextView) dialog.getCustomView().findViewById(R.id.perm_state_sms);
        TextView tvStateCamera = (TextView) dialog.getCustomView().findViewById(R.id.perm_state_camera);

        String strGranted = context.getResources().getString(R.string.permission_granted);
        String strNotGranted = context.getResources().getString(R.string.permission_not_granted);
        int colorGranted = context.getResources().getColor(R.color.main_color_grey_900);
        int colorNotGranted = context.getResources().getColor(R.color.main_color_grey_500);
        tvStateContacts.setText(isGetAccountsGranted ? strGranted : strNotGranted);
        tvStateContacts.setTextColor(isGetAccountsGranted ? colorGranted : colorNotGranted);
        tvStateStorage.setText(isReadExternalStorageGranted ? strGranted : strNotGranted);
        tvStateStorage.setTextColor(isReadExternalStorageGranted ? colorGranted : colorNotGranted);
        tvStateSMS.setText(isReceiveSMSGranted ? strGranted : strNotGranted);
        tvStateSMS.setTextColor(isReceiveSMSGranted ? colorGranted : colorNotGranted);
        tvStateCamera.setText(isCameraGranted ? strGranted : strNotGranted);
        tvStateCamera.setTextColor(isCameraGranted ? colorGranted : colorNotGranted);

        if (isGetAccountsGranted && isReadExternalStorageGranted && closeWhenAllAllowed) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
