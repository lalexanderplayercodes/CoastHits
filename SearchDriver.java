import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchDriver {
    //create a data structure to hold all hits
    private final List<Hit> Hits;

    //Main driver class
    public SearchDriver() throws IOException {
        //initialize hits list
        Hits = new ArrayList<>();
        //list of all urls to search
        List<String> urls = new ArrayList<>();
        //create list dynamically, for hits of years from 2019 to 2021
        for (int  i = 2019; i <= 2021; i++) {
            urls.add(String.format("https://top40weekly.com/all-us-top-40-singles-for-%d/", i));
        }
        //read data from each url, for each year
        for(String url: urls){
            search(url);
        }
    }

    ///add a hit if it is not already in the list
    public void add(Hit hit){
        for(Hit hit1: Hits){
            if(hit1.equals(hit)){
                //if in the list already, return without adding
                return;
            }
        }
        Hits.add(hit);
    }

    // searching a url for the hits
    // might through an IOException
    public void search(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        int curr = 9;
        for(int i = 0; i < 49; i++){
                try {
                    Element HitsEL = document.select("div.x-text").get(curr);
                    String text = HitsEL.text();
                    parse(text);
                }catch (Exception ignored) {}
            curr+=5;
        }
    }

    // Parse a string from the url containing the hits
    public void parse(String s){
        int curr = 1, start = 0, end = 0;
        while(curr <= 40){
            start = s.indexOf(" "+curr+" ", start);
            end = s.indexOf(" "+(curr+1)+" ", (start+3));
            try {
                if(end == -1) end = s.length();
                String s1 = s.substring(start, end);
                Hit hit = new Hit(s1.trim());
                add(hit);
            }catch (Exception ignored) {}
            start = end;
            curr++;
        }
    }

    // given an artist name, gives a list of all recommendations based on the collaborations
    public String[] getRecommendations(String artist){
        String found = "";
        String visited = "";
        ArrayList<String> queue = new ArrayList<>();
        queue.add(artist);
        String curr = "";
        while(queue.size() > 0){
            curr = queue.remove(0);
            visited+=","+curr;
            for(Hit s: Hits){
                if(s.getArtist().toLowerCase().contains(artist)){
                    for(String a: s.getCollaborations()){
                        if(!found.contains(a)){
                            if(found.length() > 0) found+=",";
                            found+=a;
                            if(!visited.contains(a)) queue.add(a);
                        }
                    }
                }
            }
        }


        return found.split(",");
    }

    public static void main(String[] args) throws IOException {
        SearchDriver driver = new SearchDriver();
        Scanner sc = new Scanner(System.in);

        boolean done = false;
        String input;
        while(!done){
            System.out.printf(
                    "1. List recommendation for an artist%n" +
                    "2. Exit program%n");
            input = sc.nextLine();
            if(input.equals("1")){
                System.out.println("Name of artist ?");
                String[] recommendations = driver.getRecommendations(sc.nextLine());
                System.out.println("Here is the list of Recommendations: ");
                for(String string: recommendations){
                    System.out.printf("%s%n", string);
                }
            }else if(input.equals("2")){
                done = true;
            }else{
                System.out.println("Invalid choice. Please try again");
            }
        }
    }
}
