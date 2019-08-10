import java.util.*;
import java.io.*;
import java.util.Scanner;

public class HW1Main implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		ArrayList <Course> cList = new ArrayList<>();
		
		ArrayList<Student> sList = new ArrayList<>();
		
		//Deserialize
		try {
			FileInputStream sfis = new FileInputStream("Students.ser");
			FileInputStream cfis = new FileInputStream("Courses.ser");
			ObjectInputStream sois = new ObjectInputStream(sfis);
			ObjectInputStream cois = new ObjectInputStream(cfis);
			sList = (ArrayList<Student>)sois.readObject();
			cList = (ArrayList<Course>)cois.readObject();
			sfis.close();
			cfis.close();
			sois.close();
			cois.close();
		}
		
		catch(IOException ioe) {
			popCourses(cList);
		}
		
		catch(ClassNotFoundException cnfe) {
		      cnfe.printStackTrace();
		}
		
		Scanner sc = new Scanner(System.in);
		
		String response;
		
		do {
		
			System.out.println("Welcome to The Course Registration System!\n If you are an administrator, enter a. If you are a student, enter s. To quit, enter q: ");
			
			response = sc.next();
	
			switch (response) {
			
				case "a": {
						System.out.println("\nPlease enter your username: ");
					
						String user = sc.next();
					
						System.out.println("\nPlease enter your password: ");
						
						String pass = sc.next();
						
						Admin ad = new Admin(user, pass);
						
						if (ad.login(user, pass)) {
							
							int option;
							
							do {
								
								System.out.println("\nPlease select an option number: \n");
									
								printAdminOps();
							
								option = sc.nextInt();
								
								switch (option) {
									case 1: User.printCourses(cList); 
										break;
										
									case 2: newClassProcess(sc, ad, cList);	
										break;
										
									case 3: deleteCourseProcess(sc, ad, cList);
										break;
										
									case 4: viewCourseStudentsProcess(sc, ad, cList);
										break;
									
									case 5: newStudentProcess(sc, ad, sList);
										break;
									
									case 6: editInstructorProcess(sc, ad, cList);
										break;
									
									case 7: exportFullProcess(sc, ad, cList);
										break;
									
									case 8: viewStudentProcess(sc, ad, cList);
										break;
											
									case 9: User.printCourses(ad.sortCourses(cList));
										break;
										
									case 10: System.out.println("Goodbye!");
										break;
									
									default: System.out.println("Invalid input.");
											 User.exit();
										break;
								
								}
								
							}while (option != 10);
							
						}
						
						else {
							System.out.println("Incorrect username or password.");
							Admin.exit();
						}
						
					break;	
				}
					
				
				
				case "s": {
						System.out.println("Welcome student! Please enter your login information.");

						Student st = studentLoginProcess(sc, sList);
						
						if (st != null) {
					
							int option;
							do {
							
								System.out.println("Please select an option number: \n");
								
								printStudentOps();
					
								option = sc.nextInt();
								
								switch (option) {
								
									case 1: User.printCourses(cList);
										break;
									
									case 2: st.printEmpty(cList);
										break;
									
									case 3: viewRegisteredCoursesProcess(sc, st, cList);
										break;
											
									case 4: registerCourseProcess(sc, st, cList);
										break;
										
									case 5: withdrawProcess(sc, st, cList);
										break;
										
									case 6: System.out.println("Goodbye!");
										break;
										
									default: System.out.println("Invalid input.");
											 User.exit();
										break;
								}
							
							} while (option != 6);
						}
						else {
							System.out.println("Incorrect username/password.");
							Student.exit();
						}
							
					break;
				}
				
				case "q": {
					System.out.println("Exiting...\n");
					break;
				}
					
				default: System.out.println("Sorry, the command " + sc.next() + " could not be recognized.");
					break;
			}
		
		}while (!response.equals("q"));
		
		sc.close();
		
		//Serialize
		try {
			FileOutputStream fos = new FileOutputStream("Courses.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(cList);
			FileOutputStream cor = new FileOutputStream("Students.ser");
			ObjectOutputStream coo = new ObjectOutputStream(cor);
			coo.writeObject(sList);
			oos.close();
			fos.close();
			cor.close();
			coo.close();
		} 
	
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	
	}
	
	//Process methods 
	private static void popCourses(ArrayList<Course> cList) {
		try {
			File file = new File("Contacts.csv");
			Scanner sc = new Scanner(file);
			sc.useDelimiter(",");
			sc.nextLine();
			while (sc.hasNext()) {	
				String[] array = sc.nextLine().split(",");
				Course c = new Course(array[0], array[1], array[2], array[3], array[4], array[5], array[6], array[7]);
				cList.add(c);
			}
			sc.close();
		}
		catch (FileNotFoundException error) {
			System.out.println("File not found.");
		}
	}
	
	private static void printAdminOps() {
		System.out.println("1: Print full course list\n2: Add a new course\n3: Delete a course\n"
				+ "4: View students in a specific class\n5: New student registration\n"
				+ "6: Edit Course Instructor\n7: View/Export all full courses\n8: View a student's registered courses\n"
				+ "9: Sort by Current Students\n10: Exit\n");
	}
	
	private static void printStudentOps() {
		System.out.println("1: Print full course list\n2: View open courses\n3: View your registered courses\n"
				+ "4: Register for a course\n5: Withdraw from a course\n"
				+ "6: Exit\n");
	}
	
	private static Student studentLoginProcess(Scanner sc, ArrayList<Student> sList) {
		sc.useDelimiter("\n");
		System.out.println("Please enter your username: ");
		String usern = sc.next();
		System.out.println("Please enter your password: ");
		String passw = sc.next();
		Student s = Student.login(usern, passw, sList);
		if (!(s == null)) {
			System.out.println("\nLogin Successful! " + usern + " was found in the system.\n");	
			return s;
		}
		return null;
	}
	
	private static void newClassProcess(Scanner scan, Admin ad, ArrayList<Course> cList) {
		scan.useDelimiter("\n");
		System.out.println("Please enter the course name of the course you would like to add: ");
		String name = scan.next();
		System.out.println("Next, enter the corresponding course ID: ");
		String id = scan.next();
		System.out.println("Thanks! Next, please enter the max number of students in the class: ");
		String maxNum = scan.next();
		System.out.println("How many existing students are there in the class? ");
		String stuNum = scan.next();
		System.out.println("Next, please enter the instructor's name: ");
		String instructor = scan.next();
		System.out.println("Please enter the section number of the course: ");
		String courseNum = scan.next();
		System.out.println("Thanks! Lastly, please enter the location of the class: ");
		String loc = scan.next();
		Course c = new Course(name, id, maxNum, stuNum, "NULL", instructor, courseNum, loc);
		ad.newCourse(c, cList);
	}

	private static void newStudentProcess(Scanner sc, Admin a, ArrayList<Student> sList) {
		sc.useDelimiter("\n");
		System.out.println("Please enter the student's first name: ");
		String firstn = sc.next();
		System.out.println("Please enter the student's last name: ");
		String lastn = sc.next();
		String username = Admin.usernameGen(lastn, sList);
		String password = Admin.passwordGen(sList);
		Student s = new Student(firstn, lastn, username, password);
		boolean added = a.newStudent(s, sList);
		if (added) {
			System.out.println(s.getFirstName() + " " + s.getLastName() + " was successfully added to the Course Registration System! Username and password: " + s.getUsername() + " and " + s.getPassword());
		}
		else
			System.out.println("There was an error adding the student to the student list.");
	}
	
	private static void viewStudentProcess(Scanner sc, Admin a, ArrayList<Course> cList) {
		sc.useDelimiter("\n");
		System.out.println("Please enter the username of the student you would like to view courses for: ");
		String username = sc.next();
		String studentCourses = a.viewStudent(username, cList);
		System.out.println(studentCourses);
	}
	
	private static void exportFullProcess(Scanner sc, Admin ad, ArrayList<Course> cList) {
		ad.exportFullClasses(cList);
		System.out.println(ad.printFullClasses(cList));
	}
	
	private static void deleteCourseProcess(Scanner s, Admin ad, ArrayList<Course> cList) {
		s.useDelimiter("\n");
		System.out.println("Please enter the Course ID of the course you would like to delete e.g. CSCI-GA.1144: ");
		String id = s.next();
		System.out.println("Please enter the instructor of " + id + ": ");
		String instructor = s.next();
		boolean deleted = ad.deleteCourse(id, instructor, cList);
		if (deleted) {
			System.out.println("\n" + id + " was successfully deleted from the course list.\n");
		}
		else
			System.out.println(id + " was not found in the course list.");
	}
	
	private static void viewCourseStudentsProcess(Scanner s, Admin ad, ArrayList<Course> cList) {
		s.useDelimiter("\n");
		System.out.println("Please enter the Course ID of the course you would like to view students for: ");
		String cID = s.next();
		System.out.println("Please enter the instructor of " + cID + ": ");
		String instructor = s.next();
		System.out.println(ad.viewCourseStudents(cID, instructor, cList));
	}
	
	private static void withdrawProcess(Scanner sc, Student st, ArrayList<Course> cList) {
		sc.useDelimiter("\n");
		System.out.println("Please enter the Course ID of the class you would like to withdraw from: ");
		String id = sc.next();
		System.out.println("Who is the instructor of " + id + "? ");
		String instructor = sc.next();
		System.out.println("Thanks! Now please enter your username: ");
		String username = sc.next();
		boolean withdrawn = st.withdraw(username, id, instructor, cList);
		if (withdrawn)
			System.out.println("You were successfully withdrawn from Course " + id + ".");
		else
			System.out.println("Error! The Course ID/Username you entered was incorrect: ");
	}
	
	private static void editInstructorProcess(Scanner s, Admin ad, ArrayList<Course> cList) {
		s.useDelimiter("\n");
		System.out.println("Please enter the Course ID of the class you would like to edit: ");
		String id = s.next();
		System.out.println("Please enter the instructor of " + id + ": ");
		String oldInstructor = s.next();
		System.out.println("\nPlease enter the new instructor of " + id + ": ");
		String newInstructor = s.next();
		boolean edited = ad.editInstructor(id, oldInstructor, newInstructor, cList);
		if (edited) {
			System.out.println("Success! " + id + " is now being taught by " + newInstructor + ".");
		}
		else
			System.out.println(id + " was not found in the course list.");
		System.out.println("\n");
	}
	
	private static void registerCourseProcess(Scanner sc, Student s, ArrayList<Course> cList) {
		sc.useDelimiter("\n");
		System.out.println("Please enter the Course ID of the class you would like to register for: ");
		String id = sc.next();
		System.out.println("Please enter the instructor of " + id + ": ");
		String instructor = sc.next();
		boolean registered = s.registerCourse(s, id, instructor, cList);
		if (registered)
			System.out.println("Success! You were registered in " + id + ".");
		else
			System.out.println("Error! Course ID " + id + " was not found.");
	}

	private static void viewRegisteredCoursesProcess(Scanner sc, Student s, ArrayList<Course> cList) {
		System.out.println("Please enter your username: ");
		String username = sc.next();
		String registered = s.viewRegisteredCourses(username, cList);
		System.out.print("\n" + registered + "\n");
	}
	
}

