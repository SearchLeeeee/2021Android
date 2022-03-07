package com.example.webviewapp.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.webviewapp.common.base.BaseApplication;
import com.example.webviewapp.data.DataManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class PermissionUtils {

    private static PermissionUtils sInstance;

    //    public static final long GAP_48_HOURS = 48 * 60 * 60 * 1000;
    public static final long GAP_48_HOURS = 10 * 1000; //TODO:test 10s
    public static final int OPEN_SET_REQUEST_CODE = 100;

    /**
     * 返回应用中已经获取的权限
     *
     * @return the permissions used in application
     */
    public static List<String> getPermissions() {
        return getPermissions(BaseApplication.getInstance().getPackageName());
    }

    /**
     * 返回应用中已经获取的权限
     *
     * @param packageName The name of the package.
     * @return the permissions used in application
     */
    public static List<String> getPermissions(final String packageName) {
        PackageManager pm = BaseApplication.getInstance().getPackageManager();
        try {
            String[] permissions = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
            if (permissions == null) return Collections.emptyList();
            return Arrays.asList(permissions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 判断是否已获得这些权限
     *
     * @param permissions The permissions.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isGranted(final String... permissions) {
        Pair<List<String>, List<String>> requestAndDeniedPermissions = getRequestAndDeniedPermissions(permissions);
        List<String> deniedPermissions = requestAndDeniedPermissions.second;
        if (!deniedPermissions.isEmpty()) {
            return false;
        }
        List<String> requestPermissions = requestAndDeniedPermissions.first;
        for (String permission : requestPermissions) {
            if (!isGranted(permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isGranted(final String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(BaseApplication.getInstance(), permission);
    }

    /**
     * 返回已获取的权限与被拒绝的权限
     *
     * @param permissionsParam 需要检查的权限
     * @return Pair<List < String> 已获取, List<String> 被拒绝>
     */
    private static Pair<List<String>, List<String>> getRequestAndDeniedPermissions(final String... permissionsParam) {
        List<String> requestPermissions = new ArrayList<>();
        List<String> deniedPermissions = new ArrayList<>();
        List<String> appPermissions = getPermissions();
        for (String param : permissionsParam) {
            boolean isIncludeInManifest = false;
            String[] permissions = PermissionConstants.getPermissions(param);
            for (String permission : permissions) {
                if (appPermissions.contains(permission)) {
                    requestPermissions.add(permission);
                    isIncludeInManifest = true;
                }
            }
            if (!isIncludeInManifest) {
                deniedPermissions.add(param);
                Log.e("PermissionUtils", "U should add the permission of " + param + " in manifest.");
            }
        }
        return Pair.create(requestPermissions, deniedPermissions);
    }

    /**
     * 打开应用具体设置界面
     */
    public static void launchAppDetailsSettings() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", BaseApplication.getInstance().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", BaseApplication.getInstance().getPackageName());
        }
        BaseApplication.getInstance().startActivity(intent);
    }

    public static final class PermissionsManager {
        private final Context context;

        /**
         * @param context 当前activity
         */
        public PermissionsManager(Context context) {
            this.context = context;
        }

        /**
         * 请求权限
         *
         * @param permissions
         */
        public void requestPermissions(final String... permissions) {
            for (String permission : permissions) {
                if (DataManager.get().queryDeniedPermission(permission) == null) {
                    requestPermission(permission);
                    continue;
                }
                long time = new Date().getTime() - DataManager.get().queryDeniedPermission(permission).time;
                if (time > GAP_48_HOURS) {
                    requestPermission(permission);
                } else if (time <= GAP_48_HOURS) {
                    DataManager.get().updateDeniedPermission(permission);
                }
            }
        }

        private void requestPermission(String permission) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, OPEN_SET_REQUEST_CODE);
            if (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                DataManager.get().deleteDeniedPermission(permission);
            } else {
                DataManager.get().addDeniedPermission(permission);
            }
        }
    }
}