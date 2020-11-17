package Game;
import java.io.*;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Main {
    @SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
        ClearScreen();
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
        String textLine, account, password , filePath= "Account Database.txt";
  
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
    
       password= scanner.nextLine();
        
        if (password.equals(passwordList.get(index)))
        {
            System.out.println("Logged in successfully!");
            nextstep(password);
        }


        System.out.println("Invalid password.");
        main(null);
    }
    
    public static void nextstep(String password) throws IOException
    {
    String choice,old,newpass;
    Scanner sc = new Scanner(System.in);
    System.out.println("Press 1 to play and 2 to change your password");
    choice = sc.nextLine();
    switch(choice)
    {
        case "1":
            System.out.println("In progress......");
            break;
        case "2":
            System.out.println("Enter your old password");
            old = sc.nextLine();
            if (old.contentEquals(password))
            {
            	System.out.println("Enter your new password");
            	newpass = sc.nextLine();
            	Path path = Paths.get("Account Database.txt");
            	Charset charset = StandardCharsets.UTF_8;

            	String content = new String(Files.readAllBytes(path), charset);
            	content = content.replaceAll(old, newpass);
            	Files.write(path, content.getBytes(charset));
            	System.out.println("Successfully changed");
            	nextstep(password);
            }
            
            
            
            else {
            	System.out.println("The password you entered was incorrect");
            	nextstep(password);
                 }
            break;
        default: 
        	System.out.println("Enter a valid number");
        	nextstep(password);
    }
    main(null);
    }

    public static void ClearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}


