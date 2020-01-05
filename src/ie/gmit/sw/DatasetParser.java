package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatasetParser extends Parser {

	private Database database = null;
	private BlockingQueue<KmerTask> queue = new ArrayBlockingQueue<>(1000); //TODO remove Integer
	
	public DatasetParser(String dataSource, int kmerSize, BlockingQueue<KmerTask> queue) {
		super(dataSource, kmerSize);
		this.queue = queue;
	}
	
	public void setDatabase(Database database) {
		this.database = database;
	}
	
	@Override
	public void parse(String text, String lang, int... ks) throws Throwable {
		Language language = Language.valueOf(lang);
		
		for (int i = 0; i <= text.length() - this.getKmerSize(); i++) {
			String kmer = text.substring(i, i + this.getKmerSize());
			queue.put(new KmerTask(language, kmer));
		}
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.getDataSource())));
			String line = null;

			while ((line = br.readLine()) != null) {
				String[] record = line.trim().split("@");
				if (record.length != 2)
					continue;
				parse(record[0], record[1]);
				//System.out.println(record[1]);
			}		
			queue.put(new Poison(null, null));	
			br.close();
			//System.out.println(database);
		} catch (Throwable e) {
			e.printStackTrace();
		}		
	}	
}
