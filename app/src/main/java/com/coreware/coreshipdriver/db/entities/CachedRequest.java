package com.coreware.coreshipdriver.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"objectId"}, unique = true)})
public class CachedRequest {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @NonNull
    private String objectId; // some kind of unique info from the object the request is tied to, to identify it
    private String requestAction;
    private String requestParams;

    public CachedRequest(@NonNull String objectId, String requestAction, String requestParams) {
        this.objectId = objectId;
        this.requestAction = requestAction;
        this.requestParams = requestParams;
    }

    @Override
    public String toString() {
        return "CachedRequest{" +
                "id=" + id +
                ", objectId='" + objectId + '\'' +
                ", requestAction='" + requestAction + '\'' +
                ", requestParams='" + requestParams + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @NonNull
    public String getObjectId() {
        return objectId;
    }
    public void setObjectId(@NonNull String objectId) {
        this.objectId = objectId;
    }
    public String getRequestAction() {
        return requestAction;
    }
    public void setRequestAction(String requestAction) {
        this.requestAction = requestAction;
    }
    public String getRequestParams() {
        return requestParams;
    }
    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

}
