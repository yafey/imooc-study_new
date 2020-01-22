package com.yafey.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yafey.dto.FileInfo;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    private static final String FOLDER = "D:\\_SoftData\\_git_repositorys\\imooc-study_new_c134\\clazz\\134_Spring-Security-RESTful\\C134_imooc-security-demo-browser\\upload\\";

    @PostMapping
    public FileInfo upload(MultipartFile file) throws IOException {
        log.info("file name:{},origin name:{},size:{}",file.getName(),file.getOriginalFilename(),file.getSize());

        String filePath = String.format("%s%s.%s", FOLDER, System.currentTimeMillis(), file.getOriginalFilename().split("[.]")[1]);

        File localFile = new File(filePath);
        file.transferTo(localFile);

        return new FileInfo(localFile.getPath());
    }
    
    
    @GetMapping("{id}")
    public void download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {

        String filePath = String.format("%s%s.%s", FOLDER, id, "txt");
        File file = new File(filePath);
        log.info(file.getAbsolutePath());

        try (InputStream inputStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {

            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=" + file.getName());

            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();

        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        }
    }
}
