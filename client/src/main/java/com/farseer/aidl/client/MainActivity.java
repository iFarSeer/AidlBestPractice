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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.farseer.aidl.*;
import com.farseer.aidl.client.remote.RemoteBookHelper;
import com.farseer.aidl.client.remote.RemoteResult;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    Random random = new Random();

    RemoteBookHelper remoteBookHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        remoteBookHelper = new RemoteBookHelper(this);
    }

    /**
     * 绑定服务.
     */
    @OnClick(R.id.setup)
    public void setup() {
        remoteBookHelper.setup(new RemoteBookHelper.OnSetupFinishedListener() {
            @Override
            public void onSetupFinished(RemoteResult result) {
                filterBookList("");
            }
        });
    }

    /**
     * 解绑服务.
     */
    @OnClick(R.id.dispose)
    public void dispose() {
        remoteBookHelper.unregisterDataChangeListener();
        remoteBookHelper.dispose();
        remoteBookHelper = null;
    }

    /**
     * 添加Book.
     */
    @OnClick(R.id.addBook)
    public void addBook() {
        int bookId = random.nextInt(1000);
        DevBook book = new DevBook(bookId, getString(R.string.format_book_name, bookId));
        remoteBookHelper.addBook(book);
    }

    /**
     * 获得Book
     */
    @OnClick(R.id.getBook)
    public void getBook() {
        int bookId = new Random(6).nextInt();
        remoteBookHelper.getBook(bookId, new RemoteBookHelper.OnRequestBookListener() {
            @Override
            public void onSuccess(int response, DevBook book) {

            }
        });
    }

    /**
     * 搜索Book.
     */
    @OnClick(R.id.searchBook)
    public void searchBook() {
        String filter = "第";
        filterBookList(filter);
    }

    public void filterBookList(String filter) {
        remoteBookHelper.searchBook(filter, new RemoteBookHelper.OnSearchBookListener() {
            @Override
            public void onSuccess(int response, List<DevBook> bookList) {
                if (ResultCode.SEARCH_BOOK_SUCCESS == response) {
                    if (bookList != null) {
                        for (DevBook book : bookList) {
                            Log.i(TAG, book.toString());
                        }
                    }
                }
            }
        });
    }
}
