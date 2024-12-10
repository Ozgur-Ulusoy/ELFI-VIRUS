package com.isee.elfi;


import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

public class CameraCapture {

    public static void main(String[] args) {
        capture();
    }

    public static void capture() {
        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        FFmpegFrameRecorder recorder = null;

        try {
            System.out.println("Kamerayı başlatmaya çalışıyorum...");
            grabber.start();
            System.out.println("Kamera başlatıldı.");

            // Kaydedilecek video dosyasını belirleyin
            recorder = new FFmpegFrameRecorder(Constants.resourcesPath+"video.flv", 640, 480);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1); // FLV codec (Flash Video)
            recorder.setFormat("flv"); // FLV formatı
            // Format ayarı
            recorder.setFrameRate(30); // Kare hızı 30 fps
            recorder.setVideoBitrate(400000); // Bitrate ayarı (daha stabil kayıt için)

            recorder.start();
            System.out.println("FFmpegFrameRecorder başlatıldı.");

            while (true) {
                Frame grabbedFrame = grabber.grab(); // Kameradan görüntü al
                if (grabbedFrame != null) {
                    recorder.record(grabbedFrame); // Görüntüyü kaydet
                } else {
                    System.out.println("GrabbedFrame null, görüntü alınamadı.");
                    break; // Eğer grabbedFrame null ise döngüden çık
                }
            }
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
            System.out.println("Bir hata oluştu: " + e.getMessage());
        } finally {
            // Kaynakları serbest bırak
            try {
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                    System.out.println("Recorder durduruldu ve serbest bırakıldı.");
                }
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                    System.out.println("Grabber durduruldu ve serbest bırakıldı.");
                }
            } catch (FrameRecorder.Exception | FrameGrabber.Exception e) {
                e.printStackTrace();
                System.out.println("Kaynakları kapatma sırasında hata oluştu: " + e.getMessage());
            }
        }
    }
}
