package main;

/**
 * Contains all the remote drive services available for a user to add to Fusein.
 * Stores the drive as a constant, as well as a human-readable string for a
 * better UI experience.
 * 
 * @author Cam
 * @date March 17 2014
 * @version 1.0
 * 
 */
public enum Services {
	// Add any available services here.
	// Use the format: CONSTANT_NAME("UI readable name")
	DROPBOX("Dropbox"), GOOGLE_DRIVE("Google Drive");

	String name;

	private Services(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
