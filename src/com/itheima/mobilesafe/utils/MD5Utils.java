package com.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	/**
	 * md5���ܷ���
	 * 
	 * @param password
	 * @return
	 */
	public static String md5Password(String password) {

		try {
			// �õ�һ����ϢժҪ��
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			// ��ûһ��byte ��һ�������� 0xff;
			for (byte b : result) {
				// ������
				int number = b & 0xff;// ����
				String str = Integer.toHexString(number);
				// System.out.println(str);
				if (str.length() == 1) {
					buffer.append("0");
				}
				buffer.append(str);
			}

			// ��׼��md5���ܺ�Ľ��
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * ��ȡ�ļ���������MD5
	 * 
	 * @param sourceDir
	 */
	public static String getFileMd5(String sourceDir) {
		File file = new File(sourceDir);

		try {
			FileInputStream fis = new FileInputStream(file);

			int len = -1;
			byte[] buffer = new byte[1024];
			// ��ȡ������ժҪ
			MessageDigest digest = MessageDigest.getInstance("md5");
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			StringBuffer sb = new StringBuffer();
			// ��ûһ��byte ��һ�������� 0xff;
			for (byte b : result) {
				// ������
				int number = b & 0xff;// ����
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
		
	}

}
