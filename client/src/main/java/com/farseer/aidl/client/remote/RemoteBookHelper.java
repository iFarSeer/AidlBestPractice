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

package com.farseer.aidl.client.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.farseer.aidl.*;
import com.farseer.aidl.client.BuildConfig;
import com.farseer.aidl.client.tool.LogTool;

import java.util.List;

/**
 * class description
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 16/6/20
 */
public class RemoteBookHelper {

    private static final String BOOK_MANAGER_ACTION = "com.farseer.aidl.service.RemoteBookService";
    private static final String BOOK_MANAGER_PACKAGE = "com.farseer.aidl.service";

    private Context context;
    private ServiceConnection serviceConnection;
    private IBookManager bookManager;

    private boolean isSetupDone = false;
    private boolean isDisposed = false;

    private OnBookListChangedListener bookListChangedListener = new OnBookListChangedListener.Stub() {

        @Override
        public void onBookListChanged(DevBook book) throws RemoteException {
            if (changeListener != null) {
                changeListener.onChanged(book);
            }
            LogTool.debug("onBookListChanged:book = " + book.toString());
        }
    };

    private OnDataChangeListener changeListener = null;

    public RemoteBookHelper(Context context) {
        this.context = context;
    }

    public interface OnSetupFinishedListener {
        void onSetupFinished(RemoteResult result);
    }

    public interface OnSearchBookListener {
        void onSuccess(int response, List<DevBook> bookList);
    }

    public interface OnRequestBookListener {
        void onSuccess(int response, DevBook book);
    }

    public interface OnDataChangeListener {
        void onChanged(DevBook devBook);
    }

    public void setup(final OnSetupFinishedListener setupFinishedListener) {
        checkNotDisposed();
        if (isSetupDone) {
            LogTool.debug("RemoteBookHelper is already set up.");
            return;
        }

        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogTool.error("onServiceDisconnected");
                bookManager = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (isDisposed) return;
                LogTool.debug("onServiceConnected");
                bookManager = IBookManager.Stub.asInterface(service);
                linkToDeath(service);
                try {
                    LogTool.debug("setup for IBookManager");

                    // setup for IBookManager
                    int response = bookManager.setup(BuildConfig.APPLICATION_ID, RemoteConstant.SECRET, AidlConstant.VERSION);
                    if (response != ResultCode.RESPONSE_RESULT_OK) {
                        if (setupFinishedListener != null) {
                            setupFinishedListener.onSetupFinished(getSetupResult(response));
                        }
                        return;
                    }
                    isSetupDone = true;
                } catch (RemoteException re) {
                    if (setupFinishedListener != null) {
                        setupFinishedListener.onSetupFinished(new RemoteResult(RemoteConstant.ERROR_SETUP_EXCEPTION, "RemoteException while setting up IBookManager."));
                    }
                    re.printStackTrace();
                    return;
                }

                if (setupFinishedListener != null) {
                    setupFinishedListener.onSetupFinished(new RemoteResult(RemoteConstant.ERROR_SETUP_SUCCESS, "IBookManager setup successful."));
                }
            }
        };

        bindService(setupFinishedListener);
    }

    public void dispose() {
        LogTool.debug("Disposing.");
        isSetupDone = false;
        if (serviceConnection != null) {
            LogTool.debug("Unbinding from service.");
            if (context != null) context.unbindService(serviceConnection);
        }
        isDisposed = true;
        context = null;
        serviceConnection = null;
        bookManager = null;
    }

    private boolean isReady() {
        try {
            checkNotDisposed();
            checkSetupDone("isReady");
        } catch (IllegalStateException e) {
            LogTool.error(e.getMessage());
            return false;
        }
        return true;
    }

    public void addBook(DevBook book) {
        if (!isReady()) {
            return;
        }
        try {
            if (bookManager != null) {
                bookManager.addBook(book);
            }
        } catch (RemoteException exception) {
            LogTool.error(exception.getMessage());
            LogTool.error("addBook failed");
        }
    }

    public void getBook(int bookId, final OnRequestBookListener requestBookListener) {
        if (!isReady()) {
            requestBookListener.onSuccess(ResultCode.RESPONSE_RESULT_CLIENT_NOT_READY, null);
            return;
        }
        try {
            if (bookManager != null) {
                OnRequestBookCallback callback = new OnRequestBookCallback.Stub() {
                    @Override
                    public void onSuccess(DevBook book) throws RemoteException {
                        LogTool.debug("找书成功:book = " + book.toString());
                        if (requestBookListener != null) {
                            requestBookListener.onSuccess(ResultCode.GET_BOOK_SUCCESS, book);
                        }
                    }

                    @Override
                    public void onFailed(int code) throws RemoteException {
                        LogTool.debug("找书失败: code = " + code);
                        if (requestBookListener != null) {
                            requestBookListener.onSuccess(code, null);
                        }
                    }
                };
                bookManager.getBook(bookId, callback);
            }
        } catch (RemoteException exception) {
            LogTool.debug(exception.getMessage());
            LogTool.debug("getBook failed");
        }
    }

    public void searchBook(String filter, final OnSearchBookListener searchBookListener) {
        if (!isReady()) {
            searchBookListener.onSuccess(ResultCode.RESPONSE_RESULT_CLIENT_NOT_READY, null);
            return;
        }

        try {
            checkNotDisposed();
            checkSetupDone("searchBook");
        } catch (IllegalStateException e) {
            LogTool.error(e.getMessage());
            searchBookListener.onSuccess(ResultCode.RESPONSE_RESULT_CLIENT_NOT_READY, null);
            return;
        }

        try {
            if (bookManager != null) {
                bookManager.searchBook(filter, new OnSearchBookCallback.Stub() {
                    @Override
                    public void onSuccess(List<DevBook> bookList) throws RemoteException {
                        if (searchBookListener != null) {
                            searchBookListener.onSuccess(ResultCode.SEARCH_BOOK_SUCCESS, bookList);
                        }
                    }

                    @Override
                    public void onFailed(int code) throws RemoteException {
                        if (searchBookListener != null) {
                            searchBookListener.onSuccess(code, null);
                        }
                    }
                });
            }
        } catch (RemoteException exception) {
            LogTool.error(exception.getMessage());
            LogTool.error("searchBook failed");
        }
    }

    public void registerDataChangeListener(OnDataChangeListener changeListener) {
        registerChangedListener();
        this.changeListener = changeListener;
    }

    public void unregisterDataChangeListener() {
        changeListener = null;
        unregisterChangedListener();
    }

    //为Binder设置死亡代理,当Binder死亡,DeathRecipient收到通知,客户端重连服务
    private void linkToDeath(IBinder binder) {
        try {
            binder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    bindService(null);
                }
            }, 0);
        } catch (RemoteException exception) {
            LogTool.error(exception.getMessage());
        }
    }

    //绑定服务
    private void bindService(OnSetupFinishedListener setupFinishedListener) {
        Intent intent = new Intent(BOOK_MANAGER_ACTION);
//        intent.setPackage(BOOK_MANAGER_PACKAGE);
        Intent explicitIntent = ServiceIntentConvertor.convertImplicitExplicitIntent(context, intent);
        if (explicitIntent != null) {
            //绑定服务
            context.bindService(explicitIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            // 未查找到对应的服务
            if (setupFinishedListener != null) {
                setupFinishedListener.onSetupFinished(new RemoteResult(RemoteConstant.ERROR_SETUP_UNAVAILABLE, "IBookManager is unavailable on device."));
            }
        }
    }

    //注册变化监听
    private void registerChangedListener() {
        try {
            if (bookManager != null) {
                bookManager.registerChangedListener(bookListChangedListener);
            }
        } catch (RemoteException exception) {
            LogTool.error(exception.getMessage());
        }
    }

    //注销变化监听
    private void unregisterChangedListener() {
        try {
            if (bookManager != null && bookManager.asBinder().isBinderAlive()) {
                bookManager.unregisterChangedListener(bookListChangedListener);
            }
        } catch (RemoteException exception) {
            LogTool.error(exception.getMessage());
        }
    }

    private void checkNotDisposed() {
        if (isDisposed) throw new IllegalStateException("RemoteBookHelper was disposed of, so it cannot be used.");
    }

    private void checkSetupDone(String operation) {
        if (!isSetupDone) {
            LogTool.error("Illegal state for operation (" + operation + "): IBookManager is not set up.");
            throw new IllegalStateException("IBookManager is not set up. Can't perform operation: " + operation);
        }
    }

    private RemoteResult getSetupResult(int response) {
        RemoteResult result = null;
        if (response == ResultCode.RESPONSE_RESULT_OK) {
            result = new RemoteResult(response, "IBookManager setup successful");
        } else if (response == ResultCode.RESPONSE_RESULT_INVALID_APPID) {
            result = new RemoteResult(response, "IBookManager invalid appId");
        } else if (response == ResultCode.RESPONSE_RESULT_INVALID_SECRET) {
            result = new RemoteResult(response, "IBookManager invalid secret");
        } else if (response == ResultCode.RESPONSE_RESULT_NOT_SUPPORT_VERSION) {
            result = new RemoteResult(response, "IBookManager is not support your sdk's version");
        }
        return result;
    }

}
