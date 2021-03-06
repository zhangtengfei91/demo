package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenCertificateForm;
import cn.edu.shou.missive.service.*;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shou on 2015/12/1.
 */
@SessionAttributes(value = {"userbase64","user"})
@Controller
public class html2PdfController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private MetroCenSampleRepository sampleRepository;
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private MetroCenTaskNameRepository taskNameRepository;
    @Autowired
    private MetroCenCertificateRepository certificateRepository;
    @Autowired
    private MetroCenLocationEnvironmentRepository locationEnvironmentRepository;
    @Autowired
    private MetroCenReslutIllustrateRepository reslutIllustrateRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MetroCenCertificateModelRepository modelRepository;
    @Autowired
    private MetroCenCertificateModelRepository certificateModelRepository;
    @Autowired
    private MetroCenCommentRepository commentRepository;

    //打印证书
    @RequestMapping(value = "/html2pdf/certificate/{processType}/{processId}/{userId}")
    public String printCertificate(Model model,@PathVariable long processType,@PathVariable String processId,@PathVariable long userId){
        User currentUser=userRepository.findOne(userId);
        model.addAttribute("user", currentUser);
        model.addAttribute("currentUserId",currentUser.getId());//当前登录用户ID
        List<MetroCenMissiveField> missiveField=null;//存放字段对象
        List<String> inputFieldList = new ArrayList<String>();
        Task task=null;//当前任务信息
        long taskId=5;//获取任务名称的ID
        Map<String,String> mp=new HashMap<String, String>();//用来存放样品相关信息
        List<Map<String,? extends Object>> activitiNextStepInfo =new ArrayList<Map<String, ? extends Object>>();//下一步流程信息
        MetroCenCertificateForm certificateForm=null;

        //如果processId=none，表示样品接收,加载客户信息和样品信息
        if (processId.equals("none")){
            missiveField=sampleRepository.getFieldInfo(processType,taskId);
            mp.put("currentStatus","none");//添加当前状态信息，none表示接收样品
            mp.put("clientId","0");//客户ID
            //核验员
            String checked=userRepository.findOne(currentUser.getId()).getName();//当前登录用户为检定用户
            model.addAttribute("checked",checked);

        }else {
            //获取当前任务信息
            task = activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(processId));
            if (task!=null){//如果流程已经完成，补办证书和查看已完成的流程
                taskId=taskNameRepository.findByTaskName(task.getName()).getId();//获取需要显示任务的ID
                //获取下一步流程信息
                activitiNextStepInfo =activitiService.getNextTaskInfo(String.valueOf(processId), Integer.parseInt(task.getId()));
                mp.put("currentStatus",task.getName());//添加当前状态信息
                //如果处理任务用户和流程的下一步用户不一致，就不能流转流程，只能进行查看
                if (!currentUser.getUserName().equals(task.getAssignee())){
                    model.addAttribute("check",true);//只能查看证书
                }else {
                    model.addAttribute("check",false);//可以处理证书
                }
            }else {
                model.addAttribute("check",true);//只能查看证书
                taskId=6;
            }
            missiveField=sampleRepository.getFieldInfo(processType,taskId);
            //获取证书信息，返回前台
            certificateForm=getCertificateInfo(processId);
            //批准人
            String approved=userRepository.findOne(certificateForm.getApproved()).getName();
            model.addAttribute("approved",approved);
            //核验员
            String checked=userRepository.findOne(certificateForm.getChecked()).getName();
            model.addAttribute("checked",checked);
            //检定员
            String verified=userRepository.findOne(certificateForm.getVerified()).getName();
            model.addAttribute("verified",verified);
        }
        for (MetroCenMissiveField mf:missiveField)
        {
            inputFieldList.add(mf.getInputId());
        }


        model.addAttribute("currentEditableField",inputFieldList);//将需要加载的字段加载到模板
        model.addAttribute("certificateInfo",mp);//证书信息返回前台
        model.addAttribute("activitiNextStepInfo",activitiNextStepInfo);//下一步流程信息返回前台
        model.addAttribute("certificate",certificateForm);
        return "certificatePrint";
    }
    //获取证书前端需要的所有数据
    public MetroCenCertificateForm getCertificateInfo(String processId){
        MetroCenCertificate certificate=certificateRepository.getCertificateInfoByProcessId(processId);//获取证书信息
        MetroCenCertificateForm certificateForm=new MetroCenCertificateForm();
        MetroCenCertificateBackGround backGround;
        MetroCenCertificateModel certificateModel;
        if (!certificate.equals(null)){
            MetroCenLocationEnvironment locationEnvironment=locationEnvironmentRepository.getLocationInfoByCertificateId(certificate.getId());
            MetroCenReslutIllustrate reslutIllustrate=reslutIllustrateRepository.getResultIllustrateByCertificateId(certificate.getId());
            //查找领导意见
            List<MetroCenComments> commentsList=commentRepository.getCommentsByProcessId(processId);
            certificateForm=bindCertificateForm(certificate,certificateForm,locationEnvironment,reslutIllustrate,commentsList);
//            //首先根据小类的Id查找大类的Id
            certificateModel=certificateModelRepository.findOne(certificate.getModelId());
            certificateForm.setModelParentId(certificateModel.getParentId());//设置父节点
            certificateForm.setNextPage(certificateModel.getNextPage());//
        }
        return certificateForm;
    }
    //绑定certificateForm
    public MetroCenCertificateForm bindCertificateForm(MetroCenCertificate certificate,MetroCenCertificateForm certificateForm,MetroCenLocationEnvironment locationEnvironment,
                                                       MetroCenReslutIllustrate reslutIllustrate,List<MetroCenComments>comments){
        String commentResult="";//领导名称和意见，格式为：田为民：我觉得。。。。。
        MetroCenCertificateModel model=modelRepository.findOne(certificate.getModelId());
        certificateForm.setId(certificate.getId());
        certificateForm.setProcessId(certificate.getProcessId());
        certificateForm.setInspectionUnit(certificate.getInspectionUnit());
        certificateForm.setSampleName(certificate.getSampleName());
        certificateForm.setSampleModel(certificate.getSampleModel());
        certificateForm.setSerialNumber(certificate.getSerialNumber());
        certificateForm.setManufactUnit(certificate.getManufactUnit());
        certificateForm.setCertificateNo(certificate.getCertificateNo());
        certificateForm.setVerificatReg(certificate.getVerificatReg());
        certificateForm.setConclusion(certificate.getConclusion());
        certificateForm.setApproved(certificate.getApproved());
        certificateForm.setChecked(certificate.getChecked());
        certificateForm.setVerified(certificate.getVerified());
        certificateForm.setCertificateType(certificate.getCertificateType());
        certificateForm.setValidityDate(certificate.getValidityDate());
        certificateForm.setVerifiedDate(certificate.getVerifiedDate());
        certificateForm.setSampleId(certificate.getSampleId());
        certificateForm.setDistributionId(certificate.getDistributionId());
        certificateForm.setCertificateCheckId(certificate.getChecked());
        certificateForm.setAuthorize(certificate.getApproved());
        certificateForm.setModelId(certificate.getModelId());//模板小类
        if(!model.equals(null)){
            certificateForm.setModelParentId(model.getParentId());//模板大类
        }

        certificateForm.setClient(certificate.getClient().getId());
        certificateForm.setLocationEnvId(locationEnvironment.getId());
        certificateForm.setResultId(reslutIllustrate.getId());

        certificateForm.setLocation(locationEnvironment.getLocation());
        certificateForm.setTemperature(locationEnvironment.getTemperature());
        certificateForm.setRelativeHum(locationEnvironment.getRelativeHum());
        certificateForm.setOther(locationEnvironment.getOther());

        certificateForm.setResults(reslutIllustrate.getResults());
        certificateForm.setUncertainty(certificate.getUncertainty());

        //证书是否补办
        certificateForm.setRetroactive(certificate.getRetroactive());

        //绑定领导意见
        if (comments!=null){
            for (MetroCenComments comment:comments){
                String userName=userRepository.findOne(comment.getUserId()).getName();//获取到意见用户名
                commentResult+=userName+"："+comment.getContext()+"\r\n";
            }
            certificateForm.setComments(commentResult);
        }else {
            certificateForm.setComments("");
        }

        return certificateForm;
    }
}
