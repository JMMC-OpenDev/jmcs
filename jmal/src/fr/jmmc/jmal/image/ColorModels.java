/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.image;

import fr.jmmc.jmcs.util.FileUtils;
import java.awt.image.IndexColorModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Give access to several color models.
 * 
 * @author Laurent BOURGES.
 */
public class ColorModels
{
    /** Class logger */
    protected static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ColorModels.class.getName());
    /**
     * Maximum number of colors in a 8 byte palette
     */
    public static final int MAX_COLORS = 256;
    /**
     * Maximum number of colors in earth and rainbow LUT
     */
    public static final int NB_COLORS = 240;
    /** force zero surroundings to be black */
    public final static boolean FORCE_ZERO = true;
    /**
     * Default color model
     */
    public final static String DEFAULT_COLOR_MODEL = "Earth";
    /**
     * Color model names
     */
    private final static Vector<String> colorModelNames = new Vector<String>();
    /**
     * Color models keyed by names
     */
    private final static Map<String, IndexColorModel> colorModels = new HashMap<String, IndexColorModel>();
    /**
     * Generated array of lut file names in the jmcs folder fr/jmmc/jmal/image/lut/
     */
    private final static String[] LUT_FILES = {
        "aspro.lut",
        "backgr.lut",
        "blue.lut",
        "blulut.lut",
        "green.lut",
        "heat.lut",
        "idl11.lut",
        "idl14.lut",
        "idl15.lut",
        "idl2.lut",
        "idl4.lut",
        "idl5.lut",
        "idl6.lut",
        "isophot.lut",
        "light.lut",
        "mousse.lut",
        "neg.lut",
        "pastel.lut",
        "pseudo1.lut",
        "pseudo2.lut",
        "rainbow.lut",
        "rainbow1.lut",
        "rainbow2.lut",
        "rainbow3.lut",
        "rainbow4.lut",
        "ramp.lut",
        "real.lut",
        "red.lut",
        "smooth.lut"
    };

    /**
     * Static initialization to prepare the color models (load lut files)
     */
    static {
        final long start = System.nanoTime();

        // hard coded color models :
        addColorModel(DEFAULT_COLOR_MODEL, getEarthColorModel());

        addColorModel("Gray", getGrayColorModel(MAX_COLORS));

        addColorModel("Rainbow", getRainbowColorModel());

        // color models from lut files :
        IndexColorModel colorModel;
        for (String name : LUT_FILES) {
            colorModel = loadFromFile(name);
            if (colorModel != null) {
                addColorModel(name.substring(0, name.indexOf('.')), colorModel);
            }
        }

        Collections.sort(colorModelNames);

        if (logger.isLoggable(Level.INFO)) {
            logger.info("ColorModels [" + colorModelNames.size() + " available] : duration = " + 1e-6d * (System.nanoTime() - start) + " ms.");
        }
    }

    private static void addColorModel(final String name, final IndexColorModel colorModel)
    {
        colorModelNames.add(name);
        colorModels.put(name, colorModel);
    }

    /**
     * Forbidden constructor
     */
    private ColorModels()
    {
        // no-op
    }

    public static Vector<String> getColorModelNames()
    {
        return colorModelNames;
    }

    public static IndexColorModel getDefaultColorModel()
    {
        return getColorModel(DEFAULT_COLOR_MODEL);
    }

    public static IndexColorModel getColorModel(final String name)
    {
        IndexColorModel colorModel = colorModels.get(name);
        if (colorModel == null) {
            return getDefaultColorModel();
        }
        return colorModel;
    }

    /* Private methods */
    /** Returns one 'earth' color model */
    private static IndexColorModel getEarthColorModel()
    {
        /* dk blue - lt blue - dk green - yellow green - lt brown - white */
        /* sort of like mapmakers colors from deep ocean to snow capped peak */
        /* ncolors= 240 */
        /* ntsc gray scale looks slightly better than straight intensity */
        /* ntsc= 1 */
        /* r   g   b */
        final byte[] r = new byte[NB_COLORS];
        final byte[] g = new byte[NB_COLORS];
        final byte[] b = new byte[NB_COLORS];
        r[0] = (byte) 0;
        g[0] = (byte) 0;
        b[0] = (byte) 0;
        r[1] = (byte) 0;
        g[1] = (byte) 0;
        b[1] = (byte) 46;
        r[2] = (byte) 0;
        g[2] = (byte) 0;
        b[2] = (byte) 58;
        r[3] = (byte) 0;
        g[3] = (byte) 0;
        b[3] = (byte) 69;
        r[4] = (byte) 0;
        g[4] = (byte) 0;
        b[4] = (byte) 81;
        r[5] = (byte) 0;
        g[5] = (byte) 0;
        b[5] = (byte) 92;
        r[6] = (byte) 0;
        g[6] = (byte) 0;
        b[6] = (byte) 104;
        r[7] = (byte) 0;
        g[7] = (byte) 0;
        b[7] = (byte) 116;
        r[8] = (byte) 0;
        g[8] = (byte) 3;
        b[8] = (byte) 116;
        r[9] = (byte) 1;
        g[9] = (byte) 6;
        b[9] = (byte) 116;
        r[10] = (byte) 2;
        g[10] = (byte) 8;
        b[10] = (byte) 116;
        r[11] = (byte) 2;
        g[11] = (byte) 11;
        b[11] = (byte) 116;
        r[12] = (byte) 3;
        g[12] = (byte) 13;
        b[12] = (byte) 117;
        r[13] = (byte) 4;
        g[13] = (byte) 16;
        b[13] = (byte) 117;
        r[14] = (byte) 5;
        g[14] = (byte) 18;
        b[14] = (byte) 117;
        r[15] = (byte) 5;
        g[15] = (byte) 21;
        b[15] = (byte) 117;
        r[16] = (byte) 6;
        g[16] = (byte) 23;
        b[16] = (byte) 117;
        r[17] = (byte) 7;
        g[17] = (byte) 26;
        b[17] = (byte) 118;
        r[18] = (byte) 8;
        g[18] = (byte) 28;
        b[18] = (byte) 118;
        r[19] = (byte) 8;
        g[19] = (byte) 31;
        b[19] = (byte) 118;
        r[20] = (byte) 9;
        g[20] = (byte) 33;
        b[20] = (byte) 118;
        r[21] = (byte) 10;
        g[21] = (byte) 36;
        b[21] = (byte) 118;
        r[22] = (byte) 11;
        g[22] = (byte) 38;
        b[22] = (byte) 119;
        r[23] = (byte) 11;
        g[23] = (byte) 41;
        b[23] = (byte) 119;
        r[24] = (byte) 12;
        g[24] = (byte) 43;
        b[24] = (byte) 119;
        r[25] = (byte) 13;
        g[25] = (byte) 45;
        b[25] = (byte) 119;
        r[26] = (byte) 14;
        g[26] = (byte) 48;
        b[26] = (byte) 119;
        r[27] = (byte) 15;
        g[27] = (byte) 50;
        b[27] = (byte) 120;
        r[28] = (byte) 15;
        g[28] = (byte) 52;
        b[28] = (byte) 120;
        r[29] = (byte) 16;
        g[29] = (byte) 55;
        b[29] = (byte) 120;
        r[30] = (byte) 17;
        g[30] = (byte) 57;
        b[30] = (byte) 120;
        r[31] = (byte) 18;
        g[31] = (byte) 59;
        b[31] = (byte) 120;
        r[32] = (byte) 18;
        g[32] = (byte) 61;
        b[32] = (byte) 121;
        r[33] = (byte) 19;
        g[33] = (byte) 64;
        b[33] = (byte) 121;
        r[34] = (byte) 20;
        g[34] = (byte) 66;
        b[34] = (byte) 121;
        r[35] = (byte) 21;
        g[35] = (byte) 68;
        b[35] = (byte) 121;
        r[36] = (byte) 22;
        g[36] = (byte) 70;
        b[36] = (byte) 121;
        r[37] = (byte) 22;
        g[37] = (byte) 72;
        b[37] = (byte) 122;
        r[38] = (byte) 23;
        g[38] = (byte) 74;
        b[38] = (byte) 122;
        r[39] = (byte) 24;
        g[39] = (byte) 77;
        b[39] = (byte) 122;
        r[40] = (byte) 25;
        g[40] = (byte) 79;
        b[40] = (byte) 122;
        r[41] = (byte) 26;
        g[41] = (byte) 81;
        b[41] = (byte) 122;
        r[42] = (byte) 26;
        g[42] = (byte) 83;
        b[42] = (byte) 123;
        r[43] = (byte) 27;
        g[43] = (byte) 85;
        b[43] = (byte) 123;
        r[44] = (byte) 28;
        g[44] = (byte) 87;
        b[44] = (byte) 123;
        r[45] = (byte) 29;
        g[45] = (byte) 89;
        b[45] = (byte) 123;
        r[46] = (byte) 30;
        g[46] = (byte) 91;
        b[46] = (byte) 123;
        r[47] = (byte) 31;
        g[47] = (byte) 93;
        b[47] = (byte) 124;
        r[48] = (byte) 31;
        g[48] = (byte) 95;
        b[48] = (byte) 124;
        r[49] = (byte) 32;
        g[49] = (byte) 97;
        b[49] = (byte) 124;
        r[50] = (byte) 33;
        g[50] = (byte) 99;
        b[50] = (byte) 124;
        r[51] = (byte) 34;
        g[51] = (byte) 100;
        b[51] = (byte) 124;
        r[52] = (byte) 35;
        g[52] = (byte) 102;
        b[52] = (byte) 125;
        r[53] = (byte) 36;
        g[53] = (byte) 104;
        b[53] = (byte) 125;
        r[54] = (byte) 36;
        g[54] = (byte) 106;
        b[54] = (byte) 125;
        r[55] = (byte) 37;
        g[55] = (byte) 108;
        b[55] = (byte) 125;
        r[56] = (byte) 38;
        g[56] = (byte) 109;
        b[56] = (byte) 125;
        r[57] = (byte) 39;
        g[57] = (byte) 111;
        b[57] = (byte) 126;
        r[58] = (byte) 40;
        g[58] = (byte) 113;
        b[58] = (byte) 126;
        r[59] = (byte) 41;
        g[59] = (byte) 115;
        b[59] = (byte) 126;
        r[60] = (byte) 41;
        g[60] = (byte) 116;
        b[60] = (byte) 126;
        r[61] = (byte) 42;
        g[61] = (byte) 118;
        b[61] = (byte) 126;
        r[62] = (byte) 43;
        g[62] = (byte) 120;
        b[62] = (byte) 127;
        r[63] = (byte) 44;
        g[63] = (byte) 121;
        b[63] = (byte) 127;
        r[64] = (byte) 45;
        g[64] = (byte) 123;
        b[64] = (byte) 127;
        r[65] = (byte) 46;
        g[65] = (byte) 125;
        b[65] = (byte) 127;
        r[66] = (byte) 47;
        g[66] = (byte) 126;
        b[66] = (byte) 127;
        r[67] = (byte) 48;
        g[67] = (byte) 128;
        b[67] = (byte) 128;
        r[68] = (byte) 48;
        g[68] = (byte) 128;
        b[68] = (byte) 126;
        r[69] = (byte) 48;
        g[69] = (byte) 129;
        b[69] = (byte) 125;
        r[70] = (byte) 49;
        g[70] = (byte) 129;
        b[70] = (byte) 124;
        r[71] = (byte) 49;
        g[71] = (byte) 130;
        b[71] = (byte) 123;
        r[72] = (byte) 50;
        g[72] = (byte) 131;
        b[72] = (byte) 122;
        r[73] = (byte) 50;
        g[73] = (byte) 131;
        b[73] = (byte) 120;
        r[74] = (byte) 51;
        g[74] = (byte) 132;
        b[74] = (byte) 119;
        r[75] = (byte) 51;
        g[75] = (byte) 133;
        b[75] = (byte) 118;
        r[76] = (byte) 52;
        g[76] = (byte) 133;
        b[76] = (byte) 117;
        r[77] = (byte) 52;
        g[77] = (byte) 134;
        b[77] = (byte) 115;
        r[78] = (byte) 53;
        g[78] = (byte) 134;
        b[78] = (byte) 114;
        r[79] = (byte) 53;
        g[79] = (byte) 135;
        b[79] = (byte) 113;
        r[80] = (byte) 54;
        g[80] = (byte) 136;
        b[80] = (byte) 111;
        r[81] = (byte) 54;
        g[81] = (byte) 136;
        b[81] = (byte) 110;
        r[82] = (byte) 55;
        g[82] = (byte) 137;
        b[82] = (byte) 109;
        r[83] = (byte) 55;
        g[83] = (byte) 138;
        b[83] = (byte) 108;
        r[84] = (byte) 56;
        g[84] = (byte) 138;
        b[84] = (byte) 106;
        r[85] = (byte) 56;
        g[85] = (byte) 139;
        b[85] = (byte) 105;
        r[86] = (byte) 57;
        g[86] = (byte) 140;
        b[86] = (byte) 104;
        r[87] = (byte) 57;
        g[87] = (byte) 140;
        b[87] = (byte) 102;
        r[88] = (byte) 58;
        g[88] = (byte) 141;
        b[88] = (byte) 101;
        r[89] = (byte) 58;
        g[89] = (byte) 141;
        b[89] = (byte) 100;
        r[90] = (byte) 59;
        g[90] = (byte) 142;
        b[90] = (byte) 98;
        r[91] = (byte) 59;
        g[91] = (byte) 143;
        b[91] = (byte) 97;
        r[92] = (byte) 60;
        g[92] = (byte) 143;
        b[92] = (byte) 96;
        r[93] = (byte) 61;
        g[93] = (byte) 144;
        b[93] = (byte) 94;
        r[94] = (byte) 61;
        g[94] = (byte) 145;
        b[94] = (byte) 93;
        r[95] = (byte) 62;
        g[95] = (byte) 145;
        b[95] = (byte) 92;
        r[96] = (byte) 62;
        g[96] = (byte) 146;
        b[96] = (byte) 90;
        r[97] = (byte) 63;
        g[97] = (byte) 146;
        b[97] = (byte) 89;
        r[98] = (byte) 63;
        g[98] = (byte) 147;
        b[98] = (byte) 88;
        r[99] = (byte) 64;
        g[99] = (byte) 148;
        b[99] = (byte) 86;
        r[100] = (byte) 64;
        g[100] = (byte) 148;
        b[100] = (byte) 85;
        r[101] = (byte) 65;
        g[101] = (byte) 149;
        b[101] = (byte) 84;
        r[102] = (byte) 65;
        g[102] = (byte) 150;
        b[102] = (byte) 82;
        r[103] = (byte) 66;
        g[103] = (byte) 150;
        b[103] = (byte) 81;
        r[104] = (byte) 67;
        g[104] = (byte) 151;
        b[104] = (byte) 80;
        r[105] = (byte) 67;
        g[105] = (byte) 151;
        b[105] = (byte) 78;
        r[106] = (byte) 68;
        g[106] = (byte) 152;
        b[106] = (byte) 77;
        r[107] = (byte) 68;
        g[107] = (byte) 153;
        b[107] = (byte) 76;
        r[108] = (byte) 69;
        g[108] = (byte) 153;
        b[108] = (byte) 74;
        r[109] = (byte) 69;
        g[109] = (byte) 154;
        b[109] = (byte) 73;
        r[110] = (byte) 70;
        g[110] = (byte) 155;
        b[110] = (byte) 71;
        r[111] = (byte) 71;
        g[111] = (byte) 155;
        b[111] = (byte) 70;
        r[112] = (byte) 73;
        g[112] = (byte) 156;
        b[112] = (byte) 71;
        r[113] = (byte) 76;
        g[113] = (byte) 156;
        b[113] = (byte) 72;
        r[114] = (byte) 78;
        g[114] = (byte) 157;
        b[114] = (byte) 72;
        r[115] = (byte) 81;
        g[115] = (byte) 158;
        b[115] = (byte) 73;
        r[116] = (byte) 83;
        g[116] = (byte) 158;
        b[116] = (byte) 73;
        r[117] = (byte) 86;
        g[117] = (byte) 159;
        b[117] = (byte) 74;
        r[118] = (byte) 88;
        g[118] = (byte) 160;
        b[118] = (byte) 75;
        r[119] = (byte) 91;
        g[119] = (byte) 160;
        b[119] = (byte) 75;
        r[120] = (byte) 94;
        g[120] = (byte) 161;
        b[120] = (byte) 76;
        r[121] = (byte) 96;
        g[121] = (byte) 161;
        b[121] = (byte) 76;
        r[122] = (byte) 99;
        g[122] = (byte) 162;
        b[122] = (byte) 77;
        r[123] = (byte) 101;
        g[123] = (byte) 163;
        b[123] = (byte) 77;
        r[124] = (byte) 104;
        g[124] = (byte) 163;
        b[124] = (byte) 78;
        r[125] = (byte) 106;
        g[125] = (byte) 164;
        b[125] = (byte) 79;
        r[126] = (byte) 109;
        g[126] = (byte) 165;
        b[126] = (byte) 79;
        r[127] = (byte) 111;
        g[127] = (byte) 165;
        b[127] = (byte) 80;
        r[128] = (byte) 114;
        g[128] = (byte) 166;
        b[128] = (byte) 80;
        r[129] = (byte) 117;
        g[129] = (byte) 166;
        b[129] = (byte) 81;
        r[130] = (byte) 119;
        g[130] = (byte) 167;
        b[130] = (byte) 82;
        r[131] = (byte) 121;
        g[131] = (byte) 168;
        b[131] = (byte) 82;
        r[132] = (byte) 122;
        g[132] = (byte) 168;
        b[132] = (byte) 82;
        r[133] = (byte) 124;
        g[133] = (byte) 168;
        b[133] = (byte) 83;
        r[134] = (byte) 126;
        g[134] = (byte) 169;
        b[134] = (byte) 83;
        r[135] = (byte) 128;
        g[135] = (byte) 169;
        b[135] = (byte) 83;
        r[136] = (byte) 129;
        g[136] = (byte) 170;
        b[136] = (byte) 84;
        r[137] = (byte) 131;
        g[137] = (byte) 170;
        b[137] = (byte) 84;
        r[138] = (byte) 133;
        g[138] = (byte) 171;
        b[138] = (byte) 84;
        r[139] = (byte) 135;
        g[139] = (byte) 171;
        b[139] = (byte) 85;
        r[140] = (byte) 136;
        g[140] = (byte) 172;
        b[140] = (byte) 85;
        r[141] = (byte) 138;
        g[141] = (byte) 172;
        b[141] = (byte) 85;
        r[142] = (byte) 140;
        g[142] = (byte) 172;
        b[142] = (byte) 86;
        r[143] = (byte) 141;
        g[143] = (byte) 173;
        b[143] = (byte) 86;
        r[144] = (byte) 143;
        g[144] = (byte) 173;
        b[144] = (byte) 86;
        r[145] = (byte) 145;
        g[145] = (byte) 174;
        b[145] = (byte) 87;
        r[146] = (byte) 147;
        g[146] = (byte) 174;
        b[146] = (byte) 87;
        r[147] = (byte) 149;
        g[147] = (byte) 175;
        b[147] = (byte) 87;
        r[148] = (byte) 150;
        g[148] = (byte) 175;
        b[148] = (byte) 88;
        r[149] = (byte) 152;
        g[149] = (byte) 175;
        b[149] = (byte) 88;
        r[150] = (byte) 154;
        g[150] = (byte) 176;
        b[150] = (byte) 88;
        r[151] = (byte) 156;
        g[151] = (byte) 176;
        b[151] = (byte) 89;
        r[152] = (byte) 157;
        g[152] = (byte) 177;
        b[152] = (byte) 89;
        r[153] = (byte) 159;
        g[153] = (byte) 177;
        b[153] = (byte) 89;
        r[154] = (byte) 161;
        g[154] = (byte) 178;
        b[154] = (byte) 90;
        r[155] = (byte) 163;
        g[155] = (byte) 178;
        b[155] = (byte) 90;
        r[156] = (byte) 165;
        g[156] = (byte) 179;
        b[156] = (byte) 90;
        r[157] = (byte) 166;
        g[157] = (byte) 179;
        b[157] = (byte) 91;
        r[158] = (byte) 168;
        g[158] = (byte) 179;
        b[158] = (byte) 91;
        r[159] = (byte) 170;
        g[159] = (byte) 180;
        b[159] = (byte) 91;
        r[160] = (byte) 172;
        g[160] = (byte) 180;
        b[160] = (byte) 92;
        r[161] = (byte) 174;
        g[161] = (byte) 181;
        b[161] = (byte) 92;
        r[162] = (byte) 175;
        g[162] = (byte) 181;
        b[162] = (byte) 92;
        r[163] = (byte) 177;
        g[163] = (byte) 182;
        b[163] = (byte) 93;
        r[164] = (byte) 179;
        g[164] = (byte) 182;
        b[164] = (byte) 93;
        r[165] = (byte) 181;
        g[165] = (byte) 183;
        b[165] = (byte) 93;
        r[166] = (byte) 183;
        g[166] = (byte) 183;
        b[166] = (byte) 94;
        r[167] = (byte) 183;
        g[167] = (byte) 182;
        b[167] = (byte) 94;
        r[168] = (byte) 184;
        g[168] = (byte) 181;
        b[168] = (byte) 94;
        r[169] = (byte) 184;
        g[169] = (byte) 181;
        b[169] = (byte) 95;
        r[170] = (byte) 185;
        g[170] = (byte) 180;
        b[170] = (byte) 95;
        r[171] = (byte) 185;
        g[171] = (byte) 179;
        b[171] = (byte) 95;
        r[172] = (byte) 186;
        g[172] = (byte) 178;
        b[172] = (byte) 96;
        r[173] = (byte) 186;
        g[173] = (byte) 177;
        b[173] = (byte) 96;
        r[174] = (byte) 187;
        g[174] = (byte) 176;
        b[174] = (byte) 97;
        r[175] = (byte) 187;
        g[175] = (byte) 175;
        b[175] = (byte) 97;
        r[176] = (byte) 187;
        g[176] = (byte) 174;
        b[176] = (byte) 97;
        r[177] = (byte) 188;
        g[177] = (byte) 173;
        b[177] = (byte) 98;
        r[178] = (byte) 188;
        g[178] = (byte) 172;
        b[178] = (byte) 98;
        r[179] = (byte) 189;
        g[179] = (byte) 171;
        b[179] = (byte) 98;
        r[180] = (byte) 189;
        g[180] = (byte) 170;
        b[180] = (byte) 99;
        r[181] = (byte) 190;
        g[181] = (byte) 169;
        b[181] = (byte) 99;
        r[182] = (byte) 190;
        g[182] = (byte) 168;
        b[182] = (byte) 99;
        r[183] = (byte) 190;
        g[183] = (byte) 167;
        b[183] = (byte) 100;
        r[184] = (byte) 191;
        g[184] = (byte) 166;
        b[184] = (byte) 100;
        r[185] = (byte) 191;
        g[185] = (byte) 165;
        b[185] = (byte) 100;
        r[186] = (byte) 192;
        g[186] = (byte) 164;
        b[186] = (byte) 101;
        r[187] = (byte) 192;
        g[187] = (byte) 163;
        b[187] = (byte) 101;
        r[188] = (byte) 193;
        g[188] = (byte) 163;
        b[188] = (byte) 104;
        r[189] = (byte) 195;
        g[189] = (byte) 164;
        b[189] = (byte) 106;
        r[190] = (byte) 196;
        g[190] = (byte) 164;
        b[190] = (byte) 108;
        r[191] = (byte) 197;
        g[191] = (byte) 165;
        b[191] = (byte) 111;
        r[192] = (byte) 198;
        g[192] = (byte) 165;
        b[192] = (byte) 113;
        r[193] = (byte) 199;
        g[193] = (byte) 166;
        b[193] = (byte) 116;
        r[194] = (byte) 201;
        g[194] = (byte) 167;
        b[194] = (byte) 118;
        r[195] = (byte) 202;
        g[195] = (byte) 167;
        b[195] = (byte) 121;
        r[196] = (byte) 203;
        g[196] = (byte) 168;
        b[196] = (byte) 123;
        r[197] = (byte) 204;
        g[197] = (byte) 169;
        b[197] = (byte) 126;
        r[198] = (byte) 205;
        g[198] = (byte) 170;
        b[198] = (byte) 129;
        r[199] = (byte) 207;
        g[199] = (byte) 171;
        b[199] = (byte) 131;
        r[200] = (byte) 208;
        g[200] = (byte) 172;
        b[200] = (byte) 134;
        r[201] = (byte) 209;
        g[201] = (byte) 173;
        b[201] = (byte) 137;
        r[202] = (byte) 210;
        g[202] = (byte) 174;
        b[202] = (byte) 139;
        r[203] = (byte) 211;
        g[203] = (byte) 175;
        b[203] = (byte) 142;
        r[204] = (byte) 213;
        g[204] = (byte) 176;
        b[204] = (byte) 145;
        r[205] = (byte) 214;
        g[205] = (byte) 177;
        b[205] = (byte) 148;
        r[206] = (byte) 215;
        g[206] = (byte) 178;
        b[206] = (byte) 150;
        r[207] = (byte) 216;
        g[207] = (byte) 179;
        b[207] = (byte) 153;
        r[208] = (byte) 217;
        g[208] = (byte) 181;
        b[208] = (byte) 156;
        r[209] = (byte) 219;
        g[209] = (byte) 182;
        b[209] = (byte) 159;
        r[210] = (byte) 220;
        g[210] = (byte) 184;
        b[210] = (byte) 162;
        r[211] = (byte) 221;
        g[211] = (byte) 185;
        b[211] = (byte) 165;
        r[212] = (byte) 222;
        g[212] = (byte) 187;
        b[212] = (byte) 168;
        r[213] = (byte) 223;
        g[213] = (byte) 188;
        b[213] = (byte) 170;
        r[214] = (byte) 225;
        g[214] = (byte) 190;
        b[214] = (byte) 173;
        r[215] = (byte) 226;
        g[215] = (byte) 192;
        b[215] = (byte) 176;
        r[216] = (byte) 227;
        g[216] = (byte) 194;
        b[216] = (byte) 179;
        r[217] = (byte) 228;
        g[217] = (byte) 196;
        b[217] = (byte) 182;
        r[218] = (byte) 229;
        g[218] = (byte) 198;
        b[218] = (byte) 185;
        r[219] = (byte) 231;
        g[219] = (byte) 200;
        b[219] = (byte) 189;
        r[220] = (byte) 232;
        g[220] = (byte) 202;
        b[220] = (byte) 192;
        r[221] = (byte) 233;
        g[221] = (byte) 204;
        b[221] = (byte) 195;
        r[222] = (byte) 234;
        g[222] = (byte) 206;
        b[222] = (byte) 198;
        r[223] = (byte) 235;
        g[223] = (byte) 208;
        b[223] = (byte) 201;
        r[224] = (byte) 237;
        g[224] = (byte) 211;
        b[224] = (byte) 204;
        r[225] = (byte) 238;
        g[225] = (byte) 213;
        b[225] = (byte) 207;
        r[226] = (byte) 239;
        g[226] = (byte) 215;
        b[226] = (byte) 211;
        r[227] = (byte) 240;
        g[227] = (byte) 218;
        b[227] = (byte) 214;
        r[228] = (byte) 241;
        g[228] = (byte) 221;
        b[228] = (byte) 217;
        r[229] = (byte) 243;
        g[229] = (byte) 223;
        b[229] = (byte) 220;
        r[230] = (byte) 244;
        g[230] = (byte) 226;
        b[230] = (byte) 224;
        r[231] = (byte) 245;
        g[231] = (byte) 229;
        b[231] = (byte) 227;
        r[232] = (byte) 246;
        g[232] = (byte) 232;
        b[232] = (byte) 230;
        r[233] = (byte) 247;
        g[233] = (byte) 235;
        b[233] = (byte) 234;
        r[234] = (byte) 249;
        g[234] = (byte) 238;
        b[234] = (byte) 237;
        r[235] = (byte) 250;
        g[235] = (byte) 241;
        b[235] = (byte) 241;
        r[236] = (byte) 251;
        g[236] = (byte) 244;
        b[236] = (byte) 244;
        r[237] = (byte) 252;
        g[237] = (byte) 248;
        b[237] = (byte) 248;
        r[238] = (byte) 253;
        g[238] = (byte) 251;
        b[238] = (byte) 251;
        r[239] = (byte) 255;
        g[239] = (byte) 255;
        b[239] = (byte) 255;

        return new IndexColorModel(8, NB_COLORS, r, g, b);
    }

    /** Returns one 'rainbow' color model */
    private static IndexColorModel getRainbowColorModel()
    {
        /* red - orange - yellow - green - blue - purple */
        /* colors in spectral order */
        /* ncolors= 240 */
        /* r       g       b */
        //int nbColors = 240;
        final byte[] r = new byte[NB_COLORS];
        final byte[] g = new byte[NB_COLORS];
        final byte[] b = new byte[NB_COLORS];
        r[0] = (byte) 255;
        g[0] = (byte) 0;
        b[0] = (byte) 42;
        r[1] = (byte) 255;
        g[1] = (byte) 0;
        b[1] = (byte) 36;
        r[2] = (byte) 255;
        g[2] = (byte) 0;
        b[2] = (byte) 31;
        r[3] = (byte) 255;
        g[3] = (byte) 0;
        b[3] = (byte) 26;
        r[4] = (byte) 255;
        g[4] = (byte) 0;
        b[4] = (byte) 20;
        r[5] = (byte) 255;
        g[5] = (byte) 0;
        b[5] = (byte) 15;
        r[6] = (byte) 255;
        g[6] = (byte) 0;
        b[6] = (byte) 10;
        r[7] = (byte) 255;
        g[7] = (byte) 0;
        b[7] = (byte) 4;
        r[8] = (byte) 255;
        g[8] = (byte) 5;
        b[8] = (byte) 0;
        r[9] = (byte) 255;
        g[9] = (byte) 11;
        b[9] = (byte) 0;
        r[10] = (byte) 255;
        g[10] = (byte) 16;
        b[10] = (byte) 0;
        r[11] = (byte) 255;
        g[11] = (byte) 22;
        b[11] = (byte) 0;
        r[12] = (byte) 255;
        g[12] = (byte) 27;
        b[12] = (byte) 0;
        r[13] = (byte) 255;
        g[13] = (byte) 32;
        b[13] = (byte) 0;
        r[14] = (byte) 255;
        g[14] = (byte) 38;
        b[14] = (byte) 0;
        r[15] = (byte) 255;
        g[15] = (byte) 43;
        b[15] = (byte) 0;
        r[16] = (byte) 255;
        g[16] = (byte) 48;
        b[16] = (byte) 0;
        r[17] = (byte) 255;
        g[17] = (byte) 54;
        b[17] = (byte) 0;
        r[18] = (byte) 255;
        g[18] = (byte) 59;
        b[18] = (byte) 0;
        r[19] = (byte) 255;
        g[19] = (byte) 65;
        b[19] = (byte) 0;
        r[20] = (byte) 255;
        g[20] = (byte) 70;
        b[20] = (byte) 0;
        r[21] = (byte) 255;
        g[21] = (byte) 75;
        b[21] = (byte) 0;
        r[22] = (byte) 255;
        g[22] = (byte) 81;
        b[22] = (byte) 0;
        r[23] = (byte) 255;
        g[23] = (byte) 91;
        b[23] = (byte) 0;
        r[24] = (byte) 255;
        g[24] = (byte) 97;
        b[24] = (byte) 0;
        r[25] = (byte) 255;
        g[25] = (byte) 102;
        b[25] = (byte) 0;
        r[26] = (byte) 255;
        g[26] = (byte) 108;
        b[26] = (byte) 0;
        r[27] = (byte) 255;
        g[27] = (byte) 113;
        b[27] = (byte) 0;
        r[28] = (byte) 255;
        g[28] = (byte) 118;
        b[28] = (byte) 0;
        r[29] = (byte) 255;
        g[29] = (byte) 124;
        b[29] = (byte) 0;
        r[30] = (byte) 255;
        g[30] = (byte) 129;
        b[30] = (byte) 0;
        r[31] = (byte) 255;
        g[31] = (byte) 135;
        b[31] = (byte) 0;
        r[32] = (byte) 255;
        g[32] = (byte) 140;
        b[32] = (byte) 0;
        r[33] = (byte) 255;
        g[33] = (byte) 145;
        b[33] = (byte) 0;
        r[34] = (byte) 255;
        g[34] = (byte) 151;
        b[34] = (byte) 0;
        r[35] = (byte) 255;
        g[35] = (byte) 156;
        b[35] = (byte) 0;
        r[36] = (byte) 255;
        g[36] = (byte) 161;
        b[36] = (byte) 0;
        r[37] = (byte) 255;
        g[37] = (byte) 167;
        b[37] = (byte) 0;
        r[38] = (byte) 255;
        g[38] = (byte) 178;
        b[38] = (byte) 0;
        r[39] = (byte) 255;
        g[39] = (byte) 183;
        b[39] = (byte) 0;
        r[40] = (byte) 255;
        g[40] = (byte) 188;
        b[40] = (byte) 0;
        r[41] = (byte) 255;
        g[41] = (byte) 194;
        b[41] = (byte) 0;
        r[42] = (byte) 255;
        g[42] = (byte) 199;
        b[42] = (byte) 0;
        r[43] = (byte) 255;
        g[43] = (byte) 204;
        b[43] = (byte) 0;
        r[44] = (byte) 255;
        g[44] = (byte) 210;
        b[44] = (byte) 0;
        r[45] = (byte) 255;
        g[45] = (byte) 215;
        b[45] = (byte) 0;
        r[46] = (byte) 255;
        g[46] = (byte) 221;
        b[46] = (byte) 0;
        r[47] = (byte) 255;
        g[47] = (byte) 226;
        b[47] = (byte) 0;
        r[48] = (byte) 255;
        g[48] = (byte) 231;
        b[48] = (byte) 0;
        r[49] = (byte) 255;
        g[49] = (byte) 237;
        b[49] = (byte) 0;
        r[50] = (byte) 255;
        g[50] = (byte) 242;
        b[50] = (byte) 0;
        r[51] = (byte) 255;
        g[51] = (byte) 247;
        b[51] = (byte) 0;
        r[52] = (byte) 255;
        g[52] = (byte) 253;
        b[52] = (byte) 0;
        r[53] = (byte) 245;
        g[53] = (byte) 255;
        b[53] = (byte) 0;
        r[54] = (byte) 240;
        g[54] = (byte) 255;
        b[54] = (byte) 0;
        r[55] = (byte) 235;
        g[55] = (byte) 255;
        b[55] = (byte) 0;
        r[56] = (byte) 229;
        g[56] = (byte) 255;
        b[56] = (byte) 0;
        r[57] = (byte) 224;
        g[57] = (byte) 255;
        b[57] = (byte) 0;
        r[58] = (byte) 219;
        g[58] = (byte) 255;
        b[58] = (byte) 0;
        r[59] = (byte) 213;
        g[59] = (byte) 255;
        b[59] = (byte) 0;
        r[60] = (byte) 208;
        g[60] = (byte) 255;
        b[60] = (byte) 0;
        r[61] = (byte) 202;
        g[61] = (byte) 255;
        b[61] = (byte) 0;
        r[62] = (byte) 197;
        g[62] = (byte) 255;
        b[62] = (byte) 0;
        r[63] = (byte) 192;
        g[63] = (byte) 255;
        b[63] = (byte) 0;
        r[64] = (byte) 186;
        g[64] = (byte) 255;
        b[64] = (byte) 0;
        r[65] = (byte) 181;
        g[65] = (byte) 255;
        b[65] = (byte) 0;
        r[66] = (byte) 175;
        g[66] = (byte) 255;
        b[66] = (byte) 0;
        r[67] = (byte) 170;
        g[67] = (byte) 255;
        b[67] = (byte) 0;
        r[68] = (byte) 159;
        g[68] = (byte) 255;
        b[68] = (byte) 0;
        r[69] = (byte) 154;
        g[69] = (byte) 255;
        b[69] = (byte) 0;
        r[70] = (byte) 149;
        g[70] = (byte) 255;
        b[70] = (byte) 0;
        r[71] = (byte) 143;
        g[71] = (byte) 255;
        b[71] = (byte) 0;
        r[72] = (byte) 138;
        g[72] = (byte) 255;
        b[72] = (byte) 0;
        r[73] = (byte) 132;
        g[73] = (byte) 255;
        b[73] = (byte) 0;
        r[74] = (byte) 127;
        g[74] = (byte) 255;
        b[74] = (byte) 0;
        r[75] = (byte) 122;
        g[75] = (byte) 255;
        b[75] = (byte) 0;
        r[76] = (byte) 116;
        g[76] = (byte) 255;
        b[76] = (byte) 0;
        r[77] = (byte) 111;
        g[77] = (byte) 255;
        b[77] = (byte) 0;
        r[78] = (byte) 106;
        g[78] = (byte) 255;
        b[78] = (byte) 0;
        r[79] = (byte) 100;
        g[79] = (byte) 255;
        b[79] = (byte) 0;
        r[80] = (byte) 95;
        g[80] = (byte) 255;
        b[80] = (byte) 0;
        r[81] = (byte) 89;
        g[81] = (byte) 255;
        b[81] = (byte) 0;
        r[82] = (byte) 84;
        g[82] = (byte) 255;
        b[82] = (byte) 0;
        r[83] = (byte) 73;
        g[83] = (byte) 255;
        b[83] = (byte) 0;
        r[84] = (byte) 68;
        g[84] = (byte) 255;
        b[84] = (byte) 0;
        r[85] = (byte) 63;
        g[85] = (byte) 255;
        b[85] = (byte) 0;
        r[86] = (byte) 57;
        g[86] = (byte) 255;
        b[86] = (byte) 0;
        r[87] = (byte) 52;
        g[87] = (byte) 255;
        b[87] = (byte) 0;
        r[88] = (byte) 46;
        g[88] = (byte) 255;
        b[88] = (byte) 0;
        r[89] = (byte) 41;
        g[89] = (byte) 255;
        b[89] = (byte) 0;
        r[90] = (byte) 36;
        g[90] = (byte) 255;
        b[90] = (byte) 0;
        r[91] = (byte) 30;
        g[91] = (byte) 255;
        b[91] = (byte) 0;
        r[92] = (byte) 25;
        g[92] = (byte) 255;
        b[92] = (byte) 0;
        r[93] = (byte) 19;
        g[93] = (byte) 255;
        b[93] = (byte) 0;
        r[94] = (byte) 14;
        g[94] = (byte) 255;
        b[94] = (byte) 0;
        r[95] = (byte) 9;
        g[95] = (byte) 255;
        b[95] = (byte) 0;
        r[96] = (byte) 3;
        g[96] = (byte) 255;
        b[96] = (byte) 0;
        r[97] = (byte) 0;
        g[97] = (byte) 255;
        b[97] = (byte) 1;
        r[98] = (byte) 0;
        g[98] = (byte) 255;
        b[98] = (byte) 12;
        r[99] = (byte) 0;
        g[99] = (byte) 255;
        b[99] = (byte) 17;
        r[100] = (byte) 0;
        g[100] = (byte) 255;
        b[100] = (byte) 23;
        r[101] = (byte) 0;
        g[101] = (byte) 255;
        b[101] = (byte) 28;
        r[102] = (byte) 0;
        g[102] = (byte) 255;
        b[102] = (byte) 33;
        r[103] = (byte) 0;
        g[103] = (byte) 255;
        b[103] = (byte) 39;
        r[104] = (byte) 0;
        g[104] = (byte) 255;
        b[104] = (byte) 44;
        r[105] = (byte) 0;
        g[105] = (byte) 255;
        b[105] = (byte) 49;
        r[106] = (byte) 0;
        g[106] = (byte) 255;
        b[106] = (byte) 55;
        r[107] = (byte) 0;
        g[107] = (byte) 255;
        b[107] = (byte) 60;
        r[108] = (byte) 0;
        g[108] = (byte) 255;
        b[108] = (byte) 66;
        r[109] = (byte) 0;
        g[109] = (byte) 255;
        b[109] = (byte) 71;
        r[110] = (byte) 0;
        g[110] = (byte) 255;
        b[110] = (byte) 76;
        r[111] = (byte) 0;
        g[111] = (byte) 255;
        b[111] = (byte) 82;
        r[112] = (byte) 0;
        g[112] = (byte) 255;
        b[112] = (byte) 87;
        r[113] = (byte) 0;
        g[113] = (byte) 255;
        b[113] = (byte) 98;
        r[114] = (byte) 0;
        g[114] = (byte) 255;
        b[114] = (byte) 103;
        r[115] = (byte) 0;
        g[115] = (byte) 255;
        b[115] = (byte) 109;
        r[116] = (byte) 0;
        g[116] = (byte) 255;
        b[116] = (byte) 114;
        r[117] = (byte) 0;
        g[117] = (byte) 255;
        b[117] = (byte) 119;
        r[118] = (byte) 0;
        g[118] = (byte) 255;
        b[118] = (byte) 125;
        r[119] = (byte) 0;
        g[119] = (byte) 255;
        b[119] = (byte) 130;
        r[120] = (byte) 0;
        g[120] = (byte) 255;
        b[120] = (byte) 135;
        r[121] = (byte) 0;
        g[121] = (byte) 255;
        b[121] = (byte) 141;
        r[122] = (byte) 0;
        g[122] = (byte) 255;
        b[122] = (byte) 146;
        r[123] = (byte) 0;
        g[123] = (byte) 255;
        b[123] = (byte) 152;
        r[124] = (byte) 0;
        g[124] = (byte) 255;
        b[124] = (byte) 157;
        r[125] = (byte) 0;
        g[125] = (byte) 255;
        b[125] = (byte) 162;
        r[126] = (byte) 0;
        g[126] = (byte) 255;
        b[126] = (byte) 168;
        r[127] = (byte) 0;
        g[127] = (byte) 255;
        b[127] = (byte) 173;
        r[128] = (byte) 0;
        g[128] = (byte) 255;
        b[128] = (byte) 184;
        r[129] = (byte) 0;
        g[129] = (byte) 255;
        b[129] = (byte) 189;
        r[130] = (byte) 0;
        g[130] = (byte) 255;
        b[130] = (byte) 195;
        r[131] = (byte) 0;
        g[131] = (byte) 255;
        b[131] = (byte) 200;
        r[132] = (byte) 0;
        g[132] = (byte) 255;
        b[132] = (byte) 205;
        r[133] = (byte) 0;
        g[133] = (byte) 255;
        b[133] = (byte) 211;
        r[134] = (byte) 0;
        g[134] = (byte) 255;
        b[134] = (byte) 216;
        r[135] = (byte) 0;
        g[135] = (byte) 255;
        b[135] = (byte) 222;
        r[136] = (byte) 0;
        g[136] = (byte) 255;
        b[136] = (byte) 227;
        r[137] = (byte) 0;
        g[137] = (byte) 255;
        b[137] = (byte) 232;
        r[138] = (byte) 0;
        g[138] = (byte) 255;
        b[138] = (byte) 238;
        r[139] = (byte) 0;
        g[139] = (byte) 255;
        b[139] = (byte) 243;
        r[140] = (byte) 0;
        g[140] = (byte) 255;
        b[140] = (byte) 248;
        r[141] = (byte) 0;
        g[141] = (byte) 255;
        b[141] = (byte) 254;
        r[142] = (byte) 0;
        g[142] = (byte) 250;
        b[142] = (byte) 255;
        r[143] = (byte) 0;
        g[143] = (byte) 239;
        b[143] = (byte) 255;
        r[144] = (byte) 0;
        g[144] = (byte) 234;
        b[144] = (byte) 255;
        r[145] = (byte) 0;
        g[145] = (byte) 228;
        b[145] = (byte) 255;
        r[146] = (byte) 0;
        g[146] = (byte) 223;
        b[146] = (byte) 255;
        r[147] = (byte) 0;
        g[147] = (byte) 218;
        b[147] = (byte) 255;
        r[148] = (byte) 0;
        g[148] = (byte) 212;
        b[148] = (byte) 255;
        r[149] = (byte) 0;
        g[149] = (byte) 207;
        b[149] = (byte) 255;
        r[150] = (byte) 0;
        g[150] = (byte) 201;
        b[150] = (byte) 255;
        r[151] = (byte) 0;
        g[151] = (byte) 196;
        b[151] = (byte) 255;
        r[152] = (byte) 0;
        g[152] = (byte) 191;
        b[152] = (byte) 255;
        r[153] = (byte) 0;
        g[153] = (byte) 185;
        b[153] = (byte) 255;
        r[154] = (byte) 0;
        g[154] = (byte) 180;
        b[154] = (byte) 255;
        r[155] = (byte) 0;
        g[155] = (byte) 174;
        b[155] = (byte) 255;
        r[156] = (byte) 0;
        g[156] = (byte) 169;
        b[156] = (byte) 255;
        r[157] = (byte) 0;
        g[157] = (byte) 164;
        b[157] = (byte) 255;
        r[158] = (byte) 0;
        g[158] = (byte) 153;
        b[158] = (byte) 255;
        r[159] = (byte) 0;
        g[159] = (byte) 148;
        b[159] = (byte) 255;
        r[160] = (byte) 0;
        g[160] = (byte) 142;
        b[160] = (byte) 255;
        r[161] = (byte) 0;
        g[161] = (byte) 137;
        b[161] = (byte) 255;
        r[162] = (byte) 0;
        g[162] = (byte) 131;
        b[162] = (byte) 255;
        r[163] = (byte) 0;
        g[163] = (byte) 126;
        b[163] = (byte) 255;
        r[164] = (byte) 0;
        g[164] = (byte) 121;
        b[164] = (byte) 255;
        r[165] = (byte) 0;
        g[165] = (byte) 115;
        b[165] = (byte) 255;
        r[166] = (byte) 0;
        g[166] = (byte) 110;
        b[166] = (byte) 255;
        r[167] = (byte) 0;
        g[167] = (byte) 105;
        b[167] = (byte) 255;
        r[168] = (byte) 0;
        g[168] = (byte) 99;
        b[168] = (byte) 255;
        r[169] = (byte) 0;
        g[169] = (byte) 94;
        b[169] = (byte) 255;
        r[170] = (byte) 0;
        g[170] = (byte) 88;
        b[170] = (byte) 255;
        r[171] = (byte) 0;
        g[171] = (byte) 83;
        b[171] = (byte) 255;
        r[172] = (byte) 0;
        g[172] = (byte) 78;
        b[172] = (byte) 255;
        r[173] = (byte) 0;
        g[173] = (byte) 67;
        b[173] = (byte) 255;
        r[174] = (byte) 0;
        g[174] = (byte) 62;
        b[174] = (byte) 255;
        r[175] = (byte) 0;
        g[175] = (byte) 56;
        b[175] = (byte) 255;
        r[176] = (byte) 0;
        g[176] = (byte) 51;
        b[176] = (byte) 255;
        r[177] = (byte) 0;
        g[177] = (byte) 45;
        b[177] = (byte) 255;
        r[178] = (byte) 0;
        g[178] = (byte) 40;
        b[178] = (byte) 255;
        r[179] = (byte) 0;
        g[179] = (byte) 35;
        b[179] = (byte) 255;
        r[180] = (byte) 0;
        g[180] = (byte) 29;
        b[180] = (byte) 255;
        r[181] = (byte) 0;
        g[181] = (byte) 24;
        b[181] = (byte) 255;
        r[182] = (byte) 0;
        g[182] = (byte) 18;
        b[182] = (byte) 255;
        r[183] = (byte) 0;
        g[183] = (byte) 13;
        b[183] = (byte) 255;
        r[184] = (byte) 0;
        g[184] = (byte) 8;
        b[184] = (byte) 255;
        r[185] = (byte) 0;
        g[185] = (byte) 2;
        b[185] = (byte) 255;
        r[186] = (byte) 2;
        g[186] = (byte) 0;
        b[186] = (byte) 255;
        r[187] = (byte) 7;
        g[187] = (byte) 0;
        b[187] = (byte) 255;
        r[188] = (byte) 18;
        g[188] = (byte) 0;
        b[188] = (byte) 255;
        r[189] = (byte) 24;
        g[189] = (byte) 0;
        b[189] = (byte) 255;
        r[190] = (byte) 29;
        g[190] = (byte) 0;
        b[190] = (byte) 255;
        r[191] = (byte) 34;
        g[191] = (byte) 0;
        b[191] = (byte) 255;
        r[192] = (byte) 40;
        g[192] = (byte) 0;
        b[192] = (byte) 255;
        r[193] = (byte) 45;
        g[193] = (byte) 0;
        b[193] = (byte) 255;
        r[194] = (byte) 50;
        g[194] = (byte) 0;
        b[194] = (byte) 255;
        r[195] = (byte) 56;
        g[195] = (byte) 0;
        b[195] = (byte) 255;
        r[196] = (byte) 61;
        g[196] = (byte) 0;
        b[196] = (byte) 255;
        r[197] = (byte) 67;
        g[197] = (byte) 0;
        b[197] = (byte) 255;
        r[198] = (byte) 72;
        g[198] = (byte) 0;
        b[198] = (byte) 255;
        r[199] = (byte) 77;
        g[199] = (byte) 0;
        b[199] = (byte) 255;
        r[200] = (byte) 83;
        g[200] = (byte) 0;
        b[200] = (byte) 255;
        r[201] = (byte) 88;
        g[201] = (byte) 0;
        b[201] = (byte) 255;
        r[202] = (byte) 93;
        g[202] = (byte) 0;
        b[202] = (byte) 255;
        r[203] = (byte) 104;
        g[203] = (byte) 0;
        b[203] = (byte) 255;
        r[204] = (byte) 110;
        g[204] = (byte) 0;
        b[204] = (byte) 255;
        r[205] = (byte) 115;
        g[205] = (byte) 0;
        b[205] = (byte) 255;
        r[206] = (byte) 120;
        g[206] = (byte) 0;
        b[206] = (byte) 255;
        r[207] = (byte) 126;
        g[207] = (byte) 0;
        b[207] = (byte) 255;
        r[208] = (byte) 131;
        g[208] = (byte) 0;
        b[208] = (byte) 255;
        r[209] = (byte) 136;
        g[209] = (byte) 0;
        b[209] = (byte) 255;
        r[210] = (byte) 142;
        g[210] = (byte) 0;
        b[210] = (byte) 255;
        r[211] = (byte) 147;
        g[211] = (byte) 0;
        b[211] = (byte) 255;
        r[212] = (byte) 153;
        g[212] = (byte) 0;
        b[212] = (byte) 255;
        r[213] = (byte) 158;
        g[213] = (byte) 0;
        b[213] = (byte) 255;
        r[214] = (byte) 163;
        g[214] = (byte) 0;
        b[214] = (byte) 255;
        r[215] = (byte) 169;
        g[215] = (byte) 0;
        b[215] = (byte) 255;
        r[216] = (byte) 174;
        g[216] = (byte) 0;
        b[216] = (byte) 255;
        r[217] = (byte) 180;
        g[217] = (byte) 0;
        b[217] = (byte) 255;
        r[218] = (byte) 190;
        g[218] = (byte) 0;
        b[218] = (byte) 255;
        r[219] = (byte) 196;
        g[219] = (byte) 0;
        b[219] = (byte) 255;
        r[220] = (byte) 201;
        g[220] = (byte) 0;
        b[220] = (byte) 255;
        r[221] = (byte) 206;
        g[221] = (byte) 0;
        b[221] = (byte) 255;
        r[222] = (byte) 212;
        g[222] = (byte) 0;
        b[222] = (byte) 255;
        r[223] = (byte) 217;
        g[223] = (byte) 0;
        b[223] = (byte) 255;
        r[224] = (byte) 223;
        g[224] = (byte) 0;
        b[224] = (byte) 255;
        r[225] = (byte) 228;
        g[225] = (byte) 0;
        b[225] = (byte) 255;
        r[226] = (byte) 233;
        g[226] = (byte) 0;
        b[226] = (byte) 255;
        r[227] = (byte) 239;
        g[227] = (byte) 0;
        b[227] = (byte) 255;
        r[228] = (byte) 244;
        g[228] = (byte) 0;
        b[228] = (byte) 255;
        r[229] = (byte) 249;
        g[229] = (byte) 0;
        b[229] = (byte) 255;
        r[230] = (byte) 255;
        g[230] = (byte) 0;
        b[230] = (byte) 254;
        r[231] = (byte) 255;
        g[231] = (byte) 0;
        b[231] = (byte) 249;
        r[232] = (byte) 255;
        g[232] = (byte) 0;
        b[232] = (byte) 243;
        r[233] = (byte) 255;
        g[233] = (byte) 0;
        b[233] = (byte) 233;
        r[234] = (byte) 255;
        g[234] = (byte) 0;
        b[234] = (byte) 227;
        r[235] = (byte) 255;
        g[235] = (byte) 0;
        b[235] = (byte) 222;
        r[236] = (byte) 255;
        g[236] = (byte) 0;
        b[236] = (byte) 217;
        r[237] = (byte) 255;
        g[237] = (byte) 0;
        b[237] = (byte) 211;
        r[238] = (byte) 255;
        g[238] = (byte) 0;
        b[238] = (byte) 206;
        r[239] = (byte) 255;
        g[239] = (byte) 0;
        b[239] = (byte) 201;

        return new IndexColorModel(8, NB_COLORS, r, g, b);
    }

    /** Returns one gray color model */
    private static IndexColorModel getGrayColorModel(int nbElements)
    {
        final byte[] r = new byte[nbElements];
        final byte[] g = new byte[nbElements];
        final byte[] b = new byte[nbElements];

        final int mult = MAX_COLORS / nbElements;

        for (int i = 0; i < nbElements; i++) {
            byte v = (byte) (i * mult);
            r[i] = v;
            g[i] = v;
            b[i] = v;
        }

        if (nbElements > 16) {
            return new IndexColorModel(8, nbElements, r, g, b);
        }

        return new IndexColorModel(4, nbElements, r, g, b);
    }

    private static byte[][] loadLutFromFile(final String name)
    {

        BufferedReader reader = null;
        try {
            final String path = "lut/" + name;

            final InputStream in = ColorModels.class.getResourceAsStream(path);

            reader = new BufferedReader(new InputStreamReader(in, "US-ASCII"));

            String line;
            StringTokenizer tok;

            // outputs :
            final int LEN = 128;

            final float[] rf = new float[MAX_COLORS];
            final float[] gf = new float[MAX_COLORS];
            final float[] bf = new float[MAX_COLORS];


            for (int i = 0, n = 0; (line = reader.readLine()) != null && n <= LEN; i += 2, n++) {
                tok = new StringTokenizer(line, " ");

                if (tok.countTokens() == 3) {
                    rf[i] = 255f * Float.parseFloat(tok.nextToken());
                    gf[i] = 255f * Float.parseFloat(tok.nextToken());
                    bf[i] = 255f * Float.parseFloat(tok.nextToken());
                }

            }

            for (int i = 1, j = 0, k = 0, size = MAX_COLORS - 2; i < size; i += 2) {
                j = i - 1;
                k = i + 1;
                rf[i] = 0.5f * (rf[j] + rf[k]);
                gf[i] = 0.5f * (gf[j] + gf[k]);
                bf[i] = 0.5f * (bf[j] + bf[k]);
            }

            // special case : color 255 :
            rf[MAX_COLORS - 1] = rf[MAX_COLORS - 2];
            gf[MAX_COLORS - 1] = gf[MAX_COLORS - 2];
            bf[MAX_COLORS - 1] = bf[MAX_COLORS - 2];

            if (FORCE_ZERO) {
                // force to have black color :
                final int REF_COLOR = 4;

                final float rfRef = rf[REF_COLOR];
                final float gfRef = gf[REF_COLOR];
                final float bfRef = bf[REF_COLOR];

                for (int i = REF_COLOR - 1; i >= 0; i--) {
                    rf[i] = rfRef * i / REF_COLOR;
                    gf[i] = gfRef * i / REF_COLOR;
                    bf[i] = bfRef * i / REF_COLOR;
                }
            }

            final byte[] r = new byte[MAX_COLORS];
            final byte[] g = new byte[MAX_COLORS];
            final byte[] b = new byte[MAX_COLORS];

            for (int i = 0; i < MAX_COLORS; i++) {
                r[i] = (byte) rf[i];
                g[i] = (byte) gf[i];
                b[i] = (byte) bf[i];
            }

            return new byte[][]{r, g, b};

        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException("loadLutFromFile failure : ", uee);
        } catch (IOException ioe) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "loadFromFile failure : " + name, ioe);
            }
        } finally {
            FileUtils.closeFile(reader);
        }
        return null;
    }

    private static IndexColorModel loadFromFile(final String name)
    {

        final byte[][] rgb = loadLutFromFile(name);

        if (rgb != null) {
            return new IndexColorModel(8, MAX_COLORS, rgb[0], rgb[1], rgb[2]);
        }
        return null;
    }

    /**
     * List directory contents for a resource folder.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String[] getResourceListing(final Class<?> clazz, final String path) throws URISyntaxException, IOException
    {
        URL dirURL = clazz.getClassLoader().getResource(path);

        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    /**
     * Test code and generate the array of lut file names in the jmcs folder fr/jmmc/mcs/image/lut/
     * @param args unused
     */
    public static void main(String[] args)
    {

        // Prepare the lut file list :
        try {
            final String[] lutFiles = getResourceListing(ColorModels.class, "fr/jmmc/mcs/image/lut/");

            if (lutFiles != null) {
                Arrays.sort(lutFiles);

                final StringBuilder sb = new StringBuilder(512);

                sb.append("private final static String[] LUT_FILES = {\n");
                for (String name : lutFiles) {
                    sb.append("\"").append(name).append("\"").append(", \n");
                }
                final int pos = sb.lastIndexOf(",");
                if (pos != -1) {
                    sb.deleteCharAt(pos);
                }
                sb.append("};");

                logger.severe("lut files :\n" + sb.toString());
            }

        } catch (Exception e) { // main (test)
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "resource listing failure : ", e);
            }
        }

        // test case :
        ColorModels.getDefaultColorModel();
    }
}
