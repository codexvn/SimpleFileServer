package top.codexvn.server.mapper;


import org.apache.ibatis.annotations.Param;
import top.codexvn.po.FileStore;

public interface FileStoreMapper {
    public FileStore getFileStoreByUUID(@Param("UUID") String UUID);
    public int addFileStoreItem(FileStore fileStore);
}
