import java.util.*;
import java.io.*;

abstract class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String username;
	String password;
	String firstName;
	String lastName;
	
	public User() {}
	
	public static void printCourses(ArrayList<Course> courses) {
		for (int j = 0; j < courses.size(); j++) {
			System.out.println(courses.get(j).toString() + "\n");
		}
	}
	
	public User(String username, String password) {
		
		this.username = username;
		
		this.password = password;
		
	}
	
	public User(String firstName, String lastName, String username, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
	}
	
	protected static void exit() {
		
		System.exit(0);
		
	}
	
}
