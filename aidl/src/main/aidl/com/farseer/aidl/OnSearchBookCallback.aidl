// OnSearchBookCallback.aidl
package com.farseer.aidl;

import com.farseer.aidl.DevBook;
// Declare any non-default types here with import statements

interface OnSearchBookCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onSuccess(in List<DevBook> bookList);

    void onFailed(int code);
}
