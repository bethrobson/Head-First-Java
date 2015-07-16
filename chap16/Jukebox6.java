package chap16;

import java.util.*;
import java.io.*;

public class Jukebox6
{
    ArrayList<SongBad> songList = new ArrayList<SongBad>();
    
    public static void main(String[] args) {
        new Jukebox6().go();
    }
    
    public void go() {
        getSongs();
        System.out.println(songList);
        Collections.sort(songList);
        System.out.println(songList);
        
        HashSet<SongBad> songSet = new HashSet<SongBad>();
        songSet.addAll(songList);
        System.out.println(songSet);
    }
    
    void getSongs() {
        try {
            File file = new File("SongListMore.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                addSong(line);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    void addSong(String lineToParse) {
        String[]tokens = lineToParse.split("/");
        SongBad nextSong = new SongBad(tokens[0], tokens[1], tokens[2], tokens[3]);
        songList.add(nextSong);
    }
}


class SongBad implements Comparable <SongBad>
{
    String title;
    String artist;
    String rating;
    String bpm;
    
    
    public SongBad(String t, String a, String r, String b) {
        title = t;
        artist = a;
        rating = r;
        bpm = b;
    }
    public boolean equals(Object aSong) {
        SongBad s = (SongBad) aSong;
        return getTitle().equals(s.getTitle());
    }
    
    //leaving this out makes this a bad form of song.  Uncomment this to get rid of the duplicates
    /*public int hashCode() {
        return title.hashCode();
    }
    */
    
    public int compareTo(SongBad s)
    {
        return title.compareTo(s.getTitle());
    }

    public String getArtist()
    {
        return artist;
    }

    public String getBpm()
    {
        return bpm;
    }

    public String getRating()
    {
        return rating;
    }

    public String getTitle()
    {
        return title;
    }
    
    public String toString() {
        return title;
    }

}