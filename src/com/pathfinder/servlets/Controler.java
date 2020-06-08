package com.pathfinder.servlets;

public class Controler {
	
	private JDBC jdbc;
	
	public Controler (JDBC jdbc)
	{
		this.jdbc = jdbc;
	}
	
	public boolean freePagesSpellbookOK (int idSpellbook, double spellLevel) {
		if (spellLevel < 1.0) // Les sorts prennent au moins une page
			spellLevel = 1.0;
		int freePages = jdbc.getFreePage (idSpellbook);
		if (freePages >= spellLevel) 
			return true;
		return false;
		
	}

}
