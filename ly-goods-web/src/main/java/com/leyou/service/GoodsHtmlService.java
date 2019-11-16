package com.leyou.service;

import com.leyou.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @Author: TianCi.Xiong
 * @Description: 整合Thymeleaf静态化商品详情页
 * @Date: Created in 2019-11-12 13:46
 */
@Service
public class GoodsHtmlService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TemplateEngine templateEngine;

    private static final Logger logger = LoggerFactory.getLogger(GoodsHtmlService.class);

    /**
     * 创建html页面
     *
     * @param spuId
     */
    public void createHtml(Long spuId) {
        PrintWriter writer = null;
        try {
            // 获取页面数据
            Map<String, Object> spuMap = this.goodsService.loadModel(spuId);

            // 创建thymeleaf上下文对象
            Context context = new Context();
            // 把数据放入上下文对象
            context.setVariables(spuMap);

            // 创建输出流
            File file = new File("D:\\JAVA\\nginx-1.12.2\\html\\leyou\\" + spuId + ".html");
            writer = new PrintWriter(file);

            // 执行页面静态化方法
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            logger.error("页面静态化出错：{}，" + e, spuId);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 新建线程处理页面静态化
     *
     * @param spuId
     */
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(() -> createHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }

    /**
     * 删除页面
     *
     * @param id
     */
    public void deleteHtml(Long id) {
        File file = new File("D:\\JAVA\\nginx-1.12.2\\html\\leyou\\", id + ".html");
        file.deleteOnExit();
    }
}
