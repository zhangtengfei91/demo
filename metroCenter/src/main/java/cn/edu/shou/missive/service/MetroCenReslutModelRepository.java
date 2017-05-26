package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenReslutModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/3/17.
 */
public interface MetroCenReslutModelRepository extends PagingAndSortingRepository<MetroCenReslutModel,Long> {

    List<MetroCenReslutModel>findAll();
    //根据modelId获取结果模板信息
    @Query("select resultModel from MetroCenReslutModel resultModel where resultModel.modelId=:modelId")
    public  MetroCenReslutModel getResultModelByModelId(@Param("modelId")long modelId);
}
