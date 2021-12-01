package top.codexvn.client;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleTest {
    private  Path testFile = null;
    private  final String FILE_NAME = "123.txt";
    static {
        System.setProperty("proxyHost", "localhost");
        System.setProperty("proxyPort", "8888");
    }
    @BeforeAll
    @SneakyThrows
    public void beforeAll(){
        //生成用于测试的txt文件
        Path testFile = Path.of(System.getProperty("java.io.tmpdir"), FILE_NAME);
        this.testFile=testFile;
        Files.writeString(testFile,"test");
    }
    @Test
    void test(){

        //文件上传测试
        String uuid = Main.uploadFile(testFile);
        Assertions.assertNotNull(uuid);

        //获取元信息测试
        Assertions.assertNotNull( Main.getMetaInfo(uuid));

        //获取文件下载测试
        String fileName =  Main.downloadFile(uuid, Path.of(System.getProperty("java.io.tmpdir")));
        Assertions.assertNotNull(fileName);
        Assertions.assertTrue(Files.exists(Path.of(System.getProperty("java.io.tmpdir"), fileName)));
        Assertions.assertEquals(fileName,FILE_NAME);

    }
}
