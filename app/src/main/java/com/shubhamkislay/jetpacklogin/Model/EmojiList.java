package com.shubhamkislay.jetpacklogin.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmojiList {

    List<String> emojiList;


    public EmojiList() {
        emojiList = new ArrayList<>();
        addEmojisToList();

    }

    public void addEmojisToList() {

        //smilies startU+1F607
        emojiList.add(getEmojiByUnicode(0x263A));
        emojiList.add(getEmojiByUnicode(0x1F601));
        emojiList.add(getEmojiByUnicode(0x1F602));
        emojiList.add(getEmojiByUnicode(0x1F603));
        emojiList.add(getEmojiByUnicode(0x1F643));
        emojiList.add(getEmojiByUnicode(0x1F604));
        emojiList.add(getEmojiByUnicode(0x1F607));
        emojiList.add(getEmojiByUnicode(0x1F605));
        emojiList.add(getEmojiByUnicode(0x1F606));
        emojiList.add(getEmojiByUnicode(0x1F609));
        emojiList.add(getEmojiByUnicode(0x1F92A));
        emojiList.add(getEmojiByUnicode(0x1F60A));
        emojiList.add(getEmojiByUnicode(0x1F60B));
        emojiList.add(getEmojiByUnicode(0x1F911));
        emojiList.add(getEmojiByUnicode(0x1F617));

        emojiList.add(getEmojiByUnicode(0x1F97A));


        emojiList.add(getEmojiByUnicode(0x1F92D));
        emojiList.add(getEmojiByUnicode(0x1F917));
        emojiList.add(getEmojiByUnicode(0x1F92D));
        emojiList.add(getEmojiByUnicode(0x1F92B));
        emojiList.add(getEmojiByUnicode(0x1F914));
        emojiList.add(getEmojiByUnicode(0x1F910));

        emojiList.add(getEmojiByUnicode(0x1F92C));
        emojiList.add(getEmojiByUnicode(0x1F928));
        emojiList.add(getEmojiByUnicode(0x1F610));
        emojiList.add(getEmojiByUnicode(0x1F611));
        emojiList.add(getEmojiByUnicode(0x1F636));
        emojiList.add(getEmojiByUnicode(0x1F644));
        emojiList.add(getEmojiByUnicode(0x1F62C));
        emojiList.add(getEmojiByUnicode(0x1F925));
        emojiList.add(getEmojiByUnicode(0x1F924));
        emojiList.add(getEmojiByUnicode(0x1F634));
        emojiList.add(getEmojiByUnicode(0x1F912));
        emojiList.add(getEmojiByUnicode(0x1F915));
        emojiList.add(getEmojiByUnicode(0x1F922));
        emojiList.add(getEmojiByUnicode(0x1F92E));
        emojiList.add(getEmojiByUnicode(0x1F927));
        emojiList.add(getEmojiByUnicode(0x1F975));
        emojiList.add(getEmojiByUnicode(0x1F976));
        emojiList.add(getEmojiByUnicode(0x1F974));
        emojiList.add(getEmojiByUnicode(0x1F92F));
        emojiList.add(getEmojiByUnicode(0x1F920));
        emojiList.add(getEmojiByUnicode(0x1F973));
        emojiList.add(getEmojiByUnicode(0x1F60E));
        emojiList.add(getEmojiByUnicode(0x1F913));
        emojiList.add(getEmojiByUnicode(0x1F9D0));
        emojiList.add(getEmojiByUnicode(0x1F608));
        emojiList.add(getEmojiByUnicode(0x1F47F));
        emojiList.add(getEmojiByUnicode(0x1F4A9));
        emojiList.add(getEmojiByUnicode(0x1F921));
        emojiList.add(getEmojiByUnicode(0x1F4AF));
        emojiList.add(getEmojiByUnicode(0x1F919));
        emojiList.add(getEmojiByUnicode(0x1F918));

        emojiList.add(getEmojiByUnicode(0x1F595));



/*        emojiList.add(getEmojiByUnicode(0x1F937));
        emojiList.add(getEmojiByUnicode(0x2642));
        emojiList.add(getEmojiByUnicode(0x200D));
        emojiList.add(getEmojiByUnicode(0x1F926));
        emojiList.add(getEmojiByUnicode(0xFE0F));
        emojiList.add(getEmojiByUnicode(0x1F64B));*/

        emojiList.add(getEmojiByUnicode(0x1F468));
        emojiList.add(getEmojiByUnicode(0x1F469));







        emojiList.add(getEmojiByUnicode(0x1F60C));
        emojiList.add(getEmojiByUnicode(0x1F60D));
        emojiList.add(getEmojiByUnicode(0x1F60F));
        emojiList.add(getEmojiByUnicode(0x1F612));
        emojiList.add(getEmojiByUnicode(0x1F613));
        emojiList.add(getEmojiByUnicode(0x1F614));
        emojiList.add(getEmojiByUnicode(0x1F616));
        emojiList.add(getEmojiByUnicode(0x1F618));
        emojiList.add(getEmojiByUnicode(0x1F61A));
        emojiList.add(getEmojiByUnicode(0x1F61C));
        emojiList.add(getEmojiByUnicode(0x1F61D));
        emojiList.add(getEmojiByUnicode(0x1F61E));
        emojiList.add(getEmojiByUnicode(0x1F620));
        emojiList.add(getEmojiByUnicode(0x1F621));
        emojiList.add(getEmojiByUnicode(0x1F622));
        emojiList.add(getEmojiByUnicode(0x1F623));
        emojiList.add(getEmojiByUnicode(0x1F624));
        emojiList.add(getEmojiByUnicode(0x1F625));
        emojiList.add(getEmojiByUnicode(0x1F628));
        emojiList.add(getEmojiByUnicode(0x1F629));
        emojiList.add(getEmojiByUnicode(0x1F62A));
        emojiList.add(getEmojiByUnicode(0x1F62B));
        emojiList.add(getEmojiByUnicode(0x1F62D));
        emojiList.add(getEmojiByUnicode(0x1F630));
        emojiList.add(getEmojiByUnicode(0x1F631));
        emojiList.add(getEmojiByUnicode(0x1F632));
        emojiList.add(getEmojiByUnicode(0x1F633));
        emojiList.add(getEmojiByUnicode(0x1F635));
        emojiList.add(getEmojiByUnicode(0x1F637));
        emojiList.add(getEmojiByUnicode(0x1F638));
        emojiList.add(getEmojiByUnicode(0x1F639));
        emojiList.add(getEmojiByUnicode(0x1F63A));
        emojiList.add(getEmojiByUnicode(0x1F63B));
        emojiList.add(getEmojiByUnicode(0x1F63C));
        emojiList.add(getEmojiByUnicode(0x1F63D));
        emojiList.add(getEmojiByUnicode(0x1F63E));
        emojiList.add(getEmojiByUnicode(0x1F63F));
        emojiList.add(getEmojiByUnicode(0x1F640));
        emojiList.add(getEmojiByUnicode(0x1F645));
        emojiList.add(getEmojiByUnicode(0x1F646));
        emojiList.add(getEmojiByUnicode(0x1F647));
        emojiList.add(getEmojiByUnicode(0x1F648));
        emojiList.add(getEmojiByUnicode(0x1F649));
        emojiList.add(getEmojiByUnicode(0x1F64A));
        emojiList.add(getEmojiByUnicode(0x1F64B));
        emojiList.add(getEmojiByUnicode(0x1F64D));
        emojiList.add(getEmojiByUnicode(0x1F64E));
        emojiList.add(getEmojiByUnicode(0x1F64F));
        emojiList.add(getEmojiByUnicode(0x1F64C));
        emojiList.add(getEmojiByUnicode(0x261D));
        emojiList.add(getEmojiByUnicode(0x270A));
        emojiList.add(getEmojiByUnicode(0x270B));
        emojiList.add(getEmojiByUnicode(0x270C));

        //similies end

        emojiList.add(getEmojiByUnicode(0x2734));
        emojiList.add(getEmojiByUnicode(0x2744));
        emojiList.add(getEmojiByUnicode(0x2747));
        emojiList.add(getEmojiByUnicode(0x00A9));
        emojiList.add(getEmojiByUnicode(0x00AE));
        emojiList.add(getEmojiByUnicode(0x203C));
        emojiList.add(getEmojiByUnicode(0x2049));
        emojiList.add(getEmojiByUnicode(0x2122));
        emojiList.add(getEmojiByUnicode(0x2139));
        emojiList.add(getEmojiByUnicode(0x2194));
        emojiList.add(getEmojiByUnicode(0x2195));
        emojiList.add(getEmojiByUnicode(0x2196));
        emojiList.add(getEmojiByUnicode(0x2197));
        emojiList.add(getEmojiByUnicode(0x2198));
        emojiList.add(getEmojiByUnicode(0x2199));
        emojiList.add(getEmojiByUnicode(0x21A9));
        emojiList.add(getEmojiByUnicode(0x21AA));
        emojiList.add(getEmojiByUnicode(0x231A));
        emojiList.add(getEmojiByUnicode(0x231B));
        emojiList.add(getEmojiByUnicode(0x23E9));
        emojiList.add(getEmojiByUnicode(0x23EA));
        emojiList.add(getEmojiByUnicode(0x23EB));
        emojiList.add(getEmojiByUnicode(0x23EC));
        emojiList.add(getEmojiByUnicode(0x23F0));
        emojiList.add(getEmojiByUnicode(0x23F3));
        emojiList.add(getEmojiByUnicode(0x24C2));
        emojiList.add(getEmojiByUnicode(0x25AA));
        emojiList.add(getEmojiByUnicode(0x25AB));
        emojiList.add(getEmojiByUnicode(0x25B6));
        emojiList.add(getEmojiByUnicode(0x25C0));
        emojiList.add(getEmojiByUnicode(0x25FB));
        emojiList.add(getEmojiByUnicode(0x25FC));
        emojiList.add(getEmojiByUnicode(0x25FD));
        emojiList.add(getEmojiByUnicode(0x25FE));
        emojiList.add(getEmojiByUnicode(0x2600));
        emojiList.add(getEmojiByUnicode(0x2601));
        emojiList.add(getEmojiByUnicode(0x260E));
        emojiList.add(getEmojiByUnicode(0x2611));
        emojiList.add(getEmojiByUnicode(0x2614));
        emojiList.add(getEmojiByUnicode(0x2615));
        emojiList.add(getEmojiByUnicode(0x2648));
        emojiList.add(getEmojiByUnicode(0x2649));
        emojiList.add(getEmojiByUnicode(0x264A));
        emojiList.add(getEmojiByUnicode(0x264B));
        emojiList.add(getEmojiByUnicode(0x264C));
        emojiList.add(getEmojiByUnicode(0x264D));
        emojiList.add(getEmojiByUnicode(0x264E));
        emojiList.add(getEmojiByUnicode(0x264F));
        emojiList.add(getEmojiByUnicode(0x2650));
        emojiList.add(getEmojiByUnicode(0x2651));
        emojiList.add(getEmojiByUnicode(0x2652));
        emojiList.add(getEmojiByUnicode(0x2653));
        emojiList.add(getEmojiByUnicode(0x2660));
        emojiList.add(getEmojiByUnicode(0x2663));
        emojiList.add(getEmojiByUnicode(0x2665));
        emojiList.add(getEmojiByUnicode(0x2666));
        emojiList.add(getEmojiByUnicode(0x2668));
        emojiList.add(getEmojiByUnicode(0x267B));
        emojiList.add(getEmojiByUnicode(0x267F));
        emojiList.add(getEmojiByUnicode(0x2693));
        emojiList.add(getEmojiByUnicode(0x26A0));
        emojiList.add(getEmojiByUnicode(0x26A1));
        emojiList.add(getEmojiByUnicode(0x26AA));
        emojiList.add(getEmojiByUnicode(0x26AB));
        emojiList.add(getEmojiByUnicode(0x26BD));
        emojiList.add(getEmojiByUnicode(0x26BE));
        emojiList.add(getEmojiByUnicode(0x26C4));
        emojiList.add(getEmojiByUnicode(0x26C5));
        emojiList.add(getEmojiByUnicode(0x26CE));
        emojiList.add(getEmojiByUnicode(0x26D4));
        emojiList.add(getEmojiByUnicode(0x26EA));
        emojiList.add(getEmojiByUnicode(0x26F2));
        emojiList.add(getEmojiByUnicode(0x26F3));
        emojiList.add(getEmojiByUnicode(0x26F5));
        emojiList.add(getEmojiByUnicode(0x26FA));
        emojiList.add(getEmojiByUnicode(0x26FD));
        emojiList.add(getEmojiByUnicode(0x2702));
        emojiList.add(getEmojiByUnicode(0x2705));
        emojiList.add(getEmojiByUnicode(0x2708));
        emojiList.add(getEmojiByUnicode(0x2709));
        emojiList.add(getEmojiByUnicode(0x270F));
        emojiList.add(getEmojiByUnicode(0x2712));
        emojiList.add(getEmojiByUnicode(0x2714));
        emojiList.add(getEmojiByUnicode(0x2716));
        emojiList.add(getEmojiByUnicode(0x2728));
        emojiList.add(getEmojiByUnicode(0x2733));
        emojiList.add(getEmojiByUnicode(0x274C));
        emojiList.add(getEmojiByUnicode(0x274E));
        emojiList.add(getEmojiByUnicode(0x2753));
        emojiList.add(getEmojiByUnicode(0x2754));
        emojiList.add(getEmojiByUnicode(0x2755));
        emojiList.add(getEmojiByUnicode(0x2757));
        emojiList.add(getEmojiByUnicode(0x2764));
        emojiList.add(getEmojiByUnicode(0x2795));
        emojiList.add(getEmojiByUnicode(0x2796));
        emojiList.add(getEmojiByUnicode(0x2797));
        emojiList.add(getEmojiByUnicode(0x27A1));
        emojiList.add(getEmojiByUnicode(0x27B0));
        emojiList.add(getEmojiByUnicode(0x2934));
        emojiList.add(getEmojiByUnicode(0x2935));
        emojiList.add(getEmojiByUnicode(0x2B05));
        emojiList.add(getEmojiByUnicode(0x2B06));
        emojiList.add(getEmojiByUnicode(0x2B07));
        emojiList.add(getEmojiByUnicode(0x2B1B));
        emojiList.add(getEmojiByUnicode(0x2B1C));
        emojiList.add(getEmojiByUnicode(0x2B50));
        emojiList.add(getEmojiByUnicode(0x2B55));
        emojiList.add(getEmojiByUnicode(0x3030));
        emojiList.add(getEmojiByUnicode(0x303D));
        emojiList.add(getEmojiByUnicode(0x3297));
        emojiList.add(getEmojiByUnicode(0x3299));
        emojiList.add(getEmojiByUnicode(0x1F004));
        emojiList.add(getEmojiByUnicode(0x1F0CF));
        emojiList.add(getEmojiByUnicode(0x1F170));
        emojiList.add(getEmojiByUnicode(0x1F171));
        emojiList.add(getEmojiByUnicode(0x1F17E));
        emojiList.add(getEmojiByUnicode(0x1F17F));
        emojiList.add(getEmojiByUnicode(0x1F18E));
        emojiList.add(getEmojiByUnicode(0x1F191));
        emojiList.add(getEmojiByUnicode(0x1F192));
        emojiList.add(getEmojiByUnicode(0x1F193));
        emojiList.add(getEmojiByUnicode(0x1F194));
        emojiList.add(getEmojiByUnicode(0x1F195));
        emojiList.add(getEmojiByUnicode(0x1F196));
        emojiList.add(getEmojiByUnicode(0x1F197));
        emojiList.add(getEmojiByUnicode(0x1F198));
        emojiList.add(getEmojiByUnicode(0x1F199));
        emojiList.add(getEmojiByUnicode(0x1F19A));
        emojiList.add(getEmojiByUnicode(0x1F1E8));
        emojiList.add(getEmojiByUnicode(0x1F1F3));
        emojiList.add(getEmojiByUnicode(0x1F1E9));
        emojiList.add(getEmojiByUnicode(0x1F1EA));
        emojiList.add(getEmojiByUnicode(0x1F1EA));
        emojiList.add(getEmojiByUnicode(0x1F1F8));
        emojiList.add(getEmojiByUnicode(0x1F1EB));
        emojiList.add(getEmojiByUnicode(0x1F1F7));
        emojiList.add(getEmojiByUnicode(0x1F1EC));
        emojiList.add(getEmojiByUnicode(0x1F1E7));
        emojiList.add(getEmojiByUnicode(0x1F1EE));
        emojiList.add(getEmojiByUnicode(0x1F1F9));
        emojiList.add(getEmojiByUnicode(0x1F1EF));
        emojiList.add(getEmojiByUnicode(0x1F1F5));
        emojiList.add(getEmojiByUnicode(0x1F1F0));
        emojiList.add(getEmojiByUnicode(0x1F1F7));
        emojiList.add(getEmojiByUnicode(0x1F1F7));
        emojiList.add(getEmojiByUnicode(0x1F1FA));
        emojiList.add(getEmojiByUnicode(0x1F1FA));
        emojiList.add(getEmojiByUnicode(0x1F1F8));
        emojiList.add(getEmojiByUnicode(0x1F201));
        emojiList.add(getEmojiByUnicode(0x1F202));
        emojiList.add(getEmojiByUnicode(0x1F21A));
        emojiList.add(getEmojiByUnicode(0x1F22F));
        emojiList.add(getEmojiByUnicode(0x1F232));
        emojiList.add(getEmojiByUnicode(0x1F233));
        emojiList.add(getEmojiByUnicode(0x1F234));
        emojiList.add(getEmojiByUnicode(0x1F235));
        emojiList.add(getEmojiByUnicode(0x1F236));
        emojiList.add(getEmojiByUnicode(0x1F237));
        emojiList.add(getEmojiByUnicode(0x1F238));
        emojiList.add(getEmojiByUnicode(0x1F239));
        emojiList.add(getEmojiByUnicode(0x1F23A));
        emojiList.add(getEmojiByUnicode(0x1F250));
        emojiList.add(getEmojiByUnicode(0x1F251));
        emojiList.add(getEmojiByUnicode(0x1F300));
        emojiList.add(getEmojiByUnicode(0x1F301));
        emojiList.add(getEmojiByUnicode(0x1F302));
        emojiList.add(getEmojiByUnicode(0x1F303));
        emojiList.add(getEmojiByUnicode(0x1F304));
        emojiList.add(getEmojiByUnicode(0x1F305));
        emojiList.add(getEmojiByUnicode(0x1F306));
        emojiList.add(getEmojiByUnicode(0x1F307));
        emojiList.add(getEmojiByUnicode(0x1F308));
        emojiList.add(getEmojiByUnicode(0x1F309));
        emojiList.add(getEmojiByUnicode(0x1F30A));
        emojiList.add(getEmojiByUnicode(0x1F30B));
        emojiList.add(getEmojiByUnicode(0x1F30C));
        emojiList.add(getEmojiByUnicode(0x1F30F));
        emojiList.add(getEmojiByUnicode(0x1F311));
        emojiList.add(getEmojiByUnicode(0x1F313));
        emojiList.add(getEmojiByUnicode(0x1F314));
        emojiList.add(getEmojiByUnicode(0x1F315));
        emojiList.add(getEmojiByUnicode(0x1F319));
        emojiList.add(getEmojiByUnicode(0x1F31B));
        emojiList.add(getEmojiByUnicode(0x1F31F));
        emojiList.add(getEmojiByUnicode(0x1F320));
        emojiList.add(getEmojiByUnicode(0x1F330));
        emojiList.add(getEmojiByUnicode(0x1F331));
        emojiList.add(getEmojiByUnicode(0x1F334));
        emojiList.add(getEmojiByUnicode(0x1F335));
        emojiList.add(getEmojiByUnicode(0x1F337));
        emojiList.add(getEmojiByUnicode(0x1F338));
        emojiList.add(getEmojiByUnicode(0x1F339));
        emojiList.add(getEmojiByUnicode(0x1F33A));
        emojiList.add(getEmojiByUnicode(0x1F33B));
        emojiList.add(getEmojiByUnicode(0x1F33C));
        emojiList.add(getEmojiByUnicode(0x1F33D));
        emojiList.add(getEmojiByUnicode(0x1F33E));
        emojiList.add(getEmojiByUnicode(0x1F33F));
        emojiList.add(getEmojiByUnicode(0x1F340));
        emojiList.add(getEmojiByUnicode(0x1F341));
        emojiList.add(getEmojiByUnicode(0x1F342));
        emojiList.add(getEmojiByUnicode(0x1F343));
        emojiList.add(getEmojiByUnicode(0x1F344));
        emojiList.add(getEmojiByUnicode(0x1F345));
        emojiList.add(getEmojiByUnicode(0x1F346));
        emojiList.add(getEmojiByUnicode(0x1F347));
        emojiList.add(getEmojiByUnicode(0x1F348));
        emojiList.add(getEmojiByUnicode(0x1F349));
        emojiList.add(getEmojiByUnicode(0x1F34A));
        emojiList.add(getEmojiByUnicode(0x1F34C));
        emojiList.add(getEmojiByUnicode(0x1F34D));
        emojiList.add(getEmojiByUnicode(0x1F34E));
        emojiList.add(getEmojiByUnicode(0x1F34F));
        emojiList.add(getEmojiByUnicode(0x1F351));
        emojiList.add(getEmojiByUnicode(0x1F352));
        emojiList.add(getEmojiByUnicode(0x1F353));
        emojiList.add(getEmojiByUnicode(0x1F354));
        emojiList.add(getEmojiByUnicode(0x1F355));
        emojiList.add(getEmojiByUnicode(0x1F356));
        emojiList.add(getEmojiByUnicode(0x1F357));
        emojiList.add(getEmojiByUnicode(0x1F358));
        emojiList.add(getEmojiByUnicode(0x1F359));
        emojiList.add(getEmojiByUnicode(0x1F35A));
        emojiList.add(getEmojiByUnicode(0x1F35B));
        emojiList.add(getEmojiByUnicode(0x1F35C));
        emojiList.add(getEmojiByUnicode(0x1F35D));
        emojiList.add(getEmojiByUnicode(0x1F35E));
        emojiList.add(getEmojiByUnicode(0x1F35F));
        emojiList.add(getEmojiByUnicode(0x1F360));
        emojiList.add(getEmojiByUnicode(0x1F361));
        emojiList.add(getEmojiByUnicode(0x1F362));
        emojiList.add(getEmojiByUnicode(0x1F363));
        emojiList.add(getEmojiByUnicode(0x1F364));
        emojiList.add(getEmojiByUnicode(0x1F365));
        emojiList.add(getEmojiByUnicode(0x1F366));
        emojiList.add(getEmojiByUnicode(0x1F367));
        emojiList.add(getEmojiByUnicode(0x1F368));
        emojiList.add(getEmojiByUnicode(0x1F369));
        emojiList.add(getEmojiByUnicode(0x1F36A));
        emojiList.add(getEmojiByUnicode(0x1F36B));
        emojiList.add(getEmojiByUnicode(0x1F36C));
        emojiList.add(getEmojiByUnicode(0x1F36D));
        emojiList.add(getEmojiByUnicode(0x1F36E));
        emojiList.add(getEmojiByUnicode(0x1F36F));
        emojiList.add(getEmojiByUnicode(0x1F370));
        emojiList.add(getEmojiByUnicode(0x1F371));
        emojiList.add(getEmojiByUnicode(0x1F372));
        emojiList.add(getEmojiByUnicode(0x1F373));
        emojiList.add(getEmojiByUnicode(0x1F374));
        emojiList.add(getEmojiByUnicode(0x1F375));
        emojiList.add(getEmojiByUnicode(0x1F376));
        emojiList.add(getEmojiByUnicode(0x1F377));
        emojiList.add(getEmojiByUnicode(0x1F378));
        emojiList.add(getEmojiByUnicode(0x1F379));
        emojiList.add(getEmojiByUnicode(0x1F37A));
        emojiList.add(getEmojiByUnicode(0x1F37B));
        emojiList.add(getEmojiByUnicode(0x1F380));
        emojiList.add(getEmojiByUnicode(0x1F381));
        emojiList.add(getEmojiByUnicode(0x1F382));
        emojiList.add(getEmojiByUnicode(0x1F383));
        emojiList.add(getEmojiByUnicode(0x1F384));
        emojiList.add(getEmojiByUnicode(0x1F385));
        emojiList.add(getEmojiByUnicode(0x1F386));
        emojiList.add(getEmojiByUnicode(0x1F387));
        emojiList.add(getEmojiByUnicode(0x1F388));
        emojiList.add(getEmojiByUnicode(0x1F389));
        emojiList.add(getEmojiByUnicode(0x1F38A));
        emojiList.add(getEmojiByUnicode(0x1F38B));
        emojiList.add(getEmojiByUnicode(0x1F38C));
        emojiList.add(getEmojiByUnicode(0x1F38D));
        emojiList.add(getEmojiByUnicode(0x1F38E));
        emojiList.add(getEmojiByUnicode(0x1F38F));
        emojiList.add(getEmojiByUnicode(0x1F390));
        emojiList.add(getEmojiByUnicode(0x1F391));
        emojiList.add(getEmojiByUnicode(0x1F392));
        emojiList.add(getEmojiByUnicode(0x1F393));
        emojiList.add(getEmojiByUnicode(0x1F3A0));
        emojiList.add(getEmojiByUnicode(0x1F3A1));
        emojiList.add(getEmojiByUnicode(0x1F3A2));
        emojiList.add(getEmojiByUnicode(0x1F3A3));
        emojiList.add(getEmojiByUnicode(0x1F3A4));
        emojiList.add(getEmojiByUnicode(0x1F3A5));
        emojiList.add(getEmojiByUnicode(0x1F3A6));
        emojiList.add(getEmojiByUnicode(0x1F3A7));
        emojiList.add(getEmojiByUnicode(0x1F3A8));
        emojiList.add(getEmojiByUnicode(0x1F3A9));
        emojiList.add(getEmojiByUnicode(0x1F3AA));
        emojiList.add(getEmojiByUnicode(0x1F3AB));
        emojiList.add(getEmojiByUnicode(0x1F3AC));
        emojiList.add(getEmojiByUnicode(0x1F3AD));
        emojiList.add(getEmojiByUnicode(0x1F3AE));
        emojiList.add(getEmojiByUnicode(0x1F3AF));
        emojiList.add(getEmojiByUnicode(0x1F3B0));
        emojiList.add(getEmojiByUnicode(0x1F3B1));
        emojiList.add(getEmojiByUnicode(0x1F3B2));
        emojiList.add(getEmojiByUnicode(0x1F3B3));
        emojiList.add(getEmojiByUnicode(0x1F3B4));
        emojiList.add(getEmojiByUnicode(0x1F3B5));
        emojiList.add(getEmojiByUnicode(0x1F3B6));
        emojiList.add(getEmojiByUnicode(0x1F3B7));
        emojiList.add(getEmojiByUnicode(0x1F3B8));
        emojiList.add(getEmojiByUnicode(0x1F3B9));
        emojiList.add(getEmojiByUnicode(0x1F3BA));
        emojiList.add(getEmojiByUnicode(0x1F3BB));
        emojiList.add(getEmojiByUnicode(0x1F3BC));
        emojiList.add(getEmojiByUnicode(0x1F3BD));
        emojiList.add(getEmojiByUnicode(0x1F3BE));
        emojiList.add(getEmojiByUnicode(0x1F3BF));
        emojiList.add(getEmojiByUnicode(0x1F3C0));
        emojiList.add(getEmojiByUnicode(0x1F3C1));
        emojiList.add(getEmojiByUnicode(0x1F3C2));
        emojiList.add(getEmojiByUnicode(0x1F3C3));
        emojiList.add(getEmojiByUnicode(0x1F3C4));
        emojiList.add(getEmojiByUnicode(0x1F3C6));
        emojiList.add(getEmojiByUnicode(0x1F3C8));
        emojiList.add(getEmojiByUnicode(0x1F3CA));
        emojiList.add(getEmojiByUnicode(0x1F3E0));
        emojiList.add(getEmojiByUnicode(0x1F3E1));
        emojiList.add(getEmojiByUnicode(0x1F3E2));
        emojiList.add(getEmojiByUnicode(0x1F3E3));
        emojiList.add(getEmojiByUnicode(0x1F3E5));
        emojiList.add(getEmojiByUnicode(0x1F3E6));
        emojiList.add(getEmojiByUnicode(0x1F3E7));
        emojiList.add(getEmojiByUnicode(0x1F3E8));
        emojiList.add(getEmojiByUnicode(0x1F3E9));
        emojiList.add(getEmojiByUnicode(0x1F3EA));
        emojiList.add(getEmojiByUnicode(0x1F3EB));
        emojiList.add(getEmojiByUnicode(0x1F3EC));
        emojiList.add(getEmojiByUnicode(0x1F3ED));
        emojiList.add(getEmojiByUnicode(0x1F3EE));
        emojiList.add(getEmojiByUnicode(0x1F3EF));
        emojiList.add(getEmojiByUnicode(0x1F3F0));
        emojiList.add(getEmojiByUnicode(0x1F40C));
        emojiList.add(getEmojiByUnicode(0x1F40D));
        emojiList.add(getEmojiByUnicode(0x1F40E));
        emojiList.add(getEmojiByUnicode(0x1F411));
        emojiList.add(getEmojiByUnicode(0x1F412));
        emojiList.add(getEmojiByUnicode(0x1F414));
        emojiList.add(getEmojiByUnicode(0x1F417));
        emojiList.add(getEmojiByUnicode(0x1F418));
        emojiList.add(getEmojiByUnicode(0x1F419));
        emojiList.add(getEmojiByUnicode(0x1F41A));
        emojiList.add(getEmojiByUnicode(0x1F41B));
        emojiList.add(getEmojiByUnicode(0x1F41C));
        emojiList.add(getEmojiByUnicode(0x1F41D));
        emojiList.add(getEmojiByUnicode(0x1F41E));
        emojiList.add(getEmojiByUnicode(0x1F41F));
        emojiList.add(getEmojiByUnicode(0x1F420));
        emojiList.add(getEmojiByUnicode(0x1F421));
        emojiList.add(getEmojiByUnicode(0x1F422));
        emojiList.add(getEmojiByUnicode(0x1F423));
        emojiList.add(getEmojiByUnicode(0x1F424));
        emojiList.add(getEmojiByUnicode(0x1F425));
        emojiList.add(getEmojiByUnicode(0x1F426));
        emojiList.add(getEmojiByUnicode(0x1F427));
        emojiList.add(getEmojiByUnicode(0x1F428));
        emojiList.add(getEmojiByUnicode(0x1F429));
        emojiList.add(getEmojiByUnicode(0x1F42B));
        emojiList.add(getEmojiByUnicode(0x1F42C));
        emojiList.add(getEmojiByUnicode(0x1F42D));
        emojiList.add(getEmojiByUnicode(0x1F42E));
        emojiList.add(getEmojiByUnicode(0x1F42F));
        emojiList.add(getEmojiByUnicode(0x1F430));
        emojiList.add(getEmojiByUnicode(0x1F431));
        emojiList.add(getEmojiByUnicode(0x1F432));
        emojiList.add(getEmojiByUnicode(0x1F433));
        emojiList.add(getEmojiByUnicode(0x1F434));
        emojiList.add(getEmojiByUnicode(0x1F435));
        emojiList.add(getEmojiByUnicode(0x1F436));
        emojiList.add(getEmojiByUnicode(0x1F437));
        emojiList.add(getEmojiByUnicode(0x1F438));
        emojiList.add(getEmojiByUnicode(0x1F439));
        emojiList.add(getEmojiByUnicode(0x1F43A));
        emojiList.add(getEmojiByUnicode(0x1F43B));
        emojiList.add(getEmojiByUnicode(0x1F43C));
        emojiList.add(getEmojiByUnicode(0x1F43D));
        emojiList.add(getEmojiByUnicode(0x1F43E));
        emojiList.add(getEmojiByUnicode(0x1F440));
        emojiList.add(getEmojiByUnicode(0x1F442));
        emojiList.add(getEmojiByUnicode(0x1F443));
        emojiList.add(getEmojiByUnicode(0x1F444));
        emojiList.add(getEmojiByUnicode(0x1F445));
        emojiList.add(getEmojiByUnicode(0x1F446));
        emojiList.add(getEmojiByUnicode(0x1F447));
        emojiList.add(getEmojiByUnicode(0x1F448));
        emojiList.add(getEmojiByUnicode(0x1F449));
        emojiList.add(getEmojiByUnicode(0x1F44A));
        emojiList.add(getEmojiByUnicode(0x1F44B));
        emojiList.add(getEmojiByUnicode(0x1F44C));
        emojiList.add(getEmojiByUnicode(0x1F44D));
        emojiList.add(getEmojiByUnicode(0x1F44E));
        emojiList.add(getEmojiByUnicode(0x1F44F));
        emojiList.add(getEmojiByUnicode(0x1F450));
        emojiList.add(getEmojiByUnicode(0x1F451));
        emojiList.add(getEmojiByUnicode(0x1F452));
        emojiList.add(getEmojiByUnicode(0x1F453));
        emojiList.add(getEmojiByUnicode(0x1F454));
        emojiList.add(getEmojiByUnicode(0x1F455));
        emojiList.add(getEmojiByUnicode(0x1F456));
        emojiList.add(getEmojiByUnicode(0x1F457));
        emojiList.add(getEmojiByUnicode(0x1F458));
        emojiList.add(getEmojiByUnicode(0x1F459));
        emojiList.add(getEmojiByUnicode(0x1F45A));
        emojiList.add(getEmojiByUnicode(0x1F45B));
        emojiList.add(getEmojiByUnicode(0x1F45C));
        emojiList.add(getEmojiByUnicode(0x1F45D));
        emojiList.add(getEmojiByUnicode(0x1F45E));
        emojiList.add(getEmojiByUnicode(0x1F45F));
        emojiList.add(getEmojiByUnicode(0x1F460));
        emojiList.add(getEmojiByUnicode(0x1F461));
        emojiList.add(getEmojiByUnicode(0x1F462));
        emojiList.add(getEmojiByUnicode(0x1F463));
        emojiList.add(getEmojiByUnicode(0x1F464));
        emojiList.add(getEmojiByUnicode(0x1F466));
        emojiList.add(getEmojiByUnicode(0x1F467));
        emojiList.add(getEmojiByUnicode(0x1F468));
        emojiList.add(getEmojiByUnicode(0x1F469));
        emojiList.add(getEmojiByUnicode(0x1F46A));
        emojiList.add(getEmojiByUnicode(0x1F46B));
        emojiList.add(getEmojiByUnicode(0x1F46E));
        emojiList.add(getEmojiByUnicode(0x1F46F));
        emojiList.add(getEmojiByUnicode(0x1F470));
        emojiList.add(getEmojiByUnicode(0x1F471));
        emojiList.add(getEmojiByUnicode(0x1F472));
        emojiList.add(getEmojiByUnicode(0x1F473));
        emojiList.add(getEmojiByUnicode(0x1F474));
        emojiList.add(getEmojiByUnicode(0x1F475));
        emojiList.add(getEmojiByUnicode(0x1F476));
        emojiList.add(getEmojiByUnicode(0x1F477));
        emojiList.add(getEmojiByUnicode(0x1F478));
        emojiList.add(getEmojiByUnicode(0x1F479));
        emojiList.add(getEmojiByUnicode(0x1F47A));
        emojiList.add(getEmojiByUnicode(0x1F47B));
        emojiList.add(getEmojiByUnicode(0x1F47C));
        emojiList.add(getEmojiByUnicode(0x1F47D));
        emojiList.add(getEmojiByUnicode(0x1F47E));
        emojiList.add(getEmojiByUnicode(0x1F47F));
        emojiList.add(getEmojiByUnicode(0x1F480));
        emojiList.add(getEmojiByUnicode(0x1F481));
        emojiList.add(getEmojiByUnicode(0x1F482));
        emojiList.add(getEmojiByUnicode(0x1F483));
        emojiList.add(getEmojiByUnicode(0x1F484));
        emojiList.add(getEmojiByUnicode(0x1F485));
        emojiList.add(getEmojiByUnicode(0x1F486));
        emojiList.add(getEmojiByUnicode(0x1F487));
        emojiList.add(getEmojiByUnicode(0x1F488));
        emojiList.add(getEmojiByUnicode(0x1F489));
        emojiList.add(getEmojiByUnicode(0x1F48A));
        emojiList.add(getEmojiByUnicode(0x1F48B));
        emojiList.add(getEmojiByUnicode(0x1F48C));
        emojiList.add(getEmojiByUnicode(0x1F48D));
        emojiList.add(getEmojiByUnicode(0x1F48E));
        emojiList.add(getEmojiByUnicode(0x1F48F));
        emojiList.add(getEmojiByUnicode(0x1F490));
        emojiList.add(getEmojiByUnicode(0x1F491));
        emojiList.add(getEmojiByUnicode(0x1F492));
        emojiList.add(getEmojiByUnicode(0x1F493));
        emojiList.add(getEmojiByUnicode(0x1F494));
        emojiList.add(getEmojiByUnicode(0x1F495));
        emojiList.add(getEmojiByUnicode(0x1F496));
        emojiList.add(getEmojiByUnicode(0x1F497));
        emojiList.add(getEmojiByUnicode(0x1F498));
        emojiList.add(getEmojiByUnicode(0x1F499));
        emojiList.add(getEmojiByUnicode(0x1F49A));
        emojiList.add(getEmojiByUnicode(0x1F49B));
        emojiList.add(getEmojiByUnicode(0x1F49C));
        emojiList.add(getEmojiByUnicode(0x1F49D));
        emojiList.add(getEmojiByUnicode(0x1F49E));
        emojiList.add(getEmojiByUnicode(0x1F49F));
        emojiList.add(getEmojiByUnicode(0x1F4A0));
        emojiList.add(getEmojiByUnicode(0x1F4A1));
        emojiList.add(getEmojiByUnicode(0x1F4A2));
        emojiList.add(getEmojiByUnicode(0x1F4A3));
        emojiList.add(getEmojiByUnicode(0x1F4A4));
        emojiList.add(getEmojiByUnicode(0x1F4A5));
        emojiList.add(getEmojiByUnicode(0x1F4A6));
        emojiList.add(getEmojiByUnicode(0x1F4A7));
        emojiList.add(getEmojiByUnicode(0x1F4A8));
        emojiList.add(getEmojiByUnicode(0x1F4A9));
        emojiList.add(getEmojiByUnicode(0x1F4AA));
        emojiList.add(getEmojiByUnicode(0x1F4AB));
        emojiList.add(getEmojiByUnicode(0x1F4AC));
        emojiList.add(getEmojiByUnicode(0x1F4AE));
        emojiList.add(getEmojiByUnicode(0x1F4AF));
        emojiList.add(getEmojiByUnicode(0x1F4B0));
        emojiList.add(getEmojiByUnicode(0x1F4B1));
        emojiList.add(getEmojiByUnicode(0x1F4B2));
        emojiList.add(getEmojiByUnicode(0x1F4B3));
        emojiList.add(getEmojiByUnicode(0x1F4B4));
        emojiList.add(getEmojiByUnicode(0x1F4B5));
        emojiList.add(getEmojiByUnicode(0x1F4B8));
        emojiList.add(getEmojiByUnicode(0x1F4B9));
        emojiList.add(getEmojiByUnicode(0x1F4BA));
        emojiList.add(getEmojiByUnicode(0x1F4BB));
        emojiList.add(getEmojiByUnicode(0x1F4BC));
        emojiList.add(getEmojiByUnicode(0x1F4BD));
        emojiList.add(getEmojiByUnicode(0x1F4BE));
        emojiList.add(getEmojiByUnicode(0x1F4BF));
        emojiList.add(getEmojiByUnicode(0x1F4C0));
        emojiList.add(getEmojiByUnicode(0x1F4C1));
        emojiList.add(getEmojiByUnicode(0x1F4C2));
        emojiList.add(getEmojiByUnicode(0x1F4C3));
        emojiList.add(getEmojiByUnicode(0x1F4C4));
        emojiList.add(getEmojiByUnicode(0x1F4C5));
        emojiList.add(getEmojiByUnicode(0x1F4C6));
        emojiList.add(getEmojiByUnicode(0x1F4C7));
        emojiList.add(getEmojiByUnicode(0x1F4C8));
        emojiList.add(getEmojiByUnicode(0x1F4C9));
        emojiList.add(getEmojiByUnicode(0x1F4CA));
        emojiList.add(getEmojiByUnicode(0x1F4CB));
        emojiList.add(getEmojiByUnicode(0x1F4CC));
        emojiList.add(getEmojiByUnicode(0x1F4CD));
        emojiList.add(getEmojiByUnicode(0x1F4CE));
        emojiList.add(getEmojiByUnicode(0x1F4CF));
        emojiList.add(getEmojiByUnicode(0x1F4D0));
        emojiList.add(getEmojiByUnicode(0x1F4D1));
        emojiList.add(getEmojiByUnicode(0x1F4D2));
        emojiList.add(getEmojiByUnicode(0x1F4D3));
        emojiList.add(getEmojiByUnicode(0x1F4D4));
        emojiList.add(getEmojiByUnicode(0x1F4D5));
        emojiList.add(getEmojiByUnicode(0x1F4D6));
        emojiList.add(getEmojiByUnicode(0x1F4D7));
        emojiList.add(getEmojiByUnicode(0x1F4D8));
        emojiList.add(getEmojiByUnicode(0x1F4D9));
        emojiList.add(getEmojiByUnicode(0x1F4DA));
        emojiList.add(getEmojiByUnicode(0x1F4DB));
        emojiList.add(getEmojiByUnicode(0x1F4DC));
        emojiList.add(getEmojiByUnicode(0x1F4DD));
        emojiList.add(getEmojiByUnicode(0x1F4DE));
        emojiList.add(getEmojiByUnicode(0x1F4DF));
        emojiList.add(getEmojiByUnicode(0x1F4E0));
        emojiList.add(getEmojiByUnicode(0x1F4E1));
        emojiList.add(getEmojiByUnicode(0x1F4E2));
        emojiList.add(getEmojiByUnicode(0x1F4E3));
        emojiList.add(getEmojiByUnicode(0x1F4E4));
        emojiList.add(getEmojiByUnicode(0x1F4E5));
        emojiList.add(getEmojiByUnicode(0x1F4E6));
        emojiList.add(getEmojiByUnicode(0x1F4E7));
        emojiList.add(getEmojiByUnicode(0x1F4E8));
        emojiList.add(getEmojiByUnicode(0x1F4E9));
        emojiList.add(getEmojiByUnicode(0x1F4EA));
        emojiList.add(getEmojiByUnicode(0x1F4EB));
        emojiList.add(getEmojiByUnicode(0x1F4EE));
        emojiList.add(getEmojiByUnicode(0x1F4F0));
        emojiList.add(getEmojiByUnicode(0x1F4F1));
        emojiList.add(getEmojiByUnicode(0x1F4F2));
        emojiList.add(getEmojiByUnicode(0x1F4F3));
        emojiList.add(getEmojiByUnicode(0x1F4F4));
        emojiList.add(getEmojiByUnicode(0x1F4F6));
        emojiList.add(getEmojiByUnicode(0x1F4F7));
        emojiList.add(getEmojiByUnicode(0x1F4F9));
        emojiList.add(getEmojiByUnicode(0x1F4FA));
        emojiList.add(getEmojiByUnicode(0x1F4FB));
        emojiList.add(getEmojiByUnicode(0x1F4FC));
        emojiList.add(getEmojiByUnicode(0x1F503));
        emojiList.add(getEmojiByUnicode(0x1F50A));
        emojiList.add(getEmojiByUnicode(0x1F50B));
        emojiList.add(getEmojiByUnicode(0x1F50C));
        emojiList.add(getEmojiByUnicode(0x1F50D));
        emojiList.add(getEmojiByUnicode(0x1F50E));
        emojiList.add(getEmojiByUnicode(0x1F50F));
        emojiList.add(getEmojiByUnicode(0x1F510));
        emojiList.add(getEmojiByUnicode(0x1F511));
        emojiList.add(getEmojiByUnicode(0x1F512));
        emojiList.add(getEmojiByUnicode(0x1F513));
        emojiList.add(getEmojiByUnicode(0x1F514));
        emojiList.add(getEmojiByUnicode(0x1F516));
        emojiList.add(getEmojiByUnicode(0x1F517));
        emojiList.add(getEmojiByUnicode(0x1F518));
        emojiList.add(getEmojiByUnicode(0x1F519));
        emojiList.add(getEmojiByUnicode(0x1F51A));
        emojiList.add(getEmojiByUnicode(0x1F51B));
        emojiList.add(getEmojiByUnicode(0x1F51C));
        emojiList.add(getEmojiByUnicode(0x1F51D));
        emojiList.add(getEmojiByUnicode(0x1F51E));
        emojiList.add(getEmojiByUnicode(0x1F51F));
        emojiList.add(getEmojiByUnicode(0x1F520));
        emojiList.add(getEmojiByUnicode(0x1F521));
        emojiList.add(getEmojiByUnicode(0x1F522));
        emojiList.add(getEmojiByUnicode(0x1F523));
        emojiList.add(getEmojiByUnicode(0x1F524));
        emojiList.add(getEmojiByUnicode(0x1F525));
        emojiList.add(getEmojiByUnicode(0x1F526));
        emojiList.add(getEmojiByUnicode(0x1F527));
        emojiList.add(getEmojiByUnicode(0x1F528));
        emojiList.add(getEmojiByUnicode(0x1F529));
        emojiList.add(getEmojiByUnicode(0x1F52A));
        emojiList.add(getEmojiByUnicode(0x1F52B));
        emojiList.add(getEmojiByUnicode(0x1F52E));
        emojiList.add(getEmojiByUnicode(0x1F52F));
        emojiList.add(getEmojiByUnicode(0x1F530));
        emojiList.add(getEmojiByUnicode(0x1F531));
        emojiList.add(getEmojiByUnicode(0x1F532));
        emojiList.add(getEmojiByUnicode(0x1F533));
        emojiList.add(getEmojiByUnicode(0x1F534));
        emojiList.add(getEmojiByUnicode(0x1F535));
        emojiList.add(getEmojiByUnicode(0x1F536));
        emojiList.add(getEmojiByUnicode(0x1F537));
        emojiList.add(getEmojiByUnicode(0x1F538));
        emojiList.add(getEmojiByUnicode(0x1F539));
        emojiList.add(getEmojiByUnicode(0x1F53A));
        emojiList.add(getEmojiByUnicode(0x1F53B));
        emojiList.add(getEmojiByUnicode(0x1F53C));
        emojiList.add(getEmojiByUnicode(0x1F53D));
        emojiList.add(getEmojiByUnicode(0x1F550));
        emojiList.add(getEmojiByUnicode(0x1F551));
        emojiList.add(getEmojiByUnicode(0x1F552));
        emojiList.add(getEmojiByUnicode(0x1F553));
        emojiList.add(getEmojiByUnicode(0x1F554));
        emojiList.add(getEmojiByUnicode(0x1F555));
        emojiList.add(getEmojiByUnicode(0x1F556));
        emojiList.add(getEmojiByUnicode(0x1F557));
        emojiList.add(getEmojiByUnicode(0x1F558));
        emojiList.add(getEmojiByUnicode(0x1F559));
        emojiList.add(getEmojiByUnicode(0x1F55A));
        emojiList.add(getEmojiByUnicode(0x1F55B));
        emojiList.add(getEmojiByUnicode(0x1F5FB));
        emojiList.add(getEmojiByUnicode(0x1F5FC));
        emojiList.add(getEmojiByUnicode(0x1F5FD));
        emojiList.add(getEmojiByUnicode(0x1F5FE));
        emojiList.add(getEmojiByUnicode(0x1F5FF));


        emojiList.add(getEmojiByUnicode(0x1F680));
        emojiList.add(getEmojiByUnicode(0x1F683));
        emojiList.add(getEmojiByUnicode(0x1F684));
        emojiList.add(getEmojiByUnicode(0x1F685));
        emojiList.add(getEmojiByUnicode(0x1F687));
        emojiList.add(getEmojiByUnicode(0x1F689));
        emojiList.add(getEmojiByUnicode(0x1F68C));
        emojiList.add(getEmojiByUnicode(0x1F68F));
        emojiList.add(getEmojiByUnicode(0x1F691));
        emojiList.add(getEmojiByUnicode(0x1F692));
        emojiList.add(getEmojiByUnicode(0x1F693));
        emojiList.add(getEmojiByUnicode(0x1F695));
        emojiList.add(getEmojiByUnicode(0x1F697));
        emojiList.add(getEmojiByUnicode(0x1F699));
        emojiList.add(getEmojiByUnicode(0x1F69A));
        emojiList.add(getEmojiByUnicode(0x1F6A2));
        emojiList.add(getEmojiByUnicode(0x1F6A4));
        emojiList.add(getEmojiByUnicode(0x1F6A5));
        emojiList.add(getEmojiByUnicode(0x1F6A7));
        emojiList.add(getEmojiByUnicode(0x1F6A8));
        emojiList.add(getEmojiByUnicode(0x1F6A9));
        emojiList.add(getEmojiByUnicode(0x1F6AA));
        emojiList.add(getEmojiByUnicode(0x1F6AB));
        emojiList.add(getEmojiByUnicode(0x1F6AC));
        emojiList.add(getEmojiByUnicode(0x1F6AD));
        emojiList.add(getEmojiByUnicode(0x1F6B2));
        emojiList.add(getEmojiByUnicode(0x1F6B6));
        emojiList.add(getEmojiByUnicode(0x1F6B9));
        emojiList.add(getEmojiByUnicode(0x1F6BA));
        emojiList.add(getEmojiByUnicode(0x1F6BB));
        emojiList.add(getEmojiByUnicode(0x1F6BC));
        emojiList.add(getEmojiByUnicode(0x1F6BD));
        emojiList.add(getEmojiByUnicode(0x1F6BE));
        emojiList.add(getEmojiByUnicode(0x1F6C0));
    }

    public List<String> getEmojiList() {

        //Collections.reverse(emojiList);

        return emojiList;
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

}
