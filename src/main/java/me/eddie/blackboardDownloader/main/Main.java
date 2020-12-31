package me.eddie.blackboardDownloader.main;

import me.eddie.blackboardDownloader.blackboard.Blackboard;
import me.eddie.blackboardDownloader.blackboard.ScannedBlackboardContent;
import me.eddie.blackboardDownloader.http.GsonUtil;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final String DEFAULT_CHROME_DRIVER_LOC = "lib/chromedriver.exe";
    private static final File SCANNED_CONTENTS_CACHE = new File("cache"+File.separator+"scannedContents.json");

    public static ExecutorService executorService;

    private static volatile boolean terminating = false;
    private final static Object termMon = new Object();
    public static void terminate(){
        synchronized (termMon){
            if(terminating){
                return;
            }
            terminating = true;
        }
        System.out.println("Terminating...");

        try {
            executorService.shutdown();
            if (BlackboardDownloaderApplication.instance.blackboard != null) {
                BlackboardDownloaderApplication.instance.blackboard.getWebDriver().close();
            }
        }
        finally {
            System.exit(0);
        }
    }

    public static void main(String[] args){
        System.out.println("Application start");
        executorService = Executors.newCachedThreadPool();
        new BlackboardDownloaderApplication(); //Init singleton
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                terminate();
            }
        });

        System.out.println("Launching gui...");
        GUIApp.doLaunch();
    }
}
