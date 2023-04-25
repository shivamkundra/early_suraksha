package com.example.earlysuraksha;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

//import com.example.earlysuraksha.Location.WorkerUtils;
import com.example.earlysuraksha.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UploadWorker extends Worker {
    Context context;
    boolean flag=false;
    Ringtone ringtone;
    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
    }

    boolean getstatus;
    MediaPlayer mediaPlayer;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {
    
        Executor e = Executors.newSingleThreadExecutor();
        e.execute( ()->{
            Log.d("highpitchresponse", "run: " + getstatus);
            getstatus=MainActivity.getstatus(context);
            if (!getstatus) {
                //notification play krni hai

                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alert == null){
                    // alert is null, using backup
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    if (alert == null){
                        // alert backup is null, using 2nd backup
                        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    }
                }
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
                ringtone.setStreamType(AudioManager.STREAM_ALARM);

                Log.d("highpitchresponse", "doWork: " + "mai idhar aa chuka hu ");
                mediaPlayer = MediaPlayer.create(context, R.raw.highpitch);
                mediaPlayer.start();




                while (mediaPlayer.isPlaying()) {
                    flag=false;

                }
                flag=true;
            }
                }
        );


        if (flag){
            return  Result.success();
        }else return Result.failure();
    }
}
