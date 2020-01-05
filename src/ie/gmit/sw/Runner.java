package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Runner {

	public static void main(String[] args) throws InterruptedException {
		Menu menu = new Menu();
		boolean keepGoing = true;
		
		while (keepGoing) {
			
			menu.runMenu();
			
			switch (menu.getOption()) {
				case 0:
					System.out.println("\n\tQuitting...");
					keepGoing = false;
					break;
				case 1:
					String str = menu.inputFile();
					BlockingQueue<KmerTask> queue = new ArrayBlockingQueue<>(1000);
					
					DatasetParser datasetParser = new DatasetParser(str, 2, queue);
					Database database = new Database(queue);
					datasetParser.setDatabase(database);
					
					Long time = System.nanoTime();
					
					Thread parserThread = new Thread(datasetParser);
					Thread databaseThread = new Thread(database);
					
					parserThread.start();
					databaseThread.start();
					
					System.out.print("\n\tBuilding subject database...");
					
					parserThread.join();
					databaseThread.join();

					time = System.nanoTime() - time;
					double timeSeconds =  (double)time/1000000000;
					System.out.println("\n\n\tData set proccessed in: "  + timeSeconds + "s");
					break;
				case 2:
					System.out.println("OPTION 2");
					break;
				default:
					System.out.println("INVALID INPUT");
			}
			
			//System.out.println("keepgoing: " + keepGoing);
		}
	}

}
