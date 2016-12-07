package com.want.update.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.want.core.log.lg;
import com.want.update.R;
import com.want.update.callback.IDownLoadDialogCallBack;
import com.want.update.utils.StorageUtil;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;



/**
 * <b>Create Date:</b> 16/10/12<br>
 * <b>Author:</b> ldc <br>
 * <b>Description:</b>
 * 下载进度对话框
 * <br>
 */
public class DownloadDialog {

    private static String TAG = DownloadDialog.class.getSimpleName();
    private TextView mNegativeText; //左侧按钮
    private TextView mPositiveText; //右侧按钮
    public ProgressBar progressBar;
    public TextView textView;
    private String mDownLoadUrl = "";
    private Context mContext;
    NumberFormat numberFormat;
    private IDownLoadDialogCallBack mDialogCallBack;
    private android.app.AlertDialog ad;

    public DownloadDialog(Context context, String url) {
        this(context, url, null);
    }

    public void setDialogCallBack(IDownLoadDialogCallBack mDialogCallBack) {
        this.mDialogCallBack = mDialogCallBack;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.mDownLoadUrl = downloadUrl;
    }

    public DownloadDialog(final Context context, String url, IDownLoadDialogCallBack dialogCallBack) {
        mContext = context;
        mDownLoadUrl = url;
        lg.v(TAG, "获取的下载地址为：" + mDownLoadUrl);
        mDialogCallBack = dialogCallBack;
        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        if (null == ad) ad = new android.app.AlertDialog.Builder(context).create();
        if (!ad.isShowing()) ad.show();
        ad.setCanceledOnTouchOutside(false);
        Window window = ad.getWindow();
        if (null != window) {
            window.setContentView(R.layout.base_download_dialog);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            mNegativeText = (TextView) window.findViewById(R.id.negativeButton);
            mPositiveText = (TextView) window.findViewById(R.id.positiveButton);
            mNegativeText.setVisibility(View.VISIBLE);
            mPositiveText.setVisibility(View.GONE);
            progressBar = (ProgressBar) window.findViewById(R.id.downloaddialog_progress);
            textView = (TextView) window.findViewById(R.id.downloaddialog_count);
            mNegativeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != ad) ad.dismiss();
                    if (null != mDialogCallBack) {
                        FileDownloader.getImpl().pauseAll();
                        mDialogCallBack.negativeFunction();
                    }
                }
            });
            mPositiveText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDownLoadInit();
                    if (null != ad) ad.dismiss();
                    if (null != mDialogCallBack) {
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
            startDownLoad();
        }

    }

    public void startDownLoad() {
//        String filePath = FileDownloadUtils.getDefaultSaveRootPath();
        lg.v(TAG, "获取下载文件的路径为：" + StorageUtil.getCacheDirectory(mContext));
        FileDownloader.getImpl()
                .create(mDownLoadUrl)
                .setPath(StorageUtil.getCacheDirectory(mContext).getPath(), true)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        lg.v(TAG, "startDownLoad:pending 等待，已经进入下载队列:" + task.getLargeFileTotalBytes());
                        progressBar.setProgress(soFarBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        lg.v(TAG, "startDownLoad:progress 下载进度回调:" + soFarBytes + "," + totalBytes);
                        progressBar.setMax(totalBytes);
                        progressBar.setProgress(soFarBytes);
                        textView.setText(mContext.getString(R.string.progress_content, percentage(soFarBytes, totalBytes), mContext.getString(R.string.percent)));
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        lg.v(TAG, "startDownLoad:completed 下载完成:" + task.getTargetFilePath() + "," + task.getFilename());
                        dismiss();
                        installApp(mContext, new File(task.getTargetFilePath()));
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        lg.e(TAG, "startDownLoad:paused 暂停下载");

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        lg.e(TAG, "startDownLoad:error下载出现错误---> " + task.getId() + "," + task.getTargetFilePath());
                        setDownLoadError();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        lg.e(TAG, "startDownLoad:warn 在下载队列中(正在等待/正在下载)已经存在相同下载连接与相同存储路径的任务");

                    }
                }).start();

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

    public static String accuracy(double num, double total, int scale) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(scale);
        //模式 例如四舍五入
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy_num = num / total * 100;
        return df.format(accuracy_num) + "%";
    }

    public String percentage(int num1, int num2) {
        return numberFormat.format((float) num1 / (float) num2 * 100);
    }


    public static void installApp(Context context, File file) {
        if (file == null) return;
        context.startActivity(getInstallAppIntent(file));
    }

    public static Intent getInstallAppIntent(File file) {
        if (file == null) return null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type;
        type = "application/vnd.android.package-archive";
//        if (Build.VERSION.SDK_INT < 23) {
//
//        } else {
//            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file));
//        }
        intent.setDataAndType(Uri.fromFile(file), type);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

//    /**
//     * 获取全路径中的文件拓展名
//     *
//     * @param file 文件
//     * @return 文件拓展名
//     */
//    public static String getFileExtension(File file) {
//        if (file == null) return null;
//        return getFileExtension(file.getPath());
//    }
//
//    /**
//     * 获取全路径中的文件拓展名
//     *
//     * @param filePath 文件路径
//     * @return 文件拓展名
//     */
//    public static String getFileExtension(String filePath) {
//        int lastPoi = filePath.lastIndexOf('.');
//        int lastSep = filePath.lastIndexOf(File.separator);
//        if (lastPoi == -1 || lastSep >= lastPoi) return "";
//        return filePath.substring(lastPoi + 1);
//    }

    private void setDownLoadError() {
        mNegativeText.setVisibility(View.VISIBLE);
        mPositiveText.setVisibility(View.VISIBLE);
        mNegativeText.setText(R.string.cancel);
        mPositiveText.setText(R.string.update_again_text);
        textView.setText(R.string.update_download_failure_text);
    }

    private void setDownLoadInit() {
        mNegativeText.setVisibility(View.VISIBLE);
        mPositiveText.setVisibility(View.GONE);
        mNegativeText.setText(R.string.cancel);
    }

    /**
     * 获取格式化后的URL
     * 1.判断获取的url是否存在后缀名为.apk
     * 2.不存在使用临时提供的下载地址
     *
     * @param url
     * @return
     */
//    private String getFormatDownLoadUrl(String url) {
//        if (isUrlExtension(url)) {
//            return url;
//        } else {
//            return mContext.getString(R.string.update_download_url);
//        }
//    }




}
