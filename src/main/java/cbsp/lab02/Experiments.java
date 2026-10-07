package cbsp.lab02;

import java.util.Locale;
import javax.sound.sampled.LineUnavailableException;

/** Prepared runners. Students implement the Task 4 JSyn methods in this class. */
public final class Experiments {
    private Experiments() { }

    public static void task1(TaskParameters p, boolean play) throws LineUnavailableException {
        System.out.printf(Locale.ROOT,
                "Task 1: student ID=%s, f=%.0f Hz, A=%.2f, sample rate=%d Hz, duration=%.0f s%n",
                p.studentId(), p.frequency(), p.amplitude(), p.sampleRate(), p.duration());

        double[] samples = SignalGenerator.generateSine(p.frequency(), p.amplitude(),
                0.0, p.sampleRate(), p.duration());
        render("Sine", samples, p.sampleRate(), p.duration(), play, true);
    }

    public static void task2(TaskParameters p, boolean play) throws LineUnavailableException {
        double[] additive = SignalGenerator.generateAdditive(p.additiveFrequency(),
                p.harmonicAmplitudes(), p.sampleRate(), p.duration());
        double[] fundamental = SignalGenerator.generateSine(p.additiveFrequency(),
                p.harmonicAmplitudes()[0], 0, p.sampleRate(), p.duration());
        double[] mixed = SignalMixer.mix(fundamental, additive);
        render("Fundamental", fundamental, p.sampleRate(), p.duration(), play, true);
        render("Five harmonics", additive, p.sampleRate(), p.duration(), play, true);
        render("Fundamental + harmonic signal", mixed, p.sampleRate(), p.duration(), play, true);
    }

    public static void task3(TaskParameters p, boolean play) throws LineUnavailableException {
        double[] original = SignalGenerator.generateSine(p.frequency(), p.amplitude(),
                Math.PI / 2, p.sampleRate(), p.duration());
        double[] faded = original.clone();
        Envelope.applyFade(faded, p.sampleRate(), 0.100, 0.100);
        int fadeCount = (int) Math.round(p.sampleRate() * 0.100);
        if (faded.length != original.length || Math.abs(faded[0]) > 1e-9
                || Math.abs(faded[faded.length - 1]) > 1e-9) {
            throw new IllegalArgumentException("Task 3 check failed: length or endpoints.");
        }
        for (int i = fadeCount; i < faded.length - fadeCount; i++) {
            if (faded[i] != original[i]) {
                throw new IllegalArgumentException("Task 3 check failed: middle samples changed.");
            }
        }
        render("Without fades", original, p.sampleRate(), p.duration(), play, false);
        render("100 ms fades", faded, p.sampleRate(), p.duration(), play, false);
    }

    public static void task4(TaskParameters p) throws InterruptedException {
        JSynGenerator.playJSynSine(p);
        JSynGenerator.playJSynAdditive(p);
    }

    public static void task5(TaskParameters p, boolean play)
        throws LineUnavailableException {

        double[] differences = {
            p.beatDifference(),
            p.beatDifference() + 3,
            p.beatDifference() + 6
        };

        for (double difference : differences) {
            double f1 = p.frequency();
            double f2 = f1 + difference;

            double[] first = SignalGenerator.generateSine(
                    f1, 0.3, 0.0, p.sampleRate(), 4.0);
            double[] second = SignalGenerator.generateSine(
                    f2, 0.3, 0.0, p.sampleRate(), 4.0);

            double[] mixed = SignalMixer.mix(first, second);

            Envelope.applyFade(mixed, p.sampleRate(), 0.010, 0.010);

            System.out.printf(Locale.ROOT,
                    "f1=%.0f Hz, f2=%.0f Hz, beat=%.0f Hz%n",
                    f1, f2, Math.abs(f2 - f1));

            render("Beating", mixed, p.sampleRate(), 4.0, play, true);
        }
    }

    public static void task6(TaskParameters p, boolean play) throws LineUnavailableException {
        double[] noise = SignalGenerator.generateWhiteNoise(p.noiseAmplitude(),
                p.sampleRate(), 2, p.noiseSeed());
         System.out.printf(Locale.ROOT, "Noise mean=%.6f, RMS=%.6f%n",
                SignalAnalysis.mean(noise), SignalAnalysis.rms(noise));
        render("White noise", noise, p.sampleRate(), 2, play, false);
    }

    public static void task7(TaskParameters p, boolean play) throws LineUnavailableException {
        for (double attack : new double[]{0.0005, 0.005, 0.015}) {
            double[] tone = SignalGenerator.generateSine(p.clickFrequency(), 0.3,
                    0, p.sampleRate(), 0.060);
            Envelope.applyPercussiveEnvelope(tone, p.sampleRate(), attack);
            if (Math.abs(tone[0]) > 1e-9 || Math.abs(tone[tone.length - 1]) > 1e-9) {
                throw new IllegalArgumentException("Task 7 check failed: non-zero endpoints.");
            }
            render("Attack " + (attack * 1000) + " ms", tone, p.sampleRate(), 0.060, false, true);
            int gap = (int) Math.round(0.440 * p.sampleRate());
            double[] sequence = new double[6 * (tone.length + gap)];
            for (int i = 0; i < 6; i++) {
                System.arraycopy(tone, 0, sequence, i * (tone.length + gap), tone.length);
            }
            if (play) AudioOutput.play(sequence, p.sampleRate());
        }
    }

    public static void task8(TaskParameters p, boolean play) throws LineUnavailableException {
        for (int count : new int[]{5, 11, 21}) {
            double[] packet = SignalGenerator.generateWavePacket(440, 2, count,
                    0.2, p.sampleRate(), 0.4);
            render("Packet: " + count + " components", packet,
                    p.sampleRate(), 0.4, false, false);
            if (play) {
                double[] listeningCopy = packet.clone();
                Envelope.applyFade(listeningCopy, p.sampleRate(), 0.010, 0.010);
                render("Packet playback", listeningCopy, p.sampleRate(), 0.4, true, true);
            }
        }
    }

    private static void render(String label, double[] samples, int sampleRate,
            double duration, boolean play, boolean attenuate) throws LineUnavailableException {
        if (samples == null || samples.length != Math.round(sampleRate * duration)) {
            throw new IllegalArgumentException(label + ": incorrect sample count.");
        }
        double gain = attenuate ? SignalMixer.attenuateToPeak(samples, 0.8) : 1.0;
        double peak = 0;
        for (double sample : samples) {
            if (!Double.isFinite(sample)) throw new IllegalArgumentException(label + ": non-finite sample.");
            peak = Math.max(peak, Math.abs(sample));
        }
        if (peak > (attenuate ? 0.8 : 1.0) + 1e-9) {
            throw new IllegalArgumentException(label + ": peak exceeds limit.");
        }
        System.out.printf(Locale.ROOT, "%s: N=%d, peak=%.6f, gain=%.6f%n",
                label, samples.length, peak, gain);
        if (play) AudioOutput.play(samples, sampleRate);
    }
}
