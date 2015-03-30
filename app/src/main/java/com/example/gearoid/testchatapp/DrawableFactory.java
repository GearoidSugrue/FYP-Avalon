package com.example.gearoid.testchatapp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.example.gearoid.testchatapp.character.ConstantsChara;

/**
 * Created by gearoid on 29/03/15.
 */
public class DrawableFactory {

    public static Drawable getDrawable(Resources res, String drawableName) {

        switch (drawableName) {
            case ConstantsChara.MERLIN:
                return res.getDrawable(R.drawable.good_merlin);
            case ConstantsChara.PERCIVAL:
                return res.getDrawable(R.drawable.good_percival);
            case ConstantsChara.ASSASSIN:
                return res.getDrawable(R.drawable.evil_assassin);
            case ConstantsChara.MORDRED:
                return res.getDrawable(R.drawable.evil_mordred);
            case ConstantsChara.OBERON:
                return res.getDrawable(R.drawable.evil_oberon);
            case ConstantsChara.MORGANA:
                return res.getDrawable(R.drawable.evil_morgana);
            case "Knight 1":
                return res.getDrawable(R.drawable.good_knight1);
            case "Knight 2":
                return res.getDrawable(R.drawable.good_knight2);
            case "Knight 3":
                return res.getDrawable(R.drawable.good_knight3);
            case "Knight 4":
                return res.getDrawable(R.drawable.good_knight4);
            case "Knight 5":
                return res.getDrawable(R.drawable.good_knight5);
            case "Minion 1":
                return res.getDrawable(R.drawable.evil_minion1);
            case "Minion 2":
                return res.getDrawable(R.drawable.evil_minion2);
            case "Minion 3":
                return res.getDrawable(R.drawable.evil_minion3);
            case "Lady of Lake":
                return res.getDrawable(R.drawable.token_ladyoflake);
            case "Approve Token":
                return res.getDrawable(R.drawable.token_approve);
            case "Reject Token":
                return res.getDrawable(R.drawable.token_reject);
            case "Red Loyalty Card":
                return res.getDrawable(R.drawable.misc_redloyaltycard);
            case "Blue Loyalty Card":
                return res.getDrawable(R.drawable.misc_blueloyaltycard);
            default:
                return res.getDrawable(R.drawable.token_approve);
        }
    }
}
