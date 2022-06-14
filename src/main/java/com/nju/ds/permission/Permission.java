package com.nju.ds.permission;

import java.util.Objects;

public class Permission {
    public static final String[][] permissionTable={{
    "PHONE",        "ACCEPT_HANDOVER","ADD_VOICEMAIL","ANSWER_PHONE_CALLS","CALL_PHONE","PROCESS_OUTGOING_CALLS","READ_CALL_LOG",
            "READ_PHONE_NUMBERS","READ_PHONE_STATE","USE_SIP","WRITE_CALL_LOG"},{
    "LOCATION",       "ACCESS_BACKGROUND_LOCATION","ACCESS_COARSE_LOCATION","ACCESS_FINE_LOCATION","ACCESS_MEDIA_LOCATION","UWB_RANGING"},{
    "SENSOR",        "BODY_SENSORS"},{
    "ACTIVITY_RECOGNITION",     "ACTIVITY_RECOGNITION"},{
    "CAMERA",        "CAMERA"},{
    "CONTACTS",        "GET_ACCOUNTS","READ_CONTACTS","WRITE_CONTACTS"},{
    "CALENDAR",        "READ_CALENDAR","WRITE_CALENDAR"},{
    "SMS",        "READ_SMS","RECEIVE_MMS","RECEIVE_SMS","RECEIVE_WAP_PUSH","SEND_SMS"},{
    "MICROPHONE",        "RECORD_AUDIO"},{
    "STORAGE",        "READ_EXTERNAL_STORAGE","WRITE_EXTERNAL_STORAGE"},{
    "NEARBY_DEVICES",   "BLUETOOTH_ADVERTISE","BLUETOOTH_CONNECT","BLUETOOTH_SCAN"},{
    "INTERNET",     "INTERNET"
        }
    };
    public static final String PERMISSION_REGEX=".*<uses-permission android:name=\"android.permission.(.*?)\"/>";

    public String type;
    public String name;

    public Permission(String descriptor){
        for(int i=0;i<permissionTable.length;i++){
            for(int j=1;j<permissionTable[i].length;j++){
                if(permissionTable[i][j].equals(descriptor)){
                    type=permissionTable[i][0];
                    name=permissionTable[i][j];
                    return;
                }
            }
        }
        type="NORMAL";
        name=descriptor;
    }

    public String toString(){
        return type+"."+name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
