// IOnNewBookArrvedListener.aidl
package com.developeartexplore.mode_ipc.main.aidl;

import com.developeartexplore.mode_ipc.main.aidl.Book;
// Declare any non-default types here with import statements

interface IOnNewBookArrvedListener {
    void onNewBookArrived(in Book newbook);
}
