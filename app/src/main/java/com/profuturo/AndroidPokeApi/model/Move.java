package com.profuturo.AndroidPokeApi.model;

import java.io.Serializable;

public class Move implements Serializable {
    private String flavor_text;

    public String getFlavor_text() {
        return flavor_text;
    }

    public void setFlavor_text(String flavor_text) {
        this.flavor_text = flavor_text;
    }
}
