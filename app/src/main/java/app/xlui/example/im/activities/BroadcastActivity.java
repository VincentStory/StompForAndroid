package app.xlui.example.im.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import app.xlui.example.im.R;
import app.xlui.example.im.conf.Const;
import app.xlui.example.im.util.StompUtils;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;
import ua.naiksoftware.stomp.provider.OkHttpConnectionProvider;

import static ua.naiksoftware.stomp.Stomp.ConnectionProvider.OKHTTP;

@SuppressWarnings({"FieldCanBeLocal", "ResultOfMethodCallIgnored", "CheckResult"})
public class BroadcastActivity extends AppCompatActivity {
    private Button broadcastButton;
    private Button groupButton;
    private Button chatButton;

    private EditText nameText;
    private Button sendButton;
    private TextView resultText;
    private StompClient stompClient;

    private void init() {
        broadcastButton = findViewById(R.id.broadcast);
        broadcastButton.setEnabled(true);
        groupButton = findViewById(R.id.groups);
        chatButton = findViewById(R.id.chat);
        nameText = findViewById(R.id.name);
        sendButton = findViewById(R.id.send);
        resultText = findViewById(R.id.show);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        this.init();


        broadcastButton.setOnClickListener(l -> {
            stompClient = Stomp.over(OKHTTP, Const.address);

            stompClient.lifecycle().subscribe(lifecycleEvent -> {
                //关注lifecycleEvent的回调来决定是否重连
                switch (lifecycleEvent.getType()) {
                    case OPENED:

                        Log.d(Const.TAG, "forlan debug stomp connection opened");
                        break;
                    case ERROR:

                        Log.e(Const.TAG, "forlan debug stomp connection error is ", lifecycleEvent.getException());
                        break;
                    case CLOSED:

                        Log.d(Const.TAG, "forlan debug stomp connection closed");
                        break;
                }
            });

            Toast.makeText(this, "Start connecting to server", Toast.LENGTH_SHORT).show();
            // Connect to WebSocket server

            ArrayList<StompHeader> headers = new ArrayList<>();
            headers.add(new StompHeader("userId", "103"));
            //这里必须添加headers 否则会报错 headers可以添加用户的认证相关信息
            stompClient.connect(headers);

            // 订阅消息
            Log.i(Const.TAG, "Subscribe broadcast endpoint to receive response");

            stompClient.topic(Const.broadcastResponse)
                    .subscribe(new DisposableSubscriber<StompMessage>() {
                        @Override
                        public void onNext(StompMessage stompMessage) {

                            Log.d(Const.TAG, "Received== " + stompMessage.toString());
                            Log.i(Const.TAG, "Receive: " + stompMessage.getPayload());
                            runOnUiThread(() -> {
                                try {
                                    JSONObject jsonObject = new JSONObject(stompMessage.getPayload());
                                    resultText.append(jsonObject.getString("response") + "\n");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.e(Const.TAG, "Stomp topic error", t);
                        }

                        @Override
                        public void onComplete() {
                            Log.e(Const.TAG, "Stomp connection onComplete");
                        }
                    });


        });
        sendButton.setOnClickListener(v -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", nameText.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            stompClient.send(Const.broadcast).subscribe();
//            stompClient.send(new StompMessage(
//                    // Stomp command
//                    StompCommand.SEND,
//                    // Stomp Headers, Send Headers with STOMP
//                    // the first header is required, and the other can be customized by ourselves
//                    Arrays.asList(
//                            new StompHeader(StompHeader.DESTINATION, Const.broadcast),
//                            new StompHeader("authorization", "this is a token generated by your code!")
//                    ),
//                    // Stomp payload
//                    jsonObject.toString())
//            ).subscribe();
            nameText.setText("");
        });

        groupButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(BroadcastActivity.this, GroupActivity.class);
            startActivity(intent);
            this.finish();
        });
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(BroadcastActivity.this, ChatActivity.class);
            startActivity(intent);
            this.finish();
        });
    }


}
