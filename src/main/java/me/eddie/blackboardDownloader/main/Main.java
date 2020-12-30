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

    public static void terminate(){
        executorService.shutdown();
        if(BlackboardDownloaderApplication.instance.blackboard != null){
            BlackboardDownloaderApplication.instance.blackboard.getWebDriver().close();
        }
        System.exit(0);
    }

    public static void main(String[] args){
        System.out.println("Application start");
        executorService = Executors.newCachedThreadPool();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                terminate();
            }
        });

        new BlackboardDownloaderApplication(); //Init singleton

        System.out.println("Launching gui...");
        GUIApp.doLaunch();

        //Downloading stuff...
        /*Set<Cookie> cookies = bb.getWebDriver().manage().getCookies();
        DownloadFilter df = new DefaultDownloadFilter();

        for(CourseAndContents courseAndContents : blackboardContent.getFoundContent()){
            for(CourseContentEntry cce:courseAndContents.getContents()){
                for(String link:cce.getLinksToContentOrChildren()) {
                    if (link != null && link.contains("listContent")) {
                        continue; //It's a folder...
                    }
                    //System.out.println("Resolving: " + link + " (" + cce.getEntryName() + ")");
                    if (link == null) {
                        continue;
                    }
                    HttpClient.Response<String> resolvedLink = HttpClient.resolveRedirectDestination(link, cookies);
                    if(!df.shouldDownload(resolvedLink.getResponse())) {
                        *//*System.out.println("SKIPPED:");
                        System.out.println("Staus: " + resolvedLink.getHttpCode());
                        System.out.println("Full link: " + resolvedLink.getResponse());*//*
                    }
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
    }
}
