package com.thssh.smsdispatcher;

public class NoAppKeyException extends Exception {
    public NoAppKeyException() {
        super("you should set appKey.");
    }
}
