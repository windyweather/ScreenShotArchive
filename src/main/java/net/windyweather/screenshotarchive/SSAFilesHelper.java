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
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;

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
        Yes, the code in the program and the code for the library happen to be the same
        but if they change in the future this will make it easier to fix.
        *** Also this method is used to create the "File Suffix" which is just the modified time
        of the file formated as a string so that it is unique.
     */
    private static String GetDateFolderPrefix (FileTime ftModified, String sFolderPrefix ) {
        String sDateFolderPrefix = "";
        String sDateFormat = "";
        if (Objects.equals(sFolderPrefix, "yyyy_MM")) {
            sDateFormat = "yyyy_MM";
        } else if ( Objects.equals( sFolderPrefix, "yyyy_MM_dd") ) {
            sDateFormat = "yyyy_MM_dd";
        } else if ( Objects.equals( sFolderPrefix, "yyyy_MM_dd_HH_mm_ss_SSS") ) {
            sDateFormat = sFolderPrefix;
        }
        if ( !sDateFormat.isBlank() ) {

            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(ftModified.toInstant(), ZoneId.systemDefault());
                      DateTimeFormatter dtf = DateTimeFormatter.ofPattern(sDateFormat);
            sDateFolderPrefix = dtf.format(zonedDateTime);
            //SSController.printSysOut( String.format("GetDateFolderPrefix - %s", sDateFolderPrefix ) );

            /*
            SimpleDateFormat df = new SimpleDateFormat(sDateFormat);
            sDateFolderPrefix = df.format( ftModified );
            */

        }
        return sDateFolderPrefix;
    }

    /*
        Get the folder suffix of today by using a FileTime of now
        This is called by ViewDestination to show this months or today's images from the destination
     */
    public static String GetTodayFolderSuffix( String sFolderSfxCode ) {

        String sFolderSfxToday;
        //java.time.LocalDate today = java.time.LocalDate.now();
        Instant today = Instant.now();
        FileTime ftToday = FileTime.from(today);
        sFolderSfxToday = GetDateFolderPrefix( ftToday, sFolderSfxCode );
        SSController.printSysOut(String.format("GetDayFolderSuffix of Today %s", sFolderSfxToday));

        return sFolderSfxToday;
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
        // Try / Catch around the actual copy?
        try {
            //Files.copy( aFilePath, pDestinationFile, COPY_ATTRIBUTES);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return false;
        }


        return true;
    }

    /*
        Copy a file from Source to Destination and modify the file name based on a prefix, indicating
        which game it was from and based on the modification date / time of the file.
        Once we make the absolute file name with the folder path, if the file exists, then don't copy
        it again.
     */
    private static boolean CopyModifiedSourceToDestination(
            String sSourceAbsFilePath, String sDestinationPath, String sFolderPrefix, String sFilePrefix ) throws IOException {

        //Path pSrcFile = Paths.get(sSourceAbsFilePath);
        File fSrcFile = new File(sSourceAbsFilePath);
        if (!fSrcFile.exists()) {
            SSController.printSysOut(String.format("CopyModifiedSourceToDestination - Source File Not Found %s", sSourceAbsFilePath));
            return false;
        }
        Path aFilePath = Paths.get(sSourceAbsFilePath);
        String sSourceExtension = FilenameUtils.getExtension(sSourceAbsFilePath);
        FileTime ftModified = Files.getLastModifiedTime(aFilePath);

         /*
            Get a subfolder based on the folder prefix and the modified date of the source file.
         */
        String sDateFolderPrefix = GetDateFolderPrefix( ftModified, sFolderPrefix);
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
           Make a filename from the sFilePrefix [ things like ESO_ or GW2_ for
           Elder Scroll Online or Guild Wars 2, and follow that by a string from the
           file modified date/time in the format of: yyyy_MM_dd_HH_mm_ss_SSS
         */
        String sFileDateSuffix = GetDateFolderPrefix( ftModified, "yyyy_MM_dd_HH_mm_ss_SSS" );
        //String sFileName = String.valueOf(aFilePath.getFileName());
        /*
            Add the file type [Extension] from the source file. Don't call some class to figure it out
            from the content of the file. Assume the source file knows what type it is.
         */
        String sFileDateName = sFilePrefix + sFileDateSuffix + "." + sSourceExtension;
        Path pDestinationFile = Paths.get(sDestAbsFilePathDir + File.separator + sFileDateName);


        /*
            If the destination file, in all its Date-ified Glory exists, that means that we
            copied the file, and we don't do it again. Since the file name is dateified by
            the modification date/time, we don't need to check the destination file further.
            We know we already copied that exact file.
         */
        File fDestinationFile = new File (String.valueOf(pDestinationFile.toAbsolutePath()));
        if ( fDestinationFile.exists() ) {
            SSController.printSysOut(String.format("CopyModifiedSourceToDestination - Dest Exists Do Not Copy %s >> %s",
                    aFilePath.toString(), pDestinationFile.toString()  ));
            return false;
        }
        /*
            Oh, No! the file does not exist, so Actually Copy the file.
         */
        // Try / Catch around the actual copy?
        try {
            /*
                Actually copy the file. Maybe put a check for testing around this.
             */
            Files.copy( aFilePath, pDestinationFile, COPY_ATTRIBUTES);
            SSController.printSysOut(String.format("CopyModifiedSourceToDestination - Copy %s >> %s",
                    aFilePath.toString(), pDestinationFile.toString()  ));
        } catch (Exception e) {
            //throw new RuntimeException(e);
            SSController.printSysOut(String.format("CopyModifiedSourceToDestination - EXCEPTION on Copy %s >> %s",
                    aFilePath.toString(), pDestinationFile.toString()  ));
            return false;
        }


        return true;
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
                }else {
                    iManySkipped++;
                }
            }
        }

        return String.format("%d Files Copied, %d Files Skipped", iManyCopied, iManySkipped);
    };

    /*
        All image files [BMP, PNG, JPG] are deleted from the path. Subfolders are not disturbed.
     */
    private static boolean FileHelperDeleteFiles( String sFilePath ) {
        return true;
    };

    /*
        Delete image files from the supplied folder - presumably the Game Screenshot folder -
        and return a status string for display by the app to tell the user what happened.
     */

    public static String FileHelperCleanSource( String sSourcePath, boolean bSourceSubFolders ) {

        /*
            Set things up to search for subfolders if we should
            Should only be used for Source since some games store images
            in subfolders, oddly.
         */
        String sSubPfx = "";
        if (bSourceSubFolders) {
            sSubPfx = "**\\";
        }
        String[] saIncludeImages = new String[]{sSubPfx + "*.bmp", sSubPfx + "*.jpg", sSubPfx + "*.png"};

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(saIncludeImages);
        scanner.setCaseSensitive(false);
        scanner.setBasedir(new File(sSourcePath));
        scanner.scan();

        /*
            Get a list of the source files we found
         */
        String[] sSourceFiles = scanner.getIncludedFiles();

        int iManyDeleted = 0;

        for (String sSourceFile : sSourceFiles) {
            String sSourceAbsFilePath = sSourcePath + File.separator + sSourceFile;
            /*
                Delete the file from Source Game Screen Shot folder
             */
            try {
                File fSourceFile = new File(sSourceAbsFilePath);
                if (fSourceFile.delete()) {
                    iManyDeleted++;
                }

            } catch (Exception e) {
                //throw new RuntimeException(e);
                return String.format("Error deleting source file: %s", sSourceAbsFilePath);
            }

        }

        return String.format("%d Source Images Deleted", iManyDeleted );
    }
}
