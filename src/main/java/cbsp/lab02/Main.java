package cbsp.lab02;

import java.util.Arrays;
import java.util.Locale;

/** Command-line entry point for parameter preview and experiments. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equals("--help"))) {
            printHelp();
            return;
        }
        if (args.length != 2 || !isCommand(args[0])) {
            printHelp();
            System.exit(2);
            return;
        }
        try {
            TaskParameters p = GenerateParameters.generate(args[1]);
            if (args[0].equals("--parameters")) {
                printParameters(p);
            } else {
                boolean play = args[0].startsWith("--task");
                String task = args[0].replace("--task", "").replace("--check", "");
                switch (task) {
                    case "1" -> Experiments.task1(p, play);
                    case "2" -> Experiments.task2(p, play);
                    case "3" -> Experiments.task3(p, play);
                    case "4" -> Experiments.task4(p);
                    case "5" -> Experiments.task5(p, play);
                    case "6" -> Experiments.task6(p, play);
                    case "7" -> Experiments.task7(p, play);
                    case "8" -> Experiments.task8(p, play);
                    default -> throw new IllegalArgumentException("Unknown task.");
                }
            }
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Playback interrupted.");
        } catch (javax.sound.sampled.LineUnavailableException e) {
            System.err.println("Audio device unavailable: " + e.getMessage());
            System.err.println("Use the corresponding --check command to run without PCM playback.");
            System.exit(2);
        }
    }

    private static boolean isCommand(String command) {
        return command.equals("--parameters") || command.equals("--task4")
                || command.matches("--(task|check)[1235678]");
    }

    private static void printHelp() {
        System.out.println("Usage: Main <command> <eight-digit-student-id>");
        System.out.println("--parameters                 Print individual parameters");
        System.out.println("--task1/2/3/4/5/6/7/8        Run and play a task (Task 8 optional)");
        System.out.println("--check1/2/3/5/6/7/8         Run without audio (no --check4)");
        System.out.println("Reference example: Main --check2 11111111");
    }

    private static void printParameters(TaskParameters p) {
        System.out.println("Generator: " + p.generatorVersion());
        System.out.println("Student ID: " + p.studentId());
        System.out.println("Sample rate [Hz]: " + p.sampleRate());
        System.out.printf(Locale.ROOT,
                "Task 1: f=%.0f Hz, A=%.2f, duration=%.0f s%n",
                p.frequency(), p.amplitude(), p.duration());
        System.out.printf(Locale.ROOT, "Task 2: f0=%.0f Hz%n", p.additiveFrequency());
        System.out.println("Harmonic amplitudes (k=1..5): "
                + Arrays.toString(p.harmonicAmplitudes()));
        System.out.println("Task 3: fade-in=100 ms, fade-out=100 ms");
        System.out.println("Task 4: reuse Task 1 and Task 2 parameters in JSyn");
        System.out.printf(Locale.ROOT,
                "Task 5: f1=%.0f Hz, differences=%.0f/%.0f/%.0f Hz, duration=4 s%n",
                p.frequency(), p.beatDifference(), p.beatDifference() + 3,
                p.beatDifference() + 6);
        System.out.printf(Locale.ROOT, "Task 6: noise A=%.2f, seed=%d, duration=2 s%n",
                p.noiseAmplitude(), p.noiseSeed());
        System.out.printf(Locale.ROOT,
                "Task 7: f=%.0f Hz, A=0.3, duration=60 ms, attack=0.5/5/15 ms%n",
                p.clickFrequency());
        System.out.println("Task 8 (optional): center=440 Hz, spacing=2 Hz, components=5/11/21, center time=0.2 s, duration=0.4 s");
    }
}
