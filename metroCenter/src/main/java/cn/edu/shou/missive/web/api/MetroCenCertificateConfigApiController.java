package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.MetroCenStandardEquipmentHistory;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenCertificateModelForm;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenConfigFileForm;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenReslutModelForm;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenStandardEquipmentForm;
import cn.edu.shou.missive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/27 0027.
 */
@RestController
@RequestMapping(value ="/MetroCen/api/certificateConfig" )
public class MetroCenCertificateConfigApiController {
    @Autowired
    MetroCenCertificateModelRepository  certificateModelRepository;
    @Autowired
    MetroCenCertificateBackGroundRepository certificateBackGroundRepository;
    @Autowired
    MetroCenStandardEquipmentRepository standardEquipmentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MetroCenStandardEquipmentHistoryRepository standardEquipmentHistoryRepository;

    @Autowired
    MetroCenReslutModelRepository resultModelRepository;
    @Autowired
    MetroCenReslutModelHistoryRepository resultModelHistoryRepository;
    @Autowired
    cn.edu.shou.missive.web.CommonFunction commonFunction;
    @Autowired
    MetroCenSampleInfoRepository sampleInfoRepository;


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


    //创建模板
    @RequestMapping(value = "/createCertifiModel",method =RequestMethod.POST)
    public boolean createMetroCenCertificateModel(MetroCenCertificateModelForm modelForm,@AuthenticationPrincipal User currentUser){

        MetroCenCertificateModel model=null;
        long modelId=modelForm.getId();
        /*更新证书模板*/
        if (modelId>0){
            model=certificateModelRepository.findOne(modelId);
        }
          /* 创建证书模板*/
        if(model==null){
            model=new MetroCenCertificateModel();
            model.setCreatedDate(commonFunction.getCurrentDateTime());
            model.setCreatedBy(currentUser);

        }
        model.setLab(modelForm.getLab());
        model.setPageHeight(modelForm.getPageHeight());
        model.setNextPage(modelForm.getNextPage());
        model.setModelCode(modelForm.getModelCode());
        model.setModelName(modelForm.getModelName());
        model.setParentId(modelForm.getParentId());
        model.setLastModifiedBy(currentUser);
        model.setLastModifiedDate(commonFunction.getCurrentDateTime());
        certificateModelRepository.save(model);
        return  true;

    }

    //获取当前用户权限,证书模板
    @RequestMapping(value = "/getCertifiModelAuthority/{modelId}",method =RequestMethod.GET)
    public boolean modelAuthority(@AuthenticationPrincipal User currentUser,@PathVariable long modelId,MetroCenCertificateModelForm modelForm){
        String labs=currentUser.getLab();  //获取当前用户负责的实验室

        if(modelId==0){
            String  newModelLab=modelForm.getLab();//如果是添加数据，id为0，则获取添加数据的实验室
            if(labs.contains(newModelLab)){
                return  true;
            }else {
                return false;
            }
        } else{
            MetroCenCertificateModel model=certificateModelRepository.findOne(modelId);
            String modelLab=model.getLab();//模板所在的实验室

            if(labs.contains(modelLab)){
                return true;
            }else {
                return false;
            }

        }

    }

    //删除证书模板
    @RequestMapping(value = "/deleteCertifiModel/{certifiModelId}")
    public List<MetroCenCertificateModel> deleteMetroCenCertificateModel(@PathVariable long certifiModelId){
        MetroCenCertificateModel ss=certificateModelRepository.findOne(certifiModelId);
        certificateModelRepository.delete(ss);
        List<MetroCenCertificateModel> list=new ArrayList<MetroCenCertificateModel>();
        list.add(ss);
        return list;

    }


    //read获取标准器具
    @RequestMapping(value = "/standardEquipment",method = RequestMethod.GET)
    public List<MetroCenStandardEquipment> getStandardEquipment(){
        List<MetroCenStandardEquipment> standardEquipmentList =(List<MetroCenStandardEquipment>) standardEquipmentRepository.findAll();
        return standardEquipmentList;
    }




    //获取当前用户权限
    @RequestMapping(value = "/getEquipAuthority/{equipId}",method =RequestMethod.GET)
    public boolean equipAuthority(@AuthenticationPrincipal User currentUser,@PathVariable long equipId,MetroCenStandardEquipmentForm standardEquipmentForm){
        String labs=currentUser.getLab();  //获取当前用户负责的实验室

        if(equipId==0){
            long newEquipModeId=standardEquipmentForm.getModelId();//如果是添加数据，id为0，则获取添加数的模板id
            MetroCenCertificateModel newModel=certificateModelRepository.findOne(newEquipModeId);//根据模板id获取证书模板
            String newModelLab=newModel.getLab();//获取证书模板所在实验室
            if(labs.contains(newModelLab)){
                return  true;
            }else {
                return false;
            }
        } else{
            MetroCenStandardEquipment equipment=standardEquipmentRepository.findOne(equipId);
            long modelId=equipment.getModelId();
            MetroCenCertificateModel cenCertificateModel=certificateModelRepository.findOne(modelId);//获取到证书模板信息
            String modelLab=cenCertificateModel.getLab();//模板所在的实验室

            if(labs.contains(modelLab)){
                return true;
            }else {
                return false;
            }

        }


    }




    /*标准器具创建*/
    @RequestMapping(value = "/createStandardEquipment",method =RequestMethod.POST)
    public boolean createStandardEquipment(MetroCenStandardEquipmentForm standardEquipmentForm,@AuthenticationPrincipal User currentUser){
        MetroCenStandardEquipment standardEquipment=null;
        long equipmentId=standardEquipmentForm.getId();
        /*更新器具*/
        if(equipmentId>0){
            standardEquipment=standardEquipmentRepository.findOne(equipmentId);
        }
        /*创建器具*/
        if (standardEquipment==null){
            standardEquipment=new MetroCenStandardEquipment();
            standardEquipment.setCreatedDate(commonFunction.getCurrentDateTime());
            standardEquipment.setCreatedBy(currentUser);
        }
        standardEquipment.setAccuracyEN(standardEquipmentForm.getAccuracyEN());
        standardEquipment.setAccuracyCN(standardEquipmentForm.getAccuracyCN());
        standardEquipment.setAccuracy(standardEquipmentForm.getAccuracy());
        standardEquipment.setInstrumentNo(standardEquipmentForm.getInstrumentNo());
        standardEquipment.setInstrumentAccuracyEN(standardEquipmentForm.getInstrumentAccuracyEN());
        standardEquipment.setInstrumentAccuracyCN(standardEquipmentForm.getInstrumentAccuracyCN());
        standardEquipment.setDueDate(standardEquipmentForm.getDueDate());
        standardEquipment.setEquipCertificateNo(standardEquipmentForm.getEquipCertificateNo());
        standardEquipment.setEquipmentName(standardEquipmentForm.getEquipmentName());
        standardEquipment.setMeasureRange(standardEquipmentForm.getMeasureRange());
        standardEquipment.setModelId(standardEquipmentForm.getModelId());
        standardEquipment.setParentModelId(standardEquipmentForm.getParentModelId());
        standardEquipment.setType(standardEquipmentForm.getType());

        standardEquipment.setLastModifiedBy(currentUser);
        standardEquipment.setLastModifiedDate(commonFunction.getCurrentDateTime());
        MetroCenStandardEquipment updateStandardEquipment=standardEquipmentRepository.save(standardEquipment);

        //根据modelId批量处理所有的标准器具的标题中文及英文
        List<MetroCenStandardEquipment>standardEquipments=standardEquipmentRepository.getEquipMentByModelId(standardEquipmentForm.getModelId());
        for (MetroCenStandardEquipment equipment:standardEquipments){
            equipment.setAccuracyCN(standardEquipmentForm.getAccuracyCN());//中文
            equipment.setAccuracyEN(standardEquipmentForm.getAccuracyEN());//英文
            equipment.setInstrumentAccuracyCN(standardEquipmentForm.getInstrumentAccuracyCN());//中文
            equipment.setInstrumentAccuracyEN(standardEquipmentForm.getInstrumentAccuracyEN());//英文
            standardEquipmentRepository.save(equipment);
        }
        setStandardEquipmentHistory(updateStandardEquipment.id);
        return true;
    }

    //删除标准器具
    @RequestMapping(value = "/deleteStandardEquipment/{equipId}")
    public List<MetroCenStandardEquipment> deleteStandardEquipment(@PathVariable long equipId){
        MetroCenStandardEquipment pp=standardEquipmentRepository.findOne(equipId);
        standardEquipmentRepository.delete(pp);
        List<MetroCenStandardEquipment> List=new ArrayList<MetroCenStandardEquipment>();
        List.add(pp);
        return List;
    }


    //read获取所有证书模板
    @RequestMapping(value = "/resultModel",method = RequestMethod.GET)
    public List<MetroCenReslutModel> getResultModel(){
        List< MetroCenReslutModel> resultModelList=resultModelRepository.findAll();
        return  resultModelList;
    }

    //创建证书模板
    @RequestMapping(value = "/createResultModel",method = RequestMethod.POST)
    public boolean createResultModel(MetroCenConfigFileForm configFileForm,@AuthenticationPrincipal User currentUser){
        MetroCenReslutModel resultModel=null;
        long resultId=configFileForm.getId();
        if(resultId>0){
            resultModel=resultModelRepository.findOne(resultId);
        }
        if(resultModel==null)
        {
            resultModel = new MetroCenReslutModel();
            resultModel.setCreatedDate(commonFunction.getCurrentDateTime());//当前时间
            resultModel.setCreatedBy(currentUser);//创建者
        }
        resultModel.setModelContent(configFileForm.getModelContent());
        resultModel.setModelId(configFileForm.getModelId());
        resultModel.setNextPage(configFileForm.getNextPage());
        resultModel.setPageHeight(configFileForm.getPageHeight());

        resultModel.setParentModelId(configFileForm.getParentModelId());
        resultModel.setLastModifiedDate(commonFunction.getCurrentDateTime());//修改日期
        resultModel.setLastModifiedBy(currentUser);//修改者
        resultModelRepository.save(resultModel);
        setResultModelHistory(resultModel.id);
        return true;
    }


    //删除结果说明
    @RequestMapping(value = "/deleteResultModel")
    public List< MetroCenReslutModel> deleteResultModel(MetroCenConfigFileForm configFileForm){
        MetroCenReslutModel resultModel=resultModelRepository.findOne(configFileForm.getId());
        resultModelRepository.delete(resultModel);
        List< MetroCenReslutModel> resultModelList=new ArrayList<MetroCenReslutModel>();
        resultModelList.add(resultModel);
        return resultModelList;

    }

    //获取当前用户权限
    @RequestMapping(value = "/resultModelAuthority/{resultModelId}",method =RequestMethod.GET)
    public boolean resultModelAuthority(@AuthenticationPrincipal User currentUser,@PathVariable long resultModelId,MetroCenReslutModelForm resultModelForm){
        String labs=currentUser.getLab();  //获取当前用户负责的实验室
        if(resultModelId==0){
            long newResultModeId=resultModelForm.getModelId();//如果是添加数据，id为0，则获取添加数的模板id
            MetroCenCertificateModel newModel=certificateModelRepository.findOne(newResultModeId);//根据模板id获取证书模板
            String newModelLab=newModel.getLab();//获取证书模板所在实验室
            if(labs.contains(newModelLab)){
                return  true;
            }else {
                return false;
            }
        } else{
            MetroCenReslutModel resultModel=resultModelRepository.findOne(resultModelId);
            long modelId=resultModel.getModelId();
            MetroCenCertificateModel certificateModel=certificateModelRepository.findOne(modelId);//获取到证书模板信息
            String modelLab=certificateModel.getLab();//模板所在的实验室

            if(labs.contains(modelLab)){
                return true;
            }else {
                return false;
            }

        }

    }


    //read sampleInfo
    @RequestMapping(value = "/sampleInfo",method = RequestMethod.GET)
    public List<MetroCenSampleInfo> getSampleInfo(){
        List<MetroCenSampleInfo> typelist=sampleInfoRepository.findAll();
        return typelist;
    }
    //create sampleInfo
    @RequestMapping(value = "/createSampleInfo",method = RequestMethod.GET)
    public List<MetroCenSampleInfo> createSampleMethod(@RequestParam String sampleName,@RequestParam String accuracyLevel,@RequestParam String measureRange,
                                                       @RequestParam String specificateModel,@RequestParam String factoryName,@RequestParam String factoryCode){
        MetroCenSampleInfo pp=new MetroCenSampleInfo();
        pp.setSampleName(sampleName);
        pp.setAccuracyLevel(accuracyLevel);
        pp.setMeasureRange(measureRange);
        pp.setSpecificateModel(specificateModel);
        pp.setFactoryName(factoryName);
        pp.setFactoryCode(factoryCode);

        sampleInfoRepository.save(pp);
        List<MetroCenSampleInfo> list=new ArrayList<MetroCenSampleInfo>();
        list.add(pp);
        return list;
    }
    //update SampleInfo
    @RequestMapping(value = "updateSampleInfo")
    public List<MetroCenSampleInfo> updateSampleInfo(@RequestParam long id,@RequestParam String sampleName,@RequestParam String accuracyLevel,@RequestParam String measureRange,
                                                     @RequestParam String specificateModel,@RequestParam String factoryName,@RequestParam String factoryCode){
        MetroCenSampleInfo pp=sampleInfoRepository.findOne(id);
        pp.setSampleName(sampleName);
        pp.setAccuracyLevel(accuracyLevel);
        pp.setMeasureRange(measureRange);
        pp.setSpecificateModel(specificateModel);
        pp.setFactoryName(factoryName);
        pp.setFactoryCode(factoryCode);
        MetroCenSampleInfo updateSampleInfo=sampleInfoRepository.save(pp);
        List<MetroCenSampleInfo> sampleInfoList=new ArrayList<MetroCenSampleInfo>();
        sampleInfoList.add(updateSampleInfo);
        return sampleInfoList;
    }
    //delete SampleInfo
    @RequestMapping(value = "/deleteSampleInfo")
    public List<MetroCenSampleInfo> deleteSampleInfo(@RequestParam long id){
        MetroCenSampleInfo pp=sampleInfoRepository.findOne(id);
        sampleInfoRepository.delete(pp);
        List<MetroCenSampleInfo> sampleInfoList=new ArrayList<MetroCenSampleInfo>();
        sampleInfoList.add(pp);
        return sampleInfoList;

    }


//    对标准器具历史表进行入库操作
    public  void setStandardEquipmentHistory(long equipId){

        MetroCenStandardEquipment equipment= standardEquipmentRepository.findOne(equipId);//找到更新的或者添加的那个器具
        MetroCenStandardEquipmentHistory equipmentHistory= new MetroCenStandardEquipmentHistory();//历史表新建一个器具
        //历史表每个器具的字段根据 更新或添加的那个器具 字段内容 填写
        equipmentHistory.setAccuracy(equipment.getAccuracy());
        equipmentHistory.setAccuracyCN(equipment.getAccuracyCN());
        equipmentHistory.setAccuracyEN(equipment.getAccuracyEN());
        equipmentHistory.setInstrumentAccuracyEN(equipment.getInstrumentAccuracyEN());
        equipmentHistory.setInstrumentAccuracyCN(equipment.getInstrumentAccuracyCN());
        equipmentHistory.setDueDate(equipment.getDueDate());
        equipmentHistory.setEquipCertificateNo(equipment.getEquipCertificateNo());
        equipmentHistory.setEquipmentName(equipment.getEquipmentName());
        equipmentHistory.setInstrumentNo(equipment.getInstrumentNo());
        equipmentHistory.setMeasureRange(equipment.getMeasureRange());
        equipmentHistory.setModelId(equipment.getModelId());
        equipmentHistory.setType(equipment.getType());
        equipmentHistory.setInstrumentNo(equipment.getInstrumentNo());
        equipmentHistory.setParentModelId(equipment.getParentModelId());
        equipmentHistory.setRemark1(equipment.getRemark1());
        equipmentHistory.setRemark2(equipment.getRemark2());
        equipmentHistory.setRemark3(equipment.getRemark3());

        standardEquipmentHistoryRepository.save(equipmentHistory);



    }



    public void  setResultModelHistory(long resultModelId){
        MetroCenReslutModel resultModel1=resultModelRepository.findOne(resultModelId);

        MetroCenReslutModelHistory resultModelHistory=new MetroCenReslutModelHistory();

        resultModelHistory.setParentModelId(resultModel1.getParentModelId());
        resultModelHistory.setModelId(resultModel1.getModelId());
        resultModelHistory.setModelContent(resultModel1.getModelContent());
        resultModelHistory.setNextPage(resultModel1.getNextPage());
        resultModelHistory.setPageHeight(resultModel1.getPageHeight());
        resultModelHistoryRepository.save(resultModelHistory);

    }


}
