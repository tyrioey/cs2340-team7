Changelog
============================
7/3/2013
-Because of the way ArrayList.contains() works, changed Player.occupiedTerritories <Territory,Integer> to <String, Integer>. This is because contains searchs for objects, and because of the way Territories are stored in each unit the game cannot recognize that two individual units can occupy the same Territory. Changed keys so that they are Territory names instead of the actual Territory
-Complete overhaul of TodoServlet, RiskGame and confirmation.jsp. Moved all of M2 placeholder code from confirmation.jsp to RiskGame.
-Properly integrated RiskGame into TodoServlet, and Confirmation into TodoServlet
-M3 finished.

6/28/2013
-Changed several ArrayList variables to TreeMaps for unique identification (Player.army, Player.occupiedTerritories, Territorry.occupiedByUnit). 
-Updated confirmation.jsp and respective classes to update change to TreeMap
-Implmented update() methods for Player and Territory, the purpose of which is to update the current state of occupiedTerritories and occupiedByUnit to reflect unit movement. Still buggy.
-M3 not yet fully implemented.


6/21/2013
-Confirmation.jsp now displays the Map, along with Home Territories, Asteroids and 	Units in ascii form
-Made several changes to Player, Unit and Territory classes
-Added ColorCode and spawn methods to Confirmation.jsp
-M2 finished

6/19/2013
-Finished transition from Todo to Player class
-Updated Player, Territory, Resource and Unit classes
-Added some Territory and Resource testing to Confirmation.jsp

6/18/2013
-Started changelog
-Fixed player limits, can't go to confirmation screen with less than 3 players or more than 6, clicking confirm does nothing.
-"Fixed" assigned number of troops. The idea is to not directly copy Risk, so this may be changed in the future.
-Started Player and Territory classes, still using Todo so as to note break dependancies
-Changed TreepMap of Todo (soon to be Players) to ArrayList
-Added seperateCountries method to TodoServlet; ensures each player is in a different country
-M1 for sure finished