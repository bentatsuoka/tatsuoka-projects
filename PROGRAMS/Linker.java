import java.util.*;

public class Solution {

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		
		//absolute = base + relative
		//Maps symbols to ABSOLUTE addresses
		HashMap<String, String> symTable = new HashMap<String, String>();
		
		//Maps BASES for each M to INSTRUCTIONS for M
		ArrayList <Module> Instructions = new ArrayList <Module> ();
		
		int N = input.nextInt();
		
//PASS ONE: loop for all modules 
		
		//List of EACH USES MAP for EACH MODULE
		ArrayList <HashMap<String, LinkedList<Integer>>> AllUses = new ArrayList <HashMap<String, LinkedList<Integer>>>();
		//List of ALL DEFINED SYMBOLS
		ArrayList <String> Defined = new ArrayList<String>();
		//List of ALL SYMBOLS THAT ARE USED
		ArrayList <String> Used = new ArrayList<String>();

		int ins = 0; //counter for total number instructions
		int base = 0;
		for (int i=0; i<N; i++) { //for each module
			
			int numDefs = input.nextInt();

			//loop for each definition list
			HashMap<String, Integer> Defs = new HashMap<String, Integer>();
			for (int j=0; j<numDefs; j++) {
				
				String sym = input.next("([a-zA-Z0-9])+([a-zA-Z0-9]{0,8})"); //Need to handle symbol longer than 8

				int rel = input.nextInt();
				//System.out.println(rel);
				
				Defs.put(sym, rel);
				
				//base is global for each module, rel is dependent on EACH SYMBOL
				int abs = base + rel;
				String value = Integer.toString(abs);
				
				for (int a=0; a<Defined.size(); a++) {
					if (sym.equals(Defined.get(a))) { //DOUBLY DEFINED
						symTable.remove(Defined.get(a));
						Defined.remove(a);
						value += "  ERROR: " + sym + " is defined more than once. The latest definition will be used.";
					}
					
				}
				Defined.add(sym);
				
				symTable.put(sym, value);
			
			}
			
			int numUses = input.nextInt();
			
			//Map Symbols to their Locations for Symbols in Module i
			HashMap<String, LinkedList<Integer>> Uses = new HashMap<String, LinkedList<Integer>>();
			
			//loop for each use list
			ArrayList<Integer> Locations = new ArrayList<Integer>();
			int remove = -1; //index for duplicate use--delete from all other Symbol's LinkedLists
			for (int k=0; k<numUses; k++) { 
				
				
				String s = input.next("([a-zA-Z0-9])+([a-zA-Z0-9]{0,8})");
			
				if (!Used.contains(s)) {
					Used.add(s);
				}

				
				LinkedList<Integer> L = new LinkedList<Integer>();
				
				//add uses for symbol k to hashmap
				while (input.hasNextInt()) {
					
					int loc = input.nextInt();
					//System.out.println(loc);
					
					if (loc != -1) {
						
						L.add(loc);
						
						if (!Locations.contains(loc)) {
							Locations.add(loc);
						}
						
						else {
							remove = loc;
						}
						
						
					}
					
					else {
						break;
					}

				}
				
				if (remove != -1) {
					//then, a duplicate exists
						
					Iterator <Map.Entry<String,LinkedList<Integer>>> it0 = Uses.entrySet().iterator(); //for Map
						
					while (it0.hasNext()) {
							
						Map.Entry<String,LinkedList<Integer>> element = it0.next();
							
						LinkedList<Integer> L1 = (LinkedList<Integer>) element.getValue();
							
						Iterator<Integer> it1 = L1.iterator();
							
						while (it1.hasNext()) {
								
							int temp = it1.next();
								
							if (temp == remove) {

								removeByValue(L1, temp); //REPLACES VALUE WITH -2!!!
									
							}
						}
					}
						

				}
				
				Uses.put(s, L);
				
			}
			
			AllUses.add(Uses);
			
			int numInstr = input.nextInt();
			
			LinkedList<String> L1 = new LinkedList<String>();
			for (int l=0; l<numInstr; l++) {
				
				L1.add(input.next("[0-9]{5}"));
				ins++; //update total num instructions counter for error checking
			}
			
			Module M = new Module(base, L1);
			
			//Instructions.put(base, L1);
			Instructions.add(M);
			//update base
			base += numInstr;
			
			//CHECK ERROR for Definition larger than mod size error
			Iterator <Map.Entry<String,Integer>> it = Defs.entrySet().iterator();
			while (it.hasNext()) {
				
				Map.Entry<String,Integer> element = it.next();
				String cur = element.getKey();
				
				if (element.getValue() >= numInstr) { //IS THIS THE RIGHT LOGIC???
					//update with last word in the module
					int lastIndex = ins-1;
					String newStr = Integer.toString(lastIndex) + "  Error: Definition exceeds module size; last word in module used.";
					symTable.put(cur, newStr);
				}
			}

			
		}//**********END PASS ONE**********
		
//PASS 2: CARRY OUT INSTRUCTIONS
		
		//for storing updated addresses. Index of LL is module number!
		ArrayList<Module> Updated = new ArrayList<Module>();	
		
		int count = 0; //index for current mod in AllUses
		
		for (int i=0; i<Instructions.size(); i++) { //for each module
			
			//storing updated addresses for a base b
			LinkedList<String> L1 = new LinkedList<String>();
				
			Iterator<String> it1 = Instructions.get(i).L.iterator();
			
			int n=0; //COUNTER FOR POSITION OF CURRENT INSTRUCTION!
			int b = Instructions.get(i).base; //get current module's base

			
			while (it1.hasNext()) { //for each instruction
					//update instructions
				String cur = it1.next();
				char first = cur.charAt(0);
				char last = cur.charAt(4);
					
				String sub = cur.substring(1,4);
				String newSub = "";
					
				switch(last) {
				case '1': { //unchanged
					
					newSub = first+sub;
					L1.add(newSub);
					n++;
					break;
				}
						
				case '2': { //ABS ADDY>MACHINE SIZE ERROR HERE
					
					int temp = Integer.parseInt(sub);
					
					if (temp>300) {
						sub =  "299  Error: Absolute address exceeds machine size; largest legal value used.";
					}
					
					newSub = first + sub;
					
					L1.add(newSub);
					n++;
					break;
				}
				
				//THIS CASE WORKS!
				case '3': { //relocate relative address
					//THIS STRIPS ZEROES NEED LEFT EXTEND WITH 0's TO 3 TOTAl CHARACTERS
					int temp = Integer.parseInt(sub) + b;
					String st = String.format("%03d", temp);
					newSub = first+st;
					L1.add(newSub);
					n++;
					break;
				}
				
				case '4': { //resolve external reference
					
					
					//need to find key in Uses that contains a value equal to index n
					String symbol = findSymbol(AllUses.get(count), n);
						
					if (symbol=="") {
						System.out.println("Instruction not found in Table");
						n++;
						break;
					}
					
					//*****HERE CHECK IF UNDEFINED SYMBOL IS USED!!!!
					int val;
					String newMid;
					//assuming value of symbol is never greater than 999!!
					String tempSym = symbol.replace("*", "");
					if (!symTable.containsKey(tempSym)) {
						val = 111; //default value for missing key
						newMid = Integer.toString(val) + "  ERROR: " + symbol + " is not defined; 111 used."; //include error
						newSub = first + newMid;
					}
					else if (symTable.get(tempSym).length() <= 3) {
						if (symbol.charAt(0) == '*') {
							String temp = symbol.replace("*","");
							val = Integer.parseInt(symTable.get(temp));
							newMid = String.format("%03d", val);
							newSub = first + newMid + "  Error: Multiple variables used in instruction; Use in Module " + temp + " for instruction " + first + newMid + last + " is ignored.";
						}
						else {
							val = Integer.parseInt(symTable.get(tempSym));
							newMid = String.format("%03d", val);
							newSub = first + newMid;
						}
					}
					
					else { //include error message in VAL
						val = Integer.parseInt(symTable.get(tempSym).substring(0,3).replaceAll("\\s", "")); 
						newMid = String.format("%03d", val);
						newSub = first + newMid;
					}

					L1.add(newSub);
					n++;
					break;
				}
							
				default: System.out.println("Invalid Address Type");
					n++;
					break;
				} //end switch
				

					
				
			} //end while
			
			Module M = new Module(b, L1);
			Updated.add(M); //add base-addresses item to Updated list
			count++; //update module index
			
		} //end while
			
			
		printTable(symTable);
		
		printInstr(Updated);
		
		//DEFINED BUT NOT USED
		if (Defined.size() > Used.size()) {
			
			ArrayList<String> Unused = findDifference(Defined, Used);
			
			for (int i=0; i<Unused.size(); i++) {
				System.out.println("WARNING: " + Unused.get(i) + " was defined but unused.");
			}
		}
			
		input.close();
	
	}  //END MAIN
	
	//returns elements from A that are NOT INCLUDED in B
	@SuppressWarnings("unchecked")
	public static ArrayList<String> findDifference(ArrayList<String> A, ArrayList<String> B) {
		ArrayList<String> Temp = (ArrayList<String>) A.clone();
		Temp.removeAll(B);
		
		return Temp;
		
	}
	
	public static void removeByValue(LinkedList<Integer> L, int target) {
		
		Iterator <Integer> it = L.iterator();
		int index = 0;
		
		while (it.hasNext()) {
			int cur = it.next();
			if (cur == target) {
				//remove link
				L.set(index, -2); //REPLACES VAL WITH -2 TO SYMBOLIZE REPLACED VAL

			}
			index++;
		}
	}
	
	public static String findSymbol(HashMap<String, LinkedList<Integer>> U, int cur) {
		
		Iterator <Map.Entry<String,LinkedList<Integer>>> it0 = U.entrySet().iterator();
		
		String key = "";
		
		while (it0.hasNext()) {
			
			Map.Entry<String,LinkedList<Integer>> element = it0.next();
			
			key = element.getKey();
			
			LinkedList<Integer> L = (LinkedList<Integer>) element.getValue();
			
			Iterator<Integer> it1 = L.iterator();
			
			while (it1.hasNext()) {
				
				int temp = it1.next();
				
				if (temp==cur) {
					
					return key;
				}
				
				else if (temp==-2) {
					key = "*" + key; //marks duplicate syms for one instruct
				}
				
				else {
					continue; //keep searching
				}
				
		
			}
			
		}
		
		return key;
		
	}
	
	public static void printMap(HashMap<Integer, LinkedList<String>> M) {
		
		Iterator <Map.Entry<Integer,LinkedList<String>>> it0 = M.entrySet().iterator();
		
		while (it0.hasNext()) {
			
			int count = 0;
			
			Map.Entry<Integer,LinkedList<String>> element = it0.next();
			System.out.println("+" + element.getKey() + ": ");
			LinkedList<String> L = (LinkedList<String>) element.getValue();
			
			Iterator<String> it1 = L.iterator();

			while (it1.hasNext()) {
				System.out.println(count + ": " + it1.next());
				count++;
			}
			
			System.out.println();

		}
	}
	
	public static void printTable(HashMap<String, String> M) {
		
		Iterator <Map.Entry<String, String>>it = M.entrySet().iterator();
		
		System.out.println("SYMBOL TABLE:");
		while (it.hasNext()) {
			
			Map.Entry <String, String> element = it.next();
			System.out.println(element.getKey()+ ": " + element.getValue());
			
		}
		System.out.println();
	}
	
	public static void printUses(HashMap<String, LinkedList<Integer>> U) {
		
		System.out.println("USES TABLE");
		Iterator <Map.Entry<String,LinkedList<Integer>>> it0 = U.entrySet().iterator();
		
		while (it0.hasNext()) {
			
			Map.Entry<String,LinkedList<Integer>> element = it0.next();
			LinkedList<Integer> L = (LinkedList<Integer>) element.getValue();
			
			Iterator<Integer> it1 = L.iterator();
			
			System.out.print(element.getKey() + ":");
			
			while (it1.hasNext()) {
				System.out.print(it1.next() + " ");
			}
			
			System.out.println();
		}
	}
	
	public static void printInstr(ArrayList<Module> S) {
		
		System.out.println("MEMORY MAP:");
		for (int i=0; i<S.size(); i++) {
			
			System.out.println("+" + S.get(i).base + ":");
			Iterator<String> it = S.get(i).L.iterator();
			int j=0;
			
			while (it.hasNext()) {
				System.out.println(j+": " + it.next());
				j++;
			}
		}
		System.out.println();
	}
	
	public static void printAL(ArrayList<String> S) {
		
		for (int i=0; i<S.size(); i++) {
			System.out.print(S.get(i) + " ");
		}
		System.out.println();
		
	}
	
public static void printIAL(ArrayList<Integer> S) {
		
		for (int i=0; i<S.size(); i++) {
			System.out.print(S.get(i) + " ");
		}
		System.out.println();
		
	}
	
	
	public static void printList(LinkedList<String> L) {
		Iterator<String> it = L.iterator();
		while (it.hasNext()) {
			System.out.print(it.next() + " ");
		}
		System.out.println();
	}
	

}

class Module {
	int base;
	LinkedList<String> L;
	
	public Module(int base, LinkedList<String> L) {
		this.base = base;
		this.L = L;
	}
	
	
}
