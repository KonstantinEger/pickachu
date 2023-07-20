package pickachu.components.sound;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lejos.hardware.Sound;
import pickachu.components.DataProvider;


/**
 * OUT OF ORDER
 * 
 * Component for playing sounds randomly and on demand. Sounds where ment to be places in the rsc folder
 * but this component currently does not work.
 */
public class SoundUnit {
	
	private volatile boolean running = true;
	private Worker eventGenerator;
	private static final int MIN_EVENT_DELAY = 1000;
	private static final int MAX_EVENT_DELAY = 41000;
	
	private final List<Sounds> pickachuSounds;

	public SoundUnit() {
		pickachuSounds = Arrays.asList(Sounds.Pika1, Sounds.Pika2, Sounds.Pika3, Sounds.Pika4, Sounds.Pika5);
		eventGenerator  = new Worker();
		eventGenerator.start();
	}
	
	public void stop() {
		running = false;
	}
	
	public void playSound(Sounds sound) {
		File file = new File(DataProvider.getFileSystem().getPath(sound.filename).toString());
		Sound.playSample(file, Sound.VOL_MAX);
	}
	
	
	/**
	 * Organizes Sound files
	 * 
	 * rsc/sounds/<filename> will point to the rsc/sounds folder,
	 * <filename> only will point towards the lejos programs folder
	 * (Neither did work)
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
		final String prefix = "rsc/sounds/";
		
		Sounds(String filename) {
			this.filename = prefix + filename;
		}
	}
	
	/**
	 * Generates random sound events in random intervals.
	 */
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
