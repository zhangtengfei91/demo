package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenDistributionForm;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenSampleForm;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.CommonFunction;
import cn.edu.shou.missive.web.MetroCenSampleController;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by seky on 15/1/15.
 * 对样品流程的相关操作
 */
@RestController
@RequestMapping(value = "MetroCen/api")
public class MetroCenSampleApiController {

    @Autowired
    MetroCenSampleRepository sampleRepository;
    @Autowired
    MetroCenClientRepository clientRepository;
    @Autowired
    CommonFunction commonFunction;
    @Autowired
    private ActivitiService actService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MetroCenDistributionRepository distributionRepository;
    @Autowired
    private MetroCenServiceTypeRepository serviceTypeRepository;
    @Autowired
    private MetroCenSampleMethodRepository sampleMethodRepository;
    @Autowired
    private MetroCenServiceWayRepository serviceWayRepository;
    @Autowired
    private MetroCenLabNameRepository labNameRepository;
    @Autowired
    private MetroCenStatusRepository statusRepository;
    @Autowired
    private MetroCenCharacterServiceRepository characterServiceRepository;
    @Autowired
    private MetroCenSampleInfoRepository sampleInfoRepository;
    @Autowired
    private MetroCenIdentifierRepository identifierRepository;




    //提交样品检测数据入库
    @RequestMapping(value = "/sample/insert/{clientId}/{sampleId}/{identifierId}",method = RequestMethod.POST)
    @Transactional(rollbackOn=Exception.class)
    public String insertSampleData(MetroCenSampleForm sampleForm,@PathVariable long clientId,@PathVariable long sampleId,@PathVariable long identifierId, @AuthenticationPrincipal User currentUser){
        //先插入客户信息，然后再插入样品数据，最后启动流程
        MetroCenClient cenClient=null;
        MetroCenSample cenSample=null;
        String [] factoryCodes=null;
        long DistributionId=sampleForm.getDistributionId();//获取分发用户ID
        int num=1;//
        try{
            cenClient=insertAndUpdateClientInfo(clientId,sampleForm,currentUser);//插入客户信息
            //需要对出厂编号集合进行拆分，然后一一入库或是更新
            factoryCodes=sampleForm.getFactoryCodes().split(",");//对出厂编号进行拆分
            for (int i=0;i<factoryCodes.length;++i){
                insertAndUpSampleInfo(sampleForm,factoryCodes[i],currentUser);//插入样品信息数据表
            }
            //样品数量
            num=factoryCodes.length;
            sampleForm.setSampleNum(num);//设置数量

            cenSample=insertAndUpdateSample(sampleId, cenClient, sampleForm, currentUser);//插入样品信息

            //将样品Id插入编号数据表
            updateSampleId(identifierId,cenSample.getId());
            //如果没有选择分发人员，样品先接收，后续再启动流程
            if(DistributionId!=0){
                startProcess(DistributionId,currentUser,cenSample.getId());//启动流程
            }


        }catch (Exception ex ){
            System.out.println("样品接收出问题了");
        }
        return "";
    }
    //插入样品流转信息
    @RequestMapping(value = "/distribution/insert",method = RequestMethod.POST)
    @Transactional(rollbackOn=Exception.class)
    public boolean insertDistributionData(MetroCenDistributionForm distributionForm,@AuthenticationPrincipal User currentUser){

        String proDeId="";
        String taskId="";//任务ID
        String proInstanceId=distributionForm.getProcessId();//流程编号
        String nextUser=userRepository.findOne(distributionForm.getAccreditedId()).getUserName();//下一步流程用户
        List<Map<String,? extends Object>> taskVarInfo=null;//用来接收下一步流程信息
        try {
            insertAndUpdateDistribution(0,distributionForm,currentUser);//0表示数据插入
            //更新样品状态信息
            updateSampleStatus(distributionForm.getSampleId(),distributionForm.getStatusName(),distributionForm.getSurveillancePro());
            //完成样品分发，进入样品检定
            //获取当前任务编号
            if (proInstanceId != ""){//任务id
                Task task = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
                taskId = task.getId();
            }
            taskVarInfo=actService.getNextTaskInfo(proInstanceId,Integer.parseInt(taskId));//获取流程需要的参数
            for (int i=0;i<taskVarInfo.size();++i){
                if (!taskVarInfo.get(i).containsValue("Back")){
                    Map<String,Object> vars = new HashMap<String, Object>();

                    String assineeVal=taskVarInfo.get(i).get("assignee").toString();//获取下一步
                    String conditionVal=taskVarInfo.get(i).get("condition").toString();//获取条件值
                    vars.put(assineeVal,nextUser);
                    vars.put("IsPass",conditionVal);
                    actService.completeTask(Long.parseLong(taskId),vars);
                }
            }

        }catch (Exception ex)
        {
            System.out.println("样品流转的过程出错了");
            return false;
        }
        return true;
    }
    //样品检定更新样品流转中的样品状态以及样品实际归还日期等信息
    @RequestMapping(value = "/distribution/update/{distributionId}",method = RequestMethod.POST)
    @Transactional(rollbackOn=Exception.class)
    public boolean updateDistributionData(MetroCenDistributionForm distributionForm,@AuthenticationPrincipal User currentUser,@PathVariable long distributionId){
        String proInstanceId=distributionForm.getProcessId();//流程编号
        String taskId="";
        String nextUser=userRepository.findOne(distributionForm.getDistributionId()).getUserName();//下一步流程用户
        List<Map<String,? extends Object>> taskVarInfo=null;//用来接收下一步流程信息
        try {

            if (distributionId!=0) {
                MetroCenDistribution distribution =distributionRepository.findOne(distributionId);//查询分发信息
                distribution.setActReturnDate(distributionForm.getActReturnDate());
                if (distributionForm.getStatusName()!=0) {
                    distribution.setStatusName(statusRepository.findOne(distributionForm.getStatusName()));
                    distribution.setRemark(distribution.getRemark());//退检原因
                }

                //流程进入收发仪器阶段
                //获取当前任务编号
                if (!proInstanceId.equals("")){//任务id
                    Task task = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
                    taskId = task.getId();//获取需要完成流程的任务ID

                    taskVarInfo=actService.getNextTaskInfo(proInstanceId,Integer.parseInt(taskId));//获取流程需要的参数
                    for (int i=0;i<taskVarInfo.size();++i){
                        if (!taskVarInfo.get(i).containsValue("Back")){
                            Map<String,Object> vars = new HashMap<String, Object>();

                            String assineeVal=taskVarInfo.get(i).get("assignee").toString();//获取下一步
                            String conditionVal=taskVarInfo.get(i).get("condition").toString();//获取条件值
                            vars.put(assineeVal,nextUser);
                            vars.put("IsPass",conditionVal);
                            actService.completeTask(Long.parseLong(taskId),vars);
                        }
                    }
                    Task task2 = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
                    taskId = task2.getId();//获取到需要流转的任务ID
                    distribution.setTaskId(taskId);//更新样品流转的任务ID
                    distributionRepository.save(distribution);//保存操作
                }

            }

        }catch (Exception ex){
            System.out.println("样品检测中有问题");
            return false;
        }

        return true;
    }
    //完成收发仪器流程
    @RequestMapping(value = "/distribution/sampleTran/{processId}/{sampleId}/{condition}/{distributionId}")
    public boolean sampleTran(@PathVariable String processId,@PathVariable long sampleId,@PathVariable String condition,@PathVariable long distributionId){
        String proInstanceId=processId;//流程编号
        String taskId="";
        String nextUser=distributionId==0?"":userRepository.findOne(distributionId).getUserName();//下一步流程用户
        List<Map<String,? extends Object>> taskVarInfo=null;//用来接收下一步流程信息
        try {
            //获取当前任务编号
            if (!proInstanceId.equals("")){//任务id
                Task task = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
                taskId = task.getId();

                taskVarInfo=actService.getNextTaskInfo(proInstanceId,Integer.parseInt(taskId));//获取流程需要的参数
                for (int i=0;i<taskVarInfo.size();++i){
                    if (taskVarInfo.get(i).containsValue(condition)){
                        Map<String,Object> vars = new HashMap<String, Object>();
                        String assineeVal="";//下一步流程用户
                        String conditionVal="";//流程条件
                        if (!condition.equals("end")){
                            assineeVal=taskVarInfo.get(i).get("assignee").toString();//获取下一步

                        }
                        conditionVal=taskVarInfo.get(i).get("condition").toString();//获取条件值
                        vars.put(assineeVal,nextUser);
                        vars.put("IsPass",conditionVal);
                        actService.completeTask(Long.parseLong(taskId),vars);
                    }
                }
            }
        }catch (Exception ex){
            System.out.println("完成仪器收发出现错误！");
        }
        return false;
    }
    //判断样品是否已经归还，根据样品ID
    @RequestMapping(value = "/distribution/isSampleBack/{sampleId}")
    public boolean isSampleBack(@PathVariable long sampleId){
        MetroCenDistribution distribution=distributionRepository.getDistributionBySampleId(sampleId);//获取到样品分发信息
        if (!distribution.equals(null)){
            if (distribution.getBack().equals("未还")){
                return false;
            }else {
                return true;
            }
        }else {
            return false;
        }
    }

    //根据样品流转Id，获取样品信息
    @RequestMapping(value = "/sample/getSampleInfo/{distributionId}")
    public MetroCenSample getSampleInfoByDistributionId(@PathVariable long distributionId){
        MetroCenDistribution distribution=distributionRepository.findOne(distributionId);//根据Id查找分发样品信息
        MetroCenSample sample=null;

        if (!distribution.equals(null)){
            sample=sampleRepository.findOne(distribution.getSampleId());//获取样品信息
           //其他无用的属性设置为null，方便转换
            sample.setRemark(sample.getClient().getUnitName());//使用备注字段存放送检单位信息
            sample.setFactoryCode(distribution.getFactoryCode());//出厂编号采用分发数据里面的出厂编号
            sample.setSurveillancePro(distribution.getSurveillancePro());//获取检测项目

            sample.setSampleNum(distribution.getNum());//数量
            sample.setServiceType(null);//
            sample.setServiceWay(null);
            sample.setSampleMethod(null);
            sample.setStatusName(null);
            sample.setCharacterService(null);
            sample.setClient(null);
            sample.setCreatedBy(null);
            sample.setLastModifiedBy(null);
        }
        return sample;
    }
    //返回所有客户信息
    @RequestMapping(value = "/sample/getClient/{look}")
    public List<MetroCenClient>getClient(@PathVariable String look){
        List<MetroCenClient> clients=clientRepository.getClientUnitNameByLook("%"+look+"%");
        return clients;
    }
    //返回检索样品信息
    @RequestMapping(value = "/sample/getSample/{look}")
    public List<MetroCenSampleForm>getSamples(@PathVariable String look){
        List<MetroCenSample>samples=sampleRepository.getSampleByLook("%"+look+"%");
        MetroCenSampleController sampleController=new MetroCenSampleController();
        List<MetroCenSampleForm>sampleForms=new ArrayList<MetroCenSampleForm>();
        for (MetroCenSample sample:samples){
            MetroCenSampleForm sampleForm=new MetroCenSampleForm();
            sampleForm=sampleController.bindSampleInfo(sampleForm,sample);
            sampleForms.add(sampleForm);
        }
        return sampleForms;
    }
    //根据用户获取该用户所有未取样品
    @RequestMapping(value = "/sample/distribution/getTakeSampleByUserId/{userId}")
    public List<MetroCenSampleForm>getAllGetSampleByUserId(@PathVariable long userId){
        List<MetroCenDistribution>distributions=distributionRepository.getTakeSampleByAccreditedId(userId,"未取");//获取未取样品
        List<MetroCenSampleForm>sampleForms=new ArrayList<MetroCenSampleForm>();
        for (MetroCenDistribution distribution:distributions){
            MetroCenSampleForm sampleForm=new MetroCenSampleForm();
            //根据分发样品Id获取样品Id，再根据样品Id获取客户信息 郑小罗 20151124
            MetroCenSample sample=sampleRepository.findOne(distribution.getSampleId());
            sampleForm.setUnitName(sample.getClient().getUnitName());//获取到单位名称
            sampleForm.setId(distribution.getId());//分发样品Id
            sampleForm.setSampleName(distribution.getSampleName());//分发样品名称
            sampleForm.setSampleNum(distribution.getNum());
            sampleForms.add(sampleForm);
        }
        return sampleForms;
    }
    //根据用户获取该用户所有未还样品
    @RequestMapping(value = "/sample/distribution/getBackSampleByUserId/{userId}")
    public List<MetroCenSampleForm>getAllBackSampleByUserId(@PathVariable long userId){
        List<MetroCenDistribution>distributions=distributionRepository.getBackSampleByAccreditedId(userId,"未还");//获取未还样品
        List<MetroCenSampleForm>sampleForms=new ArrayList<MetroCenSampleForm>();
        for (MetroCenDistribution distribution:distributions){
            MetroCenSampleForm sampleForm=new MetroCenSampleForm();
            //根据分发样品Id获取样品Id，再根据样品Id获取客户信息 郑小罗 20151124
            MetroCenSample sample=sampleRepository.findOne(distribution.getSampleId());
            sampleForm.setUnitName(sample.getClient().getUnitName());//获取到单位名称
            sampleForm.setId(distribution.getId());//分发样品Id
            sampleForm.setSampleName(distribution.getSampleName());//分发样品名称
            sampleForm.setSampleNum(distribution.getNum());
            sampleForms.add(sampleForm);
        }
        return sampleForms;
    }
    //获取数据库中的编号
    @RequestMapping(value ="/sample/Identifier/{sampleId}")
    public String getIdentifier(@PathVariable long sampleId){
        return identifierRepository.getSerialNumberBySampleId(sampleId).getSerialNumber();

    }

    //根据证书Id获取样品收发员Id和Name（证书交接需要）
    @RequestMapping(value = "/sample/getRecept/{sampleId}")
    public User getRecept(@PathVariable long sampleId){
        User user=new User();
        long userId=sampleRepository.getReceptIdBySampleId(sampleId);
        user.setId(userId);
        user.setName(userRepository.findUserCNameById(userId));
        return user;
    }







    //插入文件编号
    @RequestMapping(value = "/sample/Identifier/{identifierId}/{prefix}/{sampleId}")
    public MetroCenIdentifier insertAndUpdateIdentifier(@PathVariable long identifierId, @PathVariable String prefix,@PathVariable long sampleId,@AuthenticationPrincipal User currentUser){
        MetroCenIdentifier identifier=null;
        Object maxSerialNumber="001";
        if (identifierId>0){//如果Id大于0表示需要更新样品Id，否则就为新建编号
            identifier=identifierRepository.findOne(identifierId);//查找
            identifier.setSampleId(sampleId);
        }else {
            identifier=new MetroCenIdentifier();
            identifier.setCreatedDate(commonFunction.getCurrentDateTime());
            identifier.setCreatedBy(currentUser);
            identifier.setLastModifiedDate(commonFunction.getCurrentDateTime());
            identifier.setLastModifiedBy(currentUser);
            if (identifierRepository.getMaxSerialNumber()==null){
                prefix=prefix+maxSerialNumber.toString();
            }else {
                maxSerialNumber=identifierRepository.getMaxSerialNumber();//获取最大值
                //编号格式为2015001
                String numStr=maxSerialNumber.toString();//获取后面的编号数值
                //将数值转换成int
                long num=Integer.parseInt(numStr)+1;
                prefix=String.valueOf(num);
            }

            identifier.setSerialNumber(prefix);
            identifier.setType("sample");//类型为样品
        }
        identifierRepository.save(identifier);
        return identifier;
    }
    //删除样品
    @RequestMapping(value = "/sample/deleteSample/{sampleId}")
    @Transactional(rollbackOn=Exception.class)
    public boolean deleteSampleAndProcess(@PathVariable long sampleId){
        MetroCenSample sample=sampleRepository.findOne(sampleId);
        String processId=sample.getProcessId();//获取流程ID
        sampleRepository.deleteSample(sampleId);//删除样品数据
        distributionRepository.deleteDistribution(sampleId);//删除分发记录
        //删除流程
        actService.delProcessInstance(processId,"删除流程");
        return false;
    }

    //对暂存的样品进行处理
    @RequestMapping(value = "/sample/dealSample/{sampleId}/{DistributionId}/")
    @Transactional(rollbackOn=Exception.class)
    public boolean dealSampleBySampleId(@AuthenticationPrincipal User currentUser,@PathVariable long sampleId,@PathVariable long DistributionId){
        try{
            MetroCenSample cenSample=sampleRepository.findOne(sampleId);//根据样品编号，获取样品信息
            cenSample.setDistributionId(DistributionId);//更新分发Id
            sampleRepository.save(cenSample);//保存
            return startProcess(DistributionId,currentUser,sampleId);
        }catch (Exception ex){
            System.out.println("处理样品出错");
            return false;
        }

    }
    //更新样品流转中的取样品/归还样品状态以及时间
    @Transactional(rollbackOn=Exception.class)
    public boolean updateTakeAndBack(String type,long distributionId){
        try {
            if (distributionId > 0) {
                MetroCenDistribution distribution = distributionRepository.findOne(distributionId);//先查找出需要更新的样品
                //再判断是归还还是取样品
                if (type.equals("take")) {
                    distribution.setTake("已取样品");
                    distribution.setTakeTime(commonFunction.getCurrentDate());
                } else {
                    distribution.setBack("已还样品");
                    distribution.setBackTime(commonFunction.getCurrentDate());
                }
                distributionRepository.save(distribution);//保存
                return true;
            } else {
                return false;
            }
        }catch (Exception ex){
            System.out.println("样品在取样/归还的时候出错了");
            return false;
        }

    }
    //更新编号表中的样品Id
    private void updateSampleId(long identifierId,long sampleId){
        MetroCenIdentifier identifier=identifierRepository.findOne(identifierId);
        identifier.setSampleId(sampleId);
        identifierRepository.save(identifier);
    }

    //插入和更新客户信息表数据
    public MetroCenClient insertAndUpdateClientInfo(long clientId,MetroCenSampleForm sampleForm,User currentUser)
    {
        MetroCenClient cenClient=null;
        if(clientId>0){
            cenClient=clientRepository.findOne(clientId);
        }
        if (cenClient==null){
            cenClient=new MetroCenClient();
            cenClient.setCreatedDate(commonFunction.getCurrentDateTime());//当前时间
            cenClient.setCreatedBy(currentUser);//创建者
        }
        cenClient.setCertiCode(sampleForm.getCertiCode());
        cenClient.setCertiName(sampleForm.getCertiName());
        cenClient.setContacts(sampleForm.getContacts());
        cenClient.setContractNo(sampleForm.getContractNo());
        cenClient.setEmail(sampleForm.getEmail());
        cenClient.setPhone(sampleForm.getPhone());
        cenClient.setTelephone(sampleForm.getTelephone());
        cenClient.setUnitAddress(sampleForm.getUnitAddress());
        cenClient.setUnitName(sampleForm.getUnitName());
        cenClient.setLastModifiedDate(commonFunction.getCurrentDateTime());//修改日期
        cenClient.setLastModifiedBy(currentUser);//修改者
        clientRepository.save(cenClient);//保存数据

        return cenClient;
    }
    //插入和更新样品信息数据表
    public void insertAndUpSampleInfo(MetroCenSampleForm sampleForm,String factoryCode,User currentUser){
        MetroCenSampleInfo sampleInfo=sampleInfoRepository.findByFactoryCode(factoryCode);
        if (sampleInfo==null){
            sampleInfo=new MetroCenSampleInfo();
            sampleInfo.setCreatedBy(currentUser);
            sampleInfo.setCreatedDate(commonFunction.getCurrentDateTime());
        }
        sampleInfo.setLastModifiedBy(currentUser);
        sampleInfo.setLastModifiedDate(commonFunction.getCurrentDateTime());
        sampleInfo.setAccuracyLevel(sampleForm.getAccuracyLevel());
        sampleInfo.setFactoryName(sampleForm.getFactoryName());
        sampleInfo.setMeasureRange(sampleForm.getMeasureRange());
        sampleInfo.setSampleName(sampleForm.getSampleName());
        sampleInfo.setSpecificateModel(sampleForm.getSpecificateModel());
        sampleInfo.setFactoryCode(factoryCode);//出厂编号

        sampleInfoRepository.save(sampleInfo);
    }
    //插入和更新样品数据表数据
    public MetroCenSample insertAndUpdateSample(long sampleId,MetroCenClient cenClient,MetroCenSampleForm sampleForm,User currentUser){
        MetroCenSample cenSample=null;
        if (sampleId>0){
            cenSample=sampleRepository.findOne(sampleId);
        }
        if (cenSample==null){
            cenSample=new MetroCenSample();
            cenSample.setCreatedBy(currentUser);
            cenSample.setCreatedDate(commonFunction.getCurrentDateTime());
        }
        cenSample.setLastModifiedBy(currentUser);
        cenSample.setLastModifiedDate(commonFunction.getCurrentDateTime());
        cenSample.setAccuracyLevel(sampleForm.getAccuracyLevel());
        cenSample.setClient(cenClient);
        cenSample.setDistributionId(sampleForm.getDistributionId());
        cenSample.setFactoryName(sampleForm.getFactoryName());
        cenSample.setMeasureRange(sampleForm.getMeasureRange());
        cenSample.setPrincipalOther(sampleForm.getPrincipalOther());
        cenSample.setPrincipalRequre(sampleForm.getPrincipalRequre());

        cenSample.setPrincipalTestBaseOn(sampleForm.getPrincipalTestBaseOn());
        cenSample.setPrincipalTestRequre(sampleForm.getPrincipalTestRequre());
        cenSample.setReceptId(sampleForm.getReceptId());
        cenSample.setRemark(sampleForm.getRemark());
        cenSample.setSampleCode(sampleForm.getSampleCode());
        cenSample.setSampleName(sampleForm.getSampleName());
        cenSample.setSampleNum(sampleForm.getSampleNum());
        if (sampleForm.getStatusName()!=0){
            cenSample.setStatusName(statusRepository.findOne(sampleForm.getStatusName()));
        }

        cenSample.setSampleTest(sampleForm.getSampleTest());
        if (sampleForm.getSampleMethod()!=0){
            cenSample.setSampleMethod(sampleMethodRepository.findOne(sampleForm.getSampleMethod()));
        }

        cenSample.setReceptId(sampleForm.getReceptId());
        cenSample.setDistributionId(sampleForm.getDistributionId());
        if (sampleForm.getServiceType()!=0){
            cenSample.setServiceType(serviceTypeRepository.findOne(sampleForm.getServiceType()));
        }
        if(sampleForm.getServiceWay()!=0) {
            cenSample.setServiceWay(serviceWayRepository.findOne(sampleForm.getServiceWay()));
        }
        cenSample.setSpecificateModel(sampleForm.getSpecificateModel());
        cenSample.setSurveillancePro(sampleForm.getSurveillancePro());//检测项目

        if(sampleForm.getCharacterService()!=0){//特色服务
            cenSample.setCharacterService(characterServiceRepository.findOne(sampleForm.getCharacterService()));
        }
        cenSample.setFactoryCode(sampleForm.getFactoryCode());//出厂编号
        sampleRepository.save(cenSample);
        return cenSample;
    }
    //插入和更新样品流转数据
    private MetroCenDistribution insertAndUpdateDistribution(long distributionId, MetroCenDistributionForm distributionForm,@AuthenticationPrincipal User currentUser){
        MetroCenDistribution cenDistribution=null;
        //判断样品ID是否存在
        if(distributionId>0)
        {
            cenDistribution=distributionRepository.findOne(distributionId);
        }
        if (cenDistribution==null){
            cenDistribution=new MetroCenDistribution();
        }

        cenDistribution.setProcessId(distributionForm.getProcessId());
        cenDistribution.setStatusName(statusRepository.findOne(distributionForm.getStatusName()));
        cenDistribution.setSampleName(distributionForm.getSampleName());
        cenDistribution.setSampleCode(distributionForm.getSampleCode());
        if (distributionForm.getAccreditedId()!=0) {
            cenDistribution.setAccreditedId(userRepository.findOne(distributionForm.getAccreditedId()));
        }
        cenDistribution.setRemark(distributionForm.getRemark());
        cenDistribution.setActReturnDate(distributionForm.getActReturnDate());
        cenDistribution.setFactoryCode(distributionForm.getFactoryCode());
        if (distributionForm.getLabName()!=0){
            cenDistribution.setLabName(labNameRepository.findOne(distributionForm.getLabName()));
        }

        if (distributionForm.getProjectType()!=0) {
            cenDistribution.setProjectType(serviceTypeRepository.findOne(distributionForm.getProjectType()));
        }
        cenDistribution.setReceivedDate(distributionForm.getReceivedDate());
        cenDistribution.setRecReturnDate(distributionForm.getRecReturnDate());
        if (distributionForm.getReceiveId()!=0) {
            cenDistribution.setReceiveId(userRepository.findOne(distributionForm.getReceiveId()));
        }
        cenDistribution.setSampleId(distributionForm.getSampleId());
        cenDistribution.setCreatedBy(currentUser);
        cenDistribution.setLastModifiedBy(currentUser);
        cenDistribution.setBack(distributionForm.getBack());//未还
        cenDistribution.setTake(distributionForm.getTake());//未领
        if(distributionForm.getDistributionId()!=0) {
            cenDistribution.setDistributionId(distributionForm.getDistributionId());//分发者
        }
        cenDistribution.setSurveillancePro(distributionForm.getSurveillancePro());//检测项目
        cenDistribution.setCreatedDate(commonFunction.getCurrentDateTime());
        cenDistribution.setLastModifiedDate(commonFunction.getCurrentDateTime());

        //是否制作证书为0，后面才制作证书
        cenDistribution.setCertifiSubmit(0);

        //样品数量
        cenDistribution.setNum(distributionForm.getNum());


        distributionRepository.save(cenDistribution);

        return cenDistribution;
    }
    //更新样品信息中的流程ID信息
    private void updateSampleProcessID(long sampleId,String processId){
        MetroCenSample cenSample=sampleRepository.findOne(sampleId);//查找样品信息
        if(cenSample!=null){
            cenSample.setProcessId(processId);
        }
        sampleRepository.save(cenSample);

    }


    //更新样品状态信息以及更新检测项目
    private void updateSampleStatus(long sampleId,long statusId,String sampleDetected){
        MetroCenSample cenSample=sampleRepository.findOne(sampleId);//查找样品信息
        MetroCenStatus status=statusRepository.findOne(statusId);//查找状态信息
        //将已检测的项目从需要检测项目中删除
        String surveillancePro=cenSample.getSurveillancePro();//获取到需要检测项目
        surveillancePro=surveillancePro.replace(sampleDetected,",").replace(",,","");
        String oldSurveillancePro=cenSample.getSampleDetected();//原来已经检测的项目
        if (oldSurveillancePro!= null && !oldSurveillancePro.equals("")){

            sampleDetected=oldSurveillancePro+","+sampleDetected;
        }

        surveillancePro=surveillancePro.equals(",")?"":surveillancePro;
        if(cenSample!=null){
            cenSample.setStatusName(status);//更新状态
            cenSample.setSurveillancePro(surveillancePro);//更新检测项目
            cenSample.setSampleDetected(sampleDetected);//更新已检测项目,原来的检测项目需要保留
        }
        sampleRepository.save(cenSample);
    }
    //启动流程
    private boolean startProcess(long DistributionId,User currentUser,long sampleId) {
        String processStr = "SampleDistr";
        String proDeId = "";
        String taskId = "";//任务ID
        String proInstanceId = "";//启动流程编号
        String nextUser = "";//下一步流程用户
        List<Map<String, ? extends Object>> taskVarInfo = null;//用来接收下一步流程信息
        try {
            nextUser = userRepository.findOne(DistributionId).getUserName();//下一步流程用户
            proDeId = commonFunction.getCurrentDeId(processStr);
            if (proDeId != "") {//流程实例id
                proInstanceId = actService.startProcess(proDeId, Long.parseLong("0"), currentUser);//启动流程
                //获取当前任务编号
                if (proInstanceId != "") {//任务id
                    Task task = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
                    taskId = task.getId();
                }
                updateSampleProcessID(sampleId, proInstanceId);//更新样品数据表中的流程ID
            }
            //完成样品接收流程，进入样品分发
            taskVarInfo = actService.getNextTaskInfo(proInstanceId, Integer.parseInt(taskId));//获取流程需要的参数
            for (int i = 0; i < taskVarInfo.size(); ++i) {
                if (!taskVarInfo.get(i).containsValue("Back")) {
                    Map<String, Object> vars = new HashMap<String, Object>();

                    String assineeVal = taskVarInfo.get(i).get("assignee").toString();//获取下一步
                    String conditionVal = taskVarInfo.get(i).get("condition").toString();//获取条件值
                    vars.put(assineeVal, nextUser);
                    vars.put("IsPass", conditionVal);
                    actService.completeTask(Long.parseLong(taskId), vars);
                }
            }
            return true;
        } catch (Exception ex) {
            System.out.println("启动流程出问题了");
            return false;
        }
    }




}
