// IBookManager.aidl
package com.farseer.aidl;

import com.farseer.aidl.Book;
// Declare any non-default types here with import statements

interface IBookManager {

    List<Book> getBookList();

    void addBook(in Book book);
}
