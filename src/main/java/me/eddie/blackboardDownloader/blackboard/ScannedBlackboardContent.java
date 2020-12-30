package me.eddie.blackboardDownloader.blackboard;

import java.util.ArrayList;
import java.util.List;

public class ScannedBlackboardContent {
    private List<CourseAndContents> foundContent = new ArrayList<>();

    public ScannedBlackboardContent(List<CourseAndContents> foundContent) {
        this.foundContent = foundContent;
    }

    public List<CourseAndContents> getFoundContent() {
        return foundContent;
    }

    public void setFoundContent(List<CourseAndContents> foundContent) {
        this.foundContent = foundContent;
    }
}
