package com.klmtest.apps.rugby.DataObjects;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by fm on 01/11/2015.
 *
 * Class to represent a valid Panel from which team may be selected
 */
public class Panel {
    private List<Athlete> props; // arrays to store Athletes in correct position
    private List<Athlete> hookers;
    private List<Athlete> locks;
    private List<Athlete> flankers;
    private List<Athlete> scrumhalfs;
    private List<Athlete> outhalfs;
    private List<Athlete> number8s;
    private List<Athlete> centres;
    private List<Athlete> wingers;
    private List<Athlete> fullbacks;
    private int panelNum; // number of valid athletes in panel
    private HashMap<Integer, Athlete> teamSelection; // stores team selection in format <position number, Athlete>

    //Constructor
    public Panel() {
        //Initialise arrays to sort added Athletes according to their position
        this.props = new ArrayList<Athlete>();
        this.hookers = new ArrayList<Athlete>();
        this.locks = new ArrayList<Athlete>();
        this.flankers = new ArrayList<Athlete>();
        this.number8s = new ArrayList<Athlete>();
        this.scrumhalfs = new ArrayList<Athlete>();
        this.outhalfs = new ArrayList<Athlete>();
        this.centres = new ArrayList<Athlete>();
        this.wingers = new ArrayList<Athlete>();
        this.fullbacks = new ArrayList<Athlete>();
        this.panelNum = 0;
    }

    // method to add a player to panel
    // Players are sorted into arrays based on their position
    public void addPlayer(Athlete athlete) {
        boolean posnOK = true;
        switch (athlete.getPosition()){
            case "prop":
                props.add(athlete);
                break;
            case "hooker":
                hookers.add(athlete);
                break;
            case "lock":
                locks.add(athlete);
                break;
            case "flanker":
                flankers.add(athlete);
                break;
            case "number-eight":
                number8s.add(athlete);
                break;
            case "scrum-half":
                scrumhalfs.add(athlete);
                break;
            case "out-half":
                outhalfs.add(athlete);
                break;
            case "centre":
                centres.add(athlete);
                break;
            case "winger":
                wingers.add(athlete);
                break;
            case "full-back":
                fullbacks.add(athlete);
                break;
            default:
                posnOK = false;
                break;
        }
        //only increment eligible players it they have a correct assigned position
        if(posnOK){
            this.panelNum++;
        }
    }

    // Method to determine if a valid team selection may be created from the panel
    // Must be greater than 15 players
    // Must be enough players in each position
    public boolean teamValid() {
        if (this.panelNum < 15) {
            Log.e("Panel", "InValid - Less than 15 players on panel");
        }
        if (this.props.size() < 2) return false;
        if (this.hookers.size() < 1) return false;
        if (this.locks.size() < 2) return false;
        if (this.flankers.size() < 2) return false;
        if (this.number8s.size() < 1) return false;
        if (this.scrumhalfs.size() < 1) return false;
        if (this.outhalfs.size() < 1) return false;
        if (this.centres.size() < 2) return false;
        if (this.wingers.size() < 2) return false;
        if (this.fullbacks.size() < 1) return false;
        Log.i("Panel", "team is selectable");
        return true;
    }

    // Method to select team by randomly picking athlete for each position
    public HashMap<Integer, Athlete> selectTeam (){

        if(!teamValid()) return null;

        this.teamSelection = new HashMap<Integer, Athlete>();


        //depending on the position we need either one or two players returned
        // getPlayer method return a single Athlete from the relevant position array
        // get2Player method returns an array containing 2 Athletes from the relevant position array
        Athlete[] a = new Athlete[2];
        a = get2Players(props);
        teamSelection.put(1,a[0]);
        teamSelection.put(3,a[1]);
        teamSelection.put(2,getPlayer(hookers));
        a = get2Players(locks);
        teamSelection.put(4,a[0]);
        teamSelection.put(5,a[1]);
        a = get2Players(flankers);
        teamSelection.put(6,a[0]);
        teamSelection.put(7,a[1]);
        teamSelection.put(8,getPlayer(number8s));
        teamSelection.put(9,getPlayer(scrumhalfs));
        teamSelection.put(10,getPlayer(outhalfs));
        a = get2Players(wingers);
        teamSelection.put(11,a[0]);
        teamSelection.put(14,a[1]);
        a = get2Players(centres);
        teamSelection.put(12,a[0]);
        teamSelection.put(13,a[1]);
        teamSelection.put(15,getPlayer(fullbacks));

        return teamSelection;

    }

    private Athlete getPlayer(List<Athlete> athletes){
        // return single player form array
        // if more than one player select randomly
        if(athletes.size() == 1) {
            return athletes.get(0);
        } else {
            Random r = new Random();
            int idx = r.nextInt(athletes.size());
            return athletes.get(idx);
        }
    }

    private Athlete[] get2Players(List<Athlete> athletes){
        // return dual player form array
        // if more than two players select randomly
        if(athletes.size() == 2) {
            return new Athlete[]{athletes.get(0), athletes.get(1)} ;
        } else {
            List<Athlete> players = athletes;
            Athlete[] rtnPlayers = new Athlete[2];
            Random r = new Random();
            int idx = r.nextInt(players.size());
            rtnPlayers[0] =  athletes.get(idx);
            players.remove(idx);
            r = new Random();
            idx = r.nextInt(athletes.size());
            rtnPlayers[1] =  players.get(idx);
            return rtnPlayers;
        }
    }

}
