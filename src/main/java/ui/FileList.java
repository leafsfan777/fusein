
package ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import main.RemoteFile;

/**
 * Displays a list of RemoteFiles and allows the user to select one or more items
 *
 */
public class FileList extends JList<RemoteFile>
{
	/**
	 * Serialization identifier.
	 * Increment any time the class signature changes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create a FileList from a list of RemoteFiles
	 * @param model List of RemoteFiles
	 */
	public FileList(DefaultListModel<RemoteFile> model)
	{
		super(model); // ha ha ha

		this.setCellRenderer(new FileCellRenderer());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	/**
	 * Renderer for a single cell in out 
	 *
	 */
	private static class FileCellRenderer extends JLabel implements ListCellRenderer<RemoteFile>
	{
		/**
		 * Serialization identifier.
		 * Increment any time the class signature changes.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * The color displayed when the file cell is selected
		 */
		private static final Color selectedColor = new Color(230, 230, 250);
		
		/**
		 * The color displayed when the file cell is in focus
		 */
		private static final Color focusColor = new Color(202, 225, 255);
		
		/**
		 * An icon representing the service
		 */
		ImageIcon icon;
		
		
		/**
		 * Create a FileCellRenderer
		 */
		public FileCellRenderer()
		{
			super();
			
			this.icon = new ImageIcon(ClassLoader.getSystemResource("service_icons/dropbox.png"));
		}

		@Override
		public Component getListCellRendererComponent(
				JList<? extends RemoteFile> list, RemoteFile file,
				int index, boolean isSelected, boolean hasFocus) {
			this.setText(file.getName());
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
