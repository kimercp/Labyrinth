package Labyrinth;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 This class has been created to play any sound given as parameter in String which is name of wave sample
 */
public class PlaySound {
    
    // constructor has been given name of file as string
    public PlaySound(String soundToPlay){
        try {
                // make url to sound file from resources
                URL url = this.getClass().getClassLoader().getResource("MyLabResources/"+soundToPlay);
                // do only if url is not null, means the sound file is found
                if (url != null){
                    // open an audio input stream
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    // get a sound clip
                    Clip clip = AudioSystem.getClip();
                    // open audio clip and load samples from the audio input stream
                    clip.open(audioIn);
                    // start playing the clip (sound)
                    clip.start();
                }
            } catch ( UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace(System.out);
            }
    }
}
