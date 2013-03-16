/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Oct 6, 2009
 * All Rights Reserved
 */
package com.javaeye.metaphy.sound;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.javaeye.metaphy.game.Game;

/**
 * The SimpleSoundPlayer encapsulates a sound that can be opened from the file
 * system and later played.
 * 
 * All sounds are cached after it's first invoked
 */
public class SoundPlayer {
	private static Sound EQUAL = null;
	private static Sound KILL = null;
	private static Sound KILLED = null;
	private static Sound MOVE = null;
	private static Sound PICK = null;
	private static Sound SHOWFLAG = null;
	private static Sound START = null;
	private static Sound TIMER = null;
	private static Sound GAME_END = null;

	private static AudioFormat EQUAL_FORMAT;
	private static AudioFormat KILL_FORMAT;
	private static AudioFormat KILLED_FORMAT;
	private static AudioFormat MOVE_FORMAT;
	private static AudioFormat PICK_FORMAT;
	private static AudioFormat SHOWFLAG_FORMAT;
	private static AudioFormat START_FORMAT;
	private static AudioFormat TIMER_FORMAT;
	private static AudioFormat GAME_END_FORMAT;

	/**
	 * Initialize the player to cached all sounds
	 */
	static {
		EQUAL = getSound(getAudio("equal"));
		KILL = getSound(getAudio("kill"));
		KILLED = getSound(getAudio("killed"));
		MOVE = getSound(getAudio("move"));
		PICK = getSound(getAudio("pick"));
		SHOWFLAG = getSound(getAudio("showflag"));
		START = getSound(getAudio("start"));
		TIMER = getSound(getAudio("timer"));
		GAME_END = getSound(getAudio("game_end"));

		EQUAL_FORMAT = getAudio("equal").getFormat();
		KILL_FORMAT = getAudio("kill").getFormat();
		KILLED_FORMAT = getAudio("killed").getFormat();
		MOVE_FORMAT = getAudio("move").getFormat();
		PICK_FORMAT = getAudio("pick").getFormat();
		SHOWFLAG_FORMAT = getAudio("showflag").getFormat();
		START_FORMAT = getAudio("start").getFormat();
		TIMER_FORMAT = getAudio("timer").getFormat();
		GAME_END_FORMAT = getAudio("game_end").getFormat();
		// System.out.println("Sounds cached!");
	}

	/**
	 * A wapper method of play(InputStream )
	 */
	public static void play(String wav) {
		Sound sound = null;
		AudioFormat format = null;

		if (wav.equalsIgnoreCase("equal")) {
			sound = EQUAL;
			format = EQUAL_FORMAT;
		} else if (wav.equalsIgnoreCase("kill")) {
			sound = KILL;
			format = KILL_FORMAT;
		} else if (wav.equalsIgnoreCase("killed")) {
			sound = KILLED;
			format = KILLED_FORMAT;
		} else if (wav.equalsIgnoreCase("move")) {
			sound = MOVE;
			format = MOVE_FORMAT;
		} else if (wav.equalsIgnoreCase("pick")) {
			sound = PICK;
			format = PICK_FORMAT;
		} else if (wav.equalsIgnoreCase("showflag")) {
			sound = SHOWFLAG;
			format = SHOWFLAG_FORMAT;
		} else if (wav.equalsIgnoreCase("start")) {
			sound = START;
			format = START_FORMAT;
		} else if (wav.equalsIgnoreCase("timer")) {
			sound = TIMER;
			format = TIMER_FORMAT;
		} else if (wav.equalsIgnoreCase("game_end")) {
			sound = GAME_END;
			format = GAME_END_FORMAT;
		}

		if (sound != null && format != null) {
			InputStream stream = new ByteArrayInputStream(sound.getSamples());
			play(stream, format);
		}
	}

	/**
	 * Plays a stream. This method blocks (doesn't return) until the sound is
	 * finished playing.
	 */
	public static void play(InputStream source, AudioFormat format) {
		/*
		 * Play the sound only when the soundOn == true;
		 */
		if (!Game.ME.isSoundOn())
			return;

		// use a short, 100ms (1/10th sec) buffer for real-time
		// change to the sound stream
		int bufferSize = format.getFrameSize()
				* Math.round(format.getSampleRate());
		byte[] buffer = new byte[bufferSize];

		// create a line to play to
		SourceDataLine line;
		try {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, bufferSize);
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
			return;
		}

		// start the line
		line.start();

		// copy data to the line
		try {
			int numBytesRead = 0;
			while (numBytesRead != -1) {
				numBytesRead = source.read(buffer, 0, buffer.length);
				if (numBytesRead != -1) {
					line.write(buffer, 0, numBytesRead);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// wait until all data is played
		line.drain();

		// close the line
		line.close();
	}

	/**
	 * Get the audio file
	 */
	private static AudioInputStream getAudio(String audio) {
		AudioInputStream audioStream = null;
		try {
			URLClassLoader urlLoader = (URLClassLoader) (Game.class
					.getClassLoader());
			URL aurl = urlLoader.findResource("res/sounds/" + audio + ".wav");
			audioStream = AudioSystem.getAudioInputStream(aurl);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return audioStream;
	}

	/**
	 * Gets the sound(samples) from an AudioInputStream as an array of bytes.
	 */
	private static Sound getSound(AudioInputStream audioStream) {
		// get the number of bytes to read
		int length = (int) (audioStream.getFrameLength() * audioStream
				.getFormat().getFrameSize());

		// read the entire stream
		byte[] samples = new byte[length];
		DataInputStream is = new DataInputStream(audioStream);
		try {
			is.readFully(samples);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// return the samples
		return new Sound(samples);
	}
}
