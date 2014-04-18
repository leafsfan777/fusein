
package main;

/**
 * Interface for methods interested in file downloads.
 * 
 * @author Ryan K
 * @date March 06, 2014
 */
public interface RemoteFileDownloadMonitor
{
	public void progressUpdate(RemoteFile file, int bytesNow, int bytesMax);
	
	public void downloadComplete(RemoteFile file);
	
	public void downloadFailed(RemoteFile file);
}
