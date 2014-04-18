
package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ui.MainWindow;

/**
 * Contains the main application entry point. Schedules the main
 * window to be spawned by the event handler thread.
 * 
 * @author Ryan K
 * @date March 04, 2014
 */
public class Fusein
{
	/**
	 * Application entry point.
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args)
	{
		final RemoteDriveStore driveStore = new RemoteDriveStore();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				initGUI(driveStore);
			}
		});
		
		// Load the configuration now, so the file list will populate.
		driveStore.loadFromFile("conf.properties");
	}
	
	/**
	 * Initializes the graphical user interface by spawning
	 * and displaying the main window.
	 */
	private static void initGUI(RemoteDriveStore driveStore)
	{
		System.out.println("Initializing GUI...");
		
		// Try to make the interface look familiar
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			System.err.println("Couldn't set system theme!");
			System.err.println("Falling back on cross-platform theme.");
		}
		
		// Spawn and display the main window
		JFrame mainWindow = new MainWindow(driveStore);
		mainWindow.setVisible(true);
	}
}
