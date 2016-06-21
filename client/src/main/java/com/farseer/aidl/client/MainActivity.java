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
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.farseer.aidl.DevBook;
import com.farseer.aidl.ResultCode;
import com.farseer.aidl.client.remote.RemoteBookHelper;
import com.farseer.aidl.client.remote.RemoteConstant;
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

    private MaterialDialog materialDialog;

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
                hideDialog();
                showAboutDialog();
            }
        });

        setSecondActionImageView(toolbar, R.drawable.action_filter, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
                showFilterDialog();
            }
        });

        setThirdActionImageView(toolbar, R.drawable.action_add, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideDialog();
                showAddDialog();
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

                if(RemoteConstant.ERROR_SETUP_SUCCESS == result.getResponse()) {
                    Toast.makeText(MainActivity.this, R.string.setup_success, Toast.LENGTH_LONG).show();
                    filterBookList("");
                    remoteBookHelper.registerDataChangeListener(new RemoteBookHelper.OnDataChangeListener() {
                        @Override
                        public void onChanged(DevBook devBook) {
                            filterBookList("");
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.setup_failed, result.getDescription()), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //显示About对话框
    private void showAboutDialog() {
        materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.action_about)
                .content(Html.fromHtml(getString(R.string.about_content)))
                .contentLineSpacing(1.6f)
                .build();
        materialDialog.show();
    }


    private void showAddDialog() {
        View customView = LayoutInflater.from(this).inflate(R.layout.dialog_book_add, null);
        final AppCompatEditText editText = ButterKnife.findById(customView, R.id.inputEditText);

        materialDialog = new MaterialDialog.Builder(this)
                .customView(customView, false)
                .positiveColorRes(R.color.positive_color)
                .negativeColorRes(R.color.positive_color)
                .positiveText(R.string.action_sure)
                .negativeText(R.string.action_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String name = editText.getText().toString();
                        addBook(name);
                    }
                })
                .build();


        materialDialog.show();
    }

    private void showFilterDialog() {
        View customView = LayoutInflater.from(this).inflate(R.layout.dialog_book_filter, null);
        final AppCompatEditText editText = ButterKnife.findById(customView, R.id.inputEditText);

        materialDialog = new MaterialDialog.Builder(this)
                .customView(customView, false)
                .positiveColorRes(R.color.positive_color)
                .negativeColorRes(R.color.positive_color)
                .positiveText(R.string.action_sure)
                .negativeText(R.string.action_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String filter = editText.getText().toString();
                        filterBookList(filter);
                    }
                })
                .build();


        materialDialog.show();
    }

    //隐藏对话框
    private void hideDialog() {
        if (materialDialog != null) {
            materialDialog.dismiss();
            materialDialog = null;
        }
    }

    //解绑服务.
    private void dispose() {
        remoteBookHelper.unregisterDataChangeListener();
        remoteBookHelper.dispose();
        remoteBookHelper = null;
    }

    //添加Book.
    private void addBook(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        int bookId = random.nextInt(1000);
        DevBook book = new DevBook(bookId, name);
        remoteBookHelper.addBook(book);
    }

    //获得Book
    private void getBook() {
        int bookId = new Random(6).nextInt();
        remoteBookHelper.getBook(bookId, new RemoteBookHelper.OnRequestBookListener() {
            @Override
            public void onSuccess(int response, DevBook book) {
                if (ResultCode.RESPONSE_RESULT_CLIENT_NOT_READY == response) {
                    Toast.makeText(MainActivity.this, R.string.client_not_ready, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void filterBookList(String filter) {
        remoteBookHelper.searchBook(filter, new RemoteBookHelper.OnSearchBookListener() {
            @Override
            public void onSuccess(int response, List<DevBook> bookList) {
                if (ResultCode.SEARCH_BOOK_SUCCESS == response) {
                    MainActivity.this.bookList = bookList;
                    recyclerAdapter.notifyDataSetChanged();
                } else if (ResultCode.RESPONSE_RESULT_CLIENT_NOT_READY == response) {
                    Toast.makeText(MainActivity.this, R.string.client_not_ready, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

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
