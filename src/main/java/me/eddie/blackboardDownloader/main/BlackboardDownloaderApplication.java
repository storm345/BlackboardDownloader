package me.eddie.blackboardDownloader.main;

import me.eddie.blackboardDownloader.blackboard.*;
import me.eddie.blackboardDownloader.download.*;
import me.eddie.blackboardDownloader.gui.controller.SelectCoursesController;
import me.eddie.blackboardDownloader.gui.controller.StartMenuController;
import me.eddie.blackboardDownloader.gui.util.Popups;
import me.eddie.blackboardDownloader.gui.util.ProgressBarPopup;
import me.eddie.blackboardDownloader.http.GsonUtil;
import me.eddie.blackboardDownloader.util.FileUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BlackboardDownloaderApplication {
    public static BlackboardDownloaderApplication instance;

    private static final File SCANNED_CONTENTS_CACHE = new File("cache"+File.separator+"scannedContents.json");
    private static final File SKIPPED_FILES_LIST = new File("skipped.txt");
    private static final File FAILED_FILES_LIST = new File("failed.txt");

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
            bar.postUpdate("Saving...", "", 1);
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
        try {
            this.outputLocation = new File(outputLocation);
            if(!this.outputLocation.mkdirs()){
                throw new Exception("Failed to make output directory!");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Popups.showSimplePopup("Save location error!", e.getMessage());
            GUIApp.doInUIThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        StartMenuController.show(GUIApp.stage);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });
            return;
        }
        this.replaceExistingFiles = replaceExistingFiles;
        System.setProperty("webdriver.chrome.driver", webDriverLocation);
        WebDriver webDriver;
        try {
            webDriver = new ChromeDriver();
        } catch(Exception e){
            e.printStackTrace();
            Popups.showSimplePopup("Chrome driver error!", e.getMessage());
            GUIApp.doInUIThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        StartMenuController.show(GUIApp.stage);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });
            return;
        }
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

    private void saveSkippedAndFailed(SuccessSkippedFailedList<Download> list) throws IOException{
        if(!SKIPPED_FILES_LIST.exists()){
            SKIPPED_FILES_LIST.createNewFile();
        }
        if(!FAILED_FILES_LIST.exists()){
            FAILED_FILES_LIST.createNewFile();
        }
        Files.write(SKIPPED_FILES_LIST.toPath(), stringifyDownloads(list.getSkippedList()));
        Files.write(FAILED_FILES_LIST.toPath(), stringifyDownloads(list.getFailedList()));
    }

    private List<String> stringifyDownloads(List<Download> downloadables){
        List<String> lines = new ArrayList<>();
        for(Download dl:downloadables){
            lines.add(dl.getCourse().getName()+"- "+dl.getDownloadable().getResourceName()+": "+dl.getDownloadable().getResolvedURL());
        }
        return lines;
    }

    public synchronized void download(List<CourseAndContents> courses){
        try {
            System.out.println("Downloading " + courses.size() + " courses...");
            SuccessSkippedFailedList<Download> tracker = new SuccessSkippedFailedList<>();
            DownloadFilter df = new DefaultDownloadFilter();

            ProgressBarPopup.ProgressBarHandle progressBar = ProgressBarPopup.showSimpleBar("Downloading...", "Starting...");

            int i = 0;
            for (CourseAndContents courseAndContents : courses) {
                i++;
                Course c = courseAndContents.getCourse();
                File courseFolder = new File(outputLocation + File.separator + FileUtil.sanitizeFileName(c.getName()));
                courseFolder.mkdirs();
                double baseProgress = i / ((double) courses.size());
                double singleCourseProgressAmount = 1 / ((double) courses.size());
                String msg = "Downloading " + c.getName() + " (" + i + "/" + courses.size() + ")";
                progressBar.postUpdate(msg, "", "", baseProgress);
                int k = 0;
                int numEntries = courseAndContents.getContents().size();
                for (CourseContentEntry entry : courseAndContents.getContents()) {
                    k++;
                    int skippedAndFailed = tracker.getNumSkippedAndFailed();
                    double progressPerEntry = singleCourseProgressAmount / ((double) numEntries);
                    double progress = baseProgress + progressPerEntry * k;
                    progressBar.postUpdate(msg, entry.getEntryName() + " (" + k + "/" + numEntries + ")", tracker.skippedFailedSummary(), progress);
                    SuccessSkippedFailedList<Downloadable> downloadables = entry.getDownloadables(df).get(blackboard);
                    tracker.addSkipped(downloadables.getSkippedList().stream().map(x -> new Download(c, x)).collect(Collectors.toList()));
                    tracker.addFailed(downloadables.getFailedList().stream().map(x -> new Download(c, x)).collect(Collectors.toList()));

                    List<Download> toDownload = downloadables.getSuccessList().stream().map(x -> new Download(c, x)).collect(Collectors.toList());
                    if (toDownload.size() < 1) {
                        continue;
                    }
                    boolean makeFolder = toDownload.size() > 1;
                    String folderPath = courseFolder.getPath();
                    for (String s : entry.getPathWithinCourse()) {
                        folderPath += File.separator + FileUtil.sanitizeFileName(s);
                    }
                    File downloadFolder = new File(folderPath +
                            (makeFolder ? (File.separator + FileUtil.sanitizeFileName(entry.getEntryName())) : ""));
                    downloadFolder.mkdirs();
                    int z = -1;
                    double progressPerDownload = progressPerEntry / ((double) toDownload.size());
                    for (Download download : toDownload) {
                        z++;
                        progressBar.postUpdate(msg, download.getDownloadable().getFileName() + " (" + k + "/" + numEntries + ")", tracker.skippedFailedSummary(), progress + progressPerDownload * z);
                        File outFile = new File(downloadFolder + File.separator + download.getDownloadable().getFileName());
                        if (outFile.exists() && !replaceExistingFiles) {
                            tracker.addSkipped(download);
                            continue;
                        }
                        //Download file
                        System.out.println("Fetching " + download.getDownloadable().getResolvedURL());
                        byte[] contents = blackboard.fetchFile(download.getDownloadable().getResolvedURL()).get(blackboard);
                        System.out.println("Fetched, saving to " + outFile.getPath() + "...");
                        if (contents == null) {
                            tracker.addFailed(download);
                        } else {
                            try {
                                if (!outFile.exists()) {
                                    outFile.getParentFile().mkdirs();
                                    outFile.createNewFile();
                                }
                                Files.write(outFile.toPath(), contents);
                                tracker.addSuccess(download);
                            } catch (Exception e) {
                                e.printStackTrace();
                                tracker.addFailed(download);
                            }
                        }
                    }

                    if (skippedAndFailed != tracker.getNumSkippedAndFailed()) {
                        //Need to re-save
                        try {
                            saveSkippedAndFailed(tracker);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            progressBar.postUpdate("Done!", "", tracker.skippedFailedSummary(), 1);
            Popups.showSimplePopup("Download finished!", "Check failed.txt and skipped.txt for summaries of skipped and failed entries");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
