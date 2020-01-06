package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * main()
 * @author Darren
 *
 */
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
				String str = menu.inputFile();
				BlockingQueue<DataTask> dataQ = new ArrayBlockingQueue<>(100);
				ExecutorService dataExecutor = Executors.newFixedThreadPool(5); // 1 producer, 4 consumers

				dataset = new Dataset(str, dataQ);

				System.out.print("\n\tBuilding subject database...");
				Long time = System.nanoTime();
				dataExecutor.execute(dataset);
				dataExecutor.execute(new DataWorker(database.getDb(), dataQ));
				dataExecutor.execute(new DataWorker(database.getDb(), dataQ));
				dataExecutor.execute(new DataWorker(database.getDb(), dataQ));
				dataExecutor.execute(new DataWorker(database.getDb(), dataQ));

				dataExecutor.shutdown();

				do {
					// wait until all dataExecutor threads are done //TODO more graceful implementation?
				} while (!dataExecutor.isTerminated());
				database.resize(300);

				if (database.getDb().size() > 1) {
					time = System.nanoTime() - time;
					double timeSeconds = (double) time / 1000000000;
					System.out.println("\n\n\tData set proccessed in: " + timeSeconds + "s");
				} else {
					System.out.println("\n\n\tERROR - Dataset not constructed");
				}

				break;
			case 2:
				BlockingQueue<QueryTask> queryQ = new ArrayBlockingQueue<>(100);
				ExecutorService queryExecutor = Executors.newFixedThreadPool(4);

				if (dataset == null) {
					System.out.println("\n\tERROR - Dataset missing");
					break;
				}
				str = menu.inputFile();

				Query query = new Query(str, queryQ);
				queryExecutor.execute(query);
				queryExecutor.execute(new QueryWorker(queryQ));
				queryExecutor.execute(new QueryWorker(queryQ));
				queryExecutor.execute(new QueryWorker(queryQ));

				queryExecutor.shutdown();

				do {
					// run until all queryExecutor threads are done //TODO more graceful implementation?
				} while (!queryExecutor.isTerminated());
				query.resize(300);

				if (!query.getMap().isEmpty()) {
					System.out.println("\n\tLanguage detected: " + database.getLanguage(query.getMap())); // output the detected language
				}

				break;
			default:
				System.out.println("\n\tERROR - Invalid input");
			}

		}

	}
}
