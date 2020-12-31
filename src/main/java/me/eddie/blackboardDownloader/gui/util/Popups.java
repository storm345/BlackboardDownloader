package me.eddie.blackboardDownloader.gui.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.eddie.blackboardDownloader.main.GUIApp;

import javax.xml.ws.Holder;

public class Popups {
    public static interface PopupHandle {
        public void dismiss();
    }

    public static interface PopupContentFactory {
        public Scene createPopupScene(PopupHandle handle);
    }

    public static PopupHandle showPopup(String title, PopupContentFactory sceneFactory){
        return showPopup(title, sceneFactory, new PopupDisplayingListener() {
            @Override
            public void onDisplaying(PopupHandle handle, Stage stage) {

            }
        });
    }

    public static interface PopupDisplayingListener {
        public void onDisplaying(PopupHandle handle, Stage stage);
    }

    public static PopupHandle showPopup(String title, PopupContentFactory sceneFactory, PopupDisplayingListener onCreated){
        final Holder<Stage> stageRef = new Holder<>();
        final Object monitorObj = new Object();
        final Holder<Boolean> shouldShow = new Holder<Boolean>(true);
        PopupHandle handle = new PopupHandle() {
            @Override
            public void dismiss() {
                GUIApp.doInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (monitorObj){
                            shouldShow.value = false;
                            if(stageRef.value != null && stageRef.value.isShowing()){
                                stageRef.value.hide();
                            }
                        }
                    }
                });
            }
        };

        GUIApp.doInUIThread(new Runnable() {
            @Override
            public void run() {
                synchronized (monitorObj) {
                    if(shouldShow.value) {
                        Stage s = new Stage();
                        GUIApp.formatStageIcon(s);
                        s.setTitle(title);
                        s.setScene(sceneFactory.createPopupScene(handle));
                        if(!s.isShowing()) {
                            s.show();
                        }
                        stageRef.value = s;
                        onCreated.onDisplaying(handle, s);
                    }
                }
            }
        });
        return handle;
    }

    public static PopupHandle showSimplePopup(String title, String message){
        return showPopup(title, new PopupContentFactory() {
            @Override
            public Scene createPopupScene(PopupHandle handle) {
                Label label = new Label();
                label.setWrapText(true);
                label.setText(message);
                Button dismissButton = new Button("Ok");
                VBox vBox = new VBox(label, dismissButton);
                vBox.setAlignment(Pos.CENTER);
                vBox.setStyle(" -fx-padding: 10;");
                vBox.setMinWidth(200);
                dismissButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        handle.dismiss();
                    }
                });
                return new Scene(vBox);
            }
        });
    }
}
