// IBookManager.aidl
package com.farseer.aidl;

import com.farseer.aidl.DevBook;
import com.farseer.aidl.OnRequestBookCallback;
import com.farseer.aidl.OnBookListChangedListener;
// Declare any non-default types here with import statements

interface IBookManager {

    List<DevBook> getBookList();

    void addBook(in DevBook book);

    void getBook(int bookId, OnRequestBookCallback callback);

    void registerChangedListener(OnBookListChangedListener listener);

    void unregisterChangedListener(OnBookListChangedListener listener);
}
