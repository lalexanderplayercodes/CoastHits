import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;


import java.io.IOException;
import java.time.Duration;

public class Main {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("http://top40weekly.com").get();
        String page_content = document.text();
        System.out.println(page_content);

    }
