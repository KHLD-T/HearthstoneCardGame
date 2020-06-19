package model.cards.spells;

import exceptions.InvalidTargetException;
import model.cards.Rarity;
import model.cards.minions.Minion;

public class ShadowWordDeath extends Spell implements MinionTargetSpell {

	public ShadowWordDeath() {
		super("Shadow Word: Death", 3, Rarity.BASIC);
		
	}

	@Override
	public void performAction(Minion m) throws InvalidTargetException
	{
		if(m.getAttack()>=5)
		{
			m.setCurrentHP(-1);
		}
		else if (m.getAttack()<5)
		{
			throw new InvalidTargetException("Shoud have attack less than 5");
		}
	}
}
