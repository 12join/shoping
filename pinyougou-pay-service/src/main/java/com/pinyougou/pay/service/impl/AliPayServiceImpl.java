package com.pinyougou.pay.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.pinyougou.pay.service.AliPayService;
import org.springframework.beans.factory.annotation.Value;

@Service(timeout = 5000)
public class AliPayServiceImpl implements AliPayService {


    @Value("${private_key}")
    private String private_key;

    @Value("${appid}")
    private String appid;

    @Value("${alipay_public_key}")
    private String alipay_public_key;

    // 支付宝当面付2.0服务
    private static AlipayTradeService   tradeService;

    private static Log log = LogFactory.getLog(AliPayServiceImpl.class);


    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = out_trade_no;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "品优购支付宝支付";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = Double.valueOf(total_fee)/100+"";

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "共支付"+Double.valueOf(total_fee)/100+"元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance(out_trade_no, "品优购精品", Long.valueOf(total_fee), 1);
        // 创建好一个商品后添加至商品明细列表
        //goodsDetailList.add(goods1);



        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://7nnc5i.natappfree.cc/pay/alipayCallBack.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zhifubao.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zhifubao.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        Map map = new HashMap<>();//新建一个map用来返回给前端，这里面只封装前端需要的信息
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);


                map.put("out_trade_no", out_trade_no);
                map.put("total_fee", total_fee);
                map.put("code_url", response.getQrCode());//用来生成二维码的支付地址

                // 需要修改为运行机器上的路径
//                String filePath = String.format("/Users/sudo/Desktop/qr-%s.png",
//                    response.getOutTradeNo());
//                log.info("filePath:" + filePath);
                //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                return map;

            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }


        return null;
    }

    @Override
    public Map queryOrderStatus(String out_trade_no) {
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
        String outTradeNo = out_trade_no;

        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                .setOutTradeNo(outTradeNo);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        Map map = new HashMap<>();
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");

                AlipayTradeQueryResponse response = result.getResponse();
                dumpResponse(response);

                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }

                map.put("trade_state", response.getTradeStatus());
                map.put("transaction_id", response.getTradeNo());
                return map;

            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        map.put("trade_state", "");
        map.put("transaction_id", "");
        return map;
    }

    /**
     * 点击取消订单时调用此方法
     * @param out_trade_no 支付订单号
     * @param trade_no 交易流水号
     * @return
     */
    @Override
    public boolean closePay(String out_trade_no,String trade_no) {

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",appid,private_key,"json","GBK",alipay_public_key,"RSA2");
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizContent("{" +
                "\"trade_no\":"+trade_no+"," +
                "\"out_trade_no\":"+out_trade_no+"," +
                "\"operator_id\":\"test_operator_id\"" +
                "  }");
        //观察json是否有格式问题
        System.out.println(request.getBizContent());
        AlipayTradeCloseResponse response = null;
        try {
            response = alipayClient.execute(request);

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){

            System.out.println("调用成功");
            return true;
        } else {
            System.out.println("调用失败");
            return false;
        }

    }


    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

}
