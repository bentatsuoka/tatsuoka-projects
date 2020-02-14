
public class Queue {
	public final int T = 300;
	public Customer first;
	public Customer last;
	public final int nineAM = 32400;
	public final int fivePM = 61200;
	public Queue() {}
	
	public void setAllTimes() {
		int startTime;
		int leaveTime;
		int waitTime;
		Customer temp = first;
		Customer previous = temp.previous;
		do {
			//first customer
			if (previous == null) {
				if (temp.arrTime < nineAM) {
					startTime = nineAM;
					temp.startTime = startTime;
				}
				else {
					startTime = temp.arrTime;
					temp.startTime = startTime;
				}
		
				//wait times and leave times
				waitTime = startTime - temp.arrTime;
				temp.waitTime = waitTime;
				leaveTime = startTime + T;
				temp.leaveTime = leaveTime;
				
			}
			//Customer not at beginning of queue
			else {
				//set start times
				if (temp.arrTime < previous.leaveTime) {
					startTime = previous.leaveTime;
					temp.startTime = startTime;
				}
				else {
					startTime = temp.arrTime;
					temp.startTime = startTime;
				}
				//wait times and leave times
				waitTime = startTime - temp.arrTime;
				temp.waitTime = waitTime;
				leaveTime = startTime + T;
				temp.leaveTime = leaveTime;

			}
			
			previous = temp;
			temp = temp.next;
		}while (temp != null);
	}
	
	public int longestBreakLength() {
		Customer temp = first;
		int longest = 0;
		if (temp.arrTime < nineAM) {
			longest = nineAM - temp.arrTime;
		}
		while (temp != null) {
			int breakt = 0;
			if (temp.waitTime == 0) {//checks if there is no line
				breakt = temp.arrTime - temp.previous.leaveTime;
			}
			if (breakt > longest) {
				longest = breakt;
			}
			temp = temp.next;
		}
		return longest;
	}
	
	//logic error, wrong output
	public int totalIdleTime() {
		int total = 0;
		Customer temp = first;
		if (temp.arrTime > nineAM) {
			total += temp.arrTime - nineAM;
		}
		while (temp != null) {
			int breakt = 0;
			if (temp.waitTime == 0) {
				breakt = temp.arrTime - temp.previous.leaveTime;
				total += breakt;
			}
			temp = temp.next;
		}
		return total;
	}
	
	public int maxPeopleInQueue() {
		int count = 0;
		int max = 0;
		Customer temp = first.next;
		Customer previous = first; 
		while (temp != null) {
			if (temp.arrTime < previous.leaveTime) {
				count++;
			}
			else {
				count = 0;
			}
			if (count > max) {
				max = count;
			}
			previous = temp;
			temp = temp.next;
		}
		return max;
	}
	
	public Customer matchKey(int key) {
		Customer temp = first;
		while (temp != null) {
			if (key == temp.id) 
				return temp;
			else {
				temp = temp.next;
			}
		}
		return null;
	}
	
	public int waitTimeOf(int key) {
		Customer c = matchKey(key);
		int time = c.arrTime;
		int waitTime = 0;
		if (time < nineAM) {
			if (c.id == 1) {
				waitTime = nineAM - time;
			}
			else {
				int count = 0;
				Customer temp = first;
				while (temp.arrTime < nineAM) {
					count++;
					temp = temp.next;
				}
				count = count - 1; //don't want to count node you're on
				waitTime = nineAM - time + (T * count);
			}
		}
		else {
			waitTime = c.waitTime;
		}
		
		return waitTime;
	}
	
	public void addLast(Customer c) {
		if (first == null) {
			first = c;
			last = c;
		}
		last.next = c;
		Customer temp = last;
		last = c;
		last.previous = temp;
	}
	
	public int numOfCustomersServed() {
		int count = 0;
		Customer temp = first;
		while (temp != null) {
			if (temp.startTime < fivePM) {
				count++;
			}
			temp = temp.next;
		}
		return count;
	}

}


