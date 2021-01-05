package Game;

import com.sun.security.ntlm.Server;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import  java.util.Random;
import java.util.concurrent.BlockingQueue;

public class ServerBehavior implements Runnable{

    public DataInputStream inputStr;
    public DataOutputStream outputStr;
    public String input ="";
    public Socket connection;  // Create Socket
    public Boolean stop = false;
    public Boolean play = false;
    public Boolean turn = false;
    public Integer team;
    public Integer order;
    public BlockingQueue queue;
    public ServerBehavior(Socket s)
    {
        this.connection = s;
    }
    public Match match;
    public ServerBehavior matchedplayer;
    public Integer score =3;
    public Integer result = 0;
    public Integer record = 2;
    public String account;

    @Override
    public void run() {
        while(!stop)
        {
            try
            {

                inputStr = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
                outputStr = new DataOutputStream(connection.getOutputStream());

                Behavior();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public void Behavior() throws IOException {
        while(input.equals(""))
        {
            try
            {
                input = inputStr.readUTF();
                switch (input) {
                    case "1":
                        Register();
                        break;
                    case "2":
                        Login();
                        break;
                    default:
                        ServerThreadManager.ReadyAccounts.put(this, input);
                        System.out.println("Ready Players: " + ServerThreadManager.ReadyAccounts);
                        // Waiting for boolean "play" to turns true to start executing Play() method
                        while(!play)
                        {
                            try {Thread.sleep(100);} catch (InterruptedException e) { e.printStackTrace();}
                            continue;
                        }

                        while (play)
                        {
                            Play();
                            play = false;
                        }
                        break;

                }
            }
            catch (IOException e)
            {
                input = " ";
                stop = true;
                ServerThreadManager.activeConnections.remove(connection);
                System.out.println(connection+ " disconnected");
                this.run();
            }

        }
    }

    public void Register() throws IOException
    {
        String account="", password="", filePath = "Account Database.txt";

        BufferedWriter bw = null;
        try
        {
            bw = new BufferedWriter(new FileWriter(filePath,true));
        }
        catch(Exception e)
        {
            System.out.println("Cannot create file");
        }

        while (account.equals(""))
        {
            // Read account input from client

            account = inputStr.readUTF();
            //Check if account is valid
            if ((account.length() > 0) && (!CheckForDuplicate(filePath, account))&&(!account.equals(" ")))
            {
                outputStr.writeUTF("ok");
                bw.write(account);
            }
            else
            {
                System.out.println();
                outputStr.writeUTF("Invalid account, try another one");
                outputStr.flush();
                account = "";
            }
        }
        // Read password input from client
        password = inputStr.readUTF();
        if(password.length() >0)
        {
            bw.write(" ");
            bw.write(password);
            bw.newLine();
        }
        System.out.println(Thread.currentThread().getName() + " Created account: " + account);
        bw.close();
        //Reset input and call ServerBehavior method to start all over again
        input = "";
        Behavior();
    }

    public void Login() throws IOException
    {
        String account="", password="", filePath= "Account Database.txt";
        int count =0;
        int index =0;
        String[] accountInfo;
        ArrayList<String> accountList = new ArrayList<String>();
        ArrayList<String> passwordList = new ArrayList<String>();
        GetUsernameAndPassword(filePath, accountList, passwordList);

        while (account.equals(""))
        {
            account = inputStr.readUTF();

            if(ServerThreadManager.OnlineAccounts.containsValue(account)){
                outputStr.writeUTF(account + " has already logged in");
                outputStr.flush();
                account="";
            }

            else{
                for (String i : accountList)
                {
                    if (account.equals(i)) {
                        count++;
                        index = accountList.indexOf(i);
                        outputStr.writeUTF("ok");
                        outputStr.flush();
                        break;
                    }
                }
                if (count == 0)
                {
                    outputStr.writeUTF("Invalid account, try another one");
                    outputStr.flush();
                    account="";
                }
            }
        }

        while (password.equals(""))
        {
            password = inputStr.readUTF();
            if (password.equals(passwordList.get(index)))
            {
                ServerThreadManager.OnlineAccounts.put(this, account);
                System.out.println("Online Players : " + ServerThreadManager.OnlineAccounts);
                outputStr.writeUTF("ok");
                outputStr.flush();
                input="";
                this.account = account;
                Behavior();
            }
            else {
                outputStr.writeUTF("Invalid password");
                outputStr.flush();
                password="";
            }
        }
    }

    public Boolean CheckForDuplicate(String filePath, String accountname) throws IOException{

        ArrayList<String> accountList = new ArrayList<String>();
        ArrayList<String> passwordList = new ArrayList<String>();
        GetUsernameAndPassword(filePath, accountList, passwordList);

        if(accountList.contains(accountname))
        {
            return  true;
        }

        else{
            return  false;
        }
    }

    public void GetUsernameAndPassword(String filePath, ArrayList<String> accountList,ArrayList<String> passwordList) throws IOException{

        String[] accountInfo;

        BufferedReader br = null;
        String textLine;
        try
        {
            br = new BufferedReader(new FileReader(filePath));
        }
        catch(Exception e)
        {
            System.out.println("Cannot open file, program terminated");
        }

        textLine = br.readLine();
        while(textLine!=null)
        {
            accountInfo = textLine.split(" ");
            accountList.add(accountInfo[0]);
            passwordList.add(accountInfo[1]);

            textLine = br.readLine();
        }
        br.close();
    }

    public String Textgen()
    {
        String characters = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String text = "";
        Random rand = new Random();
        while(text.length()<62)
        {
            int length = rand.nextInt(8);
            char[] word = new char[length];
            if (length==0)
                continue;
            for(int i=0; i<length ;i++)
            {

                word[i]=characters.charAt(rand.nextInt(characters.length()));

            }
            for(int i=0; i < word.length;i++)
            {
                text += word[i];

            }
            text += " ";
        }
        int lastlength = 70 - text.length();
        char[] word = new char[lastlength];
        for(int i=0; i<lastlength ;i++)
        {

            word[i]=characters.charAt(rand.nextInt(characters.length()));

        }
        for(int i=0; i < word.length;i++)
        {
            text += word[i];

        }

        return text;
    }

    void Play()
    {
        if (order ==1)
        {
            try
            {
                DetermineMatchedPlayer();
                outputStr.writeUTF("You are on team " + team);
                outputStr.flush();
                outputStr.writeUTF("You are the first player on your team");
                outputStr.flush();
                CountdownAndStart();
            } catch (IOException e) { e.printStackTrace(); }
        }
        else
        {
            try
            {
                DetermineMatchedPlayer();
                outputStr.writeUTF("You are on team " + team);
                outputStr.flush();
                outputStr.writeUTF("You are the second player on your team. Wait until your turn.");
                outputStr.flush();
                while(!turn)
                {
                    Thread.sleep(200);
                    continue;
                }
                CountdownAndStart();
            }

            catch (IOException e) { e.printStackTrace(); } catch (InterruptedException e) { e.printStackTrace(); }
        }

    }

    void DetermineMatchedPlayer() {
        try {
            if (team == 1)
            {
                for (ServerBehavior Player : match.Team1)
                {
                    if (Player != this)
                    {
                        matchedplayer = Player;
                        outputStr.writeUTF("You are matched with " + ServerThreadManager.OnlineAccounts.get(Player));
                        outputStr.flush();
                    }
                }
            }
            else {
                for (ServerBehavior Player : match.Team2)
                {
                    if (Player != this)
                    {
                        matchedplayer = Player;
                        outputStr.writeUTF("You are matched with " + ServerThreadManager.OnlineAccounts.get(Player));
                        outputStr.flush();
                    }
                }
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    void CountdownAndStart()
    {
        Integer countdown = 5;
        String playerInput;
        String text = Textgen();

        try{
            outputStr.writeUTF("Game starts after:");
            outputStr.flush();
            while (countdown > 0)
            {
                outputStr.writeUTF(countdown.toString());
                outputStr.flush();
                Thread.sleep(1000);
                countdown--;
            }

            outputStr.writeUTF("Start");
            outputStr.flush();

            outputStr.writeUTF(text); // Send random text to player
            outputStr.flush();
            double start = System.currentTimeMillis(); // start timer

            playerInput = inputStr.readUTF(); // ready player input
            double end = System.currentTimeMillis(); // end timer
            double elapsedTime = end - start; // calculate elapsed time

            if(order ==1)
            {
                matchedplayer.turn =true;
            }

            if (playerInput.equals(text))
            {
                outputStr.writeUTF("Correct");
                outputStr.flush();
                outputStr.writeUTF("Time: " +elapsedTime/1000 + " seconds");
                outputStr.flush();
                score = 1; //Get score if correct
                queue.put(this.account+ " "+ score +" " + elapsedTime/1000 ); // give Match the result
            }
            else
            {
                outputStr.writeUTF("Wrong");
                outputStr.flush();
                outputStr.writeUTF("Time: " +elapsedTime/1000 + " seconds");
                outputStr.flush();
                score = 0; //Get score if wrong
                queue.put(this.account+ " "+ score +" " + elapsedTime/1000);
            }

            // Wait for the second player score to update if this is the 1st player:
            while (matchedplayer.score==3)
            {
                Thread.sleep(100);
                continue;
            }

            outputStr.writeUTF("Your team score is: " + (this.score + matchedplayer.score));
            outputStr.flush();

            // Wait for result win or lose:
            while (result==0)
            {
                Thread.sleep(100);
                continue;
            }
            if (result == 1)
            {
                outputStr.writeUTF("Your team has won");
                outputStr.flush();
            }
            else
            {
                outputStr.writeUTF("Your team has lost");
                outputStr.flush();
            }

            while(record==2)
            {
                Thread.sleep(100);
                continue;
            }
            if (record == 1)
            {
                outputStr.writeUTF("You have made a new record");
                outputStr.flush();
            }

            outputStr.writeUTF("Latest record table:");
            outputStr.flush();

            BufferedReader br = null;
            String textLine;
            try
            {
                br = new BufferedReader(new FileReader("Record.txt"));
                textLine = br.readLine();
                while(textLine!=null)
                {
                    outputStr.writeUTF(textLine);
                    outputStr.flush();
                    textLine = br.readLine();
                }
                br.close();
            }
            catch(Exception e)
            {
                System.out.println("Cannot open file, program terminated");
            }

            outputStr.writeUTF("end");
            outputStr.flush();
        }
        catch (IOException e) { e.printStackTrace(); } catch (InterruptedException e) { e.printStackTrace(); }

    }

}