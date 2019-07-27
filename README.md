![Imgur](https://i.imgur.com/w4N0yhv.png)

[![Build Status](https://travis-ci.org/Anuken/Mindustry.svg?branch=master)](https://travis-ci.org/Anuken/Mindustry)

A pixelated sandbox tower defense game made using [LibGDX](https://libgdx.badlogicgames.com/). Winner of the [GDL Metal Monstrosity Jam](https://itch.io/jam/gdl---metal-monstrosity-jam).

_[Wiki](http://mindustry.wikia.com/wiki/Mindustry_Wiki)_  

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
