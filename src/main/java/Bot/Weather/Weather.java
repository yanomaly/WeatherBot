package Bot.Weather;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class Weather {

    public static String createForecast(JSONObject object){
        String temp = new DecimalFormat("#0.00").format(object.getJSONObject("main").getDouble("temp") - 273.15);
        String real = new DecimalFormat("#0.00").format(object.getJSONObject("main").getDouble("feels_like") - 273.15);
        int deg = object.getJSONObject("wind").getInt("deg");
        String dir = "";
        if((deg >= 337 && deg <= 360) || (deg >= 0 && deg <= 22))
            dir = "   \uD83D\uDD04 Direction: S ⬆";
        if(deg > 22 && deg < 67)
            dir = "   \uD83D\uDD04 Direction: SW ↗";
        if(deg >= 67 && deg <= 112)
            dir = "   \uD83D\uDD04 Direction: W ➡";
        if(deg > 112 && deg < 157)
            dir = "   \uD83D\uDD04 Direction: NW ↘";
        if(deg >= 157 && deg <= 202)
            dir = "   \uD83D\uDD04 Direction: N ⬇";
        if(deg > 202 && deg < 247)
            dir = "   \uD83D\uDD04 Direction: NE ↙";
        if(deg >= 247 && deg <= 292)
            dir = "   \uD83D\uDD04 Direction: E ⬅";
        if(deg > 292 && deg < 337)
            dir = "   \uD83D\uDD04 Direction: SE ↖";
        return
                "\uD83C\uDFD9 City: " + object.getString("name") + ", " + object.getJSONObject("sys").getString("country") + "\n" +
                "\uD83C\uDF21 Temperature: " + temp + " °С\n" +
                "\uD83D\uDCAF Real feel: " + real + " °С\n" +
                "\uD83C\uDF8F Wind: \n" +
                "   \uD83C\uDFCE Speed: " + object.getJSONObject("wind").getDouble("speed") + " m/s\n" +
                dir + "\n" +
                "\uD83D\uDD28 Pressure: " + object.getJSONObject("main").getInt("pressure" ) + " mm. hg.\n" +
                "\uD83D\uDCA7 Humidity: " + object.getJSONObject("main").getInt("humidity")+ " %\n" +
                "\uD83E\uDD13 Description: " + object.getJSONArray("weather").getJSONObject(0).getString("main") + ", " + object.getJSONArray("weather").getJSONObject(0).getString("description");
    }

}
