package com.example.vbeat_mobile.backend.user;

public class VBeatUserModel {
    protected String email;
    protected String displayName;
    protected String userId;

    public VBeatUserModel(String email, String displayName, String userId) {
        this.email = email;
        this.displayName = displayName;
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
