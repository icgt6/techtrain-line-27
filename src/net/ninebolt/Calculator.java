package net.ninebolt;

import java.util.List;

import net.ninebolt.exception.InvalidFormatException;
import net.ninebolt.exception.InvalidInputException;

public class Calculator {

    public static void checkError(List<Record> data) throws InvalidFormatException, InvalidInputException {
        if (data.size() < 2) {
            throw new InvalidFormatException("レコードは2つ(乗車開始、終了)以上存在しなければなりません: " + data.size());
        }

        if (data.get(0).getDistance() != 0.0) {
            throw new InvalidInputException("第1レコードの走行距離は0.0でなければなりません: " + data.get(0).getDistance());
        }

        for (int i = 1; i < data.size(); i++) {
            if (getTimePassed(data.get(i), data.get(i - 1)) < 0.0) {
                throw new InvalidInputException(
                        "レコードが時系列順になっていません: " + data.get(i).getDate().toString() + " (:" + i + ")");
            }

            if (data.get(i).getDistance() < 0.0 || data.get(i).getDistance() >= 100.0) {
                throw new InvalidInputException(
                        "距離は0.0～99.9の間でなければなりません: " + data.get(i).getDistance() + " (:" + i + ")");
            }
        }
    }

    public static int getSpeed(Record target, Record previous) {
        return (int) ((target.getDistance() / 1000.0) / (getTimePassed(target, previous) / 3600.0)); // 時速(km/h)
    }

    public static double getTimePassed(Record target, Record previous) {
        return (target.getDate().getTime() - previous.getDate().getTime()) / 1000.0; // 経過時間(s);
    }

    private static double getNightRatio() {
        return 1.25;
    }

    public static double calcDistance(Record record) {
        if (record.isNight()) {
            return record.getDistance() * getNightRatio();
        } else {
            return record.getDistance();
        }
    }

    public static double calcTimeLow(Record target, Record previous) {
        int lowLimit = 10;

        if (getSpeed(target, previous) >= lowLimit) {
            if (target.isNight()) {
                return getTimePassed(target, previous) * getNightRatio();
            } else {
                return getTimePassed(target, previous);
            }
        } else {
            return 0.0;
        }
    }

    public static int calcFare(double distSum, double lowSec) throws InvalidInputException {
        int startingFare = 410; // 初乗運賃(円)
        int standardFare = 80; // 加算運賃(円)
        int lowSpeedFare = 80; // 低速運賃(円)
        int startingDist = 1052; // 初乗運賃適用区間(m)
        int fareUpDuration = 237; // 加算運賃加算基準距離(m)
        int lowFareDuration = 90; // 低速運賃加算基準時間(s)
        int fare = 0;
        double distance = distSum;
        double timeLow = lowSec;

        if (distance < 0.1) {
            throw new InvalidInputException("総走行距離が0.1以上ではありません: " + distance);
        }

        fare += startingFare;
        distance -= startingDist;

        while (distance >= fareUpDuration) {
            fare += standardFare;
            distance -= fareUpDuration;
        }

        while (timeLow >= lowFareDuration) {
            fare += lowSpeedFare;
            timeLow -= lowFareDuration;
        }

        return fare;
    }
}
