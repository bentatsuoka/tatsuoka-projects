import java.io.*;
import java.util.HashMap;

public class Solution {

	public static void main(String[] args) throws IOException {
		
		BufferedReader input = new BufferedReader(new FileReader("./sample-IO/sample-input/input3.txt"));
		//BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
		
		//initialize array mapping to Scoring matrix
		char[] Verts = popVerts();
		
		
		//read in data sequences, load each into char array
		String[] data = new String[2];
		int size = data.length;
		
		for (int i=0; i<size; i++) {
			String s = input.readLine();
			data[i] = s;
		}
		
		char[] seq1 = data[0].toCharArray();
		char[] seq2 = data[1].toCharArray();
		
		int len1 = seq1.length;
		int len2 = seq2.length;
		
		//printSeq(seq1, output);
		//printSeq(seq2, output);
		
		int gapPen = -2;
		int match = 3;
		int misMatch = -3;
		
		String optA = "";
		String optB = "";
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int[][] DP = new int[len1+1][len2+1];
		
		//initialize 
		for (int i=0; i<5; i++) {
			map.put("" + Verts[0] + Verts[i], gapPen);
			map.put("" + Verts[i] + Verts[0], gapPen);
		}
		
		for (int i=1; i<5; i++) {
			for (int j=1; j<5; j++) {
				
				if (Verts[i] == Verts[j]) {
					map.put("" + Verts[i] + Verts[j], match);
				} 
				else {
					map.put("" + Verts[i] + Verts[j], misMatch);
				}
			}
		}
		
		for (int i=0; i<len1+1; i++) {
			DP[i][0] = i*gapPen;
		}
		
		for (int i=0; i<len2+1; i++) {
			DP[0][i] = i*gapPen;
		}
		
		for (int i = 1; i < len1 + 1; i++) {
			for (int j = 1; j < len2 + 1; j++) {
				int temp = Math.max(DP[i-1][j-1] + map.get("" + seq1[i-1] + seq2[j-1]), DP[i][j-1] + map.get("" + Verts[0] + seq2[j-1]));
				DP[i][j] = Math.max(temp, DP[i-1][j] + map.get("" + seq1[i-1] + Verts[0]));
			}
		}
		
		//indexes
		 int x = len1;
		 int y=len2;
		 
		 //work backwards through the sequences
		 while (x>0 && y>0) {
			if (DP[x][y] - map.get("" + seq1[x-1] + seq2[y-1]) == DP[x-1][y-1]) {
				optA = seq1[x-1] + optA;
				optB = seq2[y-1] + optB;
				x--;
				y--;
			}
				
			else if (DP[x][y] - gapPen == DP[x][y-1]) {
				optA = "-" + optA;
				optB = seq2[y-1] + optB;
				y--;
			}
			
			else if (DP[x][y] - gapPen == DP[x-1][y]) {
				optA = seq1[x-1] + optA;
				optB = "-" + optB;
				x--;
			}
		}
		 
		if (x>0) {
			while (x>0) {
				optA = seq1[x-1] + optA;
				optB = '-' + optB;
				x--;
			}
		} 
		
		else if (y>0) {
			while (y>0) {
				optA = '-' + optA;
				optB = seq2[y-1] + optB;
				y--;
			}
		}
		
		output.write(DP[len1][len2] + "\n");
		output.write(optA + "\n");
		output.write(optB + "\n");
		
		
		
		/*
		ArrayList<Character> opt1 = new ArrayList<Character>();
		ArrayList<Character> opt2 = new ArrayList<Character>();
		
		int total = 0;
		
		int sum = Align(seq1, seq2, M, Verts, opt1, opt2, len1-1, len2-1, total);
		output.write(sum + "\n");
		
		printOpt(opt1, output);
		printOpt(opt2, output);
		*/
		input.close();
		output.close();

	}
	
	
	
	/*
	//work backwards through, comparing the sequences.
	//HOW do you know when to use a mismatch when it will optimize the score later on?
	public static int Align(char[] s1, char[] s2, int[][]S, char[] V, ArrayList<Character> o1, ArrayList<Character> o2, int i, int j, int total) {
		
		if ((i==0) && (j==0)) {
			o1.add(s1[i]);
			o2.add(s2[j]);
			if (s1[i] == s2[j]) {
				total += 3;
			}
			
			else {
				total += -3;
			}
			
			return total;
		}
		
		else if (i==0) { //(-,j)
			o1.add('-');
			o2.add(s2[j]);
			int ind = getIndex(s2[j], V);
			total += S[4][ind];
			return Align(s1, s2, S, V, o1, o2, i, j-1, total);
		}
		
		else if (j==0) { //(i,-)
			o1.add(s1[i]);
			o2.add('-');
			int ind = getIndex(s1[i], V);
			total += S[ind][4];
			return Align(s1, s2, S, V, o1, o2, i-1, j, total);
		}
		
		//else i and j are both within the sequences
		else {
			//TODO. Need to use Memoization?
			int xInd = getIndex(s1[i], V);
			int yInd = getIndex(s2[j], V);
			
			//max can either be -2 or 3. it cannot be -3.
			int max = getMax(S[xInd][4], S[4][yInd], S[xInd][yInd]);
			
			total += max;
			
			if (max==-2) {
				
				if (i<j) {
					o1.add('-');
					o2.add(s2[j]);
					return Align(s1, s2, S, V, o1, o2, i, j-1, total);

				}
				
				else {
					o1.add(s1[i]);
					o2.add('-');
					return Align(s1, s2, S, V, o1, o2, i-1, j, total);
				}
				
			}
			
			else {
				
				o1.add(s1[i]);
				o2.add(s2[j]);
				return Align(s1, s2, S, V, o1, o2, i-1, j-1, total);
				
			}
			
		}
	}
	*/
	
	public static int getMax(int one, int two, int three) {
		
		if ((one>=two) && (one>=three)) 
			return one;
		
		else if ((two>=one) && (two>=three))
			return two;
		
		else
			return three;
	}
	
	public static int getIndex(char c, char[] V) {
		
		for (int i=0; i<V.length; i++) {
			if (c==V[i])
				return i;
		}
		return -1;
	}
	
	public static char[] popVerts() {
		
		char[] V = new char[5];
		
		V[0] = '-';
		V[1] = 'A';
		V[2] = 'C';
		V[3] = 'G';
		V[4] = 'T';
		
		return V;
		
	}

	public static void popMatrix(int[][] S) {
		
		for (int i=0; i<5; i++) {
			for (int j=0; j<5; j++) {
				
				if (i == 4 && j==4) {
					S[i][j] = 100000;//temp value this will never be reached
				}
				
				else if (i==4 || j==4) {
					S[i][j] = -2;
				}
				
				else if (i == j) {
					S[i][j] = 3;
				}
				
				else if (i != j) {
					S[i][j] = -3;
				}
			}
		}
		
	}
	
	public static void printMatrix(int[][] S, BufferedWriter o) throws IOException {
		
		for (int i=0; i<S.length; i++) {
			for (int j=0; j<S[i].length; j++) {
				
				o.write(S[i][j] + " ");
				
			}
			o.write("\n");
		}
		
	}
	
	public static void printSeq(char[] seq, BufferedWriter o) throws IOException {
		
		int len = seq.length;
		
		for (int i=0; i<len; i++) {
			o.write(seq[i]);
		}
		o.write("\n");
	}
	
	/*
	public static void printOpt(ArrayList<Character> S, BufferedWriter o) throws IOException {
		
		int len = S.size();
		
		for (int i=len-1; i>=0; i--) {
			o.write(S.get(i));
		}
		o.write("\n");
	}
	*/

}
