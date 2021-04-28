/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactftrab;

import bftsmart.tom.ServiceProxy;

/**
 *
 * @author joma
 */
public class AppClient {
    
    public static void main(String[] args){
        ServiceProxy proxy = new ServiceProxy(1001);
        
        //enviar requisição EM BYTES 
        
        /*oq o cliente pode fazer nesse app:
        
            1. criar personagem (nome, força);
            2. solicitar lista de personagens (retorna array de perso);
            3. pedir pra ver informações de um personagem (wins, jogos, win ratio);
            4. realizar combate (escolhe dois personagens, o servidor retorna quem ganhou e atualiza os dados dos envolvidos);
            5. deletar personagem (passa id);
            6. editar personagem (passa id e escolhe o campo pra alterar);
        
        */
        
    }
}
