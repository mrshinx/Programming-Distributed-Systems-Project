package Game;
import java.io.*;
import java.util.*;
import java.net.*;
public class Server {

    static ServerSocket server;
    static Socket connection;
    static DataInputStream inputStr;
    static DataOutputStream outputStr;
    static String input ="";

    public static void main(String[] args) throws IOException {

        server = new ServerSocket(1234);
        System.out.println("Wait for connections");
        connection = server.accept();
        System.out.println(connection + " just logged in");
        inputStr = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
        outputStr = new DataOutputStream(connection.getOutputStream());

        ServerBehavior();

    }

    public static void ServerBehavior() throws IOException {
        while(input.equals(""))
        {
            System.out.println("Waiting input");
            input = inputStr.readUTF();
            switch(input)
            {
                case "1":
                    Register();
                    break;
                case "2":
                    Login();
                    break;
                default:

            }
        }
    }

    public static void Register() throws IOException
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
            main(null);
        }
        // Read account input from client
        while (account.equals("")) {
            System.out.println("Listening for account");
            account = inputStr.readUTF();
            if ((account.length() > 0) && (!CheckForDuplicate(filePath, account))) {
                outputStr.writeUTF("ok");
                bw.write(account);
            } else {
                System.out.println();
                outputStr.writeUTF("Account name already exists, try another one");
                outputStr.flush();
                account = "";
            }
        }
        // Read password input from client
        System.out.println("Listening for password");
        password = inputStr.readUTF();
        if(password.length() >0)
        {
            bw.write(" ");
            bw.write(password);
            bw.newLine();
        }

        bw.close();
        input = "";
        ServerBehavior();
    }

    public static void Login() throws IOException
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
            main(null);
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

    public static Boolean CheckForDuplicate(String filePath, String accountname) throws IOException{

        ArrayList<String> accountList = new ArrayList<String>();
        ArrayList<String> passwordList = new ArrayList<String>();
        GetUsernameAndPassword(filePath, accountList, passwordList);

        if(accountList.contains(accountname)){
            return  true;
        }

        else{
            return  false;
        }
    }

    public static void GetUsernameAndPassword(String filePath, ArrayList<String> accountList,
                                              ArrayList<String> passwordList) throws IOException{

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
            main(null);
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

