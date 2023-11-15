package ru.nis.pro.bottelegram.controller;

import lombok.SneakyThrows;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nis.pro.bottelegram.domain.RequestMonitoring;
import ru.nis.pro.bottelegram.domain.ResponseMonitoring;
import ru.nis.pro.bottelegram.integration.TelegramBot;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/monitoring")
public class Monitoring {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(Monitoring.class);
    private final TelegramBot bot;
    private final RestTemplate restTemplate;
    private final String URL = "http://127.0.0.1:8080/api/newchat";

    public Monitoring(TelegramBot bot, RestTemplate restTemplate) {
        this.bot = bot;
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    @PostMapping("/bot")
    public ResponseEntity<ResponseMonitoring> createNewChat(@RequestBody RequestMonitoring requestMonitoring) {
        log.warn("[createNewChat] start method ");
        log.info("Data in => " + requestMonitoring);
        HttpEntity<RequestMonitoring> request = new HttpEntity<>(requestMonitoring);
        ResponseEntity<ResponseMonitoring> exchange = null;

        try {
            log.warn("[createNewChat] start exchange ");
            exchange = restTemplate.exchange(URL, HttpMethod.POST, request, ResponseMonitoring.class);
            log.warn("[createNewChat] stop exchange ");
        } catch (RestClientException e) {
            log.error("[MONITORING,BAD REQUEST] e: " + e);
            ResponseMonitoring responseMonitoring = new ResponseMonitoring( );
            responseMonitoring.setError("MONITORING,BAD REQUEST FROM NEW CHAT CREATED");
            return new ResponseEntity<>(responseMonitoring, HttpStatus.BAD_REQUEST);
        }

        log.warn("[createNewChat] get response, exchange.getStatusCode() == HttpStatus.OK " +
                (exchange.getStatusCode( ) == HttpStatus.OK));
        if (exchange.getStatusCode( ) == HttpStatus.OK) {

            log.warn("[createNewChat] get response, exchange.getBody().getGroupId() < 0 " +
                    (exchange.getBody( ).getGroupId( ) < 0));
            if (exchange.getBody( ).getGroupId( ) != 0) {
                log.info("Telegram ID {}", String.valueOf(exchange.getBody( ).getGroupId( )));

                StringBuilder sb = new StringBuilder( );
                sb.append(requestMonitoring.getMsgPrivate( ) ==
                        null ? "Сообщение отсутствует" : requestMonitoring.getMsgPrivate( ));

                try {
                    bot.execute(SendMessage.builder( )
                            .chatId(String.valueOf(exchange.getBody( ).getGroupId( )))
                            .text(sb.toString( ))
                            .protectContent(true)
                            .build( ));
                    log.info("[MONITORING, BOT SEND] send success to group: " + exchange.getBody( ).getGroupId( ));
                    return new ResponseEntity<>(exchange.getBody( ), HttpStatus.OK);
                } catch (TelegramApiException e) {
                    log.error("[MONITORING, BOT SEND] e: " + e);
                }
            } else {
                log.error("[MONITORING, BOT NOT SEND, getGroupId() == 0] exchange.getBody(): " + exchange.getBody( ));
                return new ResponseEntity<>(exchange.getBody( ), HttpStatus.NOT_ACCEPTABLE);
            }
        }
        log.error("[MONITORING, BAD HttpStatus] exchange.getStatusCode(): " + exchange.getStatusCode( ));
        return new ResponseEntity<>(exchange.getBody( ), HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
    }


    @PostMapping("/test")
    public ResponseEntity<String> getTest(@RequestBody String body) {
        HttpHeaders headers = new HttpHeaders( );

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        body = new String(bytes, StandardCharsets.UTF_8);

        System.out.println("body = " + body);
        log.info(body);
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
