package com.developeartexplore.mode_ipc.main.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 2018/3/23.
 */

public class Book implements Parcelable {
    private int bookId;
    private String bookName;


    public Book(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    protected Book(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public int getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(bookId);
        parcel.writeString(bookName);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"bookId\":")
                .append(bookId);
        sb.append(",\"bookName\":\"")
                .append(bookName).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
