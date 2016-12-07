package com.want.update.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.want.update.R;
import com.want.update.UpdateAgent;
import com.want.update.model.UpdateInfo;

/**
 * Created by likun on 2016/12/6.
 */

public class UpdateDialog extends AlertDialog {

    private Context mContext;

    private UpdateInfo mUpdateInfo;

    private UpdateAgent mUpdateAgent;

    private boolean isForce;

    public UpdateDialog(@NonNull Context context, UpdateInfo info, UpdateAgent agent, boolean isForce) {
        super(context);
        mContext = context;
        mUpdateInfo = info;
        mUpdateAgent = agent;
        this.isForce = isForce;
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setDialogContent();
    }

    /**
     * 设置对话框内容
     */
    private void setDialogContent() {
        if (!TextUtils.isEmpty(mUpdateInfo.title)) {
            setTitle(mUpdateInfo.title);
        } else {
            setTitle(R.string.update_dialog_default_title);
        }
        DialogInterface.OnClickListener listener = new OnPromptClick(mUpdateAgent, true);

        if (isForce) {
            setMessage("您需要更新应用才能继续使用\n\n" + mUpdateInfo.changes);
            setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
        } else {
            setMessage(mUpdateInfo.changes);
            setButton(DialogInterface.BUTTON_POSITIVE, "立即更新", listener);
            setButton(DialogInterface.BUTTON_NEGATIVE, "以后再说", listener);
        }

    }

    public static class OnPromptClick implements DialogInterface.OnClickListener {
        private final UpdateAgent mAgent;
        private final boolean mIsAutoDismiss;

        public OnPromptClick(UpdateAgent agent, boolean isAutoDismiss) {
            mAgent = agent;
            mIsAutoDismiss = isAutoDismiss;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    mAgent.download();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    // not now
                    break;
            }
            if (mIsAutoDismiss) {
                dialog.dismiss();
            }
        }
    }
}
