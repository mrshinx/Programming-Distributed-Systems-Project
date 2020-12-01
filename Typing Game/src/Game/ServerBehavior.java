package Game;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerBehavior implements Runnable{

    public DataInputStream inputStr;
    public DataOutputStream outputStr;
    public String input ="";
    public Socket connection;  // Create Socket

    public ServerBehavior(Socket s)
    {
        this.connection = s;
    }

    @Override
    public void run() {
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
                e.printStackTrace();
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
        String textLine, account, password, filePath= "Account Database.txt";
        int count =0;
        int index =0;
        String[] accountInfo;
        ArrayList<String> accountList = new ArrayList<String>();
        ArrayList<String> passwordList = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);

        BufferedReader br = null;
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

        System.out.println("Enter your account: " );
        account = scanner.nextLine();
        account = inputStr.readUTF();
        for (String i : accountList)
        {
            if (account.equals(i))
            {
                count++;
                index = accountList.indexOf(i);
                outputStr.writeUTF("ok");
                break;
            }
        }
        if (count ==0)
        {
            System.out.println("Invalid account" );
            outputStr.writeUTF("Invalid account, try another one");
            outputStr.flush();
        }

        System.out.println("Enter your password: " );
        password = scanner.nextLine();
        if (password.equals(passwordList.get(index)))
        {
            System.out.println("Logged in successfully!");
            outputStr.writeUTF("ok");
        }
        else {
        System.out.println("Invalid password.");
        outputStr.writeUTF("Invalid password");
        outputStr.flush();
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



}
