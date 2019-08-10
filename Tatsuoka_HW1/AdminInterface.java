import java.util.*;
public interface AdminInterface {
	
	public boolean login(String adUser, String adPass);
	
	public void newCourse(Course c, ArrayList<Course> cList);
	
	public boolean deleteCourse(String s, String instructor, ArrayList<Course> cList);
	
	public boolean newStudent(Student st, ArrayList<Student> sList);
	
	public boolean newStudent(String firstn, String lastn, ArrayList<Student> sList);
	
	public String viewStudent(String username, ArrayList<Course> cList);
	
	public void exportFullClasses(ArrayList<Course> cList);
	
	public String printFullClasses(ArrayList<Course> cList);
	
	public boolean editInstructor(String s, String oldInstructor, String newInstructor, ArrayList<Course> cList);
	
	public String viewCourseStudents(String id, String instructor, ArrayList<Course> cList);
	
	public ArrayList<Course> sortCourses(ArrayList<Course> cList);
	
}