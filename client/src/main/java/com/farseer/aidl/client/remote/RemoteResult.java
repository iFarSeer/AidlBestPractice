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

import android.text.TextUtils;

/**
 * class description
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 16/6/20
 */
public class RemoteResult {
    private int response;
    private String description;

    public RemoteResult(int response, String description) {
        this.response = response;
        this.description = description;
    }

    public int getResponse() {
        return response;
    }

    public String getDescription() {
        return description;
    }


    public String toString() {
        return "RemoteResult: " + getDescription();
    }
}
