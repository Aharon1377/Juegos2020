package edu.uclm.esi.games2020.model;

public class CAH extends Game {
	
	private String name;
	
	public CAH(int num) {
		super(num);
		this.name= "CAH ("+num+")";
	}

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected Match buildMatch() {
        return new CAHMatch();
    }

}
