package b01.tests;

import b01.foc.Globals;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketSender {

    String host = "localhost";
    int port = 12345;         // Replace with the target port

    Socket socket;

    SocketSender(int port) {
        this.port = port;
        open();
    }

    public void open() {
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            Globals.logString("Error opening socket: " + e.getMessage());
        }
    }

    public void send(String message) {
         // Replace with the target host

        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(message);
            System.out.println("Message sent: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
