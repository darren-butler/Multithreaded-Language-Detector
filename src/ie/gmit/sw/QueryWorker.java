package ie.gmit.sw;

import java.util.concurrent.BlockingQueue;

/**
 * Multiple instances of this class will concurrently <code>take()</code> <code>QueryTask</code> objects from the <code>BlockingQueue</code> until poisoned.<br>
 * 
 * @author Darren
 *
 */
public class QueryWorker implements Runnable {

	private BlockingQueue<QueryTask> queue;
	private volatile boolean keepGoing = true;

	public QueryWorker(BlockingQueue<QueryTask> queue) {
		this.queue = queue;
	}

	/**
	 * Takes <code>QueryTask</code> objects from the queue, processes them to produce a <code>Kmer</code> which is then added to the query map.
	 * @throws InterruptedException
	 */
	public void process() throws InterruptedException {
		while (keepGoing) {
			QueryTask task = queue.take();

			if (task instanceof QueryPoison) {
				keepGoing = false;
			} else {
				int hash = task.getKmer().hashCode();

				int frequency = 1;
				if (task.getMap().containsKey(hash)) {
					frequency += task.getMap().get(hash).getFrequency();
				}

				task.getMap().put(hash, new Kmer(hash, frequency));
			}

		}
	}

	@Override
	public void run() {
		try {
			process();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
