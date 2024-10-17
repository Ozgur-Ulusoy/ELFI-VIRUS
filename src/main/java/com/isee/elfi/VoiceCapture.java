package com.isee.elfi;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class VoiceCapture{

    public AudioFormat getAudioFormat() {
        float sampleRate = 16000; // Sample rate in Hz
        int sampleSizeInBits = 16; // Sample size (bit depth)
        int channels = 1; // Mono channel
        boolean signed = true; // Signed data
        boolean bigEndian = false; // Little endian

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    private TargetDataLine line;

    // Method to start recording
    public void startRecording(String outputFilePath) {
        AudioFormat format = getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        try {
            // Open and start capturing audio
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            // Create a thread to capture audio in the background
            Thread recordingThread = new Thread(() -> {
                AudioInputStream ais = new AudioInputStream(line);
                File outputFile = new File(outputFilePath);

                try {
                    // Save the audio as a WAV file
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            recordingThread.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (line != null) {
            line.stop();
            line.close();
            System.out.println("Recording stopped.");
        }
    }
}
