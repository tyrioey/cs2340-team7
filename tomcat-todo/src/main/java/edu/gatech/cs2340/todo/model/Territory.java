package edu.gatech.cs2340.todo.model;

import edu.gatech.cs2340.todo.model.Player;
import edu.gatech.cs2340.todo.model.Resource;
import java.util.*;

public class Territory implements Comparable<Territory>{
	String name; 
	int[] coordinates;
	TreeMap<Integer,Unit> occupiedByUnit; //key is unit id, value is the unit
	ArrayList<Resource> resources;
	Player occupiedByPlayer; 

	boolean homeBase;
	boolean asteroid;
	
	//occupiable is for homebase/asteroid, isOccupied is for units
	boolean occupiable;
	boolean isOccupied; 

	int health; //only for homebases

	public Territory(String name, int[] coords){
		this.name=name;
		coordinates = coords;
		isOccupied = false;
		occupiedByUnit = new TreeMap<Integer,Unit>();
		resources = new ArrayList<Resource>();
		occupiedByPlayer = null;
		occupiable = true;
		asteroid = false;
		homeBase = false;
		health = 0;
	}
	
	public int compareTo(Territory other){
		if(coordinates[0] < other.getCoords()[0])
			return -1;
		else if(coordinates[0] > other.getCoords()[0])
			return 1;
		//if in same row
		else if(coordinates[1] > other.getCoords()[1])
			return 1;
		else 
			return -1;
	}
	
	public boolean equals(Territory other){
		if (Arrays.equals(coordinates,other.getCoords()))
			return true;
		return false;
	}
	
	//if the territory is occupied by an asteroid
	public void makeAsteroid(){
	    asteroid = true;
	    occupiable = false;
	}  
	public boolean isAsteroid()
	{
		return asteroid;
	}
	//the "home base" of the player. New units will spawn adjacent to the home base and if it is "conquered", that player loses.
	public void makeHomeBase(Player player){
	    homeBase = true;
	    occupiable = false;
	    occupiedByPlayer = player;
	    player.setHomeBase(coordinates[0],coordinates[1]);
	    health = 150;
    }
	public void takeDamage(int damage)
	{
		int dmg = damage-10;
		if(dmg > 0)
			health -=dmg;
		System.out.println("Homebase only has "+health+" health left!");
		if(health <= 0)
			occupiedByPlayer.loses();
	}
	
	public boolean isHomeBase(){
		return homeBase;
	}
	
	//antiquated as of release - reach goal
	public void addResource(Resource treasure){
		resources.add(treasure);
			if (treasure.getName().equals("Asteroid"))
				occupiable = false;
	}
	//antiquated as of release - reach goal
	public boolean hasResources(){
		if (resources.size()>0)
			return true;
		else 
			return false;
	}
	
	public void addUnit(Unit conquerer){
	
		if(occupiable){// not an asteroid or homebase
			//if territory is already occupied by another player, then nothing happens
			
			if(isOccupied && !occupiedByPlayer.equals(conquerer.getOwner())){
				System.out.println("You can't move there, an enemy unit is in the way!");
			}
			else{
				isOccupied = true;
				occupiedByUnit.put(conquerer.getID(),conquerer);
				occupiedByPlayer = conquerer.getOwner();
			}
		}
		else
			System.out.println("There's something in the way!");
	}
	
	public String getName(){
		return name;
	}
	
	public int[] getCoords(){
		return coordinates;
	}

	public boolean isOccupied(){
		return isOccupied;
	}
	public boolean isOccupiable(Player player)	{
		
		if(occupiedByPlayer != null) {
			if(occupiedByPlayer.equals(player))
				return true;
			else
				return false;
		}
		else if(occupiable)
			return true;
		else
			return false;
		
	}
	
	public TreeMap<Integer, Unit> getOccupants(){
		return occupiedByUnit;
	}
	
	public Player getPlayer(){
		return occupiedByPlayer;
	}
	

	public void update(Unit unit)
	{
    	if(!equals(unit.getTerritory().getCoords()) || unit.getHealth() < 0)
    		occupiedByUnit.remove(unit.getID());
    	if(occupiedByUnit.size() == 0)
    	{
    		isOccupied = false;
    		occupiedByPlayer = null;
    		occupiable = true;
    	}
	}
	 public void removeDeadUnits()
	    {
		 if(!homeBase){
			ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();
		     for(int id : occupiedByUnit.keySet())		     {
		    	 Unit unit = occupiedByUnit.get(id);
		    	 if(unit.getHealth()<0)
		    		 toBeRemoved.add(id);	
		     }
		     for(int id:toBeRemoved)
		    	 occupiedByUnit.remove(id);
		     
		     if(occupiedByUnit.size() == 0)
		     { 
		    	isOccupied = false;
		     	occupiedByPlayer = null;
	    		occupiable = true;
		     }
	    }
	    }
	public String toString()
	{
		return name;
	}


}