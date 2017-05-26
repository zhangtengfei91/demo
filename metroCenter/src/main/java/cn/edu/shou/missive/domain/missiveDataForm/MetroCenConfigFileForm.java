package cn.edu.shou.missive.domain.missiveDataForm;

import cn.edu.shou.missive.domain.missiveDataForm.BaseEntityForm;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by shou on 2015/4/23.
 */
public class MetroCenConfigFileForm extends BaseEntityForm {
    @Setter
    @Getter
    private String modelContent;//模板内容
    @Setter @Getter
    private long modelId;//证书模板Id
    @Setter @Getter
    private long parentModelId;//证书模板Id
    @Getter @Setter
    private int nextPage;//是否需要显示续页
    @Getter @Setter
    private int pageHeight;//续页高度



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

}
