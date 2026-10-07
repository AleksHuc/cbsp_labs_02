package cbsp.lab02;

/** Prepared infrastructure: keep unchanged when solving the assignment. */
public record TaskParameters(
        String generatorVersion,
        String studentId,
        int sampleRate,
        double frequency,
        double amplitude,
        double duration,
        double additiveFrequency,
        double[] harmonicAmplitudes,
        double beatDifference,
        double noiseAmplitude,
        long noiseSeed,
        double clickFrequency) {

    public TaskParameters {
        harmonicAmplitudes = harmonicAmplitudes.clone();
    }

    @Override
    public double[] harmonicAmplitudes() {
        return harmonicAmplitudes.clone();
    }
}
