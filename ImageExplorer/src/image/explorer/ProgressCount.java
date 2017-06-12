package image.explorer;

public class ProgressCount {
	private int count = 0;
	
	public synchronized int increment() {
		count++;
		return count;
	}

	public synchronized void reset() {
		count = 0;
	}
	
	public synchronized int getCount() {
		return count;
	}
}
