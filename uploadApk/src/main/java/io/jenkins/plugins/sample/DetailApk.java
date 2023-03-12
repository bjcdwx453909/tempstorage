package io.jenkins.plugins.sample;

public class DetailApk {

    // 获取 apk路径
    private String path;
    private OutputType outputType;
    private ApkInfo apkInfo;
    private Properties properties;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    public ApkInfo getApkInfo() {
        return apkInfo;
    }

    public void setApkInfo(ApkInfo apkInfo) {
        this.apkInfo = apkInfo;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
