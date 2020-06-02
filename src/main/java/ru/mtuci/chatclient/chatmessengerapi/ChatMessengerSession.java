package ru.mtuci.chatclient.chatmessengerapi;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

public class ChatMessengerSession {

    String endpoint = null;
    String login = null;
    String token = null;
    String sessionName = null;

    public ChatMessengerSession(String _endpoint, String _login, String password){
        endpoint = _endpoint;
        login = _login;
        sessionName = "noname";

        token = ChatMessengerApi.getToken(endpoint, login, password/*getCryptoHash(password, "SHA-256")*/, sessionName);
        System.out.print(token);
    }

    public ChatMessengerSession(String _endpoint, String _login, String password, String _name){
        endpoint = _endpoint;
        login = _login;
        sessionName = _name;

        token = ChatMessengerApi.getToken(endpoint, login, getCryptoHash(password, "SHA-256"), sessionName);
    }



    public List<Message> getMessage(){
        return ChatMessengerApi.getMessage(endpoint, token, null, null);
    }

    public List<Message> getMessage(LocalDateTime start){
        return ChatMessengerApi.getMessage(endpoint, token, start, null);
    }

    public List<Message> getMessage(Integer chatid){
        return ChatMessengerApi.getMessage(endpoint, token, null, chatid);
    }

    public List<Message> getMessage(LocalDateTime start, Integer chatid){
        return ChatMessengerApi.getMessage(endpoint, token, start, chatid);
    }


    public String sendMessage(Integer chatid, String message){
        return ChatMessengerApi.sendMessage(endpoint, token, chatid, message);
    }


    public List<Chat> getChats(){
        return ChatMessengerApi.getChats(endpoint, token);
    }

    public List<String> getChatMembers(Integer chatid){
        return ChatMessengerApi.getChatMembers(endpoint, token, chatid);
    }


    public String addChat(String name){
        return ChatMessengerApi.addChat(endpoint, token, name);
    }

    public String addUserToChat(Integer chatid, String login){
        return ChatMessengerApi.addUserToChat(endpoint, token, chatid, login);
    }






    public static String getCryptoHash(String input, String algorithm) {
        try {
            //MessageDigest classes Static getInstance method is called with MD5 hashing
            MessageDigest msgDigest = MessageDigest.getInstance(algorithm);

            //digest() method is called to calculate message digest of the input
            //digest() return array of byte.
            byte[] inputDigest = msgDigest.digest(input.getBytes());

            // Convert byte array into signum representation
            // BigInteger class is used, to convert the resultant byte array into its signum representation
            BigInteger inputDigestBigInt = new BigInteger(1, inputDigest);

            // Convert the input digest into hex value
            String hashtext = inputDigestBigInt.toString(16);

            //Add preceding 0's to pad the hashtext to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        // Catch block to handle the scenarios when an unsupported message digest algorithm is provided.
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
