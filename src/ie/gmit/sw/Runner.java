package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
	public static void main(String[] args) {
		
		Database database = new Database();

		BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);
		ExecutorService executor = Executors.newFixedThreadPool(5);
		//wili-2018-Small-11750-Edited
		String str = "wili-2018-Small-11750-Edited.txt";
		Parser parser = new Parser(queue, str, 1); //TODO remove kmersize?
		
		executor.execute(parser);
		
		executor.execute(new Worker(database.getDb(), queue)); //TODO config thread pool and blockingqueue sizes
		executor.execute(new Worker(database.getDb(), queue));
		executor.execute(new Worker(database.getDb(), queue));
		executor.execute(new Worker(database.getDb(), queue));
		
		executor.shutdown();
		
		do {
			
		}while(!executor.isTerminated());
		
		
		// Format DB
		database.resize(300);
		//System.out.println(database);
		String queryDataSource = "query.txt";
		Query query = new Query(queryDataSource);
		
		Thread t = new Thread(query);
		t.start();
		try {
			t.join();

			System.out.println(database.getLanguage(query.getQueryMap()));
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	} 
	
	
}
