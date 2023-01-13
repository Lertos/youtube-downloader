package com.lertos.youtubedownloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongList {

    private List<Song> songs;
    private int currentIndex;

    public SongList() {
        this.songs = new ArrayList<>();
        this.currentIndex = 0;
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }

    public void removeSong(int index) {
        for (Song song : songs) {
            if (song.getIndex() == index)
                songs.remove(index);
        }
    }

    public int addSong(String videoTitle, String URL) {
        Song song = new Song(currentIndex, videoTitle, URL);
        int index = currentIndex;

        songs.add(song);
        currentIndex++;

        return index;
    }

    private class Song {

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

}
