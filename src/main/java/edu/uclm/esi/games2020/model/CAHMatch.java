package edu.uclm.esi.games2020.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

public class CAHMatch extends Match {
    private Deck deckB, deckW;
    private List<Card> cardsOnTable;
    private List<Card> cartasJugadores;

    public CAHMatch() {
        super();
        this.deckB = new Deck(Suit.BLACK);
        this.deckW = new Deck(Suit.WHITE);
        this.deckB.suffle();
        this.cardsOnTable = new ArrayList<>();
        this.cartasJugadores = new ArrayList<>();
        this.cardsOnTable.add(this.deckB.getCard());
    }

    @Override
    public void start() throws IOException {
        this.started = true;
        super.notifyStart();
        super.inicializaTurn();
    }

    @Override
    protected JSONObject startData(User player) {
    	
		Card carta;
		JSONArray jsaCartasJugador = new JSONArray();

		for (int i = 0; i < 5; i++) {
	        this.deckW.suffle();
			carta = this.deckW.getCard();
			carta.setState(player.getState());
			this.cartasJugadores.add(carta);
			jsaCartasJugador.put(carta.toJSON());
		}

		JSONObject jso = new JSONObject();
		jso.put("table", this.cardsOnTable.get(0).toJSON());
		jso.put("data", jsaCartasJugador);
		return jso;
    	
    }

    @Override
    public String play(JSONObject jso, WebSocketSession session) {
		return null;
    	//ejemplo del profesor
    }

    @Override
    protected void setState(User user) {
        IState state = new CAHState();
        user.setState(state);
        state.setUser(user);
    }
}
