import java.io.*;
import java.net.*;

public class HangClient {

    public static String getWord(String urlToRead)
            throws  MalformedURLException, IOException {
        if (urlToRead == null || urlToRead.equals("")) urlToRead = "https://random-word-api.herokuapp.com/word?number=1";
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null;) {
                result.append(line);
            }
        }

        return result.toString();
    }
}
