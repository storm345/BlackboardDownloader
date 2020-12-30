package me.eddie.blackboardDownloader.blackboard;

import me.eddie.blackboardDownloader.util.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Course {
    private String name;
    private String url;

    public Course(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public BlackboardResource<List<CourseContentEntry>> getCourseContent(){
        return new BlackboardResource<>(new BlackboardResource.BlackboardResourceFetcher<List<CourseContentEntry>>() {
            @Override
            public List<CourseContentEntry> fetch(Blackboard bb) {
                List<CourseContentEntry> res = new ArrayList<>();
                bb.open(Course.this.url);

                List<WebElement> navBarItems = DriverUtil.getElementsOnceExist(bb.getWebDriver(), By.cssSelector("#courseMenuPalette_contents > li"));
                String courseContentUrl = null; //DriverUtil.tryFindElement(navMenu, By.partialLinkText("Course Content"));
                for(WebElement navBarItem:navBarItems){
                    if(navBarItem.getAttribute("innerHTML").toLowerCase().contains("course content")){
                        //Found course content button
                        courseContentUrl = bb.toFullUrl(navBarItem.findElement(By.cssSelector("a")).getAttribute("href"));
                    }
                }
                if(courseContentUrl == null){
                    System.out.println("Not found any course content link");
                    //Course has no content
                    return res;
                }
                res.addAll(getChildContents(new String[0], bb, courseContentUrl));
                return res;
            }
        });
    }

    private List<CourseContentEntry> getChildContents(String[] pathWithinCourse, Blackboard bb, String courseContentUrl){
        List<CourseContentEntry> res = new ArrayList<>();
        bb.open(courseContentUrl);
        DriverUtil.waitForElementToExist(bb.getWebDriver(), By.cssSelector(".contentBox"));
        List<WebElement> listElems = DriverUtil.tryFindElements(bb.getWebDriver(), By.cssSelector(".contentList > li"));
        for(WebElement listElem:listElems){
            WebElement titleAndLinkDiv = listElem.findElement(By.cssSelector(".item"));
            String name = getNameFromText(titleAndLinkDiv.getText());
            WebElement mainLinkElem = DriverUtil.tryFindElement(titleAndLinkDiv, By.cssSelector("a"));
            String mainLink = null;
            if(mainLinkElem != null){
                String s = mainLinkElem.getAttribute("href");
                mainLink = bb.toFullUrl(s);
                if(s.toLowerCase().contains("#")){
                    mainLink = null;
                }
            }

            String desc = "";
            List<CourseContentEntry.Attachment> attachments = new ArrayList<>();
            WebElement detailsElem = DriverUtil.tryFindElement(listElem, By.cssSelector(".details"));
            if(detailsElem != null){
                desc = detailsElem.getText().trim(); //.getAttribute("innerHTML"); //HTML formatted description because why not
                List<WebElement> attachmentsListElems = DriverUtil.tryFindElements(detailsElem, By.cssSelector("li"));
                for(WebElement attachmentListElem:attachmentsListElems){
                    List<WebElement> links = DriverUtil.tryFindElements(attachmentListElem, By.cssSelector("a"));
                    for(WebElement link:links){
                        String attachmentName = getNameFromText(link.getText());
                        String href = link.getAttribute("href");
                        if(href.toLowerCase().contains("#")){
                            continue;
                        }
                        String attachmentLink = bb.toFullUrl(href);
                        attachments.add(new CourseContentEntry.Attachment(attachmentLink, attachmentName));
                    }
                }
            }

            res.add(new CourseContentEntry(pathWithinCourse, courseContentUrl, name, desc, mainLink, attachments));
        }
        for(CourseContentEntry cce:new ArrayList<>(res)){
            res.addAll(getChildContents(bb, cce));
        }
        return res;
    }

    private String getNameFromText(String text){
        return text.replaceAll(Pattern.quote("&nbsp;"), "").trim();
    }

    private List<CourseContentEntry> getChildContents(Blackboard bb, CourseContentEntry parent){
        List<CourseContentEntry> res = new ArrayList<>();
        String[] childPath = Arrays.copyOf(parent.getPathWithinCourse(), parent.getPathWithinCourse().length+1);
        childPath[childPath.length-1] = parent.getEntryName();
        for(String link: parent.getLinksToContentOrChildren()){
            if(link.startsWith(bb.getBaseUrl()) && link.contains("listContent")){
                res.addAll(getChildContents(childPath, bb, link));
            }
        }
        return res;
    }
}
