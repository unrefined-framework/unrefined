package unrefined.desktop;

import unrefined.util.NotInstantiableError;

import java.awt.image.BufferedImage;

public final class DefaultIcon {

    private DefaultIcon() {
        throw new NotInstantiableError(DefaultIcon.class);
    }

    private static final int[] rgbArray = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1710618, -723724, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2434084, -328966, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -3092270, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2631463, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -592137, -131587, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2960427, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2697256, -131587, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -526344, -2434084, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2829098, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -920846, -1579032, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2763049, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1578776, -1184274, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -723724, -2039326, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2105119, -723724, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1512983, -1250067, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2631462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2236961, -592138, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2960427, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2960427, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -3026220, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2960427, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -131586, -2499877, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -263173, -2434084, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -723724, -2039583, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -986895, -1776154, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1250067, -1578776, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1710618, -986639, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -4934476, -11119275, -4605511, -1710361, -4078910, -4868940, -263173, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2434084, -263173, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -921103, -14475488, -14475488, -14475488, -7171179, -12369342, -14475488, -3355444, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2960427, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -2236963, -14475488, -14475488, -14475488, -9276555, -14475488, -14475488, -1644826, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2960427, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -5461077, -14475488, -14475488, -10198172, -5526355, -14475488, -14475488, -921103, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2171170, -263173, -1, -1, -1, -1, -592138, -2236961, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -2960686, -14475488, -14475488, -14475488, -8421504, -14475488, -14475488, -1513497, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2960686, -9145485, -8421505, -1, -1, -1, -1, -1, -1250067, -1512982, -1, -1, -1, -1, -1, -1250068, -657931, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -2434342, -14475488, -14475488, -13422286, -6315870, -14475488, -14475488, -1513497, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1579290, -9079691, -14475488, -14475488, -3618616, -1, -1, -1, -1, -1, -2039326, -789260, -1, -1, -1, -1, -5066062, -14475488, -14475488, -7105902, -197380, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1842205, -14475488, -14475488, -14475488, -14475488, -14475488, -12369599, -263173, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -263173, -4934476, -14475488, -14475488, -14475488, -14475488, -921103, -1, -1, -1, -1, -1, -2828841, -1, -1, -1, -1, -1, -921103, -5000526, -14475488, -14475488, -1447447, -1447447, -4013374, -5461077, -5461077, -5461077, -3421237, -1184275, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -2631721, -11119275, -10395294, -9342607, -14475488, -4211010, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513240, -9145485, -14475488, -14475488, -14475488, -14475488, -10000793, -1, -1, -1, -1, -1, -1, -2829098, -1, -1, -1, -1, -1, -1, -263173, -11119275, -14475488, -10000793, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -12369599, -131587, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -394759, -4539717, -11579568, -12369599, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -3618616, -12369599, -14475488, -14475488, -14475488, -14475488, -14475488, -6513508, -1, -1, -1, -1, -1, -131587, -2697255, -1, -1, -1, -1, -1, -1, -5921627, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -4144960, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2499876, -14475488, -3355444, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -263173, -5921627, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -4144960, -1, -1, -1, -1, -1, -723724, -1907740, -1, -1, -1, -1, -1, -1250068, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -6645094, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -3092013, -14475488, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -855310, -7829368, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -2566185, -1, -1, -1, -1, -1, -1578776, -1249811, -1, -1, -1, -1, -592138, -5000526, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -10658724, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2960427, -9079691, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -921103, -10000793, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -1250068, -1, -1, -1, -1, -1, -2368291, -460552, -1, -1, -1, -1, -2960686, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -4671304, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2631463, -6908266, -1, -1, -1, -1, -1, -1, -1, -1, -1, -921103, -10000793, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -14475488, -13488080, -1, -1, -1, -1, -1, -1, -3026220, -1, -1, -1, -1, -1, -2236963, -14475488, -10000793, -4934476, -2960943, -3355444, -3355444, -3224115, -1513497, -1513497, -1513497, -4144960, -11711412, -14475488, -7763575, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -789260, -2171169, -7303280, -1, -1, -1, -1, -1, -1, -1, -1, -1579290, -10527394, -14475488, -14475488, -12575197, -9234133, -8906452, -11920092, -14475488, -14475488, -14475488, -14475488, -8421505, -1, -1, -1, -1, -1, -1, -2960427, -1, -1, -1, -1, -1, -5987164, -8289919, -263173, -1, -1, -1, -1, -1, -1, -1, -1, -1, -592138, -3947581, -1842462, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1118482, -1578776, -2566185, -2960686, -1, -1, -1, -1, -1, -1, -921103, -11448240, -4013374, -5131855, -9233877, -5306057, -5044168, -5044168, -5044168, -7072718, -13361375, -14475488, -14475488, -5461077, -1, -1, -1, -1, -1, -394759, -2434084, -1, -1, -1, -1, -2236963, -10987689, -921103, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1578776, -921103, -1, -4079167, -1842462, -1, -1, -1, -1, -921103, -10000793, -4934476, -1, -6843501, -5240520, -5044168, -5044168, -5044168, -3702135, -1386535, -5812647, -14082015, -14475488, -3684666, -1, -1, -1, -1, -1, -1052688, -1579032, -1, -1, -1, -526345, -10000793, -1842462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2434084, -592138, -1, -1, -1842462, -2566185, -263173, -1, -921103, -10000793, -5461077, -1, -1, -6979968, -5044168, -5044168, -5044168, -5044168, -4566688, -527886, -2047292, -8709587, -14475488, -2171170, -1, -1, -1, -1, -1, -1776411, -920846, -1, -1, -1, -7105902, -3487287, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2894378, -1, -1, -1, -1, -65794, -2631721, -3684666, -10000793, -7105645, -1, -1, -1, -7504256, -5044168, -5044168, -5044168, -5044168, -5044168, -4834737, -4566688, -6614733, -14475488, -1250068, -1, -1, -1, -1, -1, -2565926, -197380, -1, -1, -4737097, -5461077, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2631719, -1, -1, -1, -1, -1, -1, -8421505, -9342607, -263173, -1, -1, -1, -4539975, -6418124, -5044168, -5044168, -5044168, -5044168, -5044168, -5044168, -6418125, -14475488, -1, -1, -1, -1, -1, -1, -2828841, -1, -1, -3355444, -7434610, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -3157806, -1, -1, -1, -1, -1, -1250068, -12369598, -1184532, -1, -1, -1, -1, -592138, -10006445, -5698506, -5044168, -5044168, -5044168, -5044168, -5044168, -8579027, -14475488, -1, -1, -1, -1, -1, -1, -3026220, -1, -3421237, -5855835, -263173, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -328965, -2434084, -1, -1, -1, -1, -1, -7829368, -3355444, -1, -1, -1, -1, -1, -1, -1907998, -9939369, -7138511, -5240008, -5044168, -5306056, -7924178, -8421505, -12895943, -1, -1, -1, -1, -1, -657931, -2894635, -5987164, -3684666, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -723724, -1841947, -1, -1, -1, -1, -3355444, -7105645, -1, -1, -1, -1, -1, -1, -1, -1, -526345, -4144960, -7697782, -7111040, -6513765, -1907998, -1513497, -13290700, -1, -1, -1, -1, -1, -3355444, -7040108, -1250068, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1512983, -1381653, -1, -1, -1, -592138, -10987688, -921103, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -14475488, -1, -1, -1, -1250068, -5000526, -5263439, -723723, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1907740, -723724, -1, -1, -1, -6053214, -4210753, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -14475488, -328966, -592138, -4144960, -3618616, -65794, -2960427, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -2434084, -263173, -1, -1, -1842462, -11053482, -263173, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -14475488, -8421505, -3618616, -263173, -1, -1, -2894378, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -3092013, -1, -1, -1, -7763575, -3750202, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -14475488, -1381654, -1, -1, -1, -197380, -2631462, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -2828841, -1, -1, -1579033, -11053482, -263173, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -14475488, -1, -1, -1, -1, -855310, -1776411, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -2763049, -1, -1, -5921627, -4605511, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -13488080, -1, -1, -1, -1, -1578776, -1052688, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -657674, -2302498, -1, -394759, -12369599, -921103, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -8553347, -1, -1, -1, -1, -2434084, -460551, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1052688, -1578776, -1, -3355444, -7763832, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -8421505, -1, -1, -1, -1, -2894891, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1578776, -1052688, -1, -7105902, -4013374, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -8421505, -1, -1, -1, -1, -3026220, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -2302498, -657930, -197380, -12566977, -1184274, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -8421505, -1, -1, -1, -460552, -2368547, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -2763048, -1, -1907998, -10987689, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1513497, -8421505, -1, -1, -1, -1184018, -1578776, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -2763305, -1, -3618616, -6776680, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2105377, -8092797, -1, -1, -1, -1907740, -789516, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -3157806, -1, -5461077, -4605511, -1, -1, -1, -1, -1, -1, -1, -1, -723724, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -3355444, -5658199, -1, -1, -1, -2631463, -131587, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1120027, -724754, -1, -1, -197380, -2434084, -1, -5461077, -3355444, -1, -1, -1, -1, -1, -1250068, -5987164, -12369598, -14475488, -14475488, -7105902, -1907998, -1, -1, -1, -1, -1, -1, -1, -3355444, -5461077, -1, -1, -1, -2828842, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -4877701, -5736108, -5538211, -4218224, -3426131, -3424327, -987927, -5987422, -1973791, -1, -1, -1, -1, -3750202, -11053738, -5000526, -1250068, -131587, -2039841, -6316386, -14475488, -4934733, -1, -1, -1, -1, -1, -1, -3355444, -5461077, -1, -1, -1, -2828585, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -4938082, -8168904, -7314109, -6590645, -5867437, -5801644, -5801644, -5801644, -4613755, -3294803, -2174517, -1251614, -5198164, -9277071, -1184275, -1, -1, -1, -1, -1, -2566185, -12369599, -5461077, -1, -1, -1, -1, -1, -4802890, -3881788, -1, -1, -723724, -2039583, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -263173, -7636367, -10471149, -10471149, -10471149, -9418713, -8366282, -7445695, -6656694, -5999022, -5736108, -5736108, -5736108, -4812165, -3558491, -2503741, -1515300, -527118, -1, -1, -1, -1842462, -12238013, -3750202, -1, -1, -1, -1, -5461077, -3355444, -1, -1, -1447446, -1315604, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -790031, -9481407, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -9681629, -8563661, -7642817, -6853816, -6130352, -5801644, -5736108, -5736108, -5142162, -3954021, -2833222, -1713194, -1909029, -10000793, -1907998, -1, -1, -1, -6052957, -1907998, -1, -1, -2236961, -592138, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1842977, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -9879265, -8761040, -7839939, -6985145, -6261937, -5736108, -5736108, -5736108, -5867436, -5733012, -3096909, -1976367, -988183, -8619656, -855310, -1, -1, -2960427, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -3422525, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10142182, -9023699, -8037318, -7116731, -6393523, -5736108, -5736108, -5736108, -5736108, -4547963, -3294803, -2174261, -3357503, -329481, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -5265759, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10405355, -9221334, -8168904, -7313853, -6459316, -5736108, -5736108, -5736108, -5801644, -4877701, -3558747, -922391, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -263173, -7109251, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -9287127, -8366282, -7445695, -6656438, -5933229, -395274, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -790031, -7504780, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -3290938, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -197381, -1382169, -2764337, -4212555, -5726824, -7768210, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -4804950, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -65794, -1382169, -2632750, -4080712, -5594981, -7636367, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -6121840, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1382169, -2632750, -3949383, -5594981, -7570573, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -10471149, -6714492, -263173, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1316376, -2501165, -3817540, -5463138, -7241094, -8229020, -790031, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
    };
    public static final BufferedImage ICON;
    static {
        ICON = new BufferedImage(64, 64, BufferedImage.TYPE_USHORT_565_RGB);
        ICON.setRGB(0, 0, 64, 64, rgbArray, 0, 64);
    }

}
