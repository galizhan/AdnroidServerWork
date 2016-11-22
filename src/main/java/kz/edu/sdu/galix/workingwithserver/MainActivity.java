package kz.edu.sdu.galix.workingwithserver;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private String TAG = "NEW";
    private ListView lv;
    EditText ed1,ed2;
    TextView tv;
    String secret="";
    ProgressBar pb;
    ArrayList<HashMap<String, String>> contactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetContacts().execute();
        ed1 = (EditText) findViewById(R.id.ed1);
        ed2 = (EditText) findViewById(R.id.ed2);
        tv = (TextView) findViewById(R.id.txt);
        contactList = new ArrayList<>();
        pb = (ProgressBar)findViewById(R.id.pb);
    }
    public void Log_in(View v){
        String name,pass;
        name = ed1.getText().toString();
        pass = ed2.getText().toString();
        if(isUser(name,pass)){
            tv.setTextColor(Color.GREEN);
            tv.setText(secret);
        ed1.setText("");
        ed2.setText("");}
        else {tv.setText("Wrong name or pass");
            tv.setTextColor(Color.RED);
        }
    }
    public boolean isUser(String name,String pass){
        int count=0;
        for (HashMap<String, String> map :contactList) {
            if(name.equals(map.get("name")) && pass.equals(map.get("pass"))) {count=1;
            secret=map.get("sec");

            }

        }
        Log.d(TAG,"sec"+secret);
        if(count==1) return true;
        else return false;
    }
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
//            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://salty-earth-81539.herokuapp.com/";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("users");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String name = c.getString("name");
                        String pass = c.getString("pass");
                        String sec = c.getString("sec");



                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("name", name);
                        contact.put("pass", pass);
                        contact.put("sec",  sec);

                        // adding contact to contact list
                       contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pb.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this,"Json Data downloading is finished",Toast.LENGTH_LONG).show();
        }
    }

}
