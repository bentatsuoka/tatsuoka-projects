
public class Customer {
	public int id;
	public int arrTime;
	public int startTime;
	public int leaveTime;
	public int waitTime;
	public Customer next;
	public Customer previous;
	public final int T = 300;
	
	public Customer(int id, int arrTime) {
		this.id = id;
		this.arrTime = arrTime;
	}
	
	public Customer() {}

}
