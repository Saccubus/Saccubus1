/**
 * �t�@�C���Ɋւ��郆�[�e�B���e�B
 */
package saccubus.util;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author PSI
 *
 */
public class FileUtil {
	private static Pattern safeFileName_SPACE = Pattern.compile(" {2}+");
	public static String safeFileName(String str) {
		//���̎Q�Ƃ̃p�[�X
		int old_index = 0;
		int new_index = 0;
		StringBuffer sb = new StringBuffer();
		String ch;
		while((new_index = str.indexOf("&#",old_index)) >= 0){
			sb.append(str,old_index,new_index);
			old_index = str.indexOf(";",new_index);
			ch = str.substring(new_index+2,old_index);
			sb.append(new String(new char[]{(char) Integer.parseInt(ch)}));
			old_index++;
		}
		//�Ō�ɒǉ�
		sb.append(str,old_index,str.length());
		str = sb.toString();
		//�t�@�C���V�X�e���ň�����`��
		str = str.replace('/', '�^');
		str = str.replace('\\', '��');
		str = str.replace('?', '�H');
		str = str.replace('*', '��');
		str = str.replace(':', '�F');
		str = str.replace('|', '�b');
		str = str.replace('\"', '�h');
		str = str.replace('<', '��');
		str = str.replace('>', '��');
		str = str.replace('.', '�D');
		str = safeFileName_SPACE.matcher(str).replaceAll(" ");
		return str;
	}
	public static String getFilePathWithoutExt(File file){
		String path = file.getPath();
		int sep_index = path.lastIndexOf(File.separator);
		if(sep_index < 0){
			sep_index = 0;
		}
		int ext_index = path.indexOf(".",sep_index);
		if(ext_index < 0){
			ext_index = path.length();
		}
		return path.substring(0,ext_index);
	}
}
