package ie.gmit.sw;

/**
 * The <code>Dataset</code> class instantiates and adds instances of this <code>DataTask</code> class to a <code>BlockingQueue</code> to be processed by worker threads.
 * @author Darren
 *
 */
public class DataTask {
	private Language language;
	private String text;

	public DataTask(Language language, String text) {
		this.language = language;
		this.text = text;
	}

	public Language getLanguage() {
		return language;
	}

	public String getText() {
		return text;
	}
}
