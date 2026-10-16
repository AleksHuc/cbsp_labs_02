package cbsp.lab02;

import java.util.Random;

/** Student implementations of digital signal generators. */
public final class SignalGenerator {
    private SignalGenerator() { }

    /** Task 1 */
    public static double[] generateSine(double frequency, double amplitude,
            double phase, int sampleRate, double duration) {
        // TODO: Task 1: return a new sine buffer with the sample count, time and phase
        // conventions from README; do not normalize or play it.
        throw new UnsupportedOperationException("TODO: generateSine");
    }

    /** Task 2 */
    public static double[] generateAdditive(double fundamentalFrequency,
            double[] harmonicAmplitudes, int sampleRate, double duration) {
        // TODO: Task 2: return the unscaled sum of phase-zero harmonics described in README.
        // Leave harmonicAmplitudes unchanged.
        throw new UnsupportedOperationException("TODO: generateAdditive");
    }

    /** Task 6 */
    public static double[] generateWhiteNoise(double amplitude, int sampleRate,
            double duration, long seed) {
        // TODO: Task 6: return a new uniform noise buffer in [-amplitude, amplitude)
        // using java.util.Random(seed), without normalization.
        throw new UnsupportedOperationException("TODO: generateWhiteNoise");
    }

    /** Task 8 */
    public static double[] generateWavePacket(double centerFrequency, double spacing,
            int componentCount, double centerTime, int sampleRate, double duration) {
        // TODO: Task 8: return the raw average of cosine components defined in README,
        // aligned at centerTime, without fades or attenuation.
        throw new UnsupportedOperationException("TODO: generateWavePacket");
    }
}
