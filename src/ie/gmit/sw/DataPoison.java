package ie.gmit.sw;

/**
 * This class kills off a worker thread, <code>DataWorker</code>, when a <code>DataTask</code> of this type is taken from a <code>BlockingQueue</code>.<br>
 * All <code>DataWorker</code> threads will eventually <code>take()</code> a <code>DataTask</code> of this type from the <code>BlockingQueue</code>
 * @author Darren
 *
 */
public class DataPoison extends DataTask {

	public DataPoison(Language language, String text) {
		super(language, text);
	}
}
