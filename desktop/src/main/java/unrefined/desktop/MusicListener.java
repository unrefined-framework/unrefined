package unrefined.desktop;

import java.util.EventListener;

@FunctionalInterface
public interface MusicListener extends EventListener {

    void update(MusicEvent event);

}
