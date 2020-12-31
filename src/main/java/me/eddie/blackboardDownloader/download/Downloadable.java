package me.eddie.blackboardDownloader.download;

import me.eddie.blackboardDownloader.util.FileUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Downloadable {
    private String resourceName;
    private String resolvedURL;
    private final Pattern fileExtPattern = Pattern.compile(".+\\.([^\\.\\?\\/]+).?");

    public Downloadable(String resourceName, String resolvedURL) {
        this.resourceName = resourceName;
        this.resolvedURL = resolvedURL;
    }

    public String getFileName(){
        String fName = getResourceName();
        Matcher m1 = fileExtPattern.matcher(fName);
        if(!m1.matches()){
            Matcher m = fileExtPattern.matcher(getResolvedURL());
            if(m.matches()){
                String ext = m.group(1);
                fName += "." + ext;
            }
        }
        while(fName.endsWith(".")){
            fName = fName.substring(0, fName.length()-1);
        }
        return FileUtil.sanitizeFileName(fName);
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getResolvedURL() {
        return resolvedURL;
    }
}
