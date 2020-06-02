package ru.mtuci.chatclient;

import ru.mtuci.chatclient.chatmessengerapi.Chat;
import ru.mtuci.chatclient.chatmessengerapi.ChatMessengerSession;
import ru.mtuci.chatclient.chatmessengerapi.Message;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args){

        new ChatClient().main();

        System.out.println("+++start+++");

    }
}
