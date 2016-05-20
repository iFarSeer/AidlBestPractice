/*
 *    Copyright 2016 ifarseer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.farseer.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * DevBook
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 2016-05-19
 */
public class DevBook implements Parcelable {

    private int bookId;
    private String bookName;

    /**
     * Book的构造方法.
     *
     * @param bookId   bookId
     * @param bookName bookName
     */
    public DevBook(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    protected DevBook(Parcel in) {
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

    public static final Creator<DevBook> CREATOR = new Creator<DevBook>() {
        @Override
        public DevBook createFromParcel(Parcel in) {
            return new DevBook(in);
        }

        @Override
        public DevBook[] newArray(int size) {
            return new DevBook[size];
        }
    };

    @Override
    public String toString() {
        return "DevBook{"
                + "bookId="
                + bookId
                + ", bookName='"
                + bookName
                + '\''
                + "}";
    }


    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
