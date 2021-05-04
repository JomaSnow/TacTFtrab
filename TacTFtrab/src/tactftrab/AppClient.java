/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactftrab;

import bftsmart.tom.ServiceProxy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author joma
 */
public class AppClient {

    public static byte[] concatenateArrays(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        byte[] c = (byte[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static byte[] intToByte(int num) {
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    public static byte[] encodeByteArray(byte[] originalArray, int requestId) {
        // inteiro ocupa 4 bytes. Conversão:
        byte[] byteRequest = ByteBuffer.allocate(4).putInt(requestId).array();
        return concatenateArrays(byteRequest, originalArray);
    }

    public static Personagem personagemFromByte(byte[] pByte) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(pByte);
        ObjectInput in = null;
        Personagem p;

        try {
            in = new ObjectInputStream(bis);
            p = (Personagem) in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }

        return p;
    }
    
    public static HashMap<Integer, Personagem> databaseFromByte(byte[] pByte) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(pByte);
        ObjectInput in = null;
        HashMap<Integer, Personagem> db;

        try {
            in = new ObjectInputStream(bis);
            db = (HashMap<Integer, Personagem>) in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }

        return db;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scan = new Scanner(System.in);
        String buffer1;
        int choice, strength, id1, id2;
        HashMap<Integer, Personagem> currentPersonagens;
        Personagem p, q;
        boolean loop = true;
        ServiceProxy proxy = new ServiceProxy(1001);

        // Inicia interface usuário
        System.out.print("\n\n\n\t\t*****************************\n\t\t* TFF Text Fiction Fighting *\n\t\t*****************************\n\nEsta aplicação coloca dois personagens da sua escolha (e criação?) para lutarem e medirem força!\nCompleto com estatísticas!\n");
        while (loop) {
            System.out.println("\nEscolha uma das opções abaixo:\n");
            System.out.println("\t1. Começar nova luta!");
            System.out.println("\t2. Exibir Lista de Personagens");
            System.out.println("\t3. Exibir dados sobre um Personagem");
            System.out.println("\t4. Criar um Personagem");
            System.out.println("\t5. Editar um Personagem");
            System.out.println("\t6. Remover um Personagem\n");
            System.out.println("\t0. Encerrar programa");

            choice = scan.nextInt();

            switch (choice) {
                case 1: {
                        byte[] content;
                        byte[] request;
                        byte[] reply;
                    try{
                        System.out.println("\n\n\n\n\n\n\n\n\t\t##################\n\t\tUm novo duelo se inicia!");
                        System.out.print("Informe o id do primeiro Personagem: ");
                        id1 = scan.nextInt();
                        content = intToByte(id1);
                        request = encodeByteArray(content, 3);
                        reply = proxy.invokeOrdered(request);
                        p = personagemFromByte(reply);
                        
                        System.out.print("Informe o id do segundo Personagem: ");
                        id2 = scan.nextInt();
                        content = intToByte(id2);
                        request = encodeByteArray(content, 3);
                        reply = proxy.invokeOrdered(request);
                        q = personagemFromByte(reply);
                        
                        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                        dueloPrint(p, q);
                        
                        content = concatenateArrays(intToByte(id1), intToByte(id2));
                        request = encodeByteArray(content, 1);
                        reply = proxy.invokeOrdered(request);
                        p = personagemFromByte(reply);
                        System.out.println("\t"+p.getName()+" Ganhou este duelo!");
                        System.out.println("\tNovos Resultados:");
                        System.out.println("\nNome: " + p.getName() + "\tForça: " + p.getStrength() + "\tBatalhas Disputadas: " + p.getMatchesPlayed() + "\tVitórias: " + p.getWins() + "\tTaxa de Vitória: " + p.getWinRatio() + "%");
                    } catch (InputMismatchException | NullPointerException | IOException | ClassNotFoundException e) {
                        // E se não tiver o id?
                        System.out.println("\nPersonagem não encontrado.");
                    }

                    break;
                }
                case 2: {
                    try{
                        System.out.println("\n\t\tPersonagens cadastrados\n");
                        byte[] request = intToByte(2);
                        byte[] reply = proxy.invokeOrdered(request);
                        currentPersonagens = databaseFromByte(reply);
                        
                        for(Map.Entry<Integer, Personagem> entry : currentPersonagens.entrySet()){
                            p = entry.getValue();
                            System.out.println("\tId: "+entry.getKey()+"\tNome: "+p.getName()+"\tForça: "+p.getStrength());
                        }
                    }catch(NullPointerException | ClassNotFoundException e){
                        System.out.println("\nNão foi possível resgatar a lista de personagens.");
                    }

                    break;
                }
                case 3: {
                    try {
                        System.out.print("Informe o id do Personagem para exibir os dados: ");
                        id1 = scan.nextInt();
                        byte[] content = intToByte(id1);
                        byte[] request = encodeByteArray(content, 3);
                        byte[] reply = proxy.invokeOrdered(request);
                        p = personagemFromByte(reply);
                        System.out.println("\nNome: " + p.getName() + "\tForça: " + p.getStrength() + "\tBatalhas Disputadas: " + p.getMatchesPlayed() + "\tVitórias: " + p.getWins() + "\tTaxa de Vitória: " + p.getWinRatio() + "%");
                    } catch (InputMismatchException | NullPointerException | IOException | ClassNotFoundException e) {
                        // E se não tiver o id?
                        System.out.println("\nPersonagem não encontrado.");
                    }

                    break;
                }
                case 4: {
                    try {
                        scan.nextLine(); // consome newLine de cima (just JAVA things)  
                        System.out.print("Informe o nome do novo Personagem: ");
                        buffer1 = scan.nextLine();
                        System.out.print("Informe a força do novo Personagem: ");
                        strength = scan.nextInt();
                        byte[] content = concatenateArrays(intToByte(strength), buffer1.getBytes());
                        byte[] request = encodeByteArray(content, 4);
                        byte[] reply = proxy.invokeOrdered(request);
                        String replyString = new String(reply);
                        System.out.println(replyString);
                    } catch (InputMismatchException e) {
                        System.out.println("\nNão foi possível adicionar o Personagem.");
                    }

                    break;
                }
                case 5: {
                    byte[] content;
                    byte[] request;
                    byte[] reply;
                    boolean editLoop = true;

                    System.out.print("Informe o Id do Personagem que deseja editar: ");
                    id1 = scan.nextInt();
                    while (editLoop) {
                        try {
                            content = intToByte(id1);
                            request = encodeByteArray(content, 3);
                            reply = proxy.invokeOrdered(request);
                            p = personagemFromByte(reply);
                            System.out.println("\n\tId: " + p.getId() + "\tNome: " + p.getName() + "\tForça: " + p.getStrength());
                            System.out.println("\n\t1. Editar nome\n\t2. Editar força\n\n\t0. Sair");
                            choice = scan.nextInt();
                        } catch (InputMismatchException | NullPointerException | IOException | ClassNotFoundException e) {
                            // E se não tiver o id?
                            System.out.println("\nPersonagem não encontrado.");
                            choice = 0;
                        }
                        switch (choice) {
                            case 1: {
                                try {
                                    System.out.print("\nNovo Nome:  ");
                                    scan.nextLine(); // consome newLine de cima.
                                    buffer1 = scan.nextLine();

                                    content = concatenateArrays(intToByte(id1), buffer1.getBytes());
                                    request = encodeByteArray(content, 51);
                                    reply = proxy.invokeOrdered(request);
                                    String replyString = new String(reply);
                                    System.out.println(replyString);
                                } catch (Exception e) {
                                    System.out.println("\nOcorreu um erro.");
                                }
                                break;
                            }
                            case 2: {
                                try {
                                    System.out.print("\nNova força:  ");
                                    strength = scan.nextInt();

                                    content = concatenateArrays(intToByte(id1), intToByte(strength));
                                    request = encodeByteArray(content, 52);
                                    reply = proxy.invokeOrdered(request);
                                    String replyString = new String(reply);
                                    System.out.println(replyString);
                                } catch (InputMismatchException e) {
                                    System.out.println("\nEntrada inválida.");
                                    choice = 3;
                                }
                                break;
                            }
                            default: {
                                editLoop = false;
                                break;
                            }
                        }
                    }
                    break;
                }
                case 6: {
                    try {
                        System.out.print("Informe o id do Personagem para remover: ");
                        id1 = scan.nextInt();
                        byte[] content = intToByte(id1);
                        byte[] request = encodeByteArray(content, 6);
                        byte[] reply = proxy.invokeOrdered(request);
                        String replyString = new String(reply);
                        System.out.println(replyString);
                    } catch (InputMismatchException | NullPointerException e) {
                        // E se não tiver o id?
                        System.out.println("\nPersonagem não encontrado.\nNenhum Personagem removido.");
                    }

                    break;
                }
                case 0: {
                    loop = false;
                    System.out.println("Encerrando o programa");
                    proxy.close();
                    break;
                }
                default: {
                    System.out.println("\nSeleciona uma opção da lista.");
                    break;
                }
            }
        }
        System.exit(0);
    }
    
    public static void dueloPrint(Personagem p, Personagem q) throws InterruptedException{
        System.out.println("\tUm novo duelo está prestes a iniciar!!");
        System.out.println("\tDe um lado: "+p.getName()+", duelista apresentando "+p.getWinRatio()+"% de taxa de vitória e "+p.getMatchesPlayed()+" partidas disputadas!");
        System.out.println("\n\tE do outro lado, o desafiante de hoje: "+q.getName()+"! Taxa de vitória: "+q.getWinRatio()+"% ("+q.getMatchesPlayed()+" partidas)");
        Thread.sleep(1000);
        System.out.println("\n\n\tO duelo começa!\n");
        Thread.sleep(500);
        if(q.getWinRatio()>p.getWinRatio()){
            System.out.println("\t"+q.getName()+", convencido da vitória, avança com tremenda velocidade em direção ao oponente!");
            Thread.sleep(300);
            if(p.getMatchesPlayed()>q.getMatchesPlayed()){
                System.out.println("\tJá um duelista experiente, "+p.getName()+" antecipa o movimento do oponente e evita o ataque!");
                Thread.sleep(300);
                if(p.getStrength()>q.getStrength()){
                    //p ganha
                    if(p.getStrength()-q.getStrength()>80){
                            System.out.println("\t"+q.getName()+" se mostra vulnerável por um breve período, e "+p.getName()+" aproveita essa abertura e encaixa um golpe preciso!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" sente bastante o golpe! O desafiante tenta se manter de pé, com muito esforço.");
                            Thread.sleep(300);
                            System.out.println("\tSem dar chances de seu oponente se recuperar, "+p.getName()+" finaliza a luta com outro acerto definitivo!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" é nocauteado!");
                            Thread.sleep(300);
                    }else{
                        if(p.getStrength()-q.getStrength()>50){
                            System.out.println("\t"+q.getName()+" se mostra vulnerável por um breve período, e "+p.getName()+" aproveita essa abertura e encaixa um golpe preciso!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" sente bastante o acerto, mas se prepara para devolver o golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\tNo entanto o duelista se recupera rapidamente, e antes que "+q.getName()+" perceba, "+p.getName()+" acerta um golpe extraordinário!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" tenta, mas não consegue se levantar!");
                            Thread.sleep(300);
                        }else{
                            System.out.println("\t"+q.getName()+" se mostra vulnerável por um breve período, e "+p.getName()+" aproveita essa abertura e tenta um golpe!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" defende o golpe com esforço e não perde tempo em devolver com um golpe próprio!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" parece sentir o golpe recebido, mas se recupera rapidamente!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista contra ataca! Com impressionante força, "+p.getName()+" tenta mais um golpe em seu oponente!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" consegue se defender novamente, mas dessa vez o ataque o deixa mais exposto!");
                            Thread.sleep(300);
                            System.out.println("\tSem perder tempo, "+p.getName()+" junta o resto de suas forças e acerta um último golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO desafiante vai ao chão! Sem forças para continuar, "+q.getName()+" perde este duelo!");
                            Thread.sleep(300);
                            
                        }
                    }
                }else{
                    //q ganha
                    if(q.getStrength()-p.getStrength()>80){
                            System.out.println("\tImpressionante!! Com incrível agilidade e força "+q.getName()+" consegue realizar um segundo golpe!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" não consegue defender o golpe! O duelista sente bastante o golpe, e tenta com esforço se manter de pé!");
                            Thread.sleep(300);
                            System.out.println("\tSem dar chances de seu oponente se recuperar, "+q.getName()+" finaliza a luta com outro acerto definitivo!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" vai ao chão, nocauteado!");
                            Thread.sleep(300);
                    }else{
                        if(q.getStrength()-p.getStrength()>50){
                            System.out.println("\t"+q.getName()+" se recupera do golpe errado e rapidamente tenta outro ataque! Desta vez "+p.getName()+" não consegue desviar!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" sente bastante o acerto, mas se prepara para devolver o golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\tNo entanto o desafiante se recupera rapidamente, e antes que "+p.getName()+" perceba, "+q.getName()+" acerta um golpe extraordinário!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" tenta, mas não consegue se levantar!");
                            Thread.sleep(300);
                        }else{
                            System.out.println("\t"+q.getName()+" se recupera do golpe errado e rapidamente tenta outro ataque!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" defende o golpe com esforço e não perde tempo em devolver com um golpe próprio!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" parece sentir o golpe recebido, mas se recupera rapidamente!");
                            Thread.sleep(300);
                            System.out.println("\tO desafiante contra ataca! Com impressionante força, "+q.getName()+" tenta mais um golpe em seu oponente!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" consegue se defender novamente, mas dessa vez o ataque o deixa mais exposto!");
                            Thread.sleep(300);
                            System.out.println("\tSem perder tempo, "+q.getName()+" junta o resto de suas forças e acerta um último golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista vai ao chão!");
                            Thread.sleep(300);
                            System.out.println("\tCom muito esforço, "+p.getName()+" se levanta!! Mas já muito fraco, não consegue se manter de pé, e cai novamente, selando sua derrota!");
                            Thread.sleep(300);
                        }
                    }
                }
            }else{
                System.out.println("\tO avanço rápido parece pegar "+p.getName()+" de surpresa, forçando uma defesa espetacular de última hora!");
                Thread.sleep(300);
                if(p.getStrength()>q.getStrength()){
                    //p ganha
                    if(p.getStrength()-q.getStrength()>80){
                            System.out.println("\t"+q.getName()+" parece não acreditar que seu ataque foi bloqueado, perdendo a concentração por um instante! "+p.getName()+" aproveita essa abertura e encaixa um golpe preciso!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" sente bastante o golpe! O desafiante tenta se manter de pé, com muito esforço.");
                            Thread.sleep(300);
                            System.out.println("\tSem dar chances de seu oponente se recuperar, "+p.getName()+" finaliza a luta com outro acerto definitivo!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" é nocauteado!");
                            Thread.sleep(300);
                    }else{
                        if(p.getStrength()-q.getStrength()>50){
                            System.out.println("\t"+q.getName()+" parece não acreditar que seu ataque foi bloqueado, perdendo a concentração por um instante! "+p.getName()+" aproveita essa abertura e encaixa um golpe preciso!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" sente bastante o acerto, mas se prepara para devolver o golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\tNo entanto o duelista se recupera rapidamente, e antes que "+q.getName()+" perceba, "+p.getName()+" acerta um golpe extraordinário!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" tenta, mas não consegue se levantar!");
                            Thread.sleep(300);
                        }else{
                            System.out.println("\t"+q.getName()+" parece não acreditar que seu ataque foi bloqueado, perdendo a concentração por um instante! "+p.getName()+" aproveita essa abertura e tenta um golpe!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" defende o golpe com esforço e não perde tempo em devolver com um golpe próprio!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" parece sentir o golpe recebido, mas se recupera rapidamente!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista contra ataca! Com impressionante força, "+p.getName()+" tenta mais um golpe em seu oponente!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" consegue se defender novamente, mas dessa vez o ataque o deixa mais exposto!");
                            Thread.sleep(300);
                            System.out.println("\tSem perder tempo, "+p.getName()+" junta o resto de suas forças e acerta um último golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO desafiante vai ao chão! Sem forças para continuar, "+q.getName()+" perde este duelo!");
                            Thread.sleep(300);
                            
                        }
                    }
                }else{
                    //q ganha
                    if(q.getStrength()-p.getStrength()>80){
                            System.out.println("\tImpressionante!! Com incrível força "+q.getName()+" consegue realizar um segundo golpe, quebrando a defesa de "+p.getName()+"!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista sente bastante o golpe, e tenta com esforço se manter de pé!");
                            Thread.sleep(300);
                            System.out.println("\tSem dar chances de seu oponente se recuperar, "+q.getName()+" finaliza a luta com outro acerto definitivo!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" vai ao chão, nocauteado!");
                            Thread.sleep(300);
                    }else{
                        if(q.getStrength()-p.getStrength()>50){
                            System.out.println("\t"+q.getName()+" rapidamente tenta outro ataque! Desta vez "+p.getName()+" não consegue defender!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" sente bastante o acerto, mas se prepara para devolver o golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\tNo entanto o desafiante se recupera rapidamente, e antes que "+p.getName()+" perceba, "+q.getName()+" acerta um golpe extraordinário!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" tenta, mas não consegue se levantar!");
                            Thread.sleep(300);
                        }else{
                            System.out.println("\t"+q.getName()+" rapidamente tenta outro ataque!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" defende o golpe com esforço e não perde tempo em devolver com um golpe próprio!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" parece sentir o golpe recebido, mas se recupera rapidamente!");
                            Thread.sleep(300);
                            System.out.println("\tO desafiante contra ataca! Com impressionante força, "+q.getName()+" tenta mais um golpe em seu oponente!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" consegue se defender novamente, mas dessa vez o ataque o deixa mais exposto!");
                            Thread.sleep(300);
                            System.out.println("\tSem perder tempo, "+q.getName()+" junta o resto de suas forças e acerta um último golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista vai ao chão!");
                            Thread.sleep(300);
                            System.out.println("\tCom muito esforço, "+p.getName()+" se levanta!! Mas já muito fraco, não consegue se manter de pé, e cai novamente, selando sua derrota!");
                            Thread.sleep(300);
                        }
                    }
                }
            }
        }else{
            System.out.println("\tOs duelistas se encaram e ameaçam golpes!");
            Thread.sleep(300);
            System.out.println("\t"+q.getName()+" toma a iniciativa e avança com tremenda velocidade em direção ao oponente!");
            Thread.sleep(300);
            if(p.getMatchesPlayed()>q.getMatchesPlayed()){
                System.out.println("\tJá um duelista experiente, "+p.getName()+" antecipa o movimento do oponente e evita o ataque!");
                Thread.sleep(300);
                if(p.getStrength()>q.getStrength()){
                    //p ganha
                    if(p.getStrength()-q.getStrength()>80){
                            System.out.println("\t"+q.getName()+" se mostra vulnerável por um breve período, e "+p.getName()+" aproveita essa abertura e encaixa um golpe preciso!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" sente bastante o golpe! O desafiante tenta se manter de pé, com muito esforço.");
                            Thread.sleep(300);
                            System.out.println("\tSem dar chances de seu oponente se recuperar, "+p.getName()+" finaliza a luta com outro acerto definitivo!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" é nocauteado!");
                            Thread.sleep(300);
                    }else{
                        if(p.getStrength()-q.getStrength()>50){
                            System.out.println("\t"+q.getName()+" se mostra vulnerável por um breve período, e "+p.getName()+" aproveita essa abertura e encaixa um golpe preciso!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" sente bastante o acerto, mas se prepara para devolver o golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\tNo entanto o duelista se recupera rapidamente, e antes que "+q.getName()+" perceba, "+p.getName()+" acerta um golpe extraordinário!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" tenta, mas não consegue se levantar!");
                            Thread.sleep(300);
                        }else{
                            System.out.println("\t"+q.getName()+" se mostra vulnerável por um breve período, e "+p.getName()+" aproveita essa abertura e tenta um golpe!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" defende o golpe com esforço e não perde tempo em devolver com um golpe próprio!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" parece sentir o golpe recebido, mas se recupera rapidamente!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista contra ataca! Com impressionante força, "+p.getName()+" tenta mais um golpe em seu oponente!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" consegue se defender novamente, mas dessa vez o ataque o deixa mais exposto!");
                            Thread.sleep(300);
                            System.out.println("\tSem perder tempo, "+p.getName()+" junta o resto de suas forças e acerta um último golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO desafiante vai ao chão! Sem forças para continuar, "+q.getName()+" perde este duelo!");
                            Thread.sleep(300);
                            
                        }
                    }
                }else{
                    //q ganha
                    if(q.getStrength()-p.getStrength()>80){
                            System.out.println("\tImpressionante!! Com incrível agilidade e força "+q.getName()+" consegue realizar um segundo golpe!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" não consegue defender o golpe! O duelista sente bastante o golpe, e tenta com esforço se manter de pé!");
                            Thread.sleep(300);
                            System.out.println("\tSem dar chances de seu oponente se recuperar, "+q.getName()+" finaliza a luta com outro acerto definitivo!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" vai ao chão, nocauteado!");
                            Thread.sleep(300);
                    }else{
                        if(q.getStrength()-p.getStrength()>50){
                            System.out.println("\t"+q.getName()+" se recupera do golpe errado e rapidamente tenta outro ataque! Desta vez "+p.getName()+" não consegue desviar!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" sente bastante o acerto, mas se prepara para devolver o golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\tNo entanto o desafiante se recupera rapidamente, e antes que "+p.getName()+" perceba, "+q.getName()+" acerta um golpe extraordinário!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" tenta, mas não consegue se levantar!");
                            Thread.sleep(300);
                        }else{
                            System.out.println("\t"+q.getName()+" se recupera do golpe errado e rapidamente tenta outro ataque!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" defende o golpe com esforço e não perde tempo em devolver com um golpe próprio!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" parece sentir o golpe recebido, mas se recupera rapidamente!");
                            Thread.sleep(300);
                            System.out.println("\tO desafiante contra ataca! Com impressionante força, "+q.getName()+" tenta mais um golpe em seu oponente!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" consegue se defender novamente, mas dessa vez o ataque o deixa mais exposto!");
                            Thread.sleep(300);
                            System.out.println("\tSem perder tempo, "+q.getName()+" junta o resto de suas forças e acerta um último golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista vai ao chão!");
                            Thread.sleep(300);
                            System.out.println("\tCom muito esforço, "+p.getName()+" se levanta!! Mas já muito fraco, não consegue se manter de pé, e cai novamente, selando sua derrota!");
                            Thread.sleep(300);
                        }
                    }
                }
            }else{
                System.out.println("\tO avanço rápido parece pegar "+p.getName()+" de surpresa, forçando uma defesa espetacular de última hora!");
                Thread.sleep(300);
                if(p.getStrength()>q.getStrength()){
                    //p ganha
                    if(p.getStrength()-q.getStrength()>80){
                            System.out.println("\t"+q.getName()+" parece não acreditar que seu ataque foi bloqueado, perdendo a concentração por um instante! "+p.getName()+" aproveita essa abertura e encaixa um golpe preciso!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" sente bastante o golpe! O desafiante tenta se manter de pé, com muito esforço.");
                            Thread.sleep(300);
                            System.out.println("\tSem dar chances de seu oponente se recuperar, "+p.getName()+" finaliza a luta com outro acerto definitivo!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" é nocauteado!");
                            Thread.sleep(300);
                    }else{
                        if(p.getStrength()-q.getStrength()>50){
                            System.out.println("\t"+q.getName()+" parece não acreditar que seu ataque foi bloqueado, perdendo a concentração por um instante! "+p.getName()+" aproveita essa abertura e encaixa um golpe preciso!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" sente bastante o acerto, mas se prepara para devolver o golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\tNo entanto o duelista se recupera rapidamente, e antes que "+q.getName()+" perceba, "+p.getName()+" acerta um golpe extraordinário!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" tenta, mas não consegue se levantar!");
                            Thread.sleep(300);
                        }else{
                            System.out.println("\t"+q.getName()+" parece não acreditar que seu ataque foi bloqueado, perdendo a concentração por um instante! "+p.getName()+" aproveita essa abertura e tenta um golpe!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" defende o golpe com esforço e não perde tempo em devolver com um golpe próprio!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" parece sentir o golpe recebido, mas se recupera rapidamente!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista contra ataca! Com impressionante força, "+p.getName()+" tenta mais um golpe em seu oponente!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" consegue se defender novamente, mas dessa vez o ataque o deixa mais exposto!");
                            Thread.sleep(300);
                            System.out.println("\tSem perder tempo, "+p.getName()+" junta o resto de suas forças e acerta um último golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO desafiante vai ao chão! Sem forças para continuar, "+q.getName()+" perde este duelo!");
                            Thread.sleep(300);
                            
                        }
                    }
                }else{
                    //q ganha
                    if(q.getStrength()-p.getStrength()>80){
                            System.out.println("\tImpressionante!! Com incrível força "+q.getName()+" consegue realizar um segundo golpe, quebrando a defesa de "+p.getName()+"!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista sente bastante o golpe, e tenta com esforço se manter de pé!");
                            Thread.sleep(300);
                            System.out.println("\tSem dar chances de seu oponente se recuperar, "+q.getName()+" finaliza a luta com outro acerto definitivo!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" vai ao chão, nocauteado!");
                            Thread.sleep(300);
                    }else{
                        if(q.getStrength()-p.getStrength()>50){
                            System.out.println("\t"+q.getName()+" rapidamente tenta outro ataque! Desta vez "+p.getName()+" não consegue defender!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" sente bastante o acerto, mas se prepara para devolver o golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\tNo entanto o desafiante se recupera rapidamente, e antes que "+p.getName()+" perceba, "+q.getName()+" acerta um golpe extraordinário!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" tenta, mas não consegue se levantar!");
                            Thread.sleep(300);
                        }else{
                            System.out.println("\t"+q.getName()+" rapidamente tenta outro ataque!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" defende o golpe com esforço e não perde tempo em devolver com um golpe próprio!");
                            Thread.sleep(300);
                            System.out.println("\tO golpe encaixa!");
                            Thread.sleep(300);
                            System.out.println("\t"+q.getName()+" parece sentir o golpe recebido, mas se recupera rapidamente!");
                            Thread.sleep(300);
                            System.out.println("\tO desafiante contra ataca! Com impressionante força, "+q.getName()+" tenta mais um golpe em seu oponente!");
                            Thread.sleep(300);
                            System.out.println("\t"+p.getName()+" consegue se defender novamente, mas dessa vez o ataque o deixa mais exposto!");
                            Thread.sleep(300);
                            System.out.println("\tSem perder tempo, "+q.getName()+" junta o resto de suas forças e acerta um último golpe!");
                            Thread.sleep(300);
                            System.out.println("\tO duelista vai ao chão!");
                            Thread.sleep(300);
                            System.out.println("\tCom muito esforço, "+p.getName()+" se levanta!! Mas já muito fraco, não consegue se manter de pé, e cai novamente, selando sua derrota!");
                            Thread.sleep(300);
                        }
                    }
                }
            }
        }    
    }
}
