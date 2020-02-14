import java.util.*;
import java.io.*;

public class Admin extends User implements AdminInterface {
	
	private static final long serialVersionUID = 1L;
	String firstName;
	String lastName;
	String user;
	String pass;
	
	public Admin() {}
	
	public Admin(String username, String password) {
		
		username = "Admin";
		
		password = "Admin001";
		
	}
	@Override
	public boolean login(String username, String password) {
		
		if (username.equals("Admin") && password.equals("Admin001"))
			
			return true;
		
		else
			
			return false;
		
	}
	public void newCourse(Course c, ArrayList<Course> cList) {
		boolean added = cList.add(c);
		if (added) {
			System.out.printf("\n%s \nID: %s \nInstructor: %s \nLocation: %s \nWas successfully added to the course list!\n\n", c.getCourseName(), c.getCourseID(), c.getInstructor(), c.getLocation());
		}
		else
			System.out.println("I'm sorry, there was an error.");	
	}
	
	public boolean deleteCourse(String s, String instructor, ArrayList<Course> cList) {
		for (int i = 0; i < cList.size(); i++) {
			if (s.equals(cList.get(i).getCourseID()) && instructor.equals(cList.get(i).getInstructor())) {
				cList.remove(i);
				return true;
			}
		}
		return false;
	}
	public boolean newStudent(Student st, ArrayList<Student> sList) {
		return sList.add(st);
	}
	
	public boolean newStudent(String firstn, String lastn, ArrayList<Student> sList) {
		String username = usernameGen(lastn, sList);
		String password = passwordGen(sList);
		Student st = new Student(firstn, lastn, username, password);
		return sList.add(st);
		
	}
	public static String usernameGen(String lastn, ArrayList<Student> sList) {
		String userID = "";
		int num = sList.size();
		userID = lastn + Integer.toString(num);
		return userID;
	}
	
	public static String passwordGen(ArrayList<Student> sList) {
		String pass = "";
		int num = sList.size();
		pass = "Bobcat" + Integer.toString(num);
		return pass;
	}

	public String viewStudent(String username, ArrayList<Course> cList) {
		for (int i = 0; i < cList.size(); i++) {
			for (int j = 0; j < cList.get(i).students.size(); j++) {
				if (username.equals(cList.get(i).students.get(j).getUsername())) {
					return cList.get(i).toString() + "\n";
				}
			}
		}
		return username + " is not currently registered in any courses.";
			
	}
	
	public void exportFullClasses(ArrayList<Course> cList) {
		try {
			PrintWriter w = new PrintWriter("fullcourses.txt", "UTF-8");
			for (int i = 0; i < cList.size(); i++) {
				int intMax = Integer.parseInt(cList.get(i).getMaxStudents());
				int intCurrent = Integer.parseInt(cList.get(i).getCurrentStudents());
				if (intCurrent == intMax) {
					w.println(cList.get(i).getCourseName());
				}
				else
					continue;
			}w.close();
		}
		
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean editInstructor(String s, String oldInstructor, String newInstructor, ArrayList<Course> cList) {
		for (int i = 0; i < cList.size(); i++) {
			if (s.equals(cList.get(i).getCourseID()) && oldInstructor.equals(cList.get(i).getInstructor())) {
				cList.get(i).setInstructor(newInstructor);
				return true;
			}
		}return false;
		
	}

	public String printFullClasses(ArrayList<Course> cList) {
		for (int i = 0; i < cList.size(); i++) {
			int intMax = Integer.parseInt(cList.get(i).getMaxStudents());
			int intCurrent = Integer.parseInt(cList.get(i).getCurrentStudents());
			if (intCurrent == intMax) {
				return cList.get(i).toString();
			}
		}return "No full classes.";
	}
	
	public String viewCourseStudents(String id, String instructor, ArrayList<Course> cList) {
		for (int i = 0; i < cList.size(); i++) {
			if (id.equals(cList.get(i).getCourseID()) && instructor.equals(cList.get(i).getInstructor())) {
				return cList.get(i).students.toString() + "\n";
			}
		}return "Course id not found.";
	}
	
	public ArrayList<Course> sortCourses(ArrayList<Course> cList) {
		ArrayList<Course> sorted = cList;
		int len = cList.size();
		for (int i = 0; i < len-1; i++) {
            for (int j = 0; j < len-i-1; j++) {
     
            	if (Integer.parseInt(sorted.get(j).getCurrentStudents()) < Integer.parseInt(sorted.get(j+1).getCurrentStudents())) 
                { 
            		Course temp = sorted.get(j);
                    sorted.set(j, sorted.get(j+1));
                    sorted.set(j + 1, temp);
                } 
			}
		}return sorted;
	}
	
}
