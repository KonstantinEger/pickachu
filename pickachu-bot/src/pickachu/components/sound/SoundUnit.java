package pickachu.components.sound;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lejos.hardware.Sound;

public class SoundUnit {
	
	private volatile boolean running = true;
	private Worker eventGenerator;
	private static final int MIN_EVENT_DELAY = 1000;
	private static final int MAX_EVENT_DELAY = 41000;
	
	private final List<Sounds> pickachuSounds;

	public SoundUnit() {
		System.out.println("Creating SoundUnit");
		pickachuSounds = Arrays.asList(Sounds.Pika1, Sounds.Pika2, Sounds.Pika3, Sounds.Pika4, Sounds.Pika5);
		eventGenerator  = new Worker();
		eventGenerator.start();
	}
	
	public void stop() {
		running = false;
	}
	
	public void playSound(Sounds sound) {
		Sound.playSample(new File(sound.filename), Sound.VOL_MAX);
	}
	
	
	/**
	 * Organizes Sound files
	 */
	public enum Sounds{
		Pika1("pikachu_sound_1.wav"),
		Pika2("pikachu_sound_2.wav"),
		Pika3("pikachu_sound_3.wav"),
		Pika4("pikachu_sound_4.wav"),
		Pika5("pikachu_sound_5.wav"),
		BattleBegin("battle.wav"),
		BattleWin("battle_win.wav")
		;
		
		final String filename;
		
		Sounds(String filename) {
			this.filename = filename;
		}
	}
	
	private class Worker extends Thread{
		
		Random random = new Random();
		
		@Override
		public void run() {
			while(running) {
				try {
					int delay = random.nextInt(MAX_EVENT_DELAY) + MIN_EVENT_DELAY;
					Thread.sleep(delay);
					playSound(pickachuSounds.get(random.nextInt(pickachuSounds.size())));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
