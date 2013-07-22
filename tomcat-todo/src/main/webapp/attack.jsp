<%@ page import="edu.gatech.cs2340.todo.model.Player" %>
<%@ page import="edu.gatech.cs2340.todo.model.Territory" %>
<%@ page import="edu.gatech.cs2340.todo.model.Resource" %>
<%@ page import="edu.gatech.cs2340.todo.model.Unit" %>
<%@ page import="java.util.*" %>


<% //some initializations
//nearly all of this code needs to merged into the RiskGame class.


%>
<html>
<head>
<title>The Game!</title>



<%//instantiating the map 
ArrayList<Player> players = (ArrayList<Player>) request.getAttribute("players");
Territory[][] map = (Territory[][]) request.getAttribute("map"); 
Player currPlayer = (Player) request.getAttribute("currplayer");
Collections.sort(players);
Integer badSpawn = (Integer)request.getAttribute("badSpawn");
int[] attackResult = (int[])request.getAttribute("attackResult");
Player dPlayer = (Player)request.getAttribute("defendingPlayer");%>



<% for (Player player: players) { %>
<span style ="color:<%= colorCode(player) %>"> <%= player %></span><br>
<% } %>
<br>
Let's make a map! <br><br>
The "O"s represent each player's home base!<br>
The "X"s represent impassable asteroids!<br>
The "#"s in each territory represent the number of Units in each territory!<br>
<br>
<br>

<select name = "Players">
<% TreeMap<String,Integer> terr = currPlayer.getOccupiedTerritories();
for(String key: terr.keySet()) {%>
<option value = "<%=key%>"><%=key%></option>
<%} %>
</select>

<font face="courier">
<%if (badSpawn.compareTo(1) == 0){%>
It's <span style="color<%=colorCode(currPlayer)%>"><%=currPlayer.getName()%>'s turn to attack! Invalid AttackFrom or TargetCoordinates coordinates!</span>
<%} else if (badSpawn.compareTo(2) == 0) {%>
It's <span style="color<%=colorCode(currPlayer)%>"><%=currPlayer.getName()%>'s turn to attack! Invalid Armies To Move coordinates!</span>
<%} else if (badSpawn.compareTo(3) == 0) {%>
It's <span style="color<%=colorCode(currPlayer)%>"><%=currPlayer.getName()%>'s turn to attack! Invalid Die Count!</span>
<% } else if (badSpawn.compareTo(4) == 0) { %>
It's <span style="color<%=colorCode(currPlayer)%>"><%=currPlayer.getName()%>'s turn to attack! Territory occupied without a fight!</span>
<% } else if (badSpawn.compareTo(5) == 0) { %>
It's <span style="color<%=colorCode(currPlayer)%>"><%=currPlayer.getName()%>'s turn to attack! You called an attack! The results are below:</span><br>
<span style="color<%=colorCode(currPlayer)%>"><%=currPlayer.getName()%>'s armies lost: <%=attackResult[0]%></span><br>
<span style="color<%=colorCode(dPlayer)%>"><%=dPlayer.getName()%>'s armies lost: <%=attackResult[1]%></span><br>
    <%if (attackResult[2] == 1) {%>
    <span style="color<%=colorCode(currPlayer)%>"><%=currPlayer.getName()%> has taken the territory!</span>
    <%} else {%>
    It's <span style="color<%=colorCode(currPlayer)%>"><%=currPlayer.getName()%>'s turn to attack!</span>
    <%}%>
<%} else if (badSpawn.compareTo(0) == 0) {%>
It's <span style="color<%=colorCode(currPlayer)%>"><%=currPlayer.getName()%>'s turn to attack!</span>
<% }%>
<form action = "update" method="POST">
<input type="hidden" name = "operation" value = "ATTACK" />
AttackFromCoord1<input type="text" name="AttackFromCoord1" /><br>
AttackFromCoord2<input type="text" name="AttackFromCoord2" /><br>
TargetCoord1<input type="text" name="TargetCoord1" /><br>
TargetCoord2<input type="text" name="TargetCoord2" /><br>
Occupy with <input type="text" name="ArmiesToMove" /> armies if you destroy the foe!<br>
Attack Dice<input type="text" name="AttackDice" /><br>
Defend Dice<input type="text" name="DefendDice" /><br>
<input type="submit" value="Attack!" />

</form>
<form action="update" method="POST">
<input type="hidden" name="operation" value = "FINISH_TURN">
<input type="submit" value="Finish Turn" />
</form>

</form>



[~]<% for(int a = 0; a < 15;a++){ %>[<%=a%>]<%}%>
<br>
<%//printing the map 
  for(int a = 0; a <9; a++)
  { %>
	[<%=a%>]<%
	for(int b = 0; b < 15; b++)	
	{
 	if(map[a][b].hasResources()) { %>[x]<% }   
	if(map[a][b].isHomeBase()) {%>[<span style="color:<%=colorCode(map[a][b].getPlayer())%>">O</span>]<%} 
 	if(map[a][b].isOccupied()) { %>[<span style="color:<%=colorCode(map[a][b].getPlayer())%>"><%=map[a][b].getOccupants().size()%></span>]<%} 
	if (!map[a][b].hasResources() && !map[a][b].isHomeBase() && !map[a][b].isOccupied()) {%>[ ]<%}  
	 }%>
<br>	
<%}%>
</font>
<%//color string can be probably be stored in player instead of making a method to calculate it
//returns the color associated with each country %>
<%! public String colorCode(Player player){ 
if (player==null) {return "pink";}
else if (player.getCountry().equals("Polaris")) { return "purple";}
else if (player.getCountry().equals("Alpha-Centauri")) { return "green";}
else if (player.getCountry().equals("Char")) { return "red";} 
else if (player.getCountry().equals("Midichloria")) { return "blue";}
else if (player.getCountry().equals("Borg")) { return "gray";} 
else if (player.getCountry().equals("HAL Space Station")) { return "orange";}
 return "";} %>
 




<% /* for(Player player: players) 
{
	Unit z = new Unit("Zergling",35,5,1,null);
	System.out.println(player.getArmy().keySet());
	TreeMap<Integer,Unit> army = player.getArmy();
	System.out.println(army.size());

System.out.println("~~~~");

} */  
%>

<%/*  for(Territory[] a: map) 
	{
	for(Territory b: a)
		{
			System.out.println(b);
			System.out.println(b.getOccupants());
		}
	System.out.println("=====");

	}
System.out.println("~~~~");
 */
%>


</head>
</html>