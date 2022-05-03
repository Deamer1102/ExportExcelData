package com.wm.file.util;

import com.wm.file.service.IAsynExportExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import static com.wm.file.service.impl.IAsynExportExcelServiceImpl.DATA_TOTAL_COUNT;


/**
 * @ClassName:AsynExcelExportUtil
 * @Description: 多线程批量导出excel工具类
 * @Author:Deamer
 * @Date:2021/8/8 23:00
 **/
@Slf4j
@Component
public class AsynExcelExportUtil {

    // 定义导出的excel文件保存的路径
    private String filePath = "C:\\Users\\Deamer\\Desktop\\export\\";
    @Resource
    private IAsynExportExcelService asynExportExcelService;
    /**
     * 每批次处理的数据量
     */
    private static final int LIMIT = 40000;
    // Queue是java自己的队列，是同步安全的
    public static Queue<Map<String, Object>> queue;

    static {
        // 一个基于链接节点的无界线程安全的队列
        queue = new ConcurrentLinkedQueue<>();
    }

    /**
     * 多线程批量导出 excel
     *
     * @param response 用于浏览器下载
     * @throws InterruptedException
     */
    public void threadExcel(HttpServletResponse response) throws InterruptedException {
        long start = System.currentTimeMillis();
        initQueue();
        //异步转同步，等待所有线程都执行完毕返回 主线程才会结束
        try {
            CountDownLatch cdl = new CountDownLatch(queue.size());
            while (queue.size() > 0) {
                asynExportExcelService.excuteAsyncTask(queue.poll(), cdl);
            }
            cdl.await();
            System.out.println("excel导出完成·······················");
            //压缩文件
            File zipFile = new File(filePath.substring(0, filePath.length() - 1) + ".zip");
            FileOutputStream fos1 = new FileOutputStream(zipFile);
            //压缩文件目录
            ZipUtils.toZip(filePath, fos1, true);
            //发送zip包
            ZipUtils.sendZip(response, zipFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("任务执行完毕共消耗：  " + (end - start) + "ms");
    }

    /**
     * 初始化队列
     */
    public void initQueue() {
        long dataTotalCount = DATA_TOTAL_COUNT;// 数据的总数
        int listCount = (int) dataTotalCount;
        // 计算出多少页，即循环次数
        int count = listCount / LIMIT + (listCount % LIMIT > 0 ? 1 : 0);
        for (int i = 1; i <= count; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("page", i);
            map.put("limit", LIMIT);
            map.put("path", filePath);
            //添加元素
            queue.offer(map);
        }
    }
}
