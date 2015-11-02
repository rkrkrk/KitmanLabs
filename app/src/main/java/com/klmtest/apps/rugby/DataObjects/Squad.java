package com.klmtest.apps.rugby.DataObjects;

/**
 * Created by fm on 01/11/2015.
 *
 * Class to represent Squads
 */
public class Squad {
    private String club;
    private String name;
    private int id;

    //Constructor
    public Squad(String club, String name, int id) {
        this.club = club;
        this.name = name;
        this.id = id;
    }

    //Setters and Getters
    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

