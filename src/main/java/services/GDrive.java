package services;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.ParentReference;

import main.RemoteDrive;
import main.RemoteEntry;
import main.RemoteFile;
import main.RemoteFileDownloadMonitor;
import main.RemoteFolder;

/**
 * Provides an implementation of the basic cloud storage functionality for a
 * Google Drive account. All functionality will require a proper authentication token
 * to be issued by the Drive authentication server.
 * 
 * @author Fusein
 * @date March 06, 2014
 * @version 1.0
 */
public class GDrive implements RemoteDrive {
	private String appID;
	private String appSecret;
	private String redirectUri;
	private HttpTransport httpTransport;
	private JsonFactory jsonFactory;
	private GoogleCredential credential;
	private GoogleAuthorizationCodeFlow flow;
	private String clientIdentifier;
	private String authToken;
	private Drive service;
	private About info; 

	/**
	 * Default constructor to initialize data
	 */
	public GDrive() {
		this.appID = "428540166150-r7chn8m9gb7o9m3vkkibmbbs444999i1.apps.googleusercontent.com";
		this.appSecret = "kJzGYz02Nog18vnkkyIOQ0kI";
		redirectUri = "urn:ietf:wg:oauth:2.0:oob";
		clientIdentifier = "FuseIn";
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
	}
	
	@Override
	public String getServiceNiceName()
	{
		return "Google Drive";
	}
	
	@Override
	public String generateAuthURL() {
		flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, appID, appSecret, Arrays.asList(DriveScopes.DRIVE)).setAccessType("offline").setApprovalPrompt("force").build();

		String url = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();

		openAuthLink(url);
		
		return null;
	}
	
	@Override
	public boolean finalizeAuth(String authKey) {
		GoogleTokenResponse response = null;
		try {
			response = flow.newTokenRequest(authKey).setRedirectUri(redirectUri).execute();
		} catch (IOException e) {
			System.out.println("Unable to authorize. Please enter a valid key.");
			return false;
		}

		this.credential = new GoogleCredential.Builder().setJsonFactory(jsonFactory)
	            .setTransport(httpTransport).setClientSecrets(this.appID, this.appSecret).build();
		credential.setFromTokenResponse(response);

		// Create a new authorized API client
		this.service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("FuseIn").build();
		
		try {
			this.info = service.about().get().execute();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		this.authToken = this.credential.getAccessToken();
		
		return true;
	}
	
	public void setInfo(){
		try {
			this.info = service.about().get().execute();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	public String getUsername()
	{
		return this.info.getName();
	}
	
	@Override
	public String getAuthToken()
	{
		return this.authToken;
	}
	
	public String getRootFolderID(){
		return this.info.getRootFolderId();
	}
	
	@Override
	public void setAuthToken(String newToken)
	{
		this.authToken = newToken;
		
		// Get our new client object
		this.credential = new GoogleCredential().setAccessToken(newToken);
		this.service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("FuseIn").build();
		try {
			this.info = this.service.about().get().execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	@Override
	public RemoteFolder getRootFolder() {
		try {
			com.google.api.services.drive.model.File rootFolder = service.files().get(this.info.getRootFolderId()).execute();
			return new DriveFolder(rootFolder, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Opens the Google Drive account authorization link in the user's
	 * default browser.
	 * 
	 * @param authorizeUrl The URL to open.
	 */
	public void openAuthLink(String authorizeUrl) {
	    try {
	        Desktop.getDesktop().browse(new URL(authorizeUrl).toURI());
	    } catch (URISyntaxException | IOException e) {
	        e.printStackTrace();
	    }
	}

	public String getAppKey() {
		return appID;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public String getClientIdentifier() {
		return clientIdentifier;
	}
	
	public Drive getService(){
		return this.service;
	}
	
	public void setService(Drive service){
		this.service = service;
	}
	
	/**
	 * A file on the Google Drive remote server.
	 * 
	 * @author Ryan K
	 * @date March 05, 2014
	 */
	public class DriveFile implements RemoteFile {
		private RemoteFolder parent;
		private com.google.api.services.drive.model.File driveFile;
		
		public DriveFile(com.google.api.services.drive.model.File driveFile, RemoteFolder parent) {
			this.parent = parent;
			this.driveFile = driveFile;
		}
		
		@Override
		public String getName() {
			return this.driveFile.getTitle();
		}
		
		@Override
		public String getPath() {
			return this.driveFile.getId();
		}
		
		@Override
		public String getLink() {
			String url = (driveFile.getAlternateLink());
			return url;
		}

		@Override
		public RemoteDrive getRemoteDrive() {
			return GDrive.this;
		}
		
		@Override
		public RemoteFolder getFolder() {
			return this.parent;
		}

		@Override
		public boolean download(String localPath) {
			InputStream is = null;
			String remoteFilePath = this.getPath();
			
			HashMap<String, String> mimeTypes = new HashMap<String, String>();
			mimeTypes.put("application/vnd.google-apps.document", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			mimeTypes.put("application/vnd.google-apps.spreadsheet", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			mimeTypes.put("application/vnd.google-apps.drawing", "application/pdf");
			mimeTypes.put("application/vnd.google-apps.presentation", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
			
			com.google.api.services.drive.model.File file = null;
			try {
				file = service.files().get(remoteFilePath).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			
			
			String downloadUrl = "";
			String mimeType = "";
			
			if (file.getDownloadUrl() == null){
				mimeType = file.getMimeType();
				mimeType = mimeTypes.get(mimeType);
				downloadUrl = file.getExportLinks().get(mimeType);
			}
			else {
				downloadUrl = file.getDownloadUrl();
			}
			
			System.out.println(downloadUrl);
			
			if (downloadUrl != null && downloadUrl.length() > 0) {
			      try {
			        HttpResponse resp =
			            service.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl))
			                .execute();
			        is = resp.getContent();
			      } catch (IOException e) {
			        // An error occurred.
			        e.printStackTrace();
			        return false;
			      }
			    } else {
			      // The file doesn't have any content stored on Drive
			    	System.out.println("test");
			    	return false;
			      
			}
			
			java.io.File localFile = new java.io.File(localPath);
			
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(localFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			
			int nRead;
			byte[] data = new byte[16384];
			try {
				while ((nRead = is.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}

				buffer.flush();

				byte[] arr = buffer.toByteArray();

				is.read(arr);
				os.write(arr);

				os.close();

				return true;
			}
			catch (IOException e){
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public boolean isFolder() {
			return false;
		}

		@Override
		public boolean isFile() {
			return true;
		}

		@Override
		public RemoteFolder asFolder() {
			return null;
		}

		@Override
		public RemoteFile asFile() {
			return this;
		}

		@Override
		public boolean delete() {
			try {
			      service.files().delete(this.getPath()).execute();
			    } catch (IOException e) {
			      System.out.println("An error occurred: " + e);
			      return false;
			    }
			return true;
		}
	}
	
	/**
	 * A folder on the Google Drive remote server.
	 * 
	 * @author Ryan K
	 * @date March 05, 2014
	 */
	public class DriveFolder implements RemoteFolder {
		private RemoteFolder parent;
		private com.google.api.services.drive.model.File driveFolder;
		
		public DriveFolder(com.google.api.services.drive.model.File driveFolder, RemoteFolder parent) {
			this.parent = parent;
			this.driveFolder = driveFolder;
		}
		
		@Override
		public String getName() {
			return this.driveFolder.getTitle();
		}
		
		@Override
		public String getPath() {
			return this.driveFolder.getId();
		}

		@Override
		public RemoteDrive getRemoteDrive() {
			return GDrive.this;
		}
		
		@Override
		public RemoteFolder getParent() {
			return this.parent;
		}

		@Override
		public List<RemoteEntry> getEntries() {
			Children.List request = null;
			
			try {
				request = service.children().list(this.getPath());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			ArrayList<RemoteEntry> entries = new ArrayList<>();
			
			do {
				try {
					ChildList children = request.execute();
					
					for (ChildReference child : children.getItems()) {
						String childId = child.getId();
						com.google.api.services.drive.model.File file = service.files().get(childId).execute();
						
						if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
							entries.add(new DriveFolder(file, this));
						} else {
							entries.add(new DriveFile(file, this));
						}
					}
					request.setPageToken(children.getNextPageToken());
				} catch (IOException e) {
					System.out.println("An error occurred: " + e);
					request.setPageToken(null);
				}
			} while (request.getPageToken() != null &&
					request.getPageToken().length() > 0);
			
			return entries;
		}

		@Override
		public boolean isFolder() {
			return true;
		}

		@Override
		public boolean isFile() {
			return false;
		}

		@Override
		public RemoteFolder asFolder() {
			return this;
		}

		@Override
		public RemoteFile asFile() {
			return null;
		}

		@Override
		public RemoteFile uploadFile(String localPath) {
			String path = localPath;
			File inputFile = null;
			if (localPath != null){
				inputFile = new File(path);
			}
			else{
				return null;
			}
			
			Path uploadPath = inputFile.toPath();

			String mimeType = "";
			try {
				mimeType = java.nio.file.Files.probeContentType(uploadPath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}

			// File's metadata.
			com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
			body.setTitle(inputFile.getName());
			body.setDescription("Uploaded from fusein");
			body.setMimeType(mimeType);

			// Set the parent folder.
			body.setParents(Arrays.asList(new ParentReference().setId(this.getPath())));

			// File's content.

			FileContent mediaContent = new FileContent(mimeType, inputFile);
			try {
				com.google.api.services.drive.model.File file = service.files().insert(body, mediaContent).execute();
				System.out.println("File was uploaded successfully");
				
				RemoteFile uploadedFile = new DriveFile(file, this);
				return uploadedFile;

			} catch (IOException e) {
				System.out.println("An error occured: " + e);
				return null;
			}
		}
		
	}
}
