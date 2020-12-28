package com.example.blue2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

<<<<<<< Updated upstream
=======
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

>>>>>>> Stashed changes
import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationResult;
import com.example.blue2.database.Message;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int Request_connect_devices_sequre = 1;
    private static final int Request_enable_bluetooth = 3;

    private BluetoothAdapter bluetoothAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        AppDatabase database = AppDatabase.getDatabase(this);

        Conversation conversation1 = new Conversation();
        conversation1.opponentAddress = "ABC";
        conversation1.opponentName = "Ahmed";
        long conversation1Id = database.conversationDao().insert(conversation1);

        Conversation conversation2 = new Conversation();
        conversation2.opponentAddress = "XYZ";
        conversation2.opponentName = "Galal";
        long conversation2Id = database.conversationDao().insert(conversation2);

        Message c1m1 = new Message();
        c1m1.senderAddress = "ABC";
        c1m1.textBody = "Body 1";
        c1m1.parentConversationId = conversation1Id;
        Message c1m2 = new Message();
        c1m2.senderAddress = null;
        c1m2.textBody = "Body 2";
        c1m2.parentConversationId = conversation1Id;
        database.conversationDao().insert(c1m1);
        database.conversationDao().insert(c1m2);


        Message c2m1 = new Message();
        c2m1.senderAddress = null;
        c2m1.textBody = "Bodyghgsghgsghs 1";
        c2m1.parentConversationId = conversation2Id;
        Message c2m2 = new Message();
        c2m2.senderAddress = "XYZ";
        c2m2.textBody = "Bodyghgsghgsghs 2";
        c2m2.parentConversationId = conversation2Id;
        Message c2m3 = new Message();
        c2m3.senderAddress = "XYZ";
        c2m3.textBody = "Bodyghgsghgsghs 3";
        c2m3.parentConversationId = conversation2Id;
        database.conversationDao().insert(c2m1);
        database.conversationDao().insert(c2m2);
        database.conversationDao().insert(c2m3);

<<<<<<< Updated upstream
        List<ConversationResult> messages = database.conversationDao().getAll();
=======
        List<ConversationResult> messages = database.conversationDao().getAll().getValue();
>>>>>>> Stashed changes
        for(int i = 0; i < messages.size(); i++) {
            ConversationResult result = messages.get(i);
            Log.d("Galal Ahmed", result.conversationId + " " + result.opponentName + " " + result.opponentAddress + " " + result.textBody);
        }
        if (bluetoothAdapter == null) {

            Toast.makeText(this, "Sie haben kein Bluetooth !!", Toast.LENGTH_SHORT).show();

            finish();

        }
    }


    @Override
    protected void onStart() {

        if (bluetoothAdapter.isEnabled()) {


            Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);


            startActivityForResult(enable, Request_enable_bluetooth);
        }


        super.onStart();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            case Request_enable_bluetooth:

                if (requestCode == Activity.RESULT_OK) {


                } else

                    Toast.makeText(this, "Bluetooth nicht an ", Toast.LENGTH_SHORT).show();


        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.blue_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {


            case R.id.secure_connect_scan: {


                Intent intent = new Intent(this, DeviceListActivity.class);

                startActivityForResult(intent, Request_connect_devices_sequre);

                return true;

            }


        }

        return false;

    }
}