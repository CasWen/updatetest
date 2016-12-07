package com.want.update.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.want.update.R;
import com.want.update.callback.IDialogCallBack;


/**
 * <b>Create Date:</b> 16/9/30<br>
 * <b>Author:</b> leon <br>
 * <b>Description:</b>
 * <br>
 */
public class UpdateDialog {

    private TextView mTitleText; //标题
    private TextView mMsgText; // 更新内容
    private TextView mNegativeText; //左侧按钮
    private TextView mPositiveText; //右侧按钮
    private IDialogCallBack mDialogCallBack;
    private android.app.AlertDialog ad;

    private long timeRange;//时间间隔


    public UpdateDialog(Context context) {
        this(context, "");
    }

    public UpdateDialog(Context context, String title) {
        this(context, title, "");
    }

    public UpdateDialog(final Context context, String title, String message) {
        this(context, title, message, "");
    }

    public UpdateDialog(final Context context, String title, String message, String negative) {
        this(context, title, message, negative, "");
    }

    public UpdateDialog(final Context context, String title, String message, String negative, String positive) {
        this(context, title, message, negative, positive, null);
    }

    public UpdateDialog(final Context context, String title, String message, String negative, String positive, IDialogCallBack dialogCallBack) {
        mDialogCallBack = dialogCallBack;
        if (null == ad) ad = new android.app.AlertDialog.Builder(context).create();
        if (!ad.isShowing()) ad.show();
        ad.setCanceledOnTouchOutside(false);
        Window window = ad.getWindow();
        if (null != window) {
            window.setContentView(R.layout.update_dialog);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            mTitleText = (TextView) window.findViewById(R.id.title);
            mMsgText = (TextView) window.findViewById(R.id.message);
            mNegativeText = (TextView) window.findViewById(R.id.negativeButton);
            mPositiveText = (TextView) window.findViewById(R.id.positiveButton);
            setTitle(title);
            setMessage(message);
            setNegative(negative);
            setPositive(positive);
            mNegativeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != ad) ad.dismiss();
                    if (null != mDialogCallBack) {
                        mDialogCallBack.negativeFunction();
                    }
                }
            });
            mPositiveText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != ad) ad.dismiss();
                    if (null != mDialogCallBack) {
                        download();
                        mDialogCallBack.positiveFunction();
                    }

                }
            });

            ad.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    //lg.v("RwAlertDialog", "RwAlertDialog");
                    if (null != mDialogCallBack) mDialogCallBack.dismissFunction();

                }
            });
        }

    }



    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleText.setVisibility(View.VISIBLE);
            mTitleText.setText(title);
        } else {
            mTitleText.setVisibility(View.GONE);
        }
    }

    public void setTitle(int title) {
        if (title > 0) {
            mTitleText.setVisibility(View.VISIBLE);
            mTitleText.setText(title);
        } else {
            mTitleText.setVisibility(View.GONE);
        }
    }

    public void setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            mMsgText.setVisibility(View.VISIBLE);
            mMsgText.setText(message);
        } else {
            mMsgText.setVisibility(View.GONE);
        }
    }

    public void setMessage(int message) {
        if (message > 0) {
            mMsgText.setVisibility(View.VISIBLE);
            mMsgText.setText(message);
        } else {
            mMsgText.setVisibility(View.GONE);
        }
    }

    public void setNegative(String negative) {
        if (!TextUtils.isEmpty(negative)) {
            mNegativeText.setVisibility(View.VISIBLE);
            mNegativeText.setText(negative);
        } else {
            mNegativeText.setVisibility(View.GONE);
        }
    }

    public void setNegative(int negative) {
        if (negative > 0) {
            mNegativeText.setVisibility(View.VISIBLE);
            mNegativeText.setText(negative);
        } else {
            mNegativeText.setVisibility(View.GONE);
        }
    }

    public void setPositive(String positive) {

        if (!TextUtils.isEmpty(positive)) {
            mPositiveText.setVisibility(View.VISIBLE);
            mPositiveText.setText(positive);
        } else {
            mPositiveText.setVisibility(View.GONE);
        }
    }

    public void setPositive(int positive) {
        if (positive > 0) {
            mPositiveText.setVisibility(View.VISIBLE);
            mPositiveText.setText(positive);
        } else {
            mPositiveText.setVisibility(View.GONE);
        }
    }

    public void setDialogCallBack(IDialogCallBack dialogCallBack) {
        this.mDialogCallBack = dialogCallBack;
    }

    /**
     * 关闭弹出框
     */
    public void dismiss() {
        if (null != ad) {
            if (ad.isShowing()) ad.dismiss();
        }
    }

    public void show() {
        if (null != ad) {
            if (!ad.isShowing()) ad.show();
        }
    }

    public boolean isShow() {
        return ad.isShowing();
    }


    private void download() {
        //防抖动,两次点击间隔小于500ms都return;
        if (System.currentTimeMillis() - timeRange < 500) {
            return;
        }
        timeRange = System.currentTimeMillis();
    }

}
