package cbsp.lab02;

public final class Envelope {
    private Envelope() { }

    /** Task 3: multiply the buffer by linear end ramps in place. */
    public static void applyFade(double[] samples, int sampleRate,
            double fadeInSeconds, double fadeOutSeconds) {
        // TODO: Task 3: apply linear end fades in place with rounded sample counts.
        // Follow README for zero/one-sample fades and inclusive ramp endpoints.
        throw new UnsupportedOperationException("TODO: applyFade");
    }

    /** Task 7: triangular envelope: 0 at start, 1 at attack index, 0 at end. */
    public static void applyPercussiveEnvelope(double[] samples, int sampleRate,
            double attackSeconds) {
        // TODO: Task 7: apply a triangular envelope in place with the rounded attack
        // peak index and endpoint gains specified in README.
        throw new UnsupportedOperationException("TODO: applyPercussiveEnvelope");
    }
}
