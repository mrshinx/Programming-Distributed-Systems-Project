package Game;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import  java.util.Random;

public class ServerBehavior implements Runnable{

    public DataInputStream inputStr;
    public DataOutputStream outputStr;
    public String input ="";
    public Socket connection;  // Create Socket
    public Boolean stop = false;

    public ServerBehavior(Socket s)
    {
        this.connection = s;
    }

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
                System.out.println("Logged in successfully!");
                ServerThreadManager.OnlineAccounts.put(Thread.currentThread().getName(), account);
                System.out.println("Online Players : " + ServerThreadManager.OnlineAccounts);
                outputStr.writeUTF("ok");
                outputStr.flush();
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

    public void textgen()
    {
        String characters = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String text = "";
        String block;
        Random rand = new Random();
        while(text.length()<80)
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
        block = text.substring(0, text.length() -1);
        System.out.println(block);
    }

}
