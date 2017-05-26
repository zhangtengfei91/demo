package cn.edu.shou.missive.domain.missiveDataForm;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhengxl on 15/7/16.
 */
public class MetroCenCertificateModelForm extends BaseEntityForm  {
    @Getter
    @Setter
    private String modelName;//模板名称
    @Getter @Setter
    private String modelPicName;//模板图片名称
    @Getter @Setter
    private int nextPage;//是否需要显示续页
    @Getter @Setter
    private int pageHeight;//续页高度
    @Getter @Setter
    private long parentId;//父节点
    @Getter @Setter
    private String modelCode;//模板代码
    @Getter @Setter
    public String lab;  //实验室

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelPicName() {
        return modelPicName;
    }

    public void setModelPicName(String modelPicName) {
        this.modelPicName = modelPicName;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }
}
