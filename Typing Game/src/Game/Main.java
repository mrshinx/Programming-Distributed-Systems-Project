package Game;
import java.io.*;
import java.util.*;
public class Main {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        ClearScreen();
        int input =-1;
        Scanner inputCommand = new Scanner(System.in);
        while(input <0)
        {
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
            if(!CheckForDuplicate(filePath, account)){
                bw.write(account);

                System.out.println("Please enter password: ");
                password = sc.nextLine();
                if(password.length() >0)
                {
                    bw.write(" ");
                    bw.write(password);
                    bw.newLine();
                }
            }
            else{
                System.out.println("Account name already exists, try another one");
            }
        }

        bw.close();
        main(null);
    }

    public static void Login() throws IOException
    {
        String textLine, account, password , choice ,newpass ,  filePath= "Account Database.txt";

        int count =0;
        int index =0;

        ArrayList<String> accountList = new ArrayList<String>();
        ArrayList<String> passwordList = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);

        GetUsernameAndPassword(filePath, accountList, passwordList);

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
            System.out.println("Press 1 to play and 2 to change your password");
            choice = scanner.nextLine();
            switch(choice)
            {
                case "1":
                    System.out.println("In progress...");
                    play();
                    break;
                case "2":
                    System.out.println("Enter your new password");
                    newpass= scanner.nextLine();
                    String oldContent = "" ;
                    File fileToBeModified = new File(filePath);
                    BufferedReader reader = null;
                    FileWriter writer = null;
                    try
                    {
                        reader = new BufferedReader(new FileReader(fileToBeModified));

                        //Reading all the lines of input text file into oldContent

                        String line = reader.readLine();

                        while (line != null)
                        {
                            oldContent = oldContent + line + System.lineSeparator();

                            line = reader.readLine();
                        }

                        //Replacing oldString with newString in the oldContent

                        String newContent = oldContent.replaceAll(password, newpass);

                        //Rewriting the input text file with newContent

                        writer = new FileWriter(fileToBeModified);

                        writer.write(newContent);
                        System.out.println("Password successfully changed");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            //Closing the resources

                            reader.close();

                            writer.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;

                default:
                    System.out.println("Invalid input");
                    Login();
            }



        }

        else {
            System.out.println("Invalid password.");
        }
        main(null);
    }


    /*	System.out.println("Enter your new password");
        newpass = sc.nextLine();
        Path path = Paths.get("Account Database.txt");
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll(old, newpass);
        Files.write(path, content.getBytes(charset));
        System.out.println("Successfully changed"); */
    public static void play() throws IOException
    { System.out.println("In progress..");
        main(null);
    }


    public static void ClearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
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
