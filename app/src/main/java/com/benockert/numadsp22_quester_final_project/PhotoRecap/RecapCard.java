package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import android.content.Intent;
import android.view.View;

public class RecapCard implements RecapCardListener {
    public String recapName;
    public String recapDate;
    public int pos;

    RecapCard(String recapName, String recapDate) {
        this.recapName = recapName;
        this.recapDate = recapDate;
    }

    /**
     * shows the user the recap they created based on the card clicked
     *
     * @param pos represents where the (potential) current recap card
     */
    @Override
    public void onCardClick(int pos) {
    }

    /**
     * gets the name of the current recap
     *
     * @param pos represents the position of the recap card
     * @return String name of the current recap
     */
    public String getRecapName(int pos) {
        if (isSamePosition(pos)) {
            return this.recapName;
        }
        return "invalid";
    }

    /**
     * gets the date the current recap was generated
     *
     * @param pos represents the position of the recap card
     * @return String date the recap was generated
     */
    public String getRecapDate(int pos) {
        if (isSamePosition(pos)) {
            return this.recapDate;
        }
        return "invalid";
    }

    /**
     * @param p
     * @return Boolean that shows if the given position is the same as the current position
     */
    public boolean isSamePosition(int p) {
        return p == this.pos;
    }

}
