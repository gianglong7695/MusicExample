package com.dg.musicexample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.dg.musicexample.helper.TransactionHelperFragment;
import com.dg.musicexample.model.PlayList;
import com.dg.musicexample.model.Song;
import com.dg.musicexample.model.eventbus.PlayServiceInfo;
import com.dg.musicexample.player.PlaybackService;
import com.dg.musicexample.ui.MusicPlayerFragment;
import com.dg.musicexample.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TransactionHelperFragment transactionHelperFragment;
    private PlaybackService mPlaybackService;

    private String[] lstUrlMp3 = {
            "https://dl.dropboxusercontent.com/s/2zhi0tpnaf9pxkz/_7KQFxEyBmk.mp3",
            "https://dl.dropboxusercontent.com/s/cf408ih36uldrp4/_8o71Hlzonk.mp3",
            "https://dl.dropboxusercontent.com/s/kv88cvqn69mh2mt/_26FQfH8994.mp3",
            "https://dl.dropboxusercontent.com/s/4vprcuopxeuo5ab/_aGNa9N2q1c.mp3",
            "https://dl.dropboxusercontent.com/s/f9ywxhzqhf6va04/_BBvHRB5vQE.mp3",
            "https://dl.dropboxusercontent.com/s/2zhi0tpnaf9pxkz/_7KQFxEyBmk.mp3",
            "https://dl.dropboxusercontent.com/s/cf408ih36uldrp4/_8o71Hlzonk.mp3",
            "https://dl.dropboxusercontent.com/s/kv88cvqn69mh2mt/_26FQfH8994.mp3",
            "https://dl.dropboxusercontent.com/s/4vprcuopxeuo5ab/_aGNa9N2q1c.mp3",
            "https://dl.dropboxusercontent.com/s/f9ywxhzqhf6va04/_BBvHRB5vQE.mp3"
    };

    public PlayList playList;
    private List<Song> songList;


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mPlaybackService = ((PlaybackService.LocalBinder) service).getService();
//            mView.onPlaybackServiceBound(mPlaybackService);
//            mView.onSongUpdated(mPlaybackService.getPlayingSong());
            EventBus.getDefault().post(new PlayServiceInfo(true));
            Logs.e("onServiceConnected...");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mPlaybackService = null;
//            mView.onPlaybackServiceUnbound();


            Logs.e("onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }


    public void init() {
        getSupportActionBar().hide();
        bindService(new Intent(this, PlaybackService.class), mConnection, Context.BIND_AUTO_CREATE);


        setPlayList();

        transactionHelperFragment = new TransactionHelperFragment(this, R.id.frame_container);
        transactionHelperFragment.addFragment(new MusicPlayerFragment(), true, 1);
    }


    public void setPlayList() {
        songList = new ArrayList<>();

        for (int i = 0; i < lstUrlMp3.length; i++) {
            Song song = new Song();
            song.setId(i);
            song.setPath(lstUrlMp3[i]);
            song.setArtist("Artist name " + (i + 1));
            song.setDisplayName("Song name " + (i + 1));
            song.setTitle(song.getDisplayName() + " - " + song.getDisplayName());
            songList.add(song);
        }


        playList = new PlayList();
        playList.setSongs(songList);

    }

    public PlaybackService getPlaybackService() {
        return mPlaybackService;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
