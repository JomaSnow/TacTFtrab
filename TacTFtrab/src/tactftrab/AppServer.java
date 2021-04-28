/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactftrab;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author joma
 */
public class AppServer extends DefaultSingleRecoverable {

    /*
        Tarefas do servidor:
    
        1. Armazenar a lista de personagens
        2. Adicionar ou remover personagens dessa lista
        3. Atualizar dados dos personagens
        4. retornar informações de um personagem
        5. Calcular vencedor de um dado combate e atualizar infos dos envolvidos
    
     */
    public AppServer(int id) {

        new ServiceReplica(id, this, this);

        HashMap<Integer, Object> database = new HashMap<Integer, Object>();
        database = initDB();
    }

    public HashMap initDB() {
//        String line = null;

//        HashMap<Integer, Object> map = new HashMap<Integer, Object>();
//
//        BufferedReader br = new BufferedReader(new FileReader("path.CSV"));
//
//        while ((line = br.readLine()) != null) {
//
//            String[] arr = line.split(";");
//            int strength = Integer.parseInt(arr[2]);
//            int wins = Integer.parseInt(arr[3]);
//            int matches_played = Integer.parseInt(arr[4]);
//            Personagem p = new Personagem(arr[1], strength, wins, matches_played);
//            map.put(p.getId(), p);
//        }
        HashMap<Integer, Object> map = new HashMap<Integer, Object>();

        Personagem p1 = new Personagem("Lucero", 100, 30, 30);
        Personagem p2 = new Personagem("Lamar", 78, 6, 15);
        Personagem p3 = new Personagem("Nalon", 90, 9, 10);
        map.put(p1.getId(), p1);
        map.put(p2.getId(), p2);
        map.put(p3.getId(), p3);
        return map;
    }

    @Override
    public byte[] appExecuteOrdered(byte[] bytes, MessageContext mc) {
        return null;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] bytes, MessageContext mc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getSnapshot() {
        return null;
    }

    @Override
    public void installSnapshot(byte[] bytes) {

    }

    public static void main(String[] args) {
        // TODO code application logic here
    }
}
