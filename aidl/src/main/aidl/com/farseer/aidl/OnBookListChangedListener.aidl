// IOnBookListChangedListener.aidl
package com.farseer.aidl;

import com.farseer.aidl.DevBook;
// Declare any non-default types here with import statements

interface OnBookListChangedListener {

    void onBookListChanged(in DevBook newBook);
}
