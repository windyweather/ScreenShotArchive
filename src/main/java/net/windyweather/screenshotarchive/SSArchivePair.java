package net.windyweather.screenshotarchive;

import javafx.stage.Window;

public class SSArchivePair {
    /* Holds the data for a Game Source and Archive Destination and
       various options that control how the screen shot images are
       handled as they are saved.
     */
    public String sPairName;
    public String sSourcePath;
    public String sDestinationPath;
    public String sFolderSuffix;
    public String sFilePrefix;
    public Boolean bSearchSubFolders;           // search subfolders of source to find images
    public Boolean bPreserveFileNames;          // do not modify the source images names during copy
    public Boolean bTestLogOnly;                // during copy / delete operation provide log entries only.
                                                // do not modify files.
    /*
    based on current contents of source path, request a new path with a folder dialog
     */
    public void AskForSourcePath( Window parent ) {

    }
    /*
    based on current contents of destination path, request a new path with a folder dialog
     */
    public void AskForDestinationPath( Window parent ) {

    }
}
