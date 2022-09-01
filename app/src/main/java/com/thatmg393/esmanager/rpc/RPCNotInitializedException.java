package com.thatmg393.esmanager.rpc;

public class RPCNotInitializedException extends Exception {
    private String exMessage;
    RPCNotInitializedException(String exMessage) { super(exMessage); this.exMessage = exMessage; }
    public String toString() { return exMessage; }
}
