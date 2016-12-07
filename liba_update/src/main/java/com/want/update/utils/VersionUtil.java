package com.want.update.utils;


import android.content.Context;
import android.content.pm.PackageManager;

/**
 * <b>Create Date:</b> 16/12/06<br>
 * <b>Author:</b> leon <br>
 * <b>Description:</b>
 * <p>
 * <br>
 */
public class VersionUtil {
    /**
     * 判断是否最新版本
     *
     * @param localVersion  本地版本号
     * @param onlineVersion 线上版本号
     * @return true 有更新 false 无更新
     */
    public static boolean compareVersion(String localVersion, String onlineVersion) {
        if (localVersion.equals(onlineVersion)) {
            return false;
        }
        String[] localArray = localVersion.split("\\.");
        String[] onlineArray = onlineVersion.split("\\.");

        int length = localArray.length < onlineArray.length ? localArray.length : onlineArray.length;

        for (int i = 0; i < length; i++) {
            if (Integer.parseInt(onlineArray[i]) > Integer.parseInt(localArray[i])) {
                return true;
            } else if (Integer.parseInt(onlineArray[i]) < Integer.parseInt(localArray[i])) {
                return false;
            }
            // 相等 比较下一组值
        }

        return true;
    }


    public static String getVersionName(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }

        return "";
    }
}
