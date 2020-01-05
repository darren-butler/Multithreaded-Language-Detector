package ie.gmit.sw;

public class Kmer implements Comparable<Kmer> {

	private int kmerHash;
	private int frequency;
	private int rank;
	
	public Kmer(int kmerHash, int frequency) {
		this.kmerHash = kmerHash;
		this.frequency = frequency;
	}
	
	public int getKmerHash() {
		return kmerHash;
	}

	public void setKmerHash(int kmerHash) {
		this.kmerHash = kmerHash;
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
		return "[" + kmerHash + "/" + frequency + "/" + rank + "]";
	}

}
