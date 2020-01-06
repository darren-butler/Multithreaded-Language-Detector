package ie.gmit.sw;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

public class DataWorker implements Runnable {

	private ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db; // handle on Database map
	private BlockingQueue<DataTask> queue;
	private volatile boolean keepGoing = true;

	public DataWorker(ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db,
			BlockingQueue<DataTask> queue) {
		this.queue = queue;
		this.db = db;
	}

	public void process() throws InterruptedException {
		while (keepGoing) {
			DataTask task = queue.take();

			if (task instanceof DataPoison) {
				keepGoing = false;
			} else {

				for (int i = 1; i <= 4; i++) {
					for (int j = 0; j <= task.getText().length() - j; j++) {
						String kmer = task.getText().substring(j, j + i);
						add(kmer, task.getLanguage());
					}
				}
			}
		}
	}

	public void add(String s, Language lang) {
		int kmerHash = s.hashCode();
		ConcurrentSkipListMap<Integer, Kmer> langDB = getLanguageEntries(lang);

		int frequency = 1;
		if (langDB.containsKey(kmerHash)) {
			frequency += langDB.get(kmerHash).getFrequency();
		}

		langDB.put(kmerHash, new Kmer(kmerHash, frequency));
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
