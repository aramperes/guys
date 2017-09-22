package ca.momoperes.guys;

import edu.cmu.sphinx.api.SpeechResult;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) throws IOException {
        BlockingQueue<SpeechResult> queue = new LinkedBlockingQueue<>();

        VoiceRecognition recognition = new VoiceRecognition();
        recognition.start(queue::add);

        while (recognition.isRunning()) {
            try {
                SpeechResult take = queue.take();
                if (take != null) {
                    String hypothesis = take.getHypothesis();
                    String pronunciation = take.getResult().getBestPronunciationResult();
                    System.out.println("Hypothesis: " + hypothesis);
                    System.out.println("Pronunciation: " + pronunciation);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
