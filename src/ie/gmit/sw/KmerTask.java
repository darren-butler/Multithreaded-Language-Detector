package ie.gmit.sw;

public class KmerTask {
	private Language language;
	private String kmer;

	public KmerTask(Language language, String kmer) {
		this.language = language;
		this.kmer = kmer;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getKmer() {
		return kmer;
	}

	public void setKmer(String kmer) {
		this.kmer = kmer;
	}
}
