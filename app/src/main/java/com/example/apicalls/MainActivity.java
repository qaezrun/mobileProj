package com.example.apicalls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText code,message;
    String nickname = null,myMessage = null;
    List<Users> users; //Stores data from API JSON
    Adapter adapter;
    private String url = "https://mocki.io/v1/de3e3ce2-19d7-4dc4-8080-18629d56af95";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        code = findViewById(R.id.codeName);
        message = findViewById(R.id.message);
        users = new ArrayList<>();
        getReq();
    }
    public void send(View view) {
        nickname = code.getText().toString();
        myMessage = message.getText().toString();
        if(nickname.equals("") || myMessage.equals("") ){
            Toast.makeText(this, "Please input message first", Toast.LENGTH_SHORT).show();
        }else{
            closeKeyboard();
            Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("message", myMessage);
                jsonObject.put("codeName", nickname);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            postReq(jsonObject);
        }
    }


    private void getReq() { //get REST
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject userObject = response.getJSONObject(i);
                        String fullName = userObject.getString("firstName")+" "+userObject.getString("lastName");
                        String uname = "@"+userObject.getString("username");
                        Users user = new Users(fullName,uname,userObject.getString("image"));
                        users.add(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new Adapter(getApplicationContext(),users);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "onErrorResponse: "+ error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);
    }

    private void postReq(JSONObject jsonObject) { //post REST
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://encouraging-fawn-gown.cyclic.app/messages",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            code.setText("");
                            message.setText("");
                            String res = response.getString("header")+"\n"+response.getString("desc");
                            Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}