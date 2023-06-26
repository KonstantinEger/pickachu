package pickachu.components.sound;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lejos.hardware.Sound;
import pickachu.components.ComponentInitializationError;
import pickachu.components.DataProvider;

public class SoundUnit {
	
	private final Worker soundEvents;
	private static final int MIN_EVENT_DELAY = 1000;
	private static final int MAX_EVENT_DELAY = 41000;
	
	private final List<Sounds> pickachuSounds;

	public SoundUnit() {
		pickachuSounds = Arrays.asList(Sounds.Pika1, Sounds.PikaN);
		Path path = DataProvider.getFileSystem().getPath(Sounds.Brackground.file);
		byte[] file;
		try {
			file = Files.readAllBytes(path);
		} catch (IOException e) {
			throw new ComponentInitializationError("");
		}
		Sound.playSample(file, 0, 4, 44000, Sound.VOL_MAX);
		soundEvents = new Worker();
		soundEvents.start();
	}
	
	public void stop() {
		soundEvents.running = false;
	}
	
	
	private void playSoundHelper(Sounds sound) {
		// todo
	}
	
	
	public void playBackgroundSound() {
		// todo play sound looping (however that may work)
		
	}
	
	public void playPickupSound() {
	}
	
	
	
	/**
	 * Organizes Sound files
	 */
	enum Sounds{
		Picku(""),
		Brackground("sound/opening_2.wav"),
		Pika1(""),
		PikaN("");
		
		final String file;
		
		Sounds(String file) {
			this.file = file;
		}
	}
	
	/*
	 * Worker Thread generating random sound events and starting their playback.
	 */
	class Worker extends Thread{
		
		boolean running = true;
		Random random = new Random();
		
		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(random.nextInt(MAX_EVENT_DELAY) + MIN_EVENT_DELAY);
					Sounds sounds = pickachuSounds.get(random.nextInt(pickachuSounds.size()));
					// todo play sound
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
