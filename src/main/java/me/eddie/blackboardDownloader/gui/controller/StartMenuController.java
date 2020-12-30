package me.eddie.blackboardDownloader.gui.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.eddie.blackboardDownloader.main.BlackboardDownloaderApplication;
import me.eddie.blackboardDownloader.main.GUIApp;
import me.eddie.blackboardDownloader.main.Main;

import java.io.File;
import java.io.IOException;

public class StartMenuController {
    @FXML
    protected Button nextButton;
    @FXML
    protected TextField blackboardURL;
    @FXML
    protected TextField chromeDriverLocation;
    @FXML
    protected CheckBox rescanBlackboard;
    @FXML
    protected CheckBox replaceExistingFiles;
    @FXML
    protected TextField outputLocation;
    @FXML
    protected Button pickChromeDriverLocation;
    @FXML
    protected Button pickOutputLocation;

    @FXML
    public void initialize(){
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GUIApp.stage.setScene(GUIApp.emptyScene);
                Main.executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        BlackboardDownloaderApplication.instance.start(
                                blackboardURL.getText(),
                                chromeDriverLocation.getText(),
                                rescanBlackboard.isSelected(),
                                outputLocation.getText(),
                                replaceExistingFiles.isSelected()
                        );
                    }
                });
            }
        });
        pickOutputLocation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                File folderSelected = new File(outputLocation.getText());
                if(!folderSelected.exists()){
                    folderSelected = new File("output");
                }
                chooser.setInitialDirectory(folderSelected);
                chooser.setTitle("Pick output location");
                File f = chooser.showDialog(GUIApp.stage);
                if(f != null) {
                    outputLocation.setText(f.getAbsolutePath());
                }
            }
        });
        pickChromeDriverLocation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                File file = new File(chromeDriverLocation.getText());
                if(!file.exists()){
                    file = new File("ChromeDriver.exe");
                }
                chooser.setInitialDirectory(file.getParentFile());
                chooser.setInitialFileName(file.getName());
                chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Executable", "exe"));
                file = chooser.showOpenDialog(GUIApp.stage);
                if(file != null) {
                    chromeDriverLocation.setText(file.getPath());
                }
            }
        });
    }

    public static void show(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getClassLoader().getResource("layout/mainLayout.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Main.class.getClassLoader().getResource("style/mainStyle.css").toExternalForm());
        if(!primaryStage.isShowing()) {
            primaryStage.show();
        }
    }
}
