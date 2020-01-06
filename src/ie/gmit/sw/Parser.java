package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class Parser implements Runnable {

	private String dataSource;
	private int kmerSize;
	
	private BlockingQueue<Task> queue; 
	
	public Parser(BlockingQueue<Task> queue, String dataSource, int kmerSize) {
		this.queue = queue;
		this.dataSource = dataSource;
		this.kmerSize = kmerSize;
	}
	
	public void parse() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataSource)));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				String[] record = line.trim().split("@");
				if (record.length != 2)
					continue;
				Language language = Language.valueOf(record[1]);
				String text = record[0];
				queue.put(new Task(language, text));
			}
			
			for(int i = 0; i < 100; i++) {
				queue.put(new Poison(null, null)); //MAYBE?! put 100 instances of POions to ensure workers are dead?
				// add loads of poison so all consumer threads die
			}
			
			br.close();		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		parse();		
	}
	
}
