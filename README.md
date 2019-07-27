![Imgur](https://i.imgur.com/w4N0yhv.png)

An old, classic version of the pixelated sandbox tower defense game known as Mindustry.

### Important Notice

This repository is **no longer being maintained.** No features will be implemented, no bugs will be fixed and no additional releases will be made unless absolutely necessary.
If you find an issue and know how to fix it, feel free to submit a pull request. **Issues will be ignored.**

### Building

First, make sure you have Java 8 and JDK 8 installed. Open a terminal in the root directory, and run the following commands:


**_Windows_**

_Running:_ `gradlew.bat desktop:run`  
_Building:_ `gradlew.bat desktop:dist`


**_Linux_**

_Running:_ `./gradlew desktop:run`  
_Building:_ `./gradlew desktop:dist`

Gradle may take up to several minutes to download files. Be patient. <br>
After building, the output .JAR file should be in the output JAR file should be in `/desktop/build/libs/desktop-release.jar.`
