package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {

	public static void main(String[] args) {
		Database database = new Database();
		
		BlockingQueue<DataTask> dataQ = new ArrayBlockingQueue<>(100);
		ExecutorService dataExecutor = Executors.newFixedThreadPool(5);
		
		//wili-2018-Small-11750-Edited
		String str = "wili-2018-Small-11750-Edited.txt";
		DataProcessor parser = new DataProcessor(str, dataQ); 
		
		dataExecutor.execute(parser);
		
		dataExecutor.execute(new DataWorker(database.getDb(), dataQ)); //TODO config thread pool and blockingqueue sizes
		dataExecutor.execute(new DataWorker(database.getDb(), dataQ));
		dataExecutor.execute(new DataWorker(database.getDb(), dataQ));
		dataExecutor.execute(new DataWorker(database.getDb(), dataQ));
		
		dataExecutor.shutdown();
		
		do {
			// run until threadpool is done //TODO add better wait implementation
		}while(!dataExecutor.isTerminated());
		
		// Format DB
		database.resize(300);
		//System.out.println(database);
		
		
		BlockingQueue<QueryTask> queryQ = new ArrayBlockingQueue<>(100);
		ExecutorService queryExecutor = Executors.newFixedThreadPool(4);
		
		
		str = "query.txt"; // have 3 or 4 test files with different languages to auto check for faster debugging verification it works
		QueryProcessor query = new QueryProcessor(str, queryQ);
		QueryWorker qw = new QueryWorker(queryQ);
		
		queryExecutor.execute(query);
		queryExecutor.execute(new QueryWorker(queryQ));
		queryExecutor.execute(new QueryWorker(queryQ));
		queryExecutor.execute(new QueryWorker(queryQ));

		
		queryExecutor.shutdown();
		do {
			// run until threadpool is done //TODO more graceful implementation
		}while(!queryExecutor.isTerminated());
		
		query.resize(300);
		
		
		System.out.println(database.getLanguage(query.getMap()));

	}
}
