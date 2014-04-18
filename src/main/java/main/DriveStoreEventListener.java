
package main;

/**
 * Provides a blueprint for functions that are interested in what happens
 * to the collection of RemoteDrives.
 * 
 * @author Ryan K
 * @date March 05, 2014
 */
public interface DriveStoreEventListener
{
	/**
	 * Called when a drive is added to the store.
	 * 
	 * @param drive The drive that was added.
	 */
	public void driveAdded(RemoteDrive drive);
	
	/**
	 * Called when a drive is removed from the store.
	 * 
	 * @param drive The drive that was removed.
	 */
	public void driveRemoved(RemoteDrive drive);
}
