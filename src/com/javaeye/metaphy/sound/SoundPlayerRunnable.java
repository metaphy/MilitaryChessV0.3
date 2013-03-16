/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Dec 22, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.sound;

public class SoundPlayerRunnable implements Runnable{
	private String sound = null;
	
	public SoundPlayerRunnable(String sound){
		this.sound = sound;
	}
	
	@Override
	public void run() {
		SoundPlayer.play(sound);		
	}
}
