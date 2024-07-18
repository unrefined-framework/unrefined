package unrefined.runtime;

import unrefined.desktop.audio.JavaSoundAudioHandler;
import unrefined.media.sound.Sampled;

public class DesktopSampled extends Sampled {

    {
        audioHandlers().add(JavaSoundAudioHandler.INSTANCE);
    }

}
