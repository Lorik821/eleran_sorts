package com.pathfinder.servlets;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;



public class JDBC {
	
	private Connection connexion = null;
    
    public void connection ()  {
    	try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/* Connexion à la base de données */
    	String url = "jdbc:mysql://localhost:3306/pathfinder";
    	String utilisateur = "root";
    	String motDePasse = "chronos";
    	try {
    	    connexion = DriverManager.getConnection( url, utilisateur, motDePasse );

    	    /* Ici, nous placerons nos requêtes vers la BDD */
    	    /* ... */

    	} catch ( SQLException e ) {
    		System.out.println ("probleme !");
    		e.printStackTrace();
    	    /* Gérer les éventuelles erreurs ici */
    	} finally {
    	    if ( connexion != null )
				/* Fermeture de la connexion */
				//connexion.close();
				System.out.println ("Connexion ok ! ");
    	}


    }
    
    public HashMap<Integer, ArrayList<String>> spellbookListSpell (int idSpellbook) {
    	
    	ArrayList<String> listAttributeSpell = new ArrayList<String>();
    	
    	HashMap<Integer, ArrayList<String>> listSpell = new HashMap<Integer, ArrayList<String>> ();
    	/* Création de l'objet gérant les requêtes */
    	Statement statement = null;
		try {
			statement = connexion.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/* Exécution d'une requête de lecture */
    	try {
			ResultSet resultat = statement.executeQuery( "SELECT spell_full.name, spell_full.school, spell_full.wiz, spell_full.short_description, spell_full.linktext, spell_full.id from spell_full, ssb where spell_full.id = ssb.id_spell and ssb.id_sb = " + idSpellbook + " order by wiz;" );
			int i = 0;
			while (resultat.next()) {
				String data = StringUtils.substringBetween(resultat.getString("linktext"), "\"", "\"");
				listAttributeSpell = new ArrayList<String>();
				listAttributeSpell.add(resultat.getString("id")); // Ajout de l'id du sort non visible par l'utilisateur
				listAttributeSpell.add("<a href=\"" + data + "\">" + resultat.getString("name") + "</a>");
				listAttributeSpell.add(resultat.getString("school"));
				listAttributeSpell.add(resultat.getString("wiz"));
				listAttributeSpell.add(resultat.getString("short_description"));			
				
				listSpell.put(i, listAttributeSpell);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return listSpell;
    }
    
    public HashMap<Integer, ArrayList<String>> searchFor (String str) {
    	ArrayList<String> listAttributeSpell = new ArrayList<String>();
    	
    	HashMap<Integer, ArrayList<String>> listSpell = new HashMap<Integer, ArrayList<String>> ();
    	/* Création de l'objet gérant les requêtes */
    	Statement statement = null;
		try {
			statement = connexion.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/* Exécution d'une requête de lecture */
    	try {
			ResultSet resultat = statement.executeQuery( "select spell_full.name, spell_full.school, spell_full.wiz, spell_full.short_description, spell_full.linktext, spell_full.id from spell_full where name like '%" + str + "%';" );
			int i = 0;
			while (resultat.next()) {
				String data = StringUtils.substringBetween(resultat.getString("linktext"), "\"", "\"");
				listAttributeSpell = new ArrayList<String>();
				listAttributeSpell.add(resultat.getString("id")); // Ajout de l'id du sort non visible par l'utilisateur
				listAttributeSpell.add("<a href=\"" + data + "\">" + resultat.getString("name") + "</a>");
				listAttributeSpell.add(resultat.getString("school"));
				listAttributeSpell.add(resultat.getString("wiz"));
				listAttributeSpell.add(resultat.getString("short_description"));
				listAttributeSpell.add("<form action=\"http://localhost:8080/eleran_sorts/toto\" method=\"get\"><input type=\"hidden\" id=\"mainParam\" name=\"mainParam\" value=\"3\">");
				listAttributeSpell.add("<input type=\"number\" id=\"IdSpellbook\" name=\"IdSpellbook\">");
				listAttributeSpell.add("<button type=\"button\" >Ajouter au grimoire</button><input type=\"hidden\" id=\"idSpell\" name=\"idSpell\" value=" + resultat.getString("id") + "></form>");
				//listAttributeSpell.add("<input type=\"hidden\" id=\"idSpell\" name=\"idSpell\""+resultat.getString("id")+">");
				
				listSpell.put(i, listAttributeSpell);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return listSpell;
    }
    
    public int getFreePage (int idSpellbook) {
    	Statement statement = null;
    	int free_pages = 0;
		try {
			statement = connexion.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/* Exécution d'une requête de lecture */
    	try {
			ResultSet resultat = statement.executeQuery( "SELECT free_pages FROM spellbook WHERE id_sb=" + idSpellbook + ";" );
			resultat.next();
			free_pages = resultat.getInt("free_pages");
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return free_pages;
    }
    
    public int getNBSpellbook () {
    	Statement statement = null;
    	int nbSpellbook = 0;
    	try {
    		statement = connexion.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	try {
			ResultSet resultat = statement.executeQuery( "SELECT COUNT(*) FROM spellbook;" );
			nbSpellbook = resultat.getInt("free_pages");
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return nbSpellbook;
    	
    }
    
    public void addSpell (int idSpellbook, double idSpell) {
    	Statement statement = null;
    	try {
    		statement = connexion.createStatement();
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    	}
    	
    	try {
    		int statut = statement.executeUpdate("INSERT INTO ssb VALUE (" + idSpell + ", "+ idSpellbook + ");");
    		removePage (idSpellbook, (int) getSpellLevel(idSpell), statement);
    		System.out.println("Statut : " + statut);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    private void removePage (int idSpellbook, int nbPage, Statement statement) {
    	try {
    		int free_pages = getFreePage (idSpellbook);
    		int pages = free_pages - nbPage;
    		System.out.println ("Valeur de free-pages : " + free_pages);
    		System.out.println ("Valeur de nbPage : " + nbPage);
    		System.out.println ("Valeur de pages : " + pages);
    		int statut = statement.executeUpdate("UPDATE spellbook SET free_pages = " + pages + " WHERE id_sb = " + idSpellbook + ";");
    		System.out.println ("Statut de removePage : " + statut);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public double getSpellLevel (double idSpell) {
    	Statement statement = null;
    	double res = 0.0;
		try {
			statement = connexion.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/* Exécution d'une requête de lecture */
    	try {
			ResultSet resultat = statement.executeQuery( "SELECT wiz FROM spell_full WHERE id = " + idSpell + ";" );
			resultat.next();
			if (resultat.getString("wiz").equals("null")) {
				System.out.println ("Le sort ne fait pas partie de la liste des sorts de l'arcaniste !");
				return 0.0;
			}
			res = resultat.getDouble("wiz");
    	} catch  (SQLException e) {
    		e.printStackTrace();
    	}
    	return res;
    }
    
    /*
     * Permet de mettre à jour le lien vers un sort de la base de données.
     */
    public int addLink (String idSpell, String link)  {
    	Statement statement = null;
    	int statut = 0;
    	try {
    		statement = connexion.createStatement();
    		ResultSet resultat = statement.executeQuery("SELECT name from spell_full where id = " + idSpell + ";");
    		resultat.next();
    		String spellName = resultat.getString("name");
    		statut = statement.executeUpdate("UPDATE spell_full SET linktext = " + "'HYPERLINK(\"" + link + "\",\"" + spellName + "\")' WHERE id = " + idSpell + ";");
    		return statut;
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return statut;
    }
    
    /*
     * Permet d'ajouter un grimoire à la base de données.
     */
    public int addSpellbook (String nbPages, String idSpellbook) {
    	Statement statement = null;
    	int statut = 0;
    	try {
    		statement = connexion.createStatement();
    		statut = statement.executeUpdate("INSERT INTO spellbook VALUES (" + idSpellbook + "," + nbPages + ");");
    		return statut;
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return statut;
    }
    
    public void closeConnection () throws SQLException {
    	connexion.close();
    }
}