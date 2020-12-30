package me.eddie.blackboardDownloader.blackboard;

import java.util.ArrayList;
import java.util.List;

public class CourseAndContents {
    private Course course;
    private List<CourseContentEntry> contents = new ArrayList<>();

    public CourseAndContents(Course course, List<CourseContentEntry> contents) {
        this.course = course;
        this.contents = contents;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<CourseContentEntry> getContents() {
        return contents;
    }

    public void setContents(List<CourseContentEntry> contents) {
        this.contents = contents;
    }
}
