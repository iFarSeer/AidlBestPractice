// IBookManager.aidl
package com.farseer.aidl;

import com.farseer.aidl.DevBook;
import com.farseer.aidl.OnRequestBookCallback;
import com.farseer.aidl.OnSearchBookCallback;
import com.farseer.aidl.OnBookListChangedListener;
import com.farseer.aidl.OnBuyBookCallback;
import com.farseer.aidl.OnSetupCallback;
// Declare any non-default types here with import statements

interface IBookManager {

    List<DevBook> getBookList();

    void setup(String appId, String secret, int version, OnSetupCallback callback);

    void buyBook(int bookId, OnBuyBookCallback callback);

    void addBook(in DevBook book);

    void getBook(int bookId, OnRequestBookCallback callback);

    void searchBook(String filter, OnSearchBookCallback callback);

    void registerChangedListener(OnBookListChangedListener listener);

    void unregisterChangedListener(OnBookListChangedListener listener);
}
