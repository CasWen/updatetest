package com.want.update.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <b>Create Date:</b> 16/12/06<br>
 * <b>Author:</b> leon <br>
 * <b>Description:</b>
 * update model
 * <br>
 */
public class UpdateInfo implements Parcelable {

    // 800不强制升级 801强制升级
    public int upgrade;

    public String version;

    //提示框标题栏显示的文字
    public String title;

    public String url;

    //显示提示信息次数,不支持小数;如为 0,则没有限制
    public int limitTimes;

    //显示提示信息的时间间隔,以小时为单位,不支持小数
    public int interval;

    //显示服务器端的更新日志
    public String changes;

    public String size;

    public UpdateInfo() {
    }

    public UpdateInfo(int upgrade, String version, String title, String url, int limitTimes, int interval, String changes, String size) {
        this.upgrade = upgrade;
        this.version = version;
        this.title = title;
        this.url = url;
        this.limitTimes = limitTimes;
        this.interval = interval;
        this.changes = changes;
        this.size = size;
    }


    @Override
    public String toString() {
        return "UpdateInfo{" +
                "upgrade=" + upgrade +
                ", version='" + version + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", limitTimes=" + limitTimes +
                ", interval=" + interval +
                ", changes='" + changes + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.upgrade);
        dest.writeString(this.version);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeInt(this.limitTimes);
        dest.writeInt(this.interval);
        dest.writeString(this.changes);
        dest.writeString(this.size);
    }

    private UpdateInfo(Parcel in) {
        this.upgrade = in.readInt();
        this.version = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.limitTimes = in.readInt();
        this.interval = in.readInt();
        this.changes = in.readString();
        this.size = in.readString();
    }

    public static final Creator<UpdateInfo> CREATOR = new Creator<UpdateInfo>() {
        @Override
        public UpdateInfo createFromParcel(Parcel source) {
            return new UpdateInfo(source);
        }

        @Override
        public UpdateInfo[] newArray(int size) {
            return new UpdateInfo[size];
        }
    };
}
