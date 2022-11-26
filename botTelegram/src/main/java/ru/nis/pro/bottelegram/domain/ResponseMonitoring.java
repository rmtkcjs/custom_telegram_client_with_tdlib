package ru.nis.pro.bottelegram.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ResponseMonitoring {
    String link;
    long groupId;
    String error;
}
