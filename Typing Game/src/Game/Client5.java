package Game;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client5 {

    static DataOutputStream outputStr;
    static DataInputStream inputStr;
    static Socket connection;

    public static void main(String[] args) throws IOException {

        int port = 1234;
        String ip = "127.0.0.1";

        try
        {
            // Connect with server
            connection = new Socket(ip, port);
            System.out.println("Connected to server! " + connection);
            // Setting up input and output streams
            outputStr = new DataOutputStream(connection.getOutputStream());
            inputStr = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
            //Start Interface
            Interface();
            // Close streams and socket
            //outputStr.close();
            //inputStr.close();
            //connection.close();
        }
        catch (IOException ex)
        {
            System.out.println("Connection error! " + ex);
        }

    }

    public static void Interface() throws IOException {
        int input =-1;
        Scanner inputCommand = new Scanner(System.in);
        while(input <0) {
            try {
                System.out.println("Press 1 to register, press 2 to login: ");
                input = inputCommand.nextInt();
                if ((input >2)||(input<1))
                {
                    System.out.println("Invalid input, please try again...");
                    input = -1;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid input, please try again...");
                inputCommand.nextLine();
            }
        }

        switch(input)
        {
            case 1:
                outputStr.writeUTF("1");
                outputStr.flush();
                Register();
                break;
            case 2:
                outputStr.writeUTF("2");
                outputStr.flush();
                Login();
                break;
            default:

        }
    }

    public static void Register() throws IOException
    {
        String account="", password="", serverMessage;
        Scanner sc = new Scanner(System.in);

        while(account.equals(""))
        {
            System.out.println("Please enter account: ");
            account = sc.nextLine();
            outputStr.writeUTF(account);
            outputStr.flush();
            serverMessage = inputStr.readUTF();
            //Check if server send ok response (account valid or not)
            if(!serverMessage.equals("ok"))
            {
                System.out.println(serverMessage);
                account = "";
            }

        }
        while(password.equals(""))
        {
            System.out.println("Please enter password: ");
            password = sc.nextLine();
            outputStr.writeUTF(password);
            outputStr.flush();
        }
        Interface();
    }

    public static void Login() throws IOException
    {
        String account="", password="", serverMessage;
        Scanner sc = new Scanner(System.in);

        while(account.equals(""))
        {
            System.out.println("Please enter account: ");
            account = sc.nextLine();
            //Send account to server
            outputStr.writeUTF(account);
            outputStr.flush();
            //Check if server send ok response (account valid or not)
            serverMessage = inputStr.readUTF();
            if(!serverMessage.equals("ok"))
            {
                System.out.println(serverMessage);
                account = "";
            }
        }

        while(password.equals(""))
        {
            System.out.println("Please enter password: ");
            password = sc.nextLine();
            //Send password to server
            outputStr.writeUTF(password);
            outputStr.flush();
            //Check if server send ok response (password valid or not)
            serverMessage = inputStr.readUTF();
            if(serverMessage.equals("ok"))
            {
                System.out.println("Logged in successfully");
                Play(account);
            }
            else
            {
                System.out.println(serverMessage);
                password = "";
            }
        }

    }

    public static void Play(String account) throws IOException
    {
        String input ="", serverMessage="";
        Scanner sc = new Scanner(System.in);

        System.out.println("Press y to get ready for matchmaking: ");
        while(!input.equals("y"))
        {
            input = sc.nextLine();
            if(!input.equals("y")) System.out.println("Invalid input, press y to get ready for matchmaking: ");
        }

        System.out.println("Waiting for matchmaking...");
        outputStr.writeUTF(account);
        outputStr.flush();

        while(!serverMessage.equals("Start"))
        {
            serverMessage = inputStr.readUTF();
            System.out.println(serverMessage);
        }
        // Read and print the random text:
        serverMessage = inputStr.readUTF();
        System.out.println(serverMessage);
        // Player types the text:
        input = sc.nextLine();
        // Send what the player typed to ServerBehavior
        outputStr.writeUTF(input);
        outputStr.flush();

        // Receive message from server to know player performance and result
        while(!serverMessage.equals("end"))
        {
            serverMessage = inputStr.readUTF();
            if(!serverMessage.equals("end")) System.out.println(serverMessage);
        }
    }

}
