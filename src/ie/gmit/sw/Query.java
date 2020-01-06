package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Query implements Runnable {

	private Map<Integer, Kmer> queryMap = new HashMap<>();// TODO ConcurrentSkiptListMap to allow concurrency?
	private String dataSource;

	public Query(String dataSource) {
		super();
		this.dataSource = dataSource;
	}
	
	public Map<Integer, Kmer> getQueryMap(){
		return queryMap;
	}

	public void parse() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataSource)));
			String line = null;
			String text = null;

			// parse text file into one contiguous String
			while ((line = br.readLine()) != null) {
				text += line;
			}

			for (int i = 0; i <= text.length() - 3; i++) { // TODO remove hardcode n-mer size
				String kmer = text.substring(i, i + 3);

				int hash = kmer.hashCode();

				int frequency = 1;
				if (queryMap.containsKey(hash)) {
					frequency += queryMap.get(hash).getFrequency();
				}

				queryMap.put(hash, new Kmer(hash, frequency));
			}

//			for (int j = 0; j <= text.length() - i; j++) {
//				String kmer = text.substring(j, j + i);
//				int kmerHash = kmer.hashCode();
//
//				int frequency = 1;
//				if (queryMap.containsKey(kmerHash)) {
//					frequency += queryMap.get(kmerHash).getFrequency();
//				}
//				queryMap.put(kmerHash, new Kmer(kmerHash, frequency));
//			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		parse();
	}

}
