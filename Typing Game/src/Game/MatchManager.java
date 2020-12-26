package Game;

import com.sun.security.ntlm.Server;

import java.io.*;

import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchManager extends Thread{

    public void run()
    {

        while(true)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (ServerThreadManager.ReadyAccounts.size() >= 4)
            {
                MakeTeam();
            }
        }
    }

    public void MakeTeam()
    {
        ExecutorService exec = Executors.newFixedThreadPool(10); //Set thread limit
        Random random = new Random();
        List<ServerBehavior> keys = new ArrayList<ServerBehavior>(ServerThreadManager.ReadyAccounts.keySet()); // Create list of players to pick
        List<ServerBehavior> Team1 = new ArrayList<ServerBehavior>();
        List<ServerBehavior> Team2 = new ArrayList<ServerBehavior>();

        for (int i = 0; i < 2; i++)
        {

            ServerBehavior randomPlayer = keys.get(random.nextInt(keys.size())); // Pick random player (serverBehavior) from ready list
            Team1.add(randomPlayer); // Add picked player to Team 1
            System.out.println("Player Team 1: " + randomPlayer);
            ServerThreadManager.ReadyAccounts.remove(randomPlayer); // Delete picked player from ready list
            System.out.println("List after picked: " + ServerThreadManager.ReadyAccounts);
            keys.remove(randomPlayer); // Delete picked player from key

            randomPlayer = keys.get(random.nextInt(keys.size())); // Pick random player (serverBehavior) from ready list
            Team2.add(randomPlayer); // Add picked player to Team 2
            System.out.println("Player Team 2: " + randomPlayer);
            ServerThreadManager.ReadyAccounts.remove(randomPlayer); // Delete picked player from ready list
            System.out.println("List after picked: " + ServerThreadManager.ReadyAccounts);
            keys.remove(randomPlayer); // Delete picked player from key

        }

        Match match = new Match(Team1, Team2); // Create a new task, which is responsible for hosting a match
        exec.execute(match); // Start the match
    }

}