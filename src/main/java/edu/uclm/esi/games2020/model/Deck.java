package edu.uclm.esi.games2020.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck(Suit suit) {
        this.cards = new ArrayList<>();
        if(suit == Suit.BLACK)
        	this.buildBlack();
        else {
        	this.buildWhite();
        }
        
    }

	public void suffle() {
        SecureRandom dado = new SecureRandom();
        for (int i = 0; i < 200; i++) {
            int a = dado.nextInt(40);
            int b = dado.nextInt(40);
            Card auxiliar = this.cards.get(a);
            this.cards.set(a, this.cards.get(b));
            this.cards.set(b, auxiliar);
        }
    }

    public Card getCard() {
        return this.cards.remove(0);
    }
    
    private void buildWhite() {
		// TODO Auto-generated method stub
		
	}

	private void buildBlack() {
		// TODO Auto-generated method stub
		
	}
    
}
