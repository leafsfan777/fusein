
package main;

/**
 * Provides a blueprint for service handlers to expose files.
 * 
 * @author Ryan K
 * @date March 06, 2014
 */
public interface RemoteFile extends RemoteEntry
{
	/**
	 * Get the folder this file is stored in.
	 * 
	 * @return The RemoteFolder this file is stored in, or null if it is stored in the root.
	 */
	public RemoteFolder getFolder();
	
	/**
	 * Get a sharable URL to this file.
	 * 
	 * @return A sharable web link to the file.
	 */
	public String getLink();
	
	/**
	 * Download this file.
	 * 
	 * @param localPath The local filepath (including filename) to download to.
	 *                  e.g. /home/carol/Pictures/funny_cat_haha.jpg
	 * @return True if the file was downloaded successfully, false otherwise.
	 */
	public boolean download(String localPath);
	
	/**
	 * Delete this file from the remote folder.
	 * 
	 * @return True if the file was deleted successfully, false otherwise.
	 */
	public boolean delete();
}
