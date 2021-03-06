package Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerThreadManager {

    private static Socket connection;  // Create Socket
    public static ArrayList<Socket> activeConnections = new ArrayList<Socket>(); // Create list of active connections
    private static ServerSocket serverSocket;   // Create a Server Socket
    public static HashMap<ServerBehavior, String> OnlineAccounts = new HashMap<ServerBehavior, String>(); // Contains thread names and corresponding account names
    public static HashMap<ServerBehavior,String> ReadyAccounts = new HashMap<ServerBehavior,String>();
    public static Integer a = 15;

    public static void main(String[] args) throws IOException
    {
        serverSocket = new ServerSocket(1234); // Set server socket
        ExecutorService exec = Executors.newFixedThreadPool(10); //Set thread

        MatchManager matchManager = new MatchManager(); // Create MatchManager
        matchManager.start(); // run matchManager

        while (true)
        {
                connection = serverSocket.accept(); // Wait for connection
                System.out.println(connection + " just logged in");

                activeConnections.add(connection); //Add new connection to connection list to track them
                System.out.println("Number of active connections: " + activeConnections.size()); //Print number of active connections

                ServerBehavior serverBehavior = new ServerBehavior(connection); // create a new socket task
                exec.execute(serverBehavior); // Run Task

        }
    }
}