package itrans.myjsontesting;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    TextView testingtv;

    //Getting time
    String localTime;
    DateFormat busTimingFormat = new SimpleDateFormat("HH:mm");
    int timeDifference = 0;

    //Internet stuff
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    //RecyclerView stuff
    private RecyclerView busNumberRecyclerView;
    private ArrayList<Buses> busList = new ArrayList<>();
    private BusAdapter adapterRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //For back button...
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        testingtv = (TextView) findViewById(R.id.testingtv) ;

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        busNumberRecyclerView = (RecyclerView) findViewById(R.id.busNumberRecyclerView);
        busNumberRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterRecycler = new BusAdapter(getApplicationContext());

        busNumberRecyclerView.setAdapter(adapterRecycler);
        sendJsonRequest();

    }

    private String getRequestUrl(int busStopID){
        return "http://datamall2.mytransport.sg/ltaodataservice/BusArrival?BusStopID=" +busStopID+ "&SST=True";
    }

    private void sendJsonRequest() {

        final JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, getRequestUrl(55189), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        busList = parseJsonResponse(response);
                        adapterRecycler.setBusList(busList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("AccountKey","6oxHbzoDSzuXhgEvfYLqLQ==");
                headers.put("UniqueUserID","2807eaf2-cf3e-4d9a-8468-edd50fd0c1cd");
                headers.put("accept","application/json");
                return headers;

            }
        };
        requestQueue.add(stringRequest);
    }

    private ArrayList<Buses> parseJsonResponse(JSONObject response) {

        ArrayList<Buses> busList = new ArrayList<>();
        int busArrivingIn;

        if (response == null || response.length() > 0) {
            try {
                JSONArray arrayValue = response.getJSONArray("Services");
                for (int i = 0; i < arrayValue.length(); i++) {
                    JSONObject thisData = arrayValue.getJSONObject(i);
                    int serviceNumber = thisData.getInt("ServiceNo");

                    JSONObject nextBus = thisData.getJSONObject("NextBus");
                    String eta = nextBus.getString("EstimatedArrival"); //12:15:2016T23:59:01+08:00
                    String time = eta.substring(11, 16);
                    busArrivingIn = findArrivalTiming(time);

                    Buses bus = new Buses();
                    bus.setBusNumber(serviceNumber);
                    bus.setNextBusTime(busArrivingIn);

                    busList.add(bus);
                }
                Toast.makeText(getApplicationContext(), busList.toString(), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            } /*finally {
                Toast.makeText(getApplicationContext(), busList.toString(), Toast.LENGTH_LONG).show();

            } */
        }
        return busList;
    }

    public int findArrivalTiming(String time){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
        Date currentLocalTime = cal.getTime();
        localTime = busTimingFormat.format(currentLocalTime);

        String LocalTimeHour = localTime.substring(0, 2);
        int localHour = Integer.parseInt(LocalTimeHour);
        String LocalTimeMinutes = localTime.substring(3, 5);
        int localMin = Integer.parseInt(LocalTimeMinutes);

        String BusTimeHour = time.substring(0, 2);
        int BusHour = Integer.parseInt(BusTimeHour);
        String BusTimeMinutes = time.substring(3, 5);
        int BusMin = Integer.parseInt(BusTimeMinutes);

        if (localHour == BusHour && localMin < BusMin) {
            timeDifference = BusMin - localMin;
        } else if (localHour < BusHour) {
            timeDifference = (BusMin + (BusHour - localHour) * 60) - localMin;
        } else if (localHour > BusHour) {
            timeDifference = (BusMin + ((24 - localHour) + (BusHour - 0)) * 60) - localMin;
        } else if (localHour == BusHour && localMin == BusMin) {
            timeDifference = 0;
        } else{
            return -1;
        }
        return timeDifference;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
