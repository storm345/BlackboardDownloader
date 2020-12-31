package me.eddie.blackboardDownloader.blackboard;

import me.eddie.blackboardDownloader.download.DownloadFilter;
import me.eddie.blackboardDownloader.download.Downloadable;
import me.eddie.blackboardDownloader.download.SuccessSkippedFailedList;
import me.eddie.blackboardDownloader.gui.util.Popups;

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

    public List<Attachment> getAllContentOrChildrenAsAttachments(){
        List<Attachment> res = new ArrayList<>();
        if(mainLink != null){
            res.add(new Attachment(mainLink, getEntryName()));
        }
        res.addAll(attachments);
        return res;
    }

    public BlackboardResource<SuccessSkippedFailedList> getDownloadables(DownloadFilter filter){
        return new BlackboardResource<>(new BlackboardResource.BlackboardResourceFetcher<SuccessSkippedFailedList>() {
            @Override
            public SuccessSkippedFailedList fetch(Blackboard blackboard) {
                List<Downloadable> downloadables = new ArrayList<>();
                List<Downloadable> skipped = new ArrayList<>();
                List<Downloadable> failed = new ArrayList<>();
                for(Attachment a:getAllContentOrChildrenAsAttachments()){
                    String resolvedURL = blackboard.getResolvedURL(a.link).get(blackboard);
                    if(resolvedURL == null){
                        System.out.println("UNABLE TO RESOLVE URL: "+a.link+", did we lose connection?");
                        //Popups.showSimplePopup("Error!", "Unable to resolve url for "+a.link+", lost connection?");
                        failed.add(new Downloadable(a.name, a.link));
                    }
                    else if(filter.shouldDownload(resolvedURL)){
                        downloadables.add(new Downloadable(a.name, resolvedURL));
                    }
                    else {
                        if(!a.link.toLowerCase().contains("listcontent")) {
                            System.out.println("Skipping " + a.name + " as not matched by filter");
                            skipped.add(new Downloadable(a.name, resolvedURL));
                        }
                    }
                }
                return new SuccessSkippedFailedList(downloadables, skipped, failed);
            }
        });
    }
}
