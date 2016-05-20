package com.farseer.aidl.client;

import com.farseer.aidl.Book;
import com.farseer.aidl.IBookManager;
import com.farseer.aidl.OnBookListChangedListener;
import com.farseer.aidl.ServiceIntentConvertor;

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

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        public void onBookListChanged(Book book) throws RemoteException {
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

    @OnClick(R.id.bindService)
    public void bindService() {
        String action = "com.farseer.aidl.service.RemoteBookService";
        Intent intent = new Intent(action);
        Intent serviceIntent = new Intent(ServiceIntentConvertor.convertImplicitExplicitIntent(this, intent));
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @OnClick(R.id.unbindService)
    public void unbindService() {
        unregisterChangedListener();
        if (bookManager != null) {
            unbindService(serviceConnection);
        }
        bookManager = null;
    }

    @OnClick(R.id.addBook)
    public void addBook() {
        int bookId = random.nextInt(1000);
        Book book = new Book(bookId, getString(R.string.format_book_name, bookId));

        try {
            if (bookManager != null) {
                bookManager.addBook(book);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "addBook failed");
        }
    }

    @OnClick(R.id.listBook)
    public void listBook() {

        try {
            if (bookManager != null) {
                List<Book> bookList = bookManager.getBookList();

                if (bookList != null) {
                    for (Book book : bookList) {
                        Log.i(TAG, book.toString());
                    }
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
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
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    //注册变化监听
    private void registerChangedListener() {
        try {
            if (bookManager != null) {
                bookManager.registerChangedListener(changedListener);
            }
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //注销变化监听
    private void unregisterChangedListener() {
        try {
            if (bookManager != null && bookManager.asBinder().isBinderAlive()) {
                bookManager.unregisterChangedListener(changedListener);
            }
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
