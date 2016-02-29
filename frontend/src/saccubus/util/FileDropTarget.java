package saccubus.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
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

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
			return;
		}
		dtde.rejectDrag();
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				Transferable t = dtde.getTransferable();
				@SuppressWarnings("rawtypes")
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
								Field.setText(evalExt(file.getPath()));
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

	private String evalExt(String path) {
		final String INTERNET_SHORTCUT = "[InternetShortcut]\nURL=";
		if(path.toLowerCase().endsWith(".url")){	//
			String text =
				saccubus.net.Path.readAllText(new File(path), "ms932");
			//System.out.println("Debug: dropped test is\n" + text + "\n");
			if(text!=null && text.startsWith(INTERNET_SHORTCUT)){
				return text.substring(INTERNET_SHORTCUT.length()).replaceAll("\n.*","");
			}
		}
		return path;
	}

}
