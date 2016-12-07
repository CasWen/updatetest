package com.want.update.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.SharedPreferencesCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



/**
 * <b>Create Date:</b> 16/12/06<br>
 * <b>Author:</b> leon <br>
 * <b>Description:</b>
 * update tools
 * <br>
 */
public class UpdateUtil {

    public interface Constants{
        String UPDATE_REQUEST_LIMIT_TIMES = "UPDATE_REQUEST_LIMIT_TIMES";//请求提醒次数
        String UPDATE_COUNT = "UPDATE_COUNT"; //提醒次数
        String UPDATE_TIMES = "UPDATE_TIMES"; //最后一次时间
    }

    private static SharedPreferences sp;
    private static final String SP_NAME = "sp";


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

    /**
     * 获取应用名称
     *
     * @param context
     * @return 返回应用名称
     */
    public static String getAppName(Context context) {
        if (null != context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo appInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                return (String) packageManager.getApplicationLabel(appInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }



    public static int getVersionCode(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return 0;
    }

    /**
     * 获取应用版本号
     *
     * @param mContext
     * @return 版本号
     */
    public static String getVersionName(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }

        return "";
    }

    public static boolean isHttpUrl(String url) {
        return (null != url) && (url.length() > 6)
                && url.substring(0, 7).equalsIgnoreCase("http://");
    }

    public static boolean isHttpsUrl(String url) {
        return (null != url) && (url.length() > 7)
                && url.substring(0, 8).equalsIgnoreCase("https://");
    }

    public static boolean isNetworkUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }
        return isHttpUrl(url) || isHttpsUrl(url);
    }

    public static void setUpdateRequestLimitTimes(Context context, String time) {
        setStringKey(context, Constants.UPDATE_REQUEST_LIMIT_TIMES, time);
    }

    public static String getUpdateRequestLimitTimes(Context context) {
        return getStringKey(context, Constants.UPDATE_REQUEST_LIMIT_TIMES);
    }


    /**
     * 设置更新的提醒时间
     *
     * @param context
     * @param time
     */
    public static void setUpdateTime(Context context, String time) {
        setStringKey(context, Constants.UPDATE_TIMES, time);
    }

    /**
     * 获取更新的提醒时间
     *
     * @param context
     * @return
     */
    public static String getUpdateTime(Context context) {
        return getStringKey(context, Constants.UPDATE_TIMES);
    }


    /**
     * 设置更新的提醒次数
     *
     * @param context
     * @param count
     */
    public static void setUpdateCount(Context context, int count) {
        setIntKey(context, Constants.UPDATE_COUNT, count);
    }

    /**
     * 获取更新的提醒次数
     *
     * @param context
     * @return
     */
    public static int getUpdateCount(Context context) {
        return getIntKey(context, Constants.UPDATE_COUNT);
    }

    /**
     * 数据存储
     *
     * @param context
     * @param key
     */
    public static void setStringKey(Context context, String key, String value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }

        SharedPreferencesCompat.EditorCompat.getInstance().apply(sp.edit().putString(key, value));
    }

    public static String getStringKey(Context context, String key) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(key, "");
    }

    public static void setIntKey(Context context, String key, int value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }

        SharedPreferencesCompat.EditorCompat.getInstance().apply(sp.edit().putInt(key, value));
    }

    public static int getIntKey(Context context, String key) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(key, 0);
    }

    /**
     * 参数String类型，格式：yyyy-MM-dd HH:mm:ss
     * 返回String类型，格式：2015-01-01 12:00:00
     */
    @SuppressLint("SimpleDateFormat")
    public static String getNowTime(String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(calendar.getTime());
    }

    /**
     * 无参数
     * 返回String类型，格式：2015-01-01 12:00:00
     */
    public static String getNowTimeDetail() {
        return getNowTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 计算相差的时间差
     *
     * @param starTime
     * @param endTime
     * @return
     */
    public static String getTimeDifferenceHour(String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            String string = Long.toString(diff);

            float parseFloat = Float.parseFloat(string);

            float hour1 = parseFloat / (60 * 60 * 1000);

            timeString = Float.toString(hour1);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;

    }

}
