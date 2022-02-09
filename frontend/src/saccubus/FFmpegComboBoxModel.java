/**
 *
 */
package saccubus;

import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

/**
 * @author Account01
 *
 */
public class FFmpegComboBoxModel extends DefaultComboBoxModel<FFmpegSelectedItem> {
	/**
	 *
	 */
	private static final long serialVersionUID = -8948187216195366156L;
	private static final File FILE_DEFAULT_FOLDER = new File(".\\option\\");

	private File optionFoler = FILE_DEFAULT_FOLDER;
	private static final int COMBOBOX_LIST_FILES = 30;
	private final ArrayList<FFmpegSelectedItem> List = new ArrayList<FFmpegSelectedItem>(
			COMBOBOX_LIST_FILES);

	private int Size = 0;

	private int Index = 0;

	public FFmpegComboBoxModel() {
		reload();
	}

	public void setOptionFolder(String folderPath) {
		File folder = new File(folderPath);
		if (folder.isDirectory()){
			optionFoler = folder;
		}
		reload();
	}

	/**
	 * ファイル読み込みその他を行う
	 */
	private static final FFmpegSelectedItem DEFAULT_ITEM = new FFmpegSelectedItem(
			0, null, "外部ファイルを用いず、下に直接入力する。");

	protected void reload() {
		File original_file = ((FFmpegSelectedItem) getSelectedItem()).getFile();
		reload(original_file);
	}

	protected void reload(File original_file) {
		int original_index = -1;
		int index = 1;
		// リストクリア
		List.clear();
		// ファイルリスト更新
		File[] file_array = optionFoler.listFiles();
		if(file_array != null){
/*			for (int i = 0; i < file_array.length; i++) {
				File file = file_array[i];
 */
			for(File file:file_array){
				if (file.getName().endsWith(".xml")) {
					List.add(new FFmpegSelectedItem(index, file, null));
					// 前回示していたのと同じファイルを発見。
					if (original_index < 0 && file.equals(original_file)) {
						original_index = index;
					}
					index++;
				}
			}
		}
		// 初期化
		if (original_index < 0) {
			Index = 0;
		} else {
			Index = original_index;
		}
		Size = index;
		this.fireContentsChanged(this, 0, Size);
	}

	/**
	 * 選ばれているオブジェクトを返す
	 */
	@Override
	public Object getSelectedItem() {
		return getElementAt(Index);
	}

	public File getSelectedFile() {
		if (Index == 0) {
			return null;
		} else {
			return List.get(Index - 1).getFile();
		}
	}

	/**
	 * オブジェクトから、インデックスを探す。
	 */
	@Override
	public void setSelectedItem(Object anItem) {
		if (anItem == null) {
			Index = 0;
			return;
		}
		FFmpegSelectedItem item = (FFmpegSelectedItem) anItem;
		Index = item.getIndex();
	}

	/**
	 * インデックスからオブジェクトを返す。
	 */
	@Override
	public FFmpegSelectedItem getElementAt(int index) {
		if (index == 0) {
			return DEFAULT_ITEM;
		} else if (index < Size) {
			return List.get(index - 1);
		} else {
			return null;
		}
	}

	/**
	 * サイズを返す。
	 */

	@Override
	public int getSize() {
		return Size;
	}

	public boolean isFile() {
		if (Index == 0) {
			return false;
		} else {
			return true;
		}
	}

}

class FFmpegSelectedItem {
	private final int Index;

	private final File File;

	private final String Name;

	protected FFmpegSelectedItem(int index, File file, String name) {
		Index = index;
		File = file;
		if (name == null) {
			String tmp = file.getName();
			Name = tmp.substring(0, tmp.lastIndexOf("."));
		} else {
			Name = name;
		}
	}

	@Override
	public String toString() {
		return Name;
	}

	protected File getFile() {
		return File;
	}

	/**
	 * 識別に使う
	 *
	 * @return
	 */
	protected int getIndex() {
		return Index;
	}
}
