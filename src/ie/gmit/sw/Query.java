package ie.gmit.sw;

import java.util.concurrent.ConcurrentSkipListMap;

public class Query {
	private ConcurrentSkipListMap<Integer, Kmer> map = new ConcurrentSkipListMap<>();// TODO ConcurrentSkiptListMap to allow concurrency?

	
	public ConcurrentSkipListMap<Integer, Kmer> getMap() {
		return map;
	}

	public void setMap(ConcurrentSkipListMap<Integer, Kmer> map) {
		this.map = map;
	}
}
