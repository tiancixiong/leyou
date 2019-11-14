package com.leyou.controller;

import com.leyou.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-02 16:19
 */
@RestController
@RequestMapping("upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;

    /**
     * 上传图片功能
     *
     * @param file
     * @return
     */
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 上传图片，获得地址
        String url = this.uploadService.upload(file);
        if (StringUtils.isBlank(url)) {
            // 上传失败
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // 上传成功，返回url
        return ResponseEntity.ok(url);
    }
}
