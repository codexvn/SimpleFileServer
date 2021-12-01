package top.codexvn.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.codexvn.server.mapper.FileStoreMapper;
import top.codexvn.po.FileStore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

@Service
@RequiredArgsConstructor
public class FileStoreService implements ResourceLoaderAware {
    private final static DateTimeFormatter FILENAME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd");
    @Value("${CustomPath.save}")
    private String savePath;
    @Value("${CustomPath.datasource}")
    private String datasource;
    private final FileStoreMapper fileStoreMapper;

    /**
     *
     * @param file 文件数据
     * @return 文件对应的uuid
     */
    public String saveFile(MultipartFile file) throws IOException {
        FileStore fileStore = new FileStore();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Path localPath = storeFileToLocal(file, uuid);
        fileStore.setUUID(uuid);
        fileStore.setFileSize(file.getSize());
        fileStore.setFileType(file.getContentType());
        fileStore.setFileName(file.getOriginalFilename());
        long timestamp = System.currentTimeMillis();
        fileStore.setCreateTime(timestamp);
        fileStore.setUrl(localPath.toUri().toString());
        fileStoreMapper.addFileStoreItem(fileStore);
        return uuid;
    }

    /**
     *
     * @param file 文件数据
     * @param uuid 文件对应的uuid
     * @return 文件保存在本地的位置
     */
    private Path storeFileToLocal(MultipartFile file, String uuid) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        Path fileDir = Path.of(savePath, now.format(FILENAME_PATTERN));
        if(!Files.isDirectory(fileDir)){Files.createDirectories(fileDir);}
        Path localPath = fileDir.resolve(uuid);
        file.transferTo(localPath);
        return localPath;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        File datasourceFile = new File(datasource);
        if(!datasourceFile.exists()){
            Resource resource = resourceLoader.getResource(CLASSPATH_URL_PREFIX+"templates/sqlite.db");
            try {
                Path sqliteFilePath = resource.getFile().toPath();
                if(!Files.isDirectory(datasourceFile.toPath().getParent())){Files.createDirectories(datasourceFile.toPath().getParent());}
                Files.copy(sqliteFilePath,Path.of(datasource));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void outputFileToResponse(Path path,HttpServletResponse response){
        try (ServletOutputStream outputStream = response.getOutputStream();
             BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            byte[] buffer = new byte[8192];
            int readSize;
            while ((readSize = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
