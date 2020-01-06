package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * This Class is similar to the <code>Dataset</code> in that it reads data from a file and adds tasks to a <code>BlockingQueue</code> to be consumed by worker threads.
 * <br> Origionally both this class and the <code>Dataset</code> class extended an abstract supertype, but this was removed because I could not figure out any benefits.
 * @author Darren
 *
 */
public class Query implements Parsable, Runnable {

	private String dataSource;
	private BlockingQueue<QueryTask> queue;
	private ConcurrentSkipListMap<Integer, Kmer> map = new ConcurrentSkipListMap<>();

	public Query(String dataSource, BlockingQueue<QueryTask> queue) {
		this.dataSource = dataSource;
		this.queue = queue;
	}

	public ConcurrentSkipListMap<Integer, Kmer> getMap() {
		return map;
	}

	/**
	 * Sets the <code>ConcurrentSkipListMap<Integer, Kmer> </code> map of hashCode()'s and their Kmers to a new sorted and trimmed map 
	 * @param max
	 */
	public void resize(int max) { // TODO take advantage of ConcurrentSkipListMap natual ordering
		// Set<Language> keys = map.keySet(); // db.descendingKeySet()?
		// for(Language lang : keys) {
		ConcurrentSkipListMap<Integer, Kmer> top = getTop(max);
		map = top;
		// }
	}

	public ConcurrentSkipListMap<Integer, Kmer> getTop(int max) {
		ConcurrentSkipListMap<Integer, Kmer> temp = new ConcurrentSkipListMap<>();
		List<Kmer> kmers = new ArrayList<>(map.values());
		Collections.sort(kmers);

		int rank = 1;
		for (Kmer k : kmers) {
			k.setRank(rank);
			temp.put(k.getHash(), k);
			if (rank == max)
				break;
			rank++;
		}
		return temp;
	}

	@Override
	public void run() {
		parse();

	}

	/**
	 * Parses a text file into a single String and then breaks the String into 1,2,3 & 4-mers and adds them to the BlockingQueue as <code>QueryTask</code> objects.
	 */
	@Override
	public void parse() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(dataSource)));

			String line = null;
			String text = null;

			while ((line = br.readLine()) != null) {
				text += line;
			}

			for (int i = 1; i <= 4; i++) {
				for (int j = 0; j <= text.length() - j; j++) {
					String kmer = text.substring(j, j + i);
					queue.put(new QueryTask(kmer, map));

				}
			}

			for (int i = 0; i < 100; i++) { // flood the queue with poison to ensure each worker thread eventually dies
				queue.put(new QueryPoison(null));
			}

			br.close();
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("\n\tERROR - Could not open file");
			for (int i = 0; i < 100; i++) {
				try {
					queue.put(new QueryPoison(null));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			return;
		}

	}

}
