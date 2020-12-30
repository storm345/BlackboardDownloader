package me.eddie.blackboardDownloader.util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.util.List;

public class DriverUtil {
    public static @Nullable WebElement tryFindElement(SearchContext webDriver, By by){
        try {
            return webDriver.findElement(by);
        } catch (NoSuchElementException exception) {
            return null;
        }
    }

    public static @Nullable List<WebElement> tryFindElements(SearchContext webDriver, By by){
        try {
            return webDriver.findElements(by);
        } catch (NoSuchElementException exception) {
            return null;
        }
    }

    public static List<WebElement> getElementsOnceExist(SearchContext webDriver, By by){
        waitForElementToExist(webDriver, by);
        return webDriver.findElements(by);
    }

    public static WebElement getElementOnceExist(SearchContext webDriver, By by){
        waitForElementToExist(webDriver, by);
        return webDriver.findElement(by);
    }

    public static void waitForElementToExist(SearchContext webDriver, By by){
        while (webDriver.findElements(by).size() < 1){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
