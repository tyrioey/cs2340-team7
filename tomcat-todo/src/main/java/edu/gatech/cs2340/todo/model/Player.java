package edu.gatech.cs2340.todo.model;
import java.util.*;


public class Player implements Comparable<Player>{    

    String name;
    String country;
    String color;
    int turn;
    TreeMap<String,Integer> occupiedTerritories; 
    TreeMap<Integer,Unit> army; //Integer key is Unit ID
    boolean hasLost;
    int[] homebase;
    Unit uniqueUnit;
    int startingArmySize;

    public Player(String title, String task){  
        name = title;
        country = task;
        turn = 0;  
        army = new TreeMap<Integer,Unit>();
        occupiedTerritories= new TreeMap<String,Integer>();
        hasLost = false;
        homebase = new int[2];
        color = determineColor(country);
        uniqueUnit = null;
        startingArmySize = setStartingArmySize(country);
    }	 
    private String determineColor(String task){
    	if (task.equals("Polaris")) { return "purple";}
    	else if (task.equals("Alpha-Centauri")) { return "green";}
    	else if (task.equals("Char")) { return "red";} 
    	else if (task.equals("Midichloria")) { return "blue";}
    	else if (task.equals("Borg")) { return "yellow";} 
    	else if (task.equals("HAL Space Station")) { return "orange";}
    	else
    		return "pink";
    }
    public int setStartingArmySize(String task)
    {
     //unique per country, set aside for Milestone
    	return 0;
    }
    
    public String getColor(){
    	return color;
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
    
    public void setHomeBase(int a, int b){
    	homebase[0] = a;
    	homebase[1] = b;
    }
    
    public int[] getHomebaseCoords(){
    	return homebase;
    }
    
    public void setName(String title){
        name = title;
    }  
    
    public String getName(){
        return name;
    }   
    
    public void setCountry(String task){
        country = task;
    }
    
    public String getCountry() {
    	return country;
    }
    
    public void setTurn(int a){
    	turn = a;
    }  
    public void setUniqueUnit(String name, int health, int strength, int defense)
    {
    	uniqueUnit = new Unit(name,health,strength,defense);
    	uniqueUnit.setOwner(this);
    }
    public Unit getUniqueUnit()
    {
    	return uniqueUnit;
    }
    
    public int getTurn(){
    	return turn;
    }
    
    public void addUnit(Unit unit){	
    	army.put(unit.getID(),unit);	
    	
    	String terr = unit.getTerritory().getName();
    	if(occupiedTerritories.containsKey(terr)){
    		int currUnitAmt = occupiedTerritories.get(terr);
    		occupiedTerritories.put(terr, currUnitAmt+1);
    	}
    	else{
    		occupiedTerritories.put(terr,1);
    	}
    } 
    
    public TreeMap<Integer,Unit> getArmy(){
    	return army;
    } 
    
    public int getArmySize(){
    	return army.size();
    }
    
    public TreeMap<String,Integer> getOccupiedTerritories(){
    	return occupiedTerritories;
    }
    
    public void update(){
    	removeDeadUnits();
    }
    
    public void update(Unit unit)
    {
    	removeDeadUnits();
    	updateTerritories(unit);
    }

    public void removeDeadUnit(Unit unit){
    	ArrayList<String> terrToBeRemoved = new ArrayList<String>();
		
    	if(unit.getHealth() < 0){
    		army.remove(unit.getID());
    		String territory = unit.getTerritory().getName();
    		int currentUnitAmount = occupiedTerritories.get(territory);
			
    	 		if(currentUnitAmount-1 <= 0){
	    			System.out.println("Removing Territory from occupiedTerritories due to unit death.");
	    			terrToBeRemoved.add(territory);
	    		}
    	 		else{
    	 			occupiedTerritories.put(territory,currentUnitAmount-1);
				}	
    	}
    	for(String a:terrToBeRemoved)
    		occupiedTerritories.remove(a);
    }
	 
    public void removeDeadUnits(){
    	ArrayList<String> terrToBeRemoved = new ArrayList<String>();
    	ArrayList<Integer> unitToBeRemoved = new ArrayList<Integer>();
    	for(int id:army.keySet()){
    		if(army.get(id).getHealth() <= 0){
    			int currentUnitAmount = occupiedTerritories.get(army.get(id).getTerritory().getName());
    	    	occupiedTerritories.put(army.get(id).getTerritory().getName(),currentUnitAmount-1);
    	    	unitToBeRemoved.add(id);
    	    	System.out.println("Removed!");
    		}
    	}
    	for(int id: unitToBeRemoved){
    		army.remove(id);
    	}
    }
  
    //updateTerritories for MOVING, not as a result of being destroyed
    //this is because destroyed units do not move when they are destroyed, and updateTerritories tries
    //to clear out the last location of the unit. Since the destroyed unit didn't move, it will try to
    //clear away a location that is no longer in occupiedTerritories.

     public void updateTerritories(Unit unit)   {

    	String previousTerritory = "";
    	if(unit.getPreviouslyOccupied() != null)
    		previousTerritory = unit.getPreviouslyOccupied().getName();
    	String currentTerritory = unit.getTerritory().getName();
    	ArrayList<String> toBeRemoved = new ArrayList<String>();
	
    	if(!previousTerritory.equals("")){
    		int currentUnitAmount = occupiedTerritories.get(previousTerritory);
	    	if(currentUnitAmount-1 <= 0){
	    		toBeRemoved.add(previousTerritory);
	    	}
	    	else
	    		occupiedTerritories.put(previousTerritory,currentUnitAmount-1);
    	}
    	
    	for(String a: toBeRemoved){
    		occupiedTerritories.remove(a);
    	}
    
    	if(occupiedTerritories.containsKey(currentTerritory)){
    		int currentUnitAmount = occupiedTerritories.get(currentTerritory);
    		occupiedTerritories.put(currentTerritory,currentUnitAmount+1);
    	}
    	else
    		occupiedTerritories.put(currentTerritory,1);
    }
    
    public void loses()
    {
    	hasLost = true;
    	for(int id: army.keySet())
    		army.get(id).takeDamage(100000);
    }
    public boolean hasLost(){
    	return hasLost;
    }
	 
    public void resetArmy(){
    	for(int id:army.keySet()){
    		army.get(id).resetForTurn();
    	}
    }

    public String toString() {	
    	return name +" from "+country+" has an army with " +army.size()+" units in it and goes on turn "+turn+"\n\n";
    }
    
}
