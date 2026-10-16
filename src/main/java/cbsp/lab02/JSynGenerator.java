package cbsp.lab02;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;

/** Student implementations of digital signal generators. */
public final class JSynGenerator {
    private JSynGenerator() { }

    /** Task 4: implement and manage the JSyn sine audio graph. */
    protected static void playJSynSine(TaskParameters p) throws InterruptedException {
        // TODO: Task 4: build a phase-zero JSyn sine graph with the assigned parameters
        // and stereo routing. Use the assigned sample rate and duration; always
        // stop the synthesizer in finally and propagate interruption after cleanup.
        throw new UnsupportedOperationException("TODO: playJSynSine");
    }

    /** Task 4: implement and manage the JSyn harmonic audio graph. */
    protected static void playJSynAdditive(TaskParameters p) throws InterruptedException {
        // TODO: Task 4: build a JSyn graph with one oscillator per harmonic and stereo
        // routing. Preserve amplitude ratios with the shared gain from README.
        // Use the assigned sample rate and duration; always stop the synthesizer
        // in finally and propagate interruption after cleanup.
        throw new UnsupportedOperationException("TODO: playJSynAdditive");
    }
}
