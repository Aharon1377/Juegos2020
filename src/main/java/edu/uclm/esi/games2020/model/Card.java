package edu.uclm.esi.games2020.model;

import org.json.JSONObject;

public class Card {
    private String text;
    private Suit suit;
    private IState state;
    
    public Card(String text, Suit suit) {
        super();
        this.text = text;
        this.suit = suit;
        
    }

    public JSONObject toJSON() {
        JSONObject jso = new JSONObject();
        jso.put("text", this.text);
        jso.put("suit", this.suit);
        return jso;
    }

	public void setState(IState state) {
		this.state = state;
	}

}
