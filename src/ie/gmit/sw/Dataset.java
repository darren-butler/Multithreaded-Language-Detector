package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

/**
 * This Class is instantiated as a <code>Runnable</code> "producer" thread.<br>
 * It reads data from a file and adds tasks to a <code>BlockingQueue</code> to be consumed by worker threads.
 * @author Darren
 *
 */

public class Dataset implements Parsable, Runnable {

	private String dataSource;
	private BlockingQueue<DataTask> queue;

	public Dataset(String dataSource, BlockingQueue<DataTask> queue) {
		super();
		this.dataSource = dataSource;
		this.queue = queue;
	}

	@Override
	public void run() {
		parse();
	}

	/**
	 * Reads data from a file and instantiates a new <code>DataTask</code> for each language in the data set. <br>
	 * This data file must be a .txt file and the data must be in the format: "this is a line of text in some language@English",<br>
	 * where each line is a different language delimited by an '@' symbol appended with the language.
	 */
	@Override
	public void parse() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(dataSource)));

			String line = null;

			while ((line = br.readLine()) != null) {
				String[] record = line.trim().split("@");
				if (record.length != 2)
					continue;
				Language language = Language.valueOf(record[1]);
				String text = record[0];
				queue.put(new DataTask(language, text));
			}

			// once the file has ended, flood the queue with poison objects,
				// ensuring that each worker thread eventually .take()'s a poison object and stops running
			for (int i = 0; i < 100; i++) {
				queue.put(new DataPoison(null, null)); 
			}

			br.close();
		} catch (Exception e) {
			for (int i = 0; i < 100; i++) {
				try {
					queue.put(new DataPoison(null, null));
				} catch (InterruptedException e1) {
					// e1.printStackTrace();
				}
			}
			// e.printStackTrace();
		}

	}

}
