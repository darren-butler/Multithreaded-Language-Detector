package ie.gmit.sw;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

public class Worker implements Runnable {

	private ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db; //handle on Database map
	private BlockingQueue<Task> queue;
	private volatile boolean keepGoing = true;
	
	private ConcurrentSkipListMap<Integer, Kmer> langaugeMap;

	public Worker(ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db, BlockingQueue<Task> queue) {
		this.queue = queue;
		this.db = db;
	}

	public void process() throws Throwable {
		// keep working til queue is poisoned
		
		while(keepGoing) {
			Task task = queue.take();
			
			if(task instanceof Poison) {
				keepGoing = false;
			}else {
				
//				for(int i = 1; i <= 4; i++) {
//					for(int j = 0; j <= task.getText().length() - j; j++) {
//						String kmer = task.getText().substring(j, j + i);	
//						add(kmer, task.getLanguage());						}
//				}
				
				
				for (int i = 0; i <= task.getText().length() - 3; i++) { //TODO remove hardcode n-mer size
					String kmer = task.getText().substring(i, i + 3);
					add(kmer, task.getLanguage());				
				}

				//add( task.getText(), task.getLanguage()); //TODO  make add() method take a Task instead of Language, String

			}
		}

		
	}

	public void add(String s, Language lang) {
		int kmerHash = s.hashCode();
		ConcurrentSkipListMap<Integer, Kmer> langDB = getLanguageEntries(lang);

		int frequency = 1;
		if(langDB.containsKey(kmerHash)) {
			frequency += langDB.get(kmerHash).getFrequency();
		}
				
		langDB.put(kmerHash, new Kmer(kmerHash, frequency));
	}
	
	private ConcurrentSkipListMap<Integer, Kmer> getLanguageEntries(Language lang) {
		ConcurrentSkipListMap<Integer, Kmer> langDB = null;
		if(db.containsKey(lang)) {
			langDB = db.get(lang);
		}
		else {
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
