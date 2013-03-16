/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Oct 7, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.sound;

public class Sound {
	/*
	 * The sound samples which are stored as a byte array
	 */
	private byte[] samples;
	
	public Sound(){}
	
	public Sound(byte[] samples) {
        this.samples = samples;
    }

	public byte[] getSamples() {
		return samples;
	}

	public void setSamples(byte[] samples) {
		this.samples = samples;
	}
}
