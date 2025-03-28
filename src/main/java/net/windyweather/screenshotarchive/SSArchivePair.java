package net.windyweather.screenshotarchive;

import javafx.stage.Window;

import java.util.prefs.Preferences;

import static net.windyweather.screenshotarchive.SSController.printSysOut;
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
        let's make one
     */
    public SSArchivePair(){

    }

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
        for Debugging. Just print out the pair we are looking at
     */

    public void PrintPair() {
        printSysOut( String.format("PairName:%s Src:%s Dst:%s", sPairName, sSourcePath, sDestinationPath));
    }
    /*
        Get / Put the number of pairs in the preferences store
     */
    public int GetNumberPairs( ) {
        Preferences pref = Preferences.userRoot().node(SSApplication.NODE_NAME);
        return pref.getInt(NUMBER_PAIRS, 0);
    }

    public void PutNumberPairs( int numPairs ){
        Preferences pref = Preferences.userRoot().node(SSApplication.NODE_NAME);
        pref.putInt( NUMBER_PAIRS, numPairs );
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
        sFolderSuffix = pref.get(sQPairFolderSfx, "yyyy_MM");
        sFilePrefix = pref.get(sQPairFilePfx, "");
        bSearchSubFolders = pref.getBoolean(sQPairSearchSubFolders, false);
        bPreserveFileNames = pref.getBoolean(sQPairPreserveFileNames, false);

        printSysOut("GetPairFromStore:");
        PrintPair();

        return !sPairName.isBlank(); //!isEmpty(sPairName);
    }

    /*
        Store the pair only if the Pair Name is Not Empty
     */
    public boolean PutPairToStore( int idx ) {

        printSysOut("PutPairToStore:");
        PrintPair();

        if ( sPairName.isEmpty() ) {
            return false;
        }
        GetQualifiedPrefNames( idx );

        Preferences pref = Preferences.userRoot().node(SSApplication.NODE_NAME);
        pref.put(sQPairName, sPairName);
        pref.put(sQPairSrcPath, sSourcePath);
        pref.put(sQPairDstPath, sDestinationPath);
        pref.put(sQPairFolderSfx, sFolderSuffix);
        pref.put(sQPairFilePfx, sFilePrefix);
        pref.putBoolean(sQPairSearchSubFolders, bSearchSubFolders);
        pref.putBoolean(sQPairPreserveFileNames, bPreserveFileNames);

        return true;

    }

    /*
        Remove this pair from the Pref Store
     */
    private void RemovePairFromStore( int idx ) {

        /*
          Remove the pair from Store. Ignore the local contents
         */
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

    /*
        Clear out old pairs from the store when we have none to save
     */
    public void ClearPairStore( int numPairs ) {

        for ( int i=0; i < numPairs; i++) {
            RemovePairFromStore( i );
        }
    }

    /*
      Clear out fields in the Pair Object
     */
    public void ClearPair( ) {
        sPairName = "";
        sSourcePath = "";
        sDestinationPath = "";
        sFolderSuffix = "";
        sFilePrefix = "";
        bSearchSubFolders = false;
        bPreserveFileNames = false;
    }

    /*
    ToString is called to render the item for the ListView
 */
    @Override
    public String toString() {
        return String.format("PairName: " + sPairName + " Source: " + sSourcePath
                /*
                + " Destination: " + sDestinationPath
                + " FolderSfx: " + sFolderSuffix
                + " FilePfx: " + sFilePrefix
                + " SrcSubFolders: " +bSearchSubFolders.toString()
                + " PreserveFnames: " +bPreserveFileNames.toString()
                */
        );

    }
}
