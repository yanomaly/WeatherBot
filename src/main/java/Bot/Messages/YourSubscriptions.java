package Bot.Messages;

import Bot.Database.Dao;
import org.checkerframework.checker.units.qual.A;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YourSubscriptions {

    public static EditMessageText yourSubscriptions(Update update, Dao dao) throws TelegramApiException, SQLException {
        EditMessageText editText = new EditMessageText();
        editText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editText.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> add = new ArrayList<>();
        PreparedStatement userid = dao.getConnection().prepareStatement("SELECT idusers FROM users WHERE chatid = ?");
        userid.setInt(1, update.getCallbackQuery().getMessage().getChatId().intValue());
        ResultSet id = userid.executeQuery();
        id.next();
        int usid = id.getInt("idusers");
        PreparedStatement preparedStatement = dao.getConnection().prepareStatement("SELECT city, time, idsubs FROM subs WHERE idsubs IN (SELECT subid FROM user_subs WHERE userid = ?)");
        preparedStatement.setInt(1, usid);
        ResultSet res = preparedStatement.executeQuery();
        while(res.next()){
            String city = res.getString("city");
            String time = res.getInt("time") / 60 + ":" + (res.getInt("time") % 60 == 0 ? "00" : String.valueOf(res.getInt("time") % 60));
            add.add(Arrays.asList(new InlineKeyboardButton(city + " - " + time, null, "sub", null, null, null, null, null),
                    new InlineKeyboardButton("Del ⛔", null, "del " + usid + " " + res.getInt("idsubs"), null, null, null, null, null)));
        }
        if(add.size() != 0){
            editText.setText("Your subscriptions: \n");
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("Menu \uD83D\uDCDD");
            button2.setCallbackData("Menu");
            add.add(Arrays.asList(button2));
            markupInline.setKeyboard(add);
            editText.setReplyMarkup(markupInline);
        }
        else {
            editText.setText("❌You don't have any subscriptions❌");
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("Menu \uD83D\uDCDD");
            button2.setCallbackData("Menu");
            add.add(Arrays.asList(button2));
            markupInline.setKeyboard(add);
            editText.setReplyMarkup(markupInline);
        }
        return editText;
    }
    public static EditMessageText delete(Update update, Dao dao) throws TelegramApiException, SQLException{
        String[] info = update.getCallbackQuery().getData().split(" ");
        PreparedStatement delete = dao.getConnection().prepareStatement("DELETE FROM user_subs WHERE userid = ? AND subid = ?");
        delete.setInt(1, Integer.parseInt(info[1]));
        delete.setInt(2, Integer.parseInt(info[2]));
        delete.execute();
        return yourSubscriptions(update, dao);
    }

}
