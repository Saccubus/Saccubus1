/**
 * �_�E�����[�h���͂��̃C���^�[�t�F�[�X����������Ɗy����
 */
package saccubus.process.net;

/**
 * @author PSI
 *
 */
public interface ConnectionCallback {
	public boolean connectionRead(int content_length,int already_read,byte[] buff,int length);
}
