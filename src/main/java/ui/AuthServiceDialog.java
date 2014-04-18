package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import main.RemoteDrive;
import main.RemoteDriveStore;

public class AuthServiceDialog extends JPanel implements ActionListener,
		KeyListener {
	private static final long serialVersionUID = 1L;

	/** 
	 * Textual representation of the service
	 */
	private String serviceName;
	
	/** 
	 * The RemoteDrive to be authenticated
	 */
	private RemoteDrive mDrive;
	
	/**
	 * The state of the authentication operation
	 */
	boolean success = false;
	
	/**
	 * List of current RemoteDrives
	 */
	private RemoteDriveStore remoteDrives;

	/**
	 * The window object for displaying the authentication service dialog
	 */
	JDialog authDiagWindow = new JDialog();
	
	/**
	 * Root panel for AuthServiceDialog
	 */
	JPanel panel;
	
	/**
	 * Button to go to service authentication website
	 */
	JButton goToSiteBtn;
	
	/**
	 * Label describing 
	 */
	JLabel topLbl;
	
	/**
	 * Label describing first step in authentication process.
	 */
	JLabel stepOneLbl;
	
	/**
	 * Label describing second step in authentication process
	 */
	JLabel stepTwoLbl;

	/**
	 * Input field for authentication token
	 */
	JTextField codeInput;

	/**
	 * Authentication dialog for authenticating with RemoteDrives
	 * @param serviceName Textual representation of the service
	 * @param mDrive RemoteDrive object being authenticated
	 * @param remoteDrives List of current RemoteDrives
	 */
	public AuthServiceDialog(String serviceName, RemoteDrive mDrive, RemoteDriveStore remoteDrives) {
		super(new BorderLayout());
		this.serviceName = serviceName;
		this.mDrive = mDrive;
		this.remoteDrives = remoteDrives;
		initAuthService();
	}

	/**
	 * Create and show authentication dialog 
	 */
	public final void initAuthService() {

		// New panel for the items, set the layout and the borders.
		// Borders used to align things.
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		EmptyBorder regBorder = new EmptyBorder(10, 10, 10, 10);
		Font btnFont = new Font("Sans Serif", Font.BOLD, 16);

		// Show top label.
		topLbl = new JLabel("<html>FuseIn cannot see your files yet - "
				+ "you must authenticate your account first.</html>");
		topLbl.setFont(btnFont);
		topLbl.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Show step one directions to user.
		stepOneLbl = new JLabel(
				"<html><b>1.</b> Click the button below.  You will "
						+ "be taken to the " + serviceName + " website.<br>"
						+ "You may need to login - don't worry, we "
						+ "can't see your password.<br></html>");
		stepOneLbl.setBorder(regBorder);

		// Display button for user to go to authorization site.
		goToSiteBtn = new JButton("Go to " + serviceName);
		goToSiteBtn.setFont(btnFont);
		goToSiteBtn.setPreferredSize(new Dimension(70, 40));
		goToSiteBtn.addActionListener(this);
		goToSiteBtn.setBorder(regBorder);

		// Show directions for step two of authentication process.
		stepTwoLbl = new JLabel(
				"<html><br><b>2.</b> Press \"Allow\" and copy the code "
						+ "you are given into the box below.<br>Press enter to "
						+ "authenticate and finish the process.<br><br></html>");
		stepTwoLbl.setBorder(regBorder);

		// Input field for the auth code.
		codeInput = new JTextField();
		codeInput.setText("Paste your code here");
		codeInput.setPreferredSize(new Dimension(70, 40));
		codeInput.addKeyListener(this);
		codeInput.setBorder(regBorder);
		codeInput.setEnabled(false);

		// Add all components to panel.
		panel.add(topLbl);
		panel.add(stepOneLbl);
		panel.add(goToSiteBtn);
		panel.add(stepTwoLbl);
		panel.add(codeInput);
		
		// Configure the window.
		authDiagWindow.setTitle("Authenticate Account");
		authDiagWindow.setPreferredSize(new Dimension(450, 400));
		authDiagWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		authDiagWindow.add(panel, BorderLayout.NORTH);
		authDiagWindow.pack();

		// Center the window.
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = scrSize.width / 2 - authDiagWindow.getWidth() / 2;
		int y = scrSize.height / 2 - authDiagWindow.getHeight() / 2;
		authDiagWindow.setLocation(x, y);
		
		// Show the dialog.
		authDiagWindow.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == goToSiteBtn) {
			URLMethodWorker mw = new URLMethodWorker();
			mw.execute();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			String token = codeInput.getText().trim();
			codeInput.setText("");
			FinalMethodWorker mw = new FinalMethodWorker(token);
			mw.execute();

			boolean succ;
			int res;
			try {
				succ = mw.get();
				if (succ) {
					res = JOptionPane
							.showConfirmDialog(authDiagWindow, "Authentication successful!",
									"Authentication Success",
									JOptionPane.PLAIN_MESSAGE);
				} else {
					res = JOptionPane
							.showConfirmDialog(authDiagWindow, "Authentication failed..."
									+ "Try again.", "Authentication Failure",
									JOptionPane.ERROR_MESSAGE);
				}

				if (res == JOptionPane.YES_OPTION) {
					// Add new authenticated drive to services window.
					remoteDrives.addDrive(mDrive);
					
					// Close dialogs.
					authDiagWindow.dispose();
				}
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}

		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	/**
	 * A class to perform URL generation for a RemoteDrives authentication in a dedicated thread
	 */
	private class URLMethodWorker extends SwingWorker<Boolean, Void> {

		@Override
		protected Boolean doInBackground() throws Exception {
			mDrive.generateAuthURL();
			return true;
		}

		@Override
		protected void done() {
			try {
				success = get();
				codeInput.setEnabled(true);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * A class to perform GUI-interacting sections of finalizing a RemoteDrive in a dedicated thread
	 *
	 */
	private class FinalMethodWorker extends SwingWorker<Boolean, Void> {
		private String token;

		/**
		 * Create a worker to finalize RemoteDrive authentication
		 * @param tok The authentication token.
		 */
		public FinalMethodWorker(String tok) {
			this.token = tok;
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			return mDrive.finalizeAuth(token);
		}

		@Override
		protected void done() {
		}

	}
}