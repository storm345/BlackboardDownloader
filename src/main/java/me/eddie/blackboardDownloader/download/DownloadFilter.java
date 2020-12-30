package me.eddie.blackboardDownloader.download;

public interface DownloadFilter {
    public boolean shouldDownload(String fullyResolvedLink);
}
