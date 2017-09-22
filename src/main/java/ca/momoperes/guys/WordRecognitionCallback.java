package ca.momoperes.guys;

import edu.cmu.sphinx.api.SpeechResult;

public interface WordRecognitionCallback {
    void onResult(SpeechResult result);
}
