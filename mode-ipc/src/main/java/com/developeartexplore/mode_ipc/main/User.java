package com.developeartexplore.mode_ipc.main;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 2018/3/26.
 */

public class User implements Parcelable {

    private int userId;
    private String userName;
    private int isMale;

    public User() {
    }

    protected User(Parcel in) {
        userId = in.readInt();
        userName = in.readString();
        isMale = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeInt(isMale);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getIsMale() {
        return isMale;
    }

    public void setIsMale(int isMale) {
        this.isMale = isMale;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"userId\":")
                .append(userId);
        sb.append(",\"userName\":\"")
                .append(userName).append('\"');
        sb.append(",\"isMale\":")
                .append(isMale);
        sb.append('}');
        return sb.toString();
    }
}
