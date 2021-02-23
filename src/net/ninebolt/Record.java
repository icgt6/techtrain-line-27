package net.ninebolt;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Record {
    private Date date;
    private double distance;

    public Record(Date date, double distance) {
        this.date = date;
        this.distance = distance;
    }

    public Date getDate() {
        return this.date;
    }

    public double getDistance() {
        return this.distance;
    }

    public boolean isNight() {
        Calendar cl = Calendar.getInstance();
        TimeZone timezone = TimeZone.getTimeZone("UTC");
        cl.setTimeZone(timezone); // タイムゾーンが適用されないように
        cl.setTime(getDate());

        int hour = cl.get(Calendar.HOUR_OF_DAY);
        if (hour <= 4 || hour >= 22) {
            return true;
        } else {
            return false;
        }
    }
}
