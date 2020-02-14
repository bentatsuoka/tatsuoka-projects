import java.io.*;

public class Solution {
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader input = new BufferedReader(new FileReader("./sample_io/test5.in"));
		//BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
		
		String s = input.readLine();
		int size = Integer.parseInt(s);
		
		Tree T = new Tree();
		
		for (int i = 0; i < size; i++) {
			String query = input.readLine();
			//output.write(query + "\n");
			char q = query.charAt(0);
			query = query.substring(2);

			switch(q) {
				case ('1'): 
					String[] data = query.split(" ");
					int fee = Integer.parseInt(data[1]);
					insert(data[0], fee, T);
					break;
				case ('2'): 
					String[] data1 = query.split(" ");
					String first = data1[0];
					String last = data1[1];
					if (first.compareTo(last) > 0) {
						String temp = first;
						first = last;
						last = temp;
					}
					int addBy = Integer.parseInt(data1[2]);
					addRange(first, last, T.root, T.height, addBy);
					break;
				case ('3'): 
					//output.write(query + "\n");
					int f = getFee(T.root, query, 0, T.height, output);
					output.write(f + "\n");
					break;
				default:
					output.write("uhhhh" + "\n");
					break;
			}
		}
		
		input.close();
		output.flush();
		
	}
	
	static void addAll(Node localRoot, int h, int inc) {
		
		if (h==0) {
			
			((LeafNode)localRoot).value += inc;
			
		}
		else {
			
			((InternalNode)localRoot).value += inc;
		}
		
	}
	
	static void addLE(Node localRoot, String constraint, int h, int inc) {
		
		if (h==0) {
			
			if (constraint.compareTo(((LeafNode)localRoot).guide) >= 0) {
				((LeafNode)localRoot).value += inc;
				
			}
			
		}
		
		else {
			
			if (constraint.compareTo(((InternalNode)localRoot).child0.guide) <= 0) {
				addLE(((InternalNode)localRoot).child0, constraint, h-1, inc);
			}
			
			else if ((((InternalNode)localRoot).child2==null) || (constraint.compareTo(((InternalNode)localRoot).child1.guide) <= 0)) {
				addAll(((InternalNode)localRoot).child0, h-1, inc);
				addLE(((InternalNode)localRoot).child1, constraint, h-1, inc);
			}
			
			else {
				addAll(((InternalNode)localRoot).child0, h-1, inc);
				addAll(((InternalNode)localRoot).child1, h-1, inc);
				addLE(((InternalNode)localRoot).child2, constraint, h-1, inc);
			}
			
		}
		
	}
	
	static void addGE(Node localRoot, String constraint, int h, int inc) {
		
		if (h==0) {
			
			if (constraint.compareTo(((LeafNode)localRoot).guide) <= 0) {
				((LeafNode)localRoot).value += inc;
			}
			
		}
		
		else {
			
			if (constraint.compareTo(((InternalNode)localRoot).child0.guide) <= 0) {
				addGE(((InternalNode)localRoot).child0, constraint, h-1, inc);
				addAll(((InternalNode)localRoot).child1, h-1, inc);
				
				if (((InternalNode)localRoot).child2 != null) {
					addAll(((InternalNode)localRoot).child2, h-1, inc);
				}
			}
			
			else if ((((InternalNode)localRoot).child2==null) || (constraint.compareTo(((InternalNode)localRoot).guide) <= 0)) {
				addGE(((InternalNode)localRoot).child1, constraint, h-1, inc);
				
				if (((InternalNode)localRoot).child2 != null) {
					addGE(((InternalNode)localRoot).child2, constraint, h-1, inc);
				}
			}
			
			else {
				addGE(((InternalNode)localRoot).child2, constraint, h-1, inc);
			}
			
		}
		
	}
	
	//Need AddLE and AddGE functions as well, then modify addRange recursive calls to be these
	static void addRange(String first, String last, Node localRoot, int h, int inc) {
		
		if (h==0) {
			
			if ((first.compareTo(((LeafNode)localRoot).guide) <= 0) && (last.compareTo(((LeafNode)localRoot).guide)>=0)) {
				((LeafNode)localRoot).value += inc;
			}
		}
		
		else {
			
			//if the end range is less than the largest value in the left subtree
			if (last.compareTo(((InternalNode)localRoot).child0.guide) <= 0) {
				addRange(first, last, ((InternalNode)localRoot).child0, h-1, inc);
			}
			
			//if the end range is less than the greatest value stored in the middle/right tree
			else if ((((InternalNode)localRoot).child2 == null) || (last.compareTo(((InternalNode)localRoot).child1.guide) <= 0)) {
				
				//if beginning range is less than the largest value in the left subtree
				if (first.compareTo(((InternalNode)localRoot).child0.guide) <= 0) {
					//sum nodes greater than first
					addGE(((InternalNode)localRoot).child0, first, h-1, inc);
					//sum nodes less than last
					addLE(((InternalNode)localRoot).child1, last, h-1, inc);
				}
				
				else {
					//enact more recursive calls
					addRange(first, last, ((InternalNode)localRoot).child1, h-1, inc);
				}
			}
			
			else {
				
				//if the beginning range is less than the largest value in the left subtree
				if (first.compareTo(((InternalNode)localRoot).child0.guide) <= 0) {
					addGE(((InternalNode)localRoot).child0, first, h-1, inc);
					addAll(((InternalNode)localRoot).child1, h-1, inc);
					addLE(((InternalNode)localRoot).child2, last, h-1, inc);
				}
				
				//else if the beginning range is less than the largest value in the middle/right tree 
				else if (first.compareTo(((InternalNode)localRoot).child1.guide) <= 0) {
					addGE(((InternalNode)localRoot).child1, first, h-1, inc);
					addLE(((InternalNode)localRoot).child2, last, h-1, inc);
				}
				
				else {
					//needs more recursion!
					addRange(first, last, ((InternalNode)localRoot).child2, h-1, inc);
				}
					
				
			}
			
		}
	}
	
	//NEED TO FIX!! LOOK AT PRINTS. SOMETIMES, FEE IS NOT UPDATED. SECOND ELSE POTENTIALLY
	static int getFee(Node localRoot, String planet, int fee, int h, BufferedWriter o) throws IOException{
		
		if (h==0) {
			//o.write(planet + "|" + ((LeafNode)localRoot).guide + "\n");
			if (planet.equals(((LeafNode)localRoot).guide)) {
				//o.write(fee + "," + ((LeafNode)localRoot).value + "\n");
				fee += ((LeafNode)localRoot).value;
				return fee;
			} 
			else {
				return -1;
			}
		}
		
		else {
			int temp;
			if (planet.compareTo(((InternalNode)localRoot).child0.guide) <= 0) {
				temp = fee+((InternalNode)localRoot).value;
				//o.write(planet + ":" + ((InternalNode)localRoot).child0.guide + ":" + temp +"\n");
				return getFee(((InternalNode)localRoot).child0, planet, fee+((InternalNode)localRoot).value, h-1, o);
			}
			else if ((((InternalNode)localRoot).child2 == null) || planet.compareTo(((InternalNode)localRoot).child1.guide) <= 0) {
				temp = fee+((InternalNode)localRoot).value;
				//o.write(planet + "!" + ((InternalNode)localRoot).child1.guide + "!" + temp + "\n");
				return getFee(((InternalNode)localRoot).child1, planet, fee+((InternalNode)localRoot).value, h-1, o);
			}
			else {
				temp = fee+((InternalNode)localRoot).value;
				//o.write(planet + ";" + ((InternalNode)localRoot).child2.guide + ";" + temp +"\n");  //why is child2 guide == child1 guide?
				return getFee(((InternalNode)localRoot).child2, planet, fee+((InternalNode)localRoot).value, h-1, o);
			}
		}
	}

	static void insert(String key, int value, Tree tree) {
	   // insert a key value pair into tree (overwrite existing value
	   // if key is already present)

		int h = tree.height;

	    if (h == -1) {
	        LeafNode newLeaf = new LeafNode();
	        newLeaf.guide = key;
	        newLeaf.value = value;
	        tree.root = newLeaf; 
	        tree.height = 0;
	    }
	    
	    else {
	        WorkSpace ws = doInsert(key, value, tree.root, h);

	        if (ws != null && ws.newNode != null) {
	         // create a new root

	        	InternalNode newRoot = new InternalNode();
	        	//newRoot.value = value;
	            if (ws.offset == 0) {
	               newRoot.child0 = ws.newNode; 
	               newRoot.child1 = tree.root;
	            }
	            else {
	               newRoot.child0 = tree.root; 
	               newRoot.child1 = ws.newNode;
	            }
	            resetGuide(newRoot);
	            tree.root = newRoot;
	            tree.height = h+1;
	         }
	      }
	}

	static WorkSpace doInsert(String key, int value, Node p, int h) {
	   // auxiliary recursive routine for insert

		if (h == 0) {
	         // we're at the leaf level, so compare and 
	         // either update value or insert new leaf

			LeafNode leaf = (LeafNode) p; //downcast
	        int cmp = key.compareTo(leaf.guide);

	        if (cmp == 0) {
	           leaf.value = value; 
	           return null;
	        }

	         // create new leaf node and insert into tree
	         LeafNode newLeaf = new LeafNode();
	         newLeaf.guide = key; 
	         newLeaf.value = value;

	         int offset = (cmp < 0) ? 0 : 1;
	         // offset == 0 => newLeaf inserted as left sibling
	         // offset == 1 => newLeaf inserted as right sibling

	         WorkSpace ws = new WorkSpace();
	         ws.newNode = newLeaf;
	         ws.offset = offset;
	         ws.scratch = new Node[4];

	         return ws;
		}
	    else {
	         InternalNode q = (InternalNode) p; // downcast
	         int pos;
	         WorkSpace ws;
	         
	         //implement lazy updates!
	         q.child0.value += p.value;
	         q.child1.value += p.value;
	         
	         if (q.child2 != null) {
	        	 q.child2.value += p.value;
	         }
	         q.value = 0;
	         
	         if (key.compareTo(q.child0.guide) <= 0) {
	            pos = 0; 
	            ws = doInsert(key, value, q.child0, h-1);
	         }
	         else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
	            pos = 1;
	            ws = doInsert(key, value, q.child1, h-1);
	         }
	         else {
	            pos = 2; 
	            ws = doInsert(key, value, q.child2, h-1);
	         }

	         if (ws != null) {
	            if (ws.newNode != null) {
	               // make ws.newNode child # pos + ws.offset of q

	               int sz = copyOutChildren(q, ws.scratch);
	               insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
	               if (sz == 2) {
	                  ws.newNode = null;
	                  ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
	               }
	               else {
	                  ws.newNode = new InternalNode();
	                  ws.offset = 1;
	                  resetChildren(q, ws.scratch, 0, 2);
	                  resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
	               }
	            }
	            else if (ws.guideChanged) {
	               ws.guideChanged = resetGuide(q);
	            }
	         }

	         return ws;
	      }
	}


	static int copyOutChildren(InternalNode q, Node[] x) {
	   // copy children of q into x, and return # of children

		int sz = 2;
	    x[0] = q.child0; x[1] = q.child1;
	    if (q.child2 != null) {
	       x[2] = q.child2; 
	       sz = 3;
	    }
	    return sz;
	 }

	static void insertNode(Node[] x, Node p, int sz, int pos) {
	   // insert p in x[0..sz) at position pos,
	   // moving existing extries to the right

		for (int i = sz; i > pos; i--)
			x[i] = x[i-1];

	    x[pos] = p;
	}

	static boolean resetGuide(InternalNode q) {
	   // reset q.guide, and return true if it changes.

	    String oldGuide = q.guide;
	    	if (q.child2 != null)
	    		q.guide = q.child2.guide;
	        else
	        	q.guide = q.child1.guide;

	    return q.guide != oldGuide;
	}


	static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
	   // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
	   // also resets guide, and returns the result of that

		q.child0 = x[pos]; 
	    q.child1 = x[pos+1];

	    if (sz == 3) 
	    	q.child2 = x[pos+2];
	    else
	        q.child2 = null;

	    return resetGuide(q);
	}
	
}

class Node {
	String guide;
	int value = 0; //we are doing lazy updates, so even internal nodes have a value.
	// guide points to max key in subtree rooted at node
}

class InternalNode extends Node {
	Node child0, child1, child2;
	// child0 and child1 are always non-null
	// child2 is null iff node has only 2 children
}

class LeafNode extends Node {
	// guide points to the key
	//int value;
}

class Tree {
	Node root;
	int height;

	Tree() {
		this.root = null;
	    this.height = -1;
	}
}

class WorkSpace {
	// this class is used to hold return values for the recursive doInsert
	// routine (see below)
	Node newNode;
	int offset;
	boolean guideChanged;
	Node[] scratch;
}







