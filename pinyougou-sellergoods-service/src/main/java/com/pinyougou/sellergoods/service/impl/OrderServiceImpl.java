package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.OrderService;
import entity.PageResult;
import entity.Result;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        orderMapper.insert(order);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        TbOrderExample.Criteria criteria = example.createCriteria();

        if (order != null) {
            if(order.getOrderId() != null && order.getOrderId()>0){
                criteria.andOrderIdEqualTo( order.getOrderId());
            }
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusEqualTo(order.getStatus());
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeEqualTo( order.getSourceType() );
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Override
    public void createExcel(List<TbOrder> orderList) {

        String s = downUserList(orderList);
        System.out.println(s);
    }


    private String[] paymentTypeList={"","在线支付","货到付款"};//支付类型，1、在线支付，2、货到付款
    private String[] sourceTypeList={"","app","pc","m端","微信","手机qq"};//订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
    private String[] statusList={"","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"};//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价

    /**
     * 创建excel
     *
     * @param orderList 是需要写入excel中的数据
     * @return
     */
    private XSSFWorkbook createUserListExcel(List<TbOrder> orderList) {
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
                row.createCell(0).setCellValue(""+orderList.get(i).getOrderId());
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

    private String csvFile = "D:\\excel";

    /**
     * 用户列表导出
     */
    private String downUserList(List<TbOrder> orderList) {
        // getTime()是一个返回当前时间的字符串，用于做文件名称
        String name = new Date().getTime() + "";
        //  csvFile是我的一个路径，自行设置就行
        String ys = csvFile + "\\" + name + ".xlsx";
        // 1.生成Excel
        XSSFWorkbook userListExcel = createUserListExcel(orderList);
        try {
            // 输出成文件
            File file = new File(csvFile);
            if (file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }
            // TODO 生成的wb对象传输
            FileOutputStream outputStream = new FileOutputStream(new File(ys));
            userListExcel.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

}
