package cbsp.lab02;

import java.util.Random;

/** Prepared infrastructure. Version and random call order define the parameters. */
public final class GenerateParameters {
    public static final String VERSION = "lab02-v1";
    public static final int SAMPLE_RATE = 44_100;

    private GenerateParameters() { }

    public static TaskParameters generate(String studentId) {
        if (studentId == null || !studentId.matches("[0-9]{8}")) {
            throw new IllegalArgumentException("Student ID must contain exactly eight digits.");
        }
        Random random = new Random(Long.parseLong(studentId));
        double frequency = 220 + random.nextInt(661);
        double amplitude = (20 + random.nextInt(31)) / 100.0;
        double duration = 2 + random.nextInt(3);
        double additiveFrequency = 110 + random.nextInt(331);
        double[] amplitudes = new double[5];
        amplitudes[0] = amplitude;
        for (int i = 1; i < amplitudes.length; i++) {
            double ratio = (20 + random.nextInt(61)) / 100.0;
            amplitudes[i] = amplitude * ratio;
        }
        double beatDifference = 2 + random.nextInt(5);
        long noiseSeed = random.nextLong();
        double clickFrequency = 500 + random.nextInt(1001);
        return new TaskParameters(VERSION, studentId, SAMPLE_RATE,
                frequency, amplitude, duration, additiveFrequency, amplitudes,
                beatDifference, 0.2, noiseSeed, clickFrequency);
    }
}
