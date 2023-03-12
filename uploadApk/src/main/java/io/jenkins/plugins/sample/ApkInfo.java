package io.jenkins.plugins.sample;

import java.util.List;

public class ApkInfo {

    private String type;
    private List<String> splits;
    private String versionCode;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSplits() {
        return splits;
    }

    public void setSplits(List<String> splits) {
        this.splits = splits;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
