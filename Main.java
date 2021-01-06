import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Main {
    private static double distSum = 0.0; // 総移動距離
    private static double timeLow = 0.0; // 総低速移動時間

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();

        // input
        try (BufferedReader buff = new BufferedReader(new InputStreamReader(System.in))) {
            String str;
            while (!(str = buff.readLine()).isEmpty()) { // 空行(最後)で入力停止
                list.add(str);
            }
        } catch (IOException e) {
            System.out.println("ERROR 入出力に失敗しました");
            e.printStackTrace();
            System.exit(1);
        }

        String firstDist = list.get(0).split(" ")[1];
        if (!firstDist.equals("0.0")) {
            System.out.println("ERROR 第1レコードの走行距離は0.0でなければなりません");
            System.exit(2);
        }

        // check data
        if (list.size() < 2) {
            System.out.println("ERROR レコードは2つ(乗車開始、終了)以上存在しなければなりません");
            System.exit(3);
        }

        for (String record : list) {
            String[] data = record.split(" ");
            double distance = Double.parseDouble(data[1]);
            int hour = Integer.parseInt(data[0].split(":")[0]);

            if (hour < 0 || hour > 99) {
                System.out.println("ERROR 時刻は0～99時の間でなければなりません");
                System.out.println("該当行: " + record);
                System.exit(2);
            }

            if (distance < 0 || distance >= 100) {
                System.out.println("ERROR 距離は0.0～99.9の間でなければなりません");
                System.out.println("該当行: " + record);
                System.exit(2);
            }
        }

        // calculate data
        for (int i = 0; i < list.size() - 1; i++) {
            try {
                calculate(list.get(i), list.get(i + 1));
            } catch (ParseException e) {
                System.out.println("ERROR 時刻の形式が違います");
                e.printStackTrace();
                System.exit(2);
            } catch (DateTimeException e1) {
                System.out.println("ERROR レコードが時系列順になっていません");
                e1.printStackTrace();
                System.exit(3);
            }
        }

        if (distSum < 0.1) {
            System.out.println("ERROR 総走行距離が0.1以上ではありません");
            System.out.println("総走行距離 " + distSum);
            System.exit(4);
        }

        // calculate fare
        int fare = calcFare();

        System.out.println(fare);
        System.exit(0);
    }

    private static void calculate(String previous, String current) throws ParseException, DateTimeException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // タイムゾーンが適用されないように
        Date prevDate = sdf.parse(previous.split(" ")[0]);
        Date currentDate = sdf.parse(current.split(" ")[0]);
        Date diff = new Date(currentDate.getTime() - prevDate.getTime());

        double passed = diff.getTime() / 1000.0; // 経過時間(s)
        double dist = Double.parseDouble(current.split(" ")[1]); // 移動距離(m)
        int speed = (int) ((dist / 1000) / (passed / 3600)); // 時速(km/h)

        Calendar cl = Calendar.getInstance();
        cl.setTimeZone(TimeZone.getTimeZone("UTC")); // タイムゾーンが適用されないように
        cl.setTime(currentDate);
        int hour = cl.get(Calendar.HOUR_OF_DAY);

        // error check
        if (passed < 0) {
            throw new DateTimeException(current);
        }

        double nightRatio = 1.25; // 深夜割増倍率
        boolean isNight = false; // 深夜フラッグ
        // 深夜判定
        if (hour <= 4 || hour >= 22) {
            isNight = true;
        }

        if (speed <= 10) {
            // 低速運賃適用
            if (isNight) {
                // 深夜割増適用
                timeLow += passed * nightRatio;
            } else {
                timeLow += passed;
            }
        }

        if (isNight) {
            // 深夜割増適用
            distSum += dist * nightRatio;
        } else {
            distSum += dist;
        }
    }

    private static int calcFare() {
        int startingFare = 410; // 初乗運賃(円)
        int standardFare = 80; // 加算運賃(円)
        int lowSpeedFare = 80; // 低速運賃(円)
        int startingDist = 1052; // 初乗運賃適用区間(m)
        int fareUpDuration = 237; // 加算運賃加算基準距離(m)
        int lowFareDuration = 90; // 低速運賃加算基準時間(s)
        int fare = 0;

        fare += startingFare;
        distSum -= startingDist;

        while (distSum >= fareUpDuration) {
            fare += standardFare;
            distSum -= fareUpDuration;
        }

        while (timeLow >= lowFareDuration) {
            fare += lowSpeedFare;
            timeLow -= lowFareDuration;
        }

        return fare;
    }
}