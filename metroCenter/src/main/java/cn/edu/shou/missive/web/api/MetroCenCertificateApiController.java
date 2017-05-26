package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenCertificateForm;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.CommonFunction;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by seky on 15/3/4.
 */
@RestController
@RequestMapping(value = "MetroCen/api")
public class MetroCenCertificateApiController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MetroCenCertificateRepository cenCertificateRepository;
    @Autowired
    private CommonFunction commonFunction;
    @Autowired
    private MetroCenLocationEnvironmentRepository locationEnvironmentRepository;
    @Autowired
    private MetroCenReslutIllustrateRepository reslutIllustrateRepository;
    @Autowired
    private MetroCenSampleRepository sampleRepository;
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private MetroCenCommentRepository commentRepository;
    @Autowired
    private MetroCenDistributionRepository distributionRepository;
    @Autowired
    private MetroCenHistotyRepository histotyRepository;


    //设置certificate_path+1(收发人员下达补办证书命令)
    @RequestMapping(value = "/certificate/certificatePath/{certificateNo}/{certificateId}",method = RequestMethod.POST)
    public void updateCertificatePath(@PathVariable String certificateNo,@PathVariable long certificateId ){
        Object path=cenCertificateRepository.getMaxCertificatePathByCertificateNo(certificateNo);//查找证书信息
        MetroCenCertificate certificate=cenCertificateRepository.findOne(certificateId);//查找证书信息
        //MetroCenCertificate certificate=null;
        int iPath=path==null?1:Integer.parseInt(path.toString())+1;
        String aa=String.valueOf(iPath);
        certificate.setCertificatePath(aa);

        cenCertificateRepository.save(certificate);
    }

    //提交证书内容入库
    @RequestMapping(value = "/certificate/insert/{certificateId}",method = RequestMethod.POST)
    @Transactional(rollbackOn=Exception.class)
    public boolean insertCertificate(MetroCenCertificateForm certificateForm,@PathVariable long certificateId, @AuthenticationPrincipal User currentUser){

        MetroCenCertificate certificate=null;
        MetroCenLocationEnvironment locationEnvironment=null;
        MetroCenReslutIllustrate reslutIllustrate=null;
        String processStr="SampleCerti";
        String proDeId="";
        String taskId="";//任务ID
        String proInstanceId=certificateForm.getProcessId();//启动流程编号
        long checkedId=certificateForm.getChecked();//获取证书审核员Id，做为下一步流程用户
        String nextUser=userRepository.findOne(checkedId).getUserName();//下一步流程用户
        List<Map<String,? extends Object>> taskVarInfo=null;//用来接收下一步流程信息
        long locationEnvId=certificateForm.getLocationEnvId();//地点及环境条件Id
        long resultId=certificateForm.getResultId();//检定结果与说明Id
        try{
            //插入地点及环境条件
            locationEnvId=insertAndUpdateLocationEnvironment(locationEnvId,certificateId,certificateForm,currentUser);
            //插入检定结果与说明
            resultId=insertAndUpdateReslutIllustrate(resultId,certificateId,certificateForm,currentUser);
            //插入证书
            certificateForm.setResultId(resultId);//设置结果编号
            certificateForm.setLocationEnvId(locationEnvId);//设置检定地点及环境条件
            long returnCertificateId=insertAndUpdateCertificateInfo(certificateId,certificateForm,currentUser);
            //更新地点及环境条件数据表中的证书Id
            locationEnvId=insertAndUpdateLocationEnvironment(locationEnvId,returnCertificateId,certificateForm,currentUser);
            resultId=insertAndUpdateReslutIllustrate(resultId,returnCertificateId,certificateForm,currentUser);

            proDeId=commonFunction.getCurrentDeId(processStr);
            if (!proDeId.equals("")) {//流程实例id
                if(certificateId==0){//如果是制作证书
                    proInstanceId = activitiService.startProcess(proDeId, Long.parseLong("0"), currentUser);//启动流程
                    updateCertificateProcessId(returnCertificateId,proInstanceId);//更新证书数据表中的流程ID
                    //样品流转数据表中，证书制作标记更新为1
                    //updateDistributionCertificateSubmitById(certificateForm.getSampleId(),certificateForm.getSerialNumber());
                    Task task = activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
                    taskId = task.getId();
                }else {
                    //获取当前任务编号
                    if (!proInstanceId.equals("")&&!proInstanceId.equals(null)){//任务id
                        Task task = activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
                        taskId = task.getId();
                    }
                }

            }
            //完成证书制作流程
            taskVarInfo=activitiService.getNextTaskInfo(proInstanceId,Integer.parseInt(taskId));//获取流程需要的参数
            for (int i=0;i<taskVarInfo.size();++i){
                if (!taskVarInfo.get(i).containsValue("Back")){
                    Map<String,Object> vars = new HashMap<String, Object>();
                    String assineeVal=taskVarInfo.get(i).get("assignee").toString();//获取下一步
                    String conditionVal=taskVarInfo.get(i).get("condition").toString();//获取条件值
                    vars.put(assineeVal,nextUser);
                    vars.put("IsPass",conditionVal);
                    activitiService.completeTask(Long.parseLong(taskId),vars);
                }
            }
            return true;
        }catch (Exception ex){
            System.out.println("证书制作出问题了。");
            return false;
        }

    }
    //任务委托测试
    @RequestMapping(value = "/certificate/DelegateTask")
    public void DelegateTask(@AuthenticationPrincipal User currentUser){
        String processStr="SampleCerti";
        String proDeId="";
        String taskId="";//任务ID
        String proInstanceId="37501";
        List<Map<String,? extends Object>> taskVarInfo=null;//用来接收下一步流程信息
        proDeId=commonFunction.getCurrentDeId(processStr);
        //proInstanceId = activitiService.startProcess(proDeId, Long.parseLong("0"), currentUser);//启动流程
        Task task = activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
        taskId = task.getId();
        taskVarInfo=activitiService.getNextTaskInfo(proInstanceId,Integer.parseInt(taskId));//获取流程需要的参数
        for (int i=0;i<taskVarInfo.size();++i){
            if (!taskVarInfo.get(i).containsValue("Back")){
                Map<String,Object> vars = new HashMap<String, Object>();

                String assineeVal=taskVarInfo.get(i).get("assignee").toString();//获取下一步
                String conditionVal=taskVarInfo.get(i).get("condition").toString();//获取条件值

                vars.put(assineeVal,"zhaoling");
                vars.put("IsPass",conditionVal);
                activitiService.DelegateTask(taskId,currentUser.getUserName(),"heyong",vars);

            }
        }
    }
    //证书审核及插入领导意见
    @RequestMapping(value = "/certificate/examine/{processId}/{nextUserId}/{comments}/{chkProcess}")
    @Transactional(rollbackOn=Exception.class)
    public boolean certificateExamine(@PathVariable String processId,@PathVariable long nextUserId,@PathVariable String comments,@PathVariable String chkProcess,@AuthenticationPrincipal User currentUser ){
        String nextUser=nextUserId==0?"":userRepository.findOne(nextUserId).getUserName();//下一步流程用户
        List<Map<String,? extends Object>> taskVarInfo=null;//用来接收下一步流程信
        MetroCenComments cenComments=null;
        String taskId="";//任务Id
        try{
            //插入领导意见
            if (!comments.equals("pass")){//如果审核通过
                insertCertificateComments(0,1,processId,"certificate",comments,currentUser);
            }

            //获取当前任务编号
            if (processId != ""){//任务id
                Task task = activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(processId));
                taskId = task.getId();
            }
            //完成证书审核流程
            taskVarInfo=activitiService.getNextTaskInfo(processId,Integer.parseInt(taskId));//获取流程需要的参数
            if(taskVarInfo!=null){
                for (int i=0;i<taskVarInfo.size();++i){
                    if (taskVarInfo.get(i).containsValue(chkProcess)){
                        Map<String,Object> vars = new HashMap<String, Object>();

                        String assineeVal=chkProcess.equals("end")?"":taskVarInfo.get(i).get("assignee").toString();//获取下一步
                        String conditionVal=taskVarInfo.get(i).get("condition").toString();//获取条件值

                        vars.put(assineeVal,nextUser);
                        vars.put("IsPass",conditionVal);
                        activitiService.completeTask(Long.parseLong(taskId),vars);
                    }
                }
            }else {
                Map<String,Object> vars = new HashMap<String, Object>();
                //完成流程
                String assineeVal="";
                String conditionVal="";
                vars.put(assineeVal,nextUser);
                vars.put("IsPass",conditionVal);
                activitiService.completeTask(Long.parseLong(taskId),vars);
            }

            return true;
        }catch (Exception ex){
            System.out.println("证书审核出问题了。");
            return false;
        }

    }
    //根据locationEnvironment自动加载相关信息
    @RequestMapping(value = "/certificate/getLocationEnvironment/{look}")
    public List<MetroCenLocationEnvironment>getLocationEnvironment(@PathVariable String look){
        List<MetroCenLocationEnvironment> locationEnvironments=locationEnvironmentRepository.getLocationInfoByLocation("%"+look+"%");
        return locationEnvironments;
    }
    //根据locationEnvironment自动加载不确定度
    @RequestMapping(value = "/certificate/getUncertainty/{look}")
    public List<MetroCenCertificate>getUncertainty(@PathVariable String look){
        List<MetroCenCertificate> getUncertainty=cenCertificateRepository.getUncertaintyByLook("%"+look+"%");
        return getUncertainty;
    }
    //根据证书processId查找当前证书是否交接 张腾飞 2015 11 19
    @RequestMapping(value = "/certificate/getReturnCer/{processId}")
    public String getReturnCer(@PathVariable String processId){
        String returnCer=cenCertificateRepository.getCertificateReturnCerByProcessId(processId);//获取到证书交接状态

        return returnCer;
    }
    //根据证书编号 更新证书交接状态 郑小罗 20151122
    @RequestMapping(value = "/certificate/updateReturnCer/{certificateId}")
    public boolean updateReturnCer(@PathVariable long certificateId){
        try {
            MetroCenCertificate certificate=cenCertificateRepository.findOne(certificateId);//根据证实Id获取证书信息
            certificate.setReturnCer("已交接");//更新交接为”已交接“，状态不能修改
            cenCertificateRepository.save(certificate);
            return  true;
        }catch (Exception ex){
            return false;
        }


    }

    //插入和更新证书内容
    //插入和更新证书内容
    public  long insertAndUpdateCertificateInfo(long certificateId,MetroCenCertificateForm certificateForm,User currentUser){

        String countNo=certificateForm.getCertificateNo();
        countNo=countNo.substring(countNo.length()-3);//获取到证书编号
        int iCountNo=Integer.parseInt(countNo);//转换成int数据
        MetroCenCertificate certificate=null;
        if(certificateId>0){
            certificate=cenCertificateRepository.findOne(certificateId);
        }
        if (certificate==null){
            certificate=new MetroCenCertificate();
            certificate.setCreatedDate(commonFunction.getCurrentDateTime());//当前时间
            certificate.setCreatedBy(currentUser);//创建者
        }

        certificate.setInspectionUnit(certificateForm.getInspectionUnit());//送检单位
        certificate.setSampleName(certificateForm.getSampleName());//计量器具名称
        certificate.setSampleModel(certificateForm.getSampleModel());//型号/规格
        certificate.setSerialNumber(certificateForm.getSerialNumber());//出厂编号
        certificate.setManufactUnit(certificateForm.getManufactUnit());//制造单位
        certificate.setCertificateNo(certificateForm.getCertificateNo());//证书编号
        certificate.setVerificatReg(certificateForm.getVerificatReg());//检定依据
        certificate.setConclusion(certificateForm.getConclusion());//检定结论
        certificate.setApproved(certificateForm.getApproved());//批准人
        certificate.setChecked(certificateForm.getChecked());//核验员
        certificate.setVerified(certificateForm.getVerified());//检定员
        certificate.setCountNo(iCountNo);//证书编号数值

        certificate.setReturnCer(certificateForm.getReturnCer());//是否交接

        certificate.setCertificateType(String.valueOf(certificateForm.getModelId()));//证书类别 暂时先用模板Id作为证书类别

        certificate.setVerifiedDate(certificateForm.getVerifiedDate());//检定日期

        certificate.setValidityDate(certificateForm.getValidityDate());//有效期至
        certificate.setSampleId(certificateForm.getSampleId());//样品编号
        certificate.setDistributionId(certificateForm.getDistributionId());//证书分发者 郑小罗 20151123
        certificate.setCertificateCheckId(certificateForm.getChecked());//证书审核人员
        certificate.setAuthorize(certificateForm.getApproved());//授权
        certificate.setModelId(certificateForm.getModelId());//证书模板ID
        certificate.setUncertainty(certificateForm.getUncertainty());//扩展不确定度
        //根据分发样品Id获取样品客户信息
        if (certificateForm.getSampleId()>0){
           //20151123 郑小罗 程序调整 更新客户信息，使用sampleId查找客户信息
            //MetroCenDistribution distribution=distributionRepository.findOne(certificateForm.getSampleId());//根据分发样品Id获取到分发样品信息
            certificate.setClient(sampleRepository.getClientIdBySampleId(certificateForm.getSampleId()));//根据分发样品Id查找出样品ID
        }
        certificate.setLastModifiedBy(currentUser);//最后修改者
        certificate.setLastModifiedDate(commonFunction.getCurrentDateTime());//最后修改时间

        //检定地点、结果说明
        certificate.setLocationId(certificateForm.getLocationEnvId());
        certificate.setResultId(certificateForm.getResultId());
        long maxRetroactive=0;//获取补办证书最大值
        if(certificateForm.getRetroactive()==1){
            maxRetroactive=Long.parseLong(cenCertificateRepository.getMaxRetroactiveByCertificateNo(certificateForm.getCertificateNo()).toString())+1;
            certificate.setRetroactive(maxRetroactive);
        }else {
            certificate.setRetroactive(certificateForm.getRetroactive());
        }

        cenCertificateRepository.save(certificate);

        return certificate.getId();
    }
    //插入及更新检定地点及环境条件数据
    public long insertAndUpdateLocationEnvironment(long locationEnvId,long certificateId,MetroCenCertificateForm certificateForm,User currentUser){
        MetroCenLocationEnvironment locationEnvironment=null;
        if(locationEnvId>0){
            locationEnvironment=locationEnvironmentRepository.findOne(locationEnvId);
        }
        if (locationEnvironment==null){
            locationEnvironment=new MetroCenLocationEnvironment();
            locationEnvironment.setCreatedDate(commonFunction.getCurrentDateTime());//当前时间
            locationEnvironment.setCreatedBy(currentUser);//创建者
        }
        locationEnvironment.setLocation(certificateForm.getLocation());//地点
        locationEnvironment.setTemperature(certificateForm.getTemperature());//温度
        locationEnvironment.setRelativeHum(certificateForm.getRelativeHum());//相对湿度
        locationEnvironment.setOther(certificateForm.getOther());//其他
        locationEnvironment.setCertificateId(certificateId);//证书编号
        locationEnvironment.setLastModifiedDate(commonFunction.getCurrentDateTime());//最后修改时间
        locationEnvironment.setLastModifiedBy(currentUser);//最后修改者

        locationEnvironmentRepository.save(locationEnvironment);

        return locationEnvironment.getId();//返回Id
    }
    //插入及更新检定结果与说明数据
    public long insertAndUpdateReslutIllustrate(long resultId,long certificateId,MetroCenCertificateForm certificateForm,User currentUser){
        MetroCenReslutIllustrate reslutIllustrate=null;
        if(resultId>0){
            reslutIllustrate=reslutIllustrateRepository.findOne(resultId);
        }
        if (reslutIllustrate==null){
            reslutIllustrate=new MetroCenReslutIllustrate();
            reslutIllustrate.setCreatedDate(commonFunction.getCurrentDateTime());//当前时间
            reslutIllustrate.setCreatedBy(currentUser);//创建者
        }
        reslutIllustrate.setResults(certificateForm.getResults());//结果说明
        reslutIllustrate.setLastModifiedBy(currentUser);//最后修改者
        reslutIllustrate.setLastModifiedDate(commonFunction.getCurrentDateTime());//最后修改时间
        reslutIllustrate.setCertificateId(certificateId);//证书编号

        reslutIllustrateRepository.save(reslutIllustrate);

        return reslutIllustrate.getId();//返回结果Id
    }
    //更新样品流转的状态，根据样品流转Id
    public  void  updateDistributionCertificateSubmitById(long distributionId,String factoryCode){
        MetroCenDistribution distribution=distributionRepository.findOne(distributionId);
        if (!distribution.equals(null)){
            //判断样品的所有证书是否已经制作完成，也就是判断num是否为1
            if (distribution.getNum()==1){
                distribution.setCertifiSubmit(1);//更新为1，已制作证书
            }else {
                distribution.setNum(distribution.getNum()-1);
                factoryCode=distribution.getFactoryCode().replace(factoryCode+",","").replace(factoryCode,"");
                distribution.setFactoryCode(factoryCode);
            }

        }
        distributionRepository.save(distribution);
    }
    //更新证书的流程Id
    public void updateCertificateProcessId(long certificateId,String processId){
        MetroCenCertificate certificate=cenCertificateRepository.findOne(certificateId);//查找证书信息
        if(certificate!=null){
            certificate.setProcessId(processId);
        }
        cenCertificateRepository.save(certificate);
    }
    //插入审核意见
    public void insertCertificateComments(long commentsId,int isAbnormal,String processId,String processType,String context,User currentUser){
        MetroCenComments cenComments=null;
        if(commentsId>0){
            cenComments=commentRepository.findOne(commentsId);
        }
        if (cenComments==null){
            cenComments=new MetroCenComments();
            cenComments.setCreatedDate(commonFunction.getCurrentDateTime());//当前时间
            cenComments.setCreatedBy(currentUser);//创建者
        }
        cenComments.setIsAbnormal(isAbnormal);
        cenComments.setProcessId(processId);
        cenComments.setProcessType(processType);
        cenComments.setContext(context);
        cenComments.setUserId(currentUser.getId());

        commentRepository.save(cenComments);
    }

}
