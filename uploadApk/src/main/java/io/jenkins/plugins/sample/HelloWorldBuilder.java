package io.jenkins.plugins.sample;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    // 什么List
    private List<ApkPath> apkPathList = new ArrayList<>();

    private final String name;
    private final String packageid;
    private final String minversion;
    private boolean useFrench;

    // 通过注解 DataBoundConstructor 来获取config.jelly  控件内容
    @DataBoundConstructor
    public HelloWorldBuilder(String name,String packageid,String minversion) {
        this.name = name;
        this.packageid = packageid;
        this.minversion = minversion;
    }
    // 通过get方法获取到内容,我们就可以在构建的时候执行我们的输入框里面的内容
    public String getName() {
        return name;
    }

    public String getPackageid() {
        return packageid;
    }

    public String getMinversion() {
        return minversion;
    }



    public boolean isUseFrench() {
        return useFrench;
    }

    @DataBoundSetter
    public void setUseFrench(boolean useFrench) {
        this.useFrench = useFrench;
    }


    // 执行构建的是时候,perform 将被调用
    // run 包括我们的构建状态 成功还是失败 构建时间 构建次数等之类的信息
    // workspace 工作空间
    // Launcher参数用于启动构建
    // listener 用来给我们控制台打印信息
    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        // launcher 可以帮我们执行Linux命令
        //  launcher.launch().stdout(listener).cmds("pwd").start();

        if (useFrench) {
            listener.getLogger().println("Bonjour, " + name + "!");
        } else {
            listener.getLogger().println("Hello, " + name + "!"+workspace);
        }

        listener.getLogger().println("当前获取到的参数有:"+name+"=="+packageid+"=="+minversion);

        // 本地死路径 拼接我们的 workspace 这个就是我们的默认路径
        String path =workspace + "/app/build/outputs/apk";
        listener.getLogger().println("开始执行:"+path);
        // 调用我们的方法
        getApk(listener,path);
        // 文件上传

    }


    // 目前我们的需求是在当前文件夹下面我们构建完成apk以后,需要插件将我们的apk上传到我们开发的后台
    // 首先我们定义方法叫 getaPK, 方法里面我们定义一个参数 String path : path 告诉我们去哪里进行遍历
    // Jenkins构建的时候默认是在项目的根目录下面
    public void getApk(TaskListener listener,String path){

        // 在构建之前情况列表 遍历apk列表
        apkPathList.clear();

        // 首先第一步: 实例化路径
        File filename = new File(path);
        // 首先判断当前路径存在不存在,如果不存在就不往下执行,直接return,顺便打印日志
        if(!filename.exists()){

            // 打印日志去控制台
            listener.getLogger().println("当前遍历的路径不存在"+path);

            return;
        }

        // 如果存在,那我们就进行遍历
        // 遍历前首先获取当前路径下面有多少文件夹:
        File file[] = filename.listFiles();

        // 首先判断 如果为空或者长度为0,那我们就return
        if(null == file || file.length == 0){

            // 打印日志去控制台
            listener.getLogger().println("当前路径下没有需要遍历的文件夹,说明没有在之前调用打包apk的命令");

            return;
        }


        // 如果有文件夹,那我们进行for循环,同时进行遍历递归
        // File 代表遍历的类型
        // file1 变量名字
        // file 遍历的那个对象
        for (File file1 : file){


            // 进行判断,如果文文件夹,那我们调用本地继续往里面遍历,如果是文件那我们输出路径
            if(file1.isDirectory()){

                // 调用方法本地,继续向下遍历
                getApk(listener,file1.getAbsolutePath());
            }else {

                // 获取文件名字
                String namePath = file1.getAbsolutePath();
                // 获取我们的后缀名字 .apk .json
                String lastName = namePath.substring(namePath.lastIndexOf(".")+1);


                // 获取路径 打印名字到控制台
                listener.getLogger().println("当前文件是:"+namePath);
                listener.getLogger().println("当前后缀名字是:"+lastName);


                // 进行判断:
                if(lastName.equals("json")){

                    // 调用解析的
                    // 首先第一步使用io流读取文件
                    getJson(listener,namePath);


                }


            }
        }


        // 递归执行完成在这里进行上传


    }


    // 是因io流读取文件同时进行解析
    public void getJson(TaskListener listener,String path){


        // 实例化一个对象
        ApkPath apkPath = new ApkPath();

        StringBuffer sb = new StringBuffer();
        //初始化读取操作的br对象
        BufferedReader br = null;

        try {
            //调用字节流,设置读取文件的编码格式
            InputStreamReader ir = new InputStreamReader(new FileInputStream(path), "UTF-8");

            br = new BufferedReader(ir);

            //格式固定,对象名len可以自拟
            String len = null;
            while ((len = br.readLine()) != null) {
                //逐行输出
                System.out.println("....." + len);

                sb.append(len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 打印去读内容
        String json = sb.toString();
        // 打印读取的json串
        listener.getLogger().println("当前读取到的json串:"+json);
        // 向内规因

        // 对sb字符串进行解析
        Gson gson = new Gson();
        // 进行解析
        List<DetailApk> detailApkList = gson.fromJson(json,new TypeToken<List<DetailApk>>(){}.getType());
        // 设置属性
        apkPath.setDetailApk(detailApkList.get(0));
        // 设置我们apk的路径
        apkPath.setApkPath(path.substring(0,path.lastIndexOf(".")-6)+detailApkList.get(0).getPath());

        // 最后加入到list
        apkPathList.add(apkPath);

        // 打印遍历结果
        listener.getLogger().println("当前遍历的文件长度是:"+apkPathList.size());

        // 定义上传路径
        String url = "http://47.110.143.159:8080/ssmLogin/user/apkUpload";

        // 使用for循环调用上传方法
        for (ApkPath apkPath1 : apkPathList){

            UploadUtils.uploadFile(listener,url,apkPath1,name);
        }

    }


    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        //做检测用的 比如输入的内容合法不合法
        public FormValidation doCheckName(@QueryParameter String value, @QueryParameter boolean useFrench)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            if (value.length() < 4)
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_tooShort());
            if (!useFrench && value.matches(".*[éáàç].*")) {
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_reallyFrench());
            }
            return FormValidation.ok();
        }

        // 表示构建起是不是可以构建各种类型的项目
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {

            return true;
        }

        // 这个方法名字一般显示在我控件的上面
        // title="${%Name}" 显示在控件上面就是Name
        @Override
        public String getDisplayName() {

            // 这里回显示在我们的控件上边
            return "upload apk to managerapk";
        }

    }

}
