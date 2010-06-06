/**
 * リトライする性質のもの用の抽象クラス
 */
package saccubus.process;

/**
 * @author PSI
 *
 */
public abstract class AbstractRetryableProcess {
	/**
	 * デフォルトのリトライ回数。3回もやればよくね？
	 */
	private static final int DEFAULT_TRY_CNT = 3;
	/**
	 * トライ回数
	 */
	private int MaxTryCnt = DEFAULT_TRY_CNT;
	/**
	 * リトライを途中で中断するか否かのフラグ。参照型。
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
	 * デフォルトの回数でトライする。
	 *
	 */
	public boolean trying(){
		return trying(MaxTryCnt);
	}
	/**
	 * 指定した回数だけトライする。
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
	 * 実装しなければならないメソッド。実際にトライする。成功したらtrueを返す事。
	 * @param retry_cnt
	 * @return
	 */
	public abstract boolean process(int retry_cnt);
}
