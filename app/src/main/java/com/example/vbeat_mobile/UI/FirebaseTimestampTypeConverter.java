package com.example.vbeat_mobile.UI;

import androidx.room.TypeConverter;

import com.google.firebase.Timestamp;

import java.util.Date;

public class FirebaseTimestampTypeConverter {
    @TypeConverter
    public  Timestamp fromString(String timestamp) {
        long miliSecondTimestamp = Long.parseLong(timestamp);
        return new Timestamp(new Date(miliSecondTimestamp));
    }

    @TypeConverter
    public  String fromTimestamp(Timestamp timestamp) {
        return Long.toString(timestamp.toDate().getTime());
    }
}
