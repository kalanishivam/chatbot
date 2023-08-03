package com.example.chatbot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    ImageButton sendBtn;
    MessagesList messagesList;
    User us, chatgpt;
    MessagesListAdapter<Message> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextTextPersonName);
        sendBtn = findViewById(R.id.imageButton);
        messagesList = findViewById(R.id.messagesList);

        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.get().load(url).into(imageView);

            }
        };
        adapter = new MessagesListAdapter<Message>("1" , imageLoader);
        messagesList.setAdapter(adapter);

        us = new User("1" , "SHIVAM" , "");
        chatgpt = new User("2" , "chatgpt" , "");


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message("m1" ,editText.getText().toString(), us, Calendar.getInstance().getTime(), null);
                adapter.addToStart(message, true);
                if(editText.getText().toString().toLowerCase().startsWith("generate image")){
                    generateImage(editText.getText().toString());
                }else {
                    performAction(editText.getText().toString());
                }
                editText.setText("");
            }
        });
    }

    public void performAction(String inputText){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openai.com/v1/completions";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("prompt" , inputText);
            jsonObject.put("model" , "text-davinci-003");
            jsonObject.put("max_tokens" , 200);

// Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url ,jsonObject ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String answer = null;
                        try {
                            answer = response.getJSONArray("choices").getJSONObject(0).getString("text");
                            Message message = new Message("M2", answer.trim() , chatgpt , Calendar.getInstance().getTime() , null);
                            adapter.addToStart(message, true);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        // Display the first 500 characters of the response string.

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String , String> map = new HashMap<>();
                map.put("Content-Type" , "application/json");
                map.put("Authorization" , "Bearer ${API_KEY}");
                return map;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 15;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    public void generateImage(String inputText){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openai.com/v1/images/generations";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("prompt" , inputText);
            jsonObject.put("n" , 2);
            jsonObject.put("size" , "512x512");

// Request a string response from the provided URL.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url ,jsonObject ,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String answer = null;
                            try {
                                answer = response.getJSONArray("data").getJSONObject(0).getString("url");
                                Message message = new Message("M2", "image" , chatgpt , Calendar.getInstance().getTime() , answer.trim());
                                adapter.addToStart(message, true);
                                Log.d("tryImage", answer);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            // Display the first 500 characters of the response string.

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String , String> map = new HashMap<>();
                    map.put("Content-Type" , "application/json");
                    map.put("Authorization" , "Bearer ${API_KEY}");
                    return map;
                }
            };
            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 15;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

// Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}