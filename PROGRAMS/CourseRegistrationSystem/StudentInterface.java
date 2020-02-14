import java.util.ArrayList;

public interface StudentInterface {
	
	public boolean registerCourse(Student s, String id, String instructor, ArrayList<Course> cList);
	
	public String viewRegisteredCourses(String username, ArrayList<Course> cList);
	
	public boolean withdraw(String username, String id, String instructor, ArrayList<Course> cList);
	
	public void printEmpty(ArrayList<Course> cList);
	
	@Override
	public String toString();

}
