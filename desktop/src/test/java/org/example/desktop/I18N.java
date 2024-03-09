package org.example.desktop;

import unrefined.app.Logger;
import unrefined.io.asset.Asset;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.TextManager;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class I18N {

    public static void main(String[] args) throws IOException {
        DesktopRuntime.initialize(args);

        TextManager textManager = TextManager.defaultInstance();
        textManager.load(Locale.ENGLISH, new Asset("i18n.lang"));
        textManager.load(Locale.CHINA, new Asset("i18n_zh_CN.lang"));
        final String TAG = textManager.get(I18N.class, "tag");
        Logger logger = Logger.defaultInstance();
        logger.info(TAG, textManager.get(I18N.class, "HelloWorld"));
        Random random = ThreadLocalRandom.current();
        int a = random.nextInt();
        int b = random.nextInt();
        logger.info(TAG, textManager.get(I18N.class, "plus", a, b, (long) a + (long) b));
    }

}
