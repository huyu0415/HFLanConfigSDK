package com.huyu.lanconfig_hf.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteUtils {

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式\n 如："2B44EFD9" to byte[]{0x2B, 0×44,
	 * 0xEF,0xD9}
	 *
	 * @param src
	 *            String 传入的字符串
	 * @return byte[] 返回的数组
	 */
	public static byte[] HexString2Bytes(String src) {
		int leng = src.length() / 2;
		byte[] ret = new byte[leng];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < leng; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF" to 0xEF
	 *
	 * @param src0
	 *            ASCII字符1
	 * @param src1
	 *            ASCII字符2
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 *
	 * @param hint
	 *            标签
	 * @param b
	 *            需要打印的数组
	 */
	public static void printHexString(String hint, byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			Log.i(hint,hex.toUpperCase() + " ");
		}
		Log.i(hint,"设备状态数据为空");
	}

	/**
	 * 将指定byte数组转换为16进制的形式
	 *
	 * @param b
	 *            传入的数组
	 * @return String
	 */
	public static String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			ret += hex.toUpperCase() + " ";
		}
		return ret;
	}

	/**
	 * 将单个字节数据转成16进制字符串
	 * 如：-1  ==》  FF
	 */
	public static String Byte2HexString(byte b) {
		String hex = Integer.toHexString(b & 0xFF);
		if (hex.length() == 1) {
			hex = "0" + hex;
		}
		return hex.toUpperCase();
	}

	/**
	 * 将指定int转换为16进制的形式
	 *
	 * @param i
	 *            指定的整数
	 * @return String
	 */
	public static String int2HaxString(int i) {
		String hex = Integer.toHexString(i);
		if (hex.length() == 1) {
			hex = "0" + hex;
		}
		return hex;
	}

	/**
	 * 讲指定的short按位取值
	 *
	 * @param n
	 *            指定的字节
	 * @param index
	 *            位数下标
	 * @return boolean
	 * */
	public static boolean getBitFromShort(int n, int index) {
		if (((n >> index) & 0x1) > 0)
			return true;
		else
			return false;
	}

	/**
	 * 十六进制转8位二进制
	 * @param hexString
	 * @return
	 */
	public static String hexString2binaryString(String hexString)
	{
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++)
		{
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(hexString
					.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;

	}

	/**
	 *
	 * @return 将二进制转换为十六进制字符输出
	 */
	public static String b2h(String binary) {
		// 这里还可以做些判断，比如传进来的数字是否都是0和1
		int length = binary.length();
		int temp = length % 4;
		// 每四位2进制数字对应一位16进制数字
		// 补足4位
		if (temp != 0) {
			for (int i = 0; i < 4 - temp; i++) {
				binary = "0" + binary;
			}
		}
		// 重新计算长度
		length = binary.length();
		StringBuilder sb = new StringBuilder();
		// 每4个二进制数为一组进行计算
		for (int i = 0; i < length / 4; i++) {
			int num = 0;
			// 将4个二进制数转成整数
			for (int j = i * 4; j < i * 4 + 4; j++) {
				num <<= 1;// 左移
				num |= (binary.charAt(j) - '0');// 或运算
			}
			sb.append(hexStr[num]);
		}
		return sb.toString();
	}

	public static String[] hexStr = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
			"9", "A", "B", "C", "D", "E", "F" };

	/**十进制 转成 8位二进制*/
	public static String int2Erjinzhi(int x){
		String str="";
		while(x!=0){
			int s=x/2;
			int y=x%2;
			str +=y;
			x=s;
		}
		String t="";
		for(int i=7/*str.length()-1*/;i>=0;i--){
			if(i>str.length()-1){
				t+=0;
			}else{
				t+=str.charAt(i);
			}
		}
		return t;
	}

	/**十六进制字符串转成十进制int*/
	public static int hex2int(String str){
		int hi = Integer.parseInt(str, 16);
		return hi;
	}

	/**判断字符串是否由 0-9 A-F 组成    如果能够匹配则返回true。*/
	public static Boolean isstring(String str){
		Boolean bl = false;
		//首先,使用Pattern解释要使用的正则表达式，其中^表是字符串的开始，$表示字符串的结尾。
		Pattern pt = Pattern.compile("^[0-9a-fA-F_]+$");
		//然后使用Matcher来对比目标字符串与上面解释得结果
		Matcher mt = pt.matcher(str);
		//如果能够匹配则返回true。实际上还有一种方法mt.find()，某些时候，可能不是比对单一的一个字符串，
		//可能是一组，那如果只要求其中一个字符串符合要求就可以用find方法了.
		if(mt.matches()){
			bl = true;
		}
		return bl;
	}




}
