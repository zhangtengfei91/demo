package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by seky on 15/3/3.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenCertificateModel extends BaseEntity{

    @Getter @Setter
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
    private String lab;//实验室名称

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
}
