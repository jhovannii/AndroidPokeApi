package com.profuturo.AndroidPokeApi.model;

import java.util.ArrayList;

public class PokemonsMoves {
    private ArrayList<Move> flavor_text_entries;
    private int power;
    private int accuracy;

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public ArrayList<Move> getflavor_text_entries() {
        return flavor_text_entries;
    }

    public void setflavor_text_entries(ArrayList<Move> flavor_text_entries) {
        this.flavor_text_entries = flavor_text_entries;
    }
}
