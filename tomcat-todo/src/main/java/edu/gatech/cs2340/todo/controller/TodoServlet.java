package edu.gatech.cs2340.todo.controller;

import edu.gatech.cs2340.todo.model.Territory;
import edu.gatech.cs2340.todo.model.Player;
import edu.gatech.cs2340.todo.model.Unit;
import edu.gatech.cs2340.todo.model.RiskGame;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Math;

@WebServlet(urlPatterns={
        "/list", // GET
        "/create", // POST 
        "/update/*", // PUT
        "/delete/*", // DELETE
        "/confirmation"
    })
public class TodoServlet extends HttpServlet {

	//List of RiskGames so you can new/load game?
 	RiskGame game = new RiskGame(1);
    ArrayList<Player> players = game.getPlayers();

    
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException, ServletException {
    	
        System.out.println("In doPost()");
   
        // Handle the hidden HTML form field that simulates
        // HTTP PUT and DELETE methods.
        String operation = (String) request.getParameter("operation");
        System.out.println(operation);
        System.out.println("It is turn "+game.getCurrTurn());
        // If form didn't contain an operation field and
        // we're in doPost(), the operation is POST
     
        //probably more elegant fix later, like just changing operation to "Confirmation"
        if (null == operation)
        {
        	operation = "CONFIRMATION";
        }
    
        
        if (operation.equalsIgnoreCase("PUT")) 
        {
            System.out.println("Delegating to doPut().");
            doPut(request, response);
        } 
        else if (operation.equalsIgnoreCase("DELETE")) 
        {
            System.out.println("Delegating to doDelete().");
            doDelete(request, response);
        }
        else if(operation.equalsIgnoreCase("SPAWN")) 
        {
        	System.out.println("We're SPAWNING more units!");
           	int a = Integer.parseInt(request.getParameter("Coord1"));
           	int b = Integer.parseInt(request.getParameter("Coord2"));
           	Player currplayer = game.getPlayers().get(game.getCurrTurn());
           	request.setAttribute("currplayer",currplayer);
           	Unit unit = new Unit("Space Frigate",40,6,0,currplayer); //probably need to give all races same unit, or ugly fix later
           	int[] co = {a,b};
           	Territory[][] newMap = game.getMap();
           	players = game.getPlayers();
           	request.setAttribute("players",players);
           	request.setAttribute("map",newMap);
           	
           	if(check(co,currplayer) || checkForPlayerOwnedArmy(co, newMap, currplayer))
           	{
           	game.spawn(game.getMap(),unit, 1, co, game.getID());
           	//game.nextTurn();
           	System.out.println("It is now turn "+game.getCurrTurn());
           	newMap = game.getMap();
           	request.setAttribute("map",newMap);
           	currplayer = game.getPlayers().get(game.getCurrTurn());
           	request.setAttribute("currplayer",currplayer);
           	request.setAttribute("badSpawn", new Integer(0));
            
            // attack result attributes (not used)
            int[] attackResult = {0, 0, 0};
            Player defendingPlayer = null;
            request.setAttribute("attackResult", attackResult);
            request.setAttribute("defindingPlayer", defendingPlayer);
            
           	RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/attack.jsp");
            dispatcher.forward(request,response);
            }
            else
            {
            	System.out.println("You can't spawn there!");
                request.setAttribute("badSpawn", new Integer(1));
               	RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/confirmation.jsp");
                dispatcher.forward(request,response);
            }
        } else if (operation.equalsIgnoreCase("ATTACK")) {
            System.out.println("We're Attacking!");
           	int a = Integer.parseInt(request.getParameter("AttackFromCoord1"));
           	int b = Integer.parseInt(request.getParameter("AttackFromCoord2"));
            System.out.println(a);
            System.out.println(b);
            int c = Integer.parseInt(request.getParameter("TargetCoord1"));
            int d = Integer.parseInt(request.getParameter("TargetCoord2"));
            int occupyCount = Integer.parseInt(request.getParameter("ArmiesToMove"));
            int aDice = Integer.parseInt(request.getParameter("AttackDice"));
            int dDice = Integer.parseInt(request.getParameter("DefendDice"));
            System.out.println("aDice: "+aDice);
            System.out.println("dDice: "+ dDice); // doot doot doot test
           	Player currplayer = game.getPlayers().get(game.getCurrTurn());
           	request.setAttribute("currplayer",currplayer);
           	//Unit unit = new Unit("Space Frigate",40,6,0,currplayer); //probably need to give all races same unit, or ugly fix later
           	int[] co = {a,b};
            int[] attackCo = {c, d};
           	Territory[][] newMap = game.getMap();
           	players = game.getPlayers();
           	request.setAttribute("players",players);
           	request.setAttribute("map",newMap);
           	//request.setAttribute("badSpawn", new Integer(0));
            int attackArmyCount = getArmyCountAtPosition(co, newMap);
            int defendArmyCount = getArmyCountAtPosition(attackCo, newMap);
            boolean validArmyOccupySize = false;
            if (attackArmyCount > occupyCount) {// must leave at least one soldier behind
                if (attackArmyCount > 0) {
                    validArmyOccupySize = true;
                    
                } else {
                    // badSpawn flag set to "wrong number of armies to occupy with."
                    request.setAttribute("badSpawn", new Integer(2)); // bad army count
                    System.out.println("Invalid attacking army count.");
                }
            } else {
                // badSpawn flag set to "wrong number of armies to occupy with."
                request.setAttribute("badSpawn", new Integer(2)); // bad army count
                System.out.println("Invalid attacking army count.");
            }
            boolean validDiceCounts = false;
            if (validArmyOccupySize) { // prevent army size error message being overrided by dice count error message.
                if (aDice <= attackArmyCount ){ //&& aDice <= 3) {
                    if (dDice <= defendArmyCount ){//&& dDice <= 2) {
                        validDiceCounts = true;
                    } else {
                        System.out.println("Invalid die count.");
                        request.setAttribute("badSpawn", new Integer(3)); // bad dice count
                    }
                } else {
                    System.out.println("Invalid die count.");
                    request.setAttribute("badSpawn", new Integer(3)); // bad dice count
                }
            }
            Player defendingPlayer = null;
            int[] attackResult = {0, 0, 0};
            if (validArmyOccupySize && validDiceCounts) {
                if((check(co,currplayer) || checkForPlayerOwnedArmy(co, newMap, currplayer)))
                {
                    // if the territory is owned by the player, then it is valid
                    // check if territory is occupied by another player, asteroid, or is empty
                    if (checkForAdjacentSquare(co, attackCo) && !checkForAsteroid(attackCo, newMap)) {
                        if (checkForOpposingArmy(attackCo, newMap, currplayer) ) {
                            //game.spawn(game.getMap(),unit, 1, co, game.getID());
                            // ATTACK function here
                            defendingPlayer = getPlayerAtPosition(attackCo, newMap);
                            attackResult = game.attackTerritory(co, attackCo, aDice, dDice, occupyCount);
                            // flag as attack result, and setAttributes
                            
                            request.setAttribute("badSpawn", new Integer(5));
                            request.setAttribute("defendingPlayer", defendingPlayer);
                        } else {
                            // no other army, simpy move units to location.
                            game.occupyTerritory(co, attackCo, occupyCount);
                            // flag as occupation and set Attributes
                            request.setAttribute("badSpawn", new Integer(4)); // free invasion.
                        }
                    } else {
                        System.out.println("You can't attack there!");
                        request.setAttribute("badSpawn", new Integer(1)); // invalid square
                    }
                } else {
                    System.out.println("You can't attack there!");
                    request.setAttribute("badSpawn", new Integer(1)); // invalid square
                }
            } // error message already handled
            
            // attack result attributes
            request.setAttribute("attackResult", attackResult);
            request.setAttribute("defendingPlayer", defendingPlayer);
            
            //game.nextTurn();
            //System.out.println("It is now turn "+game.getCurrTurn());
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/attack.jsp");
            dispatcher.forward(request,response);
        } else if (operation.equalsIgnoreCase("FINISH_TURN") ) {
           	game.nextTurn();
           	System.out.println("It is now turn "+game.getCurrTurn());
           	Territory[][] newMap = game.getMap();
           	request.setAttribute("map",newMap);
            Player currplayer = game.getPlayers().get(game.getCurrTurn());
           	request.setAttribute("currplayer",currplayer);
            request.setAttribute("players", players);
            // attack result attributes (not used)
            int[] attackResult = {0, 0, 0};
            Player defendingPlayer = null;
            request.setAttribute("attackResult", attackResult);
            request.setAttribute("defindingPlayer", defendingPlayer);
            // message flag
            request.setAttribute("badSpawn", new Integer(0));
           	
           	RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/confirmation.jsp");
            dispatcher.forward(request,response);
        } else if (operation.equalsIgnoreCase("CONFIRMATION")) {
        	
        	if(players.size() > 2 && players.size() < 7 && seperateCountries(players))
        	{
        	
        	game.finishAddingPlayers();
        	game.finishConfirmation();
        	Territory[][] map = game.initializeBoard();
        	request.setAttribute("players",players);
        	request.setAttribute("turn",game.getCurrTurn());
        	Player currplayer = game.getPlayers().get(game.getCurrTurn());
        	request.setAttribute("currplayer",currplayer);
        	request.setAttribute("map",game.getMap());
        	System.out.println("We're starting the game! It's turn "+game.getCurrTurn());
            RequestDispatcher dispatcher = 
            getServletContext().getRequestDispatcher("/confirmation.jsp");
            request.setAttribute("badSpawn", new Integer(0));
            dispatcher.forward(request,response);
        	}
        	else
        	{
        		System.out.println("The number of players in the game isn't correct!");
            	request.setAttribute("players", players);
        		RequestDispatcher dispatcher = 
                        getServletContext().getRequestDispatcher("/list.jsp");
                        dispatcher.forward(request,response);
        	}
        
        } else if (operation.equalsIgnoreCase("ADD")){ //add
        	
        	if(players.size() <6)
        	{
        	String name = request.getParameter("Name");
        	String country = request.getParameter("Country");
        	game.addPlayer(name,country);
        	players = game.getPlayers();
            request.setAttribute("players", players);
        	request.setAttribute("turn",game.getCurrTurn());
            RequestDispatcher dispatcher = 
                getServletContext().getRequestDispatcher("/list.jsp");
            dispatcher.forward(request,response);
        	}
        	else
        	{
        		System.out.println("There are too many players!!!");
        		   request.setAttribute("players", players);
        		 	request.setAttribute("turn",game.getCurrTurn());
        		   RequestDispatcher dispatcher = 
        	                getServletContext().getRequestDispatcher("/list.jsp");
        	            dispatcher.forward(request,response);
        	}
        }
    }
    protected boolean check(int[] coords, Player player)
    {
        // check if player owns the coords tile.
    	int homebasex = player.getHomebaseCoords()[0];
    	int homebasey = player.getHomebaseCoords()[1];
    	
    	int x = coords[0];
    	int y = coords[1];
        
        boolean withinHomeBase = false;
    	if (Math.abs(homebasey-y) <= 1 && Math.abs(homebasex-x) <=1) 
            withinHomeBase = true;
        System.out.println("withinHomeBase: "+ withinHomeBase);
    	return withinHomeBase;
    	
    	
    }
    
    protected boolean checkForPlayerOwnedArmy(int[] coords, Territory[][] map, Player player) {
        int x = coords[0];
        int y = coords[1];
        
        boolean occupiedByArmy = false;
        Player playerAtThisPosition = map[x][y].getPlayer();
        if (playerAtThisPosition != null) {
            if (player.equals(playerAtThisPosition))
                occupiedByArmy = true;
        }
        System.out.println("checkForPlayerOwnedArmy: "+occupiedByArmy);
        return occupiedByArmy;
    }
    
    protected boolean checkForAsteroid(int[] coords, Territory[][] map) {
        int x = coords[0];
        int y = coords[1];
        
        boolean occupiedByAsteroid = false;
        if (map[x][y].hasAsteroid())
            occupiedByAsteroid = true;
        System.out.println("checkForAsteroid: "+occupiedByAsteroid);
        return occupiedByAsteroid;
    }
    
    protected boolean checkForOpposingArmy(int[] coords, Territory[][] map, Player player) {
        int x = coords[0];
        int y = coords[1];
        
        boolean occupiedByOpposingArmy = false;
        Player opposingPlayer = map[x][y].getPlayer();
        if (opposingPlayer != null) {
            if (!player.equals(opposingPlayer)) {
                occupiedByOpposingArmy = true;
            }
        }
        System.out.println("Check for opposing Army: " + occupiedByOpposingArmy);
        return occupiedByOpposingArmy;
    }
    
    protected boolean checkForAdjacentSquare(int[] coords, int[] nextCoords) {/*
        double y = coords[0];
        double X = coords[1];
        double y2 = nextCoords[0];
        double x2 = nextCoords[1];
        double yDiff = y2 - y;
        double xDiff = x2 - x;
        double dist = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
        boolean result = false;
        if (dist <= 1.0)
            result = true;*/
        int x = coords[0];
        int y = coords[1];
        int x2 = nextCoords[0];
        int y2 = nextCoords[1];
        // make sure the grid coordinates are valid, as well.
        boolean result = false;
        if (x < 9 || x2 < 9) {
            if (y < 15 || y2 < 15) {
                int yDiff = Math.abs(y2 - y);
                int xDiff = Math.abs(x2 - x);
                if (yDiff <= 1 && xDiff <= 1) {
                    result = true;
                }
            }
        }
        System.out.println("Check for ajacent square: " + result);
        return result;
    }
    
    protected int getArmyCountAtPosition(int[] coords, Territory[][] map) {
        int x = coords[0];
        int y = coords[1];
        System.out.println("doot");
        Territory pos = map[x][y];
        System.out.println("Army count: " + map[x][y].getOccupants().size());
        return pos.getOccupants().size();
    }
    
    protected Player getPlayerAtPosition(int[] coords, Territory[][] map) {
        int x = coords[0];
        int y = coords[1];
        Territory pos = map[x][y];
        return pos.getPlayer();
    }
    
    //makes sure each player is representing a different country
    //redundant with RiskGame
    protected boolean seperateCountries(ArrayList<Player> players)
    {
     System.out.println("The size of the array "+players.size());
     boolean go = true;
     //0-1 size =3
     for(int a = 0; a<players.size()-1;a++)
     { //1-2
     for(int b = a+1; b<players.size();b++)
     {
     System.out.println(a+" "+b);
     if(players.get(a).getCountry().equals(players.get(b).getCountry()))
     {	
     System.out.println("Brothers can't fight brothers!");
     go = false;
     break;
     }
     }
    
     }
     return go;
    
    }
    
    /**
     * Called when HTTP method is GET 
     * (e.g., from an <a href="...">...</a> link).
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException {
        System.out.println("In doGet()");
//        players = new ArrayList<Player>();
   //     System.out.println("Starting a new game, erasing the player ArrayList");

        request.setAttribute("players", players);
    	request.setAttribute("turn",game.getCurrTurn());
        RequestDispatcher dispatcher = 
            getServletContext().getRequestDispatcher("/list.jsp");
        dispatcher.forward(request,response);
    }

    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException     
         {

        System.out.println("In doPut()");
        String title = (String) request.getParameter("Name");
        String task = (String)  request.getParameter("Country");
        int id = getId(request);
        players.set(id, new Player(title, task)); 
        for(Player a:players)
        {
        	System.out.println(a);
        }
        
     
        
        request.setAttribute("players", players);
        RequestDispatcher dispatcher = 
            getServletContext().getRequestDispatcher("/list.jsp");
        dispatcher.forward(request,response);
    	}

         

    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response)
            throws IOException, ServletException {
        System.out.println("In doDelete()");
        int id = getId(request);
        game.removePlayer(id);
        request.setAttribute("players", players);
    	request.setAttribute("turn",game.getCurrTurn());
        RequestDispatcher dispatcher = 
            getServletContext().getRequestDispatcher("/list.jsp");
        dispatcher.forward(request,response);
    }

    private int getId(HttpServletRequest request) {
        String uri = request.getPathInfo();
        // Strip off the leading slash, e.g. "/2" becomes "2"
        String idStr = uri.substring(1, uri.length()); 
        return Integer.parseInt(idStr);
    }

}
