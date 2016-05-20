package com.farseer.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Book
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 2016-05-19
 */
public class Book implements Parcelable {

    private int bookId;
    private String bookName;

    /**
     * Book的构造方法.
     *
     * @param bookId   bookId
     * @param bookName bookName
     */
    public Book(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    protected Book(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookName);
    }

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public String toString() {
        return "Book{"
                + "bookId="
                + bookId
                + ", bookName='"
                + bookName
                + '\''
                + "}";
    }
}
