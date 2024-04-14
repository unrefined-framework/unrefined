package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.io.asset.Asset;
import unrefined.util.TextManager;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class I18N {

    public static void main(String[] args) throws IOException {
        Lifecycle.onMain(args);

        TextManager textManager = TextManager.defaultInstance();
        textManager.load(Locale.ENGLISH, new Asset("i18n.lang"));
        textManager.load(Locale.CHINA, new Asset("i18n_zh_CN.lang"));
        final String TAG = textManager.get(I18N.class, "tag");
        Log log = Log.defaultInstance();
        log.info(TAG, textManager.get(I18N.class, "HelloWorld"));
        Random random = ThreadLocalRandom.current();
        int a = random.nextInt();
        int b = random.nextInt();
        log.info(TAG, textManager.get(I18N.class, "plus", a, b, (long) a + (long) b));
    }

}
