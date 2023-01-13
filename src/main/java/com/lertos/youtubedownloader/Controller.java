package com.lertos.youtubedownloader;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.*;

public class Controller {

    private SongList songList = new SongList();
    private final double labelSize = 25.0;

    @FXML
    private TextField tfFolderPath;
    @FXML
    private TextField tfNewURL;

    @FXML
    private VBox vbSongList;


    public void openFileChooser() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(Main.stage);

        if (selectedDirectory != null)
            tfFolderPath.setText(selectedDirectory.getAbsolutePath());
    }

    public void addSongToList() {
        String URL = tfNewURL.getText();
        String songName = getSongName(URL);

        if (songName.isEmpty())
            return;

        Button button = new Button("\uD83D\uDDD1");
        Label label = new Label(songName);

        label.setMinHeight(labelSize);
        label.setPrefHeight(labelSize);

        int index = songList.addSong(songName, URL);

        HBox hbox = new HBox(button, label);
        hbox.setSpacing(10);

        addDeleteButtonEvent(hbox, button, index);

        vbSongList.getChildren().add(hbox);
    }

    private void addDeleteButtonEvent(HBox hbox, Button button, int index) {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                songList.removeSong(index);
                vbSongList.getChildren().remove(hbox);
            }
        });
    }

    //--max-filesize = Do not download the song, just to get the title
    //--print title = Gets the title of the video
    private String getSongName(String URL) {
        String songName = "";

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "yt-dlp " + URL + " -f \"ba\" --max-filesize 1k --print title");
            builder.redirectErrorStream(true);
            Process p = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while (true) {
                line = r.readLine();
                if (line == null)
                    break;
                if (!line.startsWith("ERROR"))
                    songName = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songName;
    }

    public void downloadAllSongs() {
        for (Song song : songList.getSongs()) {
            downloadSong(song);
        }
    }

    //COMMAND TO USE (example): yt-dlp [URL] -P [PATH_TO_DOWNLOAD_TO] -f "ba"
    //URL Example: https://youtu.be/lbLug8M_5HQ
    private void downloadSong(Song song) {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "yt-dlp " + song.getURL() + " -P " + tfFolderPath.getText() + " -f \"ba\"");
            builder.redirectErrorStream(true);
            Process p = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while (true) {
                line = r.readLine();
                if (line == null)
                    break;
                else {
                    if (line.startsWith("[download]")) {
                        System.out.println(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}