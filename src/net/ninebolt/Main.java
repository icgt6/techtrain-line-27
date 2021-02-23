package net.ninebolt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.ninebolt.exception.InvalidFormatException;
import net.ninebolt.exception.InvalidInputException;

public class Main {
    public static void main(String[] args) {
        List<Record> data = new ArrayList<Record>();

        // input
        try (BufferedReader buff = new BufferedReader(new InputStreamReader(System.in))) {
            String str;
            while (!(str = buff.readLine()).isEmpty()) { // 空行(最後)で入力停止
                Date date = parse(str.split(" ")[0]);
                double distance = Double.parseDouble(str.split(" ")[1]);
                data.add(new Record(date, distance));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvalidInputException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // calculate
        try {
            double distSum = 0.0; // 総移動距離
            double timeLow = 0.0; // 総低速移動時間

            Calculator.checkError(data);

            for (int i = 1; i < data.size(); i++) {
                distSum += Calculator.calcDistance(data.get(i));
                timeLow += Calculator.calcTimeLow(data.get(i), data.get(i - 1));
            }

            int fare = Calculator.calcFare(distSum, timeLow);
            System.out.println(fare); 
        } catch (InvalidInputException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    public static Date parse(String dateStr) throws ParseException, InvalidInputException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        TimeZone timezone = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(timezone); // タイムゾーンが適用されないように

        int hour = Integer.parseInt(dateStr.split(":")[0]);
        if (hour < 0 || hour >= 100) {
            throw new InvalidInputException("時刻は0～99時の間でなければなりません: " + dateStr);
        }

        return sdf.parse(dateStr);
    }
}
