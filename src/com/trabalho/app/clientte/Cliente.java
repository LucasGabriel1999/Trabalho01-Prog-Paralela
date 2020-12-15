package com.trabalho.app.clientte;

import com.Converter;
import com.trabalho.app.bean.FileMenssage;
import com.trabalho.app.bean.FileMenssage;
import java.awt.image.BufferedImage;
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
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
                    
                    converter(menssage);
                    
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
                FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\lucas\\OneDrive\\Documentos\\NetBeansProjects\\TrabalhoProgParalela\\src" + menssage.getFile().getName());
                
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
        
        /**
         *
         * @param image
         * @throws IOException
         */
        public void writeImage(BufferedImage image) throws IOException{
            Date date = new Date();
            String imagePath = "src/ImagensResultante/imagem-convertida-" + date.toString() + ".png";

             ImageIO.write(image, "PNG", new File(imagePath));
        }

        private void converter(FileMenssage menssage) throws IOException {
            
            String path = "src/" + menssage.getFile().getName();
            System.out.println(path);

            BufferedImage image = ImageIO.read(new File(path));

            int height = image.getHeight();
            int width = image.getWidth();


            Converter quadranteC = new Converter(image, 0, height/2, 0, width/2);
            Converter quadranteD = new Converter(image,0, height/2, width/2, width );
            Converter quadranteA = new Converter(image, height/2, height, 0, width/2 );
            Converter quadranteB = new Converter(image, height/2, height, width/2, width );

            quadranteA.run();
            quadranteB.run();
            quadranteC.run();
            quadranteD.run();

            writeImage(image);
            
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
