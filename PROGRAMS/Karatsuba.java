import java.io.*;
import java.util.*;

public class Solution {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader input = new BufferedReader(new FileReader("./in1.txt"));
		//BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
		
		//read in degree
		String s = input.readLine();
		int deg = Integer.parseInt(s);
		int n = deg+1; //number of terms in each polynomial
		
		//read in each polynomial into array of its coefficients, starting from coefficient for x_0.
		String p0 = input.readLine();
		String p1 = input.readLine();
		
		String[] first = p0.split(" ");
		String[] second = p1.split(" ");
		
		long[] X = new long[first.length];
		long[] Y = new long[second.length];
		for (int i = 0; i < deg + 1; i++) {
			X[i] = Integer.parseInt(first[i]);
			Y[i] = Integer.parseInt(second[i]);
		}
	
		long[] K = Karatsuba(n, X, Y, output);
		
		for (int i=0; i<K.length; i++) {
			output.write(K[i] + " ");
		}
		
		input.close();
		output.close();

	}
	
	//First, identify your b,d,a, and c for any n-exp polynomial.
	//(b+ax)(d+cx) = bd + (ad+bc)x + acx^2
	public static long[] Karatsuba(int n, long[] X, long[] Y, BufferedWriter o) throws IOException {
		
		//size of b, a, d, c is n/2
		long[] b = Arrays.copyOfRange(X, 0, n/2); //does not include n/2
		long[] a = Arrays.copyOfRange(X, n/2, n); //does not include n
		
		long[] d = Arrays.copyOfRange(Y, 0, n/2);
		long[] c = Arrays.copyOfRange(Y, n/2, n);
		
		if (n<=8) { //reduce n using recursive Karatsuba. when n gets low enough, calculate using NAIVE
			return Naive(n, X, Y);
		}
		
		//to find mid. Confusing!!!
		long[] temp0 = new long[n/2];
		long[] temp1 = new long[n/2];
		
		for (int i=0; i<n/2; i++) {
			temp0[i] = b[i] + a[i];
			temp1[i] = d[i] + c[i];
		}
		
		//now, we can begin setting up our recursive calls.
		long[] K2 = Karatsuba(n/2, temp0, temp1, o); //this is for MIDDLE!! //DOES NOT STOP RECURSIVE CALLS!!
		long[] K1 = Karatsuba(n/2, b, d, o); //for b*d part
		long[] K3 = Karatsuba(n/2, a, c, o); //for a*c part
		
		//now, we can begin setting up K.
		long[] K = new long[2*n - 1]; //any two polynomials with n terms multiplied results in a 2*n-1 term poly.
		
		for (int i=0; i<n-1; i++) { //set first portion of K
			K[i] = K1[i];
		}
		
		for (int i=0; i<n-1; i++) { //set end portion of K
			K[n+i] = K3[i];
		}
		
		for (int i=0; i<n-1; i++) { //set middle portion of K
			K[(n/2)+i] += K2[i] - (K1[i] + K3[i]);
		}

		return K;
		
		
	}
	
	public static long[] Naive(int n, long[] X, long[] Y) {
		
		long[] K = new long[2*n - 1];
		
		for (int i=0; i<n; i++) {
			for (int j=0; j<n; j++) {
				
				K[i+j] += X[i]*Y[j];
				
			}
		}
		return K;
	}
	

}
