import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;

public final class WrapperBootstrap {
    private static final String EXPECTED_SHA256 = "81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f";
    private static final URI OFFICIAL_URI = URI.create(
        "https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradle/wrapper/gradle-wrapper.jar"
    );
    private static final long MAX_DOWNLOAD_BYTES = 2L * 1024L * 1024L;

    private WrapperBootstrap() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: java WrapperBootstrap.java <wrapper-directory>");
        }

        Path wrapperDirectory = Path.of(args[0]).toAbsolutePath().normalize();
        Files.createDirectories(wrapperDirectory);
        Path target = wrapperDirectory.resolve("gradle-wrapper.jar");

        if (Files.isRegularFile(target) && EXPECTED_SHA256.equals(sha256(target))) {
            return;
        }

        Path temporary = Files.createTempFile(wrapperDirectory, "gradle-wrapper-", ".jar.tmp");
        try {
            download(temporary);
            String actual = sha256(temporary);
            if (!EXPECTED_SHA256.equals(actual)) {
                throw new SecurityException(
                    "Gradle wrapper checksum mismatch. Expected " + EXPECTED_SHA256 + ", got " + actual
                );
            }
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            System.out.println("[Reqestra] Verified Gradle 8.13 wrapper installed: " + target);
        } catch (Exception error) {
            Files.deleteIfExists(temporary);
            throw error;
        }
    }

    private static void download(Path destination) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();
        HttpRequest request = HttpRequest.newBuilder(OFFICIAL_URI)
            .timeout(Duration.ofSeconds(60))
            .header("User-Agent", "Reqestra-Gradle-Wrapper-Bootstrap/1.0")
            .GET()
            .build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            try (InputStream ignored = response.body()) {
                throw new IOException("Unable to download Gradle wrapper: HTTP " + response.statusCode());
            }
        }

        try (InputStream input = response.body(); var output = Files.newOutputStream(destination)) {
            byte[] buffer = new byte[16 * 1024];
            long total = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > MAX_DOWNLOAD_BYTES) {
                    throw new IOException("Gradle wrapper download exceeded the 2 MiB safety limit.");
                }
                output.write(buffer, 0, read);
            }
        }
    }

    private static String sha256(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream input = Files.newInputStream(path)) {
            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
