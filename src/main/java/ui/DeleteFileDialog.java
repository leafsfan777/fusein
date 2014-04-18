
package ui;

import java.awt.BorderLayout;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import main.RemoteFile;

/**
 * A file dialog for deleting files from a RemoteDrive
 * 
 * @date March 17, 2014
 */
public class DeleteFileDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The RemoteFile that owns the file to be deleted
	 */
	private RemoteFile fileToDelete;
	
	/**
	 * The model of the file listing on the main window.
	 */
	private DefaultListModel<RemoteFile> model;
	
	/**
	 * Create a dialog for RemoteFile deletion
	 * @param fileOwner The RemoteFile to be deleted
	 */
	public DeleteFileDialog(RemoteFile fileToDelete, DefaultListModel<RemoteFile> model) {
		super(new BorderLayout());
		this.fileToDelete = fileToDelete;
		this.model = model;
		initDialog();
	}

	/**
	 * Display the delete file dialog and execute the action
	 */
	private void initDialog() {
		int res = JOptionPane.showConfirmDialog(this,
				String.format("Are you sure you want to delete the file \"%s\" from your %s account?",
						      this.fileToDelete.getPath(), this.fileToDelete.getRemoteDrive().getServiceNiceName()));
		if (res == JOptionPane.YES_OPTION) {
			DelMethodWorker dmw = new DelMethodWorker(this.fileToDelete);
			dmw.execute();
			boolean succ;
			int delResult;
			
			try {
				succ = dmw.get(); //TODO: This will block until the file is deleted!
				if (succ) {
					//TODO
					// I think this should be done by callback, but I
					// can't get it to work so I've passed in the
					// model to this dialog... -Cam
					model.removeElement(fileToDelete);
					
					delResult = JOptionPane.showConfirmDialog(this,
							"File had been deleted.", "Delete file success",
							JOptionPane.PLAIN_MESSAGE);
				} else {
					delResult = JOptionPane.showConfirmDialog(this,
							"Delete file failed..." + "Try again.",
							"Delete file failure", JOptionPane.ERROR_MESSAGE);
				}

				if (delResult == JOptionPane.YES_OPTION) {
					return;
				}
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	/**
	 * A class to perform the delete method in a dedicated thread
	 *
	 */
	private class DelMethodWorker extends SwingWorker<Boolean, Void> {
		public RemoteFile fileToDelete;
		
		public DelMethodWorker(RemoteFile fileToDelete) {
			super();
			
			this.fileToDelete = fileToDelete;
		}
		
		@Override
		protected Boolean doInBackground() {
			return this.fileToDelete.delete();
		}

		@Override
		protected void done() {
		}
	}
}
