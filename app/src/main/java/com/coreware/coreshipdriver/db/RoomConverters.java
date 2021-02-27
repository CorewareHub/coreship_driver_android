package com.coreware.coreshipdriver.db;

import java.util.Date;

import androidx.room.TypeConverter;

public final class RoomConverters {

    private RoomConverters() {
        // Cannot instantiate class
    }

    @TypeConverter
    public static Date dateFromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
