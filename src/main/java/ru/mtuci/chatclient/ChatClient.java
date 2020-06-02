package ru.mtuci.chatclient;

import ru.mtuci.chatclient.chatmessengerapi.Chat;
import ru.mtuci.chatclient.chatmessengerapi.ChatMessengerSession;
import ru.mtuci.chatclient.chatmessengerapi.Message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ChatClient {

    ChatMessengerSession chatMessengerSession = null;
    Integer currentChatId = -1;
    Long currentChatLastUpdate = null;

    String secretKey = null;


    public void initSession(){
        String endpoint = "http://127.0.0.1:8081/messenger";
        String login;
        String password;
        Scanner  scanner = new Scanner(System.in);
        System.out.println("Enter endpoint (def: http://127.0.0.1:8081/messenger)");
        for (;!scanner.hasNextLine();){}
        String line = scanner.nextLine();
        if(!line.equals("")){
            endpoint = line;
        }

        System.out.println("Enter login");
        for (;!scanner.hasNextLine();){}
        login = scanner.nextLine();

        System.out.println("Enter password");
        for (;!scanner.hasNextLine();){}
        password = scanner.nextLine();

        chatMessengerSession = new ChatMessengerSession(endpoint, login, password);

        System.out.println("====Success Authorization====");
    }
    public void main(){

        initSession();

        getListChats();





        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner  scanner = new Scanner(System.in);
                for(;;) {
                    for (;scanner.hasNextLine();) {
                        resolver(scanner.nextLine());
                    }
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }

                }
            }
        });

        t.start();

        for(;;){
            if(currentChatId != -1) {
                List<Message> newMessages = chatMessengerSession.getMessage(LocalDateTime.ofEpochSecond(currentChatLastUpdate, 0, ZoneOffset.UTC), currentChatId);
                currentChatLastUpdate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

                for(Message message : newMessages){
                    if(secretKey == null) {
                        System.out.println(message.time.format(DateTimeFormatter.ofPattern("HH:mm")) + "    " + message.login +
                                "\n" + message.message);
                    } else {
                        if(message.message.startsWith("secret:")){
                            System.out.println(message.time.format(DateTimeFormatter.ofPattern("HH:mm")) + "    " + message.login +
                                    "\n" + AES.decrypt(message.message.substring("secret:".length()), secretKey));
                        } else {
                            System.out.println(message.time.format(DateTimeFormatter.ofPattern("HH:mm")) + "    " + message.login +
                                    "\n" + message.message);
                        }
                    }
                }
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }


    }



    public void resolver(String line){
        if(line.startsWith("/")){
            commandResolver(line);
            return ;
        }

        if(currentChatId != -1){
            if(secretKey == null) {
                chatMessengerSession.sendMessage(currentChatId, line);
            } else {
                chatMessengerSession.sendMessage(currentChatId, "secret:" + AES.encrypt(line, secretKey));
            }
        }

    }

    public void commandResolver(String line){
        if(line.startsWith("/list")){
            clearPlant();
            getListChats();
        } else if(line.startsWith("/go ")){
            clearPlant();
            Integer num = !line.split(" ")[1].equals("") ? Integer.valueOf(line.split(" ")[1]) : 0;
            goToChatByNum(num);
        } else if(line.startsWith("/members ")){
            clearPlant();
            Integer num = !line.split(" ")[1].equals("") ? Integer.valueOf(line.split(" ")[1]) : 0;
            getChatMembersByNum(num);
        } else if(line.startsWith("/newchat ")){
            clearPlant();
            String name = !line.split(" ")[1].equals("") ? line.split(" ")[1] : "note-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            addNewChat(name);
        } else if(line.startsWith("/addtochat ")){
            String login = !line.split(" ")[1].equals("") ? line.split(" ")[1] : "";
            if(login.equals("") || currentChatId == -1){
                return ;
            }
            addMemberToChat(currentChatId, login);
        } else if(line.startsWith("/key ")){
            clearPlant();
            secretKey = !line.split(" ")[1].equals("") ? line.split(" ")[1] : null;
            goToChat(currentChatId);
        } else if(line.startsWith("/key")){
            clearPlant();
            secretKey = null;
            goToChat(currentChatId);
        } else {
            help();
        }
    }


    public void getListChats(){
        currentChatId = -1;

        System.out.println("====Your Chats====");

        Integer i = 0;
        List<Chat> lc = chatMessengerSession.getChats();
        for (Chat c : lc){
            System.out.println(i.toString() + ":" + c.name);
            i++;
        }

    }

    public void goToChatByNum(Integer num){
        List<Chat> lc = chatMessengerSession.getChats();
        System.out.println("====" + lc.get(num).name + "====");
        goToChat(Integer.valueOf(lc.get(num).chatId));
    }
    public void goToChat(Integer chatid){
        currentChatId = chatid;
        currentChatLastUpdate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        List<Message> lm = chatMessengerSession.getMessage(chatid);
        for (Message message : lm){
            if(secretKey == null) {
                System.out.println(message.time.format(DateTimeFormatter.ofPattern("HH:mm")) + "    " + message.login +
                        "\n" + message.message);
            } else {
                if(message.message.startsWith("secret:")){
                    System.out.println(message.time.format(DateTimeFormatter.ofPattern("HH:mm")) + "    " + message.login +
                            "\n" + AES.decrypt(message.message.substring("secret:".length()), secretKey));
                } else {
                    System.out.println(message.time.format(DateTimeFormatter.ofPattern("HH:mm")) + "    " + message.login +
                            "\n" + message.message);
                }
            }
        }
    }

    public void getChatMembersByNum(Integer num){
        List<Chat> lc = chatMessengerSession.getChats();
        System.out.println("====Members of " + lc.get(num).name + "====");
        getChatMembers(Integer.valueOf(lc.get(num).chatId));
    }
    public void getChatMembers(Integer chatid){
        currentChatId = -1;
        List<String> lcm = chatMessengerSession.getChatMembers(Integer.valueOf(chatid));
        for (String cm : lcm){
            System.out.println(cm);
        }
    }

    public void addMemberToChat(Integer chatid, String login){
        if(chatMessengerSession.addUserToChat(chatid, login).equals("error")){
            System.out.println("--Error--");
        } else {
            System.out.println("--User was added--");
        }

    }


    public void addNewChat(String name){
        if(chatMessengerSession.addChat(name).equals("error")){
            System.out.println("--Error--");
        } else {
            System.out.println("--Chat was created--");
            goToChatByNum(chatMessengerSession.getChats().size() - 1);
        }
    }

    public void help(){
        System.out.println("/help");
        System.out.println("/list");
        System.out.println("/go <num>");
        System.out.println("/members <num>");
        System.out.println("/newchat <name>");
        System.out.println("/addtochat <login>");
    }
    public void clearPlant(){
        for(int i = 0; i < 25; i++){
            System.out.println("");
        }
    }

}
