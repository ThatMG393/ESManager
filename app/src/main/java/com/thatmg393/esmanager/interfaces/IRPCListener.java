package com.thatmg393.esmanager.interfaces;

import com.thatmg393.esmanager.models.DiscordProfile;

public interface IRPCListener {
    public void onReady(DiscordProfile profile);
    public void onError(Exception err);
	public void onSessionsReplace(String oldStatus, String newStatus);
	public void onStop(String reason, int code);
}
