package test;

import services.Dropbox;
import main.RemoteFile;
import main.RemoteFolder;
import main.RemoteEntry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.io.File;

/**
 * Whitebox Tests for the file download feature
 * of dropbox. This includes backend tests only.
 * 
 * @author sid
 */
public class FileDownloadDropboxTest extends DriveTest {
    private Dropbox dropbox;

    public FileDownloadDropboxTest() {
        dropbox = new Dropbox();
    }
    
    @Before
    public void setup() {
        dropbox.setAuthToken(getDboxToken());
    }
    
    @After
    public void teardown(){
        dropbox = null;
    }

    /**
     * Test 1
     * This is to test that when we try to download
     * a file (which exists) from the root directory;
     * it succeeds.
     * (i.e. download to the current working directory, then test to
     * see if it exists, then delete from local.)
     * file: README_FUSIN.txt (in root)
     */
    @Test
    public void testDboxDownloadFileOnRoot(){
        RemoteFolder dbxRoot = dropbox.getRootFolder();
        RemoteFile toDownload = null;
        String fileToOpen = "README_FUSIN.txt";
        
        //First we check to see if the file exists in root
        ArrayList<RemoteEntry> currentRootList = (ArrayList<RemoteEntry>) dbxRoot.getEntries();
        boolean fileFound = false;
        for (RemoteEntry e : currentRootList) {
            if(e.getName().equals(fileToOpen)){
                fileFound = true;
                toDownload = e.asFile();
            }
        }
        assertTrue("File was not found.", fileFound);  //passes if file is found
        
        //Now we try to download file and check if it was successfully downloaded
        assertTrue(toDownload.download("d.txt"));
        
        //Now we check if file exists on current directory and delete it
        File file = new File("d.txt");
        assertTrue(file.exists());
        assertTrue(file.delete());
        
    }
    
    /**
     * Test 2
     * This is to test that when we try to download a file
     * which does not exist on the root; it does not succeed.
     * file: garbage.txt (does not exist!)
     * Note: This also proves when the input is null.
     */
    @Test
    public void testDboxDownloadGarbage(){
        RemoteFolder dbxRoot = dropbox.getRootFolder();
        RemoteFile toDownload = null;
        String fileToOpen1 = "garbage.txt";
        String fileToOpen2 = null;
        
        //First we check to see if the file exists in root (or if we have a null input)
        ArrayList<RemoteEntry> currentRootList = (ArrayList<RemoteEntry>) dbxRoot.getEntries();
        boolean fileFound = false;
        for (RemoteEntry e : currentRootList) {
            //check for specified file
            if(e.getName().equals(fileToOpen1)){
                fileFound = true;
                toDownload = e.asFile();
            }
            //check for null
            if(e.getName().equals(fileToOpen2)){
                fileFound = true;
                toDownload = e.asFile();
            }

        }
        assertFalse("Error: File was found.", fileFound);  //passes if file is not found
        
        //Now we check if the instance of RemoteFile is still null
        assertNull("Error: File was downloaded.", toDownload);
        
        //Now we check if file exists on current directory
        File file = new File("d.txt");
        assertFalse("Error: File exists",file.exists());    //passes if file does not exist
        
    }
    
    /**
     * Test 3
     * This is to test that when we try to download a file
     * within a folder; it succeeds.
     * (i.e. download to the current working directory, then test to
     * see if it exists, then delete from local.)
     * file: download.txt under TEST_FILES folder
     */
    @Test
    public void testDboxDownloadFileInFolder(){
        RemoteFolder dbxRoot = dropbox.getRootFolder();
        RemoteFolder toOpenFolder = null;
        RemoteFile toDownload = null;
        String fileToDownload = "download.txt";
        String folderName = "TEST_FILES";
        
        //First we check to see if the folder exists
        ArrayList<RemoteEntry> currentRootList = (ArrayList<RemoteEntry>) dbxRoot.getEntries();
        boolean folderFound = false;
        for (RemoteEntry e : currentRootList) {
            if(e.getName().equals(folderName)){
                folderFound = true;
                toOpenFolder = e.asFolder();
            }
        }
        assertTrue("Folder was not found on drobpox.",folderFound); //pass if folder exists
        
        //get the entries of the folder and check if file exists
        ArrayList<RemoteEntry> currentFolderList = (ArrayList<RemoteEntry>) toOpenFolder.getEntries();
        boolean fileFound = false;
        for (RemoteEntry e : currentFolderList) {
            if(e.getName().equals(fileToDownload)){
                fileFound = true;
                toDownload = e.asFile();
            }
        }
        assertTrue("File was not found within the folder.",fileFound); //pass if file exists within that folder
        
        //Now we try to download file and check if it was successfully downloaded
        assertTrue("Error: File could not be downloaded.", toDownload.download("dload.txt"));
        
        //Now we check if file exists on current directory and delete it
        File file = new File("dload.txt");
        assertTrue("Error: File does not exist", file.exists());
        assertTrue(file.delete());
        
    }
    
}   //End FileDownloadDropboxTest.java