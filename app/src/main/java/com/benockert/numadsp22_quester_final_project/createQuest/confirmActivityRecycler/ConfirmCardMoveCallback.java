package com.benockert.numadsp22_quester_final_project.createQuest.confirmActivityRecycler;

public interface ConfirmCardMoveCallback {
    void onRowMoved(int fromPosition, int toPosition);
    void onRowSelected(ConfirmActivityCardHolder myViewHolder);
    void onRowClear(ConfirmActivityCardHolder myViewHolder);
}
