import Bot.WeatherBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Runner {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            WeatherBot weatherBot = new WeatherBot();
            weatherBot.createConnection();
            weatherBot.setFlag();
            botsApi.registerBot(weatherBot);
            weatherBot.subWeather();
        } catch (TelegramApiException | SQLException e) {
            e.printStackTrace();
        }
    }
}
