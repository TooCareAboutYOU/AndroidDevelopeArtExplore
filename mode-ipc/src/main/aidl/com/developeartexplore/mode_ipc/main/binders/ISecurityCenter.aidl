// ISecurityCenter.aidl
package com.developeartexplore.mode_ipc.main.binders;

// Declare any non-default types here with import statements

interface ISecurityCenter {
    String encypt(in String content);
    String decypt(in String password);
}
