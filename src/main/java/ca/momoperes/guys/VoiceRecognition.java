package ca.momoperes.guys;


import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

import java.io.IOException;
import java.io.InputStream;

public class VoiceRecognition {
    private static final String PATH_ACOUSTIC_MODEL = "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String PATH_DICTIONARY = "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    private static final String PATH_LANGUAGE_MODEL = "resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin";

    private final Configuration configuration;
    private final StreamSpeechRecognizer recognizer;
    private InputStream stream = null;
    private Thread localThread = null;
    private boolean running;
    private WordRecognitionCallback callback;

    public VoiceRecognition() throws IOException {
        this(PATH_ACOUSTIC_MODEL, PATH_DICTIONARY, PATH_LANGUAGE_MODEL);
    }

    public VoiceRecognition(String acousticModel, String dictionary, String languageModel) throws IOException {
        this.configuration = new Configuration();
        configuration.setAcousticModelPath(acousticModel);
        configuration.setDictionaryPath(dictionary);
        configuration.setLanguageModelPath(languageModel);
        this.recognizer = new StreamSpeechRecognizer(configuration);
    }

    public InputStream getStream() {
        return stream;
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

    public void setStream(InputStream stream) {
        stop();
        this.stream = stream;
    }

    public void start(final WordRecognitionCallback callback) {
        if (stream == null) {
            throw new IllegalStateException("No stream set.");
        }
        if (running) {
            throw new IllegalStateException("Word recognition is already active.");
        }
        if (localThread != null) {
            localThread.interrupt();
        }
        this.callback = callback;
        this.running = true;
        localThread = new Thread(() -> {
            recognizer.startRecognition(stream);
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
