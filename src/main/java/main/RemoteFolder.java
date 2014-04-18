
package main;

import java.util.List;

/**
 * Provides a blueprint for service handlers to expose folders.
 * 
 * @author Ryan K
 * @date March 17, 2014
 */
public interface RemoteFolder extends RemoteEntry
{
	/**
	 * Get the folder this folder is a descendant of.
	 * 
	 * @return The RemoteFolder this folder is a subfolder of.
	 */
	public RemoteFolder getParent();
	
	/**
	 * Get the RemoteDrive associated with the cloud this folder exists on.
	 * 
	 * @return The associated RemoteDrive.
	 */
	public RemoteDrive getRemoteDrive();
	
	/**
	 * Get the files and folders inside of this folder.
	 */
	public List<RemoteEntry> getEntries();
	
	/**
	 * Upload the specified file to this remote folder.
	 * 
	 * @param localPath The full file path (including filename) to the file
	 *                  to be uploaded to this remote folder.
	 * @return A RemoteFile object representing the file on the remote drive,
	 *         or null if the file was not uploaded successfully.
	 */
	public RemoteFile uploadFile(String localPath);
}
