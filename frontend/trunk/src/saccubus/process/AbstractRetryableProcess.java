/**
 * ���g���C���鐫���̂��̗p�̒��ۃN���X
 */
package saccubus.process;

/**
 * @author PSI
 *
 */
public abstract class AbstractRetryableProcess {
	/**
	 * �f�t�H���g�̃��g���C�񐔁B3������΂悭�ˁH
	 */
	private static final int DEFAULT_TRY_CNT = 3;
	/**
	 * �g���C��
	 */
	private int MaxTryCnt = DEFAULT_TRY_CNT;
	/**
	 * ���g���C��r���Œ��f���邩�ۂ��̃t���O�B�Q�ƌ^�B
	 */
	private Boolean ContinueFlag;
	
	/**
	 * @param maxTryCnt
	 * @param continueFlag
	 */
	public AbstractRetryableProcess(int maxTryCnt, Boolean continueFlag) {
		MaxTryCnt = maxTryCnt;
		ContinueFlag = continueFlag;
	}
	/**
	 * @param continueFlag
	 */
	public AbstractRetryableProcess(Boolean continueFlag) {
		ContinueFlag = continueFlag;
	}
	/**
	 * �f�t�H���g�̉񐔂Ńg���C����B
	 *
	 */
	public boolean trying(){
		return trying(MaxTryCnt);
	}
	/**
	 * �w�肵���񐔂����g���C����B
	 * @param try_cnt
	 */
	public boolean trying(int try_cnt){
		for(int i=0;i<try_cnt;i++){
			if(!ContinueFlag.booleanValue()){
				break;
			}
			if(process(i)){
				return true;
			}
		}
		return false;
	}
	/**
	 * �������Ȃ���΂Ȃ�Ȃ����\�b�h�B���ۂɃg���C����B����������true��Ԃ����B
	 * @param retry_cnt
	 * @return
	 */
	public abstract boolean process(int retry_cnt);
}
