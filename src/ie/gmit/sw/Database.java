package ie.gmit.sw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

public class Database {
	private ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db = new ConcurrentSkipListMap<>();
	
	public Database() {
		
	}
	
	public ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> getDb() {
		return db;
	}
	
	public void resize(int max) { //TODO take advantage of ConcurrentSkipListMap natual ordering
		Set<Language> keys = db.keySet(); // db.descendingKeySet()?
		for(Language lang : keys) {
			ConcurrentSkipListMap<Integer, Kmer> top = getTop(max, lang);
			db.put(lang, top);
		}
	}
	
	public ConcurrentSkipListMap<Integer, Kmer> getTop(int max, Language lang) {
		ConcurrentSkipListMap<Integer, Kmer> temp = new ConcurrentSkipListMap<>(); 
		List<Kmer> kmers = new ArrayList<>(db.get(lang).values());
		Collections.sort(kmers);
		
		int rank = 1;
		for(Kmer k : kmers) {
			k.setRank(rank);
			temp.put(k.getHash(), k);
			if(rank == max) break;
			rank++;
		}
		return temp;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		int langCount = 0;
		int kmerCount = 0;
		Set<Language> keys = db.keySet();
		for(Language lang: keys) {
			langCount++;
			sb.append(lang.name() + "->\n");
			
			Collection<Kmer> m = new TreeSet<>(db.get(lang).values());
			kmerCount += m.size();
			for(Kmer k: m) {
				sb.append("\t" + k + "\n");
			}
		}
		
		sb.append(kmerCount + " total k-mers in " + langCount + " languages");
		return sb.toString();
	}
	
	public Language getLanguage(Map<Integer, Kmer> query) {
		TreeSet<OutOfPlaceMetric> oopm = new TreeSet<>();
		
		Set<Language> langs = db.keySet(); // ? already ordereD?
		for(Language lang: langs) {
			oopm.add(new OutOfPlaceMetric(lang, getOutOfPlaceDistance(query, db.get(lang))));
		}
		
		return oopm.first().getLanguage();
	}
	
	private int getOutOfPlaceDistance(Map<Integer, Kmer> query, Map<Integer, Kmer> subject) {
		int distance = 0;
		
		Set<Kmer> kmers = new TreeSet<>(query.values());
		for(Kmer q: kmers) {
			Kmer s = subject.get(q.getHash());
			if(s == null) {
				distance += subject.size() + 1;
			}
			else {
				distance += s.getRank() - q.getRank();
			}
		}
		
		return distance;
	}
	
	private class OutOfPlaceMetric implements Comparable<OutOfPlaceMetric>{
		private Language lang;
		private int distance;
		
		public OutOfPlaceMetric(Language lang, int distance) {
			super();
			this.lang = lang;
			this.distance = distance;
		}

		public Language getLanguage() {
			return lang;
		}

		public int getAbsoluteDistance() {
			return Math.abs(distance);
		}

		@Override
		public int compareTo(OutOfPlaceMetric o) {
			return Integer.compare(this.getAbsoluteDistance(), o.getAbsoluteDistance());
		}

		@Override
		public String toString() {
			return "[lang=" + lang + ", distance=" + getAbsoluteDistance() + "]";
		}
		
		
	}
}

