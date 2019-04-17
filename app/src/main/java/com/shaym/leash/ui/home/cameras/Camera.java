package com.shaym.leash.ui.home.cameras;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shaym on 2/17/18.
 */

public class Camera implements Parcelable {
    private String mBeachName;
    private String mLocation;
    private String mUrl;
    private String mStreamKind;
    private String mTag;
    private int picRef;

    protected Camera(Parcel in) {
        mBeachName = in.readString();
        mLocation = in.readString();
        mUrl = in.readString();
        mStreamKind = in.readString();
        mTag = in.readString();
        picRef = in.readInt();
    }

    public static final Creator<Camera> CREATOR = new Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel in) {
            return new Camera(in);
        }

        @Override
        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    int getPicRef() {
        return picRef;
    }

    Camera(String name, String location, String url, String streamKind, int pic_name) {
        this.mBeachName = name;
        this.mLocation = location;
        this.mUrl = url;
        this.mStreamKind = streamKind;
        this.picRef = pic_name;
    }

    String getBeachName() {
        return mBeachName;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getUrl() {
        return mUrl;
    }

    String getStreamKind() {
        return mStreamKind;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String mTag) {
        this.mTag = mTag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBeachName);
        dest.writeString(mLocation);
        dest.writeString(mUrl);
        dest.writeString(mStreamKind);
        dest.writeString(mTag);
        dest.writeInt(picRef);
    }
}
