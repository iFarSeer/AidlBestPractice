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

package com.farseer.aidl.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.farseer.aidl.*;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    Random random = new Random();

    IBookManager bookManager;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected");
            bookManager = IBookManager.Stub.asInterface(service);
            linkToDeath(service);
            registerChangedListener();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected");
            bookManager = null;
        }
    };

    private OnBookListChangedListener changedListener = new OnBookListChangedListener.Stub() {

        @Override
        public void onBookListChanged(DevBook book) throws RemoteException {
            Log.i(TAG, "onBookListChanged:book = " + book.toString());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 绑定服务.
     */
    @OnClick(R.id.bindService)
    public void bindService() {
        String action = "com.farseer.aidl.service.RemoteBookService";
        Intent intent = new Intent(action);
        Intent serviceIntent = new Intent(ServiceIntentConvertor.convertImplicitExplicitIntent(this, intent));
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务.
     */
    @OnClick(R.id.unbindService)
    public void unbindService() {
        unregisterChangedListener();
        if (bookManager != null) {
            unbindService(serviceConnection);
        }
        bookManager = null;
    }

    /**
     * 添加Book.
     */
    @OnClick(R.id.addBook)
    public void addBook() {
        int bookId = random.nextInt(1000);
        DevBook book = new DevBook(bookId, getString(R.string.format_book_name, bookId));

        try {
            if (bookManager != null) {
                bookManager.addBook(book);
            }
        } catch (RemoteException exception) {
            Log.e(TAG, exception.getMessage());
            Log.e(TAG, "addBook failed");
        }
    }

    /**
     * 获得Book List.
     */
    @OnClick(R.id.listBook)
    public void listBook() {
        try {
            if (bookManager != null) {
                List<DevBook> bookList = bookManager.getBookList();

                if (bookList != null) {
                    for (DevBook book : bookList) {
                        Log.i(TAG, book.toString());
                    }
                }
            }
        } catch (RemoteException exception) {
            Log.e(TAG, exception.getMessage());
            Log.e(TAG, "getBookList failed");
        }
    }

    /**
     * 获得Book
     */
    @OnClick(R.id.getBook)
    public void getBook() {
        try {
            if (bookManager != null) {
                int bookId = random.nextInt(4);
                OnRequestBookCallback callback = new OnRequestBookCallback.Stub() {
                    @Override
                    public void onSuccess(DevBook book) throws RemoteException {
                        Log.i(TAG, "找书成功:book = " + book.toString());
                    }

                    @Override
                    public void onFailed(int code) throws RemoteException {
                        Log.i(TAG, "找书失败: code = " + code);
                    }
                };
                bookManager.getBook(bookId, callback);
            }
        } catch (RemoteException exception) {
            Log.e(TAG, exception.getMessage());
            Log.e(TAG, "getBookList failed");
        }
    }

    //为Binder设置死亡代理,当Binder死亡,DeathRecipient收到通知,客户端重连服务
    private void linkToDeath(IBinder binder) {
        try {
            binder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    bindService();
                }
            }, 0);
        } catch (RemoteException exception) {
            Log.e(TAG, exception.getMessage());
        }
    }


    //注册变化监听
    private void registerChangedListener() {
        try {
            if (bookManager != null) {
                bookManager.registerChangedListener(changedListener);
            }
        } catch (RemoteException exception) {
            Log.e(TAG, exception.getMessage());
        }
    }

    //注销变化监听
    private void unregisterChangedListener() {
        try {
            if (bookManager != null && bookManager.asBinder().isBinderAlive()) {
                bookManager.unregisterChangedListener(changedListener);
            }
        } catch (RemoteException exception) {
            Log.e(TAG, exception.getMessage());
        }
    }
}
