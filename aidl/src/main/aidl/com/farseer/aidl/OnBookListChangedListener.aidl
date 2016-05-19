// IOnBookListChangedListener.aidl
package com.farseer.aidl;

import com.farseer.aidl.Book;
// Declare any non-default types here with import statements

interface OnBookListChangedListener {

    void onBookListChanged(in Book newBook);
}
