package com.wm.file.service.impl;

import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.wm.file.entity.MsgClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: IExcelExportServerImpl
 * @Description: 查询指定页码的数据
 * @Author: WM
 * @Date: 2021-07-24 19:23
 **/
@Service
public class IExcelExportServerImpl implements IExcelExportServer {
    @Override
    public List<Object> selectListForExcelExport(Object obj, int page) {
        // 在数据库中查询出数据集合
        //此处可以写dao层分页查询的实现方法，page为当前第几页，分批次循环查询导入数据至excel中
        //obj:查询条件，page：当前第几页，pageSize：每页条数
        //List<Test> testList = dao.xxx(obj,page,pageSize);
        int pageSize = 10000;
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {  //测试数据
            MsgClient client = new MsgClient();
            client.setBirthday(new Date());
            client.setClientName("小明" + i);
            client.setClientPhone("18797" + i);
            client.setCreateBy("JueYue");
            client.setId("1" + i);
            client.setRemark("测试" + i);
            list.add(client);
        }
        List partList = new ArrayList();
        if (page * pageSize <= 100000) {
            partList = list.subList((page - 1) * pageSize, page * pageSize);
        }
        return partList;
    }
}
