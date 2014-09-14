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


package net.tridentsdk.event.player;

import net.tridentsdk.api.entity.Player;
import net.tridentsdk.event.Cancelleable;
import net.tridentsdk.event.Event;

/**
 * 
 * @author Nicho
 *
 */

public class PlayerEvent extends Event implements Cancelleable{

	private Player player;
	
	private boolean isCancelled = false;
	
	public PlayerEvent(final Player player){
		this(player, false);
	}
	
	/**
	 * 
	 * @param player the player associated with that event
	 * @param async the boolean that determines if event is asynchronous
	 */
	
	public PlayerEvent(final Player player, boolean async){
		super(async);
		this.player = player;
	}
	
	/**
	 * 
	 * @return return the player associated with the event
	 */
	
	public final Player getPlayer(){
		return player;
	}
	
	/**
	 * Set whether or not the event is cancelled
	 * 
	 * @param b event is cancelled if true
	 */
	
	public void setCancelled(boolean b){
		isCancelled = b;
	}
	
	/**
	 * Get whether or not event has been cancelled
	 * 
	 * @return  returns true if event is cancelled
	 */
	
	public boolean isCancelled(){
		return isCancelled;
	}
	
	
}
