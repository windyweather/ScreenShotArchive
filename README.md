# ScreenShotArchive

This is a program to manage, view and backup screenshots from all the games you play.

The model is that each game stores screen shots in folders in various places, 
including in various subfolders of your 

C:\Users\Yournamehere\OneDrive\Documents\yada\yada or in 

SomeSSD:\GameCompany\GameFolder\ScreenShots.

So with these game folders all over the place how to do you conveniently:

1 - View the image files.

2 - Backup the image files to a common place.

3 - Organize the images so you can easily find them by date over the years that you play.

4 - Rename the files so you can find the files by game name. Renaming is optional.

## Where are Game Screen Shots and Where do you want them Stored?
SSArchive allow building and saves a number of "Pairs".
A Pair is a Source / Destination pair. The Source is a Source Game folder 
where the game stores screen shots, and the Destination is
a folder where you want to back up your screenshots. 
Perhaps it is convenient to point all the  "pairs" to the
same destination folder so that all the games share a common back up area.

###  SomeBackUpHardDrive:\MMOPictures\NodeName\...

SSArchive will organise the shots by date by adding yyyy_MM or yyyy_MM_dd to the path
based on the modification date of the file. And it will change the name of the file to something like 

NAMEOFGAME_yyyy_MM_dd_hh_mm_ss.JPG

so that there are no collisions and the shots can be easily found by name of the game.

Once the shots are stored like this on your computer, you can use another
program to back them up to another machine or backup service of your choice.

## View the Files
SSArchive has a viewport for images with pan / zoom. It can page through the images 
to find recent files to refer to as you are playing the game, or to review recent
files for any other reason. Both the source - Game Folder - or a backup folder can be viewed.

## Support for many games
SSArchive can have any number of pairs to gather shots from all the games you play and
back them up to one or several backup areas. It might be convenient to back the
screen shots for all the games up to one common area on the computer.

## History
ScreenShotArchive is based on a program that I wrote in 2013 in the QtFramework.

#### https://www.qt.io/product/framework

Qt is a C++ based GUI framework with an IDE. The problem of course is that there
is no simple way to build an installer for a C++ program. Rather there are a number
of choices to build a Windows installer, some of them free. And choosing one,
learning to use it, and testing the installer, may take longer than writing the
program itself.

You can find the original QtScreenShotArchive program here:

### https://windyweather.net/2013/11/23/game-screenshot-archive-program/

And here is a link to the SourceForge page where you can download the Qt program
for Windows:

### https://sourceforge.net/projects/qtscreenshotarchive/

The sources are there on SourceForge as well, but alas, 2013 was before I started 
using GitHub.

Back about 2020 I needed a simple program to display a series of
LibreOffice Impress slide shows, automatically displaying the next one when 
one had finished. After building most of it using Qt, and facing the daunting 
prospect of building installers for both Windows and Linux, I gave up
and built a Java Swing version called ImpressShowRunner. Deploying a Java program
needs no installer, only Java installed on the target system.

Here are the Jar files on SourceForge

#### https://sourceforge.net/projects/impressshowrunner/files/

And the sources on GitHub

#### https://github.com/windyweather/ImpressShowRunnerV2

Look for this program soon on those places too.

Enjoy.
