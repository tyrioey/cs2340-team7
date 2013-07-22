package edu.gatech.cs2340.todo.model;



import java.util.*;

public class Player implements Comparable<Player>
{
    
    String name;
    String country;
    String color;
    int turn;
    TreeMap<String,Integer> occupiedTerritories; //A map of all territories occupied by this player, since multiple units can occupy the same territory
    												//the territory will be the key, the number of units will be the stored value
    											//is this even needed?
//	ArrayList<Resource> ownedResources; //A List of all resources owned by the player
    TreeMap<Integer,Unit> army; //key is Unit ID, value is the actual Unit
    boolean hasLost;
    int[] homebase;

    public Player(String title, String task) { //constructors may be temporary, may later intialize with x army, y turn, etc. 
        name = title;
        country = task;
        turn = 0;  
        army = new TreeMap<Integer,Unit>();
        occupiedTerritories= new TreeMap<String,Integer>();
        hasLost = false;
        homebase = new int[2];
    }
    public int compareTo(Player other){
    	if(turn < other.getTurn())
    		return -1;
    	return 1;
    }
    public boolean equals(Player other){
    	if (name.equals(other.getName()))
    		return true;
    	return false;
    		
    }
    public void setHomeBase(int a, int b)
    {
    	homebase[0] = a;
    	homebase[1] = b;
    }
    public int[] getHomebaseCoords()
    {
    	return homebase;
    }
    public void setName(String title) {
        name = title;
    }
    public String getName() {
        return name;
    }
    public void setCountry(String task) {
        country = task;
    }
    public String getCountry() {
    	return country;
    }
    public void setTurn(int a){
    	turn = a;
    }
    public int getTurn(){
    	return turn;
    }
    public void addUnit(Unit unit) 
    {	
    	army.put(unit.getID(),unit);	
    	
    	String terr = unit.getTerritory().getName();
    	if(occupiedTerritories.containsKey(terr))
    	{
    		int currUnitAmt = occupiedTerritories.get(terr);
    		occupiedTerritories.put(terr, currUnitAmt+1);
    	}
    	else
    		occupiedTerritories.put(terr,1);
    	
    }
    
    public void removeUnit(Unit failurer) {
        /* Not even needed for now.
        String terr = failurer.getTerritory().getName();
        if (occupiedTerritories.containsKey(terr)) {
            int id = failurer.getID();
            occupiedTerritories.remove(id);
        } else
            System.out.println("No units here.");*/
    }
    
    public TreeMap<Integer,Unit> getArmy()
    {
    	return army;
    }
    public int getArmySize() 
    {
    	return army.size();
    }
    public TreeMap<String,Integer> getOccupiedTerritories()
    {
    	return occupiedTerritories;
    }
    
    //cleans up army and occupiedTerritories (gets rid of destroyed units and shifts occupiedTerritories as units move
    public void update()
    {
    	removeDeadUnits();
    }
    public void update(Unit unit)
    {
    	removeDeadUnits();
    	updateTerritories(unit);
    }
    public void removeDeadUnits()
    {
    	ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();
    	for(int id: army.keySet())
    	{
    		if(army.get(id).getHealth() <= 0)
    		{
    			int currentUnitAmount = occupiedTerritories.get(army.get(id).getTerritory().getName());
    	    	occupiedTerritories.put(army.get(id).getTerritory().getName(),currentUnitAmount-1);
    	    	toBeRemoved.add(id);
    		}
    	}
    	for(int id: toBeRemoved)
    	{
    		army.remove(id);
    	}
    }
    public void updateTerritories(Unit unit)
    {
    	String previousTerritory = "";
    	if(unit.getPreviouslyOccupied() != null)
    		previousTerritory = unit.getPreviouslyOccupied().getName();
    	String currentTerritory = unit.getTerritory().getName();
    	ArrayList<String> toBeRemoved = new ArrayList<String>();
    	
    	if(!previousTerritory.equals(""))
    	{
    		int currentUnitAmount = occupiedTerritories.get(previousTerritory);
	    	if(currentUnitAmount-1 <= 0)
	    	{
	    		toBeRemoved.add(previousTerritory);
	    	}
	    	else
	    		occupiedTerritories.put(previousTerritory,currentUnitAmount-1);
    	}
    	for(String a: toBeRemoved)
    	{
    		occupiedTerritories.remove(a);
    	}
   
    	
    	if(occupiedTerritories.containsKey(currentTerritory))
    	{
    		int currentUnitAmount = occupiedTerritories.get(currentTerritory);
    		occupiedTerritories.put(currentTerritory,currentUnitAmount+1);
    	}
    	else
    		occupiedTerritories.put(currentTerritory,1);
    	
    }
    public void loses()
    {
    	hasLost = true;
    	army = new TreeMap<Integer,Unit>();
    	occupiedTerritories = new TreeMap<String,Integer>();
    }
    public String toString() {	
    	return name +" from "+country+" has an army with " +army.size()+" units in it and goes on turn "+turn+"\n\n";
    }
    
}
