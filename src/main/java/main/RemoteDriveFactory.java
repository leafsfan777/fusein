package main;

import services.Dropbox;
import services.GDrive;

/**
 * Handles the creation of RemoteDrive objects.
 * 
 * @author FuseIn
 * @version 1.0
 * @date March 04 2014
 */
public class RemoteDriveFactory {

	/**
	 * Return a new instance of a RemoteDrive. This will be dependent on the
	 * type desired by the user.
	 * 
	 * @param type
	 *            Which type of remote drive to create.
	 * @return A new instance of a RemoteDrive, or null if the type is not
	 *         supported.
	 */
	public RemoteDrive createRemoteDrive(String type) {
		//TODO: Violation of the open/closed principle--fix this later
		if (type.equals("Dropbox")) {
			return new Dropbox();
		}
		//TODO This now fails to implement the proper interface.
		if (type.equals("Google Drive")) {
			return new GDrive();
		}

		return null;
	}
}
