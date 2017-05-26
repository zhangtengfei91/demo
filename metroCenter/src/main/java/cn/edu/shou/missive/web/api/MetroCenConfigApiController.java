package cn.edu.shou.missive.web.api;


import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/5 0005.
 */
@RestController
@RequestMapping(value ="/MetroCen/api/config" )
public class MetroCenConfigApiController {

    @Autowired
    MetroCenProcessTypeRepository processTypeRepository;
    @Autowired
    MetroCenTaskNameRepository taskNameRepository;
    @Autowired
    MetroCenMissiveFieldRepository missiveFieldRepository;

    @Autowired
    MetroCenCertificateModelRepository  certificateModelRepository;
    @Autowired
    MetroCenCertificateBackGroundRepository certificateBackGroundRepository;

    /*周雪楠师姐*/
    @Autowired
    MetroCenSampleMethodRepository sampleMethodRepository;
    @Autowired
    MetroCenServiceTypeRepository serviceTypeRepository;
    @Autowired
    MetroCenServiceWayRepository serviceWayRepository;
    //张腾飞 短信模板
    @Autowired
    MetroCenMessageModelRepository messageModelRepository;
    /*芳芳*/
    @Autowired
    MetroCenStatusRepository statusRepository;
    @Autowired
    MetroCenSurveillanceProRepository surveillanceProRepository;


    /*陈珂*/
    @Autowired
    MetroCenCharacterServiceRepository characterServiceRepository;
    @Autowired
    MetroCenLabNameRepository labNameRepository;
    //张腾飞 20151217
    @Autowired

    //短信服务
   // @RequestMapping (value =  "/createProcessType",method = RequestMethod.GET)

    //read获取所有流程类型
    @RequestMapping(value = "/processType",method = RequestMethod.GET)
    public List<MetroCenProcessType> getProcessType(){
        List<MetroCenProcessType> typeList=processTypeRepository.findAll();
        return  typeList;
    }


    //read获取所有任务
    @RequestMapping(value = "/taskName",method = RequestMethod.GET)
    public List<MetroCenTaskName> getTaskName(){
        List<MetroCenTaskName> taskList=taskNameRepository.findAll();

        return taskList;
    }

    //read获取所有 字段属性
    @RequestMapping(value = "/missiveField",method = RequestMethod.GET)
    public List<MetroCenMissiveField> getMissiveField(){
        return missiveFieldRepository.findAll();
    }


    //创建流程类型
    @RequestMapping(value = "/createProcessType",method = RequestMethod.GET)
    public List<MetroCenProcessType> createProcessType(@RequestParam String typeName){
        MetroCenProcessType pp=new MetroCenProcessType();
        pp.setTypeName(typeName);
        processTypeRepository.save(pp);
        List<MetroCenProcessType> list=new ArrayList<MetroCenProcessType>();
        list.add(pp);
        return list;
    }
    //创建任务
    @RequestMapping(value = "/createTaskName",method =RequestMethod.GET)
    public List<MetroCenTaskName> createTaskName(@RequestParam String taskName,@RequestParam long processTypeId ){
        MetroCenTaskName ss=new MetroCenTaskName();
        ss.setTaskName(taskName);
        ss.setProcessTypeId(processTypeId);
        taskNameRepository.save(ss);
        List<MetroCenTaskName> list=new ArrayList<MetroCenTaskName>();
        list.add(ss);
        return list;

    }


    //创建 字段属性
    @RequestMapping(value = "/createMissiveField",method =RequestMethod.GET)
    public List<MetroCenMissiveField> createMissiveField(@RequestParam String fieldName,@RequestParam long processTypeId){
        MetroCenMissiveField dd=new MetroCenMissiveField();
        dd.setFieldName(fieldName);
        dd.setProcessTypeId(processTypeId);
        missiveFieldRepository.save(dd);
        List<MetroCenMissiveField> list=new ArrayList<MetroCenMissiveField>();
        list.add(dd);
        return list;

    }

    //更新流程类型
    @RequestMapping(value = "updateProcessType")
    public List<MetroCenProcessType> updateProcessType(@RequestParam long id,@RequestParam String typeName){
        MetroCenProcessType pp=processTypeRepository.findOne(id);
        pp.setTypeName(typeName);
        MetroCenProcessType updateProcessType=processTypeRepository.save(pp);
        List<MetroCenProcessType> processTypeList=new ArrayList<MetroCenProcessType>();
        processTypeList.add(updateProcessType);
        return processTypeList;
    }

    //更新任务
    @RequestMapping(value = "updateTaskName")
    public List<MetroCenTaskName> updateTaskName(@RequestParam long id,@RequestParam String taskName,@RequestParam long processTypeId ){
        MetroCenTaskName ss=taskNameRepository.findOne(id);
        ss.setProcessTypeId(processTypeId);
        ss.setTaskName(taskName);
        MetroCenTaskName updateTaskName=taskNameRepository.save(ss);
        List<MetroCenTaskName> TaskNameList=new ArrayList<MetroCenTaskName>();
        TaskNameList.add(updateTaskName);
        return TaskNameList;
    }

    //更新字段属性
    @RequestMapping(value = "updateMissiveField")
    public List<MetroCenMissiveField> updateMissiveField(@RequestParam long id,@RequestParam String fieldName,@RequestParam long processTypeId){
        MetroCenMissiveField dd=missiveFieldRepository.findOne(id);
        dd.setProcessTypeId(processTypeId);
        dd.setFieldName(fieldName);

        MetroCenMissiveField updateMissiveField=missiveFieldRepository.save(dd);
        List<MetroCenMissiveField> metroCenMissiveFieldList=new ArrayList<MetroCenMissiveField>();
        metroCenMissiveFieldList.add(updateMissiveField);
        return metroCenMissiveFieldList;
    }

    //删除流程类型
    @RequestMapping(value = "deleteProcessType")
    public List<MetroCenProcessType> deleteProcessType(@RequestParam long id){
        MetroCenProcessType pp=processTypeRepository.findOne(id);
        processTypeRepository.delete(pp);
        List<MetroCenProcessType> processTypeList=new ArrayList<MetroCenProcessType>();
        processTypeList.add(pp);
        return processTypeList;

    }

    //删除任务
    @RequestMapping(value = "deleteTaskName")
    public List<MetroCenTaskName> deleteTaskName(@RequestParam long id){
        MetroCenTaskName ss=taskNameRepository.findOne(id);
        taskNameRepository.delete(ss);
        List<MetroCenTaskName> TaskNameList=new ArrayList<MetroCenTaskName>();
        TaskNameList.add(ss);
        return TaskNameList;

    }

    //删除字段属性
    @RequestMapping(value = "deleteMissiveField")
    public List<MetroCenMissiveField> deleteMissiveField(@RequestParam long id){
        MetroCenMissiveField dd=missiveFieldRepository.findOne(id);
        missiveFieldRepository.delete(dd);
        List<MetroCenMissiveField> metroCenMissiveFieldList=new ArrayList<MetroCenMissiveField>();
        metroCenMissiveFieldList.add(dd);
        return metroCenMissiveFieldList;

    }




    //    read获取所有  一级模板
    @RequestMapping(value = "/model",method = RequestMethod.GET)
    public List<MetroCenCertificateModel> getMetroCenCertificateModel(){
        List<MetroCenCertificateModel> modelList=certificateModelRepository.findByParentId(0);
        return modelList;
    }
    //    read获取所有  二级模板
    @RequestMapping(value = "/modelLevelTwo",method = RequestMethod.GET)
    public List<MetroCenCertificateModel> getMetroCenCertificateModelLevelTwo(){
        List<MetroCenCertificateModel> modelList=certificateModelRepository.findChildModel(0);
        return modelList;
    }


    //删除任务
    @RequestMapping(value = "deleteModel")
    public List<MetroCenCertificateModel> deleteMetroCenCertificateModel(@RequestParam long id){
        MetroCenCertificateModel ss=certificateModelRepository.findOne(id);
        certificateModelRepository.delete(ss);
        List<MetroCenCertificateModel> list=new ArrayList<MetroCenCertificateModel>();
        list.add(ss);
        return list;

    }


    /*周雪楠师姐*/
    //read sampleMethod
    @RequestMapping(value = "/sampleMethod",method = RequestMethod.GET)
    public List<MetroCenSampleMethod> getSampleMethod(){
        List<MetroCenSampleMethod> typeList=sampleMethodRepository.findAll();
        return  typeList;
    }
    //create sampleMethod
    @RequestMapping(value = "/createSampleMethod",method = RequestMethod.GET)
    public List<MetroCenSampleMethod> createSampleMethod(@RequestParam String sampleMethod){
        MetroCenSampleMethod pp=new MetroCenSampleMethod();
        pp.setSampleMethod(sampleMethod);
        sampleMethodRepository.save(pp);
        List<MetroCenSampleMethod> list=new ArrayList<MetroCenSampleMethod>();
        list.add(pp);
        return list;
    }
    //update sampleMethod
    @RequestMapping(value = "updateSampleMethod")
    public List<MetroCenSampleMethod> updateSampleMethod(@RequestParam long id,@RequestParam String sampleMethod){
        MetroCenSampleMethod pp=sampleMethodRepository.findOne(id);
        pp.setSampleMethod(sampleMethod);
        MetroCenSampleMethod updateSampleMethod=sampleMethodRepository.save(pp);
        List<MetroCenSampleMethod> sampleMethodList=new ArrayList<MetroCenSampleMethod>();
        sampleMethodList.add(updateSampleMethod);
        return sampleMethodList;
    }
    //delete sampleMethod
    @RequestMapping(value = "deleteSampleMethod")
    public List<MetroCenSampleMethod> deleteSampleMethod(@RequestParam long id){
        MetroCenSampleMethod pp=sampleMethodRepository.findOne(id);
        sampleMethodRepository.delete(pp);
        List<MetroCenSampleMethod> sampleMethodList=new ArrayList<MetroCenSampleMethod>();
        sampleMethodList.add(pp);
        return sampleMethodList;

    }


    //serviceType增删改查
    @RequestMapping(value = "/serviceType",method = RequestMethod.GET)
    public List<MetroCenServiceType> getServiceType(){
        List<MetroCenServiceType> taskList=serviceTypeRepository.findAll();

        return taskList;
    }
    @RequestMapping(value = "/createServiceType",method =RequestMethod.GET)
    public List<MetroCenServiceType> createServiceType(@RequestParam String serviceName ){

        MetroCenServiceType ss=new MetroCenServiceType();
        ss.setServiceName(serviceName);
        serviceTypeRepository.save(ss);
        List<MetroCenServiceType> list=new ArrayList<MetroCenServiceType>();
        list.add(ss);
        return list;

    }
    @RequestMapping(value = "updateServiceType")
    public List<MetroCenServiceType> updateServiceType(@RequestParam long id,@RequestParam String serviceName ){
        MetroCenServiceType ss=serviceTypeRepository.findOne(id);
        ss.setId(id);
        ss.setServiceName(serviceName);
        MetroCenServiceType updateServiceType=serviceTypeRepository.save(ss);
        List<MetroCenServiceType> ServiceTypeList=new ArrayList<MetroCenServiceType>();
        ServiceTypeList.add(updateServiceType);
        return ServiceTypeList;
    }
    @RequestMapping(value = "deleteServiceType")
    public List<MetroCenServiceType> deleteServiceType(@RequestParam long id){
        MetroCenServiceType ss=serviceTypeRepository.findOne(id);
        serviceTypeRepository.delete(ss);
        List<MetroCenServiceType> ServiceTypeList=new ArrayList<MetroCenServiceType>();
        ServiceTypeList.add(ss);
        return ServiceTypeList;

    }


    //serviceWay增删改查
    @RequestMapping(value = "/serviceWay",method = RequestMethod.GET)
    public List<MetroCenServiceWay> getServiceWay(){
        return serviceWayRepository.findAll();
    }
    @RequestMapping(value = "/createServiceWay",method =RequestMethod.GET)
    public List<MetroCenServiceWay> createServiceWay(@RequestParam String serviceWay){
        MetroCenServiceWay dd=new MetroCenServiceWay();
        dd.setServiceWay(serviceWay);
        serviceWayRepository.save(dd);
        List<MetroCenServiceWay> list=new ArrayList<MetroCenServiceWay>();
        list.add(dd);
        return list;

    }
    @RequestMapping(value = "updateServiceWay")
    public List<MetroCenServiceWay> updateServiceWay(@RequestParam long id,@RequestParam String serviceWay){
        MetroCenServiceWay dd=serviceWayRepository.findOne(id);
        dd.setId(id);
        dd.setServiceWay(serviceWay);
        MetroCenServiceWay updateServiceWay=serviceWayRepository.save(dd);
        List<MetroCenServiceWay> metroCenServiceWayList=new ArrayList<MetroCenServiceWay>();
        metroCenServiceWayList.add(updateServiceWay);
        return metroCenServiceWayList;
    }
    @RequestMapping(value = "deleteServiceWay")
    public List<MetroCenServiceWay> deleteServiceWay(@RequestParam long id){
        MetroCenServiceWay dd=serviceWayRepository.findOne(id);
        serviceWayRepository.delete(dd);
        List<MetroCenServiceWay> metroCenServiceWayList=new ArrayList<MetroCenServiceWay>();
        metroCenServiceWayList.add(dd);
        return metroCenServiceWayList;

    }

    //messagemodel增删改查 张腾飞20151221
    @RequestMapping(value = "/messageModel",method = RequestMethod.GET)
    public List<MetroCenMessageModel> getMessageModel(){
        List<MetroCenMessageModel> typeList=messageModelRepository.findAll();
        return typeList;
    }
    @RequestMapping(value = "/createMessageModel",method =RequestMethod.GET)
    public List<MetroCenMessageModel> createMessageModel(@RequestParam String taskName,@RequestParam String msgContent){
        MetroCenMessageModel dd=new MetroCenMessageModel();
        dd.setTaskName(taskName);
        dd.setMsgContent(msgContent);
        messageModelRepository.save(dd);
        List<MetroCenMessageModel> list=new ArrayList<MetroCenMessageModel>();
        list.add(dd);
        return list;

    }
    @RequestMapping(value = "updateMessageModel")
    public List<MetroCenMessageModel> updateMessageModel(@RequestParam long id,@RequestParam String taskName,@RequestParam String msgContent){
        MetroCenMessageModel dd=messageModelRepository.findOne(id);
        dd.setTaskName(taskName);
        dd.setMsgContent(msgContent);
        MetroCenMessageModel updateMessageModel=messageModelRepository.save(dd);
        List<MetroCenMessageModel> metroCenMessageModelList=new ArrayList<MetroCenMessageModel>();
        metroCenMessageModelList.add(updateMessageModel);
        return metroCenMessageModelList;
    }
    @RequestMapping(value = "deleteMessageModel")
    public List<MetroCenMessageModel> deleteMessageModel(@RequestParam long id){
        MetroCenMessageModel dd=messageModelRepository.findOne(id);
        messageModelRepository.delete(dd);
        List<MetroCenMessageModel> metroCenMessageModelList=new ArrayList<MetroCenMessageModel>();
        metroCenMessageModelList.add(dd);
        return metroCenMessageModelList;

    }

    /*芳芳*/


    //read获取所有样品状态
    @RequestMapping(value = "/status",method = RequestMethod.GET)
    public List<MetroCenStatus> getStatus(){
        List<MetroCenStatus> taskList=statusRepository.findAll();

        return taskList;
    }

    //创建样品状态
    @RequestMapping(value = "/createStatus",method =RequestMethod.GET)
    public List<MetroCenStatus> createStatus(@RequestParam String statusName) {
        MetroCenStatus dd=new MetroCenStatus();
        dd.setStatusName(statusName);
        statusRepository.save(dd);
        List<MetroCenStatus> list=new ArrayList<MetroCenStatus>();
        list.add(dd);
        return list;
    }

    //更新样品状态
    @RequestMapping(value = "updateStatus")
    public List<MetroCenStatus> updateStatus(@RequestParam long id,@RequestParam String statusName){
        MetroCenStatus dd=statusRepository.findOne(id);
        dd.setStatusName(statusName);
        MetroCenStatus updateStatus=statusRepository.save(dd);
        List<MetroCenStatus> metroCenStatusList=new ArrayList<MetroCenStatus>();
        metroCenStatusList.add(updateStatus);
        return metroCenStatusList;
    }
    //删除样品状态
    @RequestMapping(value = "deleteStatus")
    public List<MetroCenStatus> deleteStatus(@RequestParam long id){
        MetroCenStatus dd=statusRepository.findOne(id);
        statusRepository.delete(dd);
        List<MetroCenStatus> metroCenStatusList=new ArrayList<MetroCenStatus>();
        metroCenStatusList.add(dd);
        return metroCenStatusList;
    }


    //read获取所有监测项目
    @RequestMapping(value = "/surveillancePro",method = RequestMethod.GET)
    public List< MetroCenSurveillancePro> getSurveillancePro(){
        List<MetroCenSurveillancePro> typeList=surveillanceProRepository.findAll();
        return typeList;
    }

    //创建监测项目
    @RequestMapping(value = "/createSurveillancePro",method =RequestMethod.GET)
    public List<MetroCenSurveillancePro> createSurveillancePro(@RequestParam String surveillanceName,@RequestParam String lab) {
        MetroCenSurveillancePro ss=new MetroCenSurveillancePro();
        ss.setSurveillanceName(surveillanceName);
        ss.setLab(lab);
        surveillanceProRepository.save(ss);
        List<MetroCenSurveillancePro> list=new ArrayList<MetroCenSurveillancePro>();
        list.add(ss);
        return list;
    }
    //更新监测项目
    @RequestMapping(value = "updateSurveillancePro")
    public List<MetroCenSurveillancePro> updateSurveillancePro(@RequestParam long id,@RequestParam String surveillanceName,@RequestParam String lab){
        MetroCenSurveillancePro ss=surveillanceProRepository.findOne(id);
        ss.setSurveillanceName(surveillanceName);
        ss.setLab(lab);
        MetroCenSurveillancePro updateSurveillancePro=surveillanceProRepository.save(ss);
        List<MetroCenSurveillancePro> metroCenSurveillanceProList=new ArrayList<MetroCenSurveillancePro>();
        metroCenSurveillanceProList.add(updateSurveillancePro);
        return metroCenSurveillanceProList;
    }
    //删除监测项目
    @RequestMapping(value = "deleteSurveillancePro")
    public List<MetroCenSurveillancePro> deleteSurveillancePro(@RequestParam long id){
        MetroCenSurveillancePro ss=surveillanceProRepository.findOne(id);
        surveillanceProRepository.delete(ss);
        List<MetroCenSurveillancePro> metroCenSurveillanceProList=new ArrayList<MetroCenSurveillancePro>();
        metroCenSurveillanceProList.add(ss);
        return metroCenSurveillanceProList;

    }


    //read获取所有特色服务
    @RequestMapping(value = "/characterService",method = RequestMethod.GET)
    public List<MetroCenCharacterService> getCharacterService(){
        List<MetroCenCharacterService> characterServiceList=characterServiceRepository.findAll();
        return  characterServiceList;
    }

    //创建特色服务
    @RequestMapping(value = "/createCharacterService",method = RequestMethod.GET)
    public List<MetroCenCharacterService> createCharacterService(@RequestParam String serviceName){
        MetroCenCharacterService pp=new MetroCenCharacterService();
        pp.setServiceName(serviceName);
        characterServiceRepository.save(pp);

        List<MetroCenCharacterService> list=new ArrayList<MetroCenCharacterService>();
        list.add(pp);
        return list;
    }


    //更新特色服务
    @RequestMapping(value = "updateCharacterService")
    public List<MetroCenCharacterService> updateCharacterService(@RequestParam long id,@RequestParam String serviceName){
        MetroCenCharacterService pp=characterServiceRepository.findOne(id);
        pp.setServiceName(serviceName);

        MetroCenCharacterService updateCharacterService=characterServiceRepository.save(pp);
        List<MetroCenCharacterService> characterServiceList=new ArrayList<MetroCenCharacterService>();
        characterServiceList.add(updateCharacterService);
        return characterServiceList;
    }

    //删除特色服务
    @RequestMapping(value = "deleteCharacterService")
    public List<MetroCenCharacterService> deleteCharacterService(@RequestParam long id){
        MetroCenCharacterService pp=characterServiceRepository.findOne(id);
        characterServiceRepository.delete(pp);
        List<MetroCenCharacterService> characterServiceList=new ArrayList<MetroCenCharacterService>();
        characterServiceList.add(pp);
        return characterServiceList;

    }


    //read获取所有实验室名称
    @RequestMapping(value = "/labName",method = RequestMethod.GET)
    public List<MetroCenLabName> getLabName(){
        List<MetroCenLabName> labNameList=labNameRepository.findAll();
        return  labNameList;
    }

    //创建实验室名称
    @RequestMapping(value = "/createLabName",method = RequestMethod.GET)
    public List<MetroCenLabName> createLabName(@RequestParam String  labName){
        MetroCenLabName pp=new MetroCenLabName();
        pp.setLabName( labName);
        labNameRepository.save(pp);

        List<MetroCenLabName> list=new ArrayList<MetroCenLabName>();
        list.add(pp);
        return list;
    }


    //更新实验室名称
    @RequestMapping(value = "updateLabName")
    public List<MetroCenLabName> updateLabName(@RequestParam long id,@RequestParam String labName){
        MetroCenLabName pp=labNameRepository.findOne(id);
        pp.setLabName(labName);

        MetroCenLabName updateLabName=labNameRepository.save(pp);
        List<MetroCenLabName> labNameList=new ArrayList<MetroCenLabName>();
        labNameList.add(updateLabName);
        return labNameList;
    }

    //删除实验室名称
    @RequestMapping(value = "deleteLabName")
    public List<MetroCenLabName> deleteLabName(@RequestParam long id){
        MetroCenLabName pp=labNameRepository.findOne(id);
        labNameRepository.delete(pp);
        List<MetroCenLabName> labNameList=new ArrayList<MetroCenLabName>();
        labNameList.add(pp);
        return labNameList;

    }







}
