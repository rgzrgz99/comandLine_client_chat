package ru.mtuci.chatclient.chatmessengerapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatMessengerApi {

    public static String getToken(String endpoint, String login, String passwordHash, String name){
        try{
            URL url = new URL(endpoint + "/get-token?login=" + login +
                    "&password=" + passwordHash +
                    (name == null ? "" : "&name=" + name)    );

            ObjectMapper mapper= new ObjectMapper();
            Map<String,Object> resp = mapper.readValue(url, Map.class);

            if(resp.containsKey("token")) {
                return resp.get("token").toString();
            }
        } catch (IOException e){
            System.out.println(e);
        }

        return "error22";
    }

    public static String sendMessage(String endpoint, String token, Integer chatId, String message){
        try{
            URL url = new URL(endpoint + "/send-message?token=" + token +
                    "&chatid=" + chatId.toString() +
                    "&message=" + URLEncoder.encode(message)   );

            ObjectMapper mapper= new ObjectMapper();
            Map<String,Object> resp = mapper.readValue(url, Map.class);

            if(resp.containsKey("status")) {
                return resp.get("status").toString();
            }
        } catch (IOException e){
            System.out.println(e);
        }

        return "error";
    }

    public static List<Message> getMessage(String endpoint, String token, LocalDateTime start, Integer chatid){
        try{
            URL url = new URL(endpoint + "/get-message?token=" + token +
                    (start == null ? "" : "&start=" + start.toString()) +
                    (chatid == null ? "" : "&chatid=" + chatid.toString()) );

            ArrayList<Message> resp;

            ObjectMapper mapper= new ObjectMapper();
            resp = mapper.readValue(url, ArrayList.class);

            return mapper.convertValue(resp,  new TypeReference<List<Message>>() { });
        } catch (IOException e){
            System.out.println(e);
        }

        return new ArrayList<Message>();
    }


    public static List<Chat> getChats(String endpoint, String token){
        try{
            URL url = new URL(endpoint + "/chat-list?token=" + token);

            ArrayList<Chat> resp;

            ObjectMapper mapper= new ObjectMapper();
            resp = mapper.readValue(url, ArrayList.class);

            return mapper.convertValue(resp,  new TypeReference<List<Chat>>() { });
        } catch (IOException e){
            System.out.println(e);
        }

        return new ArrayList<Chat>();
    }


    public static List<String> getChatMembers(String endpoint, String token, Integer chatId){
        try{
            URL url = new URL(endpoint + "/chat-members-list?token=" + token + "&chatid=" + chatId.toString());

            ArrayList<String> resp;

            ObjectMapper mapper= new ObjectMapper();
            resp = mapper.readValue(url, ArrayList.class);

            return mapper.convertValue(resp,  new TypeReference<List<String>>() { });
        } catch (IOException e){
            System.out.println(e);
        }

        return new ArrayList<String>();
    }

    public static String addChat(String endpoint, String token, String name){
        try{
            URL url = new URL(endpoint + "/add-chat?token=" + token +
                    "&name=" + name);

            ObjectMapper mapper= new ObjectMapper();
            Map<String,Object> resp = mapper.readValue(url, Map.class);

            if(resp.containsKey("status")) {
                return resp.get("status").toString();
            }
        } catch (IOException e){
            System.out.println(e);
        }

        return "error";
    }

    public static String addUserToChat(String endpoint, String token, Integer chatid, String login){
        try{
            URL url = new URL(endpoint + "/add-user-to-chat?token=" + token +
                    "&chatid=" + chatid.toString() +
                    "&login=" + login);

            ObjectMapper mapper= new ObjectMapper();
            Map<String,Object> resp = mapper.readValue(url, Map.class);

            if(resp.containsKey("status")) {
                return resp.get("status").toString();
            }
        } catch (IOException e){
            System.out.println(e);
        }

        return "error";
    }

}
