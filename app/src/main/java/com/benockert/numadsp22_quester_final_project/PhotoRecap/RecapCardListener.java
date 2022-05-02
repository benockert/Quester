package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import android.view.View;

public interface RecapCardListener {
    /**
     * performs an action when the recap card is clicked
     *
     * @param pos Integer represents where the (potential) current recap card
     *                 is located in the recycler view
     */
    void onCardClick(int pos);
    /**
     * gets the title of the current book
     *
     * @param pos Integer represents the position of the recap card
     * @return String name of the current recap
     */
    String getRecapName(int pos);

    /**
     * gets the title of the current book
     *
     * @param pos Integer represents the position of the recap card
     * @return String date the recap was generated
     */
    String getRecapDate(int pos);
}
