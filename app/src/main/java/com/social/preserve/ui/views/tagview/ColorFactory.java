/*
 * Copyright 2015 lujun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.social.preserve.ui.views.tagview;

import android.graphics.Color;


public class ColorFactory {

    /**
     *                      ============= --border color
     *  background color---||-  Text --||--text color
     *                     =============
     */

    public static final String BG_COLOR_ALPHA = "33";
    public static final String BD_COLOR_ALPHA = "88";

    public static final String RED = "FFB8D7";
    public static final String LIGHTBLUE = "03A9F4";
    public static final String AMBER = "F0BEFB";
    public static final String ORANGE = "FF8292";
    public static final String YELLOW = "FFCF66";
    public static final String LIME = "CDDC39";
    public static final String BLUE = "A7DBFF";
    public static final String INDIGO = "3F51B5";
    public static final String LIGHTGREEN = "9CEB8C";
    public static final String GREY = "9E9E9E";
    public static final String DEEPPURPLE = "BCB0FF";
    public static final String TEAL = "009688";
    public static final String CYAN = "00BCD4";

    public enum PURE_COLOR{CYAN, TEAL}

    public static final int NONE = -1;
    public static final int RANDOM = 0;
    public static final int PURE_CYAN = 1;
    public static final int PURE_TEAL = 2;

    public static final int SHARP666666 = Color.parseColor("#FF666666");
    public static final int SHARP727272 = Color.parseColor("#FF727272");

    public static final String[] COLORS = new String[]{RED, LIGHTBLUE, AMBER, ORANGE, YELLOW,
            LIME, BLUE, INDIGO, LIGHTGREEN, GREY, DEEPPURPLE, TEAL, CYAN};
    public static final String[] COLORS1 = new String[]{RED, AMBER, ORANGE, YELLOW, BLUE, LIGHTGREEN, DEEPPURPLE};

    public static final String[] COLORS_FEMALE = new String[]{"FFCDC2", "A7DBFF", "BCB0FF", "F0BEFB", "FFCF66", "FFB8D7", "A7DBFF",
        "BCB0FF","FF8292","F0BEFB","FF8292","9CEB8C","BCB0FF","F0BEFB","FFB8D7","A7DBFF","FFCF66","A7DBFF","9CEB8C","A7DBFF","FF8292","FFB8D7"};
    public static final String[] COLORS_MALE = new String[]{"FFCDC2","A7DBFF","FFCF66","A7DBFF","BCB0FF","F0BEFB","FFB8D7","FFCF66","9CEB8C","9CEB8C","BCB0FF",
        "FF8292","A7DBFF","F0BEFB","FFB8D7","FFCF66","A7DBFF","9CEB8C"};
    public static int[] onRandomBuild(){
        int random = (int)(Math.random() * COLORS1.length);
        int bgColor = Color.parseColor("#FF"  + COLORS1[random]);
        int bdColor= Color.parseColor("#00000000");
//        int bdColor = Color.parseColor("#" + BD_COLOR_ALPHA + COLORS1[random]);
        int tColor = Color.parseColor("#FFFFFFFF");
        int tColor2 = Color.parseColor("#00000000");
        return new int[]{bgColor, bdColor, tColor, tColor2};
    }

    public static int[] onPureBuild(PURE_COLOR type){
        String color = type == PURE_COLOR.CYAN ? CYAN : TEAL;
        int bgColor = Color.parseColor("#" + BG_COLOR_ALPHA + color);
        int bdColor = Color.parseColor("#" + BD_COLOR_ALPHA + color);
        int tColor = SHARP727272;
        int tColor2 = SHARP666666;
        return new int[]{bgColor, bdColor, tColor, tColor2};
    }

}
