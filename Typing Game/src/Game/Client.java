package Game;
import java.io.*;
import java.util.*;
import java.net.*;

public class Client {

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
            // Start communication
            outputStr.writeUTF("Hi, I'm the client!");
            outputStr.flush();
            String message = inputStr.readUTF();
            System.out.println("Game Server: " + message);
            // Close streams and socket
            outputStr.close();
            inputStr.close();
            connection.close();
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
        String account, password;
        int count =0;
        int index =0;
        String[] accountInfo;
        ArrayList<String> accountList = new ArrayList<String>();
        ArrayList<String> passwordList = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your account: " );
        account = scanner.nextLine();
        for (String i : accountList)
        {
            if (account.equals(i))
            {
                count++;
                index = accountList.indexOf(i);
                break;
            }
        }
        if (count ==0)
        {
            System.out.println("Invalid account" );
            main(null);
        }

        System.out.println("Enter your password: " );
        password = scanner.nextLine();
        if (password.equals(passwordList.get(index)))
        {
            System.out.println("Logged in successfully!");
            main(null);
        }

        System.out.println("Invalid password.");
        main(null);
    }

}
