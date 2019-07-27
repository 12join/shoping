package com.pinyougou.manager.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.pinyougou.sellergoods.service.SeckillOrderService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.PageResult;
import entity.Result;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeckillOrder> findAll() {
        return seckillOrderService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return seckillOrderService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param seckillOrder
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.add(seckillOrder);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param seckillOrder
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.update(seckillOrder);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            seckillOrderService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param seckillOrder
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSeckillOrder seckillOrder, int page, int rows) {
        return seckillOrderService.findPage(seckillOrder, page, rows);
    }

    private String csvFile = "src\\main\\webapp\\admin\\excel";

    /**
     * 导出excel报表
     *
     * @return
     */
    @RequestMapping("/createExcel")
    public Result createExcel(@RequestBody List<TbSeckillOrder> list) {
        String path = System.getProperty("user.dir");
//        System.out.println(path);
        System.out.println("createOrderExcel...");

        try {
            XSSFWorkbook wb = createSecKillOrderListExcel(list);
            // getTime()是一个返回当前时间的字符串，用于做文件名称
            String name = "seckillorder" + new Date().getTime() + "";
            //  csvFile是我的一个路径，自行设置就行
            String ys = csvFile + "\\" + name + ".xlsx";
            System.out.println(ys);
            // 输出成文件
            File file = new File(csvFile);
            if (file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }
            // TODO 生成的wb对象传输
            FileOutputStream outputStream = new FileOutputStream(new File(ys));
            wb.write(outputStream);
            outputStream.close();
            return new Result(true, "excel\\" + name + ".xlsx");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "生成失败");
        }
    }

    /**
     * 删除excel
     * @param name
     */
    @RequestMapping("/deleteExcel")
    public void deleteExcel(final String name) {
        // 10000毫秒后执行
        Timer nTimer = new Timer();
        nTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                File f = new File("src\\main\\webapp\\admin\\"+name);
                System.out.println("删除:"+"  src\\main\\webapp\\admin\\"+name );
                f.delete();
            }
        },10000);
    }

    private String[] statusList = {"", "未付款", "已付款", "未发货", "已发货", "交易成功", "交易关闭", "待评价","已完成"};

    /**
     * 创建excel
     *
     * @param seckillOrderList 是需要写入excel中的数据
     * @return
     */
    private XSSFWorkbook createSecKillOrderListExcel(List<TbSeckillOrder> seckillOrderList) {
        // 1.创建HSSFWorkbook，一个HSSFWorkbook对应一个Excel文件
        XSSFWorkbook wb = new XSSFWorkbook();
        // 2.在workbook中添加一个sheet,对应Excel文件中的sheet
        XSSFSheet sheet = wb.createSheet("订单编号");

        // 3.设置表头，即每个列的列名
        String[] titel = {"用户账号", "收货人", "手机号", "订单金额", "订单状态", "交易流水"};
        // 3.1创建第一行
        XSSFRow row = sheet.createRow(0);
        // 此处创建一个序号列
        row.createCell(0).setCellValue("订单编号");
        // 将列名写入
        for (int i = 0; i < titel.length; i++) {
            // 给列写入数据,创建单元格，写入数据
            row.createCell(i + 1).setCellValue(titel[i]);
        }
        // 写入正式数据
        for (int i = 0; i < seckillOrderList.size(); i++) {
            // 创建行
            row = sheet.createRow(i + 1);
            // 订单编号
            if (seckillOrderList.get(i).getId() != null) {
                row.createCell(0).setCellValue("" + seckillOrderList.get(i).getId());
//                XSSFCell orderIdCell = row.createCell(0);
//                //设置单元格类型为文本格式
//                orderIdCell.setCellType(XSSFCell.CELL_TYPE_STRING);
//                orderIdCell.setCellValue(orderList.get(i).getOrderId());
            }
            if (seckillOrderList.get(i).getUserId() != null) {
                // 用户账号
                row.createCell(1).setCellValue(seckillOrderList.get(i).getUserId());
                sheet.autoSizeColumn(1, true);
            }
            if (seckillOrderList.get(i).getReceiver() != null) {
                // 收货人
                row.createCell(2).setCellValue(seckillOrderList.get(i).getReceiver());
            }
            if (seckillOrderList.get(i).getReceiverMobile() != null) {
                // 手机号
                row.createCell(3).setCellValue(seckillOrderList.get(i).getReceiverMobile());
            }

            if (seckillOrderList.get(i).getMoney() != null) {
                // 订单金额
                row.createCell(4).setCellValue(seckillOrderList.get(i).getMoney().doubleValue());
            }
            if (seckillOrderList.get(i).getStatus() != null) {
                // 订单状态
                row.createCell(5).setCellValue(statusList[Integer.parseInt(seckillOrderList.get(i).getStatus())]);
            }
            if (seckillOrderList.get(i).getTransactionId() != null) {
                // 交易流水
                row.createCell(6).setCellValue("" + seckillOrderList.get(i).getTransactionId());
            }
        }
        /**
         * 上面的操作已经是生成一个完整的文件了，只需要将生成的流转换成文件即可；
         * 下面的设置宽度可有可无，对整体影响不大
         */
        // 设置单元格宽度
        int curColWidth = 0;
        for (int i = 0; i <= titel.length; i++) {
            // 列自适应宽度，对于中文半角不友好，如果列内包含中文需要对包含中文的重新设置。
            sheet.autoSizeColumn(i, true);
            // 为每一列设置一个最小值，方便中文显示
            curColWidth = sheet.getColumnWidth(i);
            if (curColWidth < 2500) {
                sheet.setColumnWidth(i, 2500);
            }
            // 第3列文字较多，设置较大点。
            // sheet.setColumnWidth(3, 8000);
        }
        return wb;
    }

}
