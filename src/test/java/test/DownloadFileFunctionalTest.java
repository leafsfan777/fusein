package test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import main.RemoteDrive;
import main.RemoteEntry;
import main.RemoteFile;
import main.RemoteFolder;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import services.Dropbox;
import services.GDrive;

/**
 * Tests the functionality of downloading a known file from Dropbox and Google
 * Drive.
 * 
 * @author rjking
 * @date March 27, 2014
 */
@RunWith(JUnit4.class)
public class DownloadFileFunctionalTest extends DriveTest {
	// The drives we are going to test.
	private Dropbox dbox;
	private GDrive gdrive;

	/**
	 * The SHA-1 hash of the known download file, download.txt.
	 */
	private byte[] downloadTxtHash = { (byte) 0x46, (byte) 0xa6, (byte) 0x28,
			(byte) 0xb7, (byte) 0x91, (byte) 0xc6, (byte) 0x6e, (byte) 0xd3,
			(byte) 0x1f, (byte) 0xf4, (byte) 0x08, (byte) 0xe3, (byte) 0xa4,
			(byte) 0xbe, (byte) 0x9a, (byte) 0x29, (byte) 0x2c, (byte) 0x46,
			(byte) 0x93, (byte) 0xd5 };

	// Oh neat. Signed BYTES. That's real neat, Java. Thanks.

	/**
	 * Initializes and authenticates the service providers we need.
	 */
	@Before
	public void initServices() {
		dbox = new Dropbox();
		gdrive = new GDrive();

		dbox.setAuthToken(getDboxToken());
		gdrive.setService(authenticate());
		gdrive.setInfo();
	}

	/**
	 * Retrieves the standard test file (TEST_FILES/download.txt) from the given
	 * drive.
	 * 
	 * @param drive
	 *            The drive to get the test file from.
	 * @return The test file.
	 */
	private RemoteFile getTestFile(RemoteDrive drive) {
		RemoteFolder root = drive.getRootFolder();
		RemoteFolder testDir = null;
		for (RemoteEntry entry : root.getEntries()) {
			if (entry.isFile()) {
				continue;
			}

			RemoteFolder dir = entry.asFolder();
			if (dir.getName().equals("TEST_FILES")) {
				testDir = dir;
				break;
			}
		}

		if (testDir == null) {
			fail("TEST_FILES directory is missing!");
		}

		RemoteFile testFile = null;
		for (RemoteEntry entry : testDir.getEntries()) {
			if (entry.isFolder()) {
				continue;
			}

			RemoteFile file = entry.asFile();
			if (file.getName().equals("download.txt")) {
				testFile = file;
				break;
			}
		}

		if (testFile == null) {
			fail("Known download test file TEST_FILES/download.txt is missing!");
		}

		return testFile;
	}

	/**
	 * Calculates the SHA-1 sum of the given file.
	 * 
	 * @param localPath
	 *            The file to generate a hash from.
	 * @return The file's SHA-1 sum.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] sha1Sum(String localPath) throws IOException,
			NoSuchAlgorithmException {
		// Retrieve the specified file from the local system.
		File inputFile = new File(localPath);
		FileInputStream inputStream = new FileInputStream(inputFile);

		MessageDigest md = MessageDigest.getInstance("SHA-1");

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			md.update(buffer, 0, bytesRead);
		}

		inputStream.close();

		return md.digest();
	}

	/**
	 * Downloads and verifies a known file from the Dropbox test account.
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws ArrayComparisonFailure
	 */
	@Test
	public void downloadFileDropbox() throws ArrayComparisonFailure,
			NoSuchAlgorithmException, IOException {
		// Get the file, and download it.
		RemoteFile dlFile = getTestFile(dbox);
		assertTrue("Failed to download download.txt!",
				dlFile.download("download.txt"));

		// Ensure that the file matches our known one (i.e. it was not
		// corrupted).
		assertArrayEquals("download.txt failed validation!", downloadTxtHash,
				sha1Sum("download.txt"));
	}

	/**
	 * Downloads and verifies a known file from the Google Drive test account.
	 * 
	 * @throws ArrayComparisonFailure
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	@Test
	public void downloadFileGDrive() throws ArrayComparisonFailure,
			NoSuchAlgorithmException, IOException {
		// Get the file, and download it.
		RemoteFile dlFile = getTestFile(gdrive);
		assertTrue("Failed to download download.txt!",
				dlFile.download("download.txt"));

		// Ensure that the file matches our known one (i.e. it was not
		// corrupted).
		assertArrayEquals("download.txt failed validation!", downloadTxtHash,
				sha1Sum("download.txt"));
	}
}
