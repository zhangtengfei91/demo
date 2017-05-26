package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenDistributionForm;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenSampleForm;
import cn.edu.shou.missive.service.*;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.*;

/**
 * Created by seky on 15/1/8.
 */
@RequestMapping(value="MetroCen")
@SessionAttributes(value = {"userbase64","user"})
@Controller
public class MetroCenSampleController {
    @Autowired
    private UserRepository usDAO;
    @Autowired
    private MetroCenSampleRepository sampleRepository;
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private MetroCenTaskNameRepository taskNameRepository;
    @Autowired
    private MetroCenDistributionRepository distributionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MetroCenSurveillanceProRepository surveillanceProRepository;
    @Autowired
    private MetroCenIdentifierRepository identifierRepository;


    //加载样品检测,及样品流转页面
    @RequestMapping(value = "/sample/{processType}/{processId}")
    public String getSample(Model model,@AuthenticationPrincipal User currentUser,@PathVariable long processType,@PathVariable String processId){
        model.addAttribute("user", currentUser);
        List<MetroCenMissiveField> missiveField=null;//存放字段对象
        List<String> inputFieldList = new ArrayList<String>();
        MetroCenSampleForm sampleForm=null;
        MetroCenSample sample=null;//样品信息
        MetroCenIdentifier identifier=null;//样品条码数据表
        Task task=null;//当前任务信息
        long taskId=1;//获取任务名称的ID
        Map<String,String> mp=new HashMap<String, String>();//用来存放样品相关信息
        List<Map<String,? extends Object>> activitiNextStepInfo =new ArrayList<Map<String, ? extends Object>>();//下一步流程信息
        List<MetroCenDistribution> distributions=null;//样品流转信息列表
        MetroCenDistributionForm  currentDistributionForm=null;//当前用户流转的样品
        boolean isBack=true;//仪器是否归还
        String accreditedName="";//检定人员名称

        //如果processId=none，表示样品接收,加载客户信息和样品信息

        if (processId.equals("none")){
            missiveField=sampleRepository.getFieldInfo(processType,taskId);
            mp.put("currentStatus","none");//添加当前状态信息，none表示接收样品
            mp.put("clientId","0");//客户ID
            mp.put("sampleId","0");//样品ID
        }else {
                //获取当前任务信息
                task = activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(processId));
                sampleForm=getSampleInfo(processId);//获取样品信息
            if (task!=null){
                taskId=taskNameRepository.findByTaskName(task.getName()).getId();//获取需要显示任务的ID
                missiveField=sampleRepository.getFieldInfo(processType,taskId);
                //获取下一步流程信息
                activitiNextStepInfo =activitiService.getNextTaskInfo(String.valueOf(processId), Integer.parseInt(task.getId()));
                sampleForm=getSampleInfo(processId);//获取样品信息
                distributions=bindDistributionInfo(processId);
                currentDistributionForm=getCurrentUserDistributionInfo(distributions,currentUser.getId(),processId);
                //获取样品信息
                sample=sampleRepository.getSampleInfoByProcessId(processId);
                identifier=identifierRepository.getSerialNumberBySampleId(sample.getId());//获取编号相关信息
                mp.put("currentStatus", task.getName());//添加当前状态信息
                mp.put("clientId",String.valueOf(sample.getClient().getId()));//客户ID
                mp.put("sampleId",String.valueOf(sample.getId()));//样品ID
                mp.put("identifierId",String.valueOf(identifier.getId()));//条码编号ID

                if(task.getName().equals("收发仪器")){
                    MetroCenDistribution distribution=getDistributionBySampleIdAndProcessIdAndStatusId(sampleForm.getId(),processId,task.getId());
                    if (distribution.getBack().equals("未还")){
                        isBack=false;
                        accreditedName=distribution.getAccreditedId().getName();
                        model.addAttribute("isBack",isBack);//是否归还，true为已归还
                        model.addAttribute("accreditedName",accreditedName);//检定人员
                    }

                }
            }
        }

        for (MetroCenMissiveField mf:missiveField)
        {
            inputFieldList.add(mf.getInputId());
        }

        model.addAttribute("currentEditableField",inputFieldList);//将需要加载的字段加载到模板
        model.addAttribute("sampleInfo",mp);//样品信息返回前台
        model.addAttribute("activitiNextStepInfo",activitiNextStepInfo);//下一步流程信息返回前台
        model.addAttribute("sample",sampleForm);//将所有样品以及客户信息返回前台
        model.addAttribute("distribution",distributions);//样品流转信息
        model.addAttribute("currentDistribution",currentDistributionForm);//当前用户流转样品信息
        model.addAttribute("currentUserId",currentUser.getId());//当前登录用户ID

        return "MetroCenSample";
    }
    //加载样品检测及样品流转页面
    @RequestMapping(value = "/sample/print/{processType}/{processId}")
    public String getPrintSample(Model model,@AuthenticationPrincipal User currentUser,@PathVariable long processType,@PathVariable String processId){
        model.addAttribute("user", currentUser);
        MetroCenSampleForm sampleForm=null;
        sampleForm=getSampleInfo(processId);//获取样品信息

        model.addAttribute("sample",sampleForm);//将所有样品以及客户信息返回前台
        model.addAttribute("currentUserName",currentUser.getName());//当前登录用户姓名

        return "MetroCenSamplePrint";
    }
    //加载我的样品任务信息列表
    @RequestMapping(value = "/sample/mySampleTask/{pagenum}")
    public String getMySampleTask(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pagenum){
        model.addAttribute("user", currentUser);
        getCurrentTaskByUser(model, currentUser, "SampleDistr", pagenum);//样品任务
        return "mySampleTaskList";
    }
    //加载已分发的样品信息
    @RequestMapping(value = "/sample/mySampleInfo/{pagenum}",method = RequestMethod.GET)
    public String getMySampleInfo(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pagenum){
        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("user", currentUser);
        model.addAttribute("currentPage",pagenum);//当前显示的页码
        getCurrentDistributedByUser(model,currentUser,pagenum);
        return "mySampleInfoList";
    }
    //加载已待处理的样品信息
    @RequestMapping(value = "/sample/myWaitSampleInfo/{pagenum}",method = RequestMethod.GET)
    public String getMyWaitSampleInfo(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pagenum){
        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("user", currentUser);
        model.addAttribute("currentPage",pagenum);//当前显示的页码
        getWaitSampleByUser(model,currentUser,pagenum);
        return "myWaitSampleInfo";
    }
    //根据样品的流程ID获取样品信息
    public MetroCenSampleForm getSampleInfo(String processId){
        MetroCenSample sample=sampleRepository.getSampleInfoByProcessId(processId);
        MetroCenSampleForm sampleForm=new MetroCenSampleForm();
        sampleForm=bindSampleClient(sampleForm,sample.getClient());
        sampleForm=bindSampleInfo(sampleForm,sample);
        return sampleForm;
    }
    //绑定样品客户信息
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
    public List<MetroCenDistribution> bindDistributionInfo(String processId) {
        List<MetroCenDistribution> distributions = distributionRepository.getDistributionByProcessId(processId);
        //对已查寻的样品循环处理检测项目
        for (MetroCenDistribution distribution : distributions) {
            String surveillanceProName = "";//检测项目名称字符串
            String[] surveillancePro = distribution.getSurveillancePro().split(",");//由于一个人能够检测多个项目，因此检测项目先拆分
            Long[] ids = new Long[surveillancePro.length];
            for (int i = 0; i < surveillancePro.length; ++i) {
                ids[i] = Long.parseLong(surveillancePro[i]);
            }
            List idArrs = Arrays.asList(ids);
            List<MetroCenSurveillancePro> surveillancePros = surveillanceProRepository.getSurveillanceProByIds(idArrs);
            for (MetroCenSurveillancePro cenSurveillancePro : surveillancePros) {
               surveillanceProName += cenSurveillancePro.getSurveillanceName() + ",";

            }
            surveillanceProName = surveillanceProName.substring(0, surveillanceProName.length() - 1);//去除最后一个,
            distribution.setTaskId(distribution.getSurveillancePro());//因为taskId没有数据，用来存放检测项目的编号字符串
            distribution.setSurveillancePro(surveillanceProName);
        }
        return distributions;
    }

    //获取当前用户的检定样品流转信息
    public MetroCenDistributionForm getCurrentUserDistributionInfo(List<MetroCenDistribution> distributionsList,long userId,String processId){
        MetroCenDistributionForm distributionsForm=new MetroCenDistributionForm();
        for (MetroCenDistribution distribution: distributionsList){
            if (distribution.getAccreditedId().getId()==userId && distribution.getProcessId().equals(processId) && distribution.getStatusName().getId()!=3){//仪器检定
                distributionsForm.setId(distribution.getId());//样品ID
                distributionsForm.setProcessId(distribution.getProcessId());//流程ID
                distributionsForm.setReceivedDateStr(distribution.getReceivedDate());//接收日期
                distributionsForm.setRecReturnDateStr(distribution.getRecReturnDate());//建议归还日期
                distributionsForm.setActReturnDateStr(distribution.getActReturnDate());//实际归还日期
                distributionsForm.setSurveillancePro(distribution.getSurveillancePro());//检测项目
                distributionsForm.setTake(distribution.getTake());//是否已取样品
                distributionsForm.setBack(distribution.getBack());//是否已还样品
                distributionsForm.setDistributionId(distribution.getDistributionId());//分发者ID
                distributionsForm.setTaskId(distribution.getTaskId());//用来接收检测项目的编号字符串
                if (distribution.getDistributionId()!=0) {
                    distributionsForm.setRemark(userRepository.findOne(distribution.getDistributionId()).getName());//用备注字段存放分发者姓名
                }
            }
        }
        return distributionsForm;
    }
    //查找当前登录用户的所有样品任务
    public List<MetroCenSample>getCurrentTaskByUser(Model model,User usr,String processDefinitionName,Integer pageNum){
        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
        PageableTaskList result = this.activitiService.getCurrentTasksByUser(usr, processDefinitionName,20, pageNum);//待办的任务
        List<MetroCenSample> metroCenSampleList=new ArrayList<MetroCenSample>();//样品信息列表
        MetroCenSample metroCenSample;//样品信息

        //根据任务的processId对样品进行查询
        for(Task task:result.getTasklist()){
            String dealSampleUrl="/MetroCen/sample/1/";//处理样品链接信息，使用MetroCenSample中的remark来接收链接地址
            String taskName=task.getName();//任务名称
            metroCenSample=sampleRepository.getSampleInfoByProcessId(task.getProcessInstanceId());
            if(metroCenSample!=null){
                dealSampleUrl=dealSampleUrl+task.getProcessInstanceId();
                metroCenSample.setRemark(dealSampleUrl);//使用MetroCenSample中的remark来接收链接地址
                metroCenSample.setTaskId(taskName);//使用任务ID存放taskName
                metroCenSampleList.add(metroCenSample);
            }
        }
        int taskIngPagesNum=result.getPageTotal()==0?1:result.getPageTotal();
        model.addAttribute("sampleInfo",metroCenSampleList);
        model.addAttribute("taskIngTotalNum",taskIngPagesNum);
        model.addAttribute("LookPage",pageNum);
        return metroCenSampleList;
    }

    //查找当前登录用户的已分发的样品信息
    public List<MetroCenDistribution>getCurrentDistributedByUser(Model model,User usr,Integer pageNum){
        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
        int pageSize=20;//记录条数
        List<MetroCenDistribution> metroCenDistributionsList=new ArrayList<MetroCenDistribution>();//暂存样品分发列表
        Page<MetroCenDistribution>cenDistributionPage=distributionRepository.getDistributionByDistributionId(usr.getId(),new PageRequest((pageNum-1),pageSize));
        metroCenDistributionsList=cenDistributionPage.getContent();
        //start
        MetroCenSample sample=null;
        //郑小罗 20151125 根据检测项目Id获取检测项目名称
        String unit="";//客户地址
        String surveillanceProId="";//接收检测项目Id字符串 字符串
       String[] surveillanceProIds=null;//检测项目编号 数组

        //使用循环获取客户信息，根据样品Id
        if(metroCenDistributionsList!=null){
            for(MetroCenDistribution distribution :metroCenDistributionsList){
                sample=sampleRepository.findOne(distribution.getSampleId());//根据样品Id，获取样品信息
                unit=sample.getClient().getUnitName();//获取到客户的单位名称
                String surveillanceProNames="";//检测项目名称
                distribution.setRemark(unit);//使用remark存放单位名称
                surveillanceProId=distribution.getSurveillancePro();//获取到检测项目Id字符串
                surveillanceProIds=surveillanceProId.split(",");//拆分检测项目Id字符串
               if(!surveillanceProIds.equals(null)){
                        for (String surveillance:surveillanceProIds){
                        surveillanceProNames+=surveillanceProRepository.getSurveillanceProById(Long.parseLong(surveillance)).getSurveillanceName()+",";
                        //根据检测项目编号获取名称 把string类型转换为long类型
                    }
                }
                surveillanceProNames=surveillanceProNames.substring(0,surveillanceProNames.length()-1);//去掉最后一个,
               distribution.setSurveillancePro(surveillanceProNames);
            }
        }
        //stop
        int taskIngPagesNum=cenDistributionPage.getTotalPages()==0?1:cenDistributionPage.getTotalPages();//总页数
        model.addAttribute("sampleInfo",metroCenDistributionsList);
        model.addAttribute("taskIngTotalNum",taskIngPagesNum);
        model.addAttribute("LookPage",pageNum);
        return metroCenDistributionsList;
    }
    //查找当前登录用户的待处理的样品信息 即是样品信息中分发者为0和processId为空
    public List<MetroCenSample>getWaitSampleByUser(Model model,User usr,Integer pageNum){
        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
        int pageSize=20;//记录条数
        List<MetroCenSample> metroCenSampleList=new ArrayList<MetroCenSample>();//样品列表
        Page<MetroCenSample>cenSamplePage=sampleRepository.getWaitSampleByUserId(usr.getId(),new PageRequest((pageNum-1),pageSize));
        metroCenSampleList=cenSamplePage.getContent();
        int taskIngPagesNum=cenSamplePage.getTotalPages()==0?1:cenSamplePage.getTotalPages();//总页数
        model.addAttribute("sampleInfo",metroCenSampleList);
        model.addAttribute("taskIngTotalNum",taskIngPagesNum);
        model.addAttribute("LookPage",pageNum);
        return metroCenSampleList;
    }
    //判断流转样品是否以及归还
    public MetroCenDistribution getDistributionBySampleIdAndProcessIdAndStatusId(long sampleId, String processId,String taskId){
        long distributionStatus=3;//需要归还的流转样品状态，肯定是已经检定完毕状态或是退检的样品，3代表检毕，4代表退检
        long returnDistributionStatus=4;//退检
        MetroCenDistribution distribution=distributionRepository.getDistributionBySampleId(sampleId,processId,distributionStatus,returnDistributionStatus,taskId);//获取样品信息
        return distribution;
    }





}
