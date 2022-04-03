package Bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                    int flag = res.getInt(1);
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
                if(update.getCallbackQuery().getData().equals("FindCity"))
                    findCity(update);
                if(update.getCallbackQuery().getData().equals("Subscribe"))
                    subscribe(update);
                if(update.getCallbackQuery().getData().equals("MySubs"))
                    yourSubs(update);
            }
        } catch (TelegramApiException | SQLException e) {
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

    public void start(Update update) throws TelegramApiException {
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
        List<InlineKeyboardButton> addLine = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button2.setText("Menu \uD83D\uDCDD");
        button2.setCallbackData("Menu");
        button3.setText("Find city \uD83C\uDFD9");
        button3.setCallbackData("FindCity");
        button4.setText("Subscribe ✔");
        button4.setCallbackData("Subscribe");
        button5.setText("My subscribes \uD83D\uDCF0");
        button5.setCallbackData("MySubs");
        add.add(Arrays.asList(button3));
        add.add(Arrays.asList(button4));
        add.add(Arrays.asList(button5));
        addLine.add(button2);
        add.add(addLine);
        markupInline.setKeyboard(add);
        editText.setReplyMarkup(markupInline);
        editText.setText("\uD83C\uDF2C️");
        PreparedStatement statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 0 WHERE chatid = ?");
        statement.setInt(1, update.getCallbackQuery().getMessage().getChatId().intValue());
        statement.execute();
        execute(editText);
    }
    public void menuInline(Update update) throws TelegramApiException{
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> add = new ArrayList<>();
        List<InlineKeyboardButton> addLine = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button2.setText("Menu \uD83D\uDCDD");
        button2.setCallbackData("Menu");
        button3.setText("Find city \uD83C\uDFD9");
        button3.setCallbackData("FindCity");
        button4.setText("Subscribe ✔");
        button4.setCallbackData("Subscribe");
        button5.setText("My subscribes \uD83D\uDCF0");
        button5.setCallbackData("MySubs");
        add.add(Arrays.asList(button3));
        add.add(Arrays.asList(button4));
        add.add(Arrays.asList(button5));
        addLine.add(button2);
        add.add(addLine);
        markupInline.setKeyboard(add);
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        outMessage.setReplyMarkup(markupInline);
        outMessage.setText(" \uD83C\uDF2C️");
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
    public void findCity(Update update, int flag) throws TelegramApiException, SQLException {
        String city = update.getMessage().getText();
        //
        //
        //
        if(true){
            SendMessage outMessage = new SendMessage();
            outMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
            outMessage.setText(city + " \uD83C\uDF2C️");
            execute(outMessage);
        }
        else{
            SendMessage outMessage = new SendMessage();
            outMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
            outMessage.setText("Sorry didn't find nothing about " + city +"\uD83D\uDE1E");
            execute(outMessage);
        }
        PreparedStatement statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 0 WHERE chatid = ?");
        statement.setInt(1, update.getCallbackQuery().getMessage().getChatId().intValue());
        statement.execute();
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
