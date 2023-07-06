package com.lertos.youtubedownloader;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;

public class Controller {

    private final SongList songList = new SongList();
    private int downloadedSongs;
    private int totalSongsToDownload;
    private final String settingsFileName = "settings.txt";
    private final String settingsFormatAttribute = "format";
    private final String settingsFFMPEGAttribute = "ffmpeg";
    private final String settingsOutputPathAttribute = "outputPath";
    private int openSettingsFileAttempt = 0;


    @FXML
    private TextField tfFFMPEGPath;
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
    @FXML
    private HBox hbFFMPEGChooser;
    @FXML
    private ToggleGroup tgFileFormat;
    @FXML
    private RadioButton toggleMP3;
    @FXML
    private RadioButton toggleM4A;

    public void saveSettings() {
        try {
            File file = new File(settingsFileName);
            file.createNewFile();

            FileWriter fw = new FileWriter(file);
            fw.write("");

            String format = getSelectedFormat();

            fw.append(settingsFormatAttribute + "=" + format + "\n");

            if (format.equalsIgnoreCase("MP3"))
                fw.append(settingsFFMPEGAttribute + "=" + tfFFMPEGPath.getText() + "\n");

            fw.append(settingsOutputPathAttribute + "=" + tfFolderPath.getText() + "\n");

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSettings() {
        try {
            File file = new File(settingsFileName);
            Reader reader = new BufferedReader(new FileReader(file));

            StringBuilder sb = new StringBuilder();
            int nextChar = reader.read();

            while(nextChar != -1) {
                char curChar = (char) nextChar;

                if (curChar == '\n') {
                    loadSetting(sb.toString());
                    sb = new StringBuilder();
                } else
                    sb.append(curChar);

                nextChar = reader.read();
            }
            reader.close();
        } catch (Exception e) {
            saveSettings();

            if (openSettingsFileAttempt == 0) {
                loadSettings();
                openSettingsFileAttempt++;
            }
        }
    }

    private void loadSetting(String settingLine) {
        int equalsIndex = settingLine.indexOf("=");

        String setting = settingLine.substring(0, equalsIndex);
        String value = settingLine.substring(equalsIndex + 1);

        switch (setting) {
            case settingsFormatAttribute -> {
                if (value.equalsIgnoreCase("MP3")) {
                    toggleMP3.setSelected(true);
                    hbFFMPEGChooser.setVisible(true);
                }
                else
                    toggleM4A.setSelected(true);
            }
            case settingsFFMPEGAttribute -> tfFFMPEGPath.setText(value);
            case settingsOutputPathAttribute -> tfFolderPath.setText(value);
        }

    }

    private String getSelectedFormat() {
        RadioButton selected = (RadioButton) tgFileFormat.getSelectedToggle();
        return selected.getText();
    }

    public void onFileFormatToggleChange() {
        String format = getSelectedFormat();

        if (format.equalsIgnoreCase("MP3")) {
            hbFFMPEGChooser.setVisible(true);
        } else {
            hbFFMPEGChooser.setVisible(false);
        }
    }

    private void showDialog(String message) {
        Dialog<String> dialog = new Dialog<>();
        ButtonType buttonType = new ButtonType("Understood", ButtonBar.ButtonData.OK_DONE);
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(buttonType);
        dialog.showAndWait();
    }

    public void openFFMPEGFileChooser() {
        final FileChooser fileChooser = new FileChooser();
        final File selectedFile = fileChooser.showOpenDialog(Main.stage);

        if (selectedFile != null) {
            if (!selectedFile.getName().equalsIgnoreCase("ffmpeg.exe")) {
                showDialog("You must choose the 'ffmpeg.exe' file here");
                return;
            }
            tfFFMPEGPath.setText(selectedFile.getAbsolutePath());
        }
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

        if (hbFFMPEGChooser.isVisible())
            songName = songName.replace(".m4a", ".mp3");

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

        if (hbFFMPEGChooser.isVisible() && tfFFMPEGPath.getText().isEmpty()) {
            showDialog("When using MP3 you must enter the URL to the ffmpeg.exe file");
            return;
        }

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
            StringBuilder sb = new StringBuilder("yt-dlp ");
            sb.append(URL);
            sb.append(" -f m4a ");

            if (hbFFMPEGChooser.isVisible()) {
                sb.append(" -x --audio-format mp3 --ffmpeg-location ");
                sb.append(tfFFMPEGPath.getText());
            }

            sb.append(" --max-filesize 1k --print filename -o \"%(title)s.%(ext)s\" --restrict-filenames");

            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", sb.toString());
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
            System.out.println("=======================");
            e.printStackTrace();
        }
        return songName;
    }

    @FXML
    private boolean updateYTDLP() {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", " yt-dlp -U");
            builder.redirectErrorStream(true);
            Process p = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while (true) {
                line = r.readLine();
                if (line == null) {
                    showDialog("YT-DLP has been updated");
                    return true;
                }
                if (line.startsWith("yt-dlp is up to date")) {
                    showDialog("YT-DLP is already up to date");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        showDialog("YT-DLP has been updated");
        return true;
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
                StringBuilder sb = new StringBuilder("yt-dlp ");
                sb.append(song.URL());
                sb.append(" -P ");
                sb.append(tfFolderPath.getText());
                sb.append(" -f m4a ");

                if (hbFFMPEGChooser.isVisible()) {
                    sb.append(" -x --audio-format mp3 --ffmpeg-location ");
                    sb.append(tfFFMPEGPath.getText());
                }

                sb.append(" -o \"");
                sb.append(song.videoTitle());
                sb.append(".%(ext)s\" --restrict-filenames");

                System.out.println(sb.toString());

                ProcessBuilder builder = new ProcessBuilder("cmd", "/c", sb.toString());
                Process p = builder.start();

                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;

                while (true) {
                    line = r.readLine();
                    if (line == null)
                        break;
                }

                p.waitFor();
                Platform.runLater(() -> {
                    downloadedSongs++;
                    lbProgress.setText(downloadedSongs + " / " + totalSongsToDownload);

                    if (downloadedSongs == totalSongsToDownload) {
                        showDialog("All songs successfully downloaded");
                        //Save the current settings so next time user opens this it will be quicker
                        saveSettings();
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        };
        new Thread(task).start();
    }
}