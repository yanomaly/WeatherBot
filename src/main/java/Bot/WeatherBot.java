package Bot;

import Bot.Database.Dao;
import Bot.Messages.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WeatherBot extends TelegramLongPollingBot  {

    private static Dao dao;

    @Override
    public String getBotUsername() {
        return "vveather_4cast_bot";
    }
    @Override
    public String getBotToken() {
        return "5268084440:AAG4JrHF4hURqbWFRyuvngyqT_ysWqRFl-c";
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
    public void subWeather(){

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
