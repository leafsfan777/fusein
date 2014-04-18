package test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import main.RemoteDrive;
import main.RemoteEntry;
import main.RemoteFile;
import main.RemoteFolder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import services.Dropbox;
import services.GDrive;

/**
 * Tests the functionality of adding a new service to FuseIn.
 * This does not include GUI components.  The test will confirm
 * that authentication and drive stores are updated accordingly, as
 * well as ensure the factory returns the right object.
 * 
 * @author Cam
 * @date March 25 2014
 * @version 1.0
 *
 */
public class UploadFileFunctionalTest extends DriveTest {
	
	private GDrive gdrive;
	private Dropbox dropbox;
	private String uploadFilename;
	private String uploadFileTest = "uploadtest.txt";
	
	public UploadFileFunctionalTest() {
		gdrive = new GDrive();
		dropbox = new Dropbox();
	}
	
	@Before
	public void setup() {
		// Update objects with correct test tokens.
		dropbox.setAuthToken(getDboxToken());
		gdrive.setService(authenticate());
		gdrive.setInfo();
		uploadFilename = getTestFile();
	}
		
	@After
	public void teardown() {
		dropbox = null;
		gdrive = null;
	}
	
	
	@Test
	public void testDropboxUpload() {
		RemoteFolder dropboxRoot = dropbox.getRootFolder();
		RemoteFile toDelete = null;
		
		// Ensure the uploaded file does not already exist on the service.
		// This is to make sure that we don't get any false positives.
		ArrayList<RemoteEntry> oldRootList = (ArrayList<RemoteEntry>) dropboxRoot.getEntries();
		for (RemoteEntry e : oldRootList) {
			assertFalse("File is already uploaded, please delete first",
					e.getName().equals(uploadFilename));
		}
		
		// Upload the file, and assert that the upload completes correctly.
		assertNotNull("File failed to upload", dropboxRoot.uploadFile(uploadFilename));
		
		// Now we check to make sure the file is in the listing.
		ArrayList<RemoteEntry> newRootList = (ArrayList<RemoteEntry>) dropboxRoot.getEntries();
		boolean fileFound = false;
		for (RemoteEntry e : newRootList) {
			if (e.getName().equals(uploadFileTest)) {
				fileFound = true;
				toDelete = e.asFile();
			}
		}

		// Assert that the file has been found.
		assertTrue("File not found, upload must have failed", fileFound);
		
		// Now we remove the test file.
		assertTrue("File failed to delete", toDelete.delete());
		
	}
	
	@Test
	public void testGDriveUpload() {
		RemoteFolder driveRoot = gdrive.getRootFolder();
		RemoteFile toDelete = null;
		
		// Ensure the uploaded file does not already exist on the service.
		// This is to make sure that we don't get any false positives.
		ArrayList<RemoteEntry> oldRootList = (ArrayList<RemoteEntry>) driveRoot.getEntries();
		for (RemoteEntry e : oldRootList) {
			assertFalse("File is already uploaded, please delete first",
					e.getName().equals(uploadFilename));
		}
		
		// Upload the file, and assert that the upload completes correctly.
		assertNotNull("File failed to upload", driveRoot.uploadFile(uploadFilename));
		
		// Now we check to make sure the file is in the listing.
		ArrayList<RemoteEntry> newRootList = (ArrayList<RemoteEntry>) driveRoot.getEntries();
		boolean fileFound = false;
		for (RemoteEntry e : newRootList) {
			if (e.getName().equals(uploadFileTest)) {
				fileFound = true;
				toDelete = e.asFile();
			}
		}

		// Assert that the file has been found.
		assertTrue("File not found, upload must have failed", fileFound);
		
		// Now we remove the test file.
		assertTrue("File failed to delete", toDelete.delete());
	}
}
