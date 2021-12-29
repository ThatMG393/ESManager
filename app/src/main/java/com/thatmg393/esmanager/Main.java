package com.thatmg393.esmanager;

public class Main 
{
    private boolean isDebug = true;
    private boolean createLogFile = true;
    
    public boolean setIsDebug(boolean bool)
    {
        isDebug = bool;
        return bool;
    }
    
    public boolean isDebug()
    {
        return isDebug;
    }
    public boolean setCreateLogFile(boolean bool)
    {
        createLogFile = bool;
        return bool;
    }

    public boolean createLogFile()
    {
        return createLogFile;
    }
}
