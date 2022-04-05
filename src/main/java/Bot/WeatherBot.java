package Bot;

import Bot.Database.Dao;
import Bot.Messages.*;
import Bot.Weather.Weather;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WeatherBot extends TelegramLongPollingBot  {

    private static Dao dao;

    @Override
    public String getBotUsername() {
        return PrivateData.getName();
    }
    @Override
    public String getBotToken() {
        return PrivateData.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("/start"))
                    execute(Start.start(update, dao));
                else if (update.getMessage().getText().equals("/menu"))
                    execute(MenuInline.menuInline(update, dao));
                else
                    input(update);
            }
            if (update.hasCallbackQuery()){
                if(update.getCallbackQuery().getData().equals("Help"))
                    execute(Help.help(update));
                if(update.getCallbackQuery().getData().equals("Menu"))
                    execute(Menu.menu(update, dao));
                if(update.getCallbackQuery().getData().equals("/menu"))
                    execute(MenuInline.menuInline(update, dao));
                if(update.getCallbackQuery().getData().equals("FindCity"))
                    execute(FindCity.findCity(update, dao));
                if(update.getCallbackQuery().getData().equals("Subscribe"))
                    execute(Subscribe.subscribe(update, dao));
                if(update.getCallbackQuery().getData().equals("MySubs"))
                    execute(YourSubscriptions.yourSubscriptions(update, dao));
                if(update.getCallbackQuery().getData().startsWith("del")) {
                    execute(YourSubscriptions.delete(update, dao));
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.getCallbackQuery().getId(), "You successfully delete subscription", true, null, 2000);
                    execute(answerCallbackQuery);
                }
            }
        } catch (TelegramApiException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void input(Update update) throws SQLException, TelegramApiException, IOException {
        PreparedStatement statement = dao.getConnection().prepareStatement("SELECT flag FROM users WHERE chatid = ?");
        statement.setInt(1, update.getMessage().getChatId().intValue());
        ResultSet res = statement.executeQuery();
        res.next();
        int flag = res.getInt("flag");
        if (flag == 1)
            execute(FindCity.findCity(update, flag, dao));
        if (flag == 2)
            execute(Subscribe.subscribe(update, flag, dao));
    };
    public void subWeather() throws InterruptedException, SQLException, IOException, TelegramApiException {
        while(true){
            Calendar current = new GregorianCalendar();
            int minuts = current.get(Calendar.HOUR_OF_DAY) * 60 + current.get(Calendar.MINUTE);
            PreparedStatement statement = dao.getConnection().prepareStatement("SELECT userid, subid FROM user_subs WHERE subid IN (SELECT idsubs FROM subs WHERE time = ?)");
            statement.setInt(1, minuts);
            ResultSet res = statement.executeQuery();
            while(res.next()){
                int userid = res.getInt("userid");
                int subid = res.getInt("subid");
                PreparedStatement userStatement = dao.getConnection().prepareStatement("SELECT chatid FROM users WHERE idusers = ?");
                PreparedStatement subStatement = dao.getConnection().prepareStatement("SELECT city FROM subs WHERE idsubs  = ?");
                userStatement.setInt(1, userid);
                subStatement.setInt(1, subid);
                ResultSet chat = userStatement.executeQuery();
                ResultSet city = subStatement.executeQuery();
                chat.next();
                city.next();
                URL url = new URL("http://api.openweathermap.org/geo/1.0/direct?q=" + city.getString("city") + "&appid=c712fdd570b5fe58adcaec207334729e");
                Scanner in = new Scanner((InputStream) url.getContent());
                String result="";
                while (in.hasNext()) {
                    result += in.nextLine();
                }
                JSONObject object = new JSONObject(result.substring(1, result.length() - 1));
                url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + object.getDouble("lat") + "&lon=" + object.getDouble("lon") + "&appid=c712fdd570b5fe58adcaec207334729e");
                in = new Scanner((InputStream) url.getContent());
                result = "";
                while (in.hasNext()) {
                    result += in.nextLine();
                }
                object = new JSONObject(result);
                SendMessage outMessage = new SendMessage();
                outMessage.setChatId(String.valueOf(chat.getInt("chatid")));
                outMessage.setText(Weather.createForecast(object));
                execute(outMessage);
            }
            TimeUnit.MINUTES.sleep(1);
        }
    }
    public void createConnection(){
        try {
            dao = new Dao();
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    public void setFlag() throws SQLException {
        PreparedStatement statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 0");
        statement.execute();
    }
}
