import java.io.*;
import java.util.*;

public class Sol {

	public static void main(String[] args) throws IOException {
		
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
		
		BufferedReader input = new BufferedReader(new FileReader("./sample-io/in0.txt"));
		//BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		String s = input.readLine();
		int size = Integer.parseInt(s);
		
		MinHeap H = new MinHeap(size);
		
		for (int i = 0; i < size; i++) {
			String soldier = input.readLine();
			String[] data = soldier.split(" ");
			Long score = Long.parseLong(data[1]);
			SNode n = new SNode(data[0], score);
			H.insert(n, output);
		}
		
		H.setIndexes();
		
		H.printHeapArr(output, H.M);
		
		
		for (Map.Entry<String,Integer> entry : H.M.entrySet())  
            output.write("Key = " + entry.getKey() + 
                             ", Value = " + entry.getValue() + "\n");
        
		
		String st = input.readLine();
		int numQueries = Integer.parseInt((st));
		
		//loop through queries, returning output after each iteration
		//where do you use the hash map?
		for (int i = size + 1; i < size + numQueries + 1; i++) {
			String query = input.readLine();
			char q = query.charAt(0);
			query = query.substring(2);
			
			
			if (q == '1') {
				String[] data = query.split(" ");
				long value = Long.parseLong(data[1]);
				H.update(data[0], value, output); //updates M as well!
			}
			
			
			else if (q == '2') {
				long val = Long.parseLong(query);
				H.eval(val, output); 
				output.write(H.currentSize + "\n");
			}
			
			
			
				
		}
		
		for (Map.Entry<String,Integer> entry : H.M.entrySet())  
            output.write("Key = " + entry.getKey() + 
                             ", Value = " + entry.getValue() + "\n");
		
		H.printHeapArr(output, H.M);
		
		input.close();
		output.flush();

	}

}

class MinHeap {

	int maxSize;
	int currentSize;
	SNode[] heapArr;
	//M should map Node to position in heapArr!
	HashMap <String, Integer> M;
	
	public MinHeap(int maxS) {
		this.maxSize = maxS;
		this.currentSize = 0;
		this.heapArr = new SNode[maxSize];
		this.M = new HashMap <String, Integer>();
	}
	
	//HashMap is updated for insert in main
	public void insert(SNode n, BufferedWriter o) throws IOException {
		if (currentSize == maxSize)
			return;
		
		//add the node to the end of the array
		heapArr[currentSize] = n;
		//restore
		trickleUp(currentSize, o);
		currentSize++;
		
	}
	
	public void setIndexes() {
		int count = 0;
		for (int i=0; i<currentSize; i++) {
			M.put(heapArr[i].name, count);
			count++;
		}
	}
	
	public void update(String key, long val, BufferedWriter o) throws IOException {
		if (M.get(key) == null) 
			return;
		
		int i = M.get(key);
		heapArr[i].value += val;
		trickleUp(i, o);

	}
	
	
	public void eval(long k, BufferedWriter o) throws IOException {
		//o.write("has to be above " + k + "\n");
		
		for (int i = 0; i < currentSize; i++) {
			
			if (heapArr[i].value < k) {
				remove(i, o);
			}
		}
		
		//check again to make sure no values less than k remain
		//Why do you need 2 checks??
		for (int j=0; j<currentSize; j++) {
			
			if (heapArr[j].value < k) {
				remove(j, o);
			}
		}
		
	}
	
	//output is now correct!
	public void remove(int i, BufferedWriter o) throws IOException {
		
		Integer j = M.remove(heapArr[i].name);
		if (j==null)
			return;
		
		heapArr[i] = heapArr[currentSize-1];

		M.put(heapArr[i].name, j);
		currentSize--;
		trickleDown(currentSize);

	}
	

	
	public void trickleDown(int index) {
		
		int smallest = index;
			
		int leftI = 2*index + 1;
		int rightI = leftI + 1;
		
		if ((leftI < currentSize) && heapArr[leftI].value < heapArr[index].value) {
			smallest = leftI;
		}
		
		if ((rightI < currentSize) && (heapArr[rightI].value < heapArr[smallest].value)) {
			smallest = rightI;
		}
		
		
		if (smallest != index) {
			
			swap(index, smallest);
			
			trickleDown(smallest);
			
		}
			

	
	}
	
	//THERE IS A LOGIC ERROR IN HERE NOT UPDATING ONE OF THE INDEXES CORRECTLY!!!!
	public void trickleUp(int index, BufferedWriter o) throws IOException {
		
		int parent = (index)/2;
		SNode toSwap = heapArr[index];
		
		if ((index > 0) && (heapArr[parent].value > toSwap.value)) {
			
			swap(index, parent);
			
			trickleUp(parent, o);

		}
	
	}
	
	public void swap(int i, int j) {
		//M.remove(heapArr[j].name);
		//M.remove(heapArr[i].name);
		M.put(heapArr[j].name, i);
		M.put(heapArr[i].name, j);
		
		SNode temp = heapArr[i];
		heapArr[i] = heapArr[j];
		heapArr[j] = temp;
	}
	
	public void printHeapArr(BufferedWriter o, HashMap<String, Integer> M) throws IOException {
		int size = currentSize;
		for (int i = 0; i < size; i++) {
			o.write(heapArr[i].name + "," + heapArr[i].value + "," +  M.get(heapArr[i].name) + " ");
		}
		o.write("\n");
	}	
}

class SNode {
	String name;
	long value;
	
	public SNode(String key, long val) {
		this.name = key;
		this.value = val;
	}
}
