package com.thatmg393.esmanager.rpc;

import android.app.Service;

import androidx.annotation.NonNull;


import java.lang.ref.WeakReference;

public class RPCGlobal {
    private static WeakReference<RPCService> rpcServInstance;
    public static void setServiceInstance(@NonNull final Service serviceInstance) {
        rpcServInstance = new WeakReference<RPCService>((RPCService)serviceInstance);
    }
    public static RPCService getServiceInstance() {
        return rpcServInstance.get();
    }
}
