package net.windyweather.screenshotarchive;

import javafx.stage.Window;

import java.util.prefs.Preferences;

import static java.lang.Boolean.FALSE;
import static net.windyweather.screenshotarchive.SSApplication.DEFAULT_X;
import static sun.util.locale.LocaleUtils.isEmpty;


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
    // Not part of the pair. Global setting not saved
    //public Boolean bTestLogOnly;                // during copy / delete operation provide log entries only.
                                                // do not modify files

    /*
    Names of preferences. All but NUMBER_PAIRS have index appended
     */
    private static final String NUMBER_PAIRS = "NUMBER_PAIRS";
    private static final String PAIR_NAME = "PAIR_NAME_%d";
    private static final String PAIR_SRC_PATH = "PAIR_SRC_PATH_%d";
    private static final String PAIR_DST_PATH = "PAIR_DST_PATH_%d";
    private static final String PAIR_FOLDER_SFX = "PAIR_FOLDER_SFX_%d";
    private static final String PAIR_FILE_PFX = "PAIR_FILE_PFX_%d";
    private static final String PAIR_SEARCH_SUB_FOLDERS = "PAIR_SEARCH_SUB_FOLDERS_%d";
    private static final String PAIR_PRESERVE_FILE_NAMES = "PAIR_PRESERVE_FILE_NAMES_%d";

    /*
        Qualified Preference Name Strings
     */

    private static String sQPairName; // = String.format(PAIR_NAME, idx);
    private static String sQPairSrcPath;// = String.format(PAIR_SRC_PATH, idx);
    private static String sQPairDstPath;// = String.format(PAIR_DST_PATH, idx);
    private static String sQPairFolderSfx; // = String.format(PAIR_FOLDER_SFX, idx);
    private static String sQPairFilePfx; // = String.format(PAIR_FILE_PFX, idx);
    private static String sQPairSearchSubFolders; // = String.format(PAIR_SEARCH_SUB_FOLDERS, idx);
    private static String sQPairPreserveFileNames; // = String.format(PAIR_PRESERVE_FILE_NAMES, idx);

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

    /*
        Get items from the preferences store
     */
    public Integer GetNumberPairs( ) {
        Preferences pref = Preferences.userRoot().node(SSApplication.NODE_NAME);
        return pref.getInt(NUMBER_PAIRS, 0);
    }

    /*
        Make the names to get or put the prefs for this pair
     */
    private void GetQualifiedPrefNames( int idx ) {
         sQPairName = String.format(PAIR_NAME, idx);
         sQPairSrcPath = String.format(PAIR_SRC_PATH, idx);
         sQPairDstPath = String.format(PAIR_DST_PATH, idx);
         sQPairFolderSfx = String.format(PAIR_FOLDER_SFX, idx);
         sQPairFilePfx = String.format(PAIR_FILE_PFX, idx);
         sQPairSearchSubFolders = String.format(PAIR_SEARCH_SUB_FOLDERS, idx);
         sQPairPreserveFileNames = String.format(PAIR_PRESERVE_FILE_NAMES, idx);
    }

    public boolean GetPairFromStore( int idx ) {

        GetQualifiedPrefNames( idx );

        Preferences pref = Preferences.userRoot().node(SSApplication.NODE_NAME);
        sPairName = pref.get(sQPairName, "");
        sSourcePath = pref.get(sQPairSrcPath, "");
        sDestinationPath = pref.get(sQPairDstPath, "");
        sFolderSuffix = pref.get(sQPairFolderSfx, "");
        sFilePrefix = pref.get(sQPairFilePfx, "");
        bSearchSubFolders = pref.getBoolean(sQPairSearchSubFolders, FALSE);
        bPreserveFileNames = pref.getBoolean(sQPairPreserveFileNames, FALSE);

        return !isEmpty(sPairName);
    }

    /*
        Store the pair only if the Pair Name is Not Empty
     */
    public boolean PutPairToStore( int idx ) {

        if ( sPairName.isEmpty() ) {
            return false;
        }
        GetQualifiedPrefNames( idx );

        Preferences pref = Preferences.userRoot().node(SSApplication.NODE_NAME);
        pref.put(sQPairName, sPairName);
        pref.get(sQPairSrcPath, sSourcePath);
        pref.get(sQPairDstPath, sDestinationPath);
        pref.get(sQPairFolderSfx, sFolderSuffix);
        pref.get(sQPairFilePfx, sFilePrefix);
        pref.getBoolean(sQPairSearchSubFolders, bSearchSubFolders);
        pref.getBoolean(sQPairPreserveFileNames, bPreserveFileNames);

        return true;

    }

    /*
        Remove this pair from the Pref Store
     */
    public void DeletePairFromStore( int idx ) {

        /*
            Don't remove an empty pair
         */
        if ( sPairName.isEmpty() ) {
            return;
        }
        GetQualifiedPrefNames( idx );

        /*
            remove the qualified nodes from the Pref Store
         */
        Preferences pref = Preferences.userRoot().node(SSApplication.NODE_NAME);
        pref.remove(sQPairName);
        pref.remove(sQPairSrcPath);
        pref.remove(sQPairDstPath);
        pref.remove(sQPairFolderSfx);
        pref.remove(sQPairFilePfx);
        pref.remove(sQPairSearchSubFolders);
        pref.remove(sQPairPreserveFileNames);

    }
}
