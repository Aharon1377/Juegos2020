package edu.uclm.esi.games2020.model;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

public abstract class Match {
    protected List<User> players;
    protected String id;
    protected boolean started;
    private int readyPlayers;
    protected WebSocketSession turn;
    @NoJSON
    private Game game;


    protected int getPosOfSession(WebSocketSession session) {
        int pos = -1;
        for (User u : this.players) {
            if (u.getSession() == session) {
                pos = players.indexOf(u);
            }
        }
        return pos;
    }

    public Match() {
        this.id = UUID.randomUUID().toString();
        this.players = new ArrayList<>();
    }

    public void addPlayer(User user) {
        this.players.add(user);
        setState(user);
    }

    public String rotateTurn(WebSocketSession lastTurn) {

        int pos = getPosOfSession(lastTurn);

        if (pos == (players.size() - 1)) {
            pos = 0;
        } else {
            pos++;
        }

        this.turn = players.get(pos).getSession();
        return players.get(pos).getUserName();

    }

    protected abstract void setState(User user);

    public List<User> getPlayers() {
        return players;
    }

    public String getId() {
        return id;
    }

    public abstract void start() throws Exception;
	
	/*public JSONObject toJSON() {
		return JSONificador.toJSON(this);
	}*/

    public JSONObject toJSON() {
        JSONObject jso = new JSONObject();
        jso.put("idMatch", this.id);
        jso.put("started", this.started);
        JSONArray jsa = new JSONArray();
        for (User user : this.players)
            jsa.put(user.toJSON());
        jso.put("players", jsa);
        return jso;
    }

    public void notifyTurn(String name) throws IOException {
        JSONObject jso = this.toJSON();
        jso.put("type", "matchChangeTurn");
        for (User player : this.players) {
            jso.put("turn", name);
            player.send(jso);
        }
    }

    public void notifyStart() throws IOException, InterruptedException {
        JSONObject jso = this.toJSON();
        jso.put("type", "matchStarted");
        for (User player : this.players) {
            jso.put("startData", startData(player));
            player.send(jso);
        }
    }

    public void notifyFinish(String result) throws IOException {
        JSONObject jso = this.toJSON();
        jso.put("type", "matchFinished");
        jso.put("result", result);
        for (User player : this.players) {
            jso.put("startData", startData(player));
            player.send(jso);
        }
    }

    protected abstract JSONObject startData(User player);

    public void playerReady(WebSocketSession session) {
        ++readyPlayers;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean ready() {
        return this.readyPlayers == game.requiredPlayers;
    }

    public String inicializaTurn() throws IOException {
        SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            e.printStackTrace();
        }
        User u = players.get(sr.nextInt(players.size()));
        String name = rotateTurn(u.getSession());
        this.notifyTurn(name);
        return name;
    }

    public void notifyInvalidPlay(WebSocketSession session) throws IOException {
        JSONObject jso = this.toJSON();
        jso.put("type", "matchIlegalPlay");
        jso.put("result", "Ilegal play");
        int pos = getPosOfSession(session);
        if(pos>=0)
            players.get(pos).send(jso);
    }
}
