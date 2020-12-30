package me.eddie.blackboardDownloader.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProgressBarController {
    @FXML
    private Label operationDesc;
    @FXML
    private Label operationStatus;
    @FXML
    private ProgressBar progressBar;

    public void setOperationDesc(String desc){
        this.operationDesc.setText(desc);
    }

    public void setOperationStatus(String status){
        this.operationStatus.setText(status);
    }

    public void setProgress(double value){
        this.progressBar.setProgress(value); //Between 0 and 1
    }

    @FXML
    public void initialize(){
        System.out.println("Progress bar controller initialized!");
    }
}
