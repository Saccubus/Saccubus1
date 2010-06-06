/**
 * 個々の設定アイテムの、さらに要素
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
	 * コマンドラインの場合
	 * @param commandLine
	 */
	protected ConfigElement(final String commandLine) {
		ElementType = Type.command;
		CommandLine = commandLine;
		OptionName = OptionValue = null;
	}
	/**
	 * オプションの場合
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
	 * 一応全部指定する場合
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
	 * ElementType:Command時にコマンドラインを返す
	 * @return commandLine
	 */
	public String getCommandLine() {
		return CommandLine;
	}
	/**
	 * この要素のタイプを返す
	 * @return elementType
	 */
	public Type getElementType() {
		return ElementType;
	}
	/**
	 * オプションの名前は？
	 * @return optionName
	 */
	public String getOptionName() {
		return OptionName;
	}
	/**
	 * オプションの値は？
	 * @return optionValue
	 */
	public String getOptionValue() {
		return OptionValue;
	}
	
}
