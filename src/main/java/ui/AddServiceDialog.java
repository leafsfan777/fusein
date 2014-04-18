package ui;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.RemoteDrive;
import main.RemoteDriveFactory;
import main.RemoteDriveStore;
import main.Services;

/**
 * Dialog box for adding remote services
 *
 */
public class AddServiceDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** 
	 * Parent Frame
	 */
	MainWindow frame;
	
	/**
	 * List of current RemoteDrives
	 */
	RemoteDriveStore remoteDrives;
	
	/**
	 * Factory object for creating RemoteDrives
	 */
	RemoteDriveFactory factory;
	
	/**
	 * Display a dialog that allows a user to add a service.
	 * @param frame The parent frame of the dialog.
	 * @param remoteDrives List of current RemoteDrives.
	 * @param factory Object used to create instances of RemoteDrive.
	 */
	public AddServiceDialog(MainWindow frame, RemoteDriveStore remoteDrives,
			RemoteDriveFactory factory) {
		super(new BorderLayout());
		this.frame = frame;
		this.remoteDrives = remoteDrives;
		this.factory = factory;
		initAddService();
	}
	
	/** 
	 * Implementation for AddServiceDialog.
	 */
	public final void initAddService() {
		// Add dropdown list for user to select service.
		// Get list of available services from the Service enum
		int serviceCount = Services.values().length;
		Object[] serviceChoices = new Object[serviceCount];
		
		// Set service title to a more readable name.
		for (int i = 0; i < serviceCount; i++) {
			serviceChoices[i] = Services.values()[i].getName();
		}
		String title = "Add a service";
		String directions = "Select the service to add:";
		String choice = (String) JOptionPane.showInputDialog(
				frame, 
				directions,
				title,
				JOptionPane.PLAIN_MESSAGE, 
				null, 
				serviceChoices, 
				serviceChoices[0]);
		
		// If the choice is null, the user clicked cancel and we can exit.
		if (choice == null) {
			return;
		} else {
			// If we're here, the user has selected a service. We must decide 
			// now what type of object to create, and then run the authenticate
			// method for that service.
			// Our correct remoteDrive object.
			RemoteDrive mDrive = factory.createRemoteDrive(choice);
			
			// Open a new dialog to continue the process.
			System.out.println("Clicked Ok with value " + choice);
			AuthServiceDialog asd = new AuthServiceDialog(choice, mDrive, remoteDrives);
			asd.setVisible(true);
			return;
		}
	}
}
