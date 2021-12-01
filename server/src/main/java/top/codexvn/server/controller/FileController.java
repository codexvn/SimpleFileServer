package top.codexvn.server.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import top.codexvn.po.FileStore;
import top.codexvn.server.mapper.FileStoreMapper;
import top.codexvn.server.service.FileStoreService;
import top.codexvn.vo.FileStoreVo;
import top.codexvn.vo.JsonView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@RestController
@RequestMapping("file")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "简单文件服务器")
public class FileController {
    private final FileStoreService fileStoreService;
    private final FileStoreMapper fileStoreMapper;
    @PostMapping(value = "uploadFile", consumes = "multipart/form-data")
    @ApiImplicitParam(name = "file",value = "multipart/form-data 格式文件数据")
    @ApiOperation(value = "上传文件")
    public JsonView<Object> uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        JsonView<Object> result = null;
        file.getSize();
        file.getContentType();
        file.getOriginalFilename();
        try {
            String uuid = fileStoreService.saveFile(file);
            result = JsonView.getSuccessJsonView(uuid);
        } catch (IOException e) {
            log.error("IOException:", e);
            result = JsonView.getErrorJsonView();
        }
        return result;
    }

    @GetMapping(value = "getFile")
    @ApiImplicitParam(name = "uuid",value = "文件对应uuid")
    @ApiOperation(value = "通过uuid下载文件")
    public void getFile(@RequestParam String uuid, HttpServletResponse response) {
        FileStore fileStore = fileStoreMapper.getFileStoreByUUID(uuid);
        //数据库中不存在uuid对应的文件抛出410异常
        if(fileStore==null) throw new ResponseStatusException(HttpStatus.GONE);
        String encode = URLEncoder.encode(fileStore.getFileName(), StandardCharsets.UTF_8);
        //指定浏览器下载时的文件名
        String format = String.format("attachment; filename*=utf-8''%s", encode);
        response.setHeader("Content-Disposition", format);
        try {
            fileStoreService.outputFileToResponse(Path.of(new URI(fileStore.getUrl())) ,response);
        }catch (URISyntaxException e){}
    }
    @GetMapping(value = "getMetaInfo")
    @ApiImplicitParam(name = "uuid",value = "文件对应uuid")
    @ApiOperation(value = "通过uuid获取文件元数据")
    public JsonView<FileStoreVo> getMetaInfo(@RequestParam String uuid ) {
        FileStore fileStore = fileStoreMapper.getFileStoreByUUID(uuid);
        return JsonView.getSuccessJsonView(new FileStoreVo(fileStore));
    }
}
