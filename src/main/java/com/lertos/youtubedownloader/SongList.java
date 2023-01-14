package com.lertos.youtubedownloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongList {

    private final List<Song> songs;
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
            if (song.index() == index) {
                songs.remove(song);
                break;
            }
        }
    }

    public void removeAllSongs() {
        songs.clear();
    }

    public int addSong(String videoTitle, String URL) {
        Song song = new Song(currentIndex, videoTitle, URL);
        int index = currentIndex;

        songs.add(song);
        currentIndex++;

        return index;
    }

}
