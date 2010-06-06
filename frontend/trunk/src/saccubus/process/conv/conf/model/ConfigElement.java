/**
 * �X�̐ݒ�A�C�e���́A����ɗv�f
 */
package saccubus.process.conv.conf.model;

/**
 * @author PSI
 *
 */
public class ConfigElement {
	public enum Type {
		command,
		option
	}
	private final Type ElementType;
	private final String OptionName;
	private final String OptionValue;
	private final String CommandLine;
	/**
	 * �R�}���h���C���̏ꍇ
	 * @param commandLine
	 */
	protected ConfigElement(final String commandLine) {
		ElementType = Type.command;
		CommandLine = commandLine;
		OptionName = OptionValue = null;
	}
	/**
	 * �I�v�V�����̏ꍇ
	 * @param optionName
	 * @param optionValue
	 */
	protected ConfigElement(final String optionName, final String optionValue) {
		ElementType = Type.option;
		OptionName = optionName;
		OptionValue = optionValue;
		CommandLine = null;
	}
	/**
	 * �ꉞ�S���w�肷��ꍇ
	 * @param elementType
	 * @param optionName
	 * @param optionValue
	 * @param commandLine
	 */
	protected ConfigElement(final Type elementType, final String optionName, final String optionValue, final String commandLine) {
		ElementType = elementType;
		OptionName = optionName;
		OptionValue = optionValue;
		CommandLine = commandLine;
	}
	/**
	 * ElementType:Command���ɃR�}���h���C����Ԃ�
	 * @return commandLine
	 */
	public String getCommandLine() {
		return CommandLine;
	}
	/**
	 * ���̗v�f�̃^�C�v��Ԃ�
	 * @return elementType
	 */
	public Type getElementType() {
		return ElementType;
	}
	/**
	 * �I�v�V�����̖��O�́H
	 * @return optionName
	 */
	public String getOptionName() {
		return OptionName;
	}
	/**
	 * �I�v�V�����̒l�́H
	 * @return optionValue
	 */
	public String getOptionValue() {
		return OptionValue;
	}
	
}
