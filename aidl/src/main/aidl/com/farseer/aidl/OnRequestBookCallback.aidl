// OnAddBookCallback.aidl
package com.farseer.aidl;

import com.farseer.aidl.DevBook;
// Declare any non-default types here with import statements

interface OnRequestBookCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onSuccess(in DevBook book);

    void onFailed(int code);
}
