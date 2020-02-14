import java.util.*;
import java.io.*;

public class Course implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getCourseID() {
		return courseID;
	}
	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
	public String getMaxStudents() {
		return maxStudents;
	}
	public void setMaxStudents(String maxStudents) {
		this.maxStudents = maxStudents;
	}
	public String getCurrentStudents() {
		return currentStudents;
	}
	public void setCurrentStudents(String currentStudents) {
		this.currentStudents = currentStudents;
	}
	public String getStudentList() {
		return studentList;
	}

	public String getInstructor() {
		return instructor;
	}
	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}
	public String getSecNum() {
		return secNum;
	}
	public void setSecNum(String secNum) {
		this.secNum = secNum;
	}
	public String getLocation() {
		return location;
	}
	public ArrayList<Student> getStudents() {
		return this.students;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	private String courseName;
	private String courseID;
	private String maxStudents;
	private String currentStudents;
	private String studentList;
	
	
	//ArrayList for students within a course object
	public ArrayList<Student> students = new ArrayList<>();
	private String instructor;
	private String secNum;
	private String location;

	public Course() {
	}
	
	public Course(String courseName, String courseID, String maxStudents, String currentStudents, String studentList, String instructor, String secNum, String location) {
		
		this.courseName = courseName;
		this.courseID = courseID;
		this.maxStudents = maxStudents;
		this.currentStudents = currentStudents;
		this.studentList = studentList;
		this.instructor = instructor;
		this.secNum = secNum;
		this.location = location;
		
	}
	
	public Course(String courseName, String courseID, String maxStudents, int currentStudents, ArrayList<Student> students, String instructor, String secNum, String location) {
		
		this.courseName = courseName;
		this.courseID = courseID;
		this.maxStudents = maxStudents;
		this.currentStudents = Integer.toString(currentStudents);
		this.students = students;
		this.instructor = instructor;
		this.secNum = secNum;
		this.location = location;
		
	}
	public Course(String courseID) {
		this.courseID = courseID;
	}

	@Override
	public String toString() {
		return courseName + ", " + courseID + ", " + currentStudents + ", " + instructor + ", " + secNum + ", " + location;
	}

}
