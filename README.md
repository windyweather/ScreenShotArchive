# ScreenShotArchive

This is a program to manage, view and backup screenshots from all the games you play.

The model is that each game stores screen shots in folders in various places, 
including in various subfolders of your C:\Users\Yournamehere\OneDrive\Documents\yada\yada
or in SomeSSD:\GameCompany\GameFolder\ScreenShots.

So with these game folders all over the place how to do you conveniently:

1 - View the files.

2 - Backup the files to a common place.

3 - Organize them so you can easily find them by date over the years that you play.

4 - Rename the files so you can find the files by game name. Renaming is optional.

## Where are Game Screen Shots and Where do you want them Stored?
SSArchive has a number of "Pairs". A Pair is a Source / Destination pair
is a Source Game folder where the game stores screen shots, and the Destination is
a folder where you want to back up your screen shots. 
I point all my "pairs" to the same destination folder:

###  SomeBackUpHardDrive:\MMOPictures\NodeName\...

SSArchive will organise the shots by date by adding yyyy_MM [for example] to the path
based on the date of the file. And it will change the name of the file to something 
like NAMEOFGAME_yyyy_MM_dd_hh_mm_ss.JPG so that there are no collisions and the shots
can be easily found by name of the game.

Once the shots are stored like this on your computer, you can use another program to synch
them to another machine or backup service of your choice.

## View the Files
SSArchive has a viewport for images with pan / zoom. It can slide through the images 
to find recent files to refer to as you are playing the game, or to review recent
files for any other reason. 

## Support for many games
SSArchive can have any number of pairs to gather shots from all the games you play and
back them up to one or several backup areas. Personally, I back them up to one common
area on each of my computers.

## History
ScreenShotArchive is based on a program that I wrote years ago in QtFramework.

#### https://www.qt.io/product/framework

Qt is a C++ based GUI framwork with an IDE. The problem of course is that there
is no simple way to build an installer for your program. You are tossed headlong into
building a Windows installer. And this may take longer than writing the program itself.

You can find the original QtScreenShotArchive program here:

### https://windyweather.net/2013/11/23/game-screenshot-archive-program/

Including a link to the SourceForge page where you can download the program for Windows:

### https://sourceforge.net/projects/qtscreenshotarchive/

The sources are there on SourceForge as well, but alas, 2013 was before I started 
using GitHub.

Back about 2000 I needed a simple program to display a series of
LibreOffice Impress slide shows and after building most of it using Qt, I gave up
and built a Java Swing version called ImpressShowRunner.

#### https://github.com/windyweather

You can find the ShowRunner app, and eventually this application on that GitHub.

Enjoy.
