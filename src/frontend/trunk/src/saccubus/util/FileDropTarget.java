package saccubus.util;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;

import javax.swing.JTextField;

public class FileDropTarget extends DropTargetAdapter {
	private final JTextField Field;

	private final boolean isFolder;

	public FileDropTarget(JTextField field, boolean is_folder) {
		isFolder = is_folder;
		Field = field;
	}

	public void dragOver(DropTargetDragEvent dtde) {
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
			return;
		}
		dtde.rejectDrag();
	}

	public void drop(DropTargetDropEvent dtde) {
		try {
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				Transferable t = dtde.getTransferable();
				java.util.List list = (java.util.List) t
						.getTransferData(DataFlavor.javaFileListFlavor);
				for (Object o : list) {
					if (o instanceof File) {
						File file = (File) o;
						if (isFolder) {
							if (file.isDirectory()) {
								Field.setText(file.getPath());
							} else {
								Field.setText(file.getParent());
							}
						} else {
							if (file.isFile()) {
								Field.setText(file.getPath());
							}
						}
					}
				}
				dtde.dropComplete(true);
			}
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
