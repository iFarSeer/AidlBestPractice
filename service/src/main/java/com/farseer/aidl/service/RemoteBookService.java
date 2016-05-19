/*
 * RemoteBookService      2016-05-19
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.farseer.aidl.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 2016-05-19
 */
public class RemoteBookService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
