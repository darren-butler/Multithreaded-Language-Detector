package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {

	public static void main(String[] args) {

		Menu menu = new Menu();
		boolean keepGoing = true;
		Database database = new Database();
		Dataset dataset = null;

		while (keepGoing) {

			menu.runMenu();

			switch (menu.getOption()) {
			case 0:
				System.out.println("\n\tQuitting...");
				keepGoing = false;
				break;
			case 1:
				// String str = "wili-2018-Small-11750-Edited.txt"; // menu.inputFile();
				String str = menu.inputFile();
				BlockingQueue<DataTask> dataQ = new ArrayBlockingQueue<>(100);
				ExecutorService dataExecutor = Executors.newFixedThreadPool(5);

				dataset = new Dataset(str, dataQ);

				Long time = System.nanoTime();

				System.out.print("\n\tBuilding subject database...");

				dataExecutor.execute(dataset);
				dataExecutor.execute(new DataWorker(database.getDb(), dataQ));
				dataExecutor.execute(new DataWorker(database.getDb(), dataQ));
				dataExecutor.execute(new DataWorker(database.getDb(), dataQ));
				dataExecutor.execute(new DataWorker(database.getDb(), dataQ));

				dataExecutor.shutdown();

				do {
					// wait until threadpool is done //TODO find more gracefull implementation?
				} while (!dataExecutor.isTerminated());
				database.resize(300);

				if (database.getDb().size() > 1) {
					time = System.nanoTime() - time;
					double timeSeconds = (double) time / 1000000000;
					System.out.println("\n\n\tData set proccessed in: " + timeSeconds + "s");
				}else {
					System.out.println("\n\n\tERROR - Dataset not constructed");
				}

				break;
			case 2:
				BlockingQueue<QueryTask> queryQ = new ArrayBlockingQueue<>(100);
				ExecutorService queryExecutor = Executors.newFixedThreadPool(4);

				// str = "query.txt"; // TODO debugging tool
				if (dataset == null) {
					System.out.println("\n\tERROR - Dataset missing");
					break;
				}
				str = menu.inputFile();

				Query query = new Query(str, queryQ);
				QueryWorker qw = new QueryWorker(queryQ);

				queryExecutor.execute(query);
				queryExecutor.execute(new QueryWorker(queryQ));
				queryExecutor.execute(new QueryWorker(queryQ));
				queryExecutor.execute(new QueryWorker(queryQ));

				queryExecutor.shutdown();
				do {
					// run until threadpool is done //TODO more graceful implementation
				} while (!queryExecutor.isTerminated());

				query.resize(300);

				if (!query.getMap().isEmpty()) {
					System.out.println("\n\tLanguage detected: " + database.getLanguage(query.getMap()));
				}

				break;
			default:
				System.out.println("INVALID INPUT");
			}

		}

	}
}
