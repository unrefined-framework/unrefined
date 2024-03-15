package unrefined;

import unrefined.app.Logger;
import unrefined.app.Platform;
import unrefined.app.Runtime;
import unrefined.io.asset.AssetLoader;
import unrefined.io.console.Console;
import unrefined.math.Arithmetic;
import unrefined.media.graphics.Drawing;
import unrefined.media.sound.Sampled;
import unrefined.nio.Allocator;
import unrefined.util.Base64;
import unrefined.util.TextManager;
import unrefined.util.Threading;
import unrefined.util.concurrent.atomic.Atomic;
import unrefined.util.event.EventBus;
import unrefined.util.foreign.Foreign;
import unrefined.util.ref.Cleaner;
import unrefined.util.reflect.Reflection;
import unrefined.util.signal.Dispatcher;

public class This {

    public static class app {
        public static final Logger logger = Logger.defaultInstance();
        public static final Platform platform = Platform.getInstance();

        public static final Runtime runtime = Runtime.getInstance();
    }

    public static class io {
        public static final class asset {
            public static final AssetLoader assetLoader = AssetLoader.defaultInstance();
        }
        public static class console {
            public static final Console console = Console.getInstance();
        }
    }

    public static class math {
        public static final Arithmetic arithmetic = Arithmetic.getInstance();
    }

    public static class media {
        public static class graphics {
            public static final Drawing drawing = Drawing.getInstance();
        }
        public static class sound {
            public static final Sampled sampled = Sampled.getInstance();
        }
    }

    public static class nio {
        public static final Allocator allocator = Allocator.getInstance();
    }

    public static class util {
        public static final TextManager textManager = TextManager.defaultInstance();

        public static final Base64 base64 = Base64.getInstance();
        public static final Threading threading = Threading.getInstance();
        public static class concurrent {
            public static class atomic {
                public static final Atomic atomic = Atomic.getInstance();
            }
        }
        public static class event {
            public static final EventBus eventBus = EventBus.defaultInstance();
        }
        public static class foreign {
            public static final Foreign foreign = Foreign.getInstance();
        }
        public static class ref {
            public static final Cleaner cleaner = Cleaner.getInstance();
        }
        public static class reflect {
            public static final Reflection reflection = Reflection.getInstance();
        }
        public static class signal {
            public static final Dispatcher dispatcher = Dispatcher.defaultInstance();
        }
    }

}
