package Game;
import java.io.*;
import java.util.*;
public class Main {
    public static void main(String[] args) throws IOException {
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
                Register();
                break;
            case 2:
                Login();
            default:

        }



    }

    public static void Register() throws IOException
    {
        String account, password, filePath = "Account Database.txt";
        Scanner sc = new Scanner(System.in);

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

        System.out.println("Please enter account: ");
        account = sc.nextLine();
        if(account.length() >0)
        {
            bw.write(account);
        }

        System.out.println("Please enter password: ");
        password = sc.nextLine();
        if(password.length() >0)
        {
            bw.write(" ");
            bw.write(password);
            bw.newLine();
        }

        bw.close();
        main(null);
    }

    public static void Login() throws IOException
    {
        String textLine, account, password, filePath= "Account Database.txt";
        int count =0;
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
        for (String i : passwordList)
        {
            if (password.equals(i))
            {
                System.out.println("Logged in successfully!");
                main(null);
            }
        }

        System.out.println("Invalid password.");
        main(null);
    }

}

