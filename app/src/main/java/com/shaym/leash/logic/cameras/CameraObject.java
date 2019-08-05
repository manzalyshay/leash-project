package com.shaym.leash.logic.cameras;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Created by shaym on 2/17/18.
 */

@Entity (tableName = "Cameras")
public class CameraObject implements Parcelable {
    public String mLocation;
    public String mCity;
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull  public String mID;
    public String mUrl;
    public String mSponsor;
    public String mStreamKind;
    public String mPicRef;
    public String mSponsorPicRef;


    public CameraObject(@NonNull String mID, String mCity, String mLocation,String mUrl, String mStreamKind, String mSponsor, String mPicRef, String mSponsorPicRef) {
        this.mLocation = mLocation;
        this.mCity = mCity;
        this.mID = mID;
        this.mUrl = mUrl;
        this.mStreamKind = mStreamKind;
        this.mPicRef = mPicRef;
        this.mSponsor = mSponsor;
        this.mSponsorPicRef = mSponsorPicRef;
    }


    public String getSponsorPicRef() {
        return mSponsorPicRef;
    }

    @Ignore
    protected CameraObject(Parcel in) {
        mID = Objects.requireNonNull(in.readString());
        mLocation = in.readString();
        mCity = in.readString();
        mUrl = in.readString();
        mStreamKind = in.readString();
        mPicRef = in.readString();
        mSponsorPicRef = in.readString();
        mSponsor = in.readString();
    }

    public static final Creator<CameraObject> CREATOR = new Creator<CameraObject>() {
        @Override
        public CameraObject createFromParcel(Parcel in) {
            return new CameraObject(in);
        }

        @Override
        public CameraObject[] newArray(int size) {
            return new CameraObject[size];
        }
    };



    public String getCity() {
        return mCity;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getStreamKind() {
        return mStreamKind;
    }


    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getSponsor() {
        return mSponsor;
    }

    public String getID() {
        return mID;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLocation);
        dest.writeString(mCity);
        dest.writeString(mUrl);
        dest.writeString(mStreamKind);
        dest.writeString(mPicRef);
        dest.writeString(mID);
        dest.writeString(mSponsor);
        dest.writeString(mSponsorPicRef);

    }
}
