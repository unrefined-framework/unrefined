package unrefined.desktop;

import java.util.EventListener;

@FunctionalInterface
public interface SoundListener extends EventListener {

    void update(SoundEvent event);

}
