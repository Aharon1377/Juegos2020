package edu.uclm.esi.games2020.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


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
    	if(!this.cards.isEmpty()) {
    		this.suffle();
    		return this.cards.remove(0);
    	}
    	return null;
    }
    
    private void buildWhite() {
    	
    	Card carta;
		String csvFile = "respuestas.csv";
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
            while ((line = br.readLine()) != null) {

                carta = new Card(line, Suit.WHITE);
                this.cards.add(carta);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

		
	}

	private void buildBlack() {
		
    	Card carta;
		String csvFile = "preguntas.csv";
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
            while ((line = br.readLine()) != null) {

                carta = new Card(line, Suit.BLACK);
                this.cards.add(carta);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
    
}
