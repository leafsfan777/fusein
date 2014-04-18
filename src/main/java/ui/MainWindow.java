
package ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import main.DriveStoreEventListener;
import main.RemoteDrive;
import main.RemoteDriveFactory;
import main.RemoteDriveStore;
import main.RemoteEntry;
import main.RemoteFile;
import main.RemoteFolder;
import ui.FolderTree.FolderTreeNode;

/**
 * The topmost level interface for Fusein.
 * 
 * @author Ryan K
 * @date March 17, 2014
 */
public class MainWindow extends JFrame implements WindowListener, DriveStoreEventListener
{
	/**
	 * Serialization identifier.
	 * Increment any time the class signature changes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Used to keep track of the current set of remote drives.
	 */
	private RemoteDriveStore remoteDrives;
	
	
	/**
	 * The RemoteDriveFactory object used in the UI to 
	 * create RemoteDrives
	 */
	private RemoteDriveFactory factory;
	
	/**
	 * The windows top menu bar
	 */
	private JMenuBar menuBar;
	
	/**
	 * A Toolbar for actions on files and folders
	 */
	private JToolBar toolBar;
	
	/**
	 * A Displayable folder hierarchy for RemoteFolders
	 */
	private FolderTree folderTree;
	
	/**
	 * Used to display status information about operations
	 */
	private JPanel statusBar;
	
	/**
	 * A vertical listing of files.
	 */
	private FileList fileList;
	
	/**
	 * A model of RemoteFiles
	 */
	private DefaultListModel<RemoteFile> fileListModel;

	/**
	 * Button used to upload a file to a RemoteDrive
	 */
	private JButton cmdUpload;
	
	/**
	 * Create a MainWindow to display a list of RemoteDrives
	 * @param driveStore The RemoteDrives to display
	 */
	public MainWindow(RemoteDriveStore driveStore)
	{
		super();
		
		this.factory = new RemoteDriveFactory();
		this.remoteDrives = driveStore;
		
		// Set defaults
		this.setTitle("Fusein");
		this.setSize(800, 600); //TODO: Size to a scale of the current resolution.
		
		// Center the window
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = scrSize.width / 2 - this.getWidth() / 2;
		int y = scrSize.height / 2 - this.getHeight() / 2;
		this.setLocation(x, y);
		
		// We'll handle closing ourselves.
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// Use a reasonable layout manager
		this.setLayout(new BorderLayout());
		
		// ==== Menu Bar ====
		this.menuBar = new JMenuBar();
		
		// == File ==
		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuFile.getAccessibleContext().setAccessibleDescription("Operations on Files");
		
		JMenuItem cmdQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
		cmdQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				MainWindow.this.dispatchEvent(new WindowEvent(MainWindow.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		menuFile.add(cmdQuit);
		
		this.menuBar.add(menuFile);
		
		/*// == Options ==
		JMenu menuOptions = new JMenu("Options");
		menuOptions.setMnemonic(KeyEvent.VK_O);
		menuOptions.getAccessibleContext().setAccessibleDescription("Program Settings");
		this.menuBar.add(menuOptions);*/
		
		//TODO
		// WOW LOOK AT ALL OF THESE SETTINGS
		// I CAN'T EVEN BELIEVE THE CONFIGURABILITY
		// OF THIS FINE APPLICATION
		
		this.setJMenuBar(this.menuBar);
		
		// ==== Tool Bar ====
		this.toolBar = new JToolBar();
		this.toolBar.setFloatable(false);
		
		final JButton cmdOpen = new JButton("Open");
		cmdOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				RemoteFile fileToOpen = ((RemoteFile) fileList.getSelectedValue());
				OpenMethodWorker omw = new OpenMethodWorker(fileToOpen);
				omw.execute();
			}
		});
		cmdOpen.setEnabled(false);
		this.toolBar.add(cmdOpen);
		
		this.cmdUpload = new JButton("Upload");
		this.cmdUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				FolderTreeNode node = (FolderTreeNode)MainWindow.this.folderTree.getLastSelectedPathComponent();
				if (node == null) {
					return;
				}
				RemoteFolder folder = node.getFolder();
				
				UploadFileDialog ufd = new UploadFileDialog(remoteDrives, folder);
				ufd.setVisible(true);
			}
		});
		this.cmdUpload.setEnabled(false);
		this.toolBar.add(this.cmdUpload);
		
		final JButton cmdDownload = new JButton("Download");
		cmdDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				RemoteFile fileOwner = ((RemoteFile)fileList.getSelectedValue());
				DownloadFileDialog dfd = new DownloadFileDialog(fileOwner);
				dfd.setVisible(true);
			}
		});
		cmdDownload.setEnabled(false);
		this.toolBar.add(cmdDownload);
		
		final JButton cmdDelete = new JButton("Delete");
		cmdDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				RemoteFile fileOwner = ((RemoteFile) fileList.getSelectedValue());
				DeleteFileDialog dfd = new DeleteFileDialog(fileOwner, fileListModel);
				dfd.setVisible(true);
			}
		});
		cmdDelete.setEnabled(false);
		this.toolBar.add(cmdDelete);
		
		JButton cmdAddService = new JButton("Add Service");
		cmdAddService.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				AddServiceDialog asd = new AddServiceDialog(MainWindow.this, 
						remoteDrives, factory);
				asd.setVisible(true);
			}
		});
		this.toolBar.add(cmdAddService);
		
		JButton cmdShowServices = new JButton("View Services");
		cmdShowServices.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				ServiceListWindow serviceWindow = new ServiceListWindow(MainWindow.this.getDriveStore());
				serviceWindow.setVisible(true);
			}
		});
		this.toolBar.add(cmdShowServices);
		
		this.add(this.toolBar, BorderLayout.PAGE_START);
		
		// ==== The Rest ====
		JPanel folderTreePanel = new JPanel();
		folderTreePanel.setLayout(new BorderLayout());
		
		JPanel fileViewBar = new JPanel();
		fileViewBar.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridLayout fileViewLayout = new GridLayout(1, 2, 5, 5);
		fileViewBar.setLayout(fileViewLayout);
		
		JButton cmdCombinedView = new JButton("Combined");
		cmdCombinedView.setEnabled(false); //TODO: Implement this?
		fileViewBar.add(cmdCombinedView);
		
		JButton cmdSeperateView = new JButton("Seperate");
		cmdSeperateView.setEnabled(false); //TODO: Implement this?
		fileViewBar.add(cmdSeperateView);
		
		folderTreePanel.add(fileViewBar, BorderLayout.PAGE_START);
		
		// Dummy root
		this.folderTree = new FolderTree();
		this.folderTree.setMinimumSize(new Dimension(150, 100));
		JScrollPane fileTreeView = new JScrollPane(this.folderTree);
		folderTreePanel.add(fileTreeView, BorderLayout.CENTER);

		this.folderTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event)
			{
				final FolderTreeNode node = (FolderTreeNode)MainWindow.this.folderTree.getLastSelectedPathComponent();
				if (node == null) {
					return;
				}
				final RemoteFolder folder = node.getFolder();
				
				(new SwingWorker<List<RemoteEntry>, Void>() {
					@Override
					protected List<RemoteEntry> doInBackground() throws Exception
					{
						return folder.getEntries();
					}
					
					@Override
					protected void done()
					{
						List<RemoteEntry> entries;
						try {
							entries = this.get();
						} catch (InterruptedException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						
						MainWindow.this.fileListModel.clear();
						DefaultTreeModel model = (DefaultTreeModel)MainWindow.this.folderTree.getModel();
						node.removeAllChildren();
						
						Iterator<RemoteEntry> it = entries.iterator();
						while (it.hasNext()) {
							RemoteEntry entry = it.next();
							if (entry.isFile()) {
								MainWindow.this.fileListModel.addElement(entry.asFile());
							} else {
								MainWindow.this.folderTree.addFolder(node, entry.asFolder());
							}
						}
						
						model.reload(node);
					}
				}).execute();
			}
		});
		
		this.fileListModel = new DefaultListModel<>();
		this.fileList = new FileList(this.fileListModel);
		
		this.fileList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event)
			{
				if (MainWindow.this.fileList.getSelectedValue() == null) {
					cmdDelete.setEnabled(false);
					cmdDownload.setEnabled(false);
					cmdOpen.setEnabled(false);
				} else {
					cmdDelete.setEnabled(true);
					cmdDownload.setEnabled(true);
					cmdOpen.setEnabled(true);
				}
			}
		});
		
		JScrollPane fileListView = new JScrollPane(this.fileList);
		JSplitPane fileSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		                               folderTreePanel, fileListView);
		
		fileSplit.setDividerSize(5);
		fileSplit.setContinuousLayout(true);
		
		this.add(fileSplit, BorderLayout.CENTER);
		
		// ==== Status Bar ====
		this.statusBar = new JPanel();
		this.statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		//TODO Implement a functional status bar
		JLabel versionLabel = new JLabel("Fusein -- Project Deliverable 4");
		this.statusBar.add(versionLabel);
		
		this.add(this.statusBar, BorderLayout.PAGE_END);
		
		// ==== CALLBACKS ====
		this.remoteDrives.addEventListener(this);
		this.addWindowListener(this);
	}
	
	/**
	 * Get the list of RemoteDrives
	 * @return The RemoteDrives displayed by the window
	 */
	public RemoteDriveStore getDriveStore()
	{
		return this.remoteDrives;
	}
	
	@Override
	public void driveRemoved(RemoteDrive drive)
	{
		int i = 0;
		while (i < this.fileListModel.size()) {
			RemoteFile file = this.fileListModel.elementAt(i);
			if (file == null) {
				break;
			}
			
			if (file.getRemoteDrive() == drive) {
				fileListModel.remove(i);
			} else {
				i++;
			}
		}
		
		DefaultTreeModel model = (DefaultTreeModel)this.folderTree.getModel();
		FolderTreeNode root = (FolderTreeNode)model.getRoot();
		Enumeration<FolderTreeNode> en = (Enumeration<FolderTreeNode>)root.children();
		while (en.hasMoreElements()) {
			FolderTreeNode node = en.nextElement();
			if (node.getFolder().getRemoteDrive() == drive) {
				model.removeNodeFromParent(node);
			}
		}
	}
	
	@Override
	public void driveAdded(final RemoteDrive drive)
	{
		cmdUpload.setEnabled(true);
		
		(new SwingWorker<List<RemoteEntry>, RemoteFolder>() {
			private FolderTreeNode root;
			
			@Override
			protected List<RemoteEntry> doInBackground() throws Exception
			{
				RemoteFolder root = drive.getRootFolder();
				
				this.publish(root);
				
				return root.getEntries();
			}
			
			@Override
			protected void process(List<RemoteFolder> chunks)
			{
				this.root = MainWindow.this.folderTree.addFolder(null, chunks.get(0));
			}
			
			@Override
			protected void done()
			{
				List<RemoteEntry> entries;
				try {
					entries = this.get();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				
				Iterator<RemoteEntry> it = entries.iterator();
				while (it.hasNext()) {
					RemoteEntry entry = it.next();
					if (entry.isFile()) {
						MainWindow.this.fileListModel.addElement(entry.asFile());
					} else {
						MainWindow.this.folderTree.addFolder(this.root, entry.asFolder());
					}
				}
				
				//TODO this is kind of a hack
				DefaultTreeModel model = (DefaultTreeModel)MainWindow.this.folderTree.getModel();
				model.reload();
				MainWindow.this.folderTree.expandPath(new TreePath(this.root.getPath()));
			}
		}).execute();
	}

	@Override
	public void windowActivated(WindowEvent event) {}

	@Override
	public void windowClosed(WindowEvent event) {}

	@Override
	public void windowClosing(WindowEvent event)
	{
		this.remoteDrives.saveToFile("conf.properties");
		this.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent event) {}

	@Override
	public void windowDeiconified(WindowEvent event) {}

	@Override
	public void windowIconified(WindowEvent event) {}

	@Override
	public void windowOpened(WindowEvent event) {}
	
	private class OpenMethodWorker extends SwingWorker<Boolean, Void> {
		private RemoteFile fileToOpen;

		public OpenMethodWorker(RemoteFile fileToOpen) {
			this.fileToOpen = fileToOpen;
		}
		
		@Override
		protected Boolean doInBackground() throws Exception {
		    String fileUrl = this.fileToOpen.getLink();
		    
		    if (fileUrl == null) {
		    	return false;
		    }
		    
		    Desktop.getDesktop().browse(new URL(fileUrl).toURI());
		    
		    return true;
		}

		@Override
		protected void done() {
		}
	}
}
