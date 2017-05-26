package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenSampleForm;
import cn.edu.shou.missive.service.*;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by XQQ on 2014/8/5.
 */



@RestController
@RequestMapping(value = "api/")
    public class MetroCenSearchApiController {

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

    //查询样品
    @RequestMapping(value = "/search/sample/{keyWords}/{searchWord}")
    public List<MetroCenSampleForm> findAllSample(@PathVariable String keyWords,@PathVariable String searchWord){

        String sql = "SELECT a.*,b.unit_name,b.phone,d.name  FROM (metro_cen_sample a left JOIN metro_cen_client b on a.client=b.id)left join (metro_cen_distribution c left join users d on c.accredited_id=d.id) on a.id=c.sample_id where "+keyWords+" like ?";
        List<MetroCenSampleForm> samplesForm = new ArrayList<MetroCenSampleForm>();

        List<Map<String,Object>>rows = jdbcTemplate.queryForList(sql,new Object[]{"%"+searchWord+"%"});
        long sampleId=0;
        long accreditedId=0;//检定者ID

        List<MetroCenDistribution> distributions=null;
        for (Map row : rows) {
            MetroCenSampleForm sampleForm=new MetroCenSampleForm();
            sampleId=Long.parseLong(row.get("id").toString());
            sampleForm.setSampleName((String) (row.get("sample_name")));
            sampleForm.setSampleCode((String)(row.get("sample_code")));
            distributions=distributionRepository.getDistributionsBySampleId(sampleId);
            String accreditedName="";
            if (distributions.size()>1){
                for (MetroCenDistribution distribution:distributions){
                    accreditedId=distribution.getAccreditedId().getId();
                    accreditedName=userRepository.findUserCNameById(accreditedId)+",";
                }

                sampleForm.setMeasureRange(accreditedName);//使用测量范围存放检定者名称
            }
            else if(distributions.size()==1){
                accreditedId=distributionRepository.getDistributionBySampleId(sampleId).getAccreditedId().getId();
                accreditedName=userRepository.findUserCNameById(accreditedId);
                sampleForm.setMeasureRange(accreditedName);//使用测量范围存放检定者名称
            }
            else {
                sampleForm.setMeasureRange("未分发");
            }
            String unitName=metroCenClientRepository.findById(sampleRepository.getClientIdBySampleId(sampleId).getId()).getUnitName();

            sampleForm.setUnitName(unitName);
            sampleForm.setFactoryCode((String) (row.get("factory_code")));
            sampleForm.setProcessId((String)(row.get("process_id")));
            samplesForm.add(sampleForm);
        }
        return samplesForm;
    }
    //查询样品（不输入关键词和搜索内容）张腾飞 20151129
    @RequestMapping(value = "/search/sample")
    public List<MetroCenSampleForm>findAllSample(){
        String sql =" SELECT a.*,b.unit_name,b.phone,d.name  FROM (metro_cen_sample a left JOIN metro_cen_client b on a.client=b.id)left join (metro_cen_distribution c left join users d on c.accredited_id=d.id) on a.id=c.sample_id";
        List<MetroCenSampleForm> samplesForm = new ArrayList<MetroCenSampleForm>();

        List<Map<String,Object>>rows = jdbcTemplate.queryForList(sql,new Object[]{});
        long sampleId=0;
        long accreditedId=0;//检定者ID

        List<MetroCenDistribution> distributions=null;
        for (Map row : rows) {
            MetroCenSampleForm sampleForm=new MetroCenSampleForm();
            sampleId=Long.parseLong(row.get("id").toString());
            sampleForm.setSampleName((String) (row.get("sample_name")));
            sampleForm.setSampleCode((String)(row.get("sample_code")));
            distributions=distributionRepository.getDistributionsBySampleId(sampleId);
            String accreditedName="";
            if (distributions.size()>1){
                for (MetroCenDistribution distribution:distributions){
                    accreditedId=distribution.getAccreditedId().getId();
                    accreditedName=userRepository.findUserCNameById(accreditedId)+",";
                }

                sampleForm.setMeasureRange(accreditedName);//使用测量范围存放检定者名称
            }
            else if(distributions.size()==1){
                accreditedId=distributionRepository.getDistributionBySampleId(sampleId).getAccreditedId().getId();
                accreditedName=userRepository.findUserCNameById(accreditedId);
                sampleForm.setMeasureRange(accreditedName);//使用测量范围存放检定者名称
            }
            else {
                sampleForm.setMeasureRange("未分发");
            }
            String unitName=metroCenClientRepository.findById(sampleRepository.getClientIdBySampleId(sampleId).getId()).getUnitName();

            sampleForm.setUnitName(unitName);
            sampleForm.setFactoryCode((String) (row.get("factory_code")));
            sampleForm.setProcessId((String)(row.get("process_id")));
            samplesForm.add(sampleForm);
        }
        return samplesForm;
    }


    //查询证书
    @RequestMapping(value="/search/certificate/{keyWords}/{searchWord}")
    public List<MetroCenCertificate> findAllCertificate(@PathVariable String keyWords,@PathVariable String searchWord){
        String sql="";
        List<Map<String,Object>>rows;
        if(keyWords.equals("verified")||keyWords.equals("checked")||keyWords.equals("approved")){
            long keyNameId=userRepository.getUserIdByCName("%"+searchWord+"%");
            searchWord=String.valueOf(keyNameId);
            sql ="SELECT * FROM metro_cen_certificate WHERE "+keyWords+" = ?";
            rows =jdbcTemplate.queryForList(sql,new Object[]{searchWord});
        }
        else {
            sql ="SELECT * FROM metro_cen_certificate WHERE "+keyWords+" like ?";
            rows =jdbcTemplate.queryForList(sql,new Object[]{"%"+searchWord+"%"});
        }
        List<MetroCenCertificate> certificates=new ArrayList<MetroCenCertificate>();

        for(Map row: rows){
            MetroCenCertificate certificate=new MetroCenCertificate();
            certificate.setCertificateNo((String)(row.get("certificate_no")));
            certificate.setInspectionUnit((String)(row.get("inspection_unit")));
            certificate.setSampleName((String)(row.get("sample_name")));
            certificate.setProcessId((String)(row.get("process_id")));
            long verified=Long.parseLong(row.get("verified").toString());
            certificate.setManufactUnit(userRepository.findUserCNameById(verified));//setVerified
            long checked=Long.parseLong(row.get("checked").toString());
            certificate.setConclusion(userRepository.findUserCNameById(checked));//setChecked
            long approved=Long.parseLong(row.get("approved").toString());
            certificate.setRemark(userRepository.findUserCNameById(approved));//setApproved
            certificates.add(certificate);

        }
        return certificates;
    }

    //查询证书（不输入关键词和搜索内容）张腾飞 20151129
    @RequestMapping(value="/search/certificate")
    public List<MetroCenCertificate> findAllCertificate(){
        String sql ="SELECT * FROM metro_cen_certificate";
        List<Map<String,Object>>rows;
            rows =jdbcTemplate.queryForList(sql,new Object[]{});
        List<MetroCenCertificate> certificates=new ArrayList<MetroCenCertificate>();

        for(Map row: rows){
            MetroCenCertificate certificate=new MetroCenCertificate();
            certificate.setCertificateNo((String)(row.get("certificate_no")));
            certificate.setInspectionUnit((String)(row.get("inspection_unit")));
            certificate.setSampleName((String)(row.get("sample_name")));
            certificate.setProcessId((String)(row.get("process_id")));
            long verified=Long.parseLong(row.get("verified").toString());
            certificate.setManufactUnit(userRepository.findUserCNameById(verified));//setVerified
            long checked=Long.parseLong(row.get("checked").toString());
            certificate.setConclusion(userRepository.findUserCNameById(checked));//setChecked
            long approved=Long.parseLong(row.get("approved").toString());
            certificate.setRemark(userRepository.findUserCNameById(approved));//setApproved
            certificates.add(certificate);

        }
        return certificates;
    }































    //根据条码获取processId
    @RequestMapping(value = "/search/{identifier}")
    public String getProcessId(@PathVariable String identifier){
        Object sampleId=identifierRepository.getSampleId(identifier);
        if(sampleId!=null){
            String processId=identifierRepository.getProcessId(Long.parseLong(sampleId.toString()));
            return processId;
        }else {
            return "";
        }

    }
    //根据processId获取是否出具证书的项目
    @RequestMapping(value="/search/certificateList/{certifiSubmit}/{processId}")
    public String getSurveillancePro(@PathVariable int certifiSubmit,@PathVariable String processId){
        String sur="";
        List<MetroCenDistribution> distributions=distributionRepository.getSurveillanceProList(certifiSubmit, processId);
        if(distributions.size()!=0)
        {
        for (MetroCenDistribution distribution:distributions){
            sur+=distribution.getSurveillancePro()+",";
        }
        }
        return sur;
    }
    @RequestMapping(value = "/sample/searchIdentifier/{processType}/{identifier}")
    public MetroCenSampleForm getPrintSample(Model model,@AuthenticationPrincipal User currentUser,@PathVariable long processType,@PathVariable String identifier){
        //根据identifier获取sampleForm
        Object sampleId=identifierRepository.getSampleId(identifier);
        if(sampleId!=null){
            long sampleIdL=Long.parseLong(sampleId.toString());
            String processId="";
            processId = identifierRepository.getProcessId(sampleIdL);
            model.addAttribute("user", currentUser);
            MetroCenSampleForm sampleForm = null;
            sampleForm = getSampleInfo(processId);//获取样品信息
            return sampleForm;
        }else {
            return null;
        }
    }
    //根据样品的流程ID获取样品信息
    public MetroCenSampleForm getSampleInfo(String processId){
        MetroCenSample sample=sampleRepository.getSampleInfoByProcessId(processId);
        MetroCenSampleForm sampleForm=new MetroCenSampleForm();
        sampleForm=bindSampleClient(sampleForm,sample.getClient());
        sampleForm=bindSampleInfo(sampleForm,sample);
        return sampleForm;
    }
    public MetroCenSampleForm bindSampleClient(MetroCenSampleForm sampleForm,MetroCenClient client){
        sampleForm.setUnitName(client.getUnitName());
        sampleForm.setUnitAddress(client.getUnitAddress());
        sampleForm.setTelephone(client.getTelephone());
        sampleForm.setPhone(client.getPhone());
        sampleForm.setEmail(client.getEmail());

        sampleForm.setCertiCode(client.getCertiCode());
        sampleForm.setCertiName(client.getCertiName());
        sampleForm.setContacts(client.getContacts());
        sampleForm.setContractNo(client.getContractNo());
        sampleForm.setId(client.getId());


        return sampleForm;
    }
    //绑定样品信息
    public MetroCenSampleForm bindSampleInfo(MetroCenSampleForm sampleForm,MetroCenSample sample){
        sampleForm.setId(sample.getId());
        sampleForm.setSampleNum(sample.getSampleNum());
        sampleForm.setSpecificateModel(sample.getSpecificateModel());
        if (sample.getSampleMethod()!=null){
            sampleForm.setSampleMethod(sample.getSampleMethod().getId());
        }

        sampleForm.setMeasureRange(sample.getMeasureRange());
        sampleForm.setSampleTest(sample.getSampleTest());
        sampleForm.setFactoryName(sample.getFactoryName());
        if (sample.getStatusName()!=null){
            sampleForm.setStatusName(sample.getStatusName().getId());
        }
        if (sample.getServiceWay()!=null){
            sampleForm.setServiceWay(sample.getServiceWay().getId());
        }

        sampleForm.setAccuracyLevel(sample.getAccuracyLevel());
        sampleForm.setDistributionId(sample.getDistributionId());//分发者
        sampleForm.setPrincipalOther(sample.getPrincipalOther());
        sampleForm.setPrincipalRequre(sample.getPrincipalRequre());

        sampleForm.setPrincipalTestBaseOn(sample.getPrincipalTestBaseOn());
        sampleForm.setPrincipalTestRequre(sample.getPrincipalTestRequre());
        sampleForm.setReceptId(sample.getReceptId());
        sampleForm.setRemark(sample.getRemark());
        sampleForm.setSampleCode(sample.getSampleCode());
        sampleForm.setSampleName(sample.getSampleName());
        Task task=null;
        String taskName ="已完成";
        if (sample.getProcessId()!=null){
             task=activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(sample.getProcessId()));
        }else {
            taskName="异常";
        }
        if (task!=null){
            taskName=task.getName();//获取任务名称
        }
        sampleForm.setRemark(taskName);//用备注属性存放任务状态

        if (sample.getServiceType()!=null){
            sampleForm.setServiceType(sample.getServiceType().getId());
        }

        sampleForm.setProcessId(sample.getProcessId());
        if (sample.getSurveillancePro()!=null){
            sampleForm.setSurveillancePro(sample.getSurveillancePro());//检测项目
        }else {
            sampleForm.setSurveillancePro("");//检测项目
        }
        if (sample.getSampleDetected()!=null){
            sampleForm.setSampleDetected(sample.getSampleDetected());//已检测项目
        }else {
            sampleForm.setSampleDetected("");//已检测项目
        }

        if (sample.getCharacterService()!=null){
            sampleForm.setCharacterService(sample.getCharacterService().getId());//特色服务
        }
        sampleForm.setFactoryCode(sample.getFactoryCode());//出厂编号
        sampleForm.setCreateDate(sample.getCreatedDate().toString().split("T")[0]);//创建日期

        return sampleForm;
    }
    //绑定样品流转信息列表
    public List<MetroCenDistribution> bindDistributionInfo(String processId){
        List<MetroCenDistribution>distributions=distributionRepository.getDistributionByProcessId(processId);
        //对已查寻的样品循环处理检测项目
        for (MetroCenDistribution distribution:distributions){
            String surveillanceProName="";//检测项目名称字符串
            String[] surveillancePro=distribution.getSurveillancePro().split(",");//由于一个人能够检测多个项目，因此检测项目先拆分
            Long[] ids=new Long[surveillancePro.length];
            for (int i=0;i<surveillancePro.length;++i){
                ids[i]=Long.parseLong(surveillancePro[i]);
            }
            List idArrs= Arrays.asList(ids);
            List<MetroCenSurveillancePro>surveillancePros=surveillanceProRepository.getSurveillanceProByIds(idArrs);
            for (MetroCenSurveillancePro cenSurveillancePro:surveillancePros){
                surveillanceProName+=cenSurveillancePro.getSurveillanceName()+",";
            }
            surveillanceProName=surveillanceProName.substring(0,surveillanceProName.length()-1);//去除最后一个,
            distribution.setTaskId(distribution.getSurveillancePro());//因为taskId没有数据，用来存放检测项目的编号字符串
            distribution.setSurveillancePro(surveillanceProName);
        }
        return distributions;

    }

    }

