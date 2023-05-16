package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
public class Server {

    public final static int PORT = 4004;
    private static final Logger log = Logger.getLogger(Server.class);

    public static void main(String[] args){
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            log.info("Serwer został stworzony");
            Socket socket = serverSocket.accept();
            log.info("A new client has connected");
            ClientHandler clientHandler = new ClientHandler(socket);
            new Thread(clientHandler).start();
        }catch (IOException e){
            log.error(e.getMessage());
        }finally {
            log.info("Serwer się zamknął");
        }
    }
}
