package com.isee.elfi;


import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

import java.io.ByteArrayOutputStream;

public class CameraCapture {

    private static FrameGrabber grabber;
    private static FFmpegFrameRecorder recorder;
    public static boolean isRecording = false;
    public static byte[] videoByte;

    public static void main(String[] args) {
        capture();
    }

    public static void capture() {
        isRecording = true;
        grabber = new OpenCVFrameGrabber(0);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        recorder = new FFmpegFrameRecorder(byteArrayOutputStream, 640, 480);
        try {
            System.out.println("Kamerayı başlatmaya çalışıyorum...");
            grabber.start();
            System.out.println("Kamera başlatıldı.");

            // Use ByteArrayOutputStream instead of file path
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1); // FLV codec (Flash Video)
            recorder.setFormat("flv"); // FLV format
            recorder.setFrameRate(30); // Frame rate 30 fps
            recorder.setVideoBitrate(400000); // Bitrate (for stable recording)

            recorder.start();
            System.out.println("FFmpegFrameRecorder başlatıldı.");

            while (isRecording) {
                Frame grabbedFrame = grabber.grab(); // Capture frame from camera
                if (grabbedFrame != null) {
                    recorder.record(grabbedFrame); // Write frame to the byte array
                    videoByte = byteArrayOutputStream.toByteArray();
                } else {
                    System.out.println("GrabbedFrame null, görüntü alınamadı.");
                    break; // Break if grabbedFrame is null
                }
            }

            // Use videoData as needed (e.g., send it over network, save to DB, etc.)
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
            System.out.println("Bir hata oluştu: " + e.getMessage());
        } finally {
            // Release resources
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


    public static void close() {
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
            isRecording = false; // Ensure recording is stopped
        } catch (FrameRecorder.Exception | FrameGrabber.Exception e) {
            e.printStackTrace();
            System.out.println("Kaynakları kapatma sırasında hata oluştu: " + e.getMessage());
        }
    }


}
