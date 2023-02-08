package com.lertos.youtubedownloader;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.*;

public class Controller {

    private final SongList songList = new SongList();
    private int downloadedSongs;
    private int totalSongsToDownload;

    @FXML
    private TextField tfFolderPath;
    @FXML
    private TextField tfNewURL;

    @FXML
    private Label lbProgress;

    @FXML
    private VBox vbSongList;

    @FXML
    private HBox hbProgressBar;

    private void showDialog(String message) {
        Dialog<String> dialog = new Dialog<>();
        ButtonType buttonType = new ButtonType("Understood", ButtonBar.ButtonData.OK_DONE);
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(buttonType);
        dialog.showAndWait();
    }

    public void openFileChooser() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(Main.stage);

        if (selectedDirectory != null)
            tfFolderPath.setText(selectedDirectory.getAbsolutePath());
    }

    private boolean songExistsInFolder(String songName) {
        File file = new File(tfFolderPath.getText());

        for (String fileName : file.list()) {
            if (fileName.contains(songName))
                return true;
        }
        return false;
    }

    private boolean songExistsInSongList(String songName) {
        for (Song song : songList.getSongs()) {
            if (song.videoTitle().equalsIgnoreCase(songName))
                return true;
        }
        return false;
    }

    private String getTrimmedURL(String URL) {
        int index = URL.indexOf("&");

        if (index == -1)
            return URL;

        return URL.substring(0, index);
    }

    private String getProperSongName(String songName) {
        songName = songName.replace("_", " ");

        String[] arr = songName.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String word : arr) {
            sb.append(word.substring(0, 1).toUpperCase());
            sb.append(word.substring(1));
            sb.append(" ");
        }
        return sb.toString();
    }

    public void addSongToList() {
        String URL = tfNewURL.getText();
        String songName;

        if (URL.isEmpty()) {
            showDialog("You must enter a URL in the field beside 'Add'");
            return;
        }

        URL = getTrimmedURL(URL);
        songName = getProperSongName(getSongName(URL));

        if (songExistsInFolder(songName)) {
            showDialog("That song is already downloaded");
            tfNewURL.setText("");
            return;
        }

        if (songExistsInSongList(songName)) {
            showDialog("That song already exists in the list");
            tfNewURL.setText("");
            return;
        }

        Button button = new Button("\uD83D\uDDD1");
        Label label = new Label(songName);

        double labelSize = 25.0;

        label.setMinHeight(labelSize);
        label.setPrefHeight(labelSize);

        int index = songList.addSong(songName, URL);

        HBox hbox = new HBox(button, label);
        hbox.setSpacing(10);

        addDeleteButtonEvent(hbox, button, index);

        vbSongList.getChildren().add(hbox);
        tfNewURL.clear();
    }

    private void addDeleteButtonEvent(HBox hbox, Button button, int index) {
        button.setOnAction(event -> {
            songList.removeSong(index);
            vbSongList.getChildren().remove(hbox);
        });
    }

    //--max-filesize = Do not download the song, just to get the title
    //--print title = Gets the title of the video
    private String getSongName(String URL) {
        String songName = "";

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "yt-dlp " + URL + " -f m4a --max-filesize 1k --print filename -o \"%(title)s.%(ext)s\" --restrict-filenames");
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
        //Check if there are any songs
        if (songList.getSongs().size() == 0) {
            showDialog("You must add songs before downloading them");
            return;
        }

        //Check if the download location is entered
        if (tfFolderPath.getText().isEmpty()) {
            showDialog("You must specify a valid folder location for the downloads");
            return;
        }
        //Check if the download location is an existing directory
        File file = new File(tfFolderPath.getText());
        if (!file.exists()) {
            showDialog("Check that the directory entered is valid/exists");
            return;
        }

        //Change the progress bar values
        downloadedSongs = 0;
        totalSongsToDownload = songList.getSongs().size();

        lbProgress.setText(downloadedSongs + " / " + totalSongsToDownload);

        hbProgressBar.setVisible(true);

        //Start downloading all the songs in the list
        for (Song song : songList.getSongs()) {
            downloadSong(song);
        }
    }

    public void deleteAllSongs() {
        songList.removeAllSongs();
        vbSongList.getChildren().clear();
    }

    //COMMAND TO USE (example): yt-dlp [URL] -P [PATH_TO_DOWNLOAD_TO] -f "ba"
    //URL Example: https://youtu.be/lbLug8M_5HQ
    private void downloadSong(Song song) {
        Runnable task = () -> {
            try {
                ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "yt-dlp " + song.URL() + " -P " + tfFolderPath.getText() + " -f m4a -o \"" + song.videoTitle() + ".%(ext)s\" --restrict-filenames");
                Process p = builder.start();

                p.waitFor();
                Platform.runLater(() -> {
                    downloadedSongs++;
                    lbProgress.setText(downloadedSongs + " / " + totalSongsToDownload);

                    if (downloadedSongs == totalSongsToDownload)
                        showDialog("All songs successfully downloaded");
                });
            } catch (Exception e) { e.printStackTrace(); }
        };
        new Thread(task).start();
    }
}