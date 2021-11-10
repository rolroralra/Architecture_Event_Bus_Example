package framework;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIEventBus extends Remote {
	long register() throws RemoteException;
	void unRegister(long SenderID) throws RemoteException;
	void sendEvent(Event m) throws RemoteException;
	EventQueue receiveEventQueue(long senderId) throws RemoteException;

	static void addShutDownHook(RMIEventBus eventBus, long componentId) throws RemoteException {
		Runtime.getRuntime().addShutdownHook((new Thread(() -> {
			try {
				eventBus.unRegister(componentId);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		})));
	}
}
