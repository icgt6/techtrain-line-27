package net.ninebolt.testcase;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class TestcaseGenerator {
    public static void main(String[] args) {
        try {
            Calendar cl = Calendar.getInstance();
            cl.setTimeZone(TimeZone.getTimeZone("UTC")); // タイムゾーンが適用されないように
            cl.set(2021, 1, 10, 13, 24, 34);
            
            Random random = new Random();
            double debug = 0.0;
            
            File file = new File(".\\out.txt");
            FileWriter writer = new FileWriter(file);
            
            writer.write(getDateString(cl, 1) + " 0.0\n");
            
            int size = 10;
            int day = 1;
            for(int i=0; i<size-1; i++) {
                double rand = random.nextInt(1000) / 10.0;
                int dayOld = cl.get(Calendar.DAY_OF_MONTH);
                cl.add(Calendar.SECOND, random.nextInt(38));

                if(dayOld != cl.get(Calendar.DAY_OF_MONTH)) {
                    day += 1;
                }
                debug += rand;
                writer.write(getDateString(cl, day) + " " + rand + "\n");
            }
            System.out.println(debug);
            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDateString(Calendar date, int day) {
        int hour = date.get(Calendar.HOUR_OF_DAY) + (24 * (day-1));
        int minute = date.get(Calendar.MINUTE);
        int second = date.get(Calendar.SECOND);
        int millisec = date.get(Calendar.MILLISECOND);

        String dateStr = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second) + "." + String.format("%02d", millisec);
        return dateStr;
    }
}
