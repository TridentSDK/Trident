package net.tridentsdk.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * 
 * @author Nicho
 *
 */

public class RegisteredListener {

	private Listener listener;
	private Importance importance;
	private Method method;
		
	public RegisteredListener(final Listener listener, Importance importance, Method method){
		this.listener = listener;
		this.importance = importance;
		this.method = method;
	}
	
	/**
	 * 
	 * @return return the plugin's listener class
	 */

	public Listener getListener(){
		return listener;
	}
	
	/**
	 * 
	 * @return return the importance of this listener
	 */
	
	public Importance getImportance(){
		return importance;
	}
	
	/**
	 * Invokes the RegisteredListener method
	 * 
	 * @param event the event that is being executed
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	
	public void execute(final Event event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(listener, event);
	}
	
}
