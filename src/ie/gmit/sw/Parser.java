package ie.gmit.sw;

public abstract class Parser implements Runnable {

	private String dataSource;
	private int kmerSize;

	public int getKmerSize() {
		return kmerSize;
	}
	
	public String getDataSource() {
		return dataSource;
	}
	
	public Parser(String dataSource, int kmerSize) {
		this.dataSource = dataSource;
		this.kmerSize = kmerSize;
	}
	
	public abstract void parse(String text, String lang, int... ks) throws Throwable;
}
