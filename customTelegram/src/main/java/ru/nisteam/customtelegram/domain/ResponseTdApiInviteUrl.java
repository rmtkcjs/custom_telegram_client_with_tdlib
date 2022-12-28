package ru.nisteam.customtelegram.domain;

import lombok.Data;
import org.drinkless.tdlib.TdApi;

@Data
public class ResponseTdApiInviteUrl {
    TdApi.ChatInviteLink link;
    TdApi.Error error;

    public void clear(){
        link = null;
        error = null;
    }
}
