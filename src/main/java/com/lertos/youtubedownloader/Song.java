package com.lertos.youtubedownloader;

public class Song {

    private final int index;
    private final String videoTitle;
    private final String URL;

    public Song(int index, String videoTitle, String URL) {
        this.index = index;
        this.videoTitle = videoTitle;
        this.URL = URL;
    }

    public int getIndex() {
        return index;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getURL() {
        return URL;
    }
}
