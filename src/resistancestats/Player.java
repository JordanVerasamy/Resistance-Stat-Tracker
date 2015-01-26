package ResistanceStats;

public class Player
{
    String name;
    int townw;
    int townl;
    int spyw;
    int spyl;
    
    public Player(String n, int tw, int tl, int sw, int sl)
    {
        name = n;
        townw = tw;
        townl = tl;
        spyw = sw;
        spyl = sl;
    }
    
    public Player(String n)
    {
        name = n;
        townw = 0;
        townl = 0;
        spyw = 0;
        spyl = 0;
    }
    
    public Player()
    {
        name = "";
        townw = 0;
        townl = 0;
        spyw = 0;
        spyl = 0;
    }

    public double townWinRate()
    {
       return (Math.round(10000*(double)townw/((double)townw+(double)townl)))/(double)100;
    }
    
    public double spyWinRate()
    {
        return (Math.round(10000*(double)spyw/((double)spyw+(double)spyl)))/(double)100;
    }
    
    public int spyGames()
    {
        return spyw + spyl;
    }
    
    public int townGames()
    {
        return townw + townl;
    }
    
    public int totalWins()
    {
        return townw + spyw;
    }
    
    public int totalLosses()
    {
        return townl + spyl;
    }
    
    public int totalGames()
    {
        return totalWins() + totalLosses();
    }
    
    public double totalWinRate()
    {
        return (Math.round(100000*(double)totalWins()/((double)totalWins()+(double)totalLosses())))/(double)1000;
    }
}
