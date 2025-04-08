package net.windyweather.screenshotarchive;

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

/*
    Class to encapsulate our calls to the Java Files stuff
    Copy, Delete files. Directory scanner is done in the Controller class
 */
public class SSAFilesHelper {

    /*
        Return the modified date/time of the file as a folder prefix string
        so we put the files in subfolders according to date, or not.
        If the folder prefix is blank, just dump all the files in the folder provided.
     */
    private static String GetDateFolderPrefix (FileTime ftModified, String sFolderPrefix ) {
        String sDateFolderPrefix = "";
        String sDateFormat = "";
        if (Objects.equals(sFolderPrefix, "yyyy_mm")) {
            sDateFormat = "yyyy_mm";
        } else if ( Objects.equals( sFolderPrefix, "yyyy_mm_dd") ) {
            sDateFormat = "yyyy_mm_dd";
        }
        if ( !sDateFormat.isBlank() ) {
            DateFormat df = new SimpleDateFormat(sDateFormat);
            sDateFolderPrefix = df.format( ftModified );
        }
        return sDateFolderPrefix;
    }

    /*
        Copy a file from Source to Destination without modifying the file name
        Place the file in a subfolder of the destination based on the modify date of
        the file, or not.
        If the file exists, don't copy it again.
     */
    private static boolean CopyPreservedSourceToDestination(
            String sSourceAbsFilePath, String sDestinationPath, String sFolderPrefix) throws IOException {

        //Path pSrcFile = Paths.get(sSourceAbsFilePath);
        File fSrcFile = new File(sSourceAbsFilePath);
        if (!fSrcFile.exists()) {
            SSController.printSysOut(String.format("CopyPreservedSourceToDestination - Source File Not Found %s", sSourceAbsFilePath));
            return false;
        }
        Path aFilePath = Paths.get(sSourceAbsFilePath);
        FileTime ftModified = Files.getLastModifiedTime(aFilePath);
        /*
            Get a subfolder based on the folder prefix and the modified date of the source file.
         */
        String sDateFolderPrefix = GetDateFolderPrefix(ftModified, sFolderPrefix);
        String sDestAbsFilePathDir = sDestinationPath;
        if (!sDateFolderPrefix.isBlank()) {
            sDestAbsFilePathDir += File.separator + sDateFolderPrefix;
        }
        File fDestFile = new File(sDestAbsFilePathDir);
        /*
            If the destination sub-folder does not exist, then create it
         */
        if (!fDestFile.exists()) {
            Path pDstFile = Paths.get(sDestAbsFilePathDir);
            // Try / Catch around the following?
            Path pDstPathDone = Files.createDirectory( pDstFile );
        }
        /*
            Make the path to create the file based on the filename of the source
            path.
         */
        String sFileName = String.valueOf(aFilePath.getFileName());
        Path pDestinationFile = Paths.get(sDestAbsFilePathDir + File.separator + sFileName);
        SSController.printSysOut(String.format("CopyPreservedSourceToDestination - Copy %s >> %s", 
                aFilePath.toString(), pDestinationFile.toString()  ));
        /*
            Actually Copy the file.
         */
        // Try / Catch around the following?
        // Files.copy( aFilePath, pDestinationFile, COPY_ATTRIBUTES);

        return true;
    }

    /*
        Copy a file from Source to Destination and modify the file name based on a prefix, indicating
        which game it was from and based on the modification date / time of the file.
        Once we make the absolute file name with the folder path, if the file exists, then don't copy
        it again.
     */
    private static boolean CopyModifiedSourceToDestination(
            String sSourceAbsFilePath, String sDestinationPath, String sFolderPrefix, String sFilePrefix ) {
        /*
            Tie this off for now
         */
        return false;
    }


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
    public static String  FileHelperCopySource( String sSourcePath, String sDestinationPath,
                                               String sFolderPrefix, String sFilePrefix,
                                                boolean bPreserveFileNames, boolean bSourceSubFolders ) throws IOException {

        /*
            Set things up to search for subfolders if we should
            Should only be used for Source since some games store images
            in subfolders, oddly.
         */
        String sSubPfx = "";
        if ( bSourceSubFolders ) {
                sSubPfx = "**\\";
        }
        String[] saIncludeImages = new String[]{sSubPfx+ "*.bmp", sSubPfx+ "*.jpg", sSubPfx+"*.png"  };

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes( saIncludeImages );
        scanner.setCaseSensitive( false );
        scanner.setBasedir( new File( sSourcePath ));
        scanner.scan();

        /*
            Get a list of the source files we found
         */
        String[] sSourceFiles = scanner.getIncludedFiles();

        int iManyCopied = 0;
        int iManySkipped = 0;
        boolean bFileCopied = false;
        for (String sSourceFile : sSourceFiles) {
            String sSourceAbsFilePath = sSourcePath +  File.separator + sSourceFile;
            /*
                Copy the file from Source to Destination if it's not already there
                Should we modify the file names with a filename prefix?
             */
            if ( bPreserveFileNames ) {
                if ( CopyPreservedSourceToDestination(sSourceAbsFilePath, sDestinationPath, sFolderPrefix)) {
                /*
                  The file was copied, so just count it
                 */
                    iManyCopied++;
                } else {
                    iManySkipped++;
                }
            } else {
                /*
                    Copy files and modify the file name with the prefix and the modify date/time
                 */
                if ( CopyModifiedSourceToDestination( sSourceAbsFilePath, sDestinationPath, sFolderPrefix, sFilePrefix )) {
                    iManyCopied++;
                }
            }
        }

        return String.format("%d Files Copied, %d Files Skipped", iManyCopied, iManySkipped);
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
