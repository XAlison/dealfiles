package com.dealfiles.dealfiles.controller;

import com.dealfiles.dealfiles.bean.FileEntity;
import com.dealfiles.dealfiles.utils.FileUploadTool;
import com.dealfiles.dealfiles.utils.ZipFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/upload")
public class UploadController {

    @RequestMapping(value = "/uploadFile")
    public void uploadFile(String Id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        {
            Map<String, String> urlsMap = new HashMap<String, String>();
            int photoCount = 1;
            String[] list = {"b.jpg", "a.jpg"};
            for (String file_name : list) {
                //签到图片文件名：用户名+考勤时间+序号
                String userName = file_name.substring(0, file_name.lastIndexOf("."));
                String rootUrl = "static/files/".concat(file_name);
                urlsMap.put(userName, rootUrl);
                photoCount++;
            }
            String zipFileName = UUID.randomUUID().toString() + "test";    //zip包名
            ZipFile.bulkPackedFiles(zipFileName, urlsMap, request, response);    //打zip包
        }
    }

    //处理文件上传
    @RequestMapping(value = "/testuploadimg", method = RequestMethod.POST)
    public @ResponseBody
    String uploadImg(@RequestParam("file") MultipartFile file,
                     HttpServletRequest request) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        /*System.out.println("fileName-->" + fileName);
        System.out.println("getContentType-->" + contentType);*/
        String filePath = "static/files/";
        try {
            uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            // TODO: handle exception
        }
        //返回json
        return "uploadimg success";
    }

    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + fileName);
        out.write(file);
        out.flush();
        out.close();
    }


    @RequestMapping(value = "/upload", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody
    String upload(@RequestParam("file") MultipartFile multipartFile,
                  HttpServletRequest request) {
        String message = "";
        FileEntity entity;
        FileUploadTool fileUploadTool = new FileUploadTool();
        try {
            entity = fileUploadTool.createFile(multipartFile, request);
            if (entity != null) {
//                service.saveFile(entity);
                message = "上传成功";

            } else {
                message = "上传失败";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}
