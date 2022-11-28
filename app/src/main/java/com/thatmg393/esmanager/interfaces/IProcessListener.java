package com.thatmg393.esmanager.interfaces;

public interface IProcessListener {
    public void onListenerStart();
    public void onProcessAlive();
    public void onProcessGone();
    public void onListenerStop();
}
