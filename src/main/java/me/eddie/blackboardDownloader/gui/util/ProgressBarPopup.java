package me.eddie.blackboardDownloader.gui.util;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.eddie.blackboardDownloader.gui.controller.ProgressBarController;
import me.eddie.blackboardDownloader.main.GUIApp;
import me.eddie.blackboardDownloader.main.Main;

import javax.xml.ws.Holder;
import java.io.IOException;

public class ProgressBarPopup {
    public static interface ProgressBarHandle {
        public void postUpdate(String status, double progress); //Progress between 0 and 1
        public boolean isCancelled();
        public void dismiss();
    }

    public static interface CancelListener {
        public void onCancel(ProgressBarHandle handle);
        public static CancelListener getDefault(){
            return new CancelListener() {
                @Override
                public void onCancel(ProgressBarHandle handle) {
                    Main.terminate();
                }
            };
        }
    }

    private static class ProgressBarDisplayed {
        private ProgressBarController controller;
        private Popups.PopupHandle popupHandle;

        public ProgressBarDisplayed(ProgressBarController controller, Popups.PopupHandle popupHandle) {
            this.controller = controller;
            this.popupHandle = popupHandle;
        }

        public ProgressBarController getController() {
            return controller;
        }

        public Popups.PopupHandle getPopupHandle() {
            return popupHandle;
        }
    }

    private static class ProgressBarStatus {
        private String status;
        private double progress;
        private boolean cancelled = false;

        public ProgressBarStatus(String status, double progress, boolean cancelled) {
            this.cancelled = cancelled;
            this.status = status;
            this.progress = progress;
        }

        public ProgressBarStatus(String status, double progress) {
            this.status = status;
            this.progress = progress;
        }
    }

    private static void applyStatus(ProgressBarController pbc, ProgressBarStatus status){
        pbc.setOperationStatus(status.status);
        pbc.setProgress(status.progress);
    }

    public static ProgressBarHandle showSimpleBar(String title, String message){
        return showSimpleBar(title, message, CancelListener.getDefault());
    }

    public static ProgressBarHandle showSimpleBar(String title, String message, CancelListener cancelListener){
        Holder<ProgressBarDisplayed> progressBarControllerHolder = new Holder<>();
        Holder<ProgressBarStatus> statusHolder = new Holder<>(new ProgressBarStatus("", 0));
        final Object monitor = new Object();

        ProgressBarHandle handle = new ProgressBarHandle() {
            @Override
            public void postUpdate(String status, double progress) {
                System.out.println(status);
                GUIApp.doInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (monitor){
                            statusHolder.value = new ProgressBarStatus(status, progress);
                            if(progressBarControllerHolder.value != null){
                                applyStatus(progressBarControllerHolder.value.controller,statusHolder.value);
                            }
                        }
                    }
                });
            }

            @Override
            public boolean isCancelled() {
                synchronized (monitor) {
                    return statusHolder.value.cancelled;
                }
            }

            @Override
            public void dismiss() {
                GUIApp.doInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if(progressBarControllerHolder.value != null){
                            progressBarControllerHolder.value.popupHandle.dismiss();
                        }
                    }
                });
            }
        };
        Popups.showPopup(title, new Popups.PopupContentFactory() {
            @Override
            public Scene createPopupScene(Popups.PopupHandle handle) {
                FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("layout/simpleProgressBar.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                    ProgressBarController pbc = loader.getController();
                    progressBarControllerHolder.value = new ProgressBarDisplayed(pbc, handle);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Scene(root);
            }
        }, new Popups.PopupDisplayingListener() {
            @Override
            public void onDisplaying(Popups.PopupHandle popupHandle, Stage stage) {
                synchronized (monitor){
                    applyStatus(progressBarControllerHolder.value.controller,statusHolder.value);
                    progressBarControllerHolder.value.controller.setOperationDesc(message);
                }
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        synchronized (monitor) {
                            statusHolder.value.cancelled = true;
                        }
                        cancelListener.onCancel(handle);
                    }
                });
            }
        });
        return handle;
    }
}
