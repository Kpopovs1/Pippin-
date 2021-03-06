package pippin;

public class Memory {
	public static final int DATA_SIZE = 512;
	private int[] data = new int[DATA_SIZE];
	private int changedIndex = -1;

	public int getData(int index){
		int retVal = data[index];
		return retVal;
	}

	public int getChangedIndex(){
		return changedIndex;
	}

	public void setData(int index, int value){
		data[index] = value;
		changedIndex = index;
	}

	int[] getData(){
		return data;
	}

	public void clear() {
		for(int i = 0; i < DATA_SIZE; i++) {
			data[i] = 0;
		}
		changedIndex = -1;
	}
}
