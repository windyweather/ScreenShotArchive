package net.windyweather.screenshotarchive;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.stage.Window;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static net.windyweather.screenshotarchive.SSApplication.APPLICATIONNAME;
import static net.windyweather.screenshotarchive.SSApplication.ORGANIZATION;
import static net.windyweather.screenshotarchive.SSController.printSysOut;
//import static sun.util.locale.LocaleUtils.isEmpty;


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
        let's make one
     */
    public SSArchivePair(){

    }

    /*
        for Debugging. Just print out the pair we are looking at
     */

    public void PrintPair() {
        printSysOut( String.format("PairName:%s Src:%s Dst:%s", sPairName, sSourcePath, sDestinationPath));
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

    private static String MakePairsXmlPath() {

        String currentUsersHomeDir = System.getProperty("user.home");
        String sXMLPairsPath = currentUsersHomeDir + File.separator + "." + ORGANIZATION
                + File.separator + APPLICATIONNAME + "Pairs.xml";
        return sXMLPairsPath;
    }

    /*
        SavePairListToXML
     */

    public static boolean SavePairListToXML( ObservableList<SSArchivePair> olPairs ) {
        XMLEncoder encoder = null;
        String sXMLPairsListPath = MakePairsXmlPath();
        /*
            Convert Observable List to normal list
         */
        List<SSArchivePair> listOfPairs = new ArrayList<>(100);
        listOfPairs.addAll( olPairs );

        try{
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream( sXMLPairsListPath )));
        }catch( Exception e ){
            printSysOut( String.format("SavePairsList: Error: %s Creating or Opening the xml file %s", e, sXMLPairsListPath) );
            return false;
        }

        encoder.writeObject( listOfPairs );

        printSysOut( String.format("SavePairListToXML: stored %d pairs to %s", listOfPairs.size(), sXMLPairsListPath));
        encoder.close();
        return true;
    }

    /*
        Restore the pair list from XML
     */
    public static List<SSArchivePair> RestorePairListFromXML() {
        List<SSArchivePair> listOfPairs = new ArrayList<>(100);
        XMLEncoder encoder = null;
        String sXMLPairsListPath = MakePairsXmlPath();


        // Use XMLDecoder to read the XML file in.
        List<SSArchivePair> listFromXML = List.of();
        try {
            printSysOut("RestorePairListFromXML");
            final XMLDecoder decoder = new XMLDecoder(new FileInputStream(sXMLPairsListPath));
            listFromXML = (List<SSArchivePair>) decoder.readObject();
            decoder.close();
            printSysOut(String.format("%d pairs restored", listFromXML.size()));

        } catch (Exception e) {
            printSysOut(String.format("Pairs Not Restored %s", sXMLPairsListPath));
        }
        printSysOut(String.format("RestorePairListFromXML %d pairs restored", listFromXML.size() ));

        return listFromXML;
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

    public ObservableValue<String> sPairNameProperty() {
        return  new SimpleStringProperty(sPairName);
    }

    public ObservableValue<String> sPairSourcePathProperty() {
        return new SimpleStringProperty(sSourcePath);
    }
}
