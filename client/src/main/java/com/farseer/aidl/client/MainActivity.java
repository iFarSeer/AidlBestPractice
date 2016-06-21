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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.farseer.aidl.DevBook;
import com.farseer.aidl.ResultCode;
import com.farseer.aidl.client.remote.RemoteBookHelper;
import com.farseer.aidl.client.remote.RemoteResult;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;
import java.util.Random;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    Random random = new Random();
    RemoteBookHelper remoteBookHelper = null;

    private RecyclerAdapter recyclerAdapter;

    private List<DevBook> bookList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initView();

        remoteBookHelper = new RemoteBookHelper(this);

        setup();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispose();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        setTitleTextView(toolbar, R.string.app_name);

        setFirstActionImageView(toolbar, R.drawable.action_more, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setSecondActionImageView(toolbar, R.drawable.action_filter, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setThirdActionImageView(toolbar, R.drawable.action_add, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .colorResId(R.color.transport_color)
                        .sizeResId(R.dimen.list_divider_height)
                        .marginResId(R.dimen.item_margin, R.dimen.item_margin)
                        .build());
        recyclerAdapter = new RecyclerAdapter();

        recyclerView.setAdapter(recyclerAdapter);
    }

    //绑定服务.
    private void setup() {
        remoteBookHelper.setup(new RemoteBookHelper.OnSetupFinishedListener() {
            @Override
            public void onSetupFinished(RemoteResult result) {
                filterBookList("");
            }
        });
    }

    //解绑服务.
    private void dispose() {
        remoteBookHelper.unregisterDataChangeListener();
        remoteBookHelper.dispose();
        remoteBookHelper = null;
    }

    //添加Book.
    private void addBook() {
        int bookId = random.nextInt(1000);
        DevBook book = new DevBook(bookId, getString(R.string.format_book_name, bookId));
        remoteBookHelper.addBook(book);
    }

    //获得Book
    private void getBook() {
        int bookId = new Random(6).nextInt();
        remoteBookHelper.getBook(bookId, new RemoteBookHelper.OnRequestBookListener() {
            @Override
            public void onSuccess(int response, DevBook book) {

            }
        });
    }

    //搜索Book.
    private void searchBook() {
        String filter = "第";
        filterBookList(filter);
    }

    private void filterBookList(String filter) {
        remoteBookHelper.searchBook(filter, new RemoteBookHelper.OnSearchBookListener() {
            @Override
            public void onSuccess(int response, List<DevBook> bookList) {
                if (ResultCode.SEARCH_BOOK_SUCCESS == response) {
                    MainActivity.this.bookList = bookList;
                    recyclerAdapter.notifyDataSetChanged();
                    if (bookList != null) {
                        for (DevBook book : bookList) {
                            Log.i(TAG, book.toString());
                        }
                    }
                }
            }
        });
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private View.OnClickListener onClickListener;
        private View.OnLongClickListener onLongClickListener;

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_book, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final DevBook item = bookList.get(position);


            holder.nameTextView.setText(item.getBookName());
            holder.descriptionTextView.setText(item.getBookName());
            holder.itemView.setTag(item);
            holder.itemView.setOnClickListener(onClickListener);
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (bookList != null) {
                count = bookList.size();
            }
            return count;
        }

        /**
         * RecyclerAdapter's ViewHolder.
         */
        class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.nameTextView)
            TextView nameTextView;
            @Bind(R.id.descriptionTextView)
            TextView descriptionTextView;

            /**
             * ViewHolder.
             *
             * @param view view
             */
            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
