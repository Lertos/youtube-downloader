package com.lertos.youtubedownloader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    static Stage stage;
    static Controller controller;

    @Override
    public void start(Stage stage) throws IOException {
        String xmlToLoad = "main.fxml";
        boolean createdRunFile = createRunBatchFile();

        if (createdRunFile) {
            xmlToLoad = "first-run.fxml";
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(xmlToLoad));
        Scene scene = new Scene(fxmlLoader.load());

        this.stage = stage;
        this.controller = fxmlLoader.getController();

        stage.setTitle("YouTube Song Downloader");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public boolean createRunBatchFile() {
        try {
            File file = new File("run.bat");

            if (file.createNewFile()) {
                FileWriter fw = new FileWriter(file);
                fw.write("@echo off\njavaw -jar %~dp0/youtube-downloader.jar");
                fw.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}