package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenClient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/1/15.
 */
public interface MetroCenClientRepository extends PagingAndSortingRepository<MetroCenClient,Long> {

    public MetroCenClient findById(long id);

    public List<MetroCenClient>findAll();

    //根据合同号查找客户信息
    @Query("select client from MetroCenClient client where client.contractNo like:look")
    public List<MetroCenClient>getClientInfoByLook(@Param("look")String look);

    //根据单位名称查找客户信息
    @Query("select client from MetroCenClient client where client.unitName like:look")
    public List<MetroCenClient>getClientUnitNameByLook(@Param("look")String look);
}
