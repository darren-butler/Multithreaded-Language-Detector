package ie.gmit.sw;

/**
 * Custom type that implements Comparable to allow k-mers to be sorted by their frequency and assigned a ranking
 * @author Darren
 *
 */
public class Kmer implements Comparable<Kmer> {
	private int hash;
	private int frequency;
	private int rank;

	public Kmer(int kmer, int frequency) {
		super();
		this.hash = kmer;
		this.frequency = frequency;
	}

	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public int compareTo(Kmer next) {
		return -Integer.compare(frequency, next.getFrequency());
	}

	@Override
	public String toString() {
		return "[" + hash + "/" + frequency + "/" + rank + "]";
	}
}