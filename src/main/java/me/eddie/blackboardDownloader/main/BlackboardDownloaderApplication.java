package me.eddie.blackboardDownloader.main;

import me.eddie.blackboardDownloader.blackboard.Blackboard;
import me.eddie.blackboardDownloader.blackboard.CourseAndContents;
import me.eddie.blackboardDownloader.blackboard.ScannedBlackboardContent;
import me.eddie.blackboardDownloader.gui.controller.SelectCoursesController;
import me.eddie.blackboardDownloader.gui.util.ProgressBarPopup;
import me.eddie.blackboardDownloader.http.GsonUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


public class BlackboardDownloaderApplication {
    public static BlackboardDownloaderApplication instance;

    private static final File SCANNED_CONTENTS_CACHE = new File("cache"+File.separator+"scannedContents.json");

    public Blackboard blackboard = null;
    private ScannedBlackboardContent scannedBlackboardContent = null;
    private File outputLocation = new File("output");
    private boolean replaceExistingFiles = false;

    public BlackboardDownloaderApplication(){
        instance = this;
    }

    protected void loadScannedContentsFromCache(){
        if(SCANNED_CONTENTS_CACHE.exists()){
            try {
                String json = new String(Files.readAllBytes(SCANNED_CONTENTS_CACHE.toPath()));
                this.scannedBlackboardContent = GsonUtil.getGson().fromJson(json, ScannedBlackboardContent.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void scanBlackboardIfNeeded(){
        if(this.scannedBlackboardContent != null){
            return;
        }
        System.out.println("Creating progress bar...");
        //Scan blackboard with progress bar
        ProgressBarPopup.ProgressBarHandle bar = ProgressBarPopup.showSimpleBar("Scanning Blackboard", "Scanning...");
        System.out.println("Scanning blackboard...");
        this.scannedBlackboardContent =
                this.blackboard.getAllContent(bar)
                        .get(this.blackboard);

        if(this.scannedBlackboardContent != null) {
            bar.postUpdate("Saving...", 1);
            String json = GsonUtil.getGson().toJson(this.scannedBlackboardContent);
            try {
                if (!SCANNED_CONTENTS_CACHE.exists()) {
                    SCANNED_CONTENTS_CACHE.getParentFile().mkdirs();
                    SCANNED_CONTENTS_CACHE.createNewFile();
                }
                Files.write(SCANNED_CONTENTS_CACHE.toPath(), json.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bar.dismiss();
    }

    public synchronized void start(String blackboardURL, String webDriverLocation, boolean rescanBlackboard, String outputLocation,
                                   boolean replaceExistingFiles){
        this.outputLocation = new File(outputLocation);
        this.replaceExistingFiles = replaceExistingFiles;
        System.setProperty("webdriver.chrome.driver", webDriverLocation);
        WebDriver webDriver = new ChromeDriver();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                webDriver.close();
            }
        });
        this.blackboard = new Blackboard(webDriver, blackboardURL);
        //blackboard.open();

        if(!rescanBlackboard){
            loadScannedContentsFromCache();
        }
        else {
            this.scannedBlackboardContent = null;
        }

        scanBlackboardIfNeeded();

        SelectCoursesController.show(GUIApp.stage, this.scannedBlackboardContent.getFoundContent());
    }

    public synchronized void download(List<CourseAndContents> courses){
        System.out.println("Downloading "+courses.size()+" courses...");
    }
}
