package ie.gmit.sw;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database implements Runnable{

	private ConcurrentSkipListMap<Language, ConcurrentSkipListMap<Integer, Kmer>> db = new ConcurrentSkipListMap<>();
	
	private volatile boolean keepRunning = true;
	private BlockingQueue<KmerTask> queue = new ArrayBlockingQueue<>(1000);
	private ExecutorService executor = Executors.newFixedThreadPool(3);
	
	public Database(BlockingQueue<KmerTask> queue) {
		this.queue = queue;
	}

	public void process() throws InterruptedException {
		
		while(keepRunning) {
			KmerTask task = queue.take();
			
			if(task instanceof Poison) {
				keepRunning = false;
			}
			else {
				executor.execute(new Worker(db, task));
			}
		}
		
		executor.shutdown();
		//System.out.println("DEBUG - ExecutorSerivice.isShutdown() = " + executor.isShutdown());
	}
	
	@Override
	public void run() {
		try {
			process();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

}
