
package ui;

import java.awt.BorderLayout;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import main.RemoteFile;

/**
 * A file dialog for downloading files from a RemoteDrive
 * 
 * @date March 17, 2014
 */
public class DownloadFileDialog extends JPanel {
	
	/**
	 * Serialization Identifier.
	 * Increment this any time the class's signature changes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The RemoteFile representing the file to be downloaded
	 */
	private RemoteFile fileToDownload;

	/**
	 * Create a dialog to download a file
	 * @param fileOwner RemoteFile representing the file to be downloaded
	 */
	public DownloadFileDialog(RemoteFile fileToDownload) {
		super(new BorderLayout());
		this.fileToDownload = fileToDownload;
		initDialog();
	}
	
	/**
	 * Initialize dialog and execute response
	 * 
	 */
	public void initDialog() {
		String in = JOptionPane.showInputDialog(this,
				"Enter a local path for the file (including name and extension):");
		if (in != null && in.length() > 0) {
			DownloadMethodWorker dwm = new DownloadMethodWorker(in);
			dwm.execute();

			boolean succ;
			int res;
			try {
				succ = dwm.get();
				if (succ) {
					res = JOptionPane.showConfirmDialog(this,
							"Download Successful!", "Download file success",
							JOptionPane.PLAIN_MESSAGE);
				} else {
					res = JOptionPane.showConfirmDialog(this,
							"Download failed..." + "Try again.",
							"Download file Failure", JOptionPane.ERROR_MESSAGE);
				}

				if (res == JOptionPane.YES_OPTION) {
					return;
				}
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * A class to perform the download method in a dedicated thread
	 *
	 */
	private class DownloadMethodWorker extends SwingWorker<Boolean, Void> {
		private String localPath;

		public DownloadMethodWorker(String localPath) {
			this.localPath = localPath;
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			return DownloadFileDialog.this.fileToDownload.download(this.localPath);
		}

		@Override
		protected void done() {
		}
	}
}
