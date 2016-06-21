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

import android.text.TextUtils;
import com.farseer.aidl.*;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import com.farseer.aidl.service.tool.LogTool;

import java.util.ArrayList;
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
        public int setup(String appId, String secret, int version) throws RemoteException {
            //ifarseer TODO 此处采取本地简单的验证,生产环境可以采取服务端验证
            int response = check(appId, secret, version);
            //ifarseer TODO 当ResultCode.RESPONSE_RESULT_OK == response 时,可以异步处理些本地的初始化工作
            return response;
        }

        @Override
        public void searchBook(String filter, OnSearchBookCallback callback) throws RemoteException {
            List<DevBook> result = new ArrayList<>();

            if (TextUtils.isEmpty(filter)) {
                result.addAll(bookList);
                callback.onSuccess(result);
                return;
            }

            for (DevBook book : bookList) {
                if (!TextUtils.isEmpty(book.getBookName()) && book.getBookName().contains(filter)) {
                    result.add(book);
                }
            }

            if (result.size() != 0) {
                callback.onSuccess(result);
            } else {
                callback.onFailed(ResultCode.SEARCH_BOOK_FAILED);
            }
        }

        @Override
        public List<DevBook> getBookList() throws RemoteException {
            LogTool.debug(TAG, "getBookList success");
            return bookList;
        }

        @Override
        public void addBook(DevBook book) throws RemoteException {
            LogTool.debug(TAG, "addBook book = " + book.toString());
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

        LogTool.debug(TAG, "changedListenerList.size = " + count);
        changedListenerList.finishBroadcast();
    }

    private int check(String appId, String secret, int version) {
        if (TextUtils.isEmpty(appId)) {
            return ResultCode.RESPONSE_RESULT_INVALID_APPID;
        }
        if (TextUtils.isEmpty(secret)) {
            return ResultCode.RESPONSE_RESULT_INVALID_SECRET;
        }
        if (version < 1) {
            return ResultCode.RESPONSE_RESULT_NOT_SUPPORT_VERSION;
        }
        return ResultCode.RESPONSE_RESULT_OK;
    }
}
