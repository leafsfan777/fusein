
package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.DriveStoreEventListener;
import main.RemoteDrive;
import main.RemoteDriveStore;

/**
 * A Window class used to display a list of services
 *
 */
public class ServiceListWindow extends JFrame implements WindowListener, DriveStoreEventListener
{
	/**
	 * Serialization identifier.
	 * Increment any time the class signature changes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * List of current RemoteDrives
	 */
	private RemoteDriveStore remoteDrives;
	
	/**
	 * A representation of RemoteDrives for display
	 */
	private DefaultListModel<RemoteDrive> driveListModel;
	
	/**
	 * Create a window to display a list of the users services
	 * @param driveStore A list of the users RemoteDrives
	 */
	public ServiceListWindow(RemoteDriveStore driveStore)
	{
		super();
		
		this.remoteDrives = driveStore;
		
		this.setTitle("Registered Cloud Services");
		this.setSize(400, 500);
		
		// Center the window
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = scrSize.width / 2 - this.getWidth() / 2;
		int y = scrSize.height / 2 - this.getHeight() / 2;
		this.setLocation(x, y);

		// Free resources when the window is closed
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// Use a reasonable layout manager
		this.setLayout(new BorderLayout());

		// ==== Service List ====
		this.driveListModel = new DefaultListModel<>();
		final JList<RemoteDrive> driveList = new JList<>(driveListModel);
		
		Iterator<RemoteDrive> it = this.remoteDrives.iterator();
		while (it.hasNext()) {
			this.driveListModel.addElement(it.next());
		}
		
		driveList.setCellRenderer(new DriveCellRenderer());
		driveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		driveList.setSelectedIndex(0);
		
		JScrollPane driveListView = new JScrollPane(driveList);
		this.add(driveListView, BorderLayout.CENTER);
		
		// ==== Buttons ====
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2, 3, 3));
		
		final JButton cmdRemove = new JButton("Remove");
		cmdRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				RemoteDrive drive = driveList.getSelectedValue();
				if (drive != null) {
					ServiceListWindow.this.remoteDrives.removeDrive(drive);
				}
			}
		});
		cmdRemove.setEnabled(driveList.getSelectedValue() != null);
		buttonPanel.add(cmdRemove);
		
		JButton cmdClose = new JButton("Close");
		cmdClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				ServiceListWindow.this.dispose();
			}
		});
		buttonPanel.add(cmdClose);
		
		this.add(buttonPanel, BorderLayout.PAGE_END);
		
		driveList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event)
			{
				cmdRemove.setEnabled(driveList.getSelectedValue() != null);
			}
		});
		
		this.remoteDrives.addEventListener(this);
		this.addWindowListener(this);
	}

	@Override
	public void windowActivated(WindowEvent event) {}

	@Override
	public void windowClosed(WindowEvent event)
	{
		this.remoteDrives.removeEventListener(this);
	}

	@Override
	public void windowClosing(WindowEvent event) {}

	@Override
	public void windowDeactivated(WindowEvent event) {}

	@Override
	public void windowDeiconified(WindowEvent event) {}

	@Override
	public void windowIconified(WindowEvent event) {}

	@Override
	public void windowOpened(WindowEvent event) {}

	@Override
	public void driveAdded(RemoteDrive drive)
	{
		this.driveListModel.addElement(drive);
	}

	@Override
	public void driveRemoved(RemoteDrive drive)
	{
		this.driveListModel.removeElement(drive);
	}
	
	/**
	 * A Renderer for a single cell of the ServiceListWindow
	 *
	 */
	private static class DriveCellRenderer extends JLabel implements ListCellRenderer<RemoteDrive>
	{
		/**
		 * Serialization identifier.
		 * Increment any time the class signature changes.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * The color of the cell when selected
		 */
		private static final Color selectedColor = new Color(230, 230, 250);
		
		/**
		 * The color of the cell when in focus
		 */
		private static final Color focusColor = new Color(202, 225, 255);
		
		/**
		 * An Icon Representing the service
		 */
		ImageIcon icon;
		
		/**
		 * Create a default DriveCellRenderer
		 */
		public DriveCellRenderer()
		{
			super();
			
			this.icon = new ImageIcon(ClassLoader.getSystemResource("service_icons/dropbox.png"));
		}

		@Override
		public Component getListCellRendererComponent(
				JList<? extends RemoteDrive> list, RemoteDrive drive,
				int index, boolean isSelected, boolean hasFocus) {
			this.setText(String.format("%s's %s", drive.getUsername(), drive.getServiceNiceName()));
			this.setIcon(this.icon);
			
			Color background = Color.WHITE;
			
			if (isSelected) {
				background = hasFocus ? focusColor : selectedColor;
			}
			
			this.setBackground(background);
			this.setOpaque(true);
			
			return this;
		}
	}
}
