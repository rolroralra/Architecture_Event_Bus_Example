package components.course;

import framework.Event;
import framework.EventId;
import framework.EventQueue;
import framework.RMIEventBus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;

public class CourseMain {
	public static void main(String[] args) throws IOException, NotBoundException {
		RMIEventBus eventBus = (RMIEventBus) LocateRegistry.getRegistry(8080).lookup("EventBus");
//		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("CourseMain (ID:" + componentId + ") is successfully registered...");

		RMIEventBus.addShutDownHook(eventBus, componentId);

		CourseComponent coursesList = new CourseComponent("Courses.txt");
		boolean done = false;
		while (!done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			EventQueue eventQueue = eventBus.receiveEventQueue(componentId);
			for (int i = 0; i < eventQueue.getSize(); i++) {
				Event event = eventQueue.pollEvent();
				switch (event.getEventId()) {
				case ListCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeCourseList(coursesList)));
					break;
				case RegisterCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerCourse(coursesList, event.getMessage())));
					break;
				case QuitTheSystem:
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}
	private static String registerCourse(CourseComponent coursesList, String message) {
		Course course = new Course(message);
		if (!coursesList.isRegisteredCourse(course.courseId)) {
			coursesList.vCourse.add(course);
			return "This course is successfully added.";
		} else
			return "This course is already registered.";
	}
	private static String makeCourseList(CourseComponent coursesList) {
		StringBuilder returnString = new StringBuilder();
		for (int j = 0; j < coursesList.vCourse.size(); j++) {
			returnString.append(coursesList.getCourseList().get(j).getString()).append("\n");
		}
		return returnString.toString();
	}
	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}
