package com.want.update;

import android.content.Context;

import com.want.core.log.lg;
import com.want.update.enums.CheckType;
import com.want.update.listener.OnUpdateListener;
import com.want.update.model.UpdateInfo;
import com.want.update.utils.VersionUtil;

import static android.content.ContentValues.TAG;


/**
 * <b>Create Date:</b> 16/12/06<br>
 * <b>Author:</b> leon <br>
 * <b>Description:</b>
 * <p>
 * <br>
 */
public class UpdateHelper {

    private Context mContext;
    /**
     * 更新数据json
     **/
    private UpdateInfo mUpdateInfo;

    private CheckType mCheckType;


    private OnUpdateListener mUpdateListener;

    private static long sLastTime;

    private UpdateHelper(Builder builder) {
        this.mContext = builder.context;
        this.mUpdateInfo = builder.updateInfo;
        this.mCheckType = builder.checkType;
    }

    /**
     * 检查app是否有新版本，check之前先Builder所需参数
     */
    public void check() {
        check(null);
    }

    public void check(OnUpdateListener onUpdateListener) {
        if (null != onUpdateListener) this.mUpdateListener = onUpdateListener;
        long now = System.currentTimeMillis();
        if (now - sLastTime < 3000) {
            return;
        }
        sLastTime = now;
        onCheck();
    }

    private void onCheck() {
        if (null != mUpdateInfo) {
            if (compareVersionUpdate(mContext, mUpdateInfo.version)) {
                //update
                if (null != mUpdateListener) mUpdateListener.hasUpdate(true);
                UpdateAgent agent = new UpdateAgent(mContext, mUpdateInfo);
                agent.update();

            } else {
                //no update
                if (null != mUpdateListener) mUpdateListener.hasUpdate(false);
            }
        } else {
            lg.e(TAG, "UpdateInfo json data not null");
        }
    }

    /**
     * 版本号检测更新
     *
     * @param update_version
     * @return true 有更新 false 无更新
     */
    private boolean compareVersionUpdate(Context context, String update_version) {
        String localVersion = VersionUtil.getVersionName(context);
        return VersionUtil.compareVersion(localVersion, update_version);
    }

    public static class Builder {

        private Context context;
        private UpdateInfo updateInfo;
        private CheckType checkType;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setUpdateJson(UpdateInfo updateInfo) {
            this.updateInfo = updateInfo;
            return this;
        }

        public Builder setCheckType(CheckType checkType) {
            this.checkType = checkType;
            return this;
        }

        public UpdateHelper build() {
            return new UpdateHelper(this);
        }
    }
}
