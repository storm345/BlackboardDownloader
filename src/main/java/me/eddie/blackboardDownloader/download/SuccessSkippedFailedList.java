package me.eddie.blackboardDownloader.download;

import java.util.ArrayList;
import java.util.List;

public class SuccessSkippedFailedList<T> {
    private List<T> successList = new ArrayList<>();
    private List<T> skippedList = new ArrayList<>();
    private List<T> failedList = new ArrayList<>();

    public SuccessSkippedFailedList(){

    }

    public SuccessSkippedFailedList(List<T> successList, List<T> skippedList, List<T> failedList) {
        this.successList = successList;
        this.skippedList = skippedList;
        this.failedList = failedList;
    }

    public String skippedFailedSummary(){
        return getNumFailed()+" failed, "+getNumSkipped()+" skipped";
    }

    public int getNumFailed(){
        return this.failedList.size();
    }

    public int getNumSkipped(){
        return this.skippedList.size();
    }

    public int getNumSkippedAndFailed(){
        return getNumFailed() + getNumSkipped();
    }

    public void addFailed(List<T> failed){
        this.failedList.addAll(failed);
    }

    public void addFailed(T failed){
        this.failedList.add(failed);
    }

    public void addSkipped(List<T> skippedList){
        this.skippedList.addAll(skippedList);
    }

    public void addSkipped(T skipped){
        this.skippedList.add(skipped);
    }

    public void addSuccess(List<T> success){
        this.successList.addAll(success);
    }

    public void addSuccess(T success){
        this.successList.add(success);
    }

    public List<T> getSkippedList() {
        return skippedList;
    }

    public List<T> getSuccessList() {
        return successList;
    }

    public List<T> getFailedList() {
        return failedList;
    }
}
