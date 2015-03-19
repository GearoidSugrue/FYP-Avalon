package com.example.gearoid.testchatapp.character;

public class Morgana extends EvilCharacter{
	
	@Override
	public String getCharacterName() {
		return ConstantsChara.MORGANA;
	}
	
	@Override
	public boolean isVisableTo(ICharacter character) {
		
		if(character instanceof EvilCharacter && character instanceof Oberon == false){
			return true;
		} else if(character instanceof Percival){
			return true;
		} else if(character instanceof Merlin){
			return true;
		}
		return false;
	}

    @Override
    public String getShortDescription() {
        return "Appears as Merlin";
    }

}
