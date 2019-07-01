package com.trigger.snha.dto;

public class Joke {
    int position;
    String joke;

    // needed for cupboard
    public Joke() {

    }

    public Joke(int position, String joke) {
        this.position = position;
        this.joke = joke;
    }

    public String getJoke() {
        return joke;
    }

    public int getPosition() {
        return position;
    }
}
