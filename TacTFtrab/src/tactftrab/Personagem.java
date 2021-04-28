/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactftrab;

/**
 *
 * @author joma
 */
public class Personagem {
    private String name;
    private int wins = 0;
    private int matches_played = 0;
    private int strength;
    private static int personagem_count = 0;
    
    // Construtor padrão pro cliente
    public Personagem(String name, int strength){
        setName(name);
        setStrength(strength);
        this.personagem_count++;
    }
    
    // Construtor pra popular o BD
    public Personagem(String name, int strength, int wins, int matches_played){
        setName(name);
        setStrength(strength);
        setWins(wins);
        setMatchesPlayed(matches_played);
        this.personagem_count++;
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
        if(this.matches_played==0){
            return 0.0;
        }else{
            return this.wins/this.matches_played;
        }
    }
    
    // Retorna a contagem de personagens criados
    public int getPersonagemCount(){
        return this.personagem_count;
    }
}
