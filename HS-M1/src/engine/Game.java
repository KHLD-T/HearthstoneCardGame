package engine;

import java.util.ArrayList;

import exceptions.CannotAttackException;
import exceptions.FullFieldException;
import exceptions.FullHandException;
import exceptions.HeroPowerAlreadyUsedException;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughManaException;
import exceptions.NotSummonedException;
import exceptions.NotYourTurnException;
import exceptions.TauntBypassException;
import model.cards.Card;
import model.cards.minions.Icehowl;
import model.cards.minions.Minion;
import model.heroes.Hero;
import model.heroes.HeroListener;

public class Game implements ActionValidator,HeroListener {
	private Hero firstHero;
	private Hero secondHero;
	private Hero currentHero;
	private Hero opponent;
	private GameListener listener;
	
	public Game(Hero p1, Hero p2) throws FullHandException, CloneNotSupportedException
	{
		firstHero=p1;
		secondHero=p2;
		
		int coin = (int) (Math.random()*2);
		currentHero= coin==0?firstHero:secondHero;
		opponent= currentHero==firstHero?secondHero:firstHero;
		currentHero.setTotalManaCrystals(1);
		currentHero.setCurrentManaCrystals(1);
		opponent.setTotalManaCrystals(1);
		opponent.setCurrentManaCrystals(1);
		currentHero.setListener(this);
		currentHero.setValidator(this);
		
		opponent.setValidator(this);
		opponent.setListener(this);
		
		currentHero.drawCard();
		currentHero.drawCard();
		currentHero.drawCard();
		
		opponent.drawCard();
		opponent.drawCard();
		opponent.drawCard();
		opponent.drawCard();
	}

	public Hero getCurrentHero() {
		return currentHero;
	}

	public Hero getOpponent() {
		return opponent;
	}

	public void setListener(GameListener listener)
	{
		this.listener = listener;
	}
	public void onHeroDeath()
	{
			listener.onGameOver();
	}
	public void damageOpponent(int amount)
	{
		int health = opponent.getCurrentHP();
		opponent.setCurrentHP(health-amount);
	}
	public void validateTurn(Hero user) throws NotYourTurnException
	{
		if(user!=currentHero)
			throw new NotYourTurnException("Not your turn!");
	}
	public void validateAttack(Minion attacker,Minion target) throws CannotAttackException, NotSummonedException, TauntBypassException, InvalidTargetException
	{
		if(attacker.isSleeping() || attacker.isAttacked())
			throw new CannotAttackException("Minion cannot attack this turn!");
		if(attacker.getAttack()==0)
			throw new CannotAttackException("Minion has zero attack points");
		if(currentHero.getField().contains(attacker) && currentHero.getField().contains(target))
			throw new InvalidTargetException("Cannot attack friendly minion");
		if(currentHero.getField().contains(attacker)==false || opponent.getField().contains(target)==false)
			throw new NotSummonedException("Minion not in field");
		if(hasTauntMinion(opponent.getField())==true && !(target.isTaunt()))
			throw new TauntBypassException("Opponent has taunt minion,kill first");
		
	}
	public void validateAttack(Minion attacker,Hero target) throws CannotAttackException, NotSummonedException, TauntBypassException, InvalidTargetException
	{
		if(attacker.isSleeping() || attacker.isAttacked())
			throw new CannotAttackException("Minion cannot attack this turn!");
		if(attacker.getAttack()==0)
			throw new CannotAttackException("Minion has zero attack points");
		if(currentHero.getField().contains(attacker)==false)
			throw new NotSummonedException("Minion not in field");
		if(hasTauntMinion(opponent.getField())==true)
			throw new TauntBypassException("Opponent has taunt minion,kill first");
		if(attacker.getName().equals("Icehowl"))
			throw new InvalidTargetException("Icehowl attacks only minion");
		if(currentHero.getField().contains(attacker) && target.equals(currentHero))
			throw new InvalidTargetException("Cannot attack your own Hero");	
	}
	public void validateManaCost(Card card) throws NotEnoughManaException
	{
		int hero = currentHero.getCurrentManaCrystals();
		int c = card.getManaCost();
		if(c>hero)
			throw new NotEnoughManaException("Not enough mana!");
	}
	public void validatePlayingMinion(Minion minion) throws FullFieldException
	{
		if(currentHero.getField().size()==7)
			throw new FullFieldException("You have no space in your field");
	}
	 public void validateUsingHeroPower(Hero hero) throws NotEnoughManaException, HeroPowerAlreadyUsedException
	 {
		 if(hero.getCurrentManaCrystals()<2)
			 throw new NotEnoughManaException("Not enough mana!");
		 if(hero.isHeroPowerUsed()==true)
			 throw new HeroPowerAlreadyUsedException("Already used power in this turn");
	 }
	public static boolean hasTauntMinion(ArrayList<Minion> field)
	{
		for(int i =0;i<field.size();i++)
		{
			if(field.get(i).isTaunt()==true)
				return true;
		}
		return false;
	}
	public void endTurn() throws FullHandException, CloneNotSupportedException
	{
		Hero temp = currentHero;
		currentHero = opponent;
		opponent = temp;
		int number = currentHero.getTotalManaCrystals();
		currentHero.setCurrentManaCrystals(number+1);
		currentHero.setTotalManaCrystals(number+1);
		currentHero.setHeroPowerUsed(false);
		opponent.setHeroPowerUsed(false);
		for(int i =0;i<currentHero.getField().size();i++)
		{
			currentHero.getField().get(i).setAttacked(false);
			currentHero.getField().get(i).setSleeping(false);
		}
		currentHero.drawCard();
	}
}
