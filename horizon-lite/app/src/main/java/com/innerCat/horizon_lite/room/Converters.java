package com.innerCat.horizon_lite.room;

import android.content.res.Resources;
import android.util.TypedValue;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Converters {
    @TypeConverter
    public static LocalDate fromTimestamp( String value) {
        return LocalDate.parse(value);
    }

    @TypeConverter
    public static String dateToTimestamp(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static int fromDpToPixels(int dp, Resources r) {
        float dip = dp;
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int)px;
    }
}
