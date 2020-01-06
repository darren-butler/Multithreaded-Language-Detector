package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

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
