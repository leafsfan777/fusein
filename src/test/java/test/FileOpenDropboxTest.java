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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

/**
 * Whitebox Tests for the file open feature
 * of dropbox. This includes backend tests only.
 * 
 * @author sid
 */
public class FileOpenDropboxTest extends DriveTest {
    private Dropbox dropbox;

    public FileOpenDropboxTest() {
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
     * This is to test that when we try to open a file
     * which exists on the root; it opens properly
     * (i.e. sharable link is successfully generated)
     * file: README_FUSIN.txt (in root)
     */
    @Test
    public void testDboxOpen(){
        RemoteFolder dbxRoot = dropbox.getRootFolder();
        RemoteFile toOpen = null;
        String fileToOpen = "README_FUSIN.txt";
        
        //First we check to see if the file exists in root
        ArrayList<RemoteEntry> currentRootList = (ArrayList<RemoteEntry>) dbxRoot.getEntries();
        boolean fileFound = false;
        for (RemoteEntry e : currentRootList) {
            if(e.getName().equals(fileToOpen)){
                fileFound = true;
                toOpen = e.asFile();
            }
        }
        assertTrue("File was not found.", fileFound);  //passes if file is found
        
        //Now we try to generate a sharable url and if null, clearly the file cannot be opened
        assertNotNull("File URL could not be generated.",toOpen.getLink());
    }
    
    /**
     * Test 2
     * This is to test that when we try to open a file
     * which does not exist on the root; it does not open.
     * file: garbage.txt (does not exist!)
     * Note: This also proves when the input is null.
     */
    @Test
    public void testDboxOpenGarbage(){
        RemoteFolder dbxRoot = dropbox.getRootFolder();
        RemoteFile toOpen = null;
        String fileToOpen1 = "garbage.txt";
        String fileToOpen2 = null;
        
        //First we check to see if the file exists in root (or if we have a null input)
        ArrayList<RemoteEntry> currentRootList = (ArrayList<RemoteEntry>) dbxRoot.getEntries();
        boolean fileFound = false;
        for (RemoteEntry e : currentRootList) {
            if(e.getName().equals(fileToOpen1)){
                fileFound = true;
                toOpen = e.asFile();
            }
            if(e.getName().equals(fileToOpen2)){
                fileFound = true;
                toOpen = e.asFile();
            }

        }
        assertFalse("File was found.", fileFound);  //passes if file is not found
        
        //the file to open never received an instance of the RemoteFile so we check to see if it is null
        assertTrue("Valid file", toOpen == null);
    }
    
    /**
     * Test 3
     * This is to test that when we try to open a file
     * within a folder; it opens.
     * (i.e. sharable link is generated properly)
     * file: download.txt under TEST_FILES folder
     */
    @Test
    public void testDboxOpenFileInFolder(){
        RemoteFolder dbxRoot = dropbox.getRootFolder();
        RemoteFolder toOpenFolder = null;
        RemoteFile toOpenFile = null;
        String fileToOpen = "download.txt";
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
            if(e.getName().equals(fileToOpen)){
                fileFound = true;
                toOpenFile = e.asFile();
            }
        }
        assertTrue("File was not found within the folder.",fileFound); //pass if file exists within the folder
        
        //finally we generate the link
        assertNotNull("Link was not generated properly", toOpenFile.getLink());
    }
    
}   //End FileOpenDropboxTest.java