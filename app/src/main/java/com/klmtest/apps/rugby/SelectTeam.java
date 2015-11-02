package com.klmtest.apps.rugby;

import android.app.Activity;
import android.content.res.AssetManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.klmtest.apps.rugby.DataObjects.Athlete;
import com.klmtest.apps.rugby.DataObjects.Panel;
import com.klmtest.apps.rugby.DataObjects.Squad;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fm on 01/11/2015.
 *
 * Select Team from JSON object containing list of squads and athletes
 */
public class SelectTeam extends Activity {
    private List<Athlete> athletes = new ArrayList<Athlete>(); //array of athletes from JSON Object
    private List<Squad> squads = new ArrayList<Squad>(); //array of squads from JSON Object
    private List<String> squadList = new ArrayList<String>(); // array used to display squad info in Listview
    private HashMap<Integer, Athlete> teamSelection = new HashMap<Integer, Athlete>(); // Final Team Selection <position, name>
    private ListView teamListView, squadListView;
    private Panel panel; // Object representing panel of players and team selection
    private TextView teamTitle; // display team name
    private Button selectBtn;
    private static int ALL_ATHLETES = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_team);

        //initialise UI and Listeners
        initialiseUI();

        //load athletes and squads from JSON object in assets
        loadAthletesAndSquads();

        // check that squad and athletes have been read OK, show error if they haven't
        if(squads.size() <= 0 || athletes.size() <= 0) {
            Log.e("SelectTeam", "Error, unable to read squads and panels");
            String str = "Error, unable to read squads and panels";
            Toast.makeText(getApplicationContext(),
                    "Error, unable to read squads and panels" , Toast.LENGTH_LONG)
                    .show();
            squadList.add(str);

            //disable input if no data available to select
            disableInput();

        } else {
            // Got data, good to go
            // Display list of squads along with number on eligible players
            // cycle through athletes on each squad and add those not injured
            for (int i = 0; i < squads.size(); i++) {
                int squadSize = 0, injured = 0;
                for (int j = 0; j < athletes.size(); j++) {
                    Athlete a = athletes.get(i);
                    if (athletes.get(j).getSquad_id() == squads.get(i).getId() && !athletes.get(j).getIs_injured()) {
                        squadSize++;
                    } else if (athletes.get(j).getSquad_id() == squads.get(i).getId() && athletes.get(j).getIs_injured()){
                        injured++;
                    }
                }
                String str = squads.get(i).getName() + ", " + squads.get(i).getClub() + ". Eligible players: " + squadSize+ ". Injured players: " + injured;
                Log.i("SelectTeam",str);
                squadList.add(str);

            }
        }

        //display list of squads in listview
        //user can select which squad to use
        ArrayAdapter<String> adapterSquad = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.list_text, squadList);
        squadListView.setAdapter(adapterSquad);
    }

    //initialise UI and Listeners
    private void initialiseUI(){
        teamListView = (ListView) findViewById(R.id.teamListView);
        squadListView = (ListView) findViewById(R.id.squadListView);
        squadListView.setOnItemClickListener(squadListener);
        teamTitle = (TextView) findViewById(R.id.teamTextView);
        selectBtn = (Button) findViewById(R.id.selectTeamBtn);
        selectBtn.setOnClickListener(selectClickListener);
    }

    //disable input
    private void disableInput() {
        selectBtn.setClickable(false);
        selectBtn.setEnabled(false);
        squadListView.setOnItemClickListener(null);
    }

    // When this button clicked, add all athletes to the Panel
    View.OnClickListener selectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // call select team method with all athletes
            selectTeam(ALL_ATHLETES);
        }
    };

    // Select which squad to use to select team
    ListView.OnItemClickListener squadListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            // call select team method with selected squad
            selectTeam(squads.get(position).getId());
        }
    };

    // method to create a panel from valid athletes and then select a team
    private void selectTeam(int squadID) {
        //create Panel object to store all eligible players and the chosen team selection
        panel = new Panel();
        // add eligible (non injured) players to panel
        for (int i = 0; i < athletes.size(); i++) {
            if (!athletes.get(i).getIs_injured() &&
                    (squadID == ALL_ATHLETES || athletes.get(i).getSquad_id() == squadID)) {
                panel.addPlayer(athletes.get(i));
            }
        }

        String str;
        String[] teamDisplay = new String[15];

        //check if it's possible to select team, display message if not.
        if (panel.teamValid()) {
            teamSelection = panel.selectTeam();

            //create string arr to display selection in listView
            for (int i = 1; i <= teamSelection.size(); i++) {
                str = i + ". " + teamSelection.get(i).getName() + ", " + teamSelection.get(i).getPosition();
                teamDisplay[i - 1] = str;
            }
            //display team in list view
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.list_text, teamDisplay);
            teamListView.setAdapter(adapter);

            //display team title
            if (squadID == ALL_ATHLETES){
                teamTitle.setText("TEAM SELECTION: All Athletes");
            } else {
                teamTitle.setText("TEAM SELECTION for " +
                        squads.get(squadID).getName() +
                        ", " + squads.get(squadID).getClub() +
                        " (ID: " + squadID + ")" );
            }

        } else {
            // Display error message
            Log.e("SelectTeam", "Error, not enough eligible players to select team ID: ");
            Toast.makeText(getApplicationContext(),
                    "Error, not enough eligible players to select team" , Toast.LENGTH_LONG)
                    .show();
        }
    }


    // method to parse JSON object into squads and athletes
    /*
     *   Athlete data structure
     *   {
     *   "avatar_url": "https://kitmanlabs.com/avatars/athletes/2.jpg",
     *   "squad_id": 8,
     *   "country": "Bulgaria",
     *   "last_played": "Fri Sep 11 2015 14:52:35 GMT+0000 (UTC)",
     *   "name": "Bright Hicks",
     *   "position": "centre",
     *   "is_injured": true,
     *   "id": 2
     *   }
     *
     *   Squad data structure
     *   {
     *    "club": "Comvey",
     *    "name": "Squad 8",
     *    "id": 8
     *   }
     */
    private void loadAthletesAndSquads() {
        try {
            JSONObject inputJSON = new JSONObject(readJSONAsset());
            JSONArray athletesJSONArr = inputJSON.getJSONArray("athletes");
            //parse JSON data into Athletes objects
            for (int i = 0; i < athletesJSONArr.length(); i++) {
                JSONObject obj = athletesJSONArr.getJSONObject(i);
                String q = obj.getString("country");
                Athlete a = new Athlete( obj.getInt("squad_id"),  obj.getString("name"), obj.getString("position"), obj.getBoolean("is_injured"), obj.getInt("id"));
                athletes.add(a);
            }
            JSONArray squadsJSONArr = inputJSON.getJSONArray("squads");
            //parse JSON data into Squads objects
            for (int i = 0; i < squadsJSONArr.length(); i++) {
                JSONObject obj = squadsJSONArr.getJSONObject(i);
                Squad s = new Squad(obj.getString("club"), obj.getString("name"), obj.getInt("id"));
                squads.add(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // method to read JSON object from assets
    private String readJSONAsset() {
        String str = "";
        try {
            AssetManager assetManager = getAssets();
            InputStream is = getAssets().open("athletes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            str = new String(buffer, "UTF-8");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return str;
    }



}
