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

@WebServlet(urlPatterns={
        "/list", // GET
        "/create", // POST
        "/update/*", // PUT
        "/delete/*", // DELETE
        "/confirmation"
    })
public class TodoServlet extends HttpServlet {
    
    ArrayList<RiskGame> games = new ArrayList<RiskGame>();
  
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException, ServletException {
    
        System.out.println("In doPost()");
        
        // Handle the hidden HTML form field that simulates
        // HTTP PUT and DELETE methods.
        String operation = (String) request.getParameter("operation");
        System.out.println(operation);
        //System.out.println("It is turn "+game.getCurrTurn());
     
        if (null == operation){operation = "CONFIRMATION";}
        
        //probably more elegant fix later, like just changing operation to "Confirmation"
        //if (null == operation){operation = "CONFIRMATION";}
        if (operation.equalsIgnoreCase("NEW")) {
            // no input
            
            // state
            RiskGame game = new RiskGame();
            games.add(game);
            
            // dispatch
            listDispatcher(request, response, game);
            
        } //update
        else if (operation.equalsIgnoreCase("CREDITS")) {
            // no input
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/credits.jsp");
            dispatcher.forward(request,response);
      
  
            
        } //update
        else if (operation.equalsIgnoreCase("PUT")) {
            // input, state and dispatch
            RiskGame game = getGameFromID(request);
            int id = getId(request);
            int iPlayerActionsSoFar = Integer.parseInt(request.getParameter("playerActionsSoFar"));
            

            String name = request.getParameter("Name");
         String country = request.getParameter("Country");

            
            // state
            String success = null;
            boolean stateCheck = false;
            if (game != null) {
                System.out.println("Game State: " +game.getGameState());
                if (game.getGameState() == RiskGame.ADD_PLAYERS &&
                            game.getPlayerActionsSoFar() == iPlayerActionsSoFar) {
                    stateCheck = true;
                    success = game.updatePlayer(id,name,country);
                }
            } else {
                System.out.println("can't find gameID");
            }
            
            // dispatch
            if (success != null) {
                listDispatcher(request, response, game);
            } else if (stateCheck) {
                System.out.println(success);
                listDispatcher(request, response, game);
            } else {
                indexDispatcher(request, response);
            }
        }
        else if (operation.equalsIgnoreCase("DELETE")) {
            // input, state and dispatch
            //doDelete(request, response);
            // input
            RiskGame game = getGameFromID(request);
            int id = getId(request);
            int iPlayerActionsSoFar = Integer.parseInt(request.getParameter("playerActionsSoFar"));
            
            // state
            String success = null;
            boolean stateCheck = false;
            if (game != null) {
                System.out.println("Game State: " +game.getGameState());
                if (game.getGameState() == RiskGame.ADD_PLAYERS &&
                            game.getPlayerActionsSoFar() == iPlayerActionsSoFar) {
                    stateCheck = true;
                    success = game.removePlayer(id);
                }
            } else {
                System.out.println("can't find gameID");
            }
            
            // dispatch
            if (success != null) {
                listDispatcher(request, response, game);
            } else if (stateCheck) {
                System.out.println(success);
                listDispatcher(request, response, game);
            } else {
                indexDispatcher(request, response);
            }
            
        } else if (operation.equalsIgnoreCase("LOAD")) {
            // load
            RiskGame game = getGameFromID(request);
            
            // state
            
            // dispatch
            if (game == null) {
                indexDispatcher(request, response);
            } else if (game.getGameState() == RiskGame.ADD_PLAYERS) {
                listDispatcher(request, response, game);
            } else if (game.getGameState() == RiskGame.SELECT_LOCATION) {
                confirmationDispatcher(request, response, game);
            } else if (game.getGameState() == RiskGame.COMMAND) {
                commandDispatcher(request, response, game);
            }
            
            
        } else if(operation.equalsIgnoreCase("TURN")) {
         System.out.println("We're going to the next turn!");
            // input
            RiskGame game = getGameFromID(request);
            int iTurnCount, iPlayerTurn, iPlayerActionsSoFar;
            iTurnCount = Integer.parseInt(request.getParameter("turnCount"));
            iPlayerTurn = Integer.parseInt(request.getParameter("playerTurn"));
            iPlayerActionsSoFar = Integer.parseInt(request.getParameter("playerActionsSoFar"));
            int turnCount = -1;
            int playerTurn = -1;
            int playerActionsSoFar = -1;
            if (game != null) {
                turnCount = game.getTurnCount();
                playerTurn = game.getCurrTurn();
                playerActionsSoFar = game.getPlayerActionsSoFar();
            }
            
            // state
            boolean stateCheck = false;
            if (game!= null) {
                if (iTurnCount == turnCount && iPlayerTurn == playerTurn &&
                    iPlayerActionsSoFar == playerActionsSoFar) {
                    game.nextTurn();
                    stateCheck = true;
                }
            }
            
            // dispatch
            if (stateCheck)
                confirmationDispatcher(request, response, game);
            else
                indexDispatcher(request, response);
        
        }
        else if(operation.equalsIgnoreCase("SPAWN")) {
                System.out.println("We're SPAWNING more units!");
                // input
             int a = Integer.parseInt(request.getParameter("Coord1"));
             int b = Integer.parseInt(request.getParameter("Coord2"));
                int[] co = {a,b};
                RiskGame game = getGameFromID(request);
                int iTurnCount, iPlayerTurn, iPlayerActionsSoFar;
                iTurnCount = Integer.parseInt(request.getParameter("turnCount"));
                iPlayerTurn = Integer.parseInt(request.getParameter("playerTurn"));
                //iPlayerActionsSoFar = request.getParameter("playerActionsSoFar");
                int turnCount = -1;
                int playerTurn = -1;
                int playerActionsSoFar = -1;
                if (game != null) {
                    turnCount = game.getTurnCount();
                    playerTurn = game.getCurrTurn();
                    //playerActionsSoFar = game.getPlayerActionsSoFar();
                }
                
                // state
                boolean stateCheck = false;
                boolean success = false;
                if (game != null ) {
                    if (iTurnCount == turnCount && iPlayerTurn == playerTurn) {
                        stateCheck = true;
                        success = game.spawnUpdate(co);
                    }
                }
                // dispatch
                if (success) {
                    commandDispatcher(request, response, game);
                } else if (stateCheck) {
                    confirmationDispatcher(request, response, game);
                }else {
                    indexDispatcher(request, response);
                }
        }

        else if(operation.equalsIgnoreCase("MOVE")) {
            // input
            String[] selectedUnitIDs = request.getParameterValues("unit");
            ArrayList<Integer> selectedUnitIntIDs = new ArrayList<Integer>();
            for (String ids: selectedUnitIDs)
                selectedUnitIntIDs.add(Integer.parseInt(ids));
                
            int x = Integer.parseInt(request.getParameter("MoveCoordX"));
         int y = Integer.parseInt(request.getParameter("MoveCoordY"));
         int[] moveCo = {y,x};
            RiskGame game = getGameFromID(request);
            int iTurnCount, iPlayerTurn, iPlayerActionsSoFar;
            iTurnCount = Integer.parseInt(request.getParameter("turnCount"));
            iPlayerTurn = Integer.parseInt(request.getParameter("playerTurn"));
            iPlayerActionsSoFar = Integer.parseInt(request.getParameter("playerActionsSoFar"));
            int turnCount = -1;
            int playerTurn = -1;
            int playerActionsSoFar = -1;
            if (game != null) {
                turnCount = game.getTurnCount();
                playerTurn = game.getCurrTurn();
                playerActionsSoFar = game.getPlayerActionsSoFar();
            }
            
            // state
            boolean success = false;
            boolean stateCheck = false;
            if (game!= null) {
                if (iTurnCount == turnCount && iPlayerTurn == playerTurn &&
                    iPlayerActionsSoFar == playerActionsSoFar) {
                    stateCheck = true;
                    success = game.moveUpdate(selectedUnitIntIDs, moveCo);
                }
            }
            if (success)
                System.out.println("We are moving!");
            else
                System.out.println("You can't move there!");
            
            // dispatch
            if (success ) {
                commandDispatcher(request, response, game);
            } else if (stateCheck) {
                commandDispatcher(request, response, game);
            } else {
                indexDispatcher(request, response);
            }
        }
      
        else if(operation.equalsIgnoreCase("ATTACK")) {
            // input
            String[] selectedUnitIDs = request.getParameterValues("unit");
            ArrayList<Integer> selectedUnitIntIDs = new ArrayList<Integer>();
            for (String ids: selectedUnitIDs)
                selectedUnitIntIDs.add(Integer.parseInt(ids));
            
            int x = Integer.parseInt(request.getParameter("AttackCoordX"));
int y = Integer.parseInt(request.getParameter("AttackCoordY"));
int[] moveCo = {y,x};
            RiskGame game = getGameFromID(request);
            int iTurnCount, iPlayerTurn, iPlayerActionsSoFar;
            iTurnCount = Integer.parseInt(request.getParameter("turnCount"));
            iPlayerTurn = Integer.parseInt(request.getParameter("playerTurn"));
            iPlayerActionsSoFar = Integer.parseInt(request.getParameter("playerActionsSoFar"));
            int turnCount = -1;
            int playerTurn = -1;
            int playerActionsSoFar = -1;
            if (game != null) {
                turnCount = game.getTurnCount();
                playerTurn = game.getCurrTurn();
                playerActionsSoFar = game.getPlayerActionsSoFar();
            }
            
            // state
            boolean stateCheck = false;
            if (game!= null ) {
                if (iTurnCount == turnCount && iPlayerTurn == playerTurn &&
                    iPlayerActionsSoFar == playerActionsSoFar) {
                    stateCheck = true;
                    game.attackUpdate(selectedUnitIntIDs, moveCo);
                }
            }
            
            // dispatch
            if(game.checkOver())
            {
             Player winner = null;
             for(Player player: game.getPlayers())
             if(!player.hasLost())
             winner = player;
           request.setAttribute("winner",winner.getName());
           request.setAttribute("country",winner.getCountry());
         RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/congratulations.jsp");
             dispatcher.forward(request,response);
            }
            else if (stateCheck)
                commandDispatcher(request, response, game);
            else
                indexDispatcher(request, response);

      
}
else if(operation.equalsIgnoreCase("TERRITORY")){
System.out.println("Displaying Units");
            // input
            RiskGame game = getGameFromID(request);
            int iTurnCount, iPlayerTurn, iPlayerActionsSoFar;
            iTurnCount = Integer.parseInt(request.getParameter("turnCount"));
            iPlayerTurn = Integer.parseInt(request.getParameter("playerTurn"));
            iPlayerActionsSoFar = Integer.parseInt(request.getParameter("playerActionsSoFar"));
            int turnCount = -1;
            int playerTurn = -1;
            int playerActionsSoFar = -1;
            if (game != null) {
                turnCount = game.getTurnCount();
                playerTurn = game.getCurrTurn();
                playerActionsSoFar = game.getPlayerActionsSoFar();
            }
            
            // state
            boolean stateCheck = false;
            if (game!= null ) {
                if (iTurnCount == turnCount && iPlayerTurn == playerTurn &&
                    iPlayerActionsSoFar == playerActionsSoFar) {
                    stateCheck = true;
                    // might need to actuall update something here
                }
            }
            
            // dispatch
            if (stateCheck)
                commandDispatcher(request, response, game);
            else
                indexDispatcher(request, response);
}	
        else if (operation.equalsIgnoreCase("CONFIRMATION")) {
            // input
            RiskGame game = getGameFromID(request);
            int iPlayerActionsSoFar = Integer.parseInt(request.getParameter("playerActionsSoFar"));
            
            // state
            String success = null;
            boolean stateCheck = false;
            if (game != null ) {
                if (game.getGameState() == RiskGame.ADD_PLAYERS &&
                        iPlayerActionsSoFar == game.getPlayerActionsSoFar()) {
                    stateCheck = true;
                    success = game.finishAddingPlayers();
                    game.finishConfirmation();
                }
            }
            
            // dispatch
            if (success == null) {
                confirmationDispatcher(request, response, game);
            } else if (stateCheck) {
                listDispatcher(request, response, game);
            } else {
                indexDispatcher(request, response);
            }
        
        } else if (operation.equalsIgnoreCase("ADD")){
         // input
            RiskGame game = getGameFromID(request);
            String name = request.getParameter("Name");
         String country = request.getParameter("Country");
            int iPlayerActionsSoFar = Integer.parseInt(request.getParameter("playerActionsSoFar"));
            
            // state
            boolean stateCheck = false;
            String success = null;
            if (game != null) {
                System.out.println("Game State: " +game.getGameState());
                if (game.getGameState() == RiskGame.ADD_PLAYERS &&
                            game.getPlayerActionsSoFar() == iPlayerActionsSoFar) {
                    stateCheck = true;
                    success = game.addPlayer(name, country);
                }
            } else {
                System.out.println("can't find gameID");
            }
            
            // dispatch
            if (success != null) {
                listDispatcher(request, response, game);
            } else if (stateCheck) {
                System.out.println(success);
                listDispatcher(request, response, game);
            } else {
                indexDispatcher(request, response);
            }
        }
    }
    
    // HTTP methods need to retrieve the proper game object, if needed.

    /**
* Called when HTTP method is GET
* (e.g., from an <a href="...">...</a> link).
*/
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException
    {
        System.out.println("In doGet()");
        RiskGame game = getGameFromID(request);
        
        //listDispatcher(request, response, aGame);
        if (game != null) {
            request.setAttribute("players", game.getPlayers());
            RequestDispatcher dispatcher =
                getServletContext().getRequestDispatcher("/list.jsp");
            dispatcher.forward(request,response);
        } else {
            indexDispatcher(request, response);
        }
    }

protected void doPut(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException{

System.out.println("In doPut()");
String title = (String) request.getParameter("Name");
String task = (String) request.getParameter("Country");
RiskGame game = getGameFromID(request);
int id = getId(request);
        
request.setAttribute("players", game.getPlayers());
request.setAttribute("gameID", game.getPlayers());
RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/list.jsp");
dispatcher.forward(request,response);
}

protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response)
            throws IOException, ServletException{

System.out.println("In doDelete()");
        RiskGame game = getGameFromID(request);
int id = getId(request);
game.removePlayer(id);
request.setAttribute("players", game.getPlayers());
     request.setAttribute("turn",game.getCurrTurn());
RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/list.jsp");
      dispatcher.forward(request,response);
   }

private int getId(HttpServletRequest request){
String uri = request.getPathInfo();

//Strip off the leading slash, e.g. "/2" becomes "2"
String idStr = uri.substring(1, uri.length());

        return Integer.parseInt(idStr);
    }

    
    private RiskGame getGameFromID(HttpServletRequest request) {
        String gameIDString = request.getParameter("gameID");
        System.out.println("The gameID string: " + gameIDString);
        RiskGame aGame = null;
        if (gameIDString != null && !gameIDString.equals("")) {
            int gameID = Integer.parseInt( gameIDString );
            System.out.println("getGameFromID: " + gameID);
            for (int i = 0; i < games.size(); i++) {
                if (games.get(i).getGameID() == gameID) {
                    aGame = games.get(i);
                    break;
                }
            }
        }
        return aGame;
    }
    
    
    // dispatch functions, to make constructing the .jsp pages more straitforward.
    private void indexDispatcher(HttpServletRequest request, HttpServletResponse response)
                                    throws IOException, ServletException {
        System.out.println("Back to the index!");
        RequestDispatcher dispatcher =
            getServletContext().getRequestDispatcher("/index.html");
        dispatcher.forward(request,response);
    }
    
    private void listDispatcher(HttpServletRequest request, HttpServletResponse response,
                                            RiskGame aGame) throws IOException, ServletException {
        System.out.println("In list dispatcher.");
        request.setAttribute("players", aGame.getPlayers());
        request.setAttribute("gameID", aGame.getGameID());
        request.setAttribute("playerActionsSoFar", aGame.getPlayerActionsSoFar());
        RequestDispatcher dispatcher =
            getServletContext().getRequestDispatcher("/list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void confirmationDispatcher(HttpServletRequest request, HttpServletResponse response, RiskGame aGame) throws IOException, ServletException {
        Player currplayer = aGame.getCurrentPlayer();
        request.setAttribute("currplayer",currplayer);
        ArrayList<Player> playerList = aGame.getPlayers();
        request.setAttribute("gameID", aGame.getGameID());
        request.setAttribute("players",playerList);
        request.setAttribute("turnCount", aGame.getTurnCount());
        request.setAttribute("playerTurn", aGame.getCurrTurn());// needs player actions so far
        Territory[][] newMap = aGame.getMap();
        request.setAttribute("map",newMap);
        Unit unit = new Unit("Space Frigate",40,6,0, currplayer);
        TreeMap<String,Integer> terr = currplayer.getOccupiedTerritories();
        String territory = terr.firstKey();
        request.setAttribute("selectedTerritory",territory);
        int index = territory.indexOf('[');
        int index2 = territory.indexOf(']');
        int indexC = territory.indexOf(',');
        String subA = territory.substring(index+1,indexC);
        String subB = territory.substring(indexC+1,index2);
        int coA = Integer.parseInt(subA);
        int coB = Integer.parseInt(subB);
        TreeMap<Integer,Unit> occupants = aGame.getMap()[coA][coB].getOccupants();
        request.setAttribute("occupants", occupants);
        
        RequestDispatcher dispatcher =
            getServletContext().getRequestDispatcher("/confirmation.jsp");
            dispatcher.forward(request,response);
    }
    
    private void commandDispatcher(HttpServletRequest request, HttpServletResponse response,
                                            RiskGame aGame) throws IOException, ServletException {
        String territory = null;
        try {
            territory = request.getParameter("Players");
        } catch (Exception e) {
            System.out.println("using default territory");
        }
        
        Player currplayer = aGame.getCurrentPlayer();
        request.setAttribute("currplayer",currplayer);
        request.setAttribute("gameID", aGame.getGameID());
        request.setAttribute("turnCount", aGame.getTurnCount());
        request.setAttribute("playerTurn", aGame.getCurrTurn());
        request.setAttribute("playerActionsSoFar", aGame.getPlayerActionsSoFar());
        Territory[][] newMap = aGame.getMap();
        request.setAttribute("map", newMap);
        ArrayList<Player> playerList = aGame.getPlayers();
        request.setAttribute("players", playerList);
        TreeMap<String,Integer> terr = currplayer.getOccupiedTerritories();
        if (territory == null) {
            territory = terr.firstKey();
        }
        request.setAttribute("selectedTerritory",territory);
        int index = territory.indexOf('[');
        int index2 = territory.indexOf(']');
        int indexC = territory.indexOf(',');
        String subA = territory.substring(index+1,indexC);
        String subB = territory.substring(indexC+1,index2);
        int coA = Integer.parseInt(subA);
        int coB = Integer.parseInt(subB);
        request.setAttribute("selectedA", coA);
        request.setAttribute("selectedB", coB);
        TreeMap<Integer,Unit> occupants = aGame.getMap()[coA][coB].getOccupants();
        request.setAttribute("occupants", occupants);
        
       if(aGame.getPlayers().size() == 1){
     request.setAttribute("winner",aGame.getPlayers().get(0).getName());
     RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/congratulations.jsp");
         dispatcher.forward(request,response);
     }
     else{
    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/command.jsp");
        dispatcher.forward(request,response);
     }
    }
}