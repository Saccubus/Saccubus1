package saccubus.prompt;

import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;

import saccubus.info.RootInfo;

/**
 * <p>
 * �^�C�g��: ������΂�
 * </p>
 * 
 * <p>
 * ����: �j�R�j�R����̓�����R�����g���ŕۑ�
 * </p>
 * 
 * <p>
 * ���쌠: Copyright (c) 2007 PSI
 * </p>
 * 
 * <p>
 * ��Ж�:
 * </p>
 * 
 * @author ������
 * @version 1.0
 */
public class Prompt {
	/**
	 * @param rootInfo
	 */
	public Prompt() {
		super();
	}
	public void main(String[] args) {
		PromptOption opt = new PromptOption(args);
		//�ݒ�ǂݍ���
		RootInfo info = new RootInfo();
		info.loadInfo(null, opt.getOverrideProp());
		opt.fixInfo(info);
		//�X�^�[�g�A�b�v���b�Z�[�W
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Saccubus on CUI");
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		System.out.println("Overrided Properties:");
		outPropList(opt.getOverrideProp());//�I�[�o�[���C�h���ꂽ���X�g�̕\��
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		//���s
		//TODO �ǉ�
		//�I��
		System.out.println("Finished.");
	}
	public static void outPropList(Properties prop) {
	    TreeMap<Object, Object> map = new TreeMap<Object, Object>();
	    map.putAll(prop);
	    Iterator<Object> it = map.keySet().iterator();
	    while (it.hasNext()) {
	        String key = (String)it.next();
	        String value = (String)map.get(key);
	        System.out.println(key + " -> " + value);
	    }
	}
}
