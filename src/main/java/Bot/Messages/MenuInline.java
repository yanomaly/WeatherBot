package Bot.Messages;

import Bot.Database.Dao;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuInline {

    public static SendMessage menuInline(Update update, Dao dao) throws TelegramApiException, SQLException {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> add = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Help \uD83E\uDD14");
        button1.setCallbackData("Help");
        button3.setText("Find city \uD83C\uDFD9");
        button3.setCallbackData("FindCity");
        button4.setText("Subscribe ✔");
        button4.setCallbackData("Subscribe");
        button5.setText("My subscriptions \uD83D\uDCF0");
        button5.setCallbackData("MySubs");
        add.add(Arrays.asList(button3));
        add.add(Arrays.asList(button4));
        add.add(Arrays.asList(button5));
        add.add(Arrays.asList(button1));
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
        return outMessage;
    }

}
