package framework;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;

public class RMIEventBusImpl extends UnicastRemoteObject implements RMIEventBus {
    private static final long serialVersionUID = 1L; //Default value   
    static Vector<EventQueue> eventQueueList;

	public RMIEventBusImpl() throws RemoteException {
		super();
		eventQueueList = new Vector<>(15, 1);
	}
	public static void main(String[] args) {
		try {
			RMIEventBusImpl eventBus = new RMIEventBusImpl();
			Registry registry = LocateRegistry.createRegistry(8080);
			registry.bind("EventBus", eventBus);
//			Naming.bind("EventBus", eventBus);
			System.out.println("Event Bus is running now...");
		} catch (Exception e) {
			System.out.println("Event bus startup error: " + e);
		}
		
	}

	@Override
	synchronized public long register() throws RemoteException {
		EventQueue newEventQueue = new EventQueue();
		eventQueueList.add( newEventQueue );
		System.out.println("Component (ID:"+ newEventQueue.getId() + ") is registered...");
		return newEventQueue.getId();
	}

	@Override
	synchronized public void unRegister(long id) throws RemoteException {

		Iterator<EventQueue> iterator = eventQueueList.iterator();
		while (iterator.hasNext()) {
			EventQueue eventQueue = iterator.next();
			if (Objects.equals(eventQueue.getId(), id)) {
				iterator.remove();
//				eventQueueList.remove(eventQueue);		// BUG: ConcurrentModificationException
				System.out.println("Component (ID:"+ id + ") is unregistered...");
			}
		}

	}

	@Override
	synchronized public void sendEvent(Event sentEvent) throws RemoteException {
		EventQueue eventQueue;
		for ( int i = 0; i < eventQueueList.size(); i++ ) {
			eventQueue = eventQueueList.get(i);
			eventQueue.addEvent(sentEvent);
			eventQueueList.set(i, eventQueue);
		}
		System.out.println("Event Inforamtion(ID: "+sentEvent.getEventId()+", Message: "+sentEvent.getMessage()+")");
	}

	@Override
	synchronized public EventQueue receiveEventQueue(long senderId) throws RemoteException {
		EventQueue copiedQueue =  null;

		for (EventQueue eventQueue : eventQueueList) {
			if (Objects.equals(eventQueue.getId(), senderId)) {
				copiedQueue = eventQueue.getCopy();
				eventQueue.clearEventQueue();
			}
		}

		return copiedQueue;
	}
}