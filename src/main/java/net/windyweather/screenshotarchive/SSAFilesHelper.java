package net.windyweather.screenshotarchive;

import java.nio.file.Files;

/*
    Class to encapsulate our calls to the Java Files stuff
    Copy, Delete files. Directory scanner is done in the Controller class
 */
public class SSAFilesHelper {

    /*
        Copy all the image files from the source to the destination paths
        If preserveFileNames, then do not modify the file names.
        else Create file names based on the sFilePrefix and the modification date/time of the file.
        The subfolder chosen is based on the sFolderPrefix string.
        If it's empty, no subfolder is used.
        The other choices for the folder prefix is yyyy_MM, or yyyy_MM_ss, then the subfolder name taken from
        the modification date formatted appropriately.
        The name Format is "FILEPREFIX"+"yyyy_MM_dd_hh_mm_ss_uuu" where uuu is the milliseconds.
        If the file exists in the destination, then the copy is not done.
     */
    public static boolean  FileHelperCopySource( String sSourcePath, String sDestinationPath,
                                               String sFolderPrefix, String sFilePrefix, boolean bPreserveFileNames ) {

        return true;
    };

    /*
        All image files [BMP, PNG, JPG] are deleted from the path. Subfolders are not disturbed.
     */
    public static boolean FileHelperDeleteFiles( String sFilePath ) {
        return true;
    };

    public static boolean FileHelperCleanSource( String sFilePath, boolean bSubFolders ) {

        return true;
    }

}
