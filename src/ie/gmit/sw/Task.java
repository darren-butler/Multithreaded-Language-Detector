package ie.gmit.sw;

public class Task{
	private Language language;
	private String text;
	
	public Task(Language language, String text) {
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
