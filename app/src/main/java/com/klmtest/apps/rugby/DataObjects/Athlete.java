package com.klmtest.apps.rugby.DataObjects;

import java.util.Date;

/**
 * Created by fm on 01/11/2015.
 * Class to represent Athlete
 */
public class Athlete {
    private int squad_id;
    private String name;
    private String position;
    private Boolean is_injured;
    private int id;

    // Constructor
    public Athlete(int squad_id, String name, String position, Boolean is_injured, int id) {
        this.squad_id = squad_id;
        this.name = name;
        this.position = position;
        this.is_injured = is_injured;
        this.id = id;
    }

    // setters and getters


    public int getSquad_id() {
        return squad_id;
    }

    public void setSquad_id(int squad_id) {
        this.squad_id = squad_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getIs_injured() {
        return is_injured;
    }

    public void setIs_injured(Boolean is_injured) {
        this.is_injured = is_injured;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
