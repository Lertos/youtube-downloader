module com.lertos.youtubedownloader {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.lertos.youtubedownloader to javafx.fxml;
    exports com.lertos.youtubedownloader;
}