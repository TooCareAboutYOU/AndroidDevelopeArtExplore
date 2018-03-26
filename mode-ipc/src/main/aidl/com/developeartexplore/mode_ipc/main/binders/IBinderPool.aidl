// IBinderPool.aidl
package com.developeartexplore.mode_ipc.main.binders;

// Declare any non-default types here with import statements

interface IBinderPool {
   IBinder queryBinder(in int binderCode);
}
