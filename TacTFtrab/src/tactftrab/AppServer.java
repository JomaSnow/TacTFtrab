/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactftrab;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.InputMismatchException;

/**
 *
 * @author joma
 */
public class AppServer extends DefaultSingleRecoverable {

    HashMap<Integer, Personagem> database = new HashMap<Integer, Personagem>();

    /*
        Tarefas do servidor:
    
        1. Armazenar a lista de personagens
        2. Adicionar ou remover personagens dessa lista
        3. Atualizar dados dos personagens
        4. retornar informações de um personagem
        5. Calcular vencedor de um dado combate e atualizar infos dos envolvidos
    
     */
    public AppServer(int id) {

        initDB(database);
        new ServiceReplica(id, this, this);
    }

    @Override
    public byte[] appExecuteOrdered(byte[] bytes, MessageContext mc) {

        int requestId;
        byte[] requestIdBytes = new byte[4];
        byte[] request = new byte[bytes.length - 4];

        System.arraycopy(bytes, 0, requestIdBytes, 0, requestIdBytes.length);
        System.arraycopy(bytes, requestIdBytes.length, request, 0, request.length);

        requestId = ByteBuffer.wrap(requestIdBytes).getInt();

        switch (requestId) {
            case 1: {
                byte[] id1Byte = new byte[4];
                byte[] id2Byte = new byte[request.length - 4];

                System.arraycopy(request, 0, id1Byte, 0, id1Byte.length);
                System.arraycopy(request, id1Byte.length, id2Byte, 0, id2Byte.length);

                int id1 = ByteBuffer.wrap(id1Byte).getInt();
                int id2 = ByteBuffer.wrap(id2Byte).getInt();

                try {
                    Personagem p1, p2;
                    p1 = getPersonagemById(id1);
                    p2 = getPersonagemById(id2);
                    byte[] reply = personagemToByte(combat(p1, p2));
                    return reply;
                } catch (IOException | InputMismatchException e) {
                    System.out.println("Algo deu errado. Não foi possível atualizar o Personagem.");
                }
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                int personagemId = ByteBuffer.wrap(request).getInt();
                try {
                    Personagem p = getPersonagemById(personagemId);
                    byte[] reply = personagemToByte(p);
                    return reply;
                } catch (NullPointerException | IOException ex) {
                    System.out.println("O id informado não corresponde a um Personagem cadastrado.");
                }
                break;
            }
            case 4: {
                byte[] strengthByte = new byte[4];
                byte[] nameByte = new byte[request.length - 4];

                System.arraycopy(request, 0, strengthByte, 0, strengthByte.length);
                System.arraycopy(request, strengthByte.length, nameByte, 0, nameByte.length);

                int strength = ByteBuffer.wrap(strengthByte).getInt();
                String name = new String(nameByte);

                try {
                    Personagem p = new Personagem(name, strength);
                    addPersonagemToMap(p);
                    byte[] reply = ("\nNovo Personagem adicionado com sucesso.\n\n\tId: " + p.getId() + "\tNome: " + p.getName() + "\tForça: " + p.getStrength()).getBytes();
                    return reply;
                } catch (InputMismatchException e) {
                    System.out.println("Algo deu errado. Não foi possível adicionar o Personagem.");
                }
                break;
            }
            case 51: {
                byte[] personagemIdByte = new byte[4];
                byte[] nameByte = new byte[request.length - 4];

                System.arraycopy(request, 0, personagemIdByte, 0, personagemIdByte.length);
                System.arraycopy(request, personagemIdByte.length, nameByte, 0, nameByte.length);

                int personagemId = ByteBuffer.wrap(personagemIdByte).getInt();
                String name = new String(nameByte);

                try {
                    updateNamePersonagemInMap(personagemId, name);
                    byte[] reply = ("\nNome atualizado").getBytes();
                    return reply;
                } catch (InputMismatchException e) {
                    System.out.println("Algo deu errado. Não foi possível atualizar o Personagem.");
                }
                break;
            }
            case 52: {
                byte[] personagemIdByte = new byte[4];
                byte[] strengthByte = new byte[request.length - 4];

                System.arraycopy(request, 0, personagemIdByte, 0, personagemIdByte.length);
                System.arraycopy(request, personagemIdByte.length, strengthByte, 0, strengthByte.length);

                int personagemId = ByteBuffer.wrap(personagemIdByte).getInt();
                int strength = ByteBuffer.wrap(strengthByte).getInt();

                try {
                    updateStrengthPersonagemInMap(personagemId, strength);
                    byte[] reply = ("\nForça atualizado").getBytes();
                    return reply;
                } catch (InputMismatchException e) {
                    System.out.println("Algo deu errado. Não foi possível atualizar o Personagem.");
                }
                break;
            }
            case 6: {
                int personagemId = ByteBuffer.wrap(request).getInt();
                try {
                    Personagem p = getPersonagemById(personagemId);
                    removePersonagemFromMap(personagemId);
                    byte[] reply = ("\nPersonagem " + p.getName() + ", força " + p.getStrength() + " foi removido com sucesso.").getBytes();
                    return reply;
                } catch (NullPointerException ex) {
                    System.out.println("O id informado não corresponde a um Personagem cadastrado.");
                }
                break;
            }
            default: {
                break;
            }
        }

        return null;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] bytes, MessageContext mc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getSnapshot() {
        return new byte[0];
    }

    @Override
    public void installSnapshot(byte[] bytes) {

    }

    public static byte[] personagemToByte(Personagem p) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        byte[] pByte;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(p);
            oos.flush();
            pByte = bos.toByteArray();
        } finally {
            try {
                bos.close();

            } catch (IOException ex) {
            }
        }
        return pByte;
    }

    public void initDB(HashMap<Integer, Personagem> map) {
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
        Personagem p1 = new Personagem("Lucero", 100, 30, 30);
        map.put(p1.getId(), p1);
        Personagem p2 = new Personagem("Lamar", 78, 6, 15);
        map.put(p2.getId(), p2);
        Personagem p3 = new Personagem("Nalon", 90, 9, 10);
        map.put(p3.getId(), p3);
    }

    public void addPersonagemToMap(Personagem p) {
        this.database.put(p.getId(), p);
    }

    public void removePersonagemFromMap(int id) {
        this.database.remove(id);
    }

    public void updateNamePersonagemInMap(int id, String newName) {
        Personagem p = getPersonagemById(id);
        p.setName(newName);
    }

    public void updateStrengthPersonagemInMap(int id, int newStrength) {
        Personagem p = getPersonagemById(id);
        p.setStrength(newStrength);
    }

    public Personagem getPersonagemById(int id) {
        return this.database.get(id);
    }

    public Personagem combat(Personagem p1, Personagem p2) {
        p1.play();
        p2.play();
        if (p1.getStrength() > p2.getStrength()) {
            p1.win();
            return p1;
        } else {
            p2.win();
            return p2;
        }
    }

    public HashMap<Integer, Personagem> getPersonagemLista() {
        return this.database;
    }

    public static void main(String[] args) {
        // TODO code application logic here

        new AppServer(Integer.parseInt(args[0]));
    }
}
