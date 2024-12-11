package com.isee.elfi;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// Ses Kaydını aldığımız ve kaydettiğimiz VoiceCapture classımız
public class VoiceCapture {

    // Ses verilerini okumak için TargetDataLine nesnesini tanımlıyoruz
    private TargetDataLine line;

    // Ses verilerini geçici olarak depolamak için bir ByteArrayOutputStream nesnesi tanımlıyoruz
    private ByteArrayOutputStream audioStream;

    // Ses formatını tanımlayan bir fonksiyon oluşturuyoruz
    public AudioFormat getAudioFormat() {
        float sampleRate = 16000; // Örnekleme oranı, sesin saniyede kaç örnekle kaydedileceğini belirtir (Hz)
        int sampleSizeInBits = 16; // Her bir örnek için kullanılan bit sayısı (bit derinliği)
        int channels = 1; // Kanal sayısı, 1 = Mono, 2 = Stereo
        boolean signed = true; // Verinin işaretli olup olmadığını belirtir
        boolean bigEndian = false; // Veri sıralamasının little-endian (ters) olup olmadığını belirtir

        // Belirtilen parametrelerle bir AudioFormat nesnesi döndürür
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    // Ses kaydını başlatan fonksiyonumuz
    public void startRecording() {
        AudioFormat format = getAudioFormat(); // Ses formatını alıyoruz
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // Hedef hattın bilgi nesnesini oluşturuyoruz

        try {
            line = (TargetDataLine) AudioSystem.getLine(info); // Uygun bir ses hattı alıyoruz
            line.open(format); // Hat üzerinde belirttiğimiz formatı açıyoruz
            line.start(); // Ses hattını başlatıyoruz

            audioStream = new ByteArrayOutputStream(); // Veriyi tutacak bir akış oluşturuyoruz

            // Ses verilerini okumak için bir thread başlatıyoruz
            Thread recordingThread = new Thread(() -> {
                byte[] buffer = new byte[1024]; // Veriyi geçici olarak depolamak için bir tampon oluşturuyoruz
                while (line.isOpen()) { // Hat açık olduğu sürece çalışır
                    int bytesRead = line.read(buffer, 0, buffer.length); // Hattan veriyi okuyoruz
                    audioStream.write(buffer, 0, bytesRead); // Veriyi akışa yazıyoruz
                }
            });

            recordingThread.start(); // threadimizi başlattık
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Ses kaydını durduran fonksiyonumuz
    public void stopRecording() {
        if (line != null) {
            // hattımız null değilse durdurup kapatıyoruz
            line.stop();
            line.close();
            System.out.println("Recording stopped.");
        }
    }

    // Ses verisini geçerli bir WAV dosyası olarak döndüren fonksiyonumuz
    public byte[] getAudioBytes() {
        if (audioStream == null) return null; // Eğer akış boşsa null döneriz

        byte[] audioData = audioStream.toByteArray(); // Akıştaki veriyi byte dizisine çeviriyoruz
        ByteArrayOutputStream wavStream = new ByteArrayOutputStream(); // WAV formatına uygun bir akış oluşturuyoruz

        try {
            // WAV başlığını ekliyoruz
            writeWavHeader(wavStream, audioData.length, getAudioFormat());
            wavStream.write(audioData); // Ses verisini akışa yazıyoruz
        } catch (IOException e) {
            e.printStackTrace();
        }

        // WAV formatındaki byte dizisini döndürüyoruz
        return wavStream.toByteArray();
    }

    // WAV başlığı yazan yardımcı bir fonksiyon oluşturduk
    private void writeWavHeader(ByteArrayOutputStream stream, int audioDataLength, AudioFormat format) throws IOException {
        int channels = format.getChannels(); // Kanal sayısını alıyoruz
        int sampleRate = (int) format.getSampleRate();  // Örnekleme oranını alıyoruz
        int byteRate = sampleRate * channels * (format.getSampleSizeInBits() / 8); // Byte hızını hesaplıyoruz

        stream.write("RIFF".getBytes()); // RIFF başlığını yazıyoruz
        stream.write(intToByteArray(36 + audioDataLength), 0, 4); // Chunk boyutunu yazıyoruz
        stream.write("WAVE".getBytes()); // WAVE formatını belirtiyoruz
        stream.write("fmt ".getBytes()); // Format başlığını ekliyoruz
        stream.write(intToByteArray(16), 0, 4); // Alt başlığın boyutunu yazıyoruz
        stream.write(shortToByteArray((short) 1), 0, 2);  // Ses formatını PCM olarak belirtiyoruz
        stream.write(shortToByteArray((short) channels), 0, 2); // Kanal sayısını yazıyoruz
        stream.write(intToByteArray(sampleRate), 0, 4); // Örnekleme oranını yazıyoruz
        stream.write(intToByteArray(byteRate), 0, 4); // Byte hızını yazıyoruz
        stream.write(shortToByteArray((short) (channels * format.getSampleSizeInBits() / 8)), 0, 2); // Blok hizasını yazıyoruz
        stream.write(shortToByteArray((short) format.getSampleSizeInBits()), 0, 2); // Örnek başına bit sayısını yazıyoruz
        stream.write("data".getBytes()); // Veri alt başlığını yazıyoruz
        stream.write(intToByteArray(audioDataLength), 0, 4); // Veri boyutunu yazıyoruz
    }

    // Tam sayı değerini byte dizisine çeviren yardımcı bir fonksiyon tanımlıyoruz
    private byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value),
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24)
        };
    }

    // Kısa (short) bir değeri byte dizisine çeviren yardımcı bir fonksiyon tanımlıyoruz
    private byte[] shortToByteArray(short value) {
        return new byte[]{
                (byte) (value),
                (byte) (value >> 8)
        };
    }
}
