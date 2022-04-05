package Bot.Messages;

import Bot.Database.Dao;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Menu {

    public static EditMessageText menu(Update update, Dao dao) throws TelegramApiException, SQLException {
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
        button5.setText("My subscriptions \uD83D\uDCF0");
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
        return editText;
    }

}
