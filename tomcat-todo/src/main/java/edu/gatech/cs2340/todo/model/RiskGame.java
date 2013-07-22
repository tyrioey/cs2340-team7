package edu.gatech.cs2340.todo.model;
// RiskGame


import edu.gatech.cs2340.todo.model.*;
import java.util.*;


//RiskGame

import java.util.*;


public class RiskGame {
 Random rand;
 
 private int gameID;
 
 public static final int ADD_PLAYERS = 0, CONFIRMATION = 1, SELECT_LOCATION = 2;
 private int state; // 0-add players, 1-select territory, 
 
 ArrayList<Player> players;
 Territory[][] map;
 int playerTurn;
 int id = 1000;
 
 public RiskGame(int gameID) {
     this.gameID = gameID;
     this.state = ADD_PLAYERS;
     this.players = new ArrayList<Player>();
     this.playerTurn = 0;
     map = new Territory[0][0];
     rand = new Random();
 }
 
 // general functionality

 public int getGameID() {
     return gameID;
 }
 
 public int getGameState() {
     return state;
 }
 public int getCurrTurn()
 {
	 return playerTurn;
 }
 public ArrayList<Player> getPlayers() {
     return players;
 }
 public Territory[][] getMap()
 {
 	return map;
 }
 public int getID()
 {
	 id++;
	 return id;
 }
 public void nextTurn()
 {
	 playerTurn++;
	 if (playerTurn == players.size())
		 playerTurn = 0;
 }

 /**
   * add players state functions
   *
   * @return null for success, or a string with an error message to be 
   * displayed to the player.
   */
 public String addPlayer(String name, String country) {
     String result = null;
     if (state == ADD_PLAYERS) {
         if (players.size() <= 6) {
             System.out.println("We're adding a player!");
             // make sure not a duplicate
             if (!duplicateCountry(country)) {
                 players.add(new Player(name, country));
             } else {
            	 System.out.println("Players cannot choose the same country!");
                 result = "Players cannot choose the same country!";
             }
         } else {
             result = "Too many players";
         }
     }
     return result;
 }
 public String removePlayer(int id)
 {
	 String result = "";
	 if(state == ADD_PLAYERS)
	 {
		 System.out.println("We're deleting a player!");
		 players.remove(id);
		 result = "Removed!";
	 }
	 return result;
	 
 }
 
 // checks if the new player has chosen the same country as another.
 private boolean duplicateCountry(String country) {
     boolean result = false;
     for (int i = 0; i < players.size(); i++) {
         if (country.equalsIgnoreCase(players.get(i).getCountry())) {
             result = true;
             break;
         }
     }
     return result;
 }
 
 public String finishAddingPlayers() {
     String result = null;
     if (state == ADD_PLAYERS) {
         if (players.size() >= 3) {
             System.out.println("Moving on to Confirmation!");
             // calculate army numbers and turn order
             calcArmiesAndTurnOrder();
             
             // set the game state to confirmation
             state = CONFIRMATION;
         } else {
             result = "Not enough players.";
         }
     } else {
         result = "State ERROR: Not adding players.";
     }
     return result;
 }
 
 private void calcArmiesAndTurnOrder() {
 	
 	ArrayList<Integer> turns = new ArrayList<Integer>();
 	for(int b = 1; b <= players.size(); b++)
 	{turns.add(b);}
 	Collections.shuffle(turns);
 	for(int c = 0; c < players.size(); c++)
 	{ players.get(c).setTurn(turns.get(c));
 	System.out.println(players.get(c));
 	 }
 	Collections.sort(players);

 }
 
 // confirmation state functions
 public String finishConfirmation() {
     String result = null;
     if (state == CONFIRMATION) {
         state = 2;// next state goes here.
     } else {
         result = "State ERROR: Not CONFIRMATION.";
     }
     playerTurn = 0;
     return result;
 }
 
 
 // I'm not using this - Brian B
public void fight(ArrayList<Unit> attackers, ArrayList<Unit> defenders)
{
	int numAttackDice = (int)Math.floor(1.0+attackers.size()/4.0);
	int numDefenseDice = (int)Math.floor(1.0+defenders.size()/4.0);
	
	int counterAttackDice = rand.nextInt(6)+1;
	
	for(Unit attacker: attackers)
	{

		int damage = attacker.getStrength();
		String diceRolls = "";
		for(int a = 0; a<numAttackDice;a++)
		{	
			damage+=rand.nextInt(6)+1;
			diceRolls+=damage+" ";
		}
		
		Collections.shuffle(defenders);
		Unit victim = defenders.get(0);
		System.out.println(attacker.getName() +"-"+attacker.getID()+" - is attacking "+victim.getName());
		System.out.println(attacker.getName() +"-"+attacker.getID()+" - rolled "+diceRolls+" for damage!");
		int defense = victim.getDefense();
		for(int a = 0;a<numDefenseDice;a++)
			defense+=rand.nextInt(6)+1;
		damage -= defense;
		
		victim.takeDamage(damage);
		System.out.println(victim + " was attacked for "+damage+" down to "+victim.getHealth()+"/"+victim.getMaxHealth() );
	}
}

// using this instead, for now - Brian B
public int[] attackTerritory(int[] from, int[] to, int aDice, int dDice, int occupyCount) {
    int ax = from[0];
    int ay = from[1];
    int dx = to[0];
    int dy = to[1];
    Territory fromT = map[ax][ay];
    Territory toT = map[dx][dy];
    ArrayList<Unit> attackers = treemapToArrayList(fromT.getOccupants());
    ArrayList<Unit> defenders = treemapToArrayList(toT.getOccupants());
    // redundant size checks might be a good idea (don't do for now)
    ArrayList<Integer> attackDiceResults = new ArrayList<Integer>();
    ArrayList<Integer> defendDiceResults = new ArrayList<Integer>();
    for (int i = 0; i < aDice; i++) {
        attackDiceResults.add(rand.nextInt(6)+1);
    }
    for (int i = 0; i < dDice; i++) {
        defendDiceResults.add(rand.nextInt(6)+1);
    }
    // sort results
    Collections.sort(attackDiceResults);
    Collections.reverse(attackDiceResults);
    Collections.sort(defendDiceResults);
    Collections.reverse(defendDiceResults);
    // compare results, and tally death toll
    int attackersKilled = 0;
    int defendersKilled = 0;
    for (int i = 0; i < dDice; i++) {
        if (attackDiceResults.get(i) <= defendDiceResults.get(i)) {
            attackersKilled += 1;
        } else {
            defendersKilled += 1;
        }
    }
    // graveyard
    ArrayList<Unit> attackerGraves = new ArrayList<Unit>();
    ArrayList<Unit> defenderGraves = new ArrayList<Unit>();
    for (int i = 0; i < attackersKilled; i++) {
        Unit fallenSoldier = attackers.get(0);
        Player aPlayer = fallenSoldier.getOwner();
        //aPlayer.removeUnit(fallenSoldier);
        attackerGraves.add(fallenSoldier);
        fromT.removeUnit(fallenSoldier);
        attackers.remove(0);
    }
    for (int i = 0; i < defendersKilled; i++) {
        Unit fallenSoldier = defenders.get(0);
        Player aPlayer = fallenSoldier.getOwner();
        //aPlayer.removeUnit(fallenSoldier);
        defenderGraves.add(fallenSoldier);
        toT.removeUnit(fallenSoldier);
        defenders.remove(0);
    }
    int territoryOccupied = 0;
    if (defenders.size() == 0) {
        System.out.println("The defenses have been overrun!");
        territoryOccupied = 1;
        // attacker might not have enough armies.
        if (attackers.size() - occupyCount < 1)
            occupyCount = attackers.size()-1;
        occupyTerritory(from, to, occupyCount);
    }
    
    // attackers killed, defenders killed, territory occupied
    int[] battleResults = {attackersKilled, defendersKilled, territoryOccupied};
    return battleResults;
}

// can also be used for fortification
public void occupyTerritory(int[] from, int[] to, int occupyCount) {
    if (occupyCount > 0) {
        int ax = from[0];
        int ay = from[1];
        int dx = to[0];
        int dy = to[1];
        Territory fromT = map[ax][ay];
        Territory toT = map[dx][dy];
        ArrayList<Unit> armiesFrom = treemapToArrayList(fromT.getOccupants());
        //ArrayList<Unit> armiesTo = treemapToArrayList(toT.getOccupants());
        // redundant size check
        System.out.println("occupy territory, armiesFrom.size(): "+armiesFrom.size()+"\noccupyCount: "+occupyCount);
        if (armiesFrom.size() > occupyCount) {
            //remove from old territory, and add to new.
            for (int i = 0; i < occupyCount; i++) {
                Unit mover = armiesFrom.get(0);
                System.out.println("a mover has been moved to the new location."+mover.getID());
                //Player aPlayer = fallenSoldier.getOwner();
                //aPlayer.removeUnit(fallenSoldier);
                toT.addUnit(mover);
                fromT.removeUnit(mover);
                armiesFrom.remove(0);
            }
        } else {
            System.out.println("Not enough armiesFrom to occupy!");
        }
    }
}

 public Territory[][] initializeBoard()
 {
 	map = new Territory[9][15];
 	for(int a = 0; a <9; a++)
 	{
 		for(int b = 0; b < 15; b++)	
 		{
 			int[] coord = new int[2];
 			coord[0] = a;
 			coord[1] = b; 
 			map[a][b] = new Territory("Territory ["+a+","+b+"]",coord); 				
 		} 
 	}
 
 	//pretty sure we're just going to give everyone the same generic unit in the future
 	Unit ACUnit = new Unit("Alpha-Centaurian Space Frigate",5,3,1,null);
 	Unit PolarisUnit = new Unit("Polarian Manta",4,5,2,null);
 	Unit CharUnit = new Unit("Char Swarmling",3,2,0,null);
 	Unit BorgUnit = new Unit("Borg Assimilator",8,2,2,null);
 	Unit HALUnit = new Unit("HSS Probe",5,3,1,null);
 	Unit MidiUnit = new Unit("Midichlorian Force",6,4,1,null);
 	int armysize = (10-players.size()); 
 	ArrayList<String> countries = new ArrayList<String>(); 
 	for (Player player: players) { 
 		 countries.add(player.getCountry()); 
 		 if(player.getCountry().equals("Alpha-Centauri")){ACUnit.setOwner(player);}
 		 else if(player.getCountry().equals("Polaris")){PolarisUnit.setOwner(player);}
 		 else if(player.getCountry().equals("Char")){CharUnit.setOwner(player);}
 		 else if(player.getCountry().equals("Borg")){BorgUnit.setOwner(player);}
 		 else if(player.getCountry().equals("HAL Space Station")){HALUnit.setOwner(player);}
 		 else if(player.getCountry().equals("Midichloria")){MidiUnit.setOwner(player);}
 	}
 	
 	 if(countries.contains("Alpha-Centauri")){
 		 map[0][1].makeHomeBase(players.get(countries.indexOf("Alpha-Centauri"))); 
 		 int[] coords ={0,0,1,0,1,1,1,2,0,2};
 		 id = spawn(map, ACUnit,armysize,coords,id); 
 		 }
 		 
 		 //upper middle is Polaris
 		 if(countries.contains("Polaris")){
 			map[0][7].makeHomeBase(players.get(countries.indexOf("Polaris"))); 
 			int[] coords = {0,6,1,6,1,7,1,8,0,8};
 		 	id=spawn(map, PolarisUnit,armysize,coords,id);  
 		 }
 		 //upper right is Midichloria
 		 if(countries.contains("Midichloria")){
 			map[0][13].makeHomeBase(players.get(countries.indexOf("Midichloria")));
 			int[] coords = {0,12,1,12,1,13,1,14,0,14};
 			id=spawn(map, MidiUnit,armysize,coords,id); 
 		}
 		 
 		 //bottom left Char
 		 if(countries.contains("Char")){
 			map[8][1].makeHomeBase(players.get(countries.indexOf("Char")));
 			int[] coords = {8,0,7,0,7,1,7,2,8,2};
 			id=spawn(map, CharUnit,armysize,coords,id); 
 		 }

 		 //bottom middle is HAL Space Station
 		 if(countries.contains("HAL Space Station")){
 			 map[8][7].makeHomeBase(players.get(countries.indexOf("HAL Space Station"))); 
 			 int[] coords = {8,6,7,6,7,7,7,8,8,8};
 			id= spawn(map, HALUnit,armysize,coords,id); 
 		}
 		 
 		 //bottom right is Borg
 		 if(countries.contains("Borg")){
 			 map[8][13].makeHomeBase(players.get(countries.indexOf("Borg"))); 
 			 int[] coords = {8,14,7,14,7,13,7,12,8,12};
 			id= spawn(map, BorgUnit,armysize,coords,id); 
 		}

 	return map;
 }
 public int spawn(Territory[][] maps, Unit unit, int amount, int[] coords,int idn) 
 {
	
	for(int i = 0; i < coords.length; i+=2)
	{
		for(int a = 1; a <= amount; a++)
		{
		Unit toBeAdded = new Unit(unit.getName(),unit.getHealth(),unit.getStrength(),unit.getDefense(),unit.getOwner());
		toBeAdded.setID(idn++);
		toBeAdded.setTerritory(maps[coords[i]][coords[i+1]]);
		toBeAdded.getOwner().addUnit(toBeAdded);
		maps[coords[i]][coords[i+1]].addUnit(toBeAdded);
		}
		System.out.println("Spawning "+amount+" "+unit.getName()+"(s) at ["+coords[i]+","+coords[i+1]+"] for "+players.get(getCurrTurn()).getName());
	
 	}
	return idn;
 }
 
 
private ArrayList<Unit> treemapToArrayList(TreeMap<Integer, Unit> treemap) {
    ArrayList<Integer> keyList = new ArrayList<Integer>(treemap.keySet());
    ArrayList<Unit> unitsArray = new ArrayList<Unit>();
    for (int i = 0; i < keyList.size(); i++) {
        unitsArray.add(treemap.get(keyList.get(i)));
    }
    return unitsArray;
}
 
 
 
 public String toString()
 {
 	return "This is game id: "+gameID+" at state "+state;
 }
 
 // 
}