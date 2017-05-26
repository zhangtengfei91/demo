package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.service.bpm.cmd.RollbackTaskCmd;
import org.activiti.engine.ManagementService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by seky on 15/1/23.
 * 所有需要配置的数据获取
 */
@RestController
@RequestMapping(value = "/config")
public class MetroCenConfigController {
    @Autowired
    private MetroCenSampleRepository sampleRepository;
    @Autowired
    private MetroCenServiceTypeRepository serviceTypeRepository;
    @Autowired
    private MetroCenSampleMethodRepository sampleMethodRepository;
    @Autowired
    private MetroCenServiceWayRepository serviceWayRepository;
    //张腾飞 201512 21
    @Autowired
    private MetroCenMessageModelRepository messageModelRepository;
    @Autowired
    private MetroCenLabNameRepository labNameRepository;
    @Autowired
    private MetroCenStatusRepository statusRepository;
    @Autowired
    private MetroCenCharacterServiceRepository characterServiceRepository;
    @Autowired
    private MetroCenSurveillanceProRepository surveillanceProRepository;
    @Autowired
    private MetroCenDistributionRepository distributionRepository;
    @Autowired
    private MetroCenCertificateModelRepository certificateModelRepository;
    @Autowired
    private MetroCenCertificateBackGroundRepository backGroundRepository;
    @Autowired
    private MetroCenStandardEquipmentRepository equipmentRepository;
    @Autowired
    private MetroCenReslutModelRepository reslutModelRepository;
    @Autowired
    private MetroCenSampleInfoRepository sampleInfoRepository;
    @Autowired
    cn.edu.shou.missive.web.CommonFunction commF;
    @Autowired
    private MetroCenCertificateRepository cenCertificateRepository;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MetroCenLocationEnvironmentRepository locationEnvironmentRepository;

    //返回所有的服务类型
    @RequestMapping(value = "/getServiceType")
    public List<MetroCenServiceType>getAllServiceType(){
        return serviceTypeRepository.findAll();
    }
    //根据Id获取名称
    @RequestMapping(value = "/getTypeName/{typeId}")
    public String getTypeNameByTypeId(@PathVariable long typeId){
        return serviceTypeRepository.getTypeNameById(typeId).getServiceName();
    }
    //返回所有服务方式
    @RequestMapping(value = "/getServiceWay")
    public List<MetroCenServiceWay>getAllServiceWay(){
        return serviceWayRepository.findAll();
    }

    //根据ID获取服务方式
    @RequestMapping(value="/getServiceWay/{serviceId}")
    public String getServiceWayByServiceId(@PathVariable long serviceId){
        return serviceWayRepository.getServiceWayById(serviceId).getServiceWay();
    }
    //张腾飞 20151221
   /* @RequestMapping(value = "/getMessageModel")
    public List<MetroCenMessageModel>getAllMessageModel(){
        return messageModelRepository.findAll();
    }
    @RequestMapping(value="/getMessageModel/{modelId}")
    public String getMessageModelBymodelId(@PathVariable long modelId){
        return messageModelRepository.getMessageModelById(modelId).getTaskName();
    }*/


    //返回所有取样方式
    @RequestMapping(value = "/getSampleMethod")
    public List<MetroCenSampleMethod>getAllSampleMethod(){
        return sampleMethodRepository.findAll();
    }


    //根据id获取用户姓名
    @RequestMapping(value="/getUserNameById/{id}")
    public String  getUserNameById(@PathVariable long id){
        String userName=userRepository.findUserCNameById(id);
        return userName;
    }
    //根据postId获取员工信息
    @RequestMapping(value="/getUserByPostId/{post}",method=RequestMethod.GET)
    public List<User> getUserByPostId(@PathVariable String post){
        List<User> users=userRepository.getUserListByPostId("%"+post+"%");
//        List<User> users1=userRepository.getUserListByPostId(Long.parseLong("4"));//获取到综合岗位用户数据，综合岗位的用户可以在做任何操作的选择
//        List<User>usersList=new ArrayList<User>();
//        usersList.addAll(users);
////        usersList.addAll(users1);
        return users;
    }

    //根据labId获取员工信息
    @RequestMapping(value="/getUserByLabId/{post}/{labId}",method=RequestMethod.GET)
    public List<User> getUserByLabId(@PathVariable String post,@PathVariable long labId){
        List<User> users=userRepository.getUserListByLabId("%"+post+"%","%"+labId+"%");
        return users;
    }

    //根据ID获取服务方式
    @RequestMapping(value="/getSampleMethod/{methodId}")
    public String getSampleMethodByMethodId(@PathVariable long methodId){
        return sampleMethodRepository.getSampleMethodById(methodId).getSampleMethod();
    }
    //返回所有实验室名称
    @RequestMapping(value = "/getLabName")
    public List<MetroCenLabName>getAllLabName(){
        return labNameRepository.findAll();
    }
    //返回所有状态信息
    @RequestMapping(value = "/getStatus")
    public List<MetroCenStatus>getAllStatus(){
        return statusRepository.findAll();
    }
    //返回所有特色服务
    @RequestMapping(value = "/getCharacterService")
    public List<MetroCenCharacterService>getAllCharacterService(){
        return characterServiceRepository.findAll();
    }
    //根据ID获取服务方式
    @RequestMapping(value="/getCharacterService/{characterId}")
    public String getCharacterServiceByCharacterId(@PathVariable long characterId){
        return characterServiceRepository.getServiceNameById(characterId).getServiceName();
    }
    //返回所有检测项目信息
    @RequestMapping(value = "/getSurveillancePro")
    public List<MetroCenSurveillancePro>getAllSurveillancePro(){
        return surveillanceProRepository.findAll();
    }
    //根据ID获取检验项目
    @RequestMapping(value="/print/getSurveillancePro/{sampleLong}")
    public String getSurveillanceProById(@PathVariable String sampleLong){
        String proName="";//检测项目名称
        long proId=0;//检测项目编号
        String[] proIds=sampleLong.split(",");
        for (int i=0;i<proIds.length;++i){
            if (!proIds[i].equals("")){
                proId=Long.parseLong(proIds[i]);
                MetroCenSurveillancePro tt=surveillanceProRepository.findOne(proId);
                   if(tt!=null){
                      String proname=tt.getSurveillanceName() ;
                       proName+=proname+",";

                   }
            }

        }
        if(sampleLong==""){
            proName="无";
        }
        proName=proName.substring(0,proName.length()-1);//去除最后一个，
        if(proName.substring(0,0)==",")//如果有，去除第一个逗号
        {
            proName=proName.substring(1);
        }
            return proName;
    }
    //根据日期获取样品唯一性标识
    @RequestMapping(value ="/getSampleCode/{dateString}", method=RequestMethod.GET)
    public String getSampleCode(@PathVariable String dateString){
        String sampleCode="";
        String maxCode="";
        String likeStr="%"+dateString+"%";
        sampleCode=sampleRepository.getSampleCode(likeStr);
        if (sampleCode==null){
            sampleCode=dateString+"01";
        }else {
            sampleCode=String.valueOf(Long.parseLong(sampleCode)+1);
        }
        return sampleCode;
    }
    //根据ID获取证书代码, 生成证书编号
    @RequestMapping(value = "/getCertificateModelCode/{modelId}")
    public String getCertificateModelCode(@PathVariable long modelId){
        Calendar cla=Calendar.getInstance();//获取当前系统日期
        String year=String.valueOf(cla.get(Calendar.YEAR));
        String modelCode=certificateModelRepository.getModelCodeById(modelId).getModelCode();//获取证书前段字母信息
        modelCode=modelCode+"-"+year;//证书编号前半段
        //根据modelId获取当前证书的编号
        Object countNo=cenCertificateRepository.getMaxCountNo(modelId);//获取到证书最大编号
        int iCountNo=countNo==null?1:Integer.parseInt(countNo.toString())+1;
        if(iCountNo<10){
            modelCode=modelCode+"00"+String.valueOf(iCountNo);

        }else if (iCountNo<100){
            modelCode=modelCode+"0"+String.valueOf(iCountNo);
        }else {
            modelCode=modelCode+String.valueOf(iCountNo);
        }
        return modelCode;
    }
    //返回所有大类证书模板
    @RequestMapping(value = "/getCertificateModel")
    public List<MetroCenCertificateModel>getAllCertificateModel(){
        return certificateModelRepository.getParentModel(0);
    }
    //返回所有二级证书模板
    @RequestMapping(value = "/getCertificateModel/child")
    public List<MetroCenCertificateModel>getAllChildCertificateModel(){
        return certificateModelRepository.getChildModel(0);
    }
    //根据modelId获取证书相关信息
    @RequestMapping(value = "/getCertificateModel/{modelId}")
    public MetroCenCertificateModel getCertificateModelById(@PathVariable long modelId){
        return certificateModelRepository.findOne(modelId);
    }
    //根据modelId返回证书结果说明模板内容
    @RequestMapping(value = "/getCertificateResultModel/{modelId}")
    public MetroCenReslutModel getResultModelByModelId(@PathVariable long modelId){
        MetroCenReslutModel model=reslutModelRepository.getResultModelByModelId(modelId);
        return model;
    }
    //根据ID返回检测项目
    @RequestMapping(value = "/getSurveillancePro/{Ids}",method = RequestMethod.GET)
    public List<MetroCenSurveillancePro>getSurveillancePro(@PathVariable String Ids){

        String[] idList = Ids.split(",");
        Long[] ids=new Long[idList.length];
        for (int i=0;i<idList.length;++i){
            ids[i]=Long.parseLong(idList[i]);
        }
        List idArrs=Arrays.asList(ids);
        List<MetroCenSurveillancePro>surveillancePros=surveillanceProRepository.getSurveillanceProByIds(idArrs);
        return surveillancePros;
    }

    //根据检定员用户获取样品名称
    @RequestMapping(value = "/certificate/getDistribution/{accreditedId}",method = RequestMethod.GET)
    public List<MetroCenDistribution> getDistributionByAccreditedId(@PathVariable long accreditedId){
        List<MetroCenDistribution> distributions=distributionRepository.getDistributionByAccreditedId(accreditedId);
        List<MetroCenDistribution>distributionList=new ArrayList<MetroCenDistribution>();
        if (distributions!=null){
            for (MetroCenDistribution distrib:distributions){
                MetroCenDistribution distribution=new MetroCenDistribution();
                String num=distrib.getNum()==1?"":"["+String.valueOf(distrib.getNum())+"]";
                distribution.setId(distrib.getId());
                distribution.setSampleName(distrib.getSampleName()+"("+getSurveillanceProByIds(distrib.getSurveillancePro())+num+")");
                distributionList.add(distribution);
            }
        }
        return distributionList;
    }

    //根据模板Id返回背景图片信息
    @RequestMapping(value = "/getBackGround/{modelId}",method = RequestMethod.GET)
    public MetroCenCertificateBackGround getBackGroundInfoByModelId(@PathVariable long modelId){

        return backGroundRepository.getBackGroundByModelId(modelId);
    }
    //根据模板Id返回计量器具、装置等相关信息
    @RequestMapping(value = "/getEquipMentByModelId/{modelId}",method = RequestMethod.GET)
    public List<MetroCenStandardEquipment>getEquipMentInfoByModelId(@PathVariable long modelId){

        return equipmentRepository.getEquipMentByModelId(modelId);
    }
    //查询样品信息数据表
    @RequestMapping(value = "/getSampleInfoBySearch/{search}")
    public String getSampleInfoBySearch(@PathVariable String search){
        List<MetroCenSampleInfo> sampleInfos=sampleInfoRepository.getSampleInfoBySearch("%"+search+"%");
        List<Map<String,String>> maps=new ArrayList<Map<String, String>>();

        String returnStr="";//返回字符串
        for (MetroCenSampleInfo sampleInfo:sampleInfos){
            Map<String,String>mp=new HashMap<String, String>();
            mp.put("value",String.valueOf(sampleInfo.getFactoryCode()));
            mp.put("text",sampleInfo.getFactoryCode());
            maps.add(mp);
        }
        returnStr=commF.getJsonDataByObject(maps);
        return returnStr;
    }
    @RequestMapping(value="/processRollBack/{processId}")
    public boolean executeTaskRollBack(@AuthenticationPrincipal User currentUser,@PathVariable String processId) {
        String taskId = "";
        String delegator="任务退回";
        try {
            Task temptask = this.taskService.createTaskQuery().processInstanceId(String.valueOf(processId)).singleResult();

            if (temptask != null) {
                taskId = temptask.getId();
            }
            this.managementService.executeCommand(new RollbackTaskCmd(String.valueOf(taskId)));
            String rollbackText = "rollback:" + delegator;

            String userId = currentUser.getUserName();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String taskStartTime = String.valueOf(format.format(new Date()));
            String nowTime = String.valueOf(format.format(new Date()));

            this.jdbcTemplate.execute("INSERT INTO metro.act_hi_comment(ID_,TIME_,USER_ID_,TASK_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('" + taskId + "','" + nowTime + "','" + userId + "','" + taskId + "','" + String.valueOf(processId) + "','rollback','" + delegator + "')");

            return true;
        }catch (Exception ex){
            return false;
        }

    }
    //根据检测项目ID，获取检测项目名称
    private String getSurveillanceProByIds(String ids){
        String SurveillanceProName="";
        MetroCenSurveillancePro surveillancePro=null;
        if (!ids.equals("")){
            String [] id=ids.split(",");
            for (int i=0;i<id.length;++i){
                surveillancePro=surveillanceProRepository.findOne(Long.parseLong(id[i]));
                if(id.length-i==1){
                    SurveillanceProName+=surveillancePro.getSurveillanceName();
                }else {
                    SurveillanceProName+=surveillancePro.getSurveillanceName()+",";
                }

            }
        }
        return
                SurveillanceProName;
    }
}
