package cn.edu.shou.missive.domain.missiveDataForm;

import cn.edu.shou.missive.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhengxl on 15/7/16.
 */
public class MetroCenReslutModelForm extends BaseEntity {
    @Setter
    @Getter
    private String modelContent;//模板内容
    @Setter @Getter
    private long modelId;//证书模板Id
    @Setter @Getter
    private long parentModelId;
    @Getter @Setter
    private int nextPage;//是否需要显示续页
    @Getter @Setter
    private int pageHeight;//续页高度
    @Setter @Getter
    private String remark1;//备用字段
    @Setter @Getter
    private String remark2;//备用字段
    @Setter @Getter
    private String remark3;//备用字段

    public String getModelContent() {
        return modelContent;
    }

    public void setModelContent(String modelContent) {
        this.modelContent = modelContent;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

    public long getParentModelId() {
        return parentModelId;
    }

    public void setParentModelId(long parentModelId) {
        this.parentModelId = parentModelId;
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

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }
}
