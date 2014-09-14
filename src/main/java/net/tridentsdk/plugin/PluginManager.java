/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package net.tridentsdk.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.tridentsdk.event.Event;
import net.tridentsdk.event.EventHandler;
import net.tridentsdk.event.Importance;
import net.tridentsdk.event.Listener;
import net.tridentsdk.event.ListenerList;
import net.tridentsdk.event.RegisteredListener;

/**
 * 
 * @author Nicho
 *
 */

public class PluginManager {

	/**
	 * Register a listener from a plugin. Reflects any method with the @EventHandler annotation,
	 * creates a RegisteredListener for each individual listener method, and registers the RegisteredListener
	 * to its appropriate EventList (creates an event list if it is null). EventLists are sorted by event type.
	 * 
	 * @param listener the listener class registered from a plugin
	 */
	
	public void registerListener(Listener listener){
		//Iterate through list of methods from listener
	    Method[] methods = listener.getClass().getMethods();
	    for(Method method: methods){
	    	
	    	//Isolate methods using @EventHandler annotation
	    	if(method.isAnnotationPresent(EventHandler.class)){
	    		
	    		//Get importance of method from handler
	    		EventHandler handler = method.getAnnotation(EventHandler.class);
	    		Importance importance = handler.importance();
	    		
	    		//Get array of parameters and check to make sure there is only 1
	    		Class<?>[] params = method.getParameterTypes();
	    		if(params.length == 1){
	    			
	    			//Loop to get the superclass below Object of the parameter (should be Event)
	    			Class<?> c = params[0];
	    			boolean b = true;
	    			while(b){
	    				if(!c.getSuperclass().equals(Object.class)){
	    					c = c.getSuperclass();
	    				}else{
	    					//End loop
	    					b = false;
	    				}
	    			}
	    			
	    			//Check to make sure class is an event
	    			if(c.equals(Event.class)){
	    				@SuppressWarnings("unchecked")
						Class<Event> eventtype = (Class<Event>) c;
	    				
	    				//If this event type has not been used yet, create new EventList
	    				if(!ListenerList.managers.containsKey(eventtype)) ListenerList.managers.put(eventtype, new ListenerList());
	    				
	    				//Create RegisteredListener and register it to the appropriate EventList
	    				RegisteredListener rl = new RegisteredListener(listener, importance, method);
	    				ListenerList.managers.get(eventtype).register(rl);
	    			}
	    		}else{
	    			//Listener methods must only have one event parameter (output error message)
	    		}
	    	}
	    }
	}
	
	/**
	 * 
	 * When there is an action, the event for that action is throw through this method
	 * 
	 * TODO: Exception handling in a more robust way
	 * 
	 * @param event the event that has been called
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	
	public static void passEvent(Event event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		ListenerList listenerlist = ListenerList.managers.get(event.getClass());
		if(listenerlist != null) listenerlist.execute(event);
	}
}
