package com.trabalho.app.servidor;

import com.trabalho.app.bean.FileMenssage;
import com.trabalho.app.bean.FileMenssage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucas
 */
public class Servidor {
    private ServerSocket serverSocket;
    private Socket socket;
    private final Map<String, ObjectOutputStream> streamMap = new  HashMap<String, ObjectOutputStream>();

    public Servidor() {
        try {
            serverSocket = new ServerSocket(5555);
            System.out.println("Servidor On!");
            
            while(true){
                socket = serverSocket.accept();
                
                new Thread(new ListenerSocket(socket)).start();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private class ListenerSocket implements Runnable{
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;
        
        public ListenerSocket(Socket socket) throws IOException{
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            FileMenssage menssage = null;
            
            try {
                while((menssage = (FileMenssage) inputStream.readObject()) != null ){
                    streamMap.put(menssage.getCliente(), outputStream);
                    
                    if (menssage.getFile() != null) {
                        for(Map.Entry<String, ObjectOutputStream> ky : streamMap.entrySet()){
                            if(!menssage.getCliente().equals(ky.getKey())){
                                ky.getValue().writeObject(menssage);
                            }
                        }
                        
                    }

                }
            } catch (IOException ex) {
               streamMap.remove(menssage.getCliente());
                System.out.println(menssage.getCliente() + " Desconectou");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
  
    }
    
    public static void main(String[] args) {
        new Servidor();
    }
}
