import java.util.*;

public class Student extends User implements StudentInterface {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Student() {
		
	}
	
	public Student(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public Student(String firstName, String lastName, String user, String pass) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = user;
		this.password = pass;
		
	}
	
	public static Student login(String username, String pass, ArrayList<Student> sList) {
		for (int i = 0; i < sList.size(); i++) {
			if (username.equals(sList.get(i).getUsername()) && pass.equals(sList.get(i).getPassword())){
				return sList.get(i);
			}
		}return null;
	}

	public boolean registerCourse(Student s, String id, String instructor, ArrayList<Course> cList) {
		for (int i = 0; i < cList.size(); i++) {
			if (id.equals(cList.get(i).getCourseID()) && instructor.equals(cList.get(i).getInstructor())) {
				cList.get(i).students.add(s);
				int current = Integer.parseInt(cList.get(i).getCurrentStudents());
				current += 1;
				String cur = Integer.toString(current);
				cList.get(i).setCurrentStudents(cur);
				return true;
			}
		}return false;
	}
	
	public String viewRegisteredCourses(String username, ArrayList<Course> cList) {
		
		for (int i = 0; i < cList.size(); i++) {
			for (int j = 0; j < cList.get(i).students.size(); j++) {
				if (username.equals(cList.get(i).students.get(j).username)) {
					return cList.get(i).toString() + "\n";
				}
			}
		}return "You are currently not registered in any courses.";
	}
	
	public boolean withdraw(String username, String id, String instructor, ArrayList<Course> cList) {
		for (int i = 0; i < cList.size(); i++) {
			if (id.equals(cList.get(i).getCourseID()) && instructor.equals(cList.get(i).getInstructor())) {
				for (int j = 0; j < cList.get(i).students.size(); j++) {
					if (username.equals(cList.get(i).students.get(j).username)) {
						cList.get(i).students.remove(j);
						return true;
					}
				}
			}
		}return false;
	
	}
	public void printEmpty(ArrayList<Course> cList) {
		for (int i = 0; i < cList.size(); i++) {
			int intMax = Integer.parseInt(cList.get(i).getMaxStudents());
			int intCurrent = Integer.parseInt(cList.get(i).getCurrentStudents());
			if (intCurrent < intMax)
				System.out.println(cList.get(i).getCourseName() + " ID: " + cList.get(i).getCourseID());
		}
		
	}
	@Override
	public String toString() {
		return firstName + ", " + lastName + ", " + username + ", " + password;
	}
}

