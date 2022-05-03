package com.wm.file;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.wm.file.entity.MsgClient;
import com.wm.file.util.AsynExcelExportUtil;
import com.wm.file.util.MyExcelExportUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName:FileController
 * @Description:
 * @Author:Deamer
 * @Date:2020/6/15 22:42
 **/
@RestController
public class FileController {

    @Resource
    private MyExcelExportUtil myExcelExportUtil;
    @Resource
    private IExcelExportServer exportBigExcel;
    @Resource
    private AsynExcelExportUtil asynExcelExportUtil;

    @GetMapping(value = "/asynExport")
    public void asynExportData(HttpServletResponse response) throws InterruptedException {
        asynExcelExportUtil.threadExcel(response);
        //1000000-39511ms 100000-7750ms 10000-789ms
    }

    /**
     * 普通数据量导出(修改表格宽度测试)
     */
    @GetMapping(value = "/export2")
    public void test2() {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {  //测试数据
            MsgClient client = new MsgClient();
            client.setBirthday(new Date());
            client.setClientName("小明xxxsxsxsxsxsxsxsxsxsx" + i);
            client.setClientPhone("18797" + i);
            client.setCreateBy("JueYue");
            client.setId("1" + i);
            client.setRemark("测试" + i);
            list.add(client);
        }
        try {
            long start = System.currentTimeMillis();
            Workbook workbook = myExcelExportUtil.getWorkbook("计算机一班学生", "学生", MsgClient.class, list, ExcelType.XSSF);
            String filePath = "C:\\Users\\Administrator\\Desktop\\export\\exportAll.xlsx";
            File file = new File(filePath);
            MyExcelExportUtil.exportExcel2(workbook, file);
            long end = System.currentTimeMillis();
            System.out.println("任务执行完毕共消耗：  " + (end - start) + "ms");
            //1000000-69407ms  100000-3518ms 10000-1310ms
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 大数据量导出测试
     *
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "/bigDataExport")
    public void bigDataExport(HttpServletResponse response) throws IOException {
        Date start = new Date();
        ExportParams params = new ExportParams("大数据测试", "测试");
        Workbook workbook = ExcelExportUtil.exportBigExcel(params, MsgClient.class, exportBigExcel, new Object());
        MyExcelExportUtil.exportExcel(workbook, String.valueOf(System.currentTimeMillis()), response);
        System.out.println("bigDataExport:" + (new Date().getTime() - start.getTime()));//10000-bigDataExport:2278 100000-bigDataExport:19083 1000000-bigDataExport:693672
    }

    /**
     * 普通数据量导出
     *
     * @param response
     */
    @GetMapping(value = "/export")
    public void test(HttpServletResponse response) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {  //一百万数据量
            MsgClient client = new MsgClient();
            client.setBirthday(new Date());
            client.setClientName("小明xxxsxsxsxsxsxsxsxsxsx" + i);
            client.setClientPhone("18797" + i);
            client.setCreateBy("JueYue");
            client.setId("1" + i);
            client.setRemark("测试" + i);
            list.add(client);
        }
        try {
            Date start = new Date();
            Workbook workbook = myExcelExportUtil.getWorkbook("计算机一班学生", "学生", MsgClient.class, list, ExcelType.XSSF);
            MyExcelExportUtil.exportExcel(workbook, String.valueOf(System.currentTimeMillis()), response);
            System.out.println("export:" + (new Date().getTime() - start.getTime()));//10000-export:1208 100000-export:4516 1000000-export:329188
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
