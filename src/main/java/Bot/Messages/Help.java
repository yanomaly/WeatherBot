package Bot.Messages;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Help {

    public static EditMessageText help(Update update) throws TelegramApiException {
        EditMessageText editText = new EditMessageText();
        editText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editText.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        editText.setText("To start usage press \"Menu \uD83D\uDCDD\" \n\n" +
                "To find forecast for your city go \"Menu \uD83D\uDCDD\" -> \"Find city \uD83C\uDFD9️\" \n\n" +
                "To subscribe to forecast posting go \"Menu \uD83D\uDCDD\" -> \"Subscribe ✔\" \n\n" +
                "To see your subscribes go \"Menu \uD83D\uDCDD\" -> \"My subscribes \uD83D\uDCF0\" \n\n" +
                "To unsubscribe go \"Menu \uD83D\uDCDD\" -> \"My subscribes \uD83D\uDCF0\" -> {subscribe you want to unsubscribe} -> \"Del ⛔\" \n\n");
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
        return editText;
    }

}
