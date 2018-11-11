package com.dg.musicexample.ui;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dg.musicexample.MainActivity;
import com.dg.musicexample.R;
import com.dg.musicexample.model.Song;
import com.dg.musicexample.model.eventbus.PlayServiceInfo;
import com.dg.musicexample.player.IPlayback;
import com.dg.musicexample.player.PlayMode;
import com.dg.musicexample.utils.Config;
import com.dg.musicexample.utils.Logs;
import com.dg.musicexample.utils.PreferenceUtil;
import com.dg.musicexample.utils.TimeUtils;
import com.dg.musicexample.widget.ShadowImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MusicPlayerFragment extends Fragment implements IPlayback.ICallback {

    @BindView(R.id.image_view_album)
    ShadowImageView imageViewAlbum;
    @BindView(R.id.text_view_name)
    TextView textViewName;
    @BindView(R.id.text_view_artist)
    TextView textViewArtist;
    @BindView(R.id.text_view_progress)
    TextView textViewProgress;
    @BindView(R.id.text_view_duration)
    TextView textViewDuration;
    @BindView(R.id.seek_bar)
    SeekBar seekBarProgress;

    @BindView(R.id.button_play_mode_toggle)
    ImageView buttonPlayModeToggle;
    @BindView(R.id.button_play_toggle)
    ImageView buttonPlayToggle;
    @BindView(R.id.button_favorite_toggle)
    ImageView buttonFavoriteToggle;

    private IPlayback iPlayer;
    private Handler mHandler = new Handler();



    public MusicPlayerFragment() {
        // Required empty public constructor
    }


    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) return;

            if (iPlayer.isPlaying()) {
                int progress = (int) (seekBarProgress.getMax() * ((float) iPlayer.getCurrentProgress() / (float) iPlayer.getDuration()));

                updateProgressTextWithDuration(iPlayer.getCurrentProgress());

                Logs.d(progress + "");

                if (progress >= 0 && progress <= seekBarProgress.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBarProgress.setProgress(progress, true);
                    } else {
                        seekBarProgress.setProgress(progress);
                    }
                    mHandler.postDelayed(this, Config.UPDATE_PROGRESS_INTERVAL);
                }
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }


    @SuppressLint("ObjectAnimatorBinding")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        updatePlayMode(PreferenceUtil.getPlayMode(getActivity()));

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateProgressTextWithProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(getDuration(seekBar.getProgress()));
                if (iPlayer.isPlaying()) {
                    mHandler.removeCallbacks(mProgressCallback);
                    mHandler.post(mProgressCallback);
                }
            }
        });

    }


    public void initData() {
        iPlayer.setPlayList(((MainActivity) getActivity()).playList);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.button_play_toggle)
    public void playToggle() {
        if (!iPlayer.isPlaying()) {
            iPlayer.play();
        }else{
            iPlayer.pause();
        }

    }

    @OnClick(R.id.button_play_next)
    public void nextSong() {
        iPlayer.playNext();
    }

    @OnClick(R.id.button_play_last)
    public void lastSong() {
        iPlayer.playLast();
    }

    @OnClick(R.id.button_play_mode_toggle)
    public void playMode() {
        PlayMode current = PreferenceUtil.getPlayMode(getActivity());
        PlayMode newMode = PlayMode.switchNextMode(current);
        PreferenceUtil.setPlayMode(getActivity(), newMode);
        updatePlayMode(newMode);
    }

    public void updateSongInterface(Song song) {
        textViewName.setText(song.getDisplayName());
        textViewArtist.setText(song.getArtist());
    }


    public void updatePlayMode(PlayMode playMode) {
        if (playMode == null) {
            playMode = PlayMode.getDefault();
        }
        switch (playMode) {
            case LIST:
                buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_list);
                break;
            case LOOP:
                buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_loop);
                break;
            case SHUFFLE:
                buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_shuffle);
                break;
            case SINGLE:
                buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_single);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceRegister(PlayServiceInfo event) {
        if (event.isConnected()) {
            iPlayer = ((MainActivity) getActivity()).getPlaybackService();
            iPlayer.registerCallback(this);

            initData();
        }

    }


    private void seekTo(int duration) {
        iPlayer.seekTo(duration);
    }


    private void updateProgressTextWithProgress(int progress) {
        int targetDuration = getDuration(progress);
        textViewProgress.setText(TimeUtils.formatDuration(targetDuration));
    }

    private void updateProgressTextWithDuration(int duration) {
        textViewProgress.setText(TimeUtils.formatDuration(duration));
    }

    private void updateTextDuration(int duration) {
        textViewDuration.setText(TimeUtils.formatDuration(duration));
    }

    private int getDuration(int progress) {
        return (int) (iPlayer.getCurrentProgress() * ((float) progress / seekBarProgress.getMax()));
    }

    private int getCurrentSongDuration() {
        Song currentSong = iPlayer.getPlayingSong();
        int duration = 0;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        }
        return duration;
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {

    }

    @Override
    public void onSwitchNext(@Nullable Song next) {

    }

    @Override
    public void onComplete(@Nullable Song next) {
        Toast.makeText(getActivity(), "onComplete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        if (isPlaying) {
           imageViewAlbum.startRotateAnimation();
            buttonPlayToggle.setImageResource(R.drawable.ic_pause);
        } else {
            imageViewAlbum.pauseRotateAnimation();
            buttonPlayToggle.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public void onPreparedPlayer() {
        if (iPlayer != null && iPlayer.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);


            updateTextDuration(iPlayer.getDuration());
        }
    }

    @Override
    public void onSongUpdated(Song song) {
        updateSongInterface(song);
    }

}
