package ru.nisteam.customtelegram.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RequestNewChat {
    String name;
    long[] numbers;
}
