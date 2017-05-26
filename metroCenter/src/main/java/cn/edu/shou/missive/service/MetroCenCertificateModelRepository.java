package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenCertificateModel;
import cn.edu.shou.missive.domain.MetroCenSampleMethod;
import cn.edu.shou.missive.domain.MetroCenTaskName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/3/3.
 */
public interface MetroCenCertificateModelRepository extends PagingAndSortingRepository<MetroCenCertificateModel,Long> {

    public List<MetroCenCertificateModel> findAll();

    @Query("select model from MetroCenCertificateModel model where model.parentId=:parentId")
    List<MetroCenCertificateModel>getParentModel(@Param("parentId")long parentId);

    @Query("select model from MetroCenCertificateModel model where model.parentId<>:parentId")
    List<MetroCenCertificateModel>getChildModel(@Param("parentId")long parentId);

    //根据parentId返回数据
    @Query("select model from MetroCenCertificateModel model where model.parentId=:parentId")
    public List<MetroCenCertificateModel>findByParentId(@Param("parentId")long parentId);
    //返回parentId不为0的数据
    @Query("select model from MetroCenCertificateModel model where model.parentId<>:parentId")
    public List<MetroCenCertificateModel>findChildModel(@Param("parentId")long parentId);

    //根据id返回modelCode
    @Query("select modelCode from MetroCenCertificateModel modelCode where modelCode.id=:modelCodeId")
    public MetroCenCertificateModel getModelCodeById(@Param("modelCodeId")long modelCodeId);

}
