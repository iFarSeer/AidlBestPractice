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

package com.farseer.aidl.service;

import com.farseer.aidl.*;

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

    private CopyOnWriteArrayList<DevBook> bookList = new CopyOnWriteArrayList<>();

    private RemoteCallbackList<OnBookListChangedListener> changedListenerList = new RemoteCallbackList<>();

    private Binder binder = new IBookManager.Stub() {

        @Override
        public List<DevBook> getBookList() throws RemoteException {
            Log.i(TAG, "getBookList success");
            return bookList;
        }

        @Override
        public void addBook(DevBook book) throws RemoteException {
            Log.i(TAG, "addBook book = " + book.toString());
            bookList.add(book);
            notifyChanged(book);
        }

        @Override
        public void getBook(int bookId, OnRequestBookCallback callback) throws RemoteException {

            if (bookList.size() == 0) {
                callback.onFailed(ResultCode.GET_BOOK_EMPTY_LIST);
                return;
            }

            for (DevBook book : bookList) {
                if (book.getBookId() == bookId) {
                    callback.onSuccess(book);
                    return;
                }
            }

            callback.onFailed(ResultCode.GET_BOOK_NONE);
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
        bookList.add(new DevBook(1001, "解忧杂货店"));
        bookList.add(new DevBook(1002, "呼兰河传"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void notifyChanged(DevBook book) {
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
