package com.wm.file.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: MsgClient
 * @Description:
 * @Author: WM
 * @Date: 2021-07-24 19:09
 **/
@Data
@ExcelTarget("msgClient")
public class MsgClient {
    @Excel(name = "客户姓名")
    private String clientName;
    @Excel(name = "客户电话号码")
    private String clientPhone;
    @Excel(name = "创建人")
    private String createBy;
    @Excel(name = "ID")
    private String id;
    @Excel(name = "标记")
    private String remark;
    @Excel(name = "生日")
    private Date birthday;
}
