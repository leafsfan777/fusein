
package ui;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import main.RemoteFolder;

/**
 * A class that displays a set of files as folder tree
 *
 */
public class FolderTree extends JTree
{
	/**
	 * Serialization identifier.
	 * Increment any time the class signature changes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The tree root node
	 */
	private DefaultMutableTreeNode root;
	
	/**
	 * Create an empty FolderTree
	 */
	public FolderTree()
	{
		super();
		
		DefaultTreeModel model = (DefaultTreeModel)FolderTree.this.getModel();
		
		// Dummy node
		this.root = new DefaultMutableTreeNode("Fusein");
		model.setRoot(this.root);
		this.setRootVisible(false);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	public FolderTreeNode addFolder(DefaultMutableTreeNode parent, RemoteFolder folder)
	{
		FolderTreeNode newNode = new FolderTreeNode(folder);
		//DefaultTreeModel model = (DefaultTreeModel)FolderTree.this.getModel();
		
		if (parent == null) {
			parent = this.root;
		}
		
		int index = parent.getChildCount();
		//model.insertNodeInto(newNode, parent, index);
		parent.insert(newNode, index);
		
		return newNode;
	}

	/**
	 * A node in the FolderTree
	 *
	 */
	public class FolderTreeNode extends DefaultMutableTreeNode
	{
		/**
		 * Serialization identifier.
		 * Increment any time the class signature changes.
		 */
		private static final long serialVersionUID = 1L;
		
		/** 
		 * The RemoteFolder the node is representing
		 */
		private RemoteFolder folder;
		
		/**
		 * Create a node to represent a given RemoteFolder
		 * @param folder The folder to be displayed
		 */
		public FolderTreeNode(RemoteFolder folder)
		{
			super(folder.getParent() == null ? String.format("%s's %s", 
					folder.getRemoteDrive().getUsername(), folder.getRemoteDrive().getServiceNiceName())
					: folder.getName());
			
			this.folder = folder;
		}
		
		/**
		 * Get the RemoteFolder represented by this node
		 * @return The RemoteFolder.
		 */
		public RemoteFolder getFolder()
		{
			return this.folder;
		}
	}
}
