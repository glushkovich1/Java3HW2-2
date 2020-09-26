package Glushkovich.Chat.Network;

import Glushkovich.Chat.Message;

import java.util.List;

public interface IncomeMessageHandler {
    void handleMessage(Message message);

    void addOnlineUser(String userName);

    void removeOnlineUser(String userName);

    void setOnlineUsersList(List<String> users);

}