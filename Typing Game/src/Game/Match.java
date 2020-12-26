package Game;

import com.sun.security.ntlm.Server;

import java.io.*;

import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class Match implements Runnable{
    public Boolean stop = false;
    public List<ServerBehavior> Team1 = new ArrayList<ServerBehavior>();
    public List<ServerBehavior> Team2 = new ArrayList<ServerBehavior>();
    public BlockingQueue queue1 = new ArrayBlockingQueue(1024); // Create a blocking queue to communicate between Match and Team1
    public BlockingQueue queue2 = new ArrayBlockingQueue(1024); // Create a blocking queue to communicate between Match and Team2
    public Integer score11 = 0; // Score of player 1 on team 1
    public Integer score12 = 0; // Score of player 2 on team 1
    public Integer score21 = 0; // Score of player 1 on team 2
    public Integer score22 = 0; // Score of player 2 on team 2
    public Integer score1 = 0; // Total score of team 1
    public Integer score2 = 0; // Total score of team 2
    public Double time1 ; // Team 1 total time
    public Double time2 ; // Team 2 total time
    public Double time11 ; // Time of player 1 on team 1
    public Double time12 ; // Time of player 2 on team 1
    public Double time21 ; // Time of player 1 on team 2
    public Double time22 ; // Time of player 2 on team 2
    public String player11 ; // Account of player 1 on team 1
    public String player12 ; // Account of player 2 on team 1
    public String player21 ; // Account of player 1 on team 2
    public String player22 ; // Account of player 2 on team 2
    List<Record> recordData = new ArrayList<>();

    public Match(List<ServerBehavior> t1,List<ServerBehavior> t2)
    {
        Team1 = t1 ;
        Team2 = t2;
    }

    @Override
    public void run() {
        while(!stop)
        {
            int order = 1;
            boolean turn = true;
            for (ServerBehavior Player : Team1)
            {
                Player.queue = this.queue1; //Give the ServerBehavior the same queue as this Match
                Player.team = 1; // Assign team
                Player.order = order; // Assign playing order
                Player.play = true; // Tell the player to start playing
                Player.turn = turn; // Tell the player if it's his turn
                Player.match = this; // Tell the player that his game is hosted by this match thread
                order++; // change order
                turn = !turn; // change turn from true to false to assign to 2nd player (its not his turn yet)
            }
            // Do the same for team 2:
            order = 1;
            turn = true;
            for (ServerBehavior Player : Team2)
            {
                Player.queue = this.queue2;
                Player.team = 2;
                Player.order = order;
                Player.play = true;
                Player.turn = turn; // Tell the player if it's his turn
                Player.match = this; // Tell the player that his game is hosted by this match thread
                order++; // change order
                turn = !turn; // change turn from true to false to assign to 2nd player (its not his turn yet)
            }

            DetermineResult();
            try
            {
                if(score11 !=0) Record(player11,time11,queue1);
                if(score12 !=0) Record(player12,time12,queue1);
                if(score21 !=0) Record(player21,time21,queue2);
                if(score22 !=0) Record(player22,time22,queue2);
            }
            catch (IOException e) { e.printStackTrace(); } catch (InterruptedException e) { e.printStackTrace(); }

            DetermineRecord(time11);
            DetermineRecord(time12);
            DetermineRecord(time21);
            DetermineRecord(time22);

            for(ServerBehavior player : Team1)
            {
                if (player.record!=1)
                {
                    player.record = 0;
                }
            }
            for(ServerBehavior player : Team2)
            {
                if (player.record!=1)
                {
                    player.record = 0;
                }
            }
            stop = true;
        }
    }

    public void DetermineResult()
    {
        String[] queueinfo;
        try
        {
            queueinfo = queue1.take().toString().split(" ");
            player11 = queueinfo[0];
            score11 = Integer.parseInt(queueinfo[1]);
            time11 = Double.parseDouble(queueinfo[2]);
            queueinfo = queue1.take().toString().split(" ");
            player12 = queueinfo[0];
            score12 = Integer.parseInt(queueinfo[1]);
            time12 = Double.parseDouble(queueinfo[2]);
            score1 = score11+score12;
            System.out.println("Team 1 score: "+score1);

            queueinfo = queue2.take().toString().split(" ");
            player21 = queueinfo[0];
            score21 = Integer.parseInt(queueinfo[1]);
            time21 = Double.parseDouble(queueinfo[2]);
            queueinfo = queue2.take().toString().split(" ");
            player22 = queueinfo[0];
            score22 = Integer.parseInt(queueinfo[1]);
            time22 = Double.parseDouble(queueinfo[2]);
            score2 = score21+score22;
            System.out.println("Team 2 score: "+score2);

            time1 = time11+time12;
            System.out.println("Team 1 time: "+time1+" seconds");

            time2 = time21+time22;
            System.out.println("Team 2 time: "+time2+" seconds");

            if (score1>score2)
            {
                for (ServerBehavior Player : Team1)
                {
                    Player.result = 1;
                }
                for (ServerBehavior Player : Team2)
                {
                    Player.result = 2;
                }
            }
            else if(score2>score1)
            {
                for (ServerBehavior Player : Team1)
                {
                    Player.result = 2;
                }
                for (ServerBehavior Player : Team2)
                {
                    Player.result = 1;
                }
            }
            else if(time1<time2)
            {
                for (ServerBehavior Player : Team1)
                {
                    Player.result = 1;
                }
                for (ServerBehavior Player : Team2)
                {
                    Player.result = 2;
                }
            }
            else
            {
                for (ServerBehavior Player : Team1)
                {
                    Player.result = 2;
                }
                for (ServerBehavior Player : Team2)
                {
                    Player.result = 1;
                }
            }

        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void Record(String account,Double time, BlockingQueue queue) throws IOException, InterruptedException {
        String filePath= "Record.txt";
        ArrayList<String> accountList = new ArrayList<String>();
        ArrayList<String> timeList = new ArrayList<String>();
        recordData = new ArrayList<>();

        String[] recordInfo;

        BufferedReader br = null;
        String textLine;
        try
        {
            br = new BufferedReader(new FileReader(filePath));
        }
        catch(Exception e)
        {
            System.out.println("File does not exist, attempting to create new file...");
            BufferedWriter bw = null;
            try
            {
                bw = new BufferedWriter(new FileWriter(filePath,false));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            bw.close();
            br = new BufferedReader(new FileReader(filePath));
        }

        textLine = br.readLine();

        while(textLine!=null)
        {
            recordInfo = textLine.split(" ");
            accountList.add(recordInfo[0]);
            timeList.add(recordInfo[1]);

            textLine = br.readLine();
        }
        br.close();

        //Populate record data:
        if (accountList.size()>0)
        {
            for (int i = 0; i < accountList.size(); i++)
            {
                recordData.add(new Record(accountList.get(i),Double.parseDouble(timeList.get(i))));
            }
        }
        recordData.add(new Record(account,time));

        Collections.sort(recordData, new Record.CustomerSortingComparator()); // Sort record data

        BufferedWriter bw = null;
        try
        {
            bw = new BufferedWriter(new FileWriter(filePath,false));
        }
        catch(Exception e)
        {
            System.out.println("Cannot create file");
        }

        for (int i=0; i<recordData.size();i++)
        {
            if (i==5) break;
            bw.write(recordData.get(i).account);
            bw.write(" ");
            bw.write(recordData.get(i).time.toString());
            bw.newLine();
        }
        bw.close();

    }

    public void DetermineRecord(Double time)
    {
        System.out.println("Base time: "+ time);
        for (int i=0; i<recordData.size();i++)
        {
            if (i==5) break;
            System.out.println("Time to compare: "+ recordData.get(i).time);
            if (time.toString().equals(recordData.get(i).time.toString()))
            {
                System.out.println(time+" = "+recordData.get(i).time);
                for(ServerBehavior player : Team1)
                {
                    if (player.account.equals(recordData.get(i).account))
                    {
                        player.record = 1;
                    }
                }
                for(ServerBehavior player : Team2)
                {
                    if (player.account.equals(recordData.get(i).account))
                    {
                        player.record = 1;
                    }
                }
            }

        }
    }
}