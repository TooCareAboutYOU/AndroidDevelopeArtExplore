// IBookManager.aidl
package com.developeartexplore.mode_ipc.main.aidl;

import com.developeartexplore.mode_ipc.main.aidl.Book;
import com.developeartexplore.mode_ipc.main.aidl.IOnNewBookArrvedListener;


// Declare any non-default types here with import statements
interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrvedListener listener);
    void unregisterListener(IOnNewBookArrvedListener listener);
}
