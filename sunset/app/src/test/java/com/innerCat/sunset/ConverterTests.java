package com.innerCat.sunset;

import com.innerCat.sunset.room.Converters;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class ConverterTests {

    @Test
    public void from_date_to_timestamp() {

    }

    @Test
    public void null_from_date_to_timestamp() {
        assertNull(Converters.dateToTimestamp(null));
    }

    @Test
    public void null_from_timestamp_to_date() {
        assertNull(Converters.fromTimestamp(null));
    }
}
