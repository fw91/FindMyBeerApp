package com.msp.findMyBeer.android_ddp_client;

/**
 * Interface methods for event broadcasts
 */
public interface DDPStateBroadcasts {
    void broadcastConnectionState(DDPStateSingleton.DDPSTATE ddpstate);
    void broadcastDDPError(String errorMsg);
    void broadcastSubscriptionChanged(String subscriptionName,
                                      String changetype, String docId);
}
