package com.want.update;


import android.content.Context;

import com.want.update.dialog.UpdateDialog;
import com.want.update.listener.OnPromptListener;
import com.want.update.model.UpdateInfo;

/**
 * <b>Create Date:</b> 16/12/06<br>
 * <b>Author:</b> leon <br>
 * <b>Description:</b>
 * <p>
 * <br>
 */
public class UpdateAgent {

    private Context mContext;
    /**
     * 更新数据json
     **/
    private UpdateInfo mUpdateInfo;

    private OnPromptListener mOnPromptListener;


    UpdateAgent(Context context, UpdateInfo info) {
        mContext = context;
        mUpdateInfo = info;
    }

    /**
     * Normal update
     */
    public void update() {
        mOnPromptListener = new OnPrompt(mContext, false, mUpdateInfo);
        mOnPromptListener.onPrompt(this);
    }

    /**
     * force update
     */
    public void forceUpdate() {
        mOnPromptListener = new OnPrompt(mContext, true, mUpdateInfo);
        mOnPromptListener.onPrompt(this);
    }

    private long timeRange;//时间间隔

    public void download() {
        //防抖动,两次点击间隔小于1000ms都return;
        if (System.currentTimeMillis() - timeRange < 1000) {
            return;
        }
        timeRange = System.currentTimeMillis();


    }

    private static class OnPrompt implements OnPromptListener {

        private Context mContext;
        private boolean isForce;
        private UpdateInfo mUpdateInfo;

        public OnPrompt(Context context, boolean isForce, UpdateInfo info) {
            mContext = context;
            this.isForce = isForce;
            this.mUpdateInfo = info;
        }

        @Override
        public void onPrompt(UpdateAgent agent) {
            UpdateDialog updateDialog = new UpdateDialog(mContext, mUpdateInfo, agent, isForce);
            updateDialog.show();
        }
    }

}
