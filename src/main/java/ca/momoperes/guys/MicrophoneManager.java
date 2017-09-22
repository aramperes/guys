package ca.momoperes.guys;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class MicrophoneManager {
    public static void openAndWriteTo(PipedInputStream stream) throws IOException {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        TargetDataLine microphone;
        try {
            microphone = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            PipedOutputStream out = new PipedOutputStream(stream);
            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[CHUNK_SIZE];
            microphone.start();
            while (microphone.isOpen()) {
                numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                out.write(data, 0, numBytesRead);
            }
            microphone.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
