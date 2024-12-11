package com.isee.elfi;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import java.io.ByteArrayOutputStream;

// Kullanıcının Video Kaydını kaydetmek için oluşturduğumuz class
public class CameraCapture {

    private static FrameGrabber grabber; // Kameramızdan gelecek canlı medya akışı
    private static FFmpegFrameRecorder recorder;

    // şuanda video kaydediliyor mu ? bilgisini tutan boolean değişkenimiz
    public static boolean isRecording = false;

    // kaydettiğimiz videoyu byte[] halinde tutan static byte[] türünden değişkenimiz
    public static byte[] videoByte;

    // Kullanıcının Kamerasından Video Yakalama işlemini başlatır
    public static void capture() {
        // şuanda kayıt edildiğini belirten değişkenimizi true yaparız
        isRecording = true;

        // Kameramızdan görüntü yakalamak için OpenCVFrameGrabber fonksiyonunu kullandık.
        // parametre olarak 0 verdik bu sayede varsayılan kamerayı aldık ve bu
        grabber = new OpenCVFrameGrabber(0);

        // burada Geçici bellek alanı oluşturduk Video kaydını bir dosyaya kaydetmek yerine bu geçici belleğe yazıyoruz.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // FFmpegFrameRecorder'i yakalanan görüntüleri video olarak kaydetmek için kullanıyoruz
        // kaydedilen video verileri byteArrayOutputStream değişkenimize yazılacak
        // 640 eninde 480 boyutunda olacak oluşturulacak olan videomuz
        recorder = new FFmpegFrameRecorder(byteArrayOutputStream, 640, 480);
        try {
            System.out.println("Kamerayı başlatmaya çalışıyorum...");
            // kameramızı başlatıyoruz
            grabber.start();
            System.out.println("Kamera başlatıldı.");

            // Videonun hangi codec ile kodlanacağını belirler.
            // Codec, video verilerinin sıkıştırılması ve saklanması için kullanılan bir yöntemdir.
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1); // FLV codec (Flash Video)

            // Video formatımız FLV
            recorder.setFormat("flv"); // FLV format

            // Videomuzun saniyede kaç frame den oluşacağını belirledik
            recorder.setFrameRate(30); // Frame rate 30 fps

            // Videomuzun bitrate'ini ayarladık ( bir saniyelik video için ayrılan veri miktarını )
            recorder.setVideoBitrate(400000); // Bitrate

            // Video kaydedilmesini başlatıyoruz
            recorder.start();
            System.out.println("FFmpegFrameRecorder başlatıldı.");

            // Video Kaydedilme İşlemi Devam Ederken Çalışır
            while (isRecording) {
                // Kameramızdan Frame'i yakalar
                Frame grabbedFrame = grabber.grab();

                // eğer yakalanan Frame boş değilse
                if (grabbedFrame != null) {
                    // bu frame'i byteArrayOutputStream'ize yazar
                    recorder.record(grabbedFrame);

                    // static videoByte değişkenimizi videomuzun o anki halinin byte[] formatında eşitler
                    // bu değişkeni başka sınıflarda kullanıcaz o yüzden burada eşitleme yaptık
                    // diğer türlü videomuzun son haline byte[] formatına ulaşamamıştık
                    videoByte = byteArrayOutputStream.toByteArray();
                } else {
                    System.out.println("GrabbedFrame null, görüntü alınamadı.");
                    break;
                }
            }

        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
            System.out.println("Bir hata oluştu: " + e.getMessage());
        } finally {
            // En sonunda kullandığımız kaynakları ( Kamera vb. ) serbest bırakıyoruz
            try {
                if (recorder != null) {
                    // video kaydını sonlandırıp serbest bıraktık
                    recorder.stop();
                    recorder.release();
                    System.out.println("Recorder durduruldu ve serbest bırakıldı.");
                }
                if (grabber != null) {
                    // kamerayı kapatıp serbest bıraktık
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


    // Video Kaydını Kapatan fonksiyonumuz
    public static void close() {
        try {
            if (recorder != null) {
                // video kaydını sonlandırıp serbest bıraktık
                recorder.stop();
                recorder.release();
                System.out.println("Recorder durduruldu ve serbest bırakıldı.");
            }
            if (grabber != null) {
                // kamerayı kapatıp serbest bıraktık
                grabber.stop();
                grabber.release();
                System.out.println("Grabber durduruldu ve serbest bırakıldı.");
            }
            // Video kaydı kapatıldığı için değişkenimizi false olarak atadık
            isRecording = false; // Ensure recording is stopped
        } catch (FrameRecorder.Exception | FrameGrabber.Exception e) {
            e.printStackTrace();
            System.out.println("Kaynakları kapatma sırasında hata oluştu: " + e.getMessage());
        }
    }


}
