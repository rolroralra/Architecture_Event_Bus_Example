package components.client.output;

import framework.Event;
import framework.EventId;
import framework.EventQueue;
import framework.RMIEventBus;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Objects;

public class ClientOutputMain {
	public static void main(String[] args) throws IOException, NotBoundException {
		RMIEventBus eventBusInterface = (RMIEventBus) LocateRegistry.getRegistry(8080).lookup("EventBus");
//		RMIEventBus eventBusInterface = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBusInterface.register();
		System.out.println("** ClientOutputMain (ID:" + componentId + ") is successfully registered...");

		RMIEventBus.addShutDownHook(eventBusInterface, componentId);

		boolean done = false;
		while (!done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			EventQueue eventQueue = eventBusInterface.receiveEventQueue(componentId);
			for(int i = 0; i < eventQueue.getSize(); i++)  {
				Event event = eventQueue.pollEvent();
				printEvent(event);

				//printLogReceive(event);

				switch (event.getEventId()) {
					case ListCourses:
					case ListStudents:
					case RegisterCourses:
					case RegisterStudents:
						break;
					case ClientOutput:
						printOutput(event);
						break;
					case QuitTheSystem:
//						printLogReceive(event);
//						eventBusInterface.unRegister(componentId);
						done = true;
						break;
				}
			}
		}
	}

	private static void printEvent(Event event) {
		System.out.println(event.getEventId());
	}
	private static void printOutput(Event event) {
		System.out.println(event.getMessage());
	}
}
