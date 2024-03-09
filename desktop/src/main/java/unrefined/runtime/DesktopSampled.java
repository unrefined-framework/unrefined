package unrefined.runtime;

import unrefined.internal.audio.JavaSoundAudioHandler;
import unrefined.media.sound.Sampled;

public class DesktopSampled extends Sampled {

    {
        audioHandlers().add(JavaSoundAudioHandler.INSTANCE);
    }

}
