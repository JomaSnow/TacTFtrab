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

    public static byte[] doubleToByte(double num) {
        return ByteBuffer.allocate(8).putDouble(num).array();
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

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        String buffer1;
        int choice, strength, id1, id2;
        HashMap<Integer, Personagem> currentPersonagens;
        Personagem p;
        boolean loop = true;
        ServiceProxy proxy = new ServiceProxy(1001);

        // Inicia interface usuário
        while (loop) {
            System.out.print("\n\n\n\t\t*******************\n\t\t* UFC dos Famosos *\n\t\t*******************\n\nEsta aplicação coloca dois personagens da sua escolha (e criação?) para lutarem e medirem força!\nCompleto com estatísticas!\n\nEscolha uma das opções abaixo:\n\n");
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
                    System.out.println("\n\n\n\n\n\n\n\n##################");
                    System.out.print("id1: ");
                    id1 = scan.nextInt();
                    System.out.print("\nid2: ");
                    id2 = scan.nextInt();
                    byte[] content = concatenateArrays(intToByte(id1), intToByte(id2));
                    byte[] request = encodeByteArray(content, 1);

                    for (byte b : request) {
                        System.out.print(b);
                    }

                    break;
                }
                case 2: {
                    System.out.println("lista");
                    byte[] request = intToByte(2);
                    byte[] reply = proxy.invokeOrdered(request);

                    break;
                }
                case 3: {
                    System.out.print("Informe o id do Personagem para exibir os dados: ");
                    id1 = scan.nextInt();
                    byte[] content = intToByte(id1);
                    byte[] request = encodeByteArray(content, 3);
                    byte[] reply = proxy.invokeOrdered(request);

                    try {
                        p = personagemFromByte(reply);
                        System.out.println("\nNome: " + p.getName() + "\tForça: " + p.getStrength() + "\tBatalhas Disputadas: " + p.getMatchesPlayed() + "\tVitórias: " + p.getWins() + "\tTaxa de Vitória: " + p.getWinRatio() + "%");
                    } catch (NullPointerException | IOException | ClassNotFoundException e) {
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
                    System.out.println("editar");
                    id1 = scan.nextInt();
                    boolean editLoop = true;
                    while (editLoop) {
                        //retornar dados do personagem
                        System.out.println("\t1. Editar nome\n\t2. Editar força\n\n\t0. Sair");
                        choice = scan.nextInt();
                        switch (choice) {
                            case 1: {
                                System.out.print("\nNovo Nome:  ");
                                scan.nextLine(); // consome newLine de cima.
                                buffer1 = scan.nextLine();

                                byte[] content = concatenateArrays(intToByte(id1), buffer1.getBytes());
                                byte[] request = encodeByteArray(content, 51);

                                for (byte b : request) {
                                    System.out.print(b);
                                }

                                break;
                            }
                            case 2: {
                                System.out.print("\nNova força:  ");
                                strength = scan.nextInt();

                                byte[] content = concatenateArrays(intToByte(id1), intToByte(strength));
                                byte[] request = encodeByteArray(content, 52);

                                for (byte b : request) {
                                    System.out.print(b);
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
                    System.out.println("remover");
                    id1 = scan.nextInt();
                    byte[] content = intToByte(id1);
                    byte[] request = encodeByteArray(content, 6);

                    for (byte b : request) {
                        System.out.print(b);
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
                    break;
                }
            }
        }
        System.exit(0);
    }
}
