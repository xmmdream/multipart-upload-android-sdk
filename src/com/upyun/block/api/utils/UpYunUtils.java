package com.upyun.block.api.utils;import java.io.File;import java.io.IOException;import java.io.InputStream;import java.security.MessageDigest;import java.security.NoSuchAlgorithmException;import java.util.Arrays;import java.util.HashMap;import org.apache.commons.codec.binary.Hex;import org.apache.commons.codec.digest.DigestUtils;import org.json.JSONObject;import com.upyun.block.api.common.Constant;import com.upyun.block.api.exception.UpYunException;/** * 工具类 *  * @author wangxiaolong */public class UpYunUtils {	/**	 * 计算policy	 * 	 * @param paramMap	 * @return	 * @throws UpYunException	 */	public static String getPolicy(HashMap<String, Object> paramMap)			throws UpYunException {		JSONObject obj = new JSONObject(paramMap);		return Base64Coder.encodeString(obj.toString());	}	/**	 * 计算签名	 * 	 * 表单API中，"signature" 用于校验返回数据的合法性，按照下面的规则计算生成： 1.	 * 将参数（"signature"参数不参与计算）键值对根据key的字典顺序排序后，连接成一个字符串； 2.	 * 将第一步生成的字符串，与"secretKey"连接，计算md5；	 * 	 * 上面说的"secretKey"的值有2种情况： --> token_secret(在分块上传第一步初始化请求的返回中获取)	 * 第一步初始化请求以及最后一步『直接返回』（没有指定return_url和notify_url）的时候使用 --> form_api_secret	 * 第二步分块请求以及最后一步在指定了return_url或者notify_url的情况下使用	 * 	 * ps:通常我们建议签名数据在服务器端生成，仅在手机端需要上传文件的时候，才从服务器端取得签名后的数据，以防止表单API验证密钥泄露出去。	 * 	 * @param paramMap	 * @param secretKey	 * @return	 */	public static String getSignature(HashMap<String, Object> paramMap,			String secretKey) {		Object[] keys = paramMap.keySet().toArray();		Arrays.sort(keys);		StringBuffer tmp = new StringBuffer("");		for (Object key : keys) {			tmp.append(key).append(paramMap.get(key));		}		tmp.append(secretKey);		return md5Hex(tmp.toString().getBytes());//		return DigestUtils.md5Hex(tmp.toString());//		String signature = signature(md5.toString());//		System.out.println(signature);//		return signature;	}		/**	 * android 中无法使用DigestUtils.md5Hex(data),这是替代方法	 * 	 * @return	 */	public static String md5Hex(InputStream stream){		try {			return new String(Hex.encodeHex(DigestUtils.md5(stream)));		} catch (IOException e) {			e.printStackTrace();		}		return null;	}		public static String md5Hex(byte[] data) {		return new String(Hex.encodeHex(DigestUtils.md5(data)));	}//	private static String signature(String source) {//		return md5(source.getBytes());//	}////	public static String md5(byte[] bytes) {//		try {//			MessageDigest md = MessageDigest.getInstance("MD5");////			md.reset();//			md.update(bytes);////			byte[] mdbytes = md.digest();////			StringBuffer hexString = new StringBuffer();//			for (int i = 0; i < mdbytes.length; i++) {//				String hex = Integer.toHexString(0xff & mdbytes[i]);//				if (hex.length() == 1)//					hexString.append('0');//				hexString.append(hex);//			}//			return hexString.toString();////		} catch (NoSuchAlgorithmException e) {//			e.printStackTrace();//		}//		return null;//	}////	public static String md5(InputStream in) {//		byte[] digest = null;//		try{//			MessageDigest digester = MessageDigest.getInstance("MD5");////			byte[] bytes = new byte[8192];//			int byteCount;//			while ((byteCount = in.read(bytes)) > 0) {//				digester.update(bytes, 0, byteCount);//			}//			digest = digester.digest();//		}catch(Exception e){//			e.printStackTrace();//		}//		//		return new String(digest).toString();//	}	/**	 * 计算分块数目	 * 	 * @param file	 * @param blockSize	 * @return	 * @throws UpYunException	 */	public static int getBlockNum(File file, int blockSize)			throws UpYunException {		int blockNum = 0;		if (blockSize < Constant.MIN_BLOCK_SIZE) {			throw new UpYunException("BlockSize should be at least "					+ Constant.MIN_BLOCK_SIZE);		}		int size = (int) file.length() % blockSize;		blockNum = (int) file.length() / blockSize;		if (size != 0) {			blockNum++;		}		return blockNum;	}}