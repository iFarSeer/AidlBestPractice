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

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;

/**
 * Activity基类
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 2016-04-19
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 设置Toolbar的title.
     *
     * @param toolbar toolbar
     * @param resId   title的resId
     */
    protected void setTitleTextView(Toolbar toolbar, @StringRes int resId) {
        setTitleTextView(toolbar, getString(resId));
    }

    /**
     * 设置ToolBar的title.
     *
     * @param toolbar toolbar
     * @param text    title的内容
     */
    protected void setTitleTextView(Toolbar toolbar, String text) {
        TextView titleTextView = ButterKnife.findById(toolbar, R.id.titleTextView);
        if (titleTextView != null) {
            titleTextView.setText(text);
            titleTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 从右计数,设置第一个按钮以及事件
     *
     * @param toolbar         toolbar
     * @param resId           图片resId
     * @param onClickListener 点击事件
     */
    protected void setFirstActionImageView(Toolbar toolbar, @DrawableRes int resId, View.OnClickListener onClickListener) {
        setActionImageView(toolbar, R.id.rFirstActionView, R.id.rFirstActionImageView, resId, onClickListener);
    }

    /**
     * 从右计数,设置第二个按钮以及事件
     *
     * @param toolbar         toolbar
     * @param resId           图片resId
     * @param onClickListener 点击事件
     */
    protected void setSecondActionImageView(Toolbar toolbar, @DrawableRes int resId, View.OnClickListener onClickListener) {
        setActionImageView(toolbar, R.id.rSecondActionView, R.id.rSecondActionImageView, resId, onClickListener);
    }

    /**
     * 从右计数,设置第三个按钮以及事件
     *
     * @param toolbar         toolbar
     * @param resId           图片resId
     * @param onClickListener 点击事件
     */
    protected void setThirdActionImageView(Toolbar toolbar, @DrawableRes int resId, View.OnClickListener onClickListener) {
        setActionImageView(toolbar, R.id.rThirdView, R.id.rThirdImageView, resId, onClickListener);
    }

    private void setActionImageView(Toolbar toolbar, @IdRes int actionViewId, @IdRes int imageViewId, @DrawableRes int resId, View.OnClickListener onClickListener) {
        View actionView = ButterKnife.findById(toolbar, actionViewId);
        ImageView imageView = ButterKnife.findById(toolbar, imageViewId);
        if (actionView != null) {
            actionView.setVisibility(View.VISIBLE);
            actionView.setOnClickListener(onClickListener);
        }
        if (imageView != null) {
            imageView.setImageResource(resId);
        }
    }

}