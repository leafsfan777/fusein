package test;

import static org.junit.Assert.*;

import main.RemoteEntry;
import main.RemoteFolder;
import main.RemoteFile;

import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import services.GDrive;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

public class GDriveUnitTests {
	public Drive authenticate() {
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		
		GoogleCredential credential = new GoogleCredential.Builder().setJsonFactory(jsonFactory)
				.setTransport(httpTransport).setClientSecrets("428540166150-r7chn8m9gb7o9m3vkkibmbbs444999i1.apps.googleusercontent.com", "kJzGYz02Nog18vnkkyIOQ0kI").build();
			credential.setRefreshToken("1/J4GJXmIcGjkTpcs-zg-YUizyXfPGk7WqOipw0fXiVdA");
		
		Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("FuseIn").build();
		return service;
		
	}
	
////////////////////////////////////////////////////////////////////////////////
//                            UPLOAD TEST
////////////////////////////////////////////////////////////////////////////////
	/*
	 * A simple test that tests the ideal upload scenario, that is:
	 * a file is supplied that exists and is not null
	 * This test tests the basic funtionality of upload
	 */
	@Test
	public void uploadTest() {
		GDrive drive = new GDrive();
		drive.setService(authenticate());
		drive.setInfo();
		
		RemoteFolder uploadFolder = drive.getRootFolder();
		java.io.File uploadFile = new java.io.File("test.txt");
		
		assertTrue(uploadFolder.uploadFile(uploadFile.getAbsolutePath()) != null);
	}
	
	/*
	 * A test that tests the upload scenario, that is:
	 * a file is supplied that is not null but does not exist
	 * this test tests the handling of a FileNotFoundException being thrown by supplying a file that doesn't exist
	 */
	
	@Test
	public void uploadFileDoesNotExistTest() {
		GDrive drive = new GDrive();
		drive.setService(authenticate());
		drive.setInfo();
		
		RemoteFolder uploadFolder = drive.getRootFolder();
		java.io.File uploadFile = new java.io.File("nonExistentFile.txt");
		
		assertTrue(uploadFolder.uploadFile(uploadFile.getAbsolutePath()) == null);
	}
	
	/*
	 * A test that tests the upload scenario, that is:
	 * a file is supplied that is null
	 * this test tests the handling of a null argument being passed to uploadFile
	 */
	
	@Test
	public void uploadFileNullTest() {
		GDrive drive = new GDrive();
		drive.setService(authenticate());
		drive.setInfo();
		
		RemoteFolder uploadFolder = drive.getRootFolder();
		
		assertTrue(uploadFolder.uploadFile(null) == null);
	}
	
	@Test
	public void uploadNoMimeTypeTest() {
		GDrive drive = new GDrive();
		drive.setService(authenticate());
		drive.setInfo();
		
		RemoteFolder uploadFolder = drive.getRootFolder();
		java.io.File uploadFile = new java.io.File("nonExistentFile.txt");
		
		assertTrue(uploadFolder.uploadFile(uploadFile.getAbsolutePath()) == null);
	}

////////////////////////////////////////////////////////////////////////////////
//                         DELETE TEST
////////////////////////////////////////////////////////////////////////////////

    /*
     * This test contains two tests for deleting a file.
     * It first uploads a file that we want to delete.
     * It then finds the file in the drive and deletes it. 
     * This delete should be successfull.
     * It then attempts to delete the already removed file again.
     * This test should not be successful. 
     * The tests are grouped together for necessity.
     */
    @Test
    public void deleteTest() {
        GDrive drive = new GDrive();
        drive.setService(authenticate());
        drive.setInfo();
        
        RemoteFolder uploadFolder = drive.getRootFolder();
        java.io.File uploadFile = new java.io.File("deletetest.txt");
        
        assertTrue(uploadFolder.uploadFile(uploadFile.getAbsolutePath()) != null);
        
        // Find the file we just uploaded
        RemoteFile f = null;
        ArrayList<RemoteEntry> files = (ArrayList<RemoteEntry>)uploadFolder.getEntries();
        for (RemoteEntry e : files) {
            if (e.getName().equals("deletetest.txt")) {
                assertTrue(e.isFile());
                f = e.asFile();
                break;
            }
        }
        
        // ensure we found the file
        assertTrue(f != null);


        
    }

}
