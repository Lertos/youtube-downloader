package com.lertos.youtubedownloader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("y");

        //COMMAND TO USE (example):
        //yt-dlp https://youtu.be/lbLug8M_5HQ -P "C:\Users\Dylan\Downloads" -f "ba"


        //idk();
    }

    public static void idk() {
        try {
            //Runtime.getRuntime().exec("winget install yt-dlp --accept-package-agreements");
            //Runtime.getRuntime().exec("cmd.exe /c yt-dlp https://youtu.be/lbLug8M_5HQ -P \"C:\\Users\\Dylan\\Downloads\" -f \"ba\"");
            //Runtime.getRuntime().exec("cmd.exe /c mkdir \"C:\\Users\\Dylan\\Downloads\\test\"");


            /*
            try {
                File myObj = new File("filename.txt");
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            */
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c","admin.lnk");
            Process p = pb.start();

            //Runtime.getRuntime().exec("yt-dlp https://youtu.be/lbLug8M_5HQ -P C:\\Users\\Dylan\\Downloads -f \"ba\"");


            System.out.println("o");
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}