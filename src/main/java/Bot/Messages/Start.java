package Bot.Messages;

import Bot.Database.Dao;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Start {

    public static SendMessage start(Update update, Dao dao) throws TelegramApiException, SQLException {
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
        return outMessage;
    }

}
