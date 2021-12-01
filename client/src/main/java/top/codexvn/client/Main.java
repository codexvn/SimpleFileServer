package top.codexvn.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import top.codexvn.vo.JsonView;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import static top.codexvn.client.Util.bindArgs;

public class Main {
    private final static String PREFIX = "http://localhost.codexvn.top:8090";
    private final static String GET_META_INFO, GET_FILE, UPLOAD_FILE;
    private final static ObjectMapper objectMapper = new ObjectMapper();
    static {
        GET_META_INFO = PREFIX +"/file/getMetaInfo";
        GET_FILE =PREFIX +"/file/getFile";
        UPLOAD_FILE = PREFIX +"/file/uploadFile";
    }

    /**
     *
     * @param uuid 文件对应的uuid
     * @return 文件元数据Map,获取失败返回null
     */
    public static Map<String, String> getMetaInfo(String uuid) {
        try {
            URL target = bindArgs(GET_META_INFO, Map.of("uuid", uuid));
            HttpURLConnection urlConnection = (HttpURLConnection) target.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() != 200) {
                throw new IOException();
            }
            try (InputStream inputStream = urlConnection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
            ) {
                byte[] buffer = new byte[8192];
                int readSize;
                while ((readSize = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readSize);
                }
                JsonView<Map<String, String>> jsonView = (JsonView<Map<String, String>>) objectMapper.readValue(outputStream.toByteArray(), JsonView.class);
                return jsonView.getResult();
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param uuid     文件对应的uuid
     * @param savePath 文件保存位置
     * @return uuid对应文件的文件名, 获取失败返回null
     */
    public static String downloadFile(String uuid, Path savePath) {
        try {
            URL target = bindArgs(GET_FILE, Map.of("uuid", uuid));
            HttpURLConnection urlConnection = (HttpURLConnection) target.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() != 200) {
                throw new IOException();
            }
            String fileName = URLDecoder.decode(urlConnection.getHeaderField("Content-Disposition").substring(29), StandardCharsets.UTF_8);

            try (InputStream inputStream = urlConnection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(savePath.resolve(fileName).toFile())
            ) {
                byte[] buffer = new byte[8192];
                int readSize;
                while ((readSize = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readSize);
                }
            }
            return fileName;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 给定文件路径，上传到文件服务器
     *
     * @param path 需要上传的文件路径
     * @return 文件对应的uuid
     */
    @SneakyThrows
    public static String uploadFile(Path path) {
        try {
            String mimeType = Files.probeContentType(path);
            String boundry = UUID.randomUUID().toString().replace("-", "");
            File file = path.toFile();
            URL target = new URL(UPLOAD_FILE);
            HttpURLConnection urlConnection = (HttpURLConnection) target.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            //设置post类型为multipart/form-data
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundry);
            //以form-data格式传递数据
            try (OutputStream outputStream = urlConnection.getOutputStream();
                 BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                StringBuilder content = new StringBuilder();
                content.append("--").append(boundry).append("\r\n");
                content.append(String.format("Content-Disposition: form-data; name=\"file\"; filename=\"%s\"\r\n", file.getName()));
                content.append("Content-Type: ").append(mimeType).append("\r\n\r\n");
                outputStream.write(content.toString().getBytes(StandardCharsets.UTF_8));
                byte[] buffer = new byte[8192];
                int readSize;
                while ((readSize = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readSize);
                }
                outputStream.write(("\r\n--" + boundry + "--\r\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
            urlConnection.connect();
            if (urlConnection.getResponseCode() != 200) {
                throw new IOException("请求出错");
            }
            try (InputStream inputStream = urlConnection.getInputStream()) {
                JsonView<String> jsonView = (JsonView<String>) objectMapper.readValue(inputStream, JsonView.class);
                return jsonView.getResult();
            }
        } catch (IOException e) {
            return null;
        }
    }
}
