package com.hafu365.fresh.core.utils;
import java.io.*;

/**
 * 序列化工具
 * Created by SunHaiyang on 2017/6/22.
 */
public class SerializationUtils {

    /**
     * byte数组转换Hex字符串
     * @param byteArray byte数组
     * @return Hex字符串
     */
    private static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null){
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Hex字符串转换byte数组
     * @param str Hex字符串
     * @return byte数组
     */
    private static byte[] hexStrToByteArray(String str){
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    /**
     * 序列化
     * @param object 对象
     * @return hex字符串
     * @throws IOException IO流错误
     */
    public static String object2String(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return byteArrayToHexStr(bytes);
    }

    /**
     * 反序列化
     * @param str
     * @param tClass
     * @param <T>
     * @return

     */
    public static <T>T string2Object(String str,Class<T> tClass) throws IOException, ClassNotFoundException {
        byte[] bytes = hexStrToByteArray(str);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        return (T) object;
    }
}
