package cbsp.lab02;

public final class SignalMixer {
    private SignalMixer() { }

    /** Task 2: return a new element-wise sum of equal-length buffers. */
    public static double[] mix(double[] first, double[] second) {
        // TODO: Task 2: return a new element-wise sum, leaving both input buffers unchanged.
        throw new UnsupportedOperationException("TODO: mix");
    }

    /** Task 1: attenuate in place only when needed, and return the applied gain. */
    public static double attenuateToPeak(double[] samples, double peakLimit) {
        // TODO: Task 1: attenuate in place with one common gain only when the absolute
        // peak exceeds peakLimit; return the applied gain, or 1 if unchanged.
        throw new UnsupportedOperationException("TODO: attenuateToPeak");
    }
}
