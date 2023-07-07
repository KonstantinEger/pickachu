package pickachu.components.sound;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lejos.hardware.Sound;

public class SoundUnit {
	
	private volatile boolean running = true;
	private Worker worker;
	private static final int MIN_EVENT_DELAY = 1000;
	private static final int MAX_EVENT_DELAY = 41000;
	
	private final List<Sounds> pickachuSounds;

	public SoundUnit() {
		System.out.println("Creating SoundUnit");
		pickachuSounds = Arrays.asList(Sounds.Pika1, Sounds.Pika2, Sounds.Pika3, Sounds.Pika4, Sounds.Pika5);
		worker  = new Worker();
		worker.start();
		//Sound.playSample(new File(DataProvider.getFileSystem().getPath("opening_2.wav").toString()));
	}
	
	public void stop() {
		running = false;
	}
	
	public void playSound(Sounds sound) {
		Sound.playSample(sound.file);
	}
	
	
	/**
	 * Organizes Sound files
	 */
	public enum Sounds{
		Pika1(new File("pikachu_sound_1.wav")),
		Pika2(new File("pikachu_sound_2.wav")),
		Pika3(new File("pikachu_sound_3.wav")),
		Pika4(new File("pikachu_sound_4.wav")),
		Pika5(new File("pikachu_sound_5.wav")),
		BattleBegin(new File("battle.wav")),
		BattleWin(new File("battle_win.wav"))

		;
		final File file;
		
		Sounds(File file) {
			this.file = file;
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
