package com.example.luckyleaf.dataholders;

import com.google.gson.annotations.SerializedName;

public class MqqtMessageResponseModel {
    @SerializedName("status_change_event")
    MqqtMessageData data;

    public String getError_code() {
        return data!=null ? data.getError_code() : "";
    }

    public String getState() {
        return data!=null ? data.getState() : "";
    }
    class MqqtMessageData {
        @SerializedName("state")
        String state;

        @SerializedName("error_code")
        String error_code;

        public String getError_code() {
            return error_code !=null ? error_code : "";
        }

        public String getState() {
            return state !=null ? state : "";
        }
    }
}
