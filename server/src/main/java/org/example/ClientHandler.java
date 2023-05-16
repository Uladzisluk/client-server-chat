package org.example;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    private static final Logger log = Logger.getLogger(Server.class);
    private static final ArrayList<ClientHandler> CLIENTS = new ArrayList<>();
    private Socket socket;
    private String name;
    InputStream in;
    OutputStream out;
    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            in = socket.getInputStream();
            out = socket.getOutputStream();
            CLIENTS.add(this);
        }catch (IOException e){
            log.error(e.getMessage());
            close();
        }

    }

    @Override
    public void run(){
        String message = "";
        main: try{
            out.write("Podaj swoją imię".getBytes());
            out.flush();
            byte[] byteMes = new byte[80];
            in.read(byteMes);
            String nameFromBytes = new String(byteMes);
            if (nameFromBytes.equals("KONIEC")) break main;
            name = nameFromBytes;
            out.write("Teraz możesz pisać to czatu".getBytes());
            while(!message.equals("KONIEC")){
                byte[] byteMessage = new byte[80];
                in.read(byteMessage);
                log.info("Klient wysłał wiadomość");
                message = new String(byteMessage);
                if(message.equals("KONIEC")) continue;
                broadcast(message);
            }
        }catch (IOException e){
            log.error(e.getMessage());
        }finally {
            close();
        }

    }

    private void broadcast(String message){
        StringBuilder messageBuilder = new StringBuilder(message);
        for(ClientHandler clientHandler : CLIENTS){
            try{
                if(clientHandler.name.equals(this.name)) continue;
                messageBuilder.insert(0, name + ": " + message);
                byte[] byteMessage = messageBuilder.toString().getBytes();
                clientHandler.out.write(byteMessage);
                clientHandler.out.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void close(){
        CLIENTS.remove(this);
        try{
            if(socket!=null){
                socket.close();
            }
            if(in!=null){
                in.close();
            }
            if(out!=null){
                out.close();
            }
        }catch (IOException e){
            log.error(e.getMessage());
        }

    }
}
