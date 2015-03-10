package com.msp.findMyBeer.android_ddp_client;

import java.util.Map;

/**
 * Methods that need to be implemented for alternative storage
 */
public interface DDPStateStorage {
    void addDoc(Map<String, Object> jsonFields, String collName, String docId);

    boolean updateDoc(Map<String, Object> jsonFields, String collName,
                      String docId);

    boolean removeDoc(String collName, String docId);

    String getUserEmail(String userId);
}
