/*
 * RemoteBookService      2016-05-19
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.farseer.aidl.service;

import com.farseer.aidl.Book;
import com.farseer.aidl.IBookManager;
import com.farseer.aidl.OnBookListChangedListener;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

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

    private RemoteCallbackList<OnBookListChangedListener> changedListenerList = new RemoteCallbackList<>();

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
            notifyChanged(book);
        }

        @Override
        public void registerChangedListener(OnBookListChangedListener listener) throws RemoteException {
            changedListenerList.register(listener);
        }

        @Override
        public void unregisterChangedListener(OnBookListChangedListener listener) throws RemoteException {
            changedListenerList.unregister(listener);
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

    private void notifyChanged(Book book) {
        int count = changedListenerList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            OnBookListChangedListener listener = changedListenerList.getBroadcastItem(i);
            try {
                listener.onBookListChanged(book);
            } catch (RemoteException exception) {
                Log.e(TAG, exception.getMessage());
            }
        }

        Log.i(TAG, "changedListenerList.size = " + count);
        changedListenerList.finishBroadcast();
    }
}
