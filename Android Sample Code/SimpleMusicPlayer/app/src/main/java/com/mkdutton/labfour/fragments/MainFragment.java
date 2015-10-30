package com.mkdutton.labfour.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mkdutton.labfour.MusicService;
import com.mkdutton.labfour.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Matt on 10/7/14.
 */
public class MainFragment extends Fragment implements
        ServiceConnection, View.OnClickListener, RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    public static final String TAG = "MAIN_FRAG";

    public static final String ACTION_UPDATE_DETAILS = "com.mkdutton.labfour.ACTION_UPDATE_DETAILS";
    public static final String EXTRA_TITLE_UPDATE = "com.mkdutton.labfour.EXTRA_TITLE_UPDATE";
    public static final String EXTRA_COVER_UPDATE = "com.mkdutton.labfour.EXTRA_COVER_UPDATE";
    public static final String EXTRA_ARTIST_UPDATE = "com.mkdutton.labfour.EXTRA_ARTIST_UPDATE";
    public static final String EXTRA_SONG_DURATION = "com.mkdutton.labfour.EXTRA_SONG_DURATION";

    MusicService mMusicService;
    Handler mMusicHandler;
    Runnable mMusicRunnable;

    boolean mIsBound;

    IntentFilter mFilter;

    boolean mNewSong = false;

    Intent musicIntent;

    RadioGroup group;

    SeekBar seekbar;



    public static MainFragment newInstance() {

        return new MainFragment();
    }

    public static MainFragment newInstance(Bundle _extras) {

        MainFragment frag = new MainFragment();

        frag.setArguments( _extras );

        return frag;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_main, container, false);



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mIsBound = false;

        Button button = (Button) getActivity().findViewById(R.id.rewindMedia);
        button.setOnClickListener(this);

        button = (Button) getActivity().findViewById(R.id.playMedia);
        button.setOnClickListener(this);

        button = (Button) getActivity().findViewById(R.id.pauseMedia);
        button.setOnClickListener(this);

        button = (Button) getActivity().findViewById(R.id.stopMedia);
        button.setOnClickListener(this);

        button = (Button) getActivity().findViewById(R.id.fastforwardMedia);
        button.setOnClickListener(this);

        group = (RadioGroup) getActivity().findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(this);

        seekbar = (SeekBar) getActivity().findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(this);



        musicIntent = new Intent(getActivity(), MusicService.class);



        ((RadioButton)getActivity().findViewById(R.id.shuffleModifier)).setChecked(true);



        mFilter = new IntentFilter();
        mFilter.addAction(ACTION_UPDATE_DETAILS);
        getActivity().registerReceiver(receiver, mFilter);



        if (savedInstanceState != null){

            Bundle args = savedInstanceState.getBundle("key");
            if (args != null) {
                Intent detailsIntent = new Intent(ACTION_UPDATE_DETAILS);
                detailsIntent.putExtras(args);
                getActivity().sendBroadcast(detailsIntent);
            }

        } else {


            Bundle args = getArguments();
            if (args != null) {

                Intent detailsIntent = new Intent(ACTION_UPDATE_DETAILS);
                detailsIntent.putExtras(args);
                getActivity().sendBroadcast(detailsIntent);


            }
        }

    }



    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(receiver, mFilter);

        if ( !mIsBound ) {
            getActivity().startService(musicIntent);
            getActivity().bindService(musicIntent, this, Context.BIND_AUTO_CREATE);
            setHandlerAndRunnable();
        }

    }


    @Override
    public void onPause() {
        super.onPause();


        mIsBound = false;
        getActivity().unbindService(this);

        mMusicHandler.removeCallbacks(mMusicRunnable);
        getActivity().unregisterReceiver(receiver);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();


        if ( !getActivity().isChangingConfigurations()){

            getActivity().stopService(musicIntent);
        }

    }

    private void setHandlerAndRunnable() {

        mMusicHandler = new Handler();

        mMusicRunnable = new Runnable() {

            int totalTime;
            int currentPos;


            @Override
            public void run() {
                if(mIsBound){
                    if (mMusicService.mPlayer.isPlaying()){

                        if (mNewSong) {
                            totalTime = mMusicService.mPlayer.getDuration();
                            seekbar.setMax( totalTime );

                            ((TextView)getActivity().findViewById(R.id.totalTime)).setText(  (new SimpleDateFormat("mm:ss")).format(new Date(totalTime))  );
                        }

                        currentPos = mMusicService.mPlayer.getCurrentPosition();
                        seekbar.setProgress(currentPos);


                        ((TextView)getActivity().findViewById(R.id.currentTime)).setText((new SimpleDateFormat("mm:ss")).format(new Date(currentPos))) ;

                    }

                } else {
                    Log.i(TAG, "Not Bound yet");
                }

                mMusicHandler.postDelayed(this, 250);
            }
        };
        mMusicHandler.postDelayed(mMusicRunnable, 250);
    }




    public void onServiceConnected(ComponentName name, IBinder service) {

        mIsBound = true;

        MusicService.MusicBinder binder = (MusicService.MusicBinder) service;

        mMusicService = binder.getService();


    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mIsBound = false;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.rewindMedia:
                mMusicService.musicControl(MusicService.REW);
                break;

            case R.id.playMedia:
                mMusicService.musicControl(MusicService.PLAY);
                break;

            case R.id.pauseMedia:
                mMusicService.musicControl(MusicService.PAUSE);
                break;

            case R.id.stopMedia:
                mMusicService.musicControl(MusicService.STOP);
                break;

            case R.id.fastforwardMedia:
                mMusicService.musicControl(MusicService.FF);
                break;

        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {

        if (mMusicService != null) {
            switch (id) {

                case R.id.repeatModifier:
                    mMusicService.selectedModifier(MusicService.REPEAT_MOD);
                    break;

                case R.id.shuffleModifier:
                    mMusicService.selectedModifier(MusicService.SHUFFLE_MOD);
                    break;

            }
        }

    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ACTION_UPDATE_DETAILS)) {

                mNewSong = true;

                String title = intent.getStringExtra(EXTRA_TITLE_UPDATE);
                String artist = intent.getStringExtra(EXTRA_ARTIST_UPDATE);
                int cover = intent.getIntExtra(EXTRA_COVER_UPDATE, 7);

                ((TextView)getActivity().findViewById(R.id.songTitle)).setText(title);
                ((TextView)getActivity().findViewById(R.id.songArtist)).setText(artist);
                ((ImageView)getActivity().findViewById(R.id.coverArt)).setImageResource( cover );
                (getActivity().findViewById(R.id.coverArt)).setTag( cover );


            }

        }
    };


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle extras = new Bundle();

        String title = ((TextView)getActivity().findViewById(R.id.songTitle)).getText().toString();
        String artist = ((TextView) getActivity().findViewById(R.id.songArtist)).getText().toString();
        int cover =   ((Integer) (getActivity().findViewById(R.id.coverArt)).getTag());

        extras.putString( EXTRA_TITLE_UPDATE, title ) ;
        extras.putString(EXTRA_ARTIST_UPDATE, artist );
        extras.putInt(EXTRA_COVER_UPDATE, cover );

        outState.putBundle("key", extras);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

        if (fromUser){
            mMusicService.userSeekTo( i );
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
