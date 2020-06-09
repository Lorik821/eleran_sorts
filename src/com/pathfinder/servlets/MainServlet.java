/**
 * Fonctionnalités à ajouter :
 * Ajouter un nouveau grimoire directement depuis l'interface
 * Optimiser le programme en utilisant un bean pour la liste des sorts à chaque requête
 * Requête de listing des sorts par niveau appartenant à au moins un grimoire
 * Requête de listing des sorts par jds de même type (sorts offensif uniquement)
 */

package com.pathfinder.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
	private JDBC jdbc;
	private Controler controler;
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		/**
		 * Paramètres du programme "mainParam"
		 * 1 : afficher sorts du grimoire
		 * 2 : recherche d'un sort par nom
		 * 3 : ajout d'un sort à un grimoire
		 * 4 : mise à jour du lien hypertext d'un sort
		 * 5 : ajout d'un nouveau grimoire
		 */
		jdbc = new JDBC ();
		jdbc.connection();
		
		controler = new Controler (jdbc);
		
		String mainParam = request.getParameter("mainParam");
		
		
		if (mainParam.equals("1")) 
			showSpellbook (request);
		else if (mainParam.equals("2")) 
			researchFor (request);
		else if (mainParam.equals("3"))  // Dans le cas d'un ajout d'un sort à un grimoire
			addSpellToSpellbook (request);
		else if (mainParam.equals("4"))   // Dans le cas d'une mise à jour d'un lien
			addLinkToBADO (request);
		else if (mainParam.equals("5")) // Dans le cas de l'ajout d'un nouveau grimoire
			addSpellbook (request);
		
		
		
		this.getServletContext().getRequestDispatcher( "/WEB-INF/MainPage.jsp" ).forward(request,  response);
		
		try {
			jdbc.closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showSpellbook (HttpServletRequest request)
	{
		String paramSpellbook = request.getParameter("IdSpellbook");
		int idSpellbook = Integer.parseInt(paramSpellbook);
		
		HashMap<Integer, ArrayList<String>> list = new HashMap<Integer, ArrayList<String>>(); 
		
		list = jdbc.spellbookListSpell(idSpellbook);
		int freePages = jdbc.getFreePage(idSpellbook);
		
		//message = liste.get(0);
		String message = "<p>Page(s) libre(s) : " + freePages + "</br></p>";
		
		message += makeList (list);
		
		request.setAttribute("test", message);
	}
	
	public void researchFor (HttpServletRequest request)
	{
		String nameParam = request.getParameter("spellName");
		
		HashMap<Integer, ArrayList<String>> list = new HashMap<Integer, ArrayList<String>>();
		
		list = jdbc.searchFor(nameParam);
		
		String message = makeList (list);
		
		request.setAttribute("test", message);
	}
	
	public void addSpellToSpellbook (HttpServletRequest request) 
	{
		String paramSpellbook = request.getParameter("IdSpellbook");
		int idSpellbook = Integer.parseInt(paramSpellbook);
		String paramIdSpell = request.getParameter("idSpell");
		double idSpell = Double.parseDouble(paramIdSpell);
		double spellLevel = jdbc.getSpellLevel (idSpell);
		
		if (controler.freePagesSpellbookOK(idSpellbook, spellLevel)) {
			jdbc.addSpell(idSpellbook, idSpell);
		}
		else
			System.out.println ("Le grimoire ne contient pas assez de pages vierges");
	}
	
	private String makeList (HashMap<Integer, ArrayList<String>> list)
	{		
		String message = "";
		
		message += "<table>";
		for (int i = 0 ; i < list.size() ; i++) {
			message += "<tr>";
			for (int j = 1 ; j < list.get(i).size() ; j++) {
				message += "<td>" + list.get(i).get(j) + "</td>";
			}
			
			message += "<td><form action=\"http://localhost:8080/eleran_sorts/toto\" method=\"get\"><input type=\"hidden\" id=\"mainParam\" name=\"mainParam\" value=\"4\"><input type=\"text\" id=\"linkText\" name=\"linkText\"><button type=\"button\" >Mettre à jour le lien</button><input type=\"hidden\" id=\"idSpell\" name=\"idSpell\" value=\"" + list.get(i).get(0) + "\"></form></td>";
			
			message += "</tr>";
		}
		message += "</table>";
		return message;
	}
	
	public void addLinkToBADO (HttpServletRequest request)
	{
		String textLink = request.getParameter("linkText");		
		String paramIdSpell = request.getParameter("idSpell");
		
		jdbc.addLink(paramIdSpell, textLink);
	}
	
	public void addSpellbook (HttpServletRequest request)
	{
		jdbc.addSpellbook(request.getParameter("nbPages"), request.getParameter("id"));		
	}

}
