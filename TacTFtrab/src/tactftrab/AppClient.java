/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactftrab;

import bftsmart.tom.ServiceProxy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author joma
 */
public class AppClient {

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        char choice;
        String buffer1, buffer2;
        int strength, id1, id2;
        HashMap<Integer, Personagem> currentPersonagens;
        Personagem p;
        boolean loop = true;
//        ServiceProxy proxy = new ServiceProxy(1001);

        //enviar requisição EM BYTES 
        /*oq o cliente pode fazer nesse app:
        
            1. criar personagem (nome, força);
            2. solicitar lista de personagens (retorna array de perso);
            3. pedir pra ver informações de um personagem (wins, jogos, win ratio);
            4. realizar combate (escolhe dois personagens, o servidor retorna quem ganhou e atualiza os dados dos envolvidos);
            5. deletar personagem (passa id);
            6. editar personagem (passa id e escolhe o campo pra alterar);
        
         */
        // Inicia interface usuário
        while (loop) {
            System.out.print("\t\t*** Rinha de Personagem ***\n\nEsta aplicação coloca dois personagens da sua escolha (e criação?) para lutarem e medirem força!\nCompleto com estatísticas!\n\nEscolha uma das opções abaixo:\n\n");
            System.out.println("\t1. Começar nova luta!");
            System.out.println("\t2. Exibir Lista de Personagens");
            System.out.println("\t3. Exibir dados sobre um Personagem");
            System.out.println("\t4. Criar um Personagem");
            System.out.println("\t5. Editar um Personagem");
            System.out.println("\t6. Remover um Personagem\n");
            System.out.println("\t0. Encerrar programa");
            choice = (char) System.in.read();

            switch (choice) {
                case '1': {
                    System.out.println("luta");
                    break;
                }
                case '2': {
                    System.out.println("lista");
                    // REQUEST lista
                    break;
                }
                case '3': {
                    System.out.println("dados");
                    break;
                }
                case '4': {
                    System.out.println("criar");
                    break;
                }
                case '5': {
                    System.out.println("editar");
                    break;
                }
                case '6': {
                    System.out.println("remover");
                    break;
                }
                case '0': {
                    loop = false;
                    System.out.println("Encerrando o programa");
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }
}
