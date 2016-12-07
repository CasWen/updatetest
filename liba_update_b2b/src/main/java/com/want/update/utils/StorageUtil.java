package com.want.update.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.want.core.log.lg;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * <b>Project:</b> android-hollywant<br>
 * <b>Create Date:</b> 2016/10/12<br>
 * <b>Author:</b> ldc<br>
 * <b>Description:</b> <br>
 */
public class StorageUtil {

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            lg.w("UpdateFun TAG", "Can't define system cache directory! The app should be re-installed.");
        }
        return appCacheDir;
    }


    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                lg.w("UpdateFun TAG", "Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                lg.i("UpdateFun TAG", "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}
