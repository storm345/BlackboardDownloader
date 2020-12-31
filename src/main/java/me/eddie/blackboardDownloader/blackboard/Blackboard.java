package me.eddie.blackboardDownloader.blackboard;

import me.eddie.blackboardDownloader.gui.util.Popups;
import me.eddie.blackboardDownloader.gui.util.ProgressBarPopup;
import me.eddie.blackboardDownloader.http.HttpClient;
import me.eddie.blackboardDownloader.util.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class Blackboard {
    private WebDriver webDriver;
    private String url;

    public Blackboard(WebDriver webDriver, String url) {
        this.webDriver = webDriver;
        this.url = url;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public String getBaseUrl() {
        return url;
    }

    public boolean isAskingForLogin(){
        return this.webDriver.getCurrentUrl().contains("SAML2");
    }

    public String getMyCoursesListURL(){
        return this.url + "/webapps/portal/execute/tabs/tabAction?tab_tab_group_id=_1_1";
    }

    public String toFullUrl(String bbURL){
        if(bbURL.trim().startsWith("/")){
            bbURL = getBaseUrl()+bbURL.trim();
        }
        return bbURL;
    }

    public BlackboardResource<byte[]> fetchFile(String resolvedURL){
        return new BlackboardResource<>(new RetryingBlackboardResourceFetcher<>(10, new RetryingBlackboardResourceFetcher.FailableResourceFetcher<byte[]>() {
            @Override
            public HttpClient.Response<byte[]> fetch(Blackboard blackboard) {
                return HttpClient.executeGetOfBytes(resolvedURL, false, getWebDriver().manage().getCookies());
            }
        }));
    }

    public BlackboardResource<String> getResolvedURL(String url){
        return new BlackboardResource<>(new RetryingBlackboardResourceFetcher<>(10, new RetryingBlackboardResourceFetcher.FailableResourceFetcher<String>() {
            @Override
            public HttpClient.Response<String> fetch(Blackboard blackboard) {
                return HttpClient.resolveRedirectDestination(url, getWebDriver().manage().getCookies());
            }
        }));
    }

    public BlackboardResource<ScannedBlackboardContent> getAllContent(ProgressBarPopup.ProgressBarHandle progressBarHandle){
        return new BlackboardResource<>(new BlackboardResource.BlackboardResourceFetcher<ScannedBlackboardContent>() {
            @Override
            public ScannedBlackboardContent fetch(Blackboard bb) {
                progressBarHandle.postUpdate("Finding courses...", "", 0);
                List<Course> courses = bb.getCourses().get(bb);

                List<CourseAndContents> coursesAndContents = new ArrayList<>();
                for(int i=0;i<courses.size();i++) {
                    if(progressBarHandle.isCancelled()){
                        return null;
                    }
                    Course course = courses.get(i);
                    progressBarHandle.postUpdate("Scanning "+course.getName()+" ("+(i+1)+"/"+courses.size()+")", "", (i+1)/((double)courses.size()));
                    List<CourseContentEntry> contentEntries = course.getCourseContent().get(bb);
                    CourseAndContents courseAndContents = new CourseAndContents(course, contentEntries);
                    coursesAndContents.add(courseAndContents);
                }
                progressBarHandle.postUpdate("Done!", "", 1);

                return new ScannedBlackboardContent(coursesAndContents);
            }
        });
    }

    public BlackboardResource<List<Course>> getCourses(){
        return new BlackboardResource<>(new BlackboardResource.BlackboardResourceFetcher<List<Course>>() {
            @Override
            public List<Course> fetch(Blackboard bb) {
                List<Course> res = new ArrayList<>();
                open(getMyCoursesListURL());

                List<WebElement> listElemLinks = DriverUtil.getElementsOnceExist(bb.webDriver, By.cssSelector(".courseListing > li a"));
                bb.acceptCookiesIfNeeded();
                for(WebElement we:listElemLinks){
                    String cName = we.getText();
                    String link = bb.toFullUrl(we.getAttribute("href"));
                    res.add(new Course(cName, link));
                }
                return res;
            }
        });
    }

    public void open(String bbURL){
        this.webDriver.get(bbURL);
        Popups.PopupHandle popup = null;
        while(isAskingForLogin()) {
            while (isAskingForLogin()) {
                if(popup == null) {
                    popup = Popups.showSimplePopup("Login needed!", "Please login!");
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.webDriver.get(bbURL);
        }
        if(popup != null){
            popup.dismiss();
        }
        acceptCookiesIfNeeded();
    }

    public void acceptCookiesIfNeeded(){
        while (DriverUtil.tryFindElement(webDriver, By.cssSelector("#dialog-consent-title")) != null){
            DriverUtil.tryFindElement(webDriver, By.cssSelector(".consent-footer .button-1")).click();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void open(){
        open(this.url);
    }
}
