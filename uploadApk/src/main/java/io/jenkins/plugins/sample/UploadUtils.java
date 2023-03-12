package io.jenkins.plugins.sample;

import hudson.model.TaskListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UploadUtils {



    public static void uploadFile(TaskListener listener, String url,ApkPath apkPath,String name) {


        listener.getLogger().println("开始进行网络请求:"+apkPath.getApkPath());


        Map<String,String> params = new HashMap<>();
        params.put("userId",name);
        params.put("packageId", apkPath.getDetailApk().getProperties().getPackageId());
        params.put("minSdkVersion", apkPath.getDetailApk().getProperties().getMinSdkVersion());
        params.put("versionCode", apkPath.getDetailApk().getApkInfo().getVersionCode());

        // 实例化file
        File file = new File(apkPath.getApkPath());

        // 换行，或者说是回车
        //final String newLine = "\r\n";
        final String newLine = "\r\n";
        // 固定的前缀
        final String preFix = "--";
        //final String preFix = "";
        // 分界线，就是上面提到的boundary，可以是任意字符串，建议写长一点，这里简单的写了一个#
        final String bounDary = "----WebKitFormBoundaryCXRtmcVNK0H70msG";
        //final String bounDary = "";
        //请求返回内容
        String output = "";

        try {
            //统一资源定位符
            URL uploadFileUrl = new URL(url);
            //打开http链接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) uploadFileUrl.openConnection();
            //设置是否向httpURLConnection输出
            httpURLConnection.setDoOutput(true);
            //设置请求方法默认为get
            httpURLConnection.setRequestMethod("POST");
            //Post请求不能使用缓存
            httpURLConnection.setUseCaches(false);
            //设置token
//            httpURLConnection.setRequestProperty("authorization", (String) GlobalContext.getSessionAttribute("token"));
            //为web端请求
//            httpURLConnection.setRequestProperty("os", "web");
            //从新设置请求内容类型
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Accept", "*/*");
            httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
            //application/json;charset=UTF-8 application/x-www-form-urlencoded
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + bounDary);
            httpURLConnection.setRequestProperty("User-Agent", "(Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.84 Safari/537.36)");


            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());

            //设置DataOutputStream设置DataOutputStream数据输出流
            //OutputStream outputStream = httpURLConnection.getOutputStream();

            //上传普通文本文件
            if (params.size() != 0 && params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    //获取参数名称和值
                    String key = entry.getKey();
                    String value = params.get(key);
                    //向请求中写分割线
                    dos.writeBytes(preFix + bounDary + newLine);
                    //向请求拼接参数
                    //String parm = key + "=" + URLEncoder.encode(value,"utf-8") +"\r\n" ;
                    dos.writeBytes("Content-Disposition: form-data; " + "name=\"" + key + "\"" + newLine);
                    //向请求中拼接空格
                    dos.writeBytes(newLine);
                    //写入值
                    dos.writeBytes(URLEncoder.encode(value, "utf-8"));
                    //dos.writeBytes(value);
                    //向请求中拼接空格
                    dos.writeBytes(newLine);
                }
            }

            //上传文件
            if (file != null && !params.isEmpty()) {
                //向请求中写分割线
                //把file装换成byte
                File del = new File(file.toURI());
                InputStream inputStream = new FileInputStream(del);
                byte[] bytes= input2byte(inputStream);
                String filePrams = "apkfile";
                String fileName = file.getName();
                //向请求中加入分隔符号
                dos.write((preFix + bounDary + newLine).getBytes());
                //将byte写入
                dos.writeBytes("Content-Disposition: form-data; " + "name=\"" + URLEncoder.encode(filePrams, "utf-8") + "\"" + "; filename=\"" + URLEncoder.encode(fileName, "utf-8") + "\"" + newLine);
                dos.writeBytes(newLine);
                dos.write(bytes);
                //向请求中拼接空格
                dos.writeBytes(newLine);
            }
            dos.writeBytes(preFix + bounDary + preFix + newLine);
            //请求完成后关闭流
            //得到相应码
            dos.flush();
            //判断请求没有成功
            if (httpURLConnection.getResponseCode() != 200) {
//                logger.error(url + "   请求异常，错误代码为：  " + httpURLConnection.getResponseCode());
                listener.getLogger().println("当前状态码是是:"+httpURLConnection.getResponseCode());
            }
            //判断请求成功
            if (httpURLConnection.getResponseCode() == 200) {
                //将服务器的数据转化返回到客户端
                InputStream inputStream = httpURLConnection.getInputStream();
                byte[] bytes = new byte[0];
                bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                output = new String(bytes);
                inputStream.close();
                System.out.println(output);
                listener.getLogger().println("当前上传文件的返回结果是:"+output);
            }
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(output);
            listener.getLogger().println("当前上传文件的返回结果是:"+e.toString());
        }

    }

         /**
          * 将输入流转化成字节流
          * @param inStream
          * @return
          * @throws IOException
          */
    public static final byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }
}
