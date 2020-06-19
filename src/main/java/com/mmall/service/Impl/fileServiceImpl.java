package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.utility.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class fileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(fileServiceImpl.class);

    @Override
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg--->jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名{},上传的路径{},新文件名{}",fileName,path,uploadFileName);
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();//相对文件夹创建 mkdir当前文件夹
        }
        File targetFile = new File(path,uploadFileName);
        try{
            //springMVC上传文件
            file.transferTo(targetFile);
            //将targetFile上传到FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //上传完之后，删除upload下面的文件
            targetFile.delete();
        }catch(IOException e){
            logger.error("上传文件夹异常",e);
        }
        return targetFile.getName();
    }
}
