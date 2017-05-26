package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.ACT_RU_TASK;
import cn.edu.shou.missive.domain.MetroCenCertificate;
import cn.edu.shou.missive.domain.MetroCenSample;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenSampleHistoryForm;
import cn.edu.shou.missive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by XQQ on 2014/8/5.
 */



@RestController
@RequestMapping(value = "api/")
    public class MetroCenHistoryApiController {

    @Autowired
    MetroCenSampleRepository sampleRepository;
    @Autowired
    MetroCenClientRepository metroCenClientRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    MetroCenDistributionRepository distributionRepository;
    @Autowired
    MetroCenIdentifierRepository identifierRepository;
    @Autowired
    MetroCenSurveillanceProRepository surveillanceProRepository;
    @Autowired
    MetroCenCertificateRepository cenCertificateRepository;
    @Autowired
    MetroCenHistotyRepository histotyRepository;
    @Autowired
    MetroCenTaskNameRepository taskNameRepository;


    @RequestMapping(value="/history/sampleInfo")
    //获取样品信息
    public List<MetroCenSampleHistoryForm> getSampleHistoryInfo(){
        List<MetroCenSample> samples=sampleRepository.findSampleByRunProcess();
        List<MetroCenSampleHistoryForm> historyForms=new ArrayList<MetroCenSampleHistoryForm>() ;

        for(MetroCenSample sample:samples){
            MetroCenSampleHistoryForm historyForm=new MetroCenSampleHistoryForm() ;
            historyForm.setIdentifier(identifierRepository.getSerialNumberBySampleId(sample.getId()).getSerialNumber());//获取样品接收单号
            historyForm.setSampleName(sample.getSampleName());//获取样品名称
            historyForm.setSampleCode(sample.getFactoryCode());//获取出厂编号序列
            //根据样品流程获取当前流程对象
           ACT_RU_TASK act_ru_task=histotyRepository.getRunTaskByProcessId(sample.getProcessId());
            historyForm.setSampleStatus(act_ru_task.getNAME_());//获取样品流程名称
            historyForm.setStatusPercent(getStatusPercent(act_ru_task.getNAME_()));//状态百分比
            //根据当前用户英文名获取中文名（获取英文名）
            String EName=act_ru_task.getASSINGEE_();
            String CName=userRepository.getCNameByEName(EName);
            historyForm.setSampleCharge(CName);//获取样品流程负责人名称
            historyForms.add(historyForm);
        }
        return historyForms;

    }

    //获取证书状态
    @RequestMapping(value="/history/certiInfo")
    public List<MetroCenSampleHistoryForm> getCertiHistoryInfo(){
        List<MetroCenCertificate> certificates=cenCertificateRepository.getCertificateByRunProcess();
        List<MetroCenSampleHistoryForm> historyForms=new ArrayList<MetroCenSampleHistoryForm>();
        for (MetroCenCertificate certificate:certificates){
            MetroCenSampleHistoryForm historyForm=new MetroCenSampleHistoryForm();
            historyForm.setIdentifier(identifierRepository.getSerialNumberBySampleId(certificate.getSampleId()).getSerialNumber());//样品批号作为分组方式
            historyForm.setCertificateNo(certificate.getCertificateNo());//证书编号
            historyForm.setSampleName(certificate.getSampleName());//样品名称
            historyForm.setSampleCode(certificate.getSerialNumber());//样品编号
            //根据证书流程获取当前流程对象
            ACT_RU_TASK act_ru_task=histotyRepository.getRunTaskByProcessId(certificate.getProcessId());
            historyForm.setCertiStatus(act_ru_task.getNAME_());//证书状态
            historyForm.setStatusPercent(getStatusPercent(act_ru_task.getNAME_()));//百分比
            String EName=act_ru_task.getASSINGEE_();
            String CName=userRepository.getCNameByEName(EName);
            historyForm.setCertiCharge(CName);//证书负责人
            historyForms.add(historyForm);
        }
        return historyForms;
    }


    public String getStatusPercent(@PathVariable String status){
        long id=taskNameRepository.findByTaskName(status).getId()+1;
        String statusPercent=id+"0%";
        return statusPercent;
    }
}
