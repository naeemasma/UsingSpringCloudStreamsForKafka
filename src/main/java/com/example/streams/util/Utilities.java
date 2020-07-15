package com.example.streams.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.streams.constants.Constants;

public class Utilities {
	public static Timestamp getCurrentTimestamp() {
		Date date = new Date();
        return (new Timestamp(date.getTime()));
	}
	
	public static String getCurrentTimeString() {
		Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_YMD_HMS);
        return sdf.format(timestamp);
	}

}
