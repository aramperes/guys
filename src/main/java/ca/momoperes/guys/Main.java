package ca.momoperes.guys;

import edu.cmu.sphinx.api.SpeechResult;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) throws IOException {
        BlockingQueue<SpeechResult> queue = new LinkedBlockingQueue<>();
        PipedInputStream stream = new PipedInputStream();
        new Thread(() -> {
            try {
                MicrophoneManager.openAndWriteTo(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        VoiceRecognition recognition = new VoiceRecognition();
        recognition.setStream(stream);
        recognition.start(queue::add);

        while (recognition.isRunning()) {
            try {
                SpeechResult take = queue.take();
                if (take != null) {
                    System.out.println("Hypothesis: " + take.getHypothesis());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stream.close();
    }
}
