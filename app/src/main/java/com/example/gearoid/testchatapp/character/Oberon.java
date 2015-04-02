package com.example.gearoid.testchatapp.character;

public class Oberon extends EvilCharacter{
	
	@Override
	public String getCharacterName() {
		return ConstantsChara.OBERON;
	}
	
	@Override
	public boolean isVisibleTo(ICharacter character) {
		
		if(character instanceof Merlin){
			return true;
		}		
		return false;
	}

    @Override
    public String getShortDescription() {
        return "Unknown to Evil";
    }
}
