package com.benockert.numadsp22_quester_final_project.PhotoRecap;

public class RecapCard{
    public String recapName;
    public String recapDate;

    RecapCard(String recapName, String recapDate) {
        this.recapName = recapName;
        this.recapDate = recapDate;
    }

    /**
     * gets the name of the current recap
     *
     * @return String name of the current recap
     */
    public String getRecapName() {
        return this.recapName;

    }

    /**
     * gets the date the current recap was generated
     *
     * @return String date the recap was generated
     */
    public String getRecapDate() {
        return this.recapDate;
    }
}
