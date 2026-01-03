package com.reelance.exception;

public class InstagramHandleAlreadyExistsException
        extends RuntimeException {

    public InstagramHandleAlreadyExistsException(String handle) {
        super("Instagram handle already exists: " + handle);
    }
}
