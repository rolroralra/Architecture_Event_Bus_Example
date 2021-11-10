package components.course;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class CourseComponent {
    protected ArrayList<Course> vCourse;

    public CourseComponent(String sCourseFileName) throws IOException {
        BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream(sCourseFileName))));
//        BufferedReader bufferedReader  = new BufferedReader(new FileReader(sCourseFileName));
        this.vCourse  = new ArrayList<>();
        while (bufferedReader.ready()) {
            String courseInfo = bufferedReader.readLine();
            if(!courseInfo.equals("")) this.vCourse.add(new Course(courseInfo));
        }    
        bufferedReader.close();
    }
    public ArrayList<Course> getCourseList() {
        return this.vCourse;
    }
    public boolean isRegisteredCourse(String courseId) {
        for (Course course : this.vCourse) {
            if (course.match(courseId)) return true;
        }
        return false;
    }
}
