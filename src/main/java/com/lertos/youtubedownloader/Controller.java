package com.lertos.youtubedownloader;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.*;

public class Controller {

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

        HBox hbox = new HBox(button, label);
        hbox.setSpacing(10);

        vbSongList.getChildren().add(hbox);
    }

    private String getSongName(String URL) {
        try {
            //--max-filesize = Do not download the song, just to get the title
            //--print title = Gets the title of the video
            //Example URL: https://youtu.be/lbLug8M_5HQ
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
                    return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //COMMAND TO USE (example): yt-dlp [URL] -P [PATH_TO_DOWNLOAD_TO] -f "ba"
    public void downloadSongs() {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "yt-dlp https://youtu.be/lbLug8M_5HQ -P \"C:\\Users\\Dylan\\Downloads\" -f \"ba\"");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null)
                    break;
                else {
                    if (line.startsWith("ERROR")) {
                        System.out.println("y");
                    }
                    else {
                        System.out.println("no");
                    }
                }
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}