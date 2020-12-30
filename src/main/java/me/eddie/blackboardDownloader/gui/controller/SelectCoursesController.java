package me.eddie.blackboardDownloader.gui.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import me.eddie.blackboardDownloader.blackboard.CourseAndContents;
import me.eddie.blackboardDownloader.main.BlackboardDownloaderApplication;
import me.eddie.blackboardDownloader.main.GUIApp;
import me.eddie.blackboardDownloader.main.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectCoursesController {
    @FXML
    VBox coursesList;

    @FXML
    Button nextButton;

    private List<CourseAndContents> courses = new ArrayList<>();
    private List<CourseAndContents> selectedCourses = new ArrayList<>();

    public synchronized void setCoursesList(List<CourseAndContents> courses){
        this.courses = courses;
        this.selectedCourses.clear();
        this.selectedCourses.addAll(this.courses);
        updateDisplayedCourses();
    }

    public synchronized void updateDisplayedCourses(){
        this.coursesList.getChildren().clear();
        for(CourseAndContents course:this.courses){
            CheckBox selector = new CheckBox();
            selector.setPadding(new Insets(2));
            selector.setSelected(true);
            selector.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(selector.isSelected()){
                        if(!selectedCourses.contains(course)){
                            selectedCourses.add(course);
                        }
                    }
                    else {
                        selectedCourses.remove(course);
                    }
                }
            });
            Label label = new Label(course.getCourse().getName()+" ("+course.getContents().size()+" items)");
            label.setWrapText(true);
            label.setTextAlignment(TextAlignment.JUSTIFY);
            label.setPadding(new Insets(2));
            HBox box = new HBox(selector, label);
            this.coursesList.getChildren().add(box);
        }
    }

    @FXML
    public void initialize(){
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GUIApp.stage.setScene(GUIApp.emptyScene);
                Main.executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        BlackboardDownloaderApplication.instance.download(selectedCourses);
                    }
                });
            }
        });
    }

    public static void show(Stage stage, List<CourseAndContents> courses) {
        GUIApp.doInUIThread(new Runnable() {
            @Override
            public void run() {
                FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("layout/selectCourses.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SelectCoursesController controller = loader.getController();
                controller.setCoursesList(courses);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                scene.getStylesheets().add(Main.class.getClassLoader().getResource("style/mainStyle.css").toExternalForm());
                if(!stage.isShowing()) {
                    stage.show();
                }
            }
        });
    }
}
