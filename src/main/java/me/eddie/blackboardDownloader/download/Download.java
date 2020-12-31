package me.eddie.blackboardDownloader.download;

import me.eddie.blackboardDownloader.blackboard.Course;

public class Download {
    private Course course;
    private Downloadable downloadable;

    public Download(Course course, Downloadable downloadable) {
        this.course = course;
        this.downloadable = downloadable;
    }

    public Course getCourse() {
        return course;
    }

    public Downloadable getDownloadable() {
        return downloadable;
    }
}
