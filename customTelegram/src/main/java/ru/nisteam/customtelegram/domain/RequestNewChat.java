package ru.nisteam.customtelegram.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RequestNewChat {
    String name;
    long[] numbers;
}


//        Сигнал: Unavailable by ICMP ping
//        Статус: Closed
//        Критичность: 2
//        Время создания: 20.03.2023 10:58:31 +03:00
//        Время завершения: 20.03.2023 12:49:30 +03:00
//        Длительность: 1ч 50мин 59с
//        Ссылка: http://monq1.icsns.ru/sm-a/rsm/?tab=Signals