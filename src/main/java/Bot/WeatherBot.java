package Bot;

import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
                    start(update);
                else if (update.getMessage().getText().equals("/menu"))
                    menuInline(update);
                else {
                    PreparedStatement statement = dao.getConnection().prepareStatement("SELECT flag FROM users WHERE chatid = ?");
                    statement.setInt(1, update.getMessage().getChatId().intValue());
                    ResultSet res = statement.executeQuery();
                    res.next();
                    int flag = res.getInt("flag");
                    if (flag == 1)
                        findCity(update, flag);
                    if (flag == 2)
                        subscribe(update, flag);
                }
            }
            if (update.hasCallbackQuery()){
                if(update.getCallbackQuery().getData().equals("Help"))
                    help(update);
                if(update.getCallbackQuery().getData().equals("Menu"))
                    menu(update);
                if(update.getCallbackQuery().getData().equals("/menu"))
                    menuInline(update);
                if(update.getCallbackQuery().getData().equals("FindCity"))
                    findCity(update);
                if(update.getCallbackQuery().getData().equals("Subscribe"))
                    subscribe(update);
                if(update.getCallbackQuery().getData().equals("MySubs"))
                    yourSubs(update);
            }
        } catch (TelegramApiException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

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

    public void start(Update update) throws TelegramApiException, SQLException {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        outMessage.setText("Welcome! \nIt's weather forecast bot ⛅⛅⛅\n\n ");
        List<List<InlineKeyboardButton>> add = new ArrayList<>();
        List<InlineKeyboardButton> addLine = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button1.setText("Help \uD83E\uDD14");
        button1.setCallbackData("Help");
        button2.setText("Menu \uD83D\uDCDD");
        button2.setCallbackData("Menu");
        addLine.add(button1);
        addLine.add(button2);
        add.add(addLine);
        markupInline.setKeyboard(add);
        outMessage.setReplyMarkup(markupInline);
        PreparedStatement statement = dao.getConnection().prepareStatement("SELECT flag FROM users WHERE chatid = ?");
        statement.setInt(1, update.getMessage().getChatId().intValue());
        ResultSet res = statement.executeQuery();
        if(!res.next()) {
            statement = dao.getConnection().prepareStatement("INSERT INTO users (chatid) VALUE (?)");
            statement.setInt(1, update.getMessage().getChatId().intValue());
            statement.execute();
        }
        execute(outMessage);
    }
    public void help(Update update) throws TelegramApiException{
        EditMessageText editText = new EditMessageText();
        editText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editText.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        editText.setText("To start usage press \"Menu \uD83D\uDCDD\" \n\n" +
                "To find forecast for your city go \"Menu \uD83D\uDCDD\" -> \"Find city \uD83C\uDFD9️\" \n\n" +
                "To subscribe to forecast posting go \"Menu \uD83D\uDCDD\" -> \"Subscribe ✔\" \n\n" +
                "To see your subscribes go \"Menu \uD83D\uDCDD\" -> \"My subscribes \uD83D\uDCF0\" \n\n" +
                "To unsubscribe go \"Menu \uD83D\uDCDD\" -> \"Mu subscribes \uD83D\uDCF0\" -> {subscribe you want to unsubscribe} -> \"Unsubscribe ⛔\" \n\n");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> add = new ArrayList<>();
        List<InlineKeyboardButton> addLine = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Menu \uD83D\uDCDD");
        button2.setCallbackData("Menu");
        addLine.add(button2);
        add.add(addLine);
        markupInline.setKeyboard(add);
        editText.setReplyMarkup(markupInline);
        execute(editText);
    }
    public void menu(Update update) throws TelegramApiException, SQLException {
        EditMessageText editText = new EditMessageText();
        editText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editText.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> add = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button3.setText("Find city \uD83C\uDFD9");
        button3.setCallbackData("FindCity");
        button4.setText("Subscribe ✔");
        button4.setCallbackData("Subscribe");
        button5.setText("My subscribes \uD83D\uDCF0");
        button5.setCallbackData("MySubs");
        add.add(Arrays.asList(button3));
        add.add(Arrays.asList(button4));
        add.add(Arrays.asList(button5));
        markupInline.setKeyboard(add);
        editText.setReplyMarkup(markupInline);
        editText.setText("\uD83C\uDF2C️  Main menu   \uD83C\uDF2C");
        PreparedStatement statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 0 WHERE chatid = ?");
        statement.setInt(1, update.getCallbackQuery().getMessage().getChatId().intValue());
        statement.execute();
        execute(editText);
    }
    public void menuInline(Update update) throws TelegramApiException, SQLException {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> add = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button3.setText("Find city \uD83C\uDFD9");
        button3.setCallbackData("FindCity");
        button4.setText("Subscribe ✔");
        button4.setCallbackData("Subscribe");
        button5.setText("My subscribes \uD83D\uDCF0");
        button5.setCallbackData("MySubs");
        add.add(Arrays.asList(button3));
        add.add(Arrays.asList(button4));
        add.add(Arrays.asList(button5));
        markupInline.setKeyboard(add);
        SendMessage outMessage = new SendMessage();
        String id;
        if(update.getMessage() != null)
        id = String.valueOf(update.getMessage().getChatId());
        else
        id = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        outMessage.setChatId(id);
        outMessage.setReplyMarkup(markupInline);
        outMessage.setText("\uD83C\uDF2C    Main menu   \uD83C\uDF2C");
        PreparedStatement statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 0 WHERE chatid = ?");
        statement.setInt(1, Integer.parseInt(id));
        statement.execute();
        execute(outMessage);
    }
    public void findCity(Update update) throws TelegramApiException, SQLException {
        EditMessageText editText = new EditMessageText();
        editText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editText.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> add = new ArrayList<>();
        List<InlineKeyboardButton> addLine = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Menu \uD83D\uDCDD");
        button2.setCallbackData("Menu");
        addLine.add(button2);
        add.add(addLine);
        markupInline.setKeyboard(add);
        editText.setReplyMarkup(markupInline);
        editText.setText("Please, input name of the city: ");
        PreparedStatement statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 1 WHERE chatid = ?");
        statement.setInt(1, update.getCallbackQuery().getMessage().getChatId().intValue());
        statement.execute();
        execute(editText);
    }
    public void findCity(Update update, int flag) throws TelegramApiException, SQLException, IOException {
        String city = update.getMessage().getText();
        boolean fl = true;
        JSONObject object = null;
        URL url = new URL("http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&appid=c712fdd570b5fe58adcaec207334729e");
        Scanner in = new Scanner((InputStream) url.getContent());
        String result="";
        while (in.hasNext()) {
            result += in.nextLine();
        }
        if(result.equals("[]")){
            fl = false;
        }
        else {
            object = new JSONObject(result.substring(1, result.length() - 1));
            url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + object.getDouble("lat") + "&lon=" + object.getDouble("lon") + "&appid=c712fdd570b5fe58adcaec207334729e");
            in = new Scanner((InputStream) url.getContent());
            result = "";
            while (in.hasNext()) {
                result += in.nextLine();
            }
            object = new JSONObject(result);
        }
        if(fl){
            SendMessage editText = new SendMessage();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            editText.setChatId(String.valueOf(update.getMessage().getChatId()));
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
            editText.setText("\uD83C\uDFD9 City: " + city + ", " + object.getJSONObject("sys").getString("country") + "\n" +
                    "\uD83C\uDF21 Temperature: " + temp + " °С\n" +
                    "\uD83D\uDCAF Real feel: " + real + " °С\n" +
                    "\uD83C\uDF8F Wind: \n" +
                      "   \uD83C\uDFCE Speed: " + object.getJSONObject("wind").getDouble("speed") + " m/s\n" +
                      "   \uD83D\uDCA8 Gust: " + object.getJSONObject("wind").getDouble("gust") + " m/s\n" +
                       dir + "\n" +
                    "\uD83D\uDD28 Pressure: " + object.getJSONObject("main").getInt("pressure" ) + " mm. hg.\n" +
                    "\uD83D\uDCA7 Humidity: " + object.getJSONObject("main").getInt("humidity")+ " %\n" +
                    "\uD83E\uDD13 Description: " + object.getJSONArray("weather").getJSONObject(0).getString("main") + ", " + object.getJSONArray("weather").getJSONObject(0).getString("description")
                    );
            List<List<InlineKeyboardButton>> add = new ArrayList<>();
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("Menu \uD83D\uDCDD");
            button2.setCallbackData("/menu");
            add.add(Arrays.asList(button2));
            markupInline.setKeyboard(add);
            editText.setReplyMarkup(markupInline);
            PreparedStatement statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 0 WHERE chatid = ?");
            statement.setInt(1, update.getMessage().getChatId().intValue());
            statement.execute();
            execute(editText);
        }
        else{
            SendMessage editText = new SendMessage();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            editText.setChatId(String.valueOf(update.getMessage().getChatId()));
            editText.setText("Sorry didn't find nothing about " + city + "\uD83D\uDE1E \nBut you can try again:");
            List<List<InlineKeyboardButton>> add = new ArrayList<>();
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("Menu \uD83D\uDCDD");
            button2.setCallbackData("/menu");
            add.add(Arrays.asList(button2));
            markupInline.setKeyboard(add);
            editText.setReplyMarkup(markupInline);
            execute(editText);
        }
    }
    public void subscribe(Update update) throws TelegramApiException{

    }
    public void subscribe(Update update, int flag) throws TelegramApiException, SQLException {
        PreparedStatement statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 0 WHERE chatid = ?");
        statement.setInt(1, update.getCallbackQuery().getMessage().getChatId().intValue());
        statement.execute();
    }
    public void yourSubs(Update update) throws TelegramApiException{

    }
}
