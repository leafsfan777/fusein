
package main;

/**
 * Provides a blueprint for service handlers to expose files and folders.
 * 
 * @author Ryan K
 * @date March 07, 2014
 */
public interface RemoteEntry
{
	/**
	 * Get the basename of the entry.
	 * 
	 * @return The name of the entry (without the path).
	 */
	public String getName();
	
	/**
	 * Get the full path of the entry.
	 * 
	 * @return The full path of the entry.
	 */
	public String getPath();
	
	/**
	 * Get the RemoteDrive associated with the cloud this folder exists on.
	 * 
	 * @return The associated RemoteDrive.
	 */
	public RemoteDrive getRemoteDrive();
	
	/**
	 * Is this entry a folder?
	 * 
	 * @return True if this entry is a folder.
	 */
	public boolean isFolder();

	/**
	 * Is this entry a file?
	 * 
	 * @return True if this entry is a file.
	 */
	public boolean isFile();

	/**
	 * Gives a RemoteFolder instance of this object if it is a folder.
	 * 
	 * @return A RemoteFolder instance, or null if this entry is not a folder (i.e. it is a file).
	 */
	public RemoteFolder asFolder();

	/**
	 * Gives a RemoteFile instance of this object if it is a file.
	 * 
	 * @return A RemoteFile instance, or null if this entry is not a file (i.e. it is a folder).
	 */
	public RemoteFile asFile();
}
