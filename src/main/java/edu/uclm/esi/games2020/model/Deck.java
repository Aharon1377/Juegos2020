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
            int a = dado.nextInt(this.cards.size());
            int b = dado.nextInt(this.cards.size());
            Card auxiliar = this.cards.get(a);
            this.cards.set(a, this.cards.get(b));
            this.cards.set(b, auxiliar);
        }
    }

    public Card getCard() {
        return this.cards.remove(0);
    }
    
    private void buildWhite() {
		Card prueba = new Card("Prueba1", Suit.WHITE);
		this.cards.add(prueba);
		prueba = new Card("Prueba2", Suit.WHITE);
		this.cards.add(prueba);
		prueba = new Card("Prueba3", Suit.WHITE);
		this.cards.add(prueba);
		prueba = new Card("Prueba4", Suit.WHITE);
		this.cards.add(prueba);
		prueba = new Card("Prueba5", Suit.WHITE);
		this.cards.add(prueba);
		prueba = new Card("Prueba6", Suit.WHITE);
		this.cards.add(prueba);
		prueba = new Card("Prueba7", Suit.WHITE);
		this.cards.add(prueba);
		prueba = new Card("Prueba8", Suit.WHITE);
		this.cards.add(prueba);
		prueba = new Card("Prueba9", Suit.WHITE);
		this.cards.add(prueba);
		prueba = new Card("Prueba10", Suit.WHITE);
		this.cards.add(prueba);

		
	}

	private void buildBlack() {
		Card prueba = new Card("PruebaB", Suit.BLACK);
		this.cards.add(prueba);
		
	}
    
}
