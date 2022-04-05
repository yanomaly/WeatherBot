package Bot.Messages;

import Bot.Database.Dao;
import Bot.Weather.Weather;
import org.json.JSONObject;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FindCity {

    public static EditMessageText findCity(Update update, Dao dao) throws TelegramApiException, SQLException {
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
        return editText;
    }
    public static SendMessage findCity(Update update, int flag, Dao dao) throws TelegramApiException, SQLException, IOException {
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
            editText.setText(Weather.createForecast(object));
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
            return editText;
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
            return editText;
        }
    }

}
