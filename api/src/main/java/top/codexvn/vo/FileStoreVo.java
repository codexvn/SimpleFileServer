package top.codexvn.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.codexvn.po.FileStore;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class FileStoreVo {
    private final String UUID ;
    private final String fileSize;
    private final String fileType;
    private final String fileName;
    private final String createTime;
    public FileStoreVo(FileStore fileStore) {
        this.UUID = fileStore.getUUID();
        this.fileSize = String.format("%.2f kb",fileStore.getFileSize()/1024.0);
        this.fileType = fileStore.getFileType();
        this.fileName = fileStore.getFileName();
        LocalDateTime tmp = LocalDateTime.ofInstant(Instant.ofEpochMilli(fileStore.getCreateTime()), ZoneId.of("Asia/Shanghai"));
        this.createTime=tmp.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
