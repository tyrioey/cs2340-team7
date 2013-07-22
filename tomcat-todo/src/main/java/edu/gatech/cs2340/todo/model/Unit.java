package edu.gatech.cs2340.todo.model;



import java.util.*;

public class Unit
{
    static int idCounter = 0; // each unit has a unique id
	String name;
	int id; 
	int health;
	int maxHealth;
	int strength; //amount of damage Unit can deal 
	int defense; //lowers amount of damage Unit takes 
//	ArrayList<Resource> inventory; //inventory of unit ie speed boost, attack boost, etc.
	Player owner; 
	Territory previouslyOccupied;
	Territory occupying;
	Random rand = new Random();
	
	public  Unit(String name, int health, int strength, int defense)
	{
		this.name = name;
		id = idCounter;
        idCounter += 1;
		this.health = maxHealth = health;
		this.strength=strength;
		this.defense = defense;
	//	inventory = new ArrayList<Resource>();
		owner = null;
		previouslyOccupied = null;
		occupying = null;
		
	}
	public  Unit(String name, int health, int strength, int defense, Player owner)
	{
		this.name = name;
		id = 0;
		this.health = maxHealth = health;
		this.strength=strength;
		this.defense = defense;
	//	inventory = new ArrayList<Resource>();
		this.owner = owner;
		previouslyOccupied = null;
		occupying = null;
		
	}
	public String getName()
	{
		return name;
	}
	public void takeDamage(int a)
	{
		health-=a;
	}
	public int getHealth()
	{
		return health;
	}
	public int getMaxHealth()
	{
		return maxHealth;
	}
	public int getStrength()
	{
		return strength;
	}
	public int getDefense()
	{
		return defense;
	}
	public void setID(int id)
	{
		this.id = id;
	}
	public int getID()
	{
		return id;
	}
	public void setOwner(Player player)
	{
		owner = player;
	}
	public Player getOwner()
	{
		return owner;
	}
	//setTerritory and move methods separated to smooth over Player.update()
	public void setTerritory(Territory location)
	{
		previouslyOccupied = occupying;
		occupying = location;
	}
	public void move(Territory newLocation)
	{
		setTerritory(newLocation);
		owner.update(this);
	}
	public Territory getTerritory()
	{
		return occupying;
	}
	public Territory getPreviouslyOccupied()
	{
		return previouslyOccupied;
	}
	public int[] attack(Unit enemy)
	{	
//		currently only 1 die roll per modifier. Maybe more alongside future implmentations ie. flanking attack bonus/certain items, etc.
		int attackDice = rand.nextInt(6)+1;
		int enemyDefenseDice = rand.nextInt(6)+1;
		int enemyAttackDice = rand.nextInt(6)+1;
		int defenseDice = rand.nextInt(6)+1;
		int counterAttackDice = rand.nextInt(6)+1;
		
		int damage = attackDice*strength-enemyDefenseDice*enemy.getDefense();
		enemy.takeDamage(damage);
		
		int enemyDamage = enemyAttackDice*enemy.getStrength()-defenseDice*defense;
		enemyDamage = counterAttackDice/6*enemyDamage; //counterattack damage reduced by a factor of die roll
		takeDamage(enemyDamage);
		
		int[] dice = {attackDice,enemyDefenseDice,enemyAttackDice,defenseDice,counterAttackDice,damage,enemyDamage};
		return dice;
	}
	public Unit clone()
	{
		Unit a = new Unit(this.getName(),this.getMaxHealth(),this.getStrength(),this.getDefense());
		a.setTerritory(occupying);
		a.setOwner(owner);
		return a;
	}
	public String toString()
	{
		return name+" with identification number "+getID()+" currently has "+health+"/"+maxHealth+" hit points, is located at "+occupying+
				" and is owned by "+owner.getName();
	}

}
	
	
	
	
	
	
	