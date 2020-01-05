package ie.gmit.sw;

import java.util.concurrent.ConcurrentSkipListMap;

public class Worker implements Runnable{
	private ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db = null;
	private KmerTask task;
	
	public Worker(ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db, KmerTask task) {
		this.db = db;
		this.task = task;
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
		this.add(task.getKmer(), task.getLanguage());
	}

}
