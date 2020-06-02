package ru.mtuci.chatclient.chatmessengerapi;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

public class Message {
    public String chatId;
    public String login;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime time;
    public String message;

    public Message(){}
}
