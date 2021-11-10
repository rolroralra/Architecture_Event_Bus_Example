package components.student;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class StudentComponent {
	protected ArrayList<Student> vStudent;
	
	public StudentComponent(String sStudentFileName) throws IOException {
		BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream(sStudentFileName))));
//		BufferedReader bufferedReader = new BufferedReader(new FileReader(sStudentFileName));
		this.vStudent = new ArrayList<>();
		while (bufferedReader.ready()) {
			String stuInfo = bufferedReader.readLine();
			if (!stuInfo.equals("")) this.vStudent.add(new Student(stuInfo));
		}
		bufferedReader.close();
	}
	public ArrayList<Student> getStudentList() {
		return vStudent;
	}
	public void setvStudent(ArrayList<Student> vStudent) {
		this.vStudent = vStudent;
	}
	public boolean isRegisteredStudent(String sSID) {
		for (Student student : this.vStudent) {
			if (( student).match(sSID)) return true;
		}
		return false;
	}
}
