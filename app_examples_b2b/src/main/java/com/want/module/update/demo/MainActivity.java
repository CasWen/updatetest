package com.want.module.update.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.want.core.log.lg;
import com.want.update.Update;
import com.want.update.callback.IDialogCallBack;
import com.want.update.callback.IDownLoadDialogCallBack;
import com.want.update.dialog.DownloadDialog;
import com.want.update.model.UpdateInfo;
import com.want.update.utils.UpdateUtil;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    Update mUpdate;
    private UpdateInfo mUpdateInfo;
    private boolean isClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUpdateInfo = getData();
        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting();
            }
        });

        findViewById(R.id.text2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home();
            }
        });
    }


    private void setting() {
        //setting update
        mUpdate = new Update(this);
        mUpdate.setBoot(false);
        mUpdate.setAutoDialog(true);
        mUpdate.checkUpdate(mUpdateInfo);
        mUpdate.setOnUpdateListener(new Update.OnUpdateListener() {
            @Override
            public void isUpdate(boolean isUpdate) {
                if (isUpdate) {
                    lg.d(TAG, "发现新版本");
                } else {
                    lg.d(TAG, "无需更新");
                }
            }
        });
        mUpdate.setDialogListener(new IDialogCallBack() {
            @Override
            public void negativeFunction() {
                //
            }

            @Override
            public void positiveFunction() {
                download();
            }

            @Override
            public void dismissFunction() {

            }
        });
    }


    private void home() {
        mUpdate.setBoot(true);
        mUpdate.setAutoDialog(true);
        //初始化更新缓存信息
        mUpdate.initCacheUpdateInfo(mUpdateInfo);
        if (mUpdate.isAccordUpdate(mUpdateInfo)) {
            lg.v(TAG, "requestUpdate:符合更新条件");
            mUpdate.checkUpdate(mUpdateInfo);
            mUpdate.setDialogListener(new IDialogCallBack() {
                @Override
                public void negativeFunction() {
                    isClick = true;
                    launch();
                }

                @Override
                public void positiveFunction() {
                    isClick = true;
                    download();
                }

                @Override
                public void dismissFunction() {
                    if (!isClick) {
                        if (null != mUpdateInfo) {
                            String localVersion = UpdateUtil.getVersionName(MainActivity.this);
                            boolean isUpdate = UpdateUtil.compareVersion(localVersion, mUpdateInfo.version);
                            if (isUpdate) {
                                switch (mUpdateInfo.upgrade) {
                                    case 801:
                                        finish();
                                        break;
                                    default:
                                        launch();
                                        break;
                                }
                            } else {
                                launch();
                            }
                        }
                    }
                }
            });
        } else {
            lg.v(TAG, "requestUpdate:不符合更新条件");
            launch();
        }
    }

    private void launch() {
        finish();
    }


    private void download() {
        final DownloadDialog mDownLoadDialog = new DownloadDialog(this, mUpdateInfo.url);
        mDownLoadDialog.setDialogCallBack(new IDownLoadDialogCallBack() {

            @Override
            public void negativeFunction() {
                mDownLoadDialog.dismiss();
            }

            @Override
            public void positiveFunction() {
                mDownLoadDialog.startDownLoad();
                mDownLoadDialog.show();
            }

            @Override
            public void dismissFunction() {

            }
        });
        mDownLoadDialog.show();
    }

    private UpdateInfo getData() {
        UpdateInfo info = new UpdateInfo();
        info.upgrade = 800;
        info.version = "0.6.3";
        info.title = "update";
        info.url = "http://www.hollywant.com/download/hollywant_0.4.3.apk";
        info.limitTimes = 0;
        info.interval = 0;
        info.changes = "更新内容为";
        info.size = "30M";
        return info;
    }

}
