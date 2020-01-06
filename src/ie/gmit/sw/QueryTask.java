package ie.gmit.sw;

import java.util.concurrent.ConcurrentSkipListMap;

public class QueryTask {
	private String kmer;
	private ConcurrentSkipListMap<Integer, Kmer> map;

	public String getKmer() {
		return kmer;
	}

	public void setKmer(String kmer) {
		this.kmer = kmer;
	}

	public ConcurrentSkipListMap<Integer, Kmer> getMap() {
		return map;
	}

	public void setMap(ConcurrentSkipListMap<Integer, Kmer> map) {
		this.map = map;
	}

	public QueryTask(String kmer, ConcurrentSkipListMap<Integer, Kmer> map) {
		this.kmer = kmer;
		this.map = map;
	}
}
