package com.isee.elfi;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VoiceCapture {

    private TargetDataLine line;
    private ByteArrayOutputStream audioStream;

    public AudioFormat getAudioFormat() {
        float sampleRate = 16000; // Sample rate in Hz
        int sampleSizeInBits = 16; // Sample size (bit depth)
        int channels = 1; // Mono channel
        boolean signed = true; // Signed data
        boolean bigEndian = false; // Little endian

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    // Method to start recording
    public void startRecording() {
        AudioFormat format = getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            audioStream = new ByteArrayOutputStream();

            Thread recordingThread = new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (line.isOpen()) {
                    int bytesRead = line.read(buffer, 0, buffer.length);
                    audioStream.write(buffer, 0, bytesRead);
                }
            });

            recordingThread.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to stop recording
    public void stopRecording() {
        if (line != null) {
            line.stop();
            line.close();
            System.out.println("Recording stopped.");
        }
    }

    // Method to get audio bytes as a valid WAV file
    public byte[] getAudioBytes() {
        if (audioStream == null) return null;

        byte[] audioData = audioStream.toByteArray();
        ByteArrayOutputStream wavStream = new ByteArrayOutputStream();

        try {
            // Add WAV header
            writeWavHeader(wavStream, audioData.length, getAudioFormat());
            wavStream.write(audioData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wavStream.toByteArray();
    }

    // Method to write a WAV header
    private void writeWavHeader(ByteArrayOutputStream stream, int audioDataLength, AudioFormat format) throws IOException {
        int channels = format.getChannels();
        int sampleRate = (int) format.getSampleRate();
        int byteRate = sampleRate * channels * (format.getSampleSizeInBits() / 8);

        stream.write("RIFF".getBytes());
        stream.write(intToByteArray(36 + audioDataLength), 0, 4); // Chunk size
        stream.write("WAVE".getBytes());
        stream.write("fmt ".getBytes());
        stream.write(intToByteArray(16), 0, 4); // Subchunk1 size
        stream.write(shortToByteArray((short) 1), 0, 2); // Audio format (1 = PCM)
        stream.write(shortToByteArray((short) channels), 0, 2);
        stream.write(intToByteArray(sampleRate), 0, 4);
        stream.write(intToByteArray(byteRate), 0, 4);
        stream.write(shortToByteArray((short) (channels * format.getSampleSizeInBits() / 8)), 0, 2); // Block align
        stream.write(shortToByteArray((short) format.getSampleSizeInBits()), 0, 2); // Bits per sample
        stream.write("data".getBytes());
        stream.write(intToByteArray(audioDataLength), 0, 4); // Subchunk2 size
    }

    private byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value),
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24)
        };
    }

    private byte[] shortToByteArray(short value) {
        return new byte[]{
                (byte) (value),
                (byte) (value >> 8)
        };
    }
}
