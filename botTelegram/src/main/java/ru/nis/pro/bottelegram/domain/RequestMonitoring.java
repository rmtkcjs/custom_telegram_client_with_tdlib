package ru.nis.pro.bottelegram.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RequestMonitoring {
    String name;
    String link;
    long[] numbers;
    String description;
    String status;
    String critical;
    String timeCreate;
    String timeEnd;
    String duration;
//    String problemLink;

}
