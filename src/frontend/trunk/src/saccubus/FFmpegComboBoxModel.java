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
public class FFmpegComboBoxModel extends DefaultComboBoxModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8948187216195366156L;

	private final ArrayList<FFmpegSelectedItem> List = new ArrayList<FFmpegSelectedItem>(
			20);

	private int Size = 0;

	private int Index = 0;

	public FFmpegComboBoxModel() {
		reload();
	}

	/**
	 * �t�@�C���ǂݍ��݂��̑����s��
	 */
	private static final File OPTION_FOLDER = new File(".\\option\\");

	private static final FFmpegSelectedItem DEFAULT_ITEM = new FFmpegSelectedItem(
			0, null, "�O���t�@�C����p�����A���ɒ��ړ��͂���B");

	protected void reload() {
		File original_file = ((FFmpegSelectedItem) getSelectedItem()).getFile();
		reload(original_file);
	}

	protected void reload(File original_file) {
		int original_index = -1;
		int index = 1;
		// ���X�g�N���A
		List.clear();
		// �t�@�C�����X�g�X�V
		File[] file_array = OPTION_FOLDER.listFiles();
		if(file_array != null){
			for (int i = 0; i < file_array.length; i++) {
				File file = file_array[i];
				if (file.getName().endsWith(".xml")) {
					List.add(new FFmpegSelectedItem(index, file, null));
					// �O�񎦂��Ă����̂Ɠ����t�@�C���𔭌��B
					if (original_index < 0 && file.equals(original_file)) {
						original_index = index;
					}
					index++;
				}
			}
		}
		// ������
		if (original_index < 0) {
			Index = 0;
		} else {
			Index = original_index;
		}
		Size = index;
		this.fireContentsChanged(this, 0, Size);
	}

	/**
	 * �I�΂�Ă���I�u�W�F�N�g��Ԃ�
	 */
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
	 * �I�u�W�F�N�g����A�C���f�b�N�X��T���B
	 */
	public void setSelectedItem(Object anItem) {
		if (anItem == null) {
			Index = 0;
			return;
		}
		FFmpegSelectedItem item = (FFmpegSelectedItem) anItem;
		Index = item.getIndex();
	}

	/**
	 * �C���f�b�N�X����I�u�W�F�N�g��Ԃ��B
	 */
	public Object getElementAt(int index) {
		if (index == 0) {
			return DEFAULT_ITEM;
		} else if (index < Size) {
			return List.get(index - 1);
		} else {
			return null;
		}
	}

	/**
	 * �T�C�Y��Ԃ��B
	 */

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

	public String toString() {
		return Name;
	}

	protected File getFile() {
		return File;
	}

	/**
	 * ���ʂɎg��
	 * 
	 * @return
	 */
	protected int getIndex() {
		return Index;
	}
}
