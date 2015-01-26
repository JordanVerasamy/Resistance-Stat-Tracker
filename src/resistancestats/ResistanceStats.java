package ResistanceStats;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class ResistanceStats
{
    
    public static void main(String[] args) throws Exception
    {
        
        
        
        Scanner keyboard = new Scanner(System.in);
        
        String[] rawLeaderboard = getRawData().split("</tr><tr>");
        ArrayList<Player> playerList = new ArrayList<Player>();
        
        
        System.out.println("Note: to change which players show up as UW players, add or remove names from players.txt (one name per line)\n");
        
        //Hidden dev tool: type 'mine' instead of one of the given keywords in order to view your own accounts
        System.out.println("How do you want to sort the list? Type one of: 'alphabetical' 'resistance' 'spy' or 'total', or type 'quit' to quit");
        String sortType = keyboard.next();
        
        if (sortType.equals("quit"))
        {
            System.exit(0);
        }
        
        Boolean lookingAtMyAccounts = false;
        
        if (sortType.equals("mine"))
        {
            lookingAtMyAccounts = true;
        }
        
        for (int j = 1; j < rawLeaderboard.length; j++)
        {
            parsePlayerData (playerList, rawLeaderboard[j], lookingAtMyAccounts);
        }
        
        printStatsTable(sortType, playerList);
        
        
    }
    
    static String getRawData()
    {
        try {
            String rawStatsPage = "http://www.theresistanceonline.com/server/stats/leaderboard";
            URL url = new URL(rawStatsPage);
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            
            int numCharsRead;
            
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            
            String result = sb.toString();
            
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //should never reach here
        return "Error pulling data from leaderboard.";
    }
    
    static void parsePlayerData (ArrayList<Player> playerList, String rawPlayerStats, Boolean lookingAtMyAccounts)
    {
        String[] input_parts = rawPlayerStats.split("</td> <td>");
        
        String rawNameData = input_parts[0];
        String rawTownData = input_parts[1];
        String rawSpyData = input_parts[2];
        
        String name = rawNameData.split("<td>")[1];
        int townw = Integer.parseInt(rawTownData.split(" ")[1].substring(1));
        int townl = Integer.parseInt(rawTownData.split(" ")[3].substring(0, rawTownData.split(" ")[3].length()-1)) - townw;
        int spyw = Integer.parseInt(rawSpyData.split(" ")[1].substring(1));
        int spyl = Integer.parseInt(rawSpyData.split(" ")[3].substring(0, rawSpyData.split(" ")[3].length()-1)) - spyw;
        
        if (isUWPlayer(name, lookingAtMyAccounts))
        {
            addPlayer(playerList, name, townw, townl, spyw, spyl);
        }
    }
    
    static void addPlayer(ArrayList<Player> pList, String name, int tw, int tl, int sw, int sl)
    {
        
        Player newPlayer = new Player(name, tw, tl, sw, sl);
        
        pList.add(newPlayer);
    }
    
    static String readTextFile(String fileName)
    {
        
        String returnValue = "";
        FileReader file = null;
        
        try
        {
            file = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(file);
            String line;
            while ((line = reader.readLine()) != null)
            {
                returnValue += line + "\n";
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (file != null)
            {
                try
                {
                    file.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return returnValue;
    }
    
    static Boolean isUWPlayer (String name, Boolean lookingAtMyAccounts)
    {
        
        int count = 0;
        
        String nameList = "";
        
        if (!lookingAtMyAccounts)
        {
            nameList = readTextFile("players.txt") + readTextFile("C:/Users/Faggot/Desktop/Stuff/ResistanceAccounts.txt");
        }
        else
        {
            nameList = readTextFile("C:/Users/Faggot/Desktop/Stuff/ResistanceAccounts.txt");
        }
        String line = nameList.split("\n")[0];
        
        while (true)
        {
            if (line.toUpperCase().equals(name.toUpperCase()))
            {
                return true;
            }
            
            if (lookingAtMyAccounts == false && (name.length() == 10 && name.substring(0, 5).equals("Harta") && name.substring(7, 10).equals("ack")))
            {
                return true;
            }
            
            if (count >= nameList.split("\n").length - 1)
            {
                break;
            }
            
            count++;
            line = nameList.split("\n")[count];
        }
        
        return false;
    }
    
    static void printStatsTable(String sortType, ArrayList<Player> playerList)
    {
        
        playerList = sortPlayers(sortType, playerList);
        
        int townw = 0, townl = 0;
        int spyw = 0, spyl = 0;
        
        System.out.println("\nName                          "
                + "Resistance                         "
                + "Spy                          "
                + "Total");
        System.out.println("------------------------------------------------"
                + "----------------------------------------------------------");
        
        for (int i = 0; i < playerList.size(); i++)
        {
            Player p = playerList.get(i);
            
            townw += p.townw;
            spyw += p.spyw;
            townl += p.townl;
            spyl += p.spyl;
            
            System.out.printf("%-25s %3dW  %3dL  %7.2f%s          "
                    + "%3dW  %3dL  %7.2f%s          "
                    + "%3dW  %3dL  %7.2f%s\n", 
                    p.name, p.townw, p.townl, p.townWinRate(), "%", 
                    p.spyw, p.spyl, p.spyWinRate(), "%", 
                    p.totalWins(), p.totalLosses(), p.totalWinRate(), "%");
        }
        
        System.out.println("\nTotal Resistance wins: " + townw);
        System.out.println("Total Resistance losses: " + townl);
        
        System.out.println("\nTotal Spy wins: " + spyw);
        System.out.println("Total Spy losses: " + spyl);
        
        
        if (sortType.equals("mine"))
        {
            System.out.printf("\nTotal Resistance win rate across all your accounts: %.3f%s", 
                    (double)100 * (double)(townw)/(double)(townw+townl), "%\n");
            System.out.printf("Total Spy win rate across all your accounts: %.3f%s", 
                    (double)100 * (double)(spyw)/(double)(spyw+spyl), "%\n");
            System.out.printf("Total win rate across all your accounts: %.3f%s", 
                    (double)100 * (double)(spyw+townw)/(double)(townw+townl+spyw+spyl), "%\n");
        }
        else
        {
            System.out.println("\nAverage Resistance win rate: " + Math.round((double)100000*townWinRate(playerList))/(double)1000 + "%");
            System.out.println("Average Spy win rate: " + Math.round((double)100000*spyWinRate(playerList))/(double)1000 + "%");
            System.out.println("Average total win rate: " + Math.round((double)100000*totalWinRate(playerList))/(double)1000 + "%\n");
        }
    }
    
    static double townWinRate(ArrayList<Player> playerList)
    {
        double count = 0;
        double players = 0;
        
        for (int i = 0; i < playerList.size(); i++)
        {
            if (!(playerList.get(i).townw == 0 && playerList.get(i).townl == 0))
            {
                players++;
                count += (double)playerList.get(i).townw / ((double)playerList.get(i).townl + (double)playerList.get(i).townw);
            }
        }
        
        return count / players;
    }
    
    static double spyWinRate(ArrayList<Player> playerList)
    {
        double count = 0;
        double players = 0;
        
        for (int i = 0; i < playerList.size(); i++)
        {
            if (!(playerList.get(i).spyw == 0 && playerList.get(i).spyl == 0))
            {
                players++;
                count += (double)playerList.get(i).spyw / ((double)playerList.get(i).spyl + (double)playerList.get(i).spyw);
            }
        }
        
        return count / players;
    }
    
    static double totalWinRate(ArrayList<Player> playerList)
    {
        double count = 0;
        double players = 0;
        
        for (int i = 0; i < playerList.size(); i++)
        {
            if (!(playerList.get(i).totalWins() == 0 && playerList.get(i).totalLosses() == 0))
            {
                players++;
                count += (double)playerList.get(i).totalWins() / ((double)playerList.get(i).totalLosses() + (double)playerList.get(i).totalWins());
            }
        }
        
        return count / players;
    }
    
    static ArrayList<Player> sortPlayers (String sortType, ArrayList<Player> playerList)
    {
        
        // sortType can be: "alphabetical", "resistance", "spy", or "total"
        
        ArrayList<Player> oldList = playerList;
        ArrayList<Player> newList = new ArrayList<Player>();
        
        if (sortType.equals("alphabetical"))
        {
            while (!oldList.isEmpty())
            {
                String max = oldList.get(0).name;
                Player add = new Player ("SOMETHING WENT WRONG");
                
                for (int i = 0; i < oldList.size(); i++)
                {
                    if (oldList.get(i).name.compareTo(max) <= 0)
                    {
                        max = oldList.get(i).name;
                        add = oldList.get(i);
                    }
                }
                
                newList.add(add);
                oldList.remove(oldList.indexOf(add));
            }
        }
        
        if (sortType.equals("resistance"))
        {
            while (!oldList.isEmpty())
            {
                double max = oldList.get(0).townWinRate();
                int maxgames = 0;
                Player add = oldList.get(0);
                
                for (int i = 0; i < oldList.size(); i++)
                {
                    if ((oldList.get(i).townWinRate() == max && oldList.get(i).townGames() >= maxgames) || oldList.get(i).townWinRate() > max)
                    {
                        max = oldList.get(i).townWinRate();
                        maxgames = oldList.get(i).townGames();
                        add = oldList.get(i);
                    }
                }
                
                newList.add(add);
                oldList.remove(oldList.indexOf(add));
            }
        }
        
        if (sortType.equals("spy"))
        {
            while (!oldList.isEmpty())
            {
                double max = oldList.get(0).spyWinRate();
                int maxgames = 0;
                Player add = oldList.get(0);
                
                for (int i = 0; i < oldList.size(); i++)
                {
                    if ((oldList.get(i).spyWinRate() == max && oldList.get(i).spyGames() >= maxgames) || oldList.get(i).spyWinRate() > max)
                    {
                        max = oldList.get(i).spyWinRate();
                        maxgames = oldList.get(i).spyGames();
                        add = oldList.get(i);
                    }
                }
                
                newList.add(add);
                oldList.remove(oldList.indexOf(add));
            }
        }
        
        if (sortType.equals("total") || sortType.equals("mine"))
        {
            while (!oldList.isEmpty())
            {
                double max = oldList.get(0).totalWinRate();
                int maxgames = 0;
                Player add = oldList.get(0);
                
                for (int i = 0; i < oldList.size(); i++)
                {
                    if ((oldList.get(i).totalWinRate() == max && oldList.get(i).totalGames() >= maxgames) || oldList.get(i).totalWinRate() > max)
                    {
                        max = oldList.get(i).totalWinRate();
                        maxgames = oldList.get(i).totalGames();
                        add = oldList.get(i);
                    }
                }
                
                newList.add(add);
                oldList.remove(oldList.indexOf(add));
            }
        }
        
        
        
        return newList;
    }
}