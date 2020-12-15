package com.trabalho.app.clientte;

import com.trabalho.app.bean.FileMenssage;
import com.trabalho.app.bean.FileMenssage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import jdk.jfr.events.FileReadEvent;

/**
 *
 * @author lucas
 */
public class Cliente {
    private Socket socket;
    private ObjectOutputStream outputStream;

    public Cliente() throws IOException {
        this.socket = new Socket("localhost", 5555);
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        
        new Thread(new ListenerSocket(socket)).start();
        
        menu();
    }
    
    private void menu() throws IOException{
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Digite Seu nome:");
        
        String nome = scanner.nextLine();
        
        this.outputStream.writeObject(new FileMenssage(nome));
        
        int option = 0;
        
        while(option != -1){
            System.out.println("1 - Sair | 2 - Enviar");
            option = scanner.nextInt();
            
            if (option == 2) {
                send(nome);
            }else if (option == 1){
                System.exit(0);
            }
        }
    }

    private void send(String nome) throws IOException {
        FileMenssage fileMenssage = new FileMenssage();
        
        JFileChooser fileChooser = new JFileChooser();
        
        int opt = fileChooser.showOpenDialog(null);
        
        if(opt == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            
            this.outputStream.writeObject(new FileMenssage(nome, file));
        }
    }
            
    public class ListenerSocket implements Runnable{
        
        private  ObjectInputStream inputStream;
        
        public ListenerSocket(Socket socket) throws IOException{
            this.inputStream = new ObjectInputStream(socket.getInputStream());        }

        @Override
        public void run() {
             FileMenssage menssage = null;
            
            try {
                while((menssage = (FileMenssage) inputStream.readObject()) != null ){
                    System.out.println("\nVocê Recebeu um arquivo de " + menssage.getCliente());
                    System.out.println("O arquivo é " + menssage.getFile().getName());
                    
//                    imprime(menssage);

                    salvar(menssage);
                    
                    System.out.println("1 - Sair | 2 - Enviar");
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  

        private void imprime(FileMenssage menssage) throws IOException {
            try {
                FileReader fileReader = new FileReader(menssage.getFile());
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String linha;
                
                while((linha = bufferedReader.readLine()) != null){
                    System.out.println(linha);
                }
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void salvar(FileMenssage menssage) {
            try {
                FileInputStream fileInputStream = new FileInputStream(menssage.getFile());
                FileOutputStream fileOutputStream = new FileOutputStream("C:\\z\\" + menssage.getFile().getName());
                
                FileChannel fin = fileInputStream.getChannel();
                FileChannel fout = fileOutputStream.getChannel();
                
                long size = fin.size();
                
                fin.transferTo(0, size, fout);
                        
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            new Cliente();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
