package com.maxwang.buycar.controller;

import com.google.gson.Gson;
import com.maxwang.buycar.domain.ConstantQiniu;
import com.maxwang.buycar.redis.CarKey;
import com.maxwang.buycar.redis.RedisService;
import com.maxwang.buycar.util.KeyUtil;
import com.maxwang.buycar.util.Result;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;



import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class QiNiuController {

    @Autowired
    private ConstantQiniu constantQiniu;

    @Autowired
    private RedisService redisService;

    private static Logger logger= LoggerFactory.getLogger(QiNiuController.class);

    private static final String CAR_IMG="img";

    /**
     * 上传文件到七牛云存储
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @PostMapping("/qiniu")
    @ResponseBody
    public Result<Boolean> uploadImgQiniu(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
        String path = uploadQNImg(inputStream, KeyUtil.genUniqueKey()); // KeyUtil.genUniqueKey()生成图片的随机名
        logger.info(path);
        redisService.set(CarKey.carKey,CAR_IMG,path);
        return Result.success(true);
    }

    /**
     * 将图片上传到七牛云
     */
    private String uploadQNImg(FileInputStream file, String key) {
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone2());
        // 其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        // 生成上传凭证，然后准备上传

        try {
            Auth auth = Auth.create(constantQiniu.getAccessKey(), constantQiniu.getSecretKey());
            String upToken = auth.uploadToken(constantQiniu.getBucket());
            try {
                Response response = uploadManager.put(file, key, upToken, null, null);
                // 解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

                String returnPath = constantQiniu.getPath() + "/" + putRet.key;
                return returnPath;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
