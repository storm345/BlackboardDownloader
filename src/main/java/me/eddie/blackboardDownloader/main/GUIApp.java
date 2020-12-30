package me.eddie.blackboardDownloader.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.eddie.blackboardDownloader.gui.controller.StartMenuController;

public class GUIApp extends Application {
    public static Stage stage;
    public static Scene emptyScene;

    public static void doLaunch(){
        launch();
    }

    public static void doInUIThread(Runnable run){
        Platform.runLater(run);
    }

    public static void formatStageIcon(Stage stage){
        stage.getIcons().add(new Image(Main.class.getClassLoader().getResourceAsStream("img/icon.png")));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Starting!");
        GUIApp.stage = primaryStage;
        formatStageIcon(primaryStage);
        primaryStage.setTitle("Blackboard Downloader");
        emptyScene = new Scene(new AnchorPane(), 400,500);

        StartMenuController.show(primaryStage);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Main.terminate();
            }
        });
    }
}
