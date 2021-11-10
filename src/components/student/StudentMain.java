package components.student;

import framework.Event;
import framework.EventId;
import framework.EventQueue;
import framework.RMIEventBus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;

public class StudentMain {
	public static void main(String[] args) throws IOException, NotBoundException {
		RMIEventBus eventBus = (RMIEventBus) LocateRegistry.getRegistry(8080).lookup("EventBus");
//		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("** StudentMain(ID:" + componentId + ") is successfully registered. \n");

		RMIEventBus.addShutDownHook(eventBus, componentId);

		StudentComponent studentsList = new StudentComponent("Students.txt");
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
				case ListStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeStudentList(studentsList)));
					break;
				case RegisterStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerStudent(studentsList, event.getMessage())));
					break;
				case QuitTheSystem:
					printLogEvent("Get", event);
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}

	private static String registerStudent(StudentComponent studentsList, String message) {
		Student  student = new Student(message);
		if (!studentsList.isRegisteredStudent(student.studentId)) {
			studentsList.vStudent.add(student);
			return "This student is successfully added.";
		} else
			return "This student is already registered.";
	}

	private static String makeStudentList(StudentComponent studentsList) {
		StringBuilder returnString = new StringBuilder();
		for (int j = 0; j < studentsList.vStudent.size(); j++) {
			returnString.append(studentsList.getStudentList().get(j).getString()).append("\n");
		}
		return returnString.toString();
	}

	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}

}
