package com.example.beernote;

public class Model {
    private int id;
    private String breweryName, beerName, note;
    private byte[] image;

    public Model(int id, String breweryName, String beerName, String note, byte[] image) {
        this.id = id;
        this.breweryName = breweryName;
        this.beerName = beerName;
        this.note = note;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBreweryName() {
        return breweryName;
    }

    public void setBreweryName(String breweryName) {
        this.breweryName = breweryName;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
