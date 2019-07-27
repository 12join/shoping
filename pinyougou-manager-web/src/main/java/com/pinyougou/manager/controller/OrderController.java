package com.pinyougou.manager.controller;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;


import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.order.service.OrderService;
import entity.PageResult;
import entity.Result;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbOrder> findAll() {
        return orderService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return orderService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param order
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbOrder order) {
        try {
            orderService.add(order);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param order
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbOrder order) {
        try {
            orderService.update(order);
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
    public TbOrder findOne(Long id) {
        return orderService.findOne(id);
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
            orderService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbOrder order, int page, int rows) {
        System.out.println(order.getReceiver());
        System.out.println(order.getReceiverMobile());
        return orderService.findPage(order, page, rows);
    }


    private String csvFile = "src\\main\\webapp\\admin\\excel";

    /**
     * 导出excel报表
     *
     * @return
     */
    @RequestMapping("/createExcel")
    public Result createExcel(@RequestBody List<TbOrder> list) {
        String path = System.getProperty("user.dir");
//        System.out.println(path);
        System.out.println("createOrderExcel...");

        try {
            XSSFWorkbook wb = createOrderListExcel(list);
            // getTime()是一个返回当前时间的字符串，用于做文件名称
            String name ="order"+ new Date().getTime() + "";
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

    private String[] paymentTypeList = {"", "在线支付", "货到付款"};//支付类型，1、在线支付，2、货到付款
    private String[] sourceTypeList = {"", "app", "pc", "m端", "微信", "手机qq"};//订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
    private String[] statusList = {"", "未付款", "已付款", "未发货", "已发货", "交易成功", "交易关闭", "待评价","已完成"};//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价

    /**
     * 创建excel
     *
     * @param orderList 是需要写入excel中的数据
     * @return
     */
    private XSSFWorkbook createOrderListExcel(List<TbOrder> orderList) {
        // 1.创建HSSFWorkbook，一个HSSFWorkbook对应一个Excel文件
        XSSFWorkbook wb = new XSSFWorkbook();
        // 2.在workbook中添加一个sheet,对应Excel文件中的sheet
        XSSFSheet sheet = wb.createSheet("订单编号");

        // 3.设置表头，即每个列的列名
        String[] titel = {"用户账号", "收货人", "手机号", "订单金额", "支付方式", "订单来源", "订单状态"};
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
        for (int i = 0; i < orderList.size(); i++) {
            // 创建行
            row = sheet.createRow(i + 1);
            // 订单编号
            if (orderList.get(i).getOrderId() != null) {
                row.createCell(0).setCellValue("" + orderList.get(i).getOrderId());
//                XSSFCell orderIdCell = row.createCell(0);
//                //设置单元格类型为文本格式
//                orderIdCell.setCellType(XSSFCell.CELL_TYPE_STRING);
//                orderIdCell.setCellValue(orderList.get(i).getOrderId());
            }
            if (orderList.get(i).getUserId() != null) {
                // 用户账号
                row.createCell(1).setCellValue(orderList.get(i).getUserId());
                sheet.autoSizeColumn(1, true);
            }
            if (orderList.get(i).getReceiver() != null) {
                // 收货人
                row.createCell(2).setCellValue(orderList.get(i).getReceiver());
            }
            if (orderList.get(i).getReceiverMobile() != null) {
                // 手机号
                row.createCell(3).setCellValue(orderList.get(i).getReceiverMobile());
            }

            if (orderList.get(i).getPayment() != null) {
                // 订单金额
                row.createCell(4).setCellValue(orderList.get(i).getPayment().doubleValue());
            }
            if (orderList.get(i).getPaymentType() != null) {
                // 支付方式
                row.createCell(5).setCellValue(paymentTypeList[Integer.parseInt(orderList.get(i).getPaymentType())]);
            }
            if (orderList.get(i).getSourceType() != null) {
                // 订单来源
                row.createCell(6).setCellValue(sourceTypeList[Integer.parseInt(orderList.get(i).getSourceType())]);
            }
            if (orderList.get(i).getStatus() != null) {
                // 订单状态
                row.createCell(7).setCellValue(statusList[Integer.parseInt(orderList.get(i).getStatus())]);
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
//            sheet.setColumnWidth(3, 8000);
        }
        return wb;
    }


	@Reference
	private OrderService orderService;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(){
		return orderService.findAll();
	}


	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){
		return orderService.findPage(page, rows);
	}

	/**
	 * 增加
	 * @param order
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbOrder order){
		try {
			orderService.add(order);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 * @param order
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrder order){
		try {
			orderService.update(order);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbOrder findOne(Long id){
		return orderService.findOne(id);
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			orderService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

		/**
	 * 查询+分页
	 * @param order
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbOrder order, int page, int rows  ){
		return orderService.findPage(order, page, rows);
	}







    /**
     * 查询订单状态
     * @param start
     * @return
     */
    @RequestMapping("/selectOrderStatus")
	public List selectOrderStatus(Date start, Date end){

        System.out.println(start);
        System.out.println(end);

        int number1=0;
        int number2=0;
        int number3=0;
        int number4=0;
        int number5=0;

        List list=new ArrayList();
        List<TbOrder> tbOrders = orderService.selectOrderStatus(start, end);
        for (TbOrder tbOrder : tbOrders) {
            if ("1".equals(tbOrder.getStatus())){
                number1++;
            }
            if ("2".equals(tbOrder.getStatus())){
                number2++;
            }
            if ("3".equals(tbOrder.getStatus())){
                number3++;
            }
            if ("4".equals(tbOrder.getStatus())){
                number4++;
            }
            if ("5".equals(tbOrder.getStatus())){
                number5++;
            }
        }
        list.add(number1);
        list.add(number2);
        list.add(number3);
        list.add(number4);
        list.add(number5);
        System.out.println(list);
        return list;
    }


}
