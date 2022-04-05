package Bot.Messages;

import Bot.Database.Dao;
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
import java.util.regex.Pattern;

public class Subscribe {

    public static EditMessageText subscribe(Update update, Dao dao) throws TelegramApiException, SQLException {
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
        editText.setText("Please, input information about subscription this way: \nCity - XX:XX ");
        PreparedStatement statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 2 WHERE chatid = ?");
        statement.setInt(1, update.getCallbackQuery().getMessage().getChatId().intValue());
        statement.execute();
        return editText;
    }
    public static SendMessage subscribe(Update update, int flag, Dao dao) throws TelegramApiException, SQLException, IOException {
        String[] sub = update.getMessage().getText().split(" - ");
        Pattern time = Pattern.compile("(0[0-9]:[0-5][0-9])|(1[0-9]:[0-5][0-9])|(2[0-3]:[0-5][0-9])");
        if(sub.length == 2) {
            if(time.matcher(sub[1]).matches()){
                URL url = new URL("http://api.openweathermap.org/geo/1.0/direct?q=" + sub[0] + "&appid=c712fdd570b5fe58adcaec207334729e");
                Scanner in = new Scanner((InputStream) url.getContent());
                String result="";
                while (in.hasNext()) {
                    result += in.nextLine();
                }
                if(!result.equals("[]")) {
                    String[] tm = sub[1].split(":");
                    int minuts = Integer.parseInt(tm[0])*60+Integer.parseInt(tm[1]);
                    String city = sub[0];
                    PreparedStatement statement = dao.getConnection().prepareStatement("SELECT idsubs FROM subs WHERE time = ? AND city = ?");
                    statement.setInt(1, minuts);
                    statement.setString(2, city);
                    if(!statement.executeQuery().next()){
                        statement = dao.getConnection().prepareStatement("INSERT INTO subs(city, time) VALUES(?, ?)");
                        statement.setInt(2, minuts);
                        statement.setString(1, city);
                        statement.execute();
                    }
                    statement = dao.getConnection().prepareStatement(
                            "SELECT idusers_subs FROM user_subs WHERE " +
                                    "userid = (SELECT idusers FROM users WHERE chatid = ?) AND " +
                                    "subid = (SELECT idsubs FROM subs WHERE time = ? AND city = ?)");
                    statement.setInt(1, update.getMessage().getChatId().intValue());
                    statement.setInt(2, minuts);
                    statement.setString(3, city);
                    if(!statement.executeQuery().next()){
                        statement = dao.getConnection().prepareStatement("INSERT INTO user_subs (userid, subid) VALUES ((SELECT idusers FROM users WHERE chatid = ?),(SELECT idsubs FROM subs WHERE time = ? AND city = ?))");
                        statement.setInt(1, update.getMessage().getChatId().intValue());
                        statement.setInt(2, minuts);
                        statement.setString(3, city);
                        statement.execute();
                        SendMessage editText = new SendMessage();
                        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                        editText.setChatId(String.valueOf(update.getMessage().getChatId()));
                        List<List<InlineKeyboardButton>> add = new ArrayList<>();
                        InlineKeyboardButton button2 = new InlineKeyboardButton();
                        button2.setText("Menu \uD83D\uDCDD");
                        button2.setCallbackData("/menu");
                        add.add(Arrays.asList(button2));
                        markupInline.setKeyboard(add);
                        editText.setReplyMarkup(markupInline);
                        editText.setText("You successfully subscribed!\uD83D\uDE0A");
                        statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 0 WHERE chatid = ?");
                        statement.setInt(1, update.getMessage().getChatId().intValue());
                        statement.execute();
                        return editText;
                    }
                    else{
                        SendMessage editText = new SendMessage();
                        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                        editText.setChatId(String.valueOf(update.getMessage().getChatId()));
                        List<List<InlineKeyboardButton>> add = new ArrayList<>();
                        InlineKeyboardButton button2 = new InlineKeyboardButton();
                        button2.setText("Menu \uD83D\uDCDD");
                        button2.setCallbackData("/menu");
                        add.add(Arrays.asList(button2));
                        markupInline.setKeyboard(add);
                        editText.setReplyMarkup(markupInline);
                        editText.setText("You already subscribed on this forecast\uD83E\uDD13");
                        statement = dao.getConnection().prepareStatement("UPDATE users SET flag = 0 WHERE chatid = ?");
                        statement.setInt(1, update.getMessage().getChatId().intValue());
                        statement.execute();
                        return editText;
                    }
                }
                else{
                    SendMessage editText = new SendMessage();
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    editText.setChatId(String.valueOf(update.getMessage().getChatId()));
                    List<List<InlineKeyboardButton>> add = new ArrayList<>();
                    InlineKeyboardButton button2 = new InlineKeyboardButton();
                    button2.setText("Menu \uD83D\uDCDD");
                    button2.setCallbackData("/menu");
                    add.add(Arrays.asList(button2));
                    markupInline.setKeyboard(add);
                    editText.setReplyMarkup(markupInline);
                    editText.setText("City invalid\uD83C\uDF06  \n Try again:");
                    return editText;
                }
            }
            else{
                SendMessage editText = new SendMessage();
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                editText.setChatId(String.valueOf(update.getMessage().getChatId()));
                List<List<InlineKeyboardButton>> add = new ArrayList<>();
                InlineKeyboardButton button2 = new InlineKeyboardButton();
                button2.setText("Menu \uD83D\uDCDD");
                button2.setCallbackData("/menu");
                add.add(Arrays.asList(button2));
                markupInline.setKeyboard(add);
                editText.setReplyMarkup(markupInline);
                editText.setText("Time invalid\uD83D\uDD70 \n Try again:");
                return editText;
            }
        }
        else{
            SendMessage editText = new SendMessage();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            editText.setChatId(String.valueOf(update.getMessage().getChatId()));
            List<List<InlineKeyboardButton>> add = new ArrayList<>();
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("Menu \uD83D\uDCDD");
            button2.setCallbackData("/menu");
            add.add(Arrays.asList(button2));
            markupInline.setKeyboard(add);
            editText.setReplyMarkup(markupInline);
            editText.setText("Data format invalid✍  \n Try again:");
            return editText;
        }
    }

}