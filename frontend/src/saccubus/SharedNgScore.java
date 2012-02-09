package saccubus;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

/**
 * @author orz
 *
 */
public class SharedNgScore extends ButtonGroup {

	private static final long serialVersionUID = 1L;
	public static final int MINSCORE = Integer.MIN_VALUE;
	private static final int[] NGSCORE_LIMIT_VALUE = {MINSCORE,-10000,-4800,-1000};
	private AbstractButton[] array;
	private final static int LEVELS = 4;
	public final static int NONE = 0;
	public final static int LOW = 1;
	public final static int MEDIUM = 2;
	public final static int HIGH = 3;
	public final static int DEFAULT = MEDIUM;

	public SharedNgScore() {
		array = new AbstractButton[LEVELS];
	}

	public void add(AbstractButton b, int level){
		array[level] = b;
		add(b);
	}

	public void setScore(int score){
		int limit = HIGH;
		for(int i=HIGH;i>=NONE;i--){
			if(score == NGSCORE_LIMIT_VALUE[i]){
				limit = i;
				break;
			} else if (score > NGSCORE_LIMIT_VALUE[i]){
				break;
			} else if (score < NGSCORE_LIMIT_VALUE[i]){
				limit = i;
			}
		}
		for(int i=0;i<array.length;i++)
			array[i].setSelected(i==limit);
	}

	public void setScore(String scorestr){
		int score;
		try{
			score = Integer.parseInt(scorestr);
			if(score >= 0){
				score = MINSCORE;
			}
		}catch(NumberFormatException e){
			score = MINSCORE;
		}
		setScore(score);
	}

	public int getScore(){
		for(int i = 0; i < array.length; i++){
			if (array[i]!=null && array[i].isSelected()){
				return NGSCORE_LIMIT_VALUE[i];
			}
		}
		return MINSCORE;
	}
}
