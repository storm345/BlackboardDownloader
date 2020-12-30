package me.eddie.blackboardDownloader.blackboard;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseContentEntry {
    public static class Attachment {
        private String link;
        private String name;

        public Attachment(String link, String name) {
            this.link = link;
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public String getName() {
            return name;
        }
    }

    private String urlOfPageWhereDisplayed;
    private String entryName;
    private String desc = "";
    private String[] pathWithinCourse = {};
    private @Nullable String mainLink;
    private List<Attachment> attachments = new ArrayList<>();

    public CourseContentEntry(String[] pathWithinCourse, String urlOfPageWhereDisplayed, String entryName, String desc,
                              @Nullable String mainLink, List<Attachment> attachments) {
        this.pathWithinCourse = pathWithinCourse;
        this.urlOfPageWhereDisplayed = urlOfPageWhereDisplayed;
        this.entryName = entryName;
        this.desc = desc;
        this.mainLink = mainLink;
        this.attachments = attachments;
    }

    @Nullable
    public String getMainLink() {
        return mainLink;
    }

    public String getUrlOfPageWhereDisplayed() {
        return urlOfPageWhereDisplayed;
    }

    public String getDesc() {
        return desc;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public String getEntryName() {
        return entryName;
    }

    public String[] getPathWithinCourse() {
        return pathWithinCourse;
    }

    public List<String> getLinksToContentOrChildren(){
        List<String> res = new ArrayList<>();
        if(mainLink != null){
            res.add(mainLink);
        }
        res.addAll(attachments.stream().map(x -> x.getLink()).collect(Collectors.toList()));
        return res;
    }
}
