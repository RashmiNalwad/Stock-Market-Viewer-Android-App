package com.example.rash.stockmarketviewer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView autoComplete;

    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoComplete=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);

        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new MyAsync().execute(autoComplete.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final Button button = (Button)findViewById(R.id.getQuote);
        button.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v)
           {
               Log.i("MainActivity","GetQuoteClicked");
               Intent intent = new Intent(MainActivity.this,Results.class);
               if(autoComplete.getText() != null) {
                   String message = autoComplete.getText().toString();
                   intent.putExtra("SelecctedSymbol", message);
                   startActivity(intent);
                   Log.i("MainActivityIntent","IntentSent");
               }
           }
        });

    }

    class MyAsync extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params)
        {
            URL url = null;
            HttpURLConnection urlConnection= null;
            StringBuilder sb = new StringBuilder();
            try
            {
                url = new URL("http://stockapp571.appspot.com/php?SearchSymbol="+"IT");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
                String thisLine;
                while((thisLine=br.readLine()) != null)
                {
                    sb.append(thisLine);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            ArrayList<String> list = new ArrayList<String>();
            try
            {
                JSONArray jsonArray = new JSONArray(result);
                String Name = "";
                String Symbol = "";
                String Exchange = "";
                String entry = "";
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Name = jsonObject.getString("Name");
                    Symbol = jsonObject.getString("Symbol");
                    Exchange = jsonObject.getString("Exchange");
                    entry = Symbol + " " +  Name + " (" + Exchange + ")";
                    list.add(entry);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            String items[] = list.toArray(new String[0]);
            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_dropdown_item_1line, items);

            autoComplete.setAdapter(adapter);
            autoComplete.setThreshold(1);

        }

    }
}



