package unrefined.media.graphics;

import unrefined.util.NotInstantiableError;

/**
 * <p>
 * Java order (always Big-endian): <br>
 * Lowest -> Highest<br>
 * B (8) -> G (8) -> R (8) -> A (8)
 * </p>
 * Corresponding native order: <br>
 * Big-endian: BGRA8888<br>
 * Little-endian: ARGB8888
 *
 * <p>AWT always using BGRA8888 since Java is Big-endian;
 * Android always using ARGB8888 on native layer as if Little-endian,
 * so corresponding Java layer color model is BGRA8888.</p>
 */
public final class Color {

    private Color() {
        throw new NotInstantiableError(Color.class);
    }

    public static final int TRANSPARENT             = 0x00000000;

    public static final int ALICE_BLUE  	        = 0xFFF0F8FF;
    public static final int ANTIQUE_WHITE  	        = 0xFFFAEBD7;
    public static final int AQUA  	                = 0xFF00FFFF;
    public static final int AQUAMARINE  	        = 0xFF7FFFD4;
    public static final int AZURE       	        = 0xFFF0FFFF;
    public static final int BEIGE       	        = 0xFFF5F5DC;
    public static final int BISQUE  	            = 0xFFFFE4C4;
    public static final int BLACK       	        = 0xFF000000;
    public static final int BLANCHED_ALMOND         = 0xFFFFEBCD;
    public static final int BLUE        	        = 0xFF0000FF;
    public static final int BLUE_VIOLET  	        = 0xFF8A2BE2;
    public static final int BROWN       	        = 0xFFA52A2A;
    public static final int BURLY_WOOD  	        = 0xFFDEB887;
    public static final int CADET_BLUE  	        = 0xFF5F9EA0;
    public static final int CHARTREUSE  	        = 0xFF7FFF00;
    public static final int CHOCOLATE  	            = 0xFFD2691E;
    public static final int CORAL       	        = 0xFFFF7F50;
    public static final int CORNFLOWER_BLUE         = 0xFF6495ED;
    public static final int CORNSILK  	            = 0xFFFFF8DC;
    public static final int CRIMSON  	            = 0xFFDC143C;
    public static final int CYAN  	                = 0xFF00FFFF;
    public static final int DARK_BLUE  	            = 0xFF00008B;
    public static final int DARK_CYAN  	            = 0xFF008B8B;
    public static final int DARK_GOLDEN_ROD         = 0xFFB8860B;
    public static final int DARK_GRAY  	            = 0xFFA9A9A9;
    public static final int DARK_GREEN  	        = 0xFF006400;
    public static final int DARK_KHAKI  	        = 0xFFBDB76B;
    public static final int DARK_MAGENTA  	        = 0xFF8B008B;
    public static final int DARK_OLIVE_GREEN        = 0xFF556B2F;
    public static final int DARK_ORANGE  	        = 0xFFFF8C00;
    public static final int DARK_ORCHID  	        = 0xFF9932CC;
    public static final int DARK_RED  	            = 0xFF8B0000;
    public static final int DARK_SALMON  	        = 0xFFE9967A;
    public static final int DARK_SEA_GREEN          = 0xFF8FBC8F;
    public static final int DARK_SLATE_BLUE         = 0xFF483D8B;
    public static final int DARK_SLATE_GRAY         = 0xFF2F4F4F;
    public static final int DARK_TURQUOISE          = 0xFF00CED1;
    public static final int DARK_VIOLET  	        = 0xFF9400D3;
    public static final int DEEP_PINK  	            = 0xFFFF1493;
    public static final int DEEP_SKY_BLUE  	        = 0xFF00BFFF;
    public static final int DIM_GRAY  	            = 0xFF696969;
    public static final int DODGER_BLUE  	        = 0xFF1E90FF;
    public static final int FIRE_BRICK  	        = 0xFFB22222;
    public static final int FLORAL_WHITE  	        = 0xFFFFFAF0;
    public static final int FOREST_GREEN  	        = 0xFF228B22;
    public static final int FUCHSIA  	            = 0xFFFF00FF;
    public static final int GAINSBORO  	            = 0xFFDCDCDC;
    public static final int GHOST_WHITE  	        = 0xFFF8F8FF;
    public static final int GOLD         	        = 0xFFFFD700;
    public static final int GOLDEN_ROD  	        = 0xFFDAA520;
    public static final int GRAY  	                = 0xFF808080;
    public static final int GREEN  	                = 0xFF008000;
    public static final int GREEN_YELLOW  	        = 0xFFADFF2F;
    public static final int HONEY_DEW  	            = 0xFFF0FFF0;
    public static final int HOT_PINK  	            = 0xFFFF69B4;
    public static final int INDIAN_RED   	        = 0xFFCD5C5C;
    public static final int INDIGO   	            = 0xFF4B0082;
    public static final int IVORY  	                = 0xFFFFFFF0;
    public static final int KHAKI       	        = 0xFFF0E68C;
    public static final int LAVENDER  	            = 0xFFE6E6FA;
    public static final int LAVENDER_BLUSH          = 0xFFFFF0F5;
    public static final int LAWN_GREEN  	        = 0xFF7CFC00;
    public static final int LEMON_CHIFFON  	        = 0xFFFFFACD;
    public static final int LIGHT_BLUE  	        = 0xFFADD8E6;
    public static final int LIGHT_CORAL  	        = 0xFFF08080;
    public static final int LIGHT_CYAN  	        = 0xFFE0FFFF;
    public static final int LIGHT_GOLDEN_ROD_YELLOW = 0xFFFAFAD2;
    public static final int LIGHT_GRAY  	        = 0xFFD3D3D3;
    public static final int LIGHT_GREEN  	        = 0xFF90EE90;
    public static final int LIGHT_PINK  	        = 0xFFFFB6C1;
    public static final int LIGHT_SALMON  	        = 0xFFFFA07A;
    public static final int LIGHT_SEA_GREEN         = 0xFF20B2AA;
    public static final int LIGHT_SKY_BLUE          = 0xFF87CEFA;
    public static final int LIGHT_SLATE_GRAY        = 0xFF778899;
    public static final int LIGHT_STEEL_BLUE        = 0xFFB0C4DE;
    public static final int LIGHT_YELLOW  	        = 0xFFFFFFE0;
    public static final int LIME  	                = 0xFF00FF00;
    public static final int LIME_GREEN  	        = 0xFF32CD32;
    public static final int LINEN  	                = 0xFFFAF0E6;
    public static final int MAGENTA  	            = 0xFFFF00FF;
    public static final int MAROON  	            = 0xFF800000;
    public static final int MEDIUM_AQUA_MARINE  	= 0xFF66CDAA;
    public static final int MEDIUM_BLUE  	        = 0xFF0000CD;
    public static final int MEDIUM_ORCHID  	        = 0xFFBA55D3;
    public static final int MEDIUM_PURPLE  	        = 0xFF9370DB;
    public static final int MEDIUM_SEA_GREEN        = 0xFF3CB371;
    public static final int MEDIUM_SLATE_BLUE  	    = 0xFF7B68EE;
    public static final int MEDIUM_SPRING_GREEN     = 0xFF00FA9A;
    public static final int MEDIUM_TURQUOISE  	    = 0xFF48D1CC;
    public static final int MEDIUM_VIOLET_RED  	    = 0xFFC71585;
    public static final int MIDNIGHT_BLUE           = 0xFF191970;
    public static final int MINT_CREAM              = 0xFFF5FFFA;
    public static final int MISTY_ROSE              = 0xFFFFE4E1;
    public static final int MOCCASIN  	            = 0xFFFFE4B5;
    public static final int NAVAJO_WHITE            = 0xFFFFDEAD;
    public static final int NAVY  	                = 0xFF000080;
    public static final int OLD_LACE  	            = 0xFFFDF5E6;
    public static final int OLIVE  	                = 0xFF808000;
    public static final int OLIVE_DRAB  	        = 0xFF6B8E23;
    public static final int ORANGE  	            = 0xFFFFA500;
    public static final int ORANGE_RED  	        = 0xFFFF4500;
    public static final int ORCHID              	= 0xFFDA70D6;
    public static final int PALE_GOLDEN_ROD      	= 0xFFEEE8AA;
    public static final int PALE_GREEN  	        = 0xFF98FB98;
    public static final int PALE_TURQUOISE      	= 0xFFAFEEEE;
    public static final int PALE_VIOLET_RED  	    = 0xFFDB7093;
    public static final int PAPAYA_WHIP  	        = 0xFFFFEFD5;
    public static final int PEACH_PUFF  	        = 0xFFFFDAB9;
    public static final int PERU  	                = 0xFFCD853F;
    public static final int PINK                	= 0xFFFFC0CB;
    public static final int PLUM                	= 0xFFDDA0DD;
    public static final int POWDER_BLUE          	= 0xFFB0E0E6;
    public static final int PURPLE  	            = 0xFF800080;
    public static final int RED  	                = 0xFFFF0000;
    public static final int ROSY_BROWN  	        = 0xFFBC8F8F;
    public static final int ROYAL_BLUE  	        = 0xFF4169E1;
    public static final int SADDLE_BROWN  	        = 0xFF8B4513;
    public static final int SALMON  	            = 0xFFFA8072;
    public static final int SANDY_BROWN         	= 0xFFF4A460;
    public static final int SEA_GREEN               = 0xFF2E8B57;
    public static final int SEA_SHELL               = 0xFFFFF5EE;
    public static final int SIENNA                  = 0xFFA0522D;
    public static final int SILVER                  = 0xFFC0C0C0;
    public static final int SKY_BLUE                = 0xFF87CEEB;
    public static final int SLATE_BLUE          	= 0xFF6A5ACD;
    public static final int SLATE_GRAY  	        = 0xFF708090;
    public static final int SNOW                	= 0xFFFFFAFA;
    public static final int SPRING_GREEN           	= 0xFF00FF7F;
    public static final int STEEL_BLUE          	= 0xFF4682B4;
    public static final int TAN  	                = 0xFFD2B48C;
    public static final int TEAL                	= 0xFF008080;
    public static final int THISTLE                	= 0xFFD8BFD8;
    public static final int TOMATO              	= 0xFFFF6347;
    public static final int TURQUOISE            	= 0xFF40E0D0;
    public static final int VIOLET  	            = 0xFFEE82EE;
    public static final int WHEAT               	= 0xFFF5DEB3;
    public static final int WHITE               	= 0xFFFFFFFF;
    public static final int WHITE_SMOKE          	= 0xFFF5F5F5;
    public static final int YELLOW  	            = 0xFFFFFF00;
    public static final int YELLOW_GREEN        	= 0xFF9ACD32;

    public static int rgba(int r, int g, int b, float a) {
        return argb((int)(a * 255 + 0.5f), r, g, b);
    }

    public static int argb(int a, int r, int g, int b) {
        return ((0xFF & a) << 24) | ((0xFF & r) << 16) | ((0xFF & g) << 8) | ((0xFF & b) << 0);
    }

    public static int bgra(int b, int g, int r, int a) {
        return argb(a, r, g, b);
    }

    public static int rgb(int r, int g, int b) {
        return argb(255, r, g, b);
    }

    public static int bgr(int b, int g, int r) {
        return rgb(r, g, b);
    }

    public static int argb(float a, float r, float g, float b) {
        return argb((int)(a * 255 + 0.5f), (int)(r * 255 + 0.5f), (int)(g * 255 + 0.5f), (int)(b * 255 + 0.5f));
    }

    public static int bgra(float b, float g, float r, float a) {
        return argb(a, r, g, b);
    }

    public static int rgb(float r, float g, float b) {
        return argb(1, r, g, b);
    }

    public static int bgr(float b, float g, float r) {
        return rgb(r, g, b);
    }

    public static int brighter(int color, float factor) {
        int a = alpha(color);
        int r = red(color);
        int g = green(color);
        int b = blue(color);

        int i = (int) (1 / (1 - factor));
        if (r == 0 && g == 0 && b == 0) return argb(a, i, i, i);
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return argb(a, (int) Math.min(r / factor, 255), (int) Math.min(g / factor, 255), (int) Math.min(b / factor, 255));
    }

    public static void brighter(int r, int g, int b, float factor, int[] rgb, int offset) {
        int i = (int) (1 / (1 - factor));
        if (r == 0 && g == 0 && b == 0) rgb[offset] = rgb[offset + 1] = rgb[offset + 2] = i;
        else {
            if (r > 0 && r < i) r = i;
            if (g > 0 && g < i) g = i;
            if (b > 0 && b < i) b = i;

            rgb[offset] = r;
            rgb[offset + 1] = g;
            rgb[offset + 2] = b;
        }
    }

    public static void brighter(int r, int g, int b, float factor, int[] rgb) {
        brighter(r, g, b, factor, rgb, 0);
    }

    public static int darker(int color, float factor) {
        return argb(alpha(color),
                (int) Math.max(red(color) * factor, 0),
                (int) Math.max(green(color) * factor, 0),
                (int) Math.max(blue(color) * factor, 0));
    }

    public static void darker(int r, int g, int b, float factor, int[] rgb, int offset) {
        rgb[offset] = (int) Math.max(r * factor, 0);
        rgb[offset + 1] = (int) Math.max(g * factor, 0);
        rgb[offset + 2] = (int) Math.max(b * factor, 0);
    }

    public static void darker(int r, int g, int b, float factor, int[] rgb) {
        darker(r, g, b, factor, rgb, 0);
    }

    public static int alpha(int color) {
        return 0xFF & (color >> 24);
    }

    public static int red(int color) {
        return 0xFF & (color >> 16);
    }

    public static int green(int color) {
        return 0xFF & (color >> 8);
    }

    public static int blue(int color) {
        return 0xFF & color;
    }

    public static void hsl(float h, float s, float l, int[] rgb, int offset) {

        if (h < 0) h = 0.0f;
        else if (h > 1.0f) h = 1.0f;
        if (s < 0) s = 0.0f;
        else if (s > 1.0f) s = 1.0f;
        if (l < 0) l = 0.0f;
        else if (l > 1.0f) l = 1.0f;

        int r, g, b;

        if (s - 0.01f <= 0.0f) {
            r = (int) (l * 255.0f);
            g = (int) (l * 255.0f);
            b = (int) (l * 255.0f);
        } else {
            float var_1, var_2;
            if (l < 0.5f) {
                var_2 = l * (1 + s);
            } else {
                var_2 = (l + s) - (s * l);
            }
            var_1 = 2 * l - var_2;

            r = (int) (255.0f * hue2rgb(var_1, var_2, h + (1.0f / 3.0f)));
            g = (int) (255.0f * hue2rgb(var_1, var_2, h));
            b = (int) (255.0f * hue2rgb(var_1, var_2, h - (1.0f / 3.0f)));
        }

        rgb[offset] = r;
        rgb[offset + 1] = g;
        rgb[offset + 2] = b;
    }

    public static void hsl(float h, float s, float l, int[] rgb) {
        hsl(h, s, l, rgb, 0);
    }

    public static int hsl(float h, float s, float l) {

        if (h < 0) h = 0.0f;
        else if (h > 1.0f) h = 1.0f;
        if (s < 0) s = 0.0f;
        else if (s > 1.0f) s = 1.0f;
        if (l < 0) l = 0.0f;
        else if (l > 1.0f) l = 1.0f;

        int r, g, b;

        if (s - 0.01f <= 0.0f) {
            r = (int) (l * 255.0f);
            g = (int) (l * 255.0f);
            b = (int) (l * 255.0f);
        } else {
            float var_1, var_2;
            if (l < 0.5f) {
                var_2 = l * (1 + s);
            } else {
                var_2 = (l + s) - (s * l);
            }
            var_1 = 2 * l - var_2;

            r = (int) (255.0f * hue2rgb(var_1, var_2, h + (1.0f / 3.0f)));
            g = (int) (255.0f * hue2rgb(var_1, var_2, h));
            b = (int) (255.0f * hue2rgb(var_1, var_2, h - (1.0f / 3.0f)));
        }

        return rgb(r, g, b);
    }

    private static float hue2rgb(float v1, float v2, float vH) {
        if (vH < 0.0f) vH += 1.0f;
        if (vH > 1.0f) vH -= 1.0f;
        if ((6.0f * vH) < 1.0f) return v1 + (v2 - v1) * 6.0f * vH;
        if ((2.0f * vH) < 1.0f) return v2;
        if ((3.0f * vH) < 2.0f) return v1 + (v2 - v1) * ((2.0f / 3.0f) - vH) * 6.0f;
        return v1;
    }

    public static float hue(int r, int g, int b) {

        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;

        float var_R = (r / 255f);
        float var_G = (g / 255f);
        float var_B = (b / 255f);

        float var_Min;
        float var_Max;
        float del_Max;

        if (var_R > var_G) {
            var_Min = var_G;
            var_Max = var_R;
        }
        else {
            var_Min = var_R;
            var_Max = var_G;
        }
        if (var_B > var_Max) var_Max = var_B;
        if (var_B < var_Min) var_Min = var_B;

        del_Max = var_Max - var_Min;

        float h;

        if (del_Max - 0.01f <= 0.0f) h = 0;
        else {

            float del_R = (((var_Max - var_R) / 6f) + (del_Max / 2f)) / del_Max;
            float del_G = (((var_Max - var_G) / 6f) + (del_Max / 2f)) / del_Max;
            float del_B = (((var_Max - var_B) / 6f) + (del_Max / 2f)) / del_Max;

            if (var_R == var_Max) h = del_B - del_G;
            else if (var_G == var_Max) h = (1 / 3f) + del_R - del_B;
            else h = (2 / 3f) + del_G - del_R;
            if (h < 0) h += 1;
            if (h > 1) h -= 1;
        }

        return h;
    }

    public static float saturation(int r, int g, int b) {

        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;

        float var_R = (r / 255f);
        float var_G = (g / 255f);
        float var_B = (b / 255f);

        float var_Min;
        float var_Max;
        float del_Max;

        if (var_R > var_G) {
            var_Min = var_G;
            var_Max = var_R;
        }
        else {
            var_Min = var_R;
            var_Max = var_G;
        }
        if (var_B > var_Max) var_Max = var_B;
        if (var_B < var_Min) var_Min = var_B;

        del_Max = var_Max - var_Min;

        float s, l;
        l = (var_Max + var_Min) / 2f;

        if (del_Max - 0.01f <= 0.0f) s = 0;
        else {
            if (l < 0.5f) s = del_Max / (var_Max + var_Min);
            else s = del_Max / (2 - var_Max - var_Min);
        }

        return s;
    }

    public static float luminance(int r, int g, int b) {

        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;

        float var_R = (r / 255f);
        float var_G = (g / 255f);
        float var_B = (b / 255f);

        float var_Min;
        float var_Max;

        if (var_R > var_G) {
            var_Min = var_G;
            var_Max = var_R;
        }
        else {
            var_Min = var_R;
            var_Max = var_G;
        }
        if (var_B > var_Max) var_Max = var_B;
        if (var_B < var_Min) var_Min = var_B;

        float l;
        l = (var_Max + var_Min) / 2f;

        return l;
    }

    public static void hsl(int color, float[] hsl, int offset) {
        hsl(red(color), green(color), blue(color), hsl, offset);
    }

    public static void hsl(int color, float[] hsl) {
        hsl(red(color), green(color), blue(color), hsl, 0);
    }

    public static void hsl(int r, int g, int b, float[] hsl, int offset) {

        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;

        float var_R = (r / 255f);
        float var_G = (g / 255f);
        float var_B = (b / 255f);

        float var_Min;
        float var_Max;
        float del_Max;

        if (var_R > var_G) {
            var_Min = var_G;
            var_Max = var_R;
        }
        else {
            var_Min = var_R;
            var_Max = var_G;
        }
        if (var_B > var_Max) var_Max = var_B;
        if (var_B < var_Min) var_Min = var_B;

        del_Max = var_Max - var_Min;

        float h, s, l;
        l = (var_Max + var_Min) / 2f;

        if (del_Max - 0.01f <= 0.0f) {
            h = 0;
            s = 0;
        } else {
            if (l < 0.5f) s = del_Max / (var_Max + var_Min);
            else s = del_Max / (2 - var_Max - var_Min);

            float del_R = (((var_Max - var_R) / 6f) + (del_Max / 2f)) / del_Max;
            float del_G = (((var_Max - var_G) / 6f) + (del_Max / 2f)) / del_Max;
            float del_B = (((var_Max - var_B) / 6f) + (del_Max / 2f)) / del_Max;

            if (var_R == var_Max) h = del_B - del_G;
            else if (var_G == var_Max) h = (1 / 3f) + del_R - del_B;
            else h = (2 / 3f) + del_G - del_R;
            if (h < 0) h += 1;
            if (h > 1) h -= 1;
        }

        hsl[offset] = h;
        hsl[offset + 1] = s;
        hsl[offset + 2] = l;
    }

    public static void hsl(int r, int g, int b, float[] hsl) {
        hsl(r, g, b, hsl, 0);
    }

    public static String toString(int color) {
        return String.format("0x%08X", color);
    }

}
