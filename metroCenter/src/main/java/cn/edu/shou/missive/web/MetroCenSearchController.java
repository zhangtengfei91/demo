package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenSampleForm;
import cn.edu.shou.missive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Arrays;
import java.util.List;

/**
 * Created by seky on 15/1/8.
 */
@RequestMapping(value="MetroCen")
@SessionAttributes(value = {"userbase64","user"})
@Controller
public class MetroCenSearchController {
    @Autowired
    private MetroCenSampleRepository sampleRepository;
    @Autowired
    private MetroCenDistributionRepository distributionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MetroCenSurveillanceProRepository surveillanceProRepository;
    @Autowired
    MetroCenIdentifierRepository identifierRepository;


    @RequestMapping(value = "/search")
    public String getSearch(Model model,@AuthenticationPrincipal User currentUser){
        model.addAttribute("user", currentUser);
        return "MetroCenSearch";
    }
    @RequestMapping(value = "/sample/searchIdentifier/{processType}/{processId}")
    public String getPrintSample(Model model,@AuthenticationPrincipal User currentUser,@PathVariable long processType,@PathVariable String processId){
        model.addAttribute("user", currentUser);
        MetroCenSampleForm sampleForm=null;
        sampleForm=getSampleInfo(processId);//获取样品信息

        model.addAttribute("sample",sampleForm);//将所有样品以及客户信息返回前台
        model.addAttribute("currentUserName",currentUser.getName());//当前登录用户姓名

        return "MetroCenSearchIdentifier";
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


