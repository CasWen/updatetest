package com.want.update;

import android.content.Context;
import android.text.TextUtils;

import com.want.core.log.lg;
import com.want.update.callback.IDialogCallBack;
import com.want.update.dialog.UpdateDialog;
import com.want.update.model.UpdateInfo;
import com.want.update.utils.UpdateUtil;

import java.io.File;

/**
 * <b>Create Date:</b> 16/9/30<br>
 * <b>Author:</b> ldc <br>
 * <b>Description:</b>
 * <br>
 */
public class Update {

    private static String TAG = Update.class.getSimpleName();
    private Context mContext;
    private boolean isForce = false; //是否强制更新
    private boolean isAutoDialog = true; //是否自动弹出框
    private boolean isBoot = false; //是否引导
    private UpdateInfo mCodeInfo;
    private UpdateDialog mUpdateDialog;

    public interface OnUpdateListener {
        /**
         * 是否更新
         *
         * @param isUpdate
         */
        void isUpdate(boolean isUpdate);
    }

    //TODO Leon 060928增加请求更新接口监听
    private OnUpdateListener onUpdateListener;


    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public boolean isForce() {
        return isForce;
    }

    public UpdateDialog getUpdateDialog() {
        return mUpdateDialog;
    }

    /**
     * 设置是否第一次启动
     *
     * @param boot
     */
    public void setBoot(boolean boot) {
        isBoot = boot;
    }

    /**
     * 设置是否强制更新
     *
     * @param force
     */
    public void setForce(boolean force) {
        isForce = force;
    }

    public boolean isAutoDialog() {
        return isAutoDialog;
    }

    /**
     * 设置是否自动弹出框
     *
     * @param autoDialog
     */
    public void setAutoDialog(boolean autoDialog) {
        isAutoDialog = autoDialog;
    }

    public Update(Context context) {
        this.mContext = context;
    }


    /**
     * 设置对话框按钮监听
     *
     * @param dialogCallBack
     */
    public void setDialogListener(IDialogCallBack dialogCallBack) {
        if (null != mUpdateDialog) mUpdateDialog.setDialogCallBack(dialogCallBack);
    }


    /**
     * 检测更新
     *
     * @param codeInfo
     */
    public void checkUpdate(UpdateInfo codeInfo) {
        mCodeInfo = codeInfo;
        if (null != codeInfo) {
            lg.v(TAG, "checkUpdate:获取更新信息" + mCodeInfo.toString());
            if (TextUtils.isEmpty(mCodeInfo.url) && isUrlExtension(mCodeInfo.url)) {
                lg.v(TAG, "checkUpdate:已是更新、url后缀名不为.apk");
                if (null != onUpdateListener) onUpdateListener.isUpdate(false);
                if (!isBoot) {
                    if (isAutoDialog()) showDefaultDialog(mCodeInfo);
                }
            } else {
                String localVersion = UpdateUtil.getVersionName(mContext);
                boolean isUpdate = UpdateUtil.compareVersion(localVersion, codeInfo.version);
                // 更新监听回调
                //优先级版本号
                if (isUpdate) {
                    if (null != onUpdateListener) onUpdateListener.isUpdate(true);
                    switch (mCodeInfo.upgrade) {
                        case 801:
                            //强制更新
                            lg.v(TAG, "checkUpdate:开启强制更新");
                            if (isAutoDialog()) showForceDialog(mCodeInfo);
                            break;
                        case 800:
                            //非强制更新
                            lg.v(TAG, "checkUpdate:开启非强制更新");
                            if (isAutoDialog()) showAutoDialog(mCodeInfo);
                            break;
                        default:
                            lg.v(TAG, "checkUpdate:已是更新");
                            if (!isBoot) {
                                if (isAutoDialog()) showDefaultDialog(mCodeInfo);
                            }
                            break;
                    }
                } else {
                    if (null != onUpdateListener) onUpdateListener.isUpdate(false);
                    if (!isBoot) {
                        if (isAutoDialog())
                            showDefaultDialog(mCodeInfo);
                    }
                }

            }
        }
    }


    /**
     * 强制更新对话框
     *
     * @param mCodeInfo
     */
    private void showForceDialog(UpdateInfo mCodeInfo) {
        if (null == mUpdateDialog) mUpdateDialog = new UpdateDialog(mContext);
        mUpdateDialog.show();
        if (isBoot) {
            //计数
            setCacheRemindCount();
            setCacheRemindTime();
        }
        mUpdateDialog.setTitle(mCodeInfo.title);
        mUpdateDialog.setMessage(mCodeInfo.changes);
        mUpdateDialog.setPositive(R.string.setting_update_once_text);
    }

    /**
     * 自动更新对话框
     *
     * @param mCodeInfo
     */
    private void showAutoDialog(UpdateInfo mCodeInfo) {
        if (null == mUpdateDialog) mUpdateDialog = new UpdateDialog(mContext);
        mUpdateDialog.show();
        if (isBoot) {
            //计数
            setCacheRemindCount();
            setCacheRemindTime();
        }
        mUpdateDialog.setTitle(mCodeInfo.title);
        mUpdateDialog.setMessage(mCodeInfo.changes);
        mUpdateDialog.setNegative(R.string.setting_update_wait_text);
        mUpdateDialog.setPositive(R.string.setting_update_once_text);
    }

    /**
     * 缺省最新对话框
     *
     * @param mCodeInfo
     */
    public void showDefaultDialog(UpdateInfo mCodeInfo) {
        if (null == mUpdateDialog) mUpdateDialog = new UpdateDialog(mContext);
        mUpdateDialog.show();
        mUpdateDialog.setMessage(R.string.setting_update_msg);
        mUpdateDialog.setNegative(R.string.setting_close_text);
    }


    /**
     * 初始化提醒次数与间隔时间
     * 规则:每次更新请求获取数据
     * 1.判断缓存提醒次数与获取提醒次数差异性 ,不一致更新
     * 2.
     * #提醒次数
     *
     * @param mCodeInfo
     */
    public void initCacheUpdateInfo(UpdateInfo mCodeInfo) {
        if (null != mCodeInfo) {
            String cacheCount = UpdateUtil.getUpdateRequestLimitTimes(mContext);
            if (!String.valueOf(mCodeInfo.limitTimes).equals(cacheCount)) {
                lg.v(TAG, "update初始化_initCacheUpdateInfo: 缓存提醒次数与获取提醒次数不一致 ,缓存次数:" + cacheCount + ",获取提醒次数:" + mCodeInfo.limitTimes);
                //更新提醒次数
                UpdateUtil.setUpdateRequestLimitTimes(mContext, String.valueOf(mCodeInfo.limitTimes));
                UpdateUtil.setUpdateCount(mContext, mCodeInfo.limitTimes);
                //更新时间
                UpdateUtil.setUpdateTime(mContext, "");
            }
            lg.v(TAG, "update初始化_initCacheUpdateInfo: 缓存提醒次数:" + UpdateUtil.getUpdateCount(mContext));
            lg.v(TAG, "update初始化_initCacheUpdateInfo: 缓存时间间隔:" + mCodeInfo.interval);
            lg.v(TAG, "update初始化_initCacheUpdateInfo: 缓存时间:" + UpdateUtil.getUpdateTime(mContext));

        }

    }


    /**
     * 是否符合更新机制
     * 规则:
     * 1.时间间隔是否符合规格
     * 2.提醒次数是否符合达到获取次数
     * false 超过获取提醒次数 不更新
     * true  超过获取提醒次数 更新
     *
     * @param codeInfo
     * @return true false
     */
    public boolean isAccordUpdate(UpdateInfo codeInfo) {
        if (isRemindTime(codeInfo)) {
            lg.v(TAG, "update判断更新_isAccordUpdate:缓存的时间和当前的时间差,超过");
            if (isRemindCount(codeInfo)) {
                lg.v(TAG, "update判断更新_isUpdateRemind:缓存获取提醒次数大于0");
                return true;
            } else {
                lg.v(TAG, "update判断更新_isUpdateRemind:缓存获取提醒次数小于0");
                return false;
            }
        } else {
            lg.v(TAG, "update判断更新_isAccordUpdate:缓存的时间和当前的时间差,未超过");
            return false;

        }

    }

    /**
     * 是否超过时间间隔
     *
     * @param codeInfo
     * @return true 缓存的时间和当前的时间差超过获取的时间间隔(小时为单位)
     */
    private boolean isRemindTime(UpdateInfo codeInfo) {
        try {
            int getTimeCount = codeInfo.interval;//
            lg.v(TAG, "update时间间隔_isUpdateRemind:获取返回的缓存的时间间隔:" + getTimeCount + "小时");
            if (getTimeCount == 0) {
                lg.v(TAG, "update时间间隔_isUpdateRemind:获取返回的缓存的时间间隔为0，不限制更新 ");
                return true;
            }

            if (getTimeCount < 0) return false;
            String time = UpdateUtil.getUpdateTime(mContext);
            if (TextUtils.isEmpty(time)) {
                lg.v(TAG, "update时间间隔_isUpdateRemind:获取缓存最后的提醒时间为空!");
                return true;
            }
            String currentDate = UpdateUtil.getNowTimeDetail();
            String compareValue = UpdateUtil.getTimeDifferenceHour(time, currentDate);
            lg.v(TAG, "update时间间隔_isRemindTime:比较时间间隔,当前的时间:" + currentDate + ",比较的时间差(小时):" + compareValue);
            if (TextUtils.isEmpty(compareValue)) return false;
            return Float.valueOf(compareValue) > getTimeCount;
        } catch (Exception e) {
            e.printStackTrace();
            lg.e(TAG, "update时间间隔_isRemindTime:是否超过时间间隔异常");
            return false;
        }
    }

    /**
     * 缓存的次数是否大于0
     *
     * @param codeInfo
     * @return true 大于0 false
     */
    private boolean isRemindCount(UpdateInfo codeInfo) {
        try {
            if (UpdateUtil.getUpdateRequestLimitTimes(mContext).equals("0")) {
                lg.e(TAG, "isRemindCount:获取缓存的次数为0 不限制更新");
                return true;
            } else {
                return UpdateUtil.getUpdateCount(mContext) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            lg.e(TAG, "isRemindCount:是否超过提醒次数异常");
            return false;
        }

    }


    /**
     * 设置缓存累计提醒次数
     */
    private void setCacheRemindCount() {
        try {
            int cacheCount = UpdateUtil.getUpdateCount(mContext);
            if (cacheCount > 0) {
                cacheCount--;
                UpdateUtil.setUpdateCount(mContext, cacheCount);
            }
            lg.v(TAG, "设置缓存累计提醒次数:" + UpdateUtil.getUpdateCount(mContext));
        } catch (Exception e) {
            e.printStackTrace();
            lg.e(TAG, "setUpdateRemind:设置缓存累计提醒次数异常");
        }
    }


    /**
     * 设置缓存累计提醒时间
     */
    private void setCacheRemindTime() {
        try {
            UpdateUtil.setUpdateTime(mContext, UpdateUtil.getNowTimeDetail());
            lg.v(TAG, "update设置缓存提醒时间_setCacheRemindTime:设置缓存累计提醒时间," + UpdateUtil.getUpdateTime(mContext));
        } catch (Exception e) {
            e.printStackTrace();
            lg.e(TAG, "update设置缓存提醒时间_setCacheRemindTime:设置缓存累计提醒时间异常");
        }
    }


    /**
     * 判断URL是否存在后缀名.APK
     *
     * @param url
     * @return
     */
    private boolean isUrlExtension(String url) {
        String extension = getUrlExtension(url);
        lg.v(TAG, "获取当前URL的后缀名为：" + extension);
        if (TextUtils.isEmpty(extension)) {
            return false;
        } else {
            if (extension.equals(".apk")) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param url 网址
     * @return 文件拓展名
     */
    public static String getUrlExtension(String url) {
        if (TextUtils.isEmpty(url)) return url;
        int lastPoi = url.lastIndexOf('.');
        int lastSep = url.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return url.substring(lastPoi);
    }

    public void cancelDialog() {
        if (null != mUpdateDialog) {
            mUpdateDialog.dismiss();
        }
    }


}
