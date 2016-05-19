package com.farseer.aidl.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.farseer.aidl.Book;
import com.farseer.aidl.IBookManager;
import com.farseer.aidl.ServiceIntentConvertor;

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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected");
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
        unbindService(serviceConnection);
    }

    @OnClick(R.id.addBook)
    public void addBook() {
        int bookId = random.nextInt(1000);
        Book book = new Book(bookId, getString(R.string.format_book_name, bookId));

        try {
            bookManager.addBook(book);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, "addBook failed");
        }
    }

    @OnClick(R.id.listBook)
    public void listBook() {

        try {
            List<Book> bookList = bookManager.getBookList();

            if (bookList != null) {
                for (Book book : bookList) {
                    Log.i(TAG, book.toString());
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, "getBookList failed");
        }
    }


}
