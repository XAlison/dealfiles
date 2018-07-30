package com.dealfiles.dealfiles.utils;


import org.apache.tools.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @ClassName: ZipFile
 * @Description: TODO
 * @Author: Administrator
 * @Date: 2018/7/27 11:34
 * @Version 1.0
 */
public class ZipFile {

    /**
     * 批量压缩问文件
     *
     * @param zipFileName 压缩包文件名
     * @param urlsMap     Map<String, String>-Map<文件重命名, 文件url>
     * @param request
     * @param response
     * @throws Exception
     */
    public static void bulkPackedFiles(String zipFileName, Map<String, String> urlsMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = zipFileName + ".zip";
        //在服务器端创建打包下载的临时文件
        String relativePath = "tmp/";        //保存视频及截图的目录
        String realPath = request.getSession().getServletContext().getRealPath("/");    //web项目部署的实际物理地址E:/tomcat/webapps/em/
        String directory = realPath + relativePath;                        //视频上传web临时文件夹

        String outFilePath = directory + fileName;    //将打包文件输出到服务器临时目录下，待用户下载完成后删除
        System.out.println("输出路径：" + outFilePath);

        File file = new File(outFilePath);
        //文件输出流
        FileOutputStream outStream = new FileOutputStream(file);
        //压缩流
        ZipOutputStream toClient = new ZipOutputStream(outStream);
        toClient.setEncoding("UTF-8");
        zipBatchFileByStream(urlsMap, toClient);
        toClient.close();
        outStream.close();
        downloadZip(file, response);
    }

    public static void bulkPackedFilesByStream(String zipFileName, Map<String, String> urlsMap) throws Exception {
        String fileName = zipFileName + ".zip";
        //在服务器端创建打包下载的临时文件
        String directory = "d://";                        //视频上传web临时文件夹

        String outFilePath = directory + fileName;    //将打包文件输出到服务器临时目录下，待用户下载完成后删除
        System.out.println("输出路径：" + outFilePath);

        File file = new File(outFilePath);
        //文件输出流
        FileOutputStream outStream = new FileOutputStream(file);
        //压缩流
        ZipOutputStream toClient = new ZipOutputStream(outStream);
        toClient.setEncoding("UTF-8");
        zipBatchFileByStream(urlsMap, toClient);
        toClient.close();
        outStream.close();
        //downloadZip(file, response);
    }

    /**
     * @Description: 压缩文件列表
     * @Author: xiewl
     * @param:
     * @Date: 2018/7/27 13:43
     * @Version 1.0
     */

    public static void zipBatchFileByStream(Map<String, String> urlsMap, ZipOutputStream outputStream) throws Exception {
        try {
            for (String tempFileName : urlsMap.keySet()) {
                String urlStr = urlsMap.get(tempFileName);
                // 生成文件名
                String fileExtension = urlStr.substring(urlStr.lastIndexOf("."));
                String fileName = tempFileName + fileExtension;
                File f = new File(urlStr);
                InputStream is = new FileInputStream(f);
                // 重命名文件，并写入到ZIP压缩包中
                zipFileByStream(fileName, is, outputStream);
                is.close();
            }
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * @Description: 文件写入zip文件
     * @Author: xiewl
     * @param:
     * @Date: 2018/7/27 13:41
     * @Version 1.0
     */
    public static void zipFileByStream(String fileName, InputStream is, ZipOutputStream outputStream) throws Exception {
        try {
            BufferedInputStream bInStream = new BufferedInputStream(is);
            org.apache.tools.zip.ZipEntry entry = new org.apache.tools.zip.ZipEntry(fileName);
            outputStream.putNextEntry(entry);
            byte[] buffer = new byte[1024];
            int r = 0;
            while ((r = bInStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, r);
            }
            // 文件名乱码
            outputStream.setEncoding("gbk");
            outputStream.closeEntry();
            // 关闭流
            bInStream.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * @Description: 下载导报文件
     * @Author: xiewl
     * @param:
     * @Date: 2018/7/27 13:39
     * @Version 1.0
     */
    public static void downloadZip(File file, HttpServletResponse response) {
        try {
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();

            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            file.delete();        //将生成的服务器端文件删除
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}