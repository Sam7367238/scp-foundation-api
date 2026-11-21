package org.playground.scpapi.media;

public class MediaNotFoundException extends RuntimeException {
    public MediaNotFoundException() {
        super("The media supplied has not been found");
    }
}
