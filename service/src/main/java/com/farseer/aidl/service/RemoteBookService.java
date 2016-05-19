/*
 * RemoteBookService      2016-05-19
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.farseer.aidl.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.farseer.aidl.Book;
import com.farseer.aidl.IBookManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 2016-05-19
 */
public class RemoteBookService extends Service {

    private static final String TAG = RemoteBookService.class.getSimpleName();

    private CopyOnWriteArrayList<Book> bookList = new CopyOnWriteArrayList<>();


    private Binder binder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.i(TAG, "getBookList success");
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.i(TAG, "addBook book = " + book.toString());
            bookList.add(book);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        bookList.add(new Book(1001, "解忧杂货店"));
        bookList.add(new Book(1002, "呼兰河传"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
