# Lab 02: Sound generation

## Setup

- JDK 21 and Maven 3.9.x.
- VS Code with **Extension Pack for Java**, or IntelliJ IDEA.
- Internet access for Maven dependencies, including JSyn via JitPack.
- An audio device for playback; numerical checks work without one.

Open the folder containing `pom.xml` in your IDE and select JDK 21 for the
project and Maven. In VS Code, use **Java: Configure Java Runtime** if needed;
in IntelliJ, import the folder as a Maven project.

```bash
java -version
javac -version
mvn -version
mvn clean compile
```

## Your parameters

Run the following command from the folder containing `pom.xml`. Replace
`11111111` with your eight-digit student ID, including leading zeroes. The
program prints your assigned parameters for the tasks; do not hard-code them
or change the parameter generator.

```bash
mvn -q compile exec:java -Dexec.args="--parameters 11111111"
```

For each task below, replace `11111111` with your student ID. `--checkN` runs
without playback and `--taskN` also plays the sound; Task 4 supports playback
only. `compile` rebuilds your code and `-q` hides Maven informational messages.
Start listening at low device volume. The checks provide basic diagnostics,
so also verify sample values and invalid inputs yourself.

## Signal conventions

- Mono `double[]` buffers; durations in seconds, frequencies in hertz, phases
  in radians. Assigned sample rate: 44,100 Hz.
- `N = Math.round(sampleRate * duration)`; indices `0 <= n < N`,
  `t = n / (double) sampleRate`. Do not add an endpoint sample.
- Require positive sample rate, finite positive duration, and rounded sample
  count from 1 through `Integer.MAX_VALUE`. Validate before casting/allocating.
- Require finite numerical inputs, `0 < frequency < sampleRate/2.0`, and
  generator amplitude in `[0,1]`. Zero amplitude is valid silence.
- Required arrays must be non-null, non-empty and contain finite samples.
- Throw `IllegalArgumentException` for invalid inputs before modifying buffers.
- Generators return new buffers. Methods documented as in-place modify their
  supplied buffers without changing length.
- Intermediate sums may exceed `[-1,1]`. Before playback, attenuate with one
  common factor to peak ≤ 0.8. Do not amplify quiet buffers or clip components.
- `AudioOutput` handles signed 16-bit little-endian mono PCM and cleanup; it
  does not synthesize, fade or normalize signals.

Use a tolerance such as `1e-9` when comparing floating-point sample values.

## Task 1 — Sine and phase

Generate a sine signal using the assigned frequency, amplitude and duration.
Support an arbitrary phase and implement common attenuation so that signals
can be played within the specified peak limit.

Expected sound: a steady, pure tone with constant pitch and loudness during
the signal, possibly with a brief click at the start or end.

Add your code in:

- `SignalGenerator.java`: `generateSine(frequency, amplitude, phase, sampleRate, duration)`.
- `SignalMixer.java`: `attenuateToPeak(samples, peakLimit)`.

For the sine, use:

```text
x[n] = A * sin(2*pi*f*n/fs + phase)
```

Use floating-point division for time and return a new buffer without playback
or normalization. Validate inputs according to the signal conventions above.

For attenuation, validate the entire buffer before modifying it. Require
`peakLimit` in `(0,1]`. Find the absolute peak and apply one common gain only
when it exceeds the limit. Modify samples in place and return the applied
gain; return 1 for silence or an already quiet buffer. Do not amplify quiet
signals or clip individual samples. Later tasks reuse this method.

The prepared `Experiments.task1()` generates the assigned sine at phase 0.
Its shared `render()` checks the sample count, attenuates to a peak limit of
0.8, checks finite samples and the resulting peak, and prints `N`, `peak` and
`gain`. It does not compare samples with the sine formula or test other phases.

Verify independently:

- `f=440`, `A=0.5`, `fs=44100`, `duration=1` produces 44,100 samples.
- The first sample is 0 at phase 0 and 0.5 at phase π/2.
- Peak does not exceed A; A=0 produces silence.
- Adding 2π to phase gives equivalent values; repeated calls return distinct arrays.
- Invalid inputs, including frequency at Nyquist, are rejected.
- Attenuation of `[2, -1, 0]` to peak 0.8 gives `[0.8, -0.4, 0]` and gain 0.4.
- Silence and already quiet buffers remain unchanged with gain 1.

Check without playback, then listen:

```bash
mvn -q compile exec:java -Dexec.args="--check1 11111111"
mvn -q compile exec:java -Dexec.args="--task1 11111111"
```

## Task 2 — Harmonics, mixing and attenuation

Build a signal by adding harmonics of the assigned fundamental frequency.
Implement buffer mixing, then compare the pure fundamental, the harmonic
signal and a mixture with a stronger fundamental.

Expected sound: a pure tone followed by a brighter, richer tone at the same
fundamental pitch, then a tone with a more prominent fundamental and a less
bright character than the harmonic signal.

Add your code in:

- `SignalGenerator.java`: `generateAdditive(fundamentalFrequency, harmonicAmplitudes, sampleRate, duration)`.
- `SignalMixer.java`: `mix(first, second)`.

For additive synthesis, sum phase-zero sine components at
`(i+1)*fundamentalFrequency`, with amplitude `harmonicAmplitudes[i]`.
Validate every component, leave the amplitude array unchanged and return the
unscaled sum. The assigned array contains five amplitudes, starting with the
fundamental. Reuse attenuation from Task 1.

`mix` returns a new element-wise sum of equal-length, non-empty finite buffers,
leaving both inputs unchanged. Verify that mixing with silence preserves
values and attenuation preserves component ratios.

The prepared runner plays three buffers, each attenuated separately to 0.8.
The harmonic signal already includes the fundamental. The third buffer adds
it again, doubling its coefficient before attenuation; listen for how this
changes the balance between the fundamental and higher harmonics.

Check without playback, then listen:

```bash
mvn -q compile exec:java -Dexec.args="--check2 11111111"
mvn -q compile exec:java -Dexec.args="--task2 11111111"
```

## Task 3 — End fades

Apply a gradual increase and decrease in amplitude at the ends of a signal.
Compare the assigned tone with and without these fades and listen for the
change in clicks at the buffer boundaries.

Expected sound: the faded tone should enter and leave smoothly, reducing
boundary clicks while preserving the pitch and the steady middle section.

Add your code in:

- `Envelope.java`: `applyFade(samples, sampleRate, fadeInSeconds, fadeOutSeconds)`.

Modify the buffer in place using linear ramps. Round fade durations to sample
counts and reject negative/non-finite durations and overlapping fades before
modifying any samples.

- A fade rounding to zero samples leaves that end unchanged.
- A fade rounding to one sample sets that endpoint to zero.
- Longer fades include both ramp endpoints, 0→1 or 1→0.

The prepared runner compares fresh copies of a phase-π/2 sine with and without
100 ms end fades. Verify zero endpoints, unchanged middle samples and unchanged
buffer length. Test zero, one-sample and overlapping fades too, using a buffer
of ones to inspect the ramps. Tasks 5 and 8 reuse this method with 10 ms fades.

Check without playback, then listen:

```bash
mvn -q compile exec:java -Dexec.args="--check3 11111111"
mvn -q compile exec:java -Dexec.args="--task3 11111111"
```

## Task 4 — JSyn

Generate and play the assigned sine and harmonic signal using JSyn oscillators.
Build the audio graph and manage its lifecycle instead of generating a sample
buffer for `AudioOutput`.

Expected sound: a steady pure tone followed by a richer harmonic tone,
similar in pitch and timbre to the corresponding sounds in Tasks 1 and 2,
although their loudness may differ.

Add your code in:

- `JSynGenerator.java`: `playJSynSine(TaskParameters)`.
- `JSynGenerator.java`: `playJSynAdditive(TaskParameters)`.

Preserve the prepared `protected static` declarations. The prepared
`Experiments.task4()` calls the sine method followed by the harmonic method.
JSyn `v17.2.0` is configured in `pom.xml`.

- Use `JSyn.createSynthesizer()`, `SineOscillator` and `LineOut`.
- Play the Task 1 sine at phase zero, then the Task 2 harmonic signal using
  assigned parameters. The extra fundamental-plus-harmonics mixture is not
  part of this experiment.
- Use one oscillator per harmonic and route the summed output to both channels.
- For harmonics, use shared gain `min(1, 0.8/sum(amplitudes))`; use gain 1 when
  the sum is zero. Preserve amplitude ratios.
- Start at the assigned 44,100 Hz and play for the assigned duration.
- Stop the synthesizer in `finally`, including after failure or interruption.
  Propagate `InterruptedException` after cleanup.

The conservative JSyn gain may differ from Task 2's sampled-peak gain;
bit-identical output is not required. Compare the sounds with Tasks 1 and 2.
Task 4 requires audio playback; `--check4` is not supported.

Run and listen:

```bash
mvn -q compile exec:java -Dexec.args="--task4 11111111"
```

## Task 5 — Beating

Combine two sine tones with nearby frequencies and observe the periodic
changes in loudness. Compare how the beating speeds up as the frequency
difference increases.

Expected sound: a tone whose loudness repeatedly swells and fades, with
faster pulsations in each successive comparison.

No new method is required. Reuse your implementations in:

- `SignalGenerator.java`: `generateSine(...)`.
- `SignalMixer.java`: `mix(...)` and `attenuateToPeak(...)`.
- `Envelope.java`: `applyFade(...)`.

The prepared `Experiments.task5()` runs the experiment; keep it unchanged.
It mixes phase-zero sines with amplitude 0.3, lasting four seconds. The first
frequency is your Task 1 frequency; the second adds your assigned difference,
then that difference plus 3 Hz and plus 6 Hz in the next two comparisons.

The runner applies 10 ms end fades and common attenuation. Compare the audible
amplitude changes with the printed beat frequency `abs(f2-f1)`.

Check without playback, then listen:

```bash
mvn -q compile exec:java -Dexec.args="--check5 11111111"
mvn -q compile exec:java -Dexec.args="--task5 11111111"
```

## Task 6 — White noise

Generate reproducible white noise with uniformly distributed sample values.
Measure its mean and RMS to compare the observed signal with the expected
statistical properties.

Expected sound: a continuous broadband hiss, similar to radio static,
without a clear musical pitch or regular pulsation.

Add your code in:

- `SignalGenerator.java`: `generateWhiteNoise(amplitude, sampleRate, duration, seed)`.
- `SignalAnalysis.java`: `mean(samples)` and `rms(samples)`.

Use `java.util.Random(seed)` to generate independent uniform samples in
`[-amplitude, amplitude)`. The runner uses amplitude 0.2, duration two seconds
and your assigned seed. Return a new buffer without normalization.

Mean is the arithmetic average; RMS is the square root of the mean squared
value. Both require a non-empty buffer of finite samples. Mean measures the
DC offset, while RMS measures effective amplitude. Silence also has mean zero,
so mean alone is insufficient. Neither measurement proves randomness or a
white spectrum.

Verify reproducibility, sample bounds and silence at amplitude zero. For a
long buffer, expect mean near zero and RMS near `A/sqrt(3)`. Do not force either
value or subtract the measured mean. The prepared runner prints both values;
verify reproducibility and bounds yourself.

Check without playback, then listen:

```bash
mvn -q compile exec:java -Dexec.args="--check6 11111111"
mvn -q compile exec:java -Dexec.args="--task6 11111111"
```

## Task 7 — Short percussive tones

Shape a short sine tone with a triangular amplitude envelope. Compare different
attack times to hear how the envelope changes the sharpness of the onset while
frequency and total tone duration stay fixed.

Expected sound: three groups of six short tonal taps separated by silence,
with a sharp onset for the shortest attack and progressively softer onsets
for the longer attacks.

Add your code in:

- `Envelope.java`: `applyPercussiveEnvelope(samples, sampleRate, attackSeconds)`.

Modify the buffer in place with a linear attack followed by a linear release,
with no sustain:

- Peak index: `Math.round(attackSeconds * sampleRate)`.
- Require `0 < peakIndex < N-1`.
- Envelope is 0 at index 0, 1 at the peak, and 0 at index `N-1`.
- Validate all inputs before modifying the buffer.

The runner uses your assigned click frequency, amplitude 0.3 and duration
60 ms. It compares attacks of 0.5, 5 and 15 ms. Each tone is attenuated before
being repeated six times with 440 ms silence after every repetition.

Verify envelope endpoints and slopes using a buffer of ones. Listen for the
change from a sharp to a softer onset. With total duration fixed, a longer
attack leaves a shorter release.

Check without playback, then listen:

```bash
mvn -q compile exec:java -Dexec.args="--check7 11111111"
mvn -q compile exec:java -Dexec.args="--task7 11111111"
```

## Task 8 — Wave packet

Generate a wave packet by averaging cosine components with nearby frequencies.
Align them at a chosen time and compare how increasing their frequency span
narrows the central packet.

Expected sound: three short tones around 440 Hz whose loudness swells near
the middle, with the main swell becoming briefer and more concentrated as
the number of components increases.

Add your code in:

- `SignalGenerator.java`: `generateWavePacket(centerFrequency, spacing, componentCount, centerTime, sampleRate, duration)`.

Use the following component frequencies and sample values:

```text
k = 0, ..., componentCount-1
middle = (componentCount-1)/2
fk = centerFrequency + (k - middle)*spacing
x[n] = sum_k cos(2*pi*fk*(n/(double)fs - centerTime)) / componentCount
```

Require an odd positive component count, finite positive spacing, center time
in `[0,duration)`, and valid component frequencies. Return raw samples without
fades or additional attenuation.

The runner uses center 440 Hz, spacing 2 Hz, counts 5/11/21, center time 0.2 s
and duration 0.4 s. Playback uses a faded, attenuated copy; retain the raw signal
for checking. Equally spaced frequencies produce recurring packets.

At `centerTime` all cosine components equal 1. If this time falls on a sample,
that sample should be approximately 1; for the prepared parameters it is
sample 8820. Inspect or plot the raw samples to compare packet widths, since
the printed peak alone does not show their temporal shape.

Check without playback, then listen:

```bash
mvn -q compile exec:java -Dexec.args="--check8 11111111"
mvn -q compile exec:java -Dexec.args="--task8 11111111"
```

## Submission

Submit one ZIP containing `pom.xml`, `README.md` and `src/`. Keep the provided
public signatures and parameter generator unchanged. All required tasks must
build with `mvn clean compile` and run with the commands above.

Exclude `target/`, IDE settings, compiled classes, audio files, plots and reports.
Only code is submitted. Validation includes other valid and invalid inputs,
not only your assigned parameters.

## References

- [Java Sound API](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/sound/sampled/package-summary.html)
- [JSyn project and examples](https://github.com/philburk/jsyn)
- [JSyn programming guide](https://www.softsynth.com/jsyn/docs/usersguide/)
