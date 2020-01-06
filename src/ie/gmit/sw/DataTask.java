package ie.gmit.sw;

public class DataTask{
	private Language language;
	private String text;
	
	public DataTask(Language language, String text) {
		this.language = language;
		this.text = text;
	}
	
	public Language getLanguage() { // FOR DEBUGGING
		return language;
	}
	
	public String getText() {
		return text;
	}
}
