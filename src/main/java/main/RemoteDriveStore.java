
package main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Maintains a set of remote drives.
 * 
 * @author Ryan K
 * @date March 05, 2014
 */
public class RemoteDriveStore
{
	private ArrayList<RemoteDrive> remoteDrives;
	private ArrayList<DriveStoreEventListener> listeners;
	
	/**
	 * Initialize an empty RemoteDriveStore.
	 */
	public RemoteDriveStore()
	{
		remoteDrives = new ArrayList<RemoteDrive>();
		listeners = new ArrayList<DriveStoreEventListener>();
	}
	
	/**
	 * Add a drive to the set.
	 * 
	 * @param drive The drive to add to the set.
	 * @return The session ID of the added drive, or -1 if the drive was already in the set.
	 */
	public int addDrive(RemoteDrive drive)
	{
		if (this.remoteDrives.contains(drive)) {
			return -1;
		}
		
		this.remoteDrives.add(drive);
		
		Iterator<DriveStoreEventListener> it = this.listeners.iterator();
		while (it.hasNext()) {
			it.next().driveAdded(drive);
		}
		
		System.out.printf("Added provider \"%s\"\n", drive.getServiceNiceName());
		
		return this.remoteDrives.indexOf(drive);
	}
	
	/**
	 * Remove a drive from the set.
	 * 
	 * @param drive The drive to remove from the set
	 * @return True if the drive was in the set (and has now been removed), false otherwise.
	 * @throws DriveNotFoundException if the given RemoteDrive is not stored.
	 */
	public void removeDrive(RemoteDrive drive)
	{
		if (!this.remoteDrives.remove(drive)) {
			throw new DriveNotFoundException("RemoteDrive not stored!");
		}
		
		Iterator<DriveStoreEventListener> it = this.listeners.iterator();
		while (it.hasNext()) {
			it.next().driveRemoved(drive);
		}
		
		System.out.printf("Removed provider \"%s\"\n", drive.getServiceNiceName());
	}
	
	/**
	 * Remove a drive from the set.
	 * 
	 * @param sessionID The session ID of the drive.
	 * @return The RemoteDrive that has been removed.
	 * @throws DriveNotFoundException if there is no stored drive with the given session ID.
	 */
	public RemoteDrive removeDriveById(int sessionID)
	{
		try {
			RemoteDrive removedDrive = this.remoteDrives.remove(sessionID);
			
			Iterator<DriveStoreEventListener> it = this.listeners.iterator();
			while (it.hasNext()) {
				it.next().driveRemoved(removedDrive);
			}
			
			System.out.printf("Removed provider \"%s\"\n", removedDrive.getServiceNiceName());
			
			return removedDrive;
		} catch (IndexOutOfBoundsException ex) {
			throw new DriveNotFoundException(String.format("RemoteDrive with session ID %d not stored!", sessionID), ex);
		}
	}
	
	/**
	 * Get a RemoteDrive by its session ID.
	 * 
	 * @param sessionID The session ID of the drive.
	 * @return The drive with the given session ID.
	 * @throws DriveNotFoundException if there is no stored drive with the given session ID.
	 */
	public RemoteDrive getDriveById(int sessionID)
	{
		try {
			return this.remoteDrives.get(sessionID);
		} catch (IndexOutOfBoundsException ex) {
			throw new DriveNotFoundException(String.format("RemoteDrive with session ID %d not stored!", sessionID), ex);
		}
	}
	
	public ArrayList<RemoteDrive> getAllDrives() {
		return remoteDrives;
	}
	
	public void addEventListener(DriveStoreEventListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeEventListener(DriveStoreEventListener listener)
	{
		this.listeners.remove(listener);
	}
	
	public Iterator<RemoteDrive> iterator()
	{
		//TODO wrap this so we're notified if the user calls Iterator.remove()
		return this.remoteDrives.iterator();
	}
	
	public void saveToFile(String path)
	{
		Properties properties = new Properties();
		
		Iterator<RemoteDrive> it = this.remoteDrives.iterator();
		int i = 0;
		while (it.hasNext()) {
			RemoteDrive drive = it.next();
			properties.setProperty("service" + i, String.format("%s:%s", drive.getServiceNiceName(), drive.getAuthToken()));
			i++;
		}
		
		try {
			FileWriter writer = new FileWriter(path);
			properties.store(writer, "Authentication Tokens");
			writer.close();
		} catch (IOException e) {
			System.err.println("Failed to write " + path);
		}
	}
	
	public void loadFromFile(String path)
	{
		FileReader reader;
		Properties properties = new Properties();
		
		try {
			reader = new FileReader(path);
			properties.load(reader);
			reader.close();
		} catch (IOException e) {
			System.err.println("Failed to read " + path);
			return;
		}
		
		RemoteDriveFactory rdFactory = new RemoteDriveFactory();
		
		Iterator<?> it = properties.values().iterator();
		while (it.hasNext()) {
			String line = (String)it.next();
			int sepIndex = line.indexOf(':');
			
			if (sepIndex == -1) {
				System.err.printf("Malformed provider definition \"%s\"!\n", line);
				continue;
			}
			
			String driveType = line.substring(0, sepIndex);
			String authToken = line.substring(sepIndex + 1);
			
			System.out.printf("Restoring %s provider from file...\n", driveType);
			
			RemoteDrive drive = rdFactory.createRemoteDrive(driveType);
			if (drive == null) {
				System.err.printf("Bad provider name \"%q\"!\n", driveType);
				continue;
			}
			
			drive.setAuthToken(authToken);
			this.addDrive(drive);
		}
	}
}
