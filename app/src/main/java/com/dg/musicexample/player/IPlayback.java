package com.dg.musicexample.player;

import com.dg.musicexample.model.PlayList;
import com.dg.musicexample.model.Song;


/**
 * Created by Giang Long on 11/10/2018.
 * Skype: gianglong7695@gmail.com (id: gianglong7695_1)
 * Phone: 0979 579 283
 */
public interface IPlayback {

    void setPlayList(PlayList list);

    boolean play();

    boolean play(PlayList list);

    boolean play(PlayList list, int startIndex);

    boolean play(Song song);

    boolean playLast();

    boolean playNext();

    boolean pause();

    boolean isPlaying();

    int getProgress();

    Song getPlayingSong();

    boolean seekTo(int progress);

    void setPlayMode(PlayMode playMode);

    void registerCallback(ICallback callback);

    void unregisterCallback(ICallback callback);

    void removeCallbacks();

    void releasePlayer();
}
