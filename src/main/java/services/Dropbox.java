package services;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import main.RemoteDrive;
import main.RemoteEntry;
import main.RemoteFile;
import main.RemoteFolder;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;

/**
 * Provides an implementation of the basic cloud storage functionality for a
 * Dropbox account. All functionality will require a proper authentication token
 * to be issued by the Dropbox authentication server.
 * 
 * @author Fusein
 * @date March 17, 2014
 * @version 2.0
 */
public class Dropbox implements RemoteDrive {
	private String appKey;
	private String appSecret;
	private String clientIdentifier;
	private String userLocale;
	private String authToken;
	private DbxClient client;
	private DbxAccountInfo info;
	private DbxWebAuthNoRedirect webAuth; 
	private DbxAppInfo appInfo;
	private DbxRequestConfig requestConfig;

	/**
	 * Default constructor to initialize data
	 */
	public Dropbox() {
		this.appKey = "7qtdwzgsnts24wm";
		this.appSecret = "5dbvc8pxaxn6d61";
		this.clientIdentifier = "fusein/0.1";
		this.userLocale = Locale.getDefault().toString();
	}
	
	@Override
	public String getServiceNiceName()
	{
		return "Dropbox";
	}
	
	@Override
	public String generateAuthURL() {
		// Confirm app as registered by Dropbox.
		appInfo = new DbxAppInfo(appKey, appSecret);
		
		 // Defaults to locale: English.
		requestConfig = new DbxRequestConfig(clientIdentifier, userLocale);
		webAuth = new DbxWebAuthNoRedirect(requestConfig, appInfo);
		
		// Fetch the authorization authorization url properly.
		String authorizeUrl = webAuth.start(); 
		System.out.println("----AUTHENTICATE: DROPBOX----");
		openAuthLink(authorizeUrl);
		
		return null;
	}
	
	@Override
	public boolean finalizeAuth(String authKey) {
		DbxAuthFinish authFinish = null;
		try {
			authFinish = webAuth.finish(authKey);
		} catch (DbxException e) {
			System.out.println("Unable to authorize. Please enter a valid key.");
			return false;
		}
		
		this.authToken = authFinish.accessToken;
		DbxRequestConfig requestConfig = new DbxRequestConfig(clientIdentifier, userLocale);
		this.client = new DbxClient(requestConfig, this.authToken);
		try {
			this.info = this.client.getAccountInfo();
		} catch (DbxException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	@Override
	public String getUsername()
	{
		return this.info.displayName;
	}
	
	@Override
	public String getAuthToken()
	{
		return this.authToken;
	}
		
	@Override
	public void setAuthToken(String newToken)
	{
		this.authToken = newToken;
		
		// Get our new client object
		DbxRequestConfig requestConfig = new DbxRequestConfig(clientIdentifier, userLocale);
		this.client = new DbxClient(requestConfig, this.authToken);
		try {
			this.info = this.client.getAccountInfo();
		} catch (DbxException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public RemoteFolder getRootFolder() {
		try {
			return new DropboxFolder(this.client.getMetadata("/").asFolder(), null);
		} catch (DbxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Opens the Dropbox account authorization link in the user's
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
		return appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public String getClientIdentifier() {
		return clientIdentifier;
	}
	
	/**
	 * A file on the Dropbox remote server.
	 * 
	 * @author Ryan K
	 * @date March 17, 2014
	 */
	public class DropboxFile implements RemoteFile {
		private RemoteFolder parent;
		private DbxEntry.File dbxFile;
		
		public DropboxFile(DbxEntry.File dbxFile, RemoteFolder parent) {
			this.parent = parent;
			this.dbxFile = dbxFile;
		}
		
		@Override
		public String getName() {
			return this.dbxFile.name;
		}
		
		@Override
		public String getPath() {
			return this.dbxFile.path;
		}
		
		@Override
		public String getLink() {
			try {
				return Dropbox.this.client.createShareableUrl(this.getPath());
			} catch (DbxException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public RemoteDrive getRemoteDrive() {
			return Dropbox.this;
		}
		
		@Override
		public RemoteFolder getFolder() {
			return this.parent;
		}

		@Override
		public boolean download(String localPath) {
			FileOutputStream outputStream = null;

			// Retrieve client, open file stream.
			try {
				outputStream = new FileOutputStream(localPath);
				// Fully write the file to local system.
				Dropbox.this.client.getFile(this.getPath(), null, outputStream);
			} catch (DbxException | IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return true;
		}

		@Override
		public boolean delete() {
			try {
				Dropbox.this.client.delete(this.getPath());
			} catch (DbxException e) {
				e.printStackTrace();
				return false;
			}

			return true;
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
	}
	
	/**
	 * A folder on the Dropbox remote server.
	 * 
	 * @author Ryan K
	 * @date March 17, 2014
	 */
	public class DropboxFolder implements RemoteFolder {
		private RemoteFolder parent;
		private DbxEntry.Folder dbxFolder;
		
		public DropboxFolder(DbxEntry.Folder dbxFolder, RemoteFolder parent) {
			this.parent = parent;
			this.dbxFolder = dbxFolder;
		}
		
		@Override
		public String getName() {
			return this.dbxFolder.name;
		}
		
		@Override
		public String getPath() {
			return this.dbxFolder.path;
		}

		@Override
		public RemoteDrive getRemoteDrive() {
			return Dropbox.this;
		}
		
		@Override
		public RemoteFolder getParent() {
			return this.parent;
		}

		@Override
		public List<RemoteEntry> getEntries() {
			DbxEntry.WithChildren listing = null;

			// Now get the file listing information from the account.
			try {
				listing = Dropbox.this.client.getMetadataWithChildren(this.getPath());
			} catch (DbxException e) {
				e.printStackTrace();
				return null;
			}
			
			ArrayList<RemoteEntry> entries = new ArrayList<>();
			
			for (DbxEntry child : listing.children) {
				if (child.isFile()) {
					entries.add(new DropboxFile(child.asFile(), this));
				} else {
					entries.add(new DropboxFolder(child.asFolder(), this));
				}
			}
			
			return entries;
		}
		
		@Override
		public RemoteFile uploadFile(String localPath) {
			FileInputStream inputStream = null;

			// Retrieve the specified file from the local system.
			File inputFile = new File(localPath);
			try {
				inputStream = new FileInputStream(inputFile);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			// Now upload it to the authenticated Dropbox account.
			try {
				DbxEntry.File uploadedFile = Dropbox.this.client.uploadFile(this.getPath() + "/" + inputFile.getName(), DbxWriteMode.add(),
                                                                            inputFile.length(), inputStream);
				return new DropboxFile(uploadedFile, this);
			} catch (IOException | DbxException e) {
				e.printStackTrace();
				return null;
			}
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
	}
}
