/*
 Ben Tatsuoka
 HW3
 */

import java.util.*;
import java.io.*;
public class Queue_Main {

	public static void main(String[] args) throws IOException {
		Queue q = new Queue();
		//Read customers and queries .txt files 
		String customers = args[0];
		String queries = args[1];
		File f1 = new File(customers);
		File f2 = new File(queries);
		Scanner sc = new Scanner(f1);
		Scanner sc2 = new Scanner(f2);
		sc.nextLine();
		while (sc.hasNextLine()) { 
			Customer c = new Customer();
			sc.nextLine();
			String s1 = sc.nextLine();
			String s2 = sc.nextLine();
			String[] arr1 = s1.split(":  ");//Array of customer ID's
			int id = Integer.parseInt(arr1[1]); 
			c.id = id;
			String[] arr2 = s2.split(" ");//Array of customer arrival times
			String[] times = arr2[1].split(":");
			int h = Integer.parseInt(times[0]);
			int m = Integer.parseInt(times[1]);
			int s = Integer.parseInt(times[2]);
			if (h >= 1 && h <= 5) {
				h += 12;
			}
			int aTime;
			aTime = h*3600 + m*60 + s;
			c.arrTime = aTime;
			q.addLast(c);
			}
		sc.close();
		
		q.setAllTimes();
		
		//Switch statement by first letter of query to simplify matching
		while (sc2.hasNextLine()) {
			String q1 = sc2.nextLine();
			char firstL = q1.charAt(0);
			switch (firstL) {
				case 'N': {
					int num = q.numOfCustomersServed();
					System.out.println("NUMBER-OF-CUSTOMERS-SERVED: " + num);
					break;
				}
					
				case 'L': {
					int num = q.longestBreakLength();
					System.out.println("LONGEST-BREAK-LENGTH: " + num);
					break;
				}
					
				case 'T': {
					int num = q.totalIdleTime();
					System.out.println("TOTAL-IDLE-TIME: " + num);
					break;
				}
					
				case 'M': {
					int num = q.maxPeopleInQueue();
					System.out.println("MAXIMUM-NUMBER-OF-PEOPLE-IN-QUEUE-AT-ANY-TIME: " + num);
					break;
				}
					
				case 'W': {
					int id = Integer.parseInt(q1.substring(16));
					int num = q.waitTimeOf(id);
					System.out.println("WAITING-TIME-OF " + id + ": " + num);
					break;
				}
				
				default: System.out.println("Invalid query.");
					break;
		
			}
		}
		sc2.close();
	}


}
