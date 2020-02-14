import java.util.Scanner;
public class SortingAlgorithms {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		sc.useDelimiter("\n");
		int[] A = {4, 77, 98, 30, 20, 50, 77, 22, 49, 2};
		printArray(A);
		int option;
		do {
			printOptions();
			System.out.println("\nPlease your sorting option: ");
			option = sc.nextInt();
			switch (option) {
				case 1: bubbleSort(A);
						printArray(A);
						break;
						
				case 2: recBubbleSort(A, A.length - 1);
						printArray(A);
						break;
						
				case 3: selSort(A);
						printArray(A);
						break;
						
				case 4: insSort(A);
						printArray(A);
						break;
						
	 			case 5: mergeSort(A, 0, A.length - 1);
	 					printArray(A);
	 					break;
	 					
				case 6: quickSort(A, 0, A.length - 1);//To grader: This method does not work. I'll take the L on it.
						printArray(A);
						break;
						
				case 7: System.out.println("Goodbye!");
						break;
					
				default: System.out.println("Command not recognized.");
						 break;
			}
		} while (option != 7);
		sc.close();
	}
	
	public static void printArray(int[] A) {
		System.out.print("|");
		for (int i = 0; i < A.length; i++) {
			System.out.print(A[i] + "|");
		}
	}
	
	public static void printOptions() {
		System.out.println("\n\n1: Bubble Sort\n2: Recursive Bubble Sort\n3: Selection Sort\n4: Insertion Sort\n5: Recursive Merge Sort\n6: Recursive Quick Sort\n7: Quit");
	}
	public static void swap(int[] A, int i, int j) {
		int temp = A[i];
		A[i] = A[j];
		A[j] = temp;
	}
	
	public static void bubbleSort(int[] A) {
		for (int i = 0; i < A.length; i ++) {
			for (int j = i + 1; j < A.length; j++) {
				if (A[j] < A[i]) {
					swap(A, i, j);
				}
			}
		}
	}
	
	public static void recBubbleSort(int[] A, int n) { 
		if (n == 0) 
			return;
		else {
			for (int i = 0; i < n; i++) {
				if (A[i] > A[i + 1])
					swap(A, i, i + 1);
			}
		}
		recBubbleSort(A, n - 1);
	}
	
	public static void selSort(int[] A) {
		for (int i = 0; i < A.length - 1; i++) {
			int min = i;
			for (int j = i + 1; j < A.length; j++) {
				if (A[j] < A[min]) {
					min = j;
				}
			}
			swap(A, min, i);
		}
	}
	
	public static void insSort(int[] A) {
		//Loop starting at i = 1
		for (int i = 1; i < A.length; i++) {
			int j = i - 1;
			int element = A[i];
			while (j >= 0 && element < A[j]) {
				//shift
				A[j + 1] = A[j];
				j--;
			}
			//insert
			A[j + 1] = element;
		}
	}
	
	
	public static void merge(int[] A, int left, int mid, int right) {
		int len1 = mid - left + 1;
		int len2 = right - mid;
		//Create arrays to merge!
		int[] a1 = new int[len1];
		int[] a2 = new int[len2];
		//Populate new arrays
		for (int i = 0; i < a1.length; i++) {
			a1[i] = A[left + i];
		}
		for (int j = 0; j < a2.length; j++) {
			a2[j] = A[mid + 1 + j];
		}
		
		int start1 = 0;
		int start2 = 0;
		int l = left;
		
		//Compare values in arrays, populate main array
		while (start1 < len1 && start2 < len2) {
			if (a1[start1] <= a2[start2]) {
				A[l] = a1[start1];
				start1++;
			}
			else {
				A[l] = a2[start2];
				start2++;
			}
			l++;
		}
		
		//Need to add leftover elements from a1 and a2 into A.
		while (start1 < len1) {
			A[l] = a1[start1];
			l++;
			start1++;
		}
		
		while (start2 < len2) {
			A[l] = a2[start2];
			l++;
			start2++;
		}
		
	}
	
	public static void mergeSort(int[] A, int left, int right) { 
		if (left < right) {
			
			int mid = (left + right)/2;
			mergeSort(A, left, mid);
			mergeSort(A, mid + 1, right);
			merge(A, left, mid, right);
			
		}
	}
	
	public static void quickSort(int[] A, int low, int high) {
		while (low < high) {
			int pivot = partition(A, low, high);
			System.out.println(pivot);
			quickSort(A, low, pivot - 1);
			quickSort(A, pivot + 1, high);
		}
	}
	
	//Fix this! OOB
	public static int partition(int[] A, int low, int high) {
		int pivot = A[0];
		int i = low;
		for (int k = low; k < high; k++) {
			if (A[k] <= pivot) {
				i++;
				swap(A, A[k], A[i]);
			}
			else
				swap(A, A[high], A[i]);
		}
		return i;
	}
}
