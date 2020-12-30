package me.eddie.blackboardDownloader.download;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultDownloadFilter implements DownloadFilter {
    private final Pattern fileExtPattern = Pattern.compile(".+\\.([^\\.\\?\\/]+).?");

    private final String[] exlucdedExts = new String[]{"jsp", "html", "php", "asp", "aspx"};
    private final List<String> exlucdedExtsList = Arrays.asList(exlucdedExts);

    private List<String> knownExts = new ArrayList<>();

    protected boolean shouldIncludeFileByExt(String ext, String link){
        if(exlucdedExtsList.contains(ext)){
            return false;
        }

        if(!knownExts.contains(ext)) {
            System.out.println("Discovered allowed ext: "+ext+" ("+link+")");
            knownExts.add(ext);
        }
        return true;
    }

    @Override
    public boolean shouldDownload(String fullyResolvedLink) {
        if(fullyResolvedLink.toLowerCase().contains("cloud.panopto.eu")
            || fullyResolvedLink.toLowerCase().contains("href=/webapps/")
            || fullyResolvedLink.toLowerCase().contains("youtube.com")
            || fullyResolvedLink.toLowerCase().contains("calendly.com")
            || fullyResolvedLink.toLowerCase().contains("imperial.ac.uk/people/")){
            return false;
        }
        Matcher m = fileExtPattern.matcher(fullyResolvedLink);
        if(!m.matches()){ //Has no file extension...
            return false;
        }
        String ext = m.group(1);

        return shouldIncludeFileByExt(ext, fullyResolvedLink);
    }
}
