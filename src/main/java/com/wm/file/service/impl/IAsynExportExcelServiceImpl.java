package com.wm.file.service.impl;

import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.wm.file.entity.MsgClient;
import com.wm.file.service.IAsynExportExcelService;
import com.wm.file.util.MyExcelExportUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: IAsynExportExcelServiceImpl
 * @Description:
 * @Author: WM
 * @Date: 2021-08-06 20:06
 **/
@Service
public class IAsynExportExcelServiceImpl implements IAsynExportExcelService {

    // 假定数据量是40万，因为我系统的最大线程数是8，数据量设置太大容易内存溢出
    public static final long DATA_TOTAL_COUNT = 400000;
    // 查询要导出的批次数据
    static List<Object> list = new ArrayList<>();

    static {
        for (int i = 0; i < DATA_TOTAL_COUNT; i++) {  //模拟库中一百万数据量
            MsgClient client = new MsgClient();
            client.setBirthday(new Date());
            client.setClientName("小明xxxsxsxsxsxsxsxsxsxsx" + i);
            client.setClientPhone("18797" + i);
            client.setCreateBy("JueYue");
            client.setId("1" + i);
            client.setRemark("测试" + i);
            list.add(client);
        }
    }

    @Resource
    private MyExcelExportUtil myExcelExportUtil;

    @Override
    @Async("taskExecutor")
    public void excuteAsyncTask(Map<String, Object> map, CountDownLatch cdl) {
        long start = System.currentTimeMillis();
        int currentPage = (int) map.get("page");
        int pageSize = (int) map.get("limit");
        List subList = new ArrayList(page(list, pageSize, currentPage));
        int count = subList.size();
        System.out.println("线程：" + Thread.currentThread().getName() + " , 读取数据，耗时 ：" + (System.currentTimeMillis() - start) + "ms");
        StringBuilder filePath = new StringBuilder(map.get("path").toString());
        filePath.append("线程").append(Thread.currentThread().getName()).append("-")
                .append("页码").append(map.get("page")).append(".xlsx");
        // 调用导出的文件方法
        Workbook workbook = myExcelExportUtil.getWorkbook("计算机一班学生", "学生", MsgClient.class, subList, ExcelType.XSSF);
        File file = new File(filePath.toString());
        MyExcelExportUtil.exportExcel2(workbook, file);
        long end = System.currentTimeMillis();
        System.out.println("线程：" + Thread.currentThread().getName() + " , 导出excel" + map.get("page") + ".xlsx成功 , 导出数据：" + count + " ,耗时 ：" + (end - start) + "ms");
        // 执行完线程数减1
        cdl.countDown();
        System.out.println("剩余任务数  ===========================> " + cdl.getCount());
    }

    // 手动分页方法
    public List page(List list, int pageSize, int page) {
        int totalcount = list.size();
        int pagecount = 0;
        int m = totalcount % pageSize;
        if (m > 0) {
            pagecount = totalcount / pageSize + 1;
        } else {
            pagecount = totalcount / pageSize;
        }
        List<Integer> subList = new ArrayList<>();
        if (pagecount < page) {
            return subList;
        }

        if (m == 0) {
            subList = list.subList((page - 1) * pageSize, pageSize * (page));
        } else {
            if (page == pagecount) {
                subList = list.subList((page - 1) * pageSize, totalcount);
            } else {
                subList = list.subList((page - 1) * pageSize, pageSize * (page));
            }
        }
        return subList;
    }
}
