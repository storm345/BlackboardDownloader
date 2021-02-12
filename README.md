# BlackboardDownloader
![Alt Image](src/main/resources/img/icon.ico?raw=true "Image")  
Java program to download all lecture notes and other files from blackboard.

Utilises Selenium to scrape from the blackboard websites, requires ChromeDriver and Chrome to be installed to run.

Designed for and tested on bb.imperial.ac.uk, since it is inspecting the web content it may not work out of the box with other blackboard instances and is likely to require slight modification.

# Download:
https://github.com/storm345/BlackboardDownloader/tree/master/release  
Note that the executable is unsigned and may produce a warning when trying to download/run, if you do not trust it then you re-compile the project from source using gradle.
Requires:
- Java 8 or newer
- ChromeDriver https://chromedriver.chromium.org/downloads
