package org.example.desktop;

import unrefined.io.asset.Asset;
import unrefined.media.sound.Music;
import unrefined.media.sound.Sound;
import unrefined.runtime.DesktopRuntime;

import java.io.IOException;

public class AudioPlayer {

    public static void main(String[] args) throws IOException, InterruptedException {
        DesktopRuntime.initialize(args);

        Music music = Music.read(new Asset("game_over.ogg"));
        Sound sound = Sound.read(new Asset("game_over.ogg"));
        music.prepare();
        long wait = music.getMillisecondLength() + 100;
        music.start();
        music.onStop().connect(stopEvent -> music.dispose());
        Thread.sleep(wait);
        sound.start();
        Thread.sleep(wait);
    }

}
