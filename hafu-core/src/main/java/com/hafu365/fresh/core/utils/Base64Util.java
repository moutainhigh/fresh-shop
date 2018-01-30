package com.hafu365.fresh.core.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;

/**
 * 采用 Base64进行加密解密处理
 * Created by zhaihuilin on 2017/9/5  14:09.
 */
public class Base64Util {

    public  static  void main(String args[]){
         Base64Util base64Util=new Base64Util();
         //加密
        String  S=  base64Util.getBase64("ILoveYou");
        System.out.println("----加密---:"+S);
       //解密
        String string=base64Util.getFromBase64(S);
        System.out.println("----解密---:"+string);

    }

      //加密
      public  String getBase64(String str){
           byte[] b=null;
           String s=null;
           try {
               b=str.getBytes("utf-8");
           }catch (UnsupportedEncodingException e){
               e.printStackTrace();
           }
           if (b !=null){
                s=new BASE64Encoder().encode(b);
           }
           return  s;
      }

      //解密
    public  String getFromBase64(String s){
           byte[] b =null;
           String result=null;
           if (s!=null){
               BASE64Decoder decoder=new BASE64Decoder();
                try {
                    b= decoder.decodeBuffer(s);
                    result=new String(b,"utf-8");
                }catch (Exception e){
                     e.printStackTrace();
                }
           }
           return result;
    }


}
