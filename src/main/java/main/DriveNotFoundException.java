
package main;

/**
 * Thrown when a RemoteDriveStore is asked to deal with a drive it does not store.
 * 
 * @author Ryan K
 * @date March 05, 2014
 */
public class DriveNotFoundException extends RuntimeException
{
	/**
	 * Serialization identifier.
	 * Increment any time the class signature changes.
	 */
	private static final long serialVersionUID = 1L;

	public DriveNotFoundException(String message)
	{
		super(message);
	}

	public DriveNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
