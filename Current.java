package com.example.rash.stockmarketviewer;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rash on 4/15/2016.
 */
public class Current extends Fragment
{
    View rootView;
    ListView listView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String symbol=getArguments().getString("symbol");
        rootView =  inflater.inflate(R.layout.activity_current, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        buildCurrentTable();
    }

    private void buildCurrentTable() {
        final String symbol=getArguments().getString("symbol");
        final Map<String,String> quoteResults = new HashMap<String,String>();
        final LinearLayout mainLayout = (LinearLayout) rootView.findViewById(R.id.current_layout);
        mainLayout.removeAllViews();
        new AsyncTask<String, Integer, String>()
        {
            @Override
            protected String doInBackground( String... params )
            {
                String fullUrl = params[0];
                fullUrl = fullUrl.trim();
                fullUrl = "http://stockapp571.appspot.com/php?Symbol=" + fullUrl;

                URL url = null;
                HttpURLConnection urlConnection = null;
                StringBuilder sb = new StringBuilder();
                Log.d( "Quote", fullUrl );
                try
                {
                    url = new URL( fullUrl );
                    urlConnection = ( HttpURLConnection ) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader( in );
                    BufferedReader br = new BufferedReader( isr );
                    String thisLine;
                    while ( ( thisLine = br.readLine() ) != null )
                    {
                        sb.append( thisLine );
                    }
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
                finally
                {
                    if ( urlConnection != null )
                        urlConnection.disconnect();
                }
                return sb.toString();
            }

            protected void onPostExecute( String result )
            {
                if ( result == null || result.trim().isEmpty() )
                {
                    return;
                }
                JSONObject jsonObject;
                try
                {
                    jsonObject = new JSONObject( result );
                    String status = jsonObject.getString("Status");
                    String name = jsonObject.getString("Name");
                    String symbol = jsonObject.getString("Symbol");
                    String price = jsonObject.getString("LastPrice");
                    String change = jsonObject.getString("Change");
                    String changepercent = jsonObject.getString("ChangePercent");
                    String timestamp = jsonObject.getString("Timestamp");
                    String mdate = jsonObject.getString("MSDate");
                    String marketcap = jsonObject.getString("MarketCap");
                    String volume = jsonObject.getString("Volume");
                    String changeytd = jsonObject.getString("ChangeYTD");
                    String changepercentytd = jsonObject.getString("ChangePercentYTD");
                    String high = jsonObject.getString("High");
                    String low = jsonObject.getString("Low");
                    String open = jsonObject.getString("Open");
                    quoteResults.put("status",status);
                    quoteResults.put("name",name);
                    quoteResults.put("symbol",symbol);
                    quoteResults.put("price",price);
                    quoteResults.put("change",change);
                    quoteResults.put("changePercent",changepercent);
                    quoteResults.put("timestamp",timestamp);
                    quoteResults.put("mdate",mdate);
                    quoteResults.put("market cap",marketcap);
                    quoteResults.put("volume",volume);
                    quoteResults.put("changeytd",changeytd);
                    quoteResults.put("changepercentytd",changepercentytd);
                    quoteResults.put("high",high);
                    quoteResults.put("low",low);
                    quoteResults.put("open",open);

                    for(Map.Entry<String,String> entry:quoteResults.entrySet())
                    {
                        RelativeLayout row = new RelativeLayout(getActivity());
                        row.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 175));
                        mainLayout.addView(row);

                        LinearLayout col1 = new LinearLayout(getActivity());
                        col1.setOrientation(LinearLayout.VERTICAL);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                        col1.setLayoutParams(params);
                        row.addView(col1);

                        TextView t1 = new TextView(getActivity());
                        t1.setText(entry.getKey());
                        t1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        t1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 32);
                        t1.setTextColor(Color.parseColor("#000000"));
                        t1.setAllCaps(true);
                        col1.addView(t1);

                        TextView t2 = new TextView(getActivity());
                        t2.setText(entry.getValue());
                        t2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        t2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48);
                        t2.setTextColor(Color.parseColor("#000000"));
                        col1.addView(t2);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.execute( symbol );
    }
}
