package com.benockert.numadsp22_quester_final_project.PhotoRecap;

public class RecapCard {
    public String recapName;
    public String recapDate;

    RecapCard(String recapName, String recapDate){
        this.recapName = recapName;
        this.recapDate = recapDate;
    }

    public String getRecapName(){
        return this.recapName;
    }

    public String getRecapDate(){
        return this.recapDate;
    }

}
