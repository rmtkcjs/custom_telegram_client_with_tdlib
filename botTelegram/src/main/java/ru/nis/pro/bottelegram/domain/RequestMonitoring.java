package ru.nis.pro.bottelegram.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RequestMonitoring {
    String keMain;
    String keInform;
    String keComponent;
    String name;
    String description;
    long[] numbers;

}
