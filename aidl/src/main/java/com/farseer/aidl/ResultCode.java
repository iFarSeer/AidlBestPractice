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

package com.farseer.aidl;

/**
 * class description
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 16/5/20
 */
public class ResultCode {

    public static final int RESPONSE_RESULT_OK = 0;
    public static final int RESPONSE_RESULT_INVALID_APPID = 1;
    public static final int RESPONSE_RESULT_INVALID_SECRET = 2;
    public static final int RESPONSE_RESULT_NOT_SUPPORT_VERSION = 3;
    public static final int RESPONSE_RESULT_CLIENT_NOT_READY = 4;

    public static final int GET_BOOK_SUCCESS = 0;
    public static final int GET_BOOK_NONE = 1;
    public static final int GET_BOOK_EMPTY_LIST = 2;

    public static final int SEARCH_BOOK_SUCCESS = 0;
    public static final int SEARCH_BOOK_FAILED = 1;
}
