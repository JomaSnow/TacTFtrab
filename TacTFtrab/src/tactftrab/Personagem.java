/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactftrab;

import java.io.Serializable;

/**
 *
 * @author joma
 */
public class Personagem implements Serializable{
    private String name;
    private int wins = 0;
    private int matches_played = 0;
    private int strength;
    private static int id = 0;
    
    // Construtor padrão pro cliente
    public Personagem(String name, int strength){
        setName(name);
        setStrength(strength);
        this.id++;
    }
    
    // Construtor pra popular o BD
    public Personagem(String name, int strength, int wins, int matches_played){
        setName(name);
        setStrength(strength);
        setWins(wins);
        setMatchesPlayed(matches_played);
        this.id++;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setStrength(int strength){
        this.strength = strength;
    }
    
    public int getStrength(){
        return this.strength;
    }
    
    public void setWins(int wins){
        this.wins = wins;
    }
    
    public int getWins(){
        return this.wins;
    }
    
    public void setMatchesPlayed(int matches_played){
        this.matches_played = matches_played;
    }
    
    public int getMatchesPlayed(){
        return this.matches_played;
    }
    
    // Aumenta em 1 o número de marches_played
    public void play(){
        int current_matches = this.matches_played;
        current_matches++;
        this.matches_played = current_matches;
    }
    
    // Aumenta em 1 o número de wins
    public void win(){
        int current_wins = this.wins;
        current_wins++;
        this.wins = current_wins;
    }
    
    // Retorna o porcentual de vitória deste personagem
    public double getWinRatio(){
        if(this.matches_played<1){
            return 0.0;
        }else{
            double w = this.wins;
            double mp = this.matches_played;
            return (w/mp)*100;
        }
    }
    
    // Retorna o id do personagem
    public int getId(){
        return this.id;
    }
}
