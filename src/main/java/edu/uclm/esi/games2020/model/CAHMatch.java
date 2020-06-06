package edu.uclm.esi.games2020.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

public class CAHMatch extends Match {
	private Deck deckB, deckW;
	private List<Card> cardsOnTable;
	private List<Card> cartasJugadores;
	protected ConcurrentHashMap<String, Boolean> respondido;

	public CAHMatch() {
		super();
		this.respondido = new ConcurrentHashMap<>();
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
		
		player.setPuntos(0);
		Card carta;
		JSONArray jsaCartasenMesa = new JSONArray();
		JSONArray jsaCartasJugador = new JSONArray();
		this.respondido.put(player.getUserName(), false);

		jsaCartasenMesa.put(this.cardsOnTable.get(0).toJSON());

		for (int i = 0; i < 5; i++) {
			this.deckW.suffle();
			carta = this.deckW.getCard();
			carta.setState(player.getState());
			this.cartasJugadores.add(carta);
			jsaCartasJugador.put(carta.toJSON());
		}

		JSONObject jso = new JSONObject();
		jso.put("table", jsaCartasenMesa);
		jso.put("data", jsaCartasJugador);
		return jso;

	}

	@Override
	public String play(JSONObject jso, WebSocketSession session) throws IOException {

		if (jso.getString("type").equals("responder"))
			this.responder(jso, session);

		if (jso.getString("type").equals("votar"))
			this.votar(jso, session);

		return null;
	}

	private void votar(JSONObject jso, WebSocketSession session) throws IOException {

		JSONObject jsa = (JSONObject) jso.get("carta");

		Card carta = find(this.cardsOnTable, (String) jsa.get("text"));

		if (session == this.turn) {
			this.respondido.put(super.getNamePlayerSession(session), true);
			boolean todos_responden = true;
			for (boolean respuesta : this.respondido.values()) {
				if (respuesta = false)
					todos_responden = false;
			}

			if (todos_responden) {
				this.cardsOnTable = new ArrayList<>();
				this.notifyCarta(session);
				this.notifyBlack();
				this.notifyTurn(this.rotateTurn(session));
				this.notifyWinner(carta.getState().getUser());
			} else {
				this.notifyInvalidPlay(session, "Espera a que todos respondan");
			}

		} else {
			this.notifyInvalidPlay(session, "No es tu turno");
		}

	}

	private Card find(List<Card> list, String str) {
		
		for(Card carta : list) {
			if(carta.getText().equals(str))
				return carta;
		}
		
		return null;
		
	}

	private void responder(JSONObject jso, WebSocketSession session) throws IOException {

		JSONObject jsa = (JSONObject) jso.get("carta");

		Card carta = find(this.cartasJugadores,(String) jsa.get("text"));

		if (session != this.turn && !this.respondido.get(super.getNamePlayerSession(session))) {

			this.cartasJugadores.remove(carta);
			this.cardsOnTable.add(carta);
			this.respondido.put(super.getNamePlayerSession(session), true);
			this.notifyPlay();
		} else {
			this.notifyInvalidPlay(session, "Jugada inválida");
		}

	}

	@Override
	public void notifyTurn(String name) throws IOException {
		JSONObject jso = this.toJSON();
		jso.put("type", "matchChangeTurn");
		for (User player : this.players) {
			jso.put("turn", name);
			player.send(jso);
		}
	}

	private void notifyPlay() throws IOException {

		JSONObject jso = this.toJSON();
		jso.put("type", "nuevaRespuesta");
		jso.put("carta", this.cardsOnTable.get(this.cardsOnTable.size() - 1).toJSON());
		for (User player : this.players) {
			player.send(jso);
		}

	}
	
	private void notifyBlack() throws IOException {

		JSONObject jso = this.toJSON();
		Card carta;
		this.deckB.suffle();
		carta = this.deckB.getCard();
		this.cardsOnTable.add(carta);
		jso.put("type", "nuevaPregunta");
		jso.put("carta", carta.toJSON());
		for (User player : this.players) {
			player.send(jso);
		}

	}

	private void notifyCarta(WebSocketSession session) throws IOException {

		for (User player : this.players) {

			if (player.getSession() != session) {

				JSONObject jso = this.toJSON();
				Card carta;
				this.deckW.suffle();
				carta = this.deckW.getCard();
				carta.setState(player.getState());
				this.cartasJugadores.add(carta);
				jso.put("type", "nuevaCarta");
				jso.put("carta", carta.toJSON());
				player.send(jso);

			}
		}

	}
	
	private void notifyWinner(User user) throws IOException {
		
		JSONObject jso = this.toJSON();
		jso.put("type", "winner");
		jso.put("winner", user.getUserName());
		Manager.get().actualizarPuntos(user.getUserName());
		for (User player : this.players) {
			player.send(jso);
		}
		
	}

	@Override
	protected void setState(User user) {
		IState state = new CAHState();
		user.setState(state);
		state.setUser(user);
	}
}
