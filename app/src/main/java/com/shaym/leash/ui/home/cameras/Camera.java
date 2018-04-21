package com.shaym.leash.ui.home.cameras;

/**
 * Created by shaym on 2/17/18.
 */

public class Camera {
    private String mBeachName;
    private String mLocation;
    private String mUrl;
    private String mStreamKind;

    public Camera(String name, String location, String url, String streamKind) {
        this.mBeachName = name;
        this.mLocation = location;
        this.mUrl = url;
        this.mStreamKind = streamKind;
    }

    public String getBeachName() {
        return mBeachName;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getmStreamKind() {
        return mStreamKind;
    }
}
