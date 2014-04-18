package test;

import java.net.URL;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

public abstract class DriveTest {

	private final String CLIENT_ID = "428540166150-r7chn8m9gb7o9m3vkkibmbbs444999i1.apps.googleusercontent.com";
	private final String APP_SECRET = "kJzGYz02Nog18vnkkyIOQ0kI";
	private final String REFRESH_TOKEN = "1/J4GJXmIcGjkTpcs-zg-YUizyXfPGk7WqOipw0fXiVdA";
	private final String DBOX_TOKEN = "IqhDQMtCNg4AAAAAAAAAAbUGqzss1S41kKTxgpzA1UbI7rjlDfuCvnRNxAR56uKI";
	
	private final String UPLOAD_TEST = "uploadtest.txt";

	public Drive authenticate() {
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(jsonFactory).setTransport(httpTransport)
				.setClientSecrets(CLIENT_ID, APP_SECRET).build();
		credential.setRefreshToken(REFRESH_TOKEN);

		Drive service = new Drive.Builder(httpTransport, jsonFactory,
				credential).setApplicationName("FuseIn").build();
		return service;
	}
	
	public String getDboxToken() {
		return DBOX_TOKEN;
	}
	
	public String getTestFile() {
		URL testFile = ClassLoader.getSystemResource("tokens/uploadtest.txt");
		return testFile.getFile();
	}

}
