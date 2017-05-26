package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenCertificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/3/4.
 */
public interface MetroCenCertificateRepository extends PagingAndSortingRepository<MetroCenCertificate,Long> {

    @Query("select certificate from MetroCenCertificate certificate")
    public MetroCenCertificate getAll();

    @Query("select certificate from MetroCenCertificate certificate where certificate.processId=:processId")
    public MetroCenCertificate getCertificateInfoByProcessId(@Param("processId")String processId);

    //根据用户ID获取该用户已出具的证书  郑小罗 20151123
    @Query("select certificate from MetroCenCertificate certificate where certificate.verified=:verifiedId order by certificate.createdDate desc")
    public Page<MetroCenCertificate> getCertificateByVerifiedId(@Param("verifiedId") long verifiedId,Pageable pageable);

    //根据用户ID获取该用户已分发的样品（distribute）
    @Query("select certificate from MetroCenCertificate certificate where certificate.distributionId=:distributionId")
    public Page<MetroCenCertificate> getCertificateByDistributionId(@Param("distributionId") long distributionId,Pageable pageable);

    //根据用户ID获取该用户已接收的样品（recept）
    @Query("SELECT certificate FROM MetroCenCertificate certificate where certificate.sampleId in(SELECT sample.id FROM MetroCenSample sample where sample.receptId=:receptId)")
    public Page<MetroCenCertificate> getCertificateByReceptId(@Param("receptId") long receptId,Pageable pageable);

    //获取当前证书最大编号数
    @Query("select max(certificate.countNo) from MetroCenCertificate certificate where certificate.modelId=:modelId")
    public Object getMaxCountNo(@Param("modelId")long modelId);

    //根据证书编号，获取最大补办次数
    @Query("select max(certificate.retroactive) from MetroCenCertificate certificate where certificate.certificateNo=:certificateNo")
    public Object getMaxRetroactiveByCertificateNo(@Param("certificateNo")String certificateNo);

    //根据证书编号，获取证书最大certificatePath
    @Query("select max(certificate.certificatePath) from MetroCenCertificate certificate where certificate.certificateNo=:certificateNo")
    public Object getMaxCertificatePathByCertificateNo(@Param("certificateNo")String certificateNo);

    //根据地址模糊获取环境及说明数据
    @Query("select certificate from MetroCenCertificate certificate where certificate.uncertainty like:uncertainty")
    public List<MetroCenCertificate> getUncertaintyByLook(@Param("uncertainty")String uncertainty);

    //根据客户Id，，查找证书
    @Query("select certificate from MetroCenCertificate certificate where certificate.client.id=:clientId")
    public List<MetroCenCertificate> getCertificateByClientId(@Param("clientId")long clientId);

    //查找当前运行流程的证书相关信息
    @Query("select certificate from MetroCenCertificate certificate where certificate.processId in (select runTask.PROC_INST_ID_ from ACT_RU_TASK runTask )")
    public List<MetroCenCertificate> getCertificateByRunProcess();

    //根据证书processId查找当前证书是否交接 张腾飞 2015 11 19
    @Query("select certificate.returnCer from MetroCenCertificate certificate where certificate.processId =:processId")
    public String getCertificateReturnCerByProcessId(@Param("processId")String processId);


}
