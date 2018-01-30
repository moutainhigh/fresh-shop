package com.hafu365.fresh.core.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.*;

/**
* 功能：支付宝MD5签名处理核心文件，不需要修改
* 版本：3.3
* 修改日期：2012-08-17
* 说明：
* 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
* 该代码仅供学习和研究支付宝接口使用，只是提供一个
* */

public class MD5Encrypt {

    /**
     * 对字符串进行MD5签名
     * @param text 明文
     * @param charset 编码
     * @return 密文
     */
    public static String md5(String text, String charset) {
        return DigestUtils.md5Hex(getContentBytes(text,charset));
    }

    /**
     * 生成签名字符串
     * @param text 需要签名的字符串
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static String sign(String text, String key, String input_charset) {
    	text = text + key;
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }

    /**
     * 不排序把数组所有元素按照“参数1={参数1}&参数2={参数2}&……&参数n={参数n}”的模式用“&”字符拼接成字符串
     * @param params 参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        return createLinkString(params, false);
    }

    /**
     * 把数组所有元素排序，并按照“参数1={参数1}&参数2={参数2}&……&参数n={参数n}”的模式用“&”字符拼接成字符串
     *
     * @param params 参与字符拼接的参数组
     * @param isSort 是否排序
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params, boolean isSort) {
        List<String> keys = new ArrayList<String>(params.keySet());
        if (isSort) {
            Collections.sort(keys);
        }
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    /**
     * 把数组所有元素参数值相加
     * @param params 参与字符拼接的参数组
     * @param isSort 是否排序
     * @return 拼接后字符串
     */
    public static String createLinkStringSum(Map<String, String> params,boolean isSort) {
        List<String> keys = new ArrayList<String>(params.keySet());
        if(isSort){
            Collections.sort(keys);
        }
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            prestr = prestr + value;
        }
        return prestr;
    }

    /**
     * 除去数组中的空值
     * @param sArray 签名参数组
     * @return 去掉空值参数后的新签名参数组
     */
    public static Map<String, String> paraFilterNone(Map<String, String> sArray) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
    /**
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")
            		|| key.equalsIgnoreCase("signMsg")|| key.equalsIgnoreCase("signType")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * 生成签名结果
     * @param sPara 要签名的数组
     * @param key 要签名的key
     * @return 签名结果字符串
     */
    public static String buildRequestMysign(Map<String, String> sPara,String key, String input_charset) {
        sPara = paraFilter(sPara);   //除去数组中的空值和签名参数
        String prestr = createLinkString(sPara,true); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        System.out.println("签名字符串：" + prestr);
        String mysign = "";
        mysign = MD5Encrypt.sign(prestr, key, input_charset);
        System.out.println("签名：" + mysign);
        return mysign;
    }

    /**
     * 微信签名
	 * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 */
	public static  String createSign(SortedMap<String, String> params,String key, String input_charset) {
		StringBuffer sb = new StringBuffer();
		Set es = params.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + key);
		String sign = MD5Encrypt.sign(sb.toString(), key, input_charset)
				.toUpperCase();
		return sign;

	}

    /**
     * 验证签名字符串
     * @param text 需要签名的字符串
     * @param sign 签名结果
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String key, String input_charset) {
    	text = text + key;
    	String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
    	if(mysign.equals(sign)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    public static void main(String[] args) {
        //System.out.println(sign("哈福"+System.currentTimeMillis(), "onlinepay", "utf-8"));
        //System.out.println(sign("body=哈福订单:201503251335451237&buyer_email=18175707822&buyer_id=2088112392627469&discount=0.00&extra_common_param=MID^2020506831060484|NURL^http://www.hafu365.com|CID^ALIPAY|CURL^http://www.hafu365.com&gmt_create=2015-03-25 17:44:51&gmt_payment=2015-03-25 17:45:02&is_total_fee_adjust=N&notify_id=2ac02e86e727d62ce091e3010cd087954k&notify_time=2015-03-25 17:45:02&notify_type=trade_status_sync&out_trade_no=201503251335451237&payment_type=1&price=0.01&quantity=1&seller_email=hafu365@163.com&seller_id=2088411556471332&subject=哈福订单:201503251335451237&total_fee=0.01&trade_no=2015032500001000460047848792&trade_status=TRADE_SUCCESS&use_coupon=N", "3taad8xsnwdufh9kuj01x61m8k25c83n", "utf-8"));
        //System.out.println(verify("body=哈福订单:201503251335451237&buyer_email=18175707822&buyer_id=2088112392627469&discount=0.00&extra_common_param=MID^2020506831060484|NURL^http://www.hafu365.com|CID^ALIPAY|CURL^http://www.hafu365.com&gmt_create=2015-03-25 17:44:51&gmt_payment=2015-03-25 17:45:02&is_total_fee_adjust=N&notify_id=2ac02e86e727d62ce091e3010cd087954k&notify_time=2015-03-25 17:45:02&notify_type=trade_status_sync&out_trade_no=201503251335451237&payment_type=1&price=0.01&quantity=1&seller_email=hafu365@163.com&seller_id=2088411556471332&subject=哈福订单:201503251335451237&total_fee=0.01&trade_no=2015032500001000460047848792&trade_status=TRADE_SUCCESS&use_coupon=N", "9d92671cfa0b042441392f73c77492f8", "3taad8xsnwdufh9kuj01x61m8k25c83n", "utf-8"));

    	/*Map<String, String> map = new HashMap<String, String>();*/
    	/*map.put("version", "v1.0");
    	map.put("merchantPartner", "2020506831060484");
    	map.put("outTradeSN", "SN20150908141115");
    	map.put("requestTime", "20150908141115");
    	map.put("totalFee", "1");
    	map.put("channelId", "101");
    	map.put("payType", "0");
    	map.put("bankCode", null);
    	map.put("factPayOID", "1441692684747057");
    	map.put("channelTradeNo", "2015090821001004460066508307");
    	map.put("payTime", "20150908141223");
    	map.put("payStatus", "1");
    	map.put("signType", "0");
    	map.put("signMsg", "d0e1abbac356eda10cfc9e11be856b9c");*/

    	/*map.put("version", "v1.0");
    	map.put("merchantPartner", "2020506831060484");
    	map.put("outTradeSN", "150908180133883160");
    	map.put("requestTime", "20150908180142");
    	map.put("totalFee", "1");
    	map.put("channelId", "100");
    	map.put("payType", "1");
    	map.put("bankCode", "ceb");
    	map.put("factPayOID", "1441706498197004");
    	map.put("channelTradeNo", "201509081801423480");
    	map.put("payTime", "20150908180210");
    	map.put("payStatus", "1");
    	map.put("signType", "0");
    	map.put("signMsg", "e69c2dd4a3356876f047a5ae6ac80283");

    	map = paraFilter(map);   //除去数组中的空值和签名参数
        String prestr = createLinkString(map,true); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
    	System.out.println(prestr);
    	System.out.println(buildRequestMysign(map, "8373d7456c2274ef607d218787505f4a", "utf-8"));*/
    	//System.out.println(sign("channelId=101&channelTradeNo=2015090821001004160037031061&factPayOID=1441682499536050&merchantPartner=2020506831060484&outTradeSN=150908112031242272&payStatus=1&payTime=20150908112221&payType=0&requestTime=20150908112053&totalFee=1&version=v1.0", "8373d7456c2274ef607d218787505f4a", "utf-8"));
    }
    /**
     * 字典排序
     * @param
     * @param
     * @return
     * @throws Exception
     */
    /*public static String FormatBizQueryParaMap(Map<String, String> paraMap,
                                               boolean urlencode) throws Exception {
        String buff = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(paraMap.entrySet());
            Collections.sort(infoIds,
                    new Comparator<Map.Entry<String,String>>() {
                        public int compare(Map.Entry<String, String> o1,
                                           Map.Entry<String, String> o2) {
                            return (o1.getKey()).toString().compareTo(
                                    o2.getKey());
                        }
                    });
            for (int i = 0; i < infoIds.size(); i++) {
                Map.Entry<String, String> item = infoIds.get(i);
                //System.out.println(item.getKey());
                if (item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlencode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    buff += key + "=" + val + "&";
                }
            }
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return buff;
    }*/

    //转为XML格式
    public static String ArrayToXml(Map<String, String> arr) {
        String xml = "<xml>";
        Iterator<Map.Entry<String, String>> iter = arr.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if (IsNumeric(val)) {
                xml += "<" + key + ">" + val + "</" + key + ">";
            } else
                xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
        }
        xml += "</xml>";
        return xml;
    }

    public static boolean IsNumeric(String str) {
        if (str.matches("\\d *")) {
            return true;
        } else {
            return false;
        }
    }

    //解析XML
    public static Map<String, String> doXMLParse(String xml)
            throws  IOException {

        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

        Map<String, String> map = null;

//        XmlPullParser pullParser = XmlPullParserFactory.newInstance()
//                .newPullParser();
//
//        pullParser.setInput(inputStream, "UTF-8");// 为xml设置要解析的xml数据
//
//        int eventType = pullParser.getEventType();
//
//        while (eventType != XmlPullParser.END_DOCUMENT) {
//            switch (eventType) {
//                case XmlPullParser.START_DOCUMENT:
//                    map = new HashMap<String, String>();
//                    break;
//
//                case XmlPullParser.START_TAG:
//                    String key = pullParser.getName();
//                    if (key.equals("xml"))
//                        break;
//                    String value = pullParser.nextText();
//                    map.put(key, value);
//                    break;
//                case XmlPullParser.END_TAG:
//                    break;
//            }
//            eventType = pullParser.next();
//        }
        return map;
    }

    /**
     * 发送https请求
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return 返回微信服务器响应的信息
     */
    /*public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            //conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce) {
//          log.error("连接超时：{}", ce);
        } catch (Exception e) {
//          log.error("https请求异常：{}", e);
        }
        return null;
    }*/

    /**
     * @Description：sign签名
     * @param characterEncoding 编码格式
     * @param parameters 请求参数
     * @return
     */
    /*public static String createSign1(String characterEncoding,SortedMap<String,String> parameters,String key){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key="+key);
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }*/


}