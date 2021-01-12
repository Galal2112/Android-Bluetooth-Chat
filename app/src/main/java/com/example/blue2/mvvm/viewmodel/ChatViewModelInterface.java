package com.example.blue2.mvvm.viewmodel;

import android.bluetooth.BluetoothDevice;

import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationDao;

public interface ChatViewModelInterface {

    /**
     * start bluetooth service
     * @param bluetoothDevice the bluetooth device from StartChatActivity
     *
     *
     */
    void start(BluetoothDevice bluetoothDevice);
    /**
     * to get the conversation from Database by the device address
     * if the conversation doesn't exist, then create a new conversation
     * and insert it in the database
     */
    void createOrGetConversation();

    /**
     * this method creates an intent to send message to the service
     * put the extra message and than start service
     * @param textBody message body
     */
    void sendMessage(String textBody);

    /**
     * Methode , um das aktuelle Geraet (mit dem dieses Chat durchgefuert wird) zu setzen .
     *
     * @param mDevice :das aktuelle gebundene Geraet
     */

    void setmDevice(BluetoothDevice mDevice);

    /**
     * Methode , um den aktuellen Conversation abzuholen
     *
     *
     * @return : der  aktuelle Conversation dieses Chat .
     */
    Conversation getmConversation();
    /**
     * Methode , um den aktuellen Converstion zu setzen .
     *
     * @param mConversation :der aktuelle Conversation dieses Chat
     */

    void setmConversation(Conversation mConversation);
    /**
     * Methode , um das aktuelle Data Acsses Objekt fuer Converstion abzuholen
     *
     *
     * @return : das aktuelle Data Acsses Objekt fuer Converstion dieses Chat .
     */
    ConversationDao getmDao();
    /**
     * Methode , um das aktuelle Data Acsses Objekt fuer Converstion zu setzen .
     *
     * @param mDao :das aktuelle Data Acsses Objekt fuer Converstion dieses Chat
     */

    void setmDao(ConversationDao mDao);
    
}
