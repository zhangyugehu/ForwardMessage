package com.thssh.smsdispatcher.exception;

public class NoAppKeyException extends Exception {
    public NoAppKeyException() {
        super("you should set appKey.");
    }
}
