package edu.gatech.cs2340.todo.model;
import edu.gatech.cs2340.todo.model.*;
import java.util.*;

public class RiskGame {

    private static int gameIDs = 0;
    private int gameID;
 
 public static final int ADD_PLAYERS = 0, CONFIRMATION = 1, SELECT_LOCATION = 2, 
                                COMMAND = 3;
 private int state; // 0-add players, 1-select territory, 
 
 ArrayList<Player> players;
 Territory[][] map;
 int turnCount;
 int playerTurn;
 int playerActionsSoFar;
 int id = 1000;
 
 public RiskGame() {
     this.gameID = gameIDs;
     gameIDs += 1;
     this.state = ADD_PLAYERS;
     this.turnCount = 0;
     this.players = new ArrayList<Player>();
     this.playerTurn = 0;
     this.playerActionsSoFar = 0;
     map = new Territory[0][0];
 }
public RiskGame(int gameID){
     this.gameID = gameID;
     this.state = ADD_PLAYERS;
     this.turnCount = 0;
     this.players = new ArrayList<Player>();
     this.playerTurn = 0;
     this.playerActionsSoFar = 0;
     map = new Territory[0][0];
}

// general functionality
public int getGameID() {
    return gameID;
}

public int getGameState() {
    return state;
}

public int getTurnCount() {
    return turnCount;
}

public int getCurrTurn()
{
	return playerTurn;
}

public int getPlayerActionsSoFar() {
    return playerActionsSoFar;
}

public ArrayList<Player> getPlayers() {
    return players;
}

public Player getCurrentPlayer() {
    // get the player whose turn it is.
    return players.get(getCurrTurn());
}

public Territory[][] getMap(){
    return map;
}

public int getID(){
	id++;
	return id;
}

// main state interaction functions
public void nextTurn()
{
    turnCount += 1;
    playerTurn++;
	if (playerTurn == players.size())
		playerTurn = 0;
    playerActionsSoFar = 0;
    state = SELECT_LOCATION;
    if(getCurrentPlayer().hasLost())
    	nextTurn();
}

public boolean spawnUpdate(int[] co) {
    boolean success = false;
    
    if (state == SELECT_LOCATION) {
        // spawn for this
        Player player = getCurrentPlayer();
        if (check(co, player)) {
        	Unit a = player.getUniqueUnit();
            spawn(getMap(), a.getName(), a.getHealth(), a.getStrength(), a.getDefense(), 1, player, co);
            state = COMMAND;
            success = true;
            playerActionsSoFar += 1;
        }
    }
    return success;
}

public boolean moveUpdate(ArrayList<Integer> selectedUnitIDs, int[] moveCo) {
    boolean success = true;
    ArrayList<Unit> selectedUnits = new ArrayList<Unit>();
    for (int i = 0; i < selectedUnitIDs.size(); i++)
        selectedUnits.add(getCurrentPlayer().getArmy().get(selectedUnitIDs.get(i)));
    Territory start = selectedUnits.get(0).getTerritory();
    int[] startCo = start.getCoords();
    Territory location = map[moveCo[0]][moveCo[1]];
    
    // try to move iff all units can move there.
    if (checkAdjacent(startCo, moveCo) && location.isOccupiable(start.getPlayer())) {
        for(Unit a: selectedUnits) {
            if (a.getMoved()) {
                success = false;
            }
        }
        playerActionsSoFar += 1;
    }
    else
    	success = false;
    
    if(success)
    	for(Unit a: selectedUnits)
    		a.move(location);
    
    return success;
}

public boolean attackUpdate(ArrayList<Integer> selectedUnitIDs, int[] moveCo) {
    boolean success = false;
    ArrayList<Unit> selectedUnits = new ArrayList<Unit>();
    for (int i = 0; i < selectedUnitIDs.size(); i++)
        selectedUnits.add( getCurrentPlayer().getArmy().get( selectedUnitIDs.get(i) ) );
    int[] startCo = selectedUnits.get(0).getTerritory().getCoords();
    Territory location = map[moveCo[0]][moveCo[1]];
    ArrayList<Unit> victims = new ArrayList<Unit>();
	for(int ids: location.getOccupants().keySet())
        victims.add(location.getOccupants().get(ids));
    
    // try the attack - attack succeeds iff all units can attack
    boolean attackSucceed = true;
    if (checkAdjacent(startCo, moveCo)) {
        System.out.println("We are attacking!");
        playerActionsSoFar += 1;
        for (Unit a: selectedUnits) {
            if (a.getAttacked())
                attackSucceed = false;
        }
        
        if (attackSucceed) {
        	if(location.isHomeBase())
        		bombard(selectedUnits,location);
        	else
        		fight(selectedUnits, victims);
            success = true;
        } else {
            System.out.println("Some of the selected units can't attack!");
        }
    }
    return success;
}

public String addPlayer(String name, String country){
     String result = null;

     if (state == ADD_PLAYERS){
         if (players.size() <= 6){
             System.out.println("We're adding a player!");
             // make sure not a duplicate
             if (!duplicateCountry(country)){
                 players.add(new Player(name, country));

                 playerActionsSoFar += 1;
             } else {
            	 System.out.println("Players cannot choose the same country!");
                 result = "Players cannot choose the same country!";
             }
         } 
			else{
             result = "Too many players";
         }
     }
     return result;

 }

public String removePlayer(int id){
	 String result = "";
	 if(state == ADD_PLAYERS){
		 System.out.println("We're deleting a player!");
		 players.remove(id);
		 result = "Removed!";
         playerActionsSoFar += 1;
	 }
	 return result;	 
}
public String updatePlayer(int id, String name, String country){
	 String result = "";
	 if(state == ADD_PLAYERS){
		 System.out.println("We're updating a player!");
		 players.set(id, new Player(name, country));
		 result = "Updated!";
        playerActionsSoFar += 1;
	 }
	 return result;	 
}
 
 // checks if the new player has chosen the same country as another.
private boolean duplicateCountry(String country){
     boolean result = false;
     for (int i = 0; i < players.size(); i++){
         if (country.equalsIgnoreCase(players.get(i).getCountry())){
             result = true;
             break;
         }
     }
     return result;
}
 
public String finishAddingPlayers(){
     String result = null;

     if (state == ADD_PLAYERS) {
         if ( players.size() >= 3 && players.size() <= 6 ) { 
             System.out.println("Moving on to Confirmation!");
             calcArmiesAndTurnOrder();
             state = CONFIRMATION;
         } 
		 else{
             result = "Not enough players.";
         }
     } 
	 else{
         result = "State ERROR: Not adding players.";
     }
     return result;
}
 
private void calcArmiesAndTurnOrder(){
 	ArrayList<Integer> turns = new ArrayList<Integer>();
	
 	for(int b = 1; b <= players.size(); b++){
		turns.add(b);
	}
	
 	Collections.shuffle(turns);
	
 	for(int c = 0; c < players.size(); c++){ 
	players.get(c).setTurn(turns.get(c));
 	System.out.println(players.get(c));
 	}
	
 	Collections.sort(players);
}
 
 // confirmation state functions
public String finishConfirmation(){
     String result = null;

     if (state == CONFIRMATION) {
         state = SELECT_LOCATION;
         initializeBoard();
     } 
     else {
         result = "State ERROR: Not CONFIRMATION.";
     }
     playerTurn = 0;
     playerActionsSoFar = 0;
     return result;
}

public void bombard(ArrayList<Unit> attackers, Territory homebase){
	Random rand = new Random();
	int numAttackDice = (int) Math.floor(1+attackers.size()/4);
	Player victim = homebase.getPlayer();

		for(Unit attacker: attackers){
			int damage = attacker.getStrength();
			String diceRolls = "";
			for(int a = 0; a<numAttackDice;a++){	
				int roll = rand.nextInt(6)+1;
				damage+=roll;
				diceRolls+=roll+", ";
			}
			diceRolls = diceRolls.substring(0,diceRolls.length()-2);
			
			System.out.println(attacker.getName() +"-"+attacker.getID()+"- is attacking "+victim.getName()+"'s homebase!");
			System.out.println(attacker.getName() +"-"+attacker.getID()+"- rolled "+numAttackDice+" dice ("+diceRolls+") plus its strength ("+attacker.getStrength()+") for "+damage+" damage!");
		
			homebase.takeDamage(damage);
			
		}
		if(victim.hasLost()){
			for(Territory[] a: map)
				for(Territory b: a)
					b.removeDeadUnits();


			System.out.println(victim.getName()+" has lost!!!");
	/*		for(int a = 0; a<players.size();a++){
				if(players.get(a).getName().equals(victim.getName())){
					players.remove(a);
					break;
				}
			}*/
			//since we're removing a player directly from the player ArrayList upon death, need to make sure we don't
			//skip over the next player when the turn increments
/*			for(int a = 0; a<players.size()-1;a++)
				nextTurn();*/
		}	

}

public void fight(ArrayList<Unit> attackers, ArrayList<Unit> defenders){
	Random rand = new Random();
	int numAttackDice = (int) Math.floor(1+attackers.size()/4);
	int numDefenseDice = (int) Math.floor(1+defenders.size()/4);		
	int counterAttackDice = rand.nextInt(6)+1;
	
		for(Unit attacker: attackers){
			int damage = attacker.getStrength();
			String diceRolls = "";
			
			for(int a = 0; a<numAttackDice;a++){	
				int roll = rand.nextInt(6)+1;
				damage+=roll;
				diceRolls+=roll+", ";
			}
			diceRolls = diceRolls.substring(0,diceRolls.length()-2);
			Collections.shuffle(defenders);
			Unit victim = defenders.get(0);
			
			if(victim != null){
			System.out.println(attacker.getName() +"-"+attacker.getID()+"- is attacking "+victim.getName()+"-"+victim.getID());
			System.out.println(attacker.getName() +"-"+attacker.getID()+"- rolled "+numAttackDice+" dice ("+diceRolls+") plus its strength ("+attacker.getStrength()+") for "+damage+" damage!");
			int defense = victim.getDefense();
			diceRolls = "";
			for(int a = 0;a<numDefenseDice;a++){
				int roll = rand.nextInt(6)+1;
				defense+=roll;	
				diceRolls +=roll+", ";
			}
			diceRolls=diceRolls.substring(0,diceRolls.length()-2);
			System.out.println(victim.getName() +"-"+victim.getID()+"- rolled "+numDefenseDice+" dice ("+diceRolls+") plus its defense ("+victim.getDefense()+") to prevent "+defense+" damage!");
			damage -= defense;
			
			if(damage < 0)
				damage = 0;
			victim.takeDamage(damage);
			System.out.println(victim.getName() + "-"+victim.getID()+" was attacked for "+damage+" down to "+victim.getHealth()+"/"+victim.getMaxHealth()+"!");
			}
		}
		for(Player player:players)
			player.removeDeadUnits();
		for(Territory[] a: map)
			for(Territory b: a)
				b.removeDeadUnits();
}

public Territory[][] initializeBoard(){
	ArrayList<String> countries = new ArrayList<String>();
	int armysize = 10-players.size();
 	map = new Territory[9][15];
 	for(int a = 0; a <9; a++){
 		for(int b = 0; b < 15; b++){
			int[] coord = new int[2];
			coord[0] = a;
 			coord[1] = b; 
 			map[a][b] = new Territory("Territory ["+a+","+b+"]",coord); 				
 		} 
 	}
 	Random rand = new Random();
 	for(int a = 2; a < 6; a++){
 		for(int b = 0; b < 15;b++) 		{
 			int chanceToSpawnAsteroid = rand.nextInt(100)+1;
 			if(chanceToSpawnAsteroid > 70)
 				map[a][b].makeAsteroid();
 		}
 	}
 	
	for (Player player: players){ 
		countries.add(player.getCountry()); 
		if(player.getCountry().equals("Alpha-Centauri")){
			player.setUniqueUnit("Alpha-Centaurian Space Frigate", 7,3,1);
		}
 		 	else if(player.getCountry().equals("Polaris")){
				player.setUniqueUnit("Polarian Manta",7,5,2);
			}
 		 	else if(player.getCountry().equals("Char")){
 		 		player.setUniqueUnit("Char Swarmling",4,3,0);
			}
 		 	else if(player.getCountry().equals("Borg")){
 		 		player.setUniqueUnit("Borg Assimilator",10,3,1);
			}
 		 	else if(player.getCountry().equals("HAL Space Station")){
 		 		player.setUniqueUnit("HSS Probe",4,4,1);
			}
 		 	else if(player.getCountry().equals("Midichloria")){
 		 		player.setUniqueUnit("Midichlorian Force",6,4,0);
			}
		}
	
		//upper left is AC
		if(countries.contains("Alpha-Centauri")){
		 Player aPlayer = players.get(countries.indexOf("Alpha-Centauri"));
 		 map[0][1].makeHomeBase(players.get(countries.indexOf("Alpha-Centauri"))); 
 		 int[] coords ={0,0,1,0,1,1,1,2,0,2};
 		 id = spawn(map, "Alpha-Centaurian Space Frigate",7,3,1,armysize,aPlayer,coords); 
		}
 		 
 		 //upper middle is Polaris
 		if(countries.contains("Polaris")){
            Player aPlayer = players.get(countries.indexOf("Polaris"));
 			map[0][7].makeHomeBase(aPlayer); 
 			int[] coords = {0,6,1,6,1,7,1,8,0,8};
 		 	id=spawn(map, "Polarian Manta",7,5,2,armysize, aPlayer, coords);  
 	    }
 		 //upper right is Midichloria
 		if(countries.contains("Midichloria")){
            Player aPlayer = players.get(countries.indexOf("Midichloria"));
 			map[0][13].makeHomeBase(aPlayer);
 			int[] coords = {0,12,1,12,1,13,1,14,0,14};
 			id=spawn(map, "Midichlorian Force",6,4,0,armysize, aPlayer, coords); 
 		}
 		 
 		 //bottom left Char
 		 if(countries.contains("Char")){
            Player aPlayer = players.get(countries.indexOf("Char"));
 			map[8][1].makeHomeBase(aPlayer);
 			int[] coords = {8,0,7,0,7,1,7,2,8,2};
 			id=spawn(map, "Char Swarmling",4,3,0,armysize, aPlayer,coords); 
 		 }

 		 //bottom middle is HAL Space Station
 		 if(countries.contains("HAL Space Station")){
             Player aPlayer = players.get(countries.indexOf("HAL Space Station"));
 			 map[8][7].makeHomeBase(aPlayer); 
 			 int[] coords = {8,6,7,6,7,7,7,8,8,8};
 			id= spawn(map, "HSS Probe",4,4,1 ,armysize, aPlayer, coords); 
 		}
 		 
 		 //bottom right is Borg
 		 if(countries.contains("Borg")){
             Player aPlayer = players.get(countries.indexOf("Borg"));
 			 map[8][13].makeHomeBase(aPlayer); 
 			 int[] coords = {8,14,7,14,7,13,7,12,8,12};
 			id= spawn(map, "Borg Assimilator",10,3,1, armysize, aPlayer, coords); 
 		}

 	return map;
 }

 public int spawn(Territory[][] maps, String name, int health, int strength, int defense,
                                int amount, Player owner, int[] coords) 
 {
	int unitID = -1;
	for(int i = 0; i < coords.length; i+=2)
	{
		for(int a = 1; a <= amount; a++)
		{
		Unit toBeAdded = new Unit(name, health, strength, defense, owner);
		unitID = toBeAdded.getID();
        toBeAdded.setTerritory(maps[coords[i]][coords[i+1]]);
		toBeAdded.getOwner().addUnit(toBeAdded);
		maps[coords[i]][coords[i+1]].addUnit(toBeAdded);
		}
		System.out.println("Spawning "+amount+" "+name+"(s) at ["+coords[i]+","+coords[i+1]+"] for "+players.get(getCurrTurn()).getName());
	
 	}
	return unitID;
 }

protected boolean check(int[] coords, Player player)
{
    	int homebasey = player.getHomebaseCoords()[0];
    	int homebasex = player.getHomebaseCoords()[1];
    	
    	int y = coords[0];
    	int x = coords[1];
    	
    	return Math.abs(homebasey-y) <= 1 && Math.abs(homebasex-x) <=1;
    	 	
}

protected boolean checkAdjacent(int[] homeCo, int[] adjacentCo){
		int homeX = homeCo[1];
		int homeY = homeCo[0];
		
		int adjX = adjacentCo[1];
		int adjY = adjacentCo[0];
		
		return Math.abs(homeX-adjX) <= 1 && Math.abs(homeY-adjY) <=1;
}
public boolean checkOver()
{
	int count = 0;
	for(Player player: players)
	{
		if(!player.hasLost())
			count++;
	}
	return count==1;
}
 
  
public String toString(){
 	return "This is game id: "+gameID+" at state "+state;
}
 
}