package com.white.hot.doremember.function.FileEncryption;

import com.white.hot.doremember.base.BaseApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 使用系统Javax包中Cipher类进行加密解密的工具（这里使用AES对称加密方式）
 * Created by zhangshuo on 2016/6/28.
 */
public class CipherUtil {

    private static byte[] seed;
    private static final String ENCRYPTION_SUFFIX = ".dat";

    private static byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException {
        File f = new File(BaseApplication.getGlobalContext().getFilesDir(), "/m3.dat");
        if(f.exists()){
            return decryptSeed();
        }
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG"); // 获得一个随机数，传入的参数为默认方式。
        sr.setSeed(seed);  // 设置一个种子，这个种子一般是用户设定的密码。也可以是其它某个固定的字符串
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");  // 获得一个key生成器（AES加密模式）
        keyGen.init(128, sr);      // 设置密匙长度128位
        SecretKey key = keyGen.generateKey();  // 获得密匙
        byte[] raw = key.getEncoded();   // 返回密匙的byte数组供加解密使用
        byte[] saveSeed;
        saveSeed = Arrays.copyOf(raw, raw.length);
        encryptSeed(saveSeed);

        return raw;
    }

    private static void encryptSeed(byte[] raw) {
        //与0xF3异或
        for (int i = 0; i < raw.length; i++) {
            raw[i] = (byte) (raw[i] ^ 0xF3);
        }
        byte temp;
        //翻转一半
        for(int i=0; i < raw.length / 4; i++){
            temp = raw[raw.length/2 - 1 -i];
            raw[raw.length/2 -1 -i] = raw[i];
            raw[i] = temp;
        }
        File f = BaseApplication.getGlobalContext().getFilesDir();
        byteArrayToFile(raw, f.getAbsolutePath()+"/m3.dat");
    }

    private static byte[] decryptSeed(){
        File f = new File(BaseApplication.getGlobalContext().getFilesDir(), "/m3.dat");
        byte[] raw = fileToByteArray(f);
        //与0xF3异或
        for (int i = 0; i < raw.length; i++) {
            raw[i] = (byte) (raw[i] ^ 0xF3);
        }
        byte temp;
        //翻转一半
        for(int i=0; i < raw.length / 4; i++){
            temp = raw[raw.length/2 - 1 -i];
            raw[raw.length/2 -1 -i] = raw[i];
            raw[i] = temp;
        }
        return raw;
    }

    public static byte[] encry(String pwd, byte[] input) throws Exception {  // 加密
        SecretKeySpec keySpec = new SecretKeySpec(getRawKey(pwd.getBytes()), "AES"); // 根据上一步生成的密匙指定一个密匙（密匙二次加密？）
        Cipher cipher = Cipher.getInstance("AES");  // 获得Cypher实例对象
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);  // 初始化模式为加密模式，并指定密匙
        byte[] encode = cipher.doFinal(input);  // 执行加密操作。 input为需要加密的byte数组
        return encode;                         // 返回加密后的密文（byte数组)
    }

    /***
     * 文件加密
     * @param pwd 种子密码
     * @param f   待加密的文件
     * @return
     * @throws Exception
     */
    public static File encry(String pwd, File f) throws Exception {  // 加密

        byte[] input = fileToByteArray(f);
        SecretKeySpec keySpec = new SecretKeySpec(getRawKey(pwd.getBytes()), "AES"); // 根据上一步生成的密匙指定一个密匙（密匙二次加密？）
        Cipher cipher = Cipher.getInstance("AES");      // 获得Cypher实例对象
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);      // 初始化模式为加密模式，并指定密匙
        byte[] encode = cipher.doFinal(input);          // 执行加密操作。 input为需要加密的byte数组, 返回加密后的密文（byte数组)
        String oldPath = f.getAbsolutePath();           //待加密的文件路径
        String newPath = oldPath.substring(0, oldPath.lastIndexOf(".")) + ENCRYPTION_SUFFIX;         //新文件的路径
        return byteArrayToFile(encode, newPath);
    }

    public static byte[] decry(byte[] encode) throws Exception { // 解密
        if (!new File(BaseApplication.getGlobalContext().getFilesDir(), "/m3.dat").exists()) {
            throw new Exception("Can't decrept before encypt");
        }else{
            SecretKeySpec keySpec = new SecretKeySpec(getRawKey(null), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);   // 解密的的方法差不多，只是这里的模式不一样
            byte[] decode = cipher.doFinal(encode);  // 加解密都通过doFinal方法来执行最终的实际操作
            return decode;
        }
    }

    /***
     * 文件解密
     *
     * @param f          被加密过的文件
     * @param fileSuffix 解密后的文件类型 如  ".jpg" , 注意：需要带点
     * @return 返回解密过后的文件
     * @throws Exception
     */
    public static File decry(File f, String fileSuffix) throws Exception { // 解密
        if (!new File(BaseApplication.getGlobalContext().getFilesDir(), "/m3.dat").exists()) {
            throw new Exception("Can't decrept before encypt");
        }else{
            byte[] encode = fileToByteArray(f);
            SecretKeySpec keySpec = new SecretKeySpec(getRawKey(null), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);   // 解密的的方法差不多，只是这里的模式不一样
            byte[] decode = cipher.doFinal(encode);  // 加解密都通过doFinal方法来执行最终的实际操作
            String oldPath = f.getAbsolutePath();           //待加密的文件路径
            String newPath = oldPath.substring(0, oldPath.lastIndexOf(".")) + fileSuffix;         //新文件的路径
            return byteArrayToFile(decode, newPath);
        }
    }

    public static String decryptString(String seed, byte[] encode) throws Exception {
//        byte[] raw = getRawKey(seed.getBytes());
        if (seed == null) {
            throw new Exception("Can't decrept before encypt");
        }
        byte[] decode = decry(encode);
        return new String(decode);
    }

    public static byte[] encryptString(String pwd, String input) throws Exception {
//        byte[] raw = getRawKey(seed.getBytes());
        if (seed == null) {
            seed = getRawKey(pwd.getBytes());
        }
        byte[] encode = encry(pwd, input.getBytes());
// return new String(encode);  // 加密后的byte数组转换成字符串时将出现问题。
        return encode;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static byte[] fileToByteArray(File f) {
        if (f == null || (!f.exists())) {
            return null;
        }
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            int readCount;
            while ((readCount = fis.read(buffer)) > 0) {
                baos.write(buffer, 0, readCount);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fis.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File byteArrayToFile(byte[] datas, String outFilePath) {

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        File f = new File(outFilePath);
        try {
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
            bos.write(datas);
            bos.flush();
            return f;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
