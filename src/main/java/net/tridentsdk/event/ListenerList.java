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


package net.tridentsdk.event;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Nicho
 *
 */

public class ListenerList {

	/*
	 *Static list of managers for each registered event type
	 */
	
	public static HashMap<Class<Event>, ListenerList> managers = new HashMap<Class<Event>, ListenerList>();
	
	/*
	 * Array of listeners sorted from lowest priority to highest (order of execution)
	 */
	
	private RegisteredListener[] listeners = null;
	
	/*
	 * EnumMap used to sort listeners by importance
	 */
	
	private EnumMap<Importance, ArrayList<RegisteredListener>> importancemap = 
			new EnumMap<Importance, ArrayList<RegisteredListener>>(Importance.class);
	
	/**
	 * Load importance values from Importance enum to importancemap
	 * 
	 * @param type the type of event this EventList handles
	 */
	
	public ListenerList(){
		for(Importance i : Importance.values()){
			importancemap.put(i, new ArrayList<RegisteredListener>());
		}
	}
	
	/**
	 * Register a RegisteredListener to importancemap
	 * 
	 * @param l the RegisisteredListener being registered for this event
	 */
	
	public void register(RegisteredListener l){
		importancemap.get(l.getImportance()).add(l);
	}
	
	/**
	 * Unregister a RegisteredListener from importancemap
	 * 
	 * @param l the RegisteredListener being unregistered from this event
	 */
	
	public void unregister(RegisteredListener l){
		importancemap.get(l.getListener()).remove(l);
	}
	
	/**
	 * Convert importancemap map into listeners array
	 */
	
	public void toArray(){
		ArrayList<RegisteredListener> l = new ArrayList<RegisteredListener>();
		for(Map.Entry<Importance, ArrayList<RegisteredListener>> entry : importancemap.entrySet()){
			l.addAll(entry.getValue());
		}
		listeners = l.toArray(new RegisteredListener[l.size()]);
	}
	
	/**
	 * 
	 * @param event the event that is being passed
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	
	public void execute(Event event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		for(RegisteredListener l : listeners){
			l.execute(event);
		}
	}
}
