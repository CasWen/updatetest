package com.want.module.update.demo;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.init(getApplicationContext(),
                new FileDownloadHelper.OkHttpClientCustomMaker() { // is not has to provide.
                    @Override
                    public OkHttpClient customMake() {
                        // just for OkHttpClient customize.
                        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        // you can set the connection timeout.
                        builder.connectTimeout(15_000, TimeUnit.MILLISECONDS);
                        // you can set the HTTP proxy.
                        builder.proxy(Proxy.NO_PROXY);
                        // etc.
                        return builder.build();
                    }
                });
    }
}
