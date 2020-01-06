package ie.gmit.sw;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Multiple instances of this class will concurrently <code>take()</code> <code>DataTask</code> objects from the <code>BlockingQueue</code> <br>
 * Each <code>DataTask</code> represents a single line from the data set file split into its <code>String</code> text, and <code>Language</code> enum. <br>
 * 
 * @author Darren
 *
 */
public class DataWorker implements Runnable {

	private ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db; // handle on Database map
	private BlockingQueue<DataTask> queue;
	private volatile boolean keepGoing = true;

	public DataWorker(ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db,
			BlockingQueue<DataTask> queue) {
		this.queue = queue;
		this.db = db;
	}

	/**
	 * This method continually calls .take() on the BlockingQueue until a poison object is taken, setting keepGoing to false and eventually terminating the thread.<br>
	 * Each DataTask taken from the queue involves splitting a single line of text from the data set into 1,2,3 and 4-mers<br>
	 * eg: each time the loop runs, this thread takes a String of text and splits it into k-mers of 4 sizes and calls add() on each k-mer.
	 * @throws InterruptedException
	 */
	public void process() throws InterruptedException {
		while (keepGoing) {
			DataTask task = queue.take();

			if (task instanceof DataPoison) {
				keepGoing = false;
			} else {

				for (int i = 1; i <= 4; i++) { // set to 5 to include 5-mers?
					for (int j = 0; j <= task.getText().length() - j; j++) {
						String kmer = task.getText().substring(j, j + i);
						add(kmer, task.getLanguage());
					}
				}
			}
		}
	}

	/**
	 * This method takes a k-mer as a String, gets the hashCode() of the k-mer, and adds a new <code>Kmer</code> to the map of it's language, 
	 * setting it's frequency to 1 if the k-mer was not already in the map, or incrementing it's frequency by 1 if it was.
	 * @param s
	 * @param lang
	 */
	public void add(String s, Language lang) {
		int kmerHash = s.hashCode();
		ConcurrentSkipListMap<Integer, Kmer> langDB = getLanguageEntries(lang);

		int frequency = 1;
		if (langDB.containsKey(kmerHash)) {
			frequency += langDB.get(kmerHash).getFrequency();
		}

		langDB.put(kmerHash, new Kmer(kmerHash, frequency));
	}

	/**
	 * This method returns a map of Kmers of a given Language type or adds a new map for that language if not already present.
	 * @param lang
	 * @return
	 */
	private ConcurrentSkipListMap<Integer, Kmer> getLanguageEntries(Language lang) {
		ConcurrentSkipListMap<Integer, Kmer> langDB = null;
		if (db.containsKey(lang)) {
			langDB = db.get(lang);
		} else {
			langDB = new ConcurrentSkipListMap<Integer, Kmer>();
			db.put(lang, langDB);
		}

		return langDB;
	}

	@Override
	public void run() {
		try {
			process();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
