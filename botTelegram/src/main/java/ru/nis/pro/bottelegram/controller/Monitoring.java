package ru.nis.pro.bottelegram.controller;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nis.pro.bottelegram.domain.RequestMonitoring;
import ru.nis.pro.bottelegram.domain.ResponseMonitoring;
import ru.nis.pro.bottelegram.integration.TelegramBot;

import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/api/monitoring")
public class Monitoring {

    private final TelegramBot bot;
    private final RestTemplate restTemplate;
    private final String URL = "http://localhost:8080/api/newchat";

    public Monitoring(TelegramBot bot, RestTemplate restTemplate) {
        this.bot = bot;
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    @PostMapping("/bot")
    public ResponseEntity<ResponseMonitoring> createNewChat(@RequestBody RequestMonitoring requestMonitoring) {
        log.warn("[createNewChat] start method " );
        HttpEntity<RequestMonitoring> request = new HttpEntity<>(requestMonitoring);
        ResponseEntity<ResponseMonitoring> exchange = null;
        try {
            log.warn("[createNewChat] start exchange " );
            exchange = restTemplate.exchange(URL, HttpMethod.POST, request, ResponseMonitoring.class);
            log.warn("[createNewChat] stop exchange " );
        } catch (RestClientException e) {
            //log.error("[MONITORING, BAD REQUEST] exchange.getStatusCode(): " + exchange.getStatusCode());

            log.error("[MONITORING,BAD REQUEST] e: " + e);
            ResponseMonitoring responseMonitoring = new ResponseMonitoring();
            responseMonitoring.setError("MONITORING,BAD REQUEST FROM NEW CHAT CREATED");
            return new ResponseEntity<>(responseMonitoring, HttpStatus.BAD_REQUEST);
        }
        log.warn("[createNewChat] get response, exchange.getStatusCode() == HttpStatus.OK "  + (exchange.getStatusCode() == HttpStatus.OK));
        if (exchange.getStatusCode() == HttpStatus.OK) {
            log.warn("[createNewChat] get response, exchange.getBody().getGroupId() < 0 "  + (exchange.getBody().getGroupId() < 0));
            if (exchange.getBody().getGroupId() != 0){
                log.info("Telegram ID {}", String.valueOf(exchange.getBody().getGroupId()));


                String responsible = "Серябряков Андрей Анатольевич";
                String id = "132223";
                String type = "Host";
                String problem = "Снизилось напряжение на остовном контуре";
                String ke = "name: nl-sp-idmweb01";
                String date = "28.12.2022 11:55:49 +03:00";
                String text = String.format("Готов к работе, ссылка на текущую группу: %s" +
                        "\n Время создания: %s" +
                                "\n id обьекта: %s" +
                                "\n Тип обьекта: %s" +
                                "\n Ответственный менеджер: %s" +
                                "\n Описание проблемы: %s" +
                                "\n Затронутое КЕ: %s"
                        , exchange.getBody().getLink(), date, id, type, responsible, problem, ke);

                try {
                    Thread.sleep(5000);
                    bot.execute(SendMessage.builder()
                            .chatId(String.valueOf(exchange.getBody().getGroupId()))
                            .text(text)
                            .protectContent(true)
                            .build());
                    return new ResponseEntity<>(exchange.getBody(), HttpStatus.OK);
                } catch (TelegramApiException e) {
                    log.error("[MONITORING, BOT SEND] e: " + e);
                }
            }else{
                log.error("[MONITORING, BOT NOT SEND, getGroupId() == 0] exchange.getBody(): " + exchange.getBody());
                return new ResponseEntity<>(exchange.getBody(), HttpStatus.NOT_ACCEPTABLE);
            }
        }
        log.error("[MONITORING, BAD HttpStatus] exchange.getStatusCode(): " + exchange.getStatusCode());
        return new ResponseEntity<>(exchange.getBody(), HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
    }


    @GetMapping("/test")
    public ResponseEntity<String> getTest(){
        ResponseEntity<String> exchange = restTemplate.exchange("https://httpbin.org/ip", HttpMethod.GET, null, String.class);
        return new ResponseEntity<>(exchange.getBody(), HttpStatus.OK);
    }
}
