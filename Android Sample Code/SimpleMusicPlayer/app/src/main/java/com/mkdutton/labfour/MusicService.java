package com.mkdutton.labfour;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.mkdutton.labfour.fragments.MainFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Matt on 10/7/14.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener{

    public static final String TAG = "MUSIC_SERVICE";

    public static final int NOTIF_ID = 0x01222;
    NotificationManager mNotifMGR;

    public static final String NEXT_TRACK = "com.mkdutton.labfour.NEXT_TRACK";
    public static final String PREV_TRACK = "com.mkdutton.labfour.PREV_TRACK";

    //constants for music controls
    public static final int REW = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int STOP = 3;
    public static final int FF = 4;

    public static final int SHUFFLE_MOD = 987654;
    public static final int REPEAT_MOD = 54321;

    // initial setting when creating the service is shuffle
    public int mSelectedModifier = SHUFFLE_MOD;

    public MediaPlayer mPlayer;

    boolean mIsPrepared;

    boolean mPlayWhenPrepared;

    String[] mSongResource;

    String[] mTitle;

    String[] mArtist;

    int[] mArtwork;

    int mSong = -1;

    ArrayList<Integer> mLastSong;


    public class MusicBinder extends Binder {

        public MusicService getService(){
            return MusicService.this;
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();

        mLastSong = new ArrayList<Integer>();

        mSongResource = new String[]{
                "android.resource://"+ getPackageName() + "/raw/duhast",
                "android.resource://"+ getPackageName() + "/raw/desire",
                "android.resource://"+ getPackageName() + "/raw/amishparadise",
                "android.resource://"+ getPackageName() + "/raw/carabella",
                "android.resource://"+ getPackageName() + "/raw/drunkatdennys",
                "android.resource://"+ getPackageName() + "/raw/fansong",
                "android.resource://"+ getPackageName() + "/raw/fusantaclause"
        };

        mArtist = new String[]{
                "Rammstein",
                "Techno Trance",
                "Weird Al Yankovic",
                "Techno Trance",
                "Monsters in the Morning",
                "Monsters in the Morning",
                "Monsters in the Morning",
        };

        mTitle = new String[]{
                "Du Hast",
                "Desire",
                "Amish Paradise",
                "Carabella",
                "Drunk At Denny's",
                "Fan Song",
                "F.U. Santa Clause"
        };

        mArtwork = new int[]{
                R.drawable.rammstein,
                R.drawable.techno,
                R.drawable.weirdal,
                R.drawable.techno,
                R.drawable.monsters,
                R.drawable.monsters,
                R.drawable.monsters,
                R.drawable.placeholder
        };

        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(NEXT_TRACK);
        notificationFilter.addAction(PREV_TRACK);
        registerReceiver(notificationReceiver, notificationFilter);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(mPlayer == null){

            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnSeekCompleteListener(this);

            try {
                mSong = pickNextSong();

                mPlayer.setDataSource(this,
                        Uri.parse(mSongResource[mSong]));

            } catch (IOException e) {
                e.printStackTrace();

                mPlayer.release();
                mPlayer = null;
            }

        }

        if(mPlayer != null && !mIsPrepared) {
            mPlayer.prepareAsync();
            mPlayWhenPrepared = true;
        }

        return START_STICKY;



    }

    private int pickNextSong() {

        return new Random().nextInt(  mSongResource.length  );

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPlayer != null) {

            mPlayer.release();
        }
        unregisterReceiver(notificationReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }



    public void musicControl(int _action){

        switch (_action){

            case REW:

                gotoPrevTrack();


                break;


            case PLAY:
                Log.i(TAG, "PLAY MUSIC");

                if(mPlayer != null && mIsPrepared) {
                    showNotification();
                    mPlayer.start();

                }

                break;


            case PAUSE:
                Log.i(TAG, "PAUSE MUSIC");

                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    stopForeground(true);
                }
                break;


            case STOP:
                Log.i(TAG, "STOP MUSIC");

                if (mPlayer != null && mPlayer.isPlaying()){

                    stopForeground(true);

                    mPlayer.stop();
                    mIsPrepared = false;

                    mPlayer.reset();
                    try {
                        mPlayer.setDataSource(this, Uri.parse(mSongResource[mSong]));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mPlayer.prepareAsync();
                    mPlayWhenPrepared = false;

                }

                break;


            case FF:

                Log.i(TAG, "FAST FORWARD MUSIC");

                stopAndSetToSong(mSong, true);


                break;

        }

    }

    public void selectedModifier(int _modifier){

        if(_modifier == SHUFFLE_MOD){
            mSelectedModifier = SHUFFLE_MOD;
            Log.i(TAG, "shuffle Radio");

        } else if(_modifier == REPEAT_MOD){
            mSelectedModifier = REPEAT_MOD;
            Log.i(TAG, "repeat Radio");

        }

    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        mIsPrepared = true;

        if ( mPlayWhenPrepared ) {
            showNotification();
            mPlayer.start();
        }

        // Broadcast to the receiver in the fragment to update the displayed details.
        Intent showDetailsBroadcast = new Intent(MainFragment.ACTION_UPDATE_DETAILS);
        showDetailsBroadcast.putExtra(MainFragment.EXTRA_TITLE_UPDATE,mTitle[mSong]);
        showDetailsBroadcast.putExtra(MainFragment.EXTRA_COVER_UPDATE, mArtwork[mSong]);
        showDetailsBroadcast.putExtra(MainFragment.EXTRA_ARTIST_UPDATE, mArtist[mSong]);
        sendBroadcast(showDetailsBroadcast);




    }


    private void showNotification(){

        mNotifMGR = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);

        // notification for the task bar
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_music);
        builder.setContentTitle(mArtist[mSong]);
        builder.setContentText(mTitle[mSong]);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), mArtwork[mSong]));
        builder.setAutoCancel(false);
        builder.setOngoing(true);

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle(mArtist[mSong]);
        style.setSummaryText(mTitle[mSong]);
        style.bigLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_music ));
        style.bigPicture(BitmapFactory.decodeResource(this.getResources(), mArtwork[mSong]));

        builder.setStyle(style);

        Intent skipIntent = new Intent(PREV_TRACK);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, skipIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(android.R.drawable.ic_media_rew, "Last Track", pIntent);

        skipIntent = new Intent(NEXT_TRACK);
        pIntent = PendingIntent.getBroadcast(this, 0, skipIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(android.R.drawable.ic_media_ff, "Next Track", pIntent);


        Intent clickIntent = new Intent(this, MainActivity.class);
        clickIntent.putExtra(MainFragment.EXTRA_TITLE_UPDATE,mTitle[mSong]);
        clickIntent.putExtra(MainFragment.EXTRA_COVER_UPDATE, mArtwork[mSong]);
        clickIntent.putExtra(MainFragment.EXTRA_ARTIST_UPDATE, mArtist[mSong]);
        clickIntent.putExtra(MainFragment.EXTRA_SONG_DURATION, mPlayer.getDuration());

        PendingIntent pendClickIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendClickIntent);



        Notification notification = builder.build();
        startForeground(NOTIF_ID, notification);

    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if (mPlayer != null) {

            stopAndSetToSong(mSong, true);


        }
    }


    public void stopAndSetToSong(int _song, boolean _playwhenpreped ){

        if (mPlayer != null) {


            stopForeground(true);

            mPlayer.stop();

            mIsPrepared = false;

            int next;
            if (mSelectedModifier == SHUFFLE_MOD) {
                next = pickNextSong();

            } else { // if its not shuffle its repeat, so play again.
                next = _song;
            }

            //add the current song to the array of previous songs
            mLastSong.add(_song);

            // set mSong to the new random number
            mSong = next;

            try {

                mPlayer.reset();

                mPlayer.setDataSource(this, Uri.parse(mSongResource[next]));

            } catch (IOException e) {
                e.printStackTrace();
            }

            mPlayer.prepareAsync();

            mPlayWhenPrepared = _playwhenpreped;

        }

    }


    public void userSeekTo(int _time){

        if (mPlayer != null && mIsPrepared ){

            mPlayer.pause();
            mPlayer.seekTo(  _time  );

        }

    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

        if (mediaPlayer != null && mIsPrepared ){

            mediaPlayer.start();

        }


    }


    public void gotoPrevTrack(){

        Log.i(TAG, "REWIND MUSIC");
        if (mPlayer != null && !mLastSong.isEmpty() ){

            mPlayer.stop();

            mIsPrepared = false;

            // set the mSong to the last song added in the []
            mSong = mLastSong.get( mLastSong.size()-1 );

            // remove the last index from the []
            mLastSong.remove( mLastSong.size()-1 );


            try {
                // rest the player back to an idle state
                mPlayer.reset();

                // reset the datasource to the mSong that was updated
                mPlayer.setDataSource(this, Uri.parse(mSongResource[mSong]));

            } catch (IOException e) {
                e.printStackTrace();
            }

            mPlayer.prepareAsync();

            mPlayWhenPrepared = true;

        } else { // if the player is null or the array is empty

            Toast.makeText(this, "No Previous Track Available", Toast.LENGTH_SHORT).show();

        }

    }

    BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(PREV_TRACK)){

                Log.i(TAG, "PREV TRACK BROADCAST RECEIVED");
                gotoPrevTrack();

            } else if (intent.getAction().equals(NEXT_TRACK)){

                Log.i(TAG, "NEXT TRACK BROADCAST RECEIVED");
                stopAndSetToSong(mSong, true);

            }

        }
    };



}
