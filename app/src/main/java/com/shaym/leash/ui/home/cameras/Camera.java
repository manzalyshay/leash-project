package com.shaym.leash.ui.home.cameras;

/**
 * Created by shaym on 2/17/18.
 */

public class Camera {
    private String mBeachName;
    private String mLocation;
    private String mUrl;
    private String mStreamKind;
    private String mTag;

    String getPicName() {
        return picName;
    }

    private String picName;


    Camera(String name, String location, String url, String streamKind, String picname) {
        this.mBeachName = name;
        this.mLocation = location;
        this.mUrl = url;
        this.mStreamKind = streamKind;
        this.picName = picname;
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
}
