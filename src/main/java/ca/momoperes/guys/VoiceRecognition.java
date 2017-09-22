package ca.momoperes.guys;


import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

import java.io.IOException;

public class VoiceRecognition {
    private static final String PATH_ACOUSTIC_MODEL = "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String PATH_DICTIONARY = "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    private static final String PATH_LANGUAGE_MODEL = "resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin";

    private final Configuration configuration;
    private final LiveSpeechRecognizer recognizer;
    private Thread localThread = null;
    private boolean running;

    public VoiceRecognition() throws IOException {
        this(PATH_ACOUSTIC_MODEL, PATH_DICTIONARY, PATH_LANGUAGE_MODEL);
    }

    public VoiceRecognition(String acousticModel, String dictionary, String languageModel) throws IOException {
        this.configuration = new Configuration();
        configuration.setAcousticModelPath(acousticModel);
        configuration.setDictionaryPath(dictionary);
        configuration.setLanguageModelPath(languageModel);
        this.recognizer = new LiveSpeechRecognizer(configuration);
    }

    public void stop() {
        if (localThread != null) {
            localThread.stop();
        }
        try {
            recognizer.stopRecognition();
        } catch (Exception ignored) {
        }
        running = false;
    }

    public void start(final WordRecognitionCallback callback) {
        if (running) {
            throw new IllegalStateException("Word recognition is already active.");
        }
        if (localThread != null) {
            localThread.interrupt();
        }
        this.running = true;
        localThread = new Thread(() -> {
            recognizer.startRecognition(true);
            SpeechResult result;
            while (running) {
                result = recognizer.getResult();
                if (result != null) {
                    callback.onResult(result);
                }
            }
            stop();
        });
        localThread.start();
    }

    public boolean isRunning() {
        return running;
    }
}
