package ru.nisteam.customtelegram.domain;

import lombok.Data;
import org.drinkless.tdlib.TdApi;

@Data
public class ResponseTdApiCreate {
    TdApi.Chat chat;
    TdApi.Error error;
}
