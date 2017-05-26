package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenCertificateForm;
import cn.edu.shou.missive.service.*;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
 * Created by seky on 15/1/9.
 */
@RequestMapping(value="MetroCen")
@SessionAttributes(value = {"userbase64","user"})
@Controller
public class MetroCenCertificateController {


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


    //新建证书
    @RequestMapping(value = "/certificate/{processType}/{processId}")
    public String getCertificate(Model model,@AuthenticationPrincipal User currentUser,@PathVariable long processType,@PathVariable String processId){
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
        return "MetroCenCertificate";
    }



    //加载我的证书任务信息列表
    @RequestMapping(value = "/certificate/myCertificateTask/{pagenum}")
    public String myCertificateTask(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pagenum){
        model.addAttribute("user", currentUser);
        getCurrentTaskByUser(model, currentUser, "SampleCerti", pagenum);//证书任务
        return "myCertificateTaskList";
    }

    //加载补发页面
    @RequestMapping(value = "/certificate/myCertificate/remake/{pageNum}/{condition}")
    public String getMyCertificateRemake(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pageNum,@PathVariable String condition){
        model.addAttribute("user",currentUser);
        if(condition.equals("0")){
            getCurrentCertificateByReceptId(model, currentUser, pageNum);
        }else {
            findSearchCertificate(model,condition,currentUser,pageNum);
        }
        return "myCertificateRemake";
    }

    public List<MetroCenCertificate> findSearchCertificate(Model model,String condition,User user,Integer pageNum){
        String sql="";
        String[] conditions=condition.split(",");//拆分条件组合
        String endConditions="";
        String[] searchs=new String[conditions.length];
        for (int i=0;i<conditions.length;++i){
            String[] fields=conditions[i].split("like");//字段名称和查询值进行拆分fields[0]为字段名，fields[1]为查询值
            if(i==0){
                endConditions="certificate."+fields[0]+" like ? ";
            }else {
                endConditions+=" and certificate."+fields[0]+" like ? ";
            }
            searchs[i]=fields[1];
        }
        long userId=user.getId();
        Object[] obj=null;
        int len=searchs.length;
        List<Map<String,Object>>rows;
        sql ="SELECT * FROM metro_cen_certificate certificate WHERE "+endConditions+" and certificate.sample_id in(SELECT sample.id FROM metro_cen_sample sample where sample.recept_id=?)";
        switch (len){
            case 1:obj=new Object[]{"%"+searchs[0].trim()+"%",userId};
                break;
            case 2:obj=new Object[]{"%"+searchs[0].trim()+"%","%"+searchs[1].trim()+"%",userId};
                break;
            case 3:obj=new Object[]{"%"+searchs[0].trim()+"%","%"+searchs[1].trim()+"%","%"+searchs[2].trim()+"%",userId};
                break;
        }

        rows =jdbcTemplate.queryForList(sql,obj);
        List<MetroCenCertificate> certificates=new ArrayList<MetroCenCertificate>();
        //Page<MetroCenCertificate> cenCertificates=certificateRepository.findSearchCertificate(usr.getId(), new PageRequest((pageNum - 1), pageSize));
        //certificates=cenCertificates.getContent();
        //int taskIngPagesNum=cenCertificates.getTotalPages()==0?1:cenCertificates.getTotalPages();//总页数
        for(Map row: rows){
            MetroCenCertificate certificate=new MetroCenCertificate();
                certificate.setCertificateNo((String)(row.get("certificate_no")));
            certificate.setInspectionUnit((String)(row.get("inspection_unit")));
            certificate.setSampleName((String)(row.get("sample_name")));
            certificate.setProcessId((String)(row.get("process_id")));
            certificate.setSerialNumber((String)(row.get("serial_number")));
            certificate.setRetroactive(Long.parseLong((String) (row.get("retroactive").toString())));
            Task task=null;

            String taskName ="已完成";
            if (certificate.getProcessId()!=null){
                task=activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(certificate.getProcessId()));
            }else {
                taskName="异常";
            }
            if (task!=null){
                taskName=task.getName();//获取任务名称
            }

            certificate.setRemark(taskName.toString());//用备注属性存放任务状态
            certificates.add(certificate);
        }

        model.addAttribute("certificateInfo",certificates);
        model.addAttribute("taskIngTotalNum",1);
        model.addAttribute("LookPage",pageNum);


        return certificates;
    }

    //加载我的证书
    @RequestMapping(value = "/certificate/myCertificate/{pageNum}")
    public String getMyCertificateList(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pageNum){
        model.addAttribute("user",currentUser);
        getVerifiedCertificateByUser(model, currentUser, pageNum);
        return "myCertificateList";
    }
    //补发证书
    @RequestMapping(value = "/certificate/2/none/{certificateId}")
    public String retroactiveCertificate(Model model,@AuthenticationPrincipal User currentUser,@PathVariable long certificateId){
        MetroCenCertificate certificate=certificateRepository.findOne(certificateId);//查找证书
        model.addAttribute("user", currentUser);
        model.addAttribute("currentUserId",currentUser.getId());//当前登录用户ID
        List<MetroCenMissiveField> missiveField=null;//存放字段对象
        List<String> inputFieldList = new ArrayList<String>();
        Task task=null;//当前任务信息
        long taskId=5;//获取任务名称的ID
        Map<String,String> mp=new HashMap<String, String>();//用来存放样品相关信息
        List<Map<String,? extends Object>> activitiNextStepInfo =new ArrayList<Map<String, ? extends Object>>();//下一步流程信息
        MetroCenCertificateForm certificateForm=new MetroCenCertificateForm();
        MetroCenLocationEnvironment environment=locationEnvironmentRepository.findOne(certificate.getLocationId());//获取地点信息
        MetroCenReslutIllustrate reslutIllustrate=reslutIllustrateRepository.findOne(certificate.getResultId());//获取结果信息
        bindCertificateForm(certificate,certificateForm,environment,reslutIllustrate,null);//绑定证书信息
        missiveField=sampleRepository.getFieldInfo(2,taskId);
        mp.put("currentStatus","none");//添加当前状态信息，none表示接收样品
        mp.put("clientId","0");//客户ID

        //检定员
        String verified=userRepository.findOne(certificateForm.getVerified()).getName();
        model.addAttribute("verified",verified);

        for (MetroCenMissiveField mf:missiveField)
        {
            inputFieldList.add(mf.getInputId());
        }
        model.addAttribute("currentEditableField",inputFieldList);//将需要加载的字段加载到模板
        model.addAttribute("certificateInfo",mp);//证书信息返回前台
        model.addAttribute("activitiNextStepInfo",activitiNextStepInfo);//下一步流程信息返回前台
        model.addAttribute("certificate",certificateForm);
        model.addAttribute("retroactive",true);

        return "MetroCenCertificate";
    }

    //查找当前登录用户的已接收（recept）的样品信息(新增的)
    public List<MetroCenCertificate>getCurrentCertificateByReceptId(Model model,User usr,Integer pageNum){
        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
        int pageSize=20;//记录条数
        List<MetroCenCertificate> metroCenCertificateList=new ArrayList<MetroCenCertificate>();//证书列表
        List<MetroCenCertificate>metroCenCertificateListReturn=new ArrayList<MetroCenCertificate>();//返回证书列表
        Page<MetroCenCertificate> cenCertificates=certificateRepository.getCertificateByReceptId(usr.getId(), new PageRequest((pageNum - 1), pageSize));
        metroCenCertificateList=cenCertificates.getContent();
        int taskIngPagesNum=cenCertificates.getTotalPages()==0?1:cenCertificates.getTotalPages();//总页数
        for (MetroCenCertificate certificate:metroCenCertificateList){
            Task task=null;
            String taskName ="已完成";
            if (certificate.getProcessId()!=null){
                task=activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(certificate.getProcessId()));
            }else {
                taskName="异常";
            }
            if (task!=null){
                taskName=task.getName();//获取任务名称
            }
            certificate.setRemark(taskName);//用备注属性存放任务状态
            metroCenCertificateListReturn.add(certificate);
        }
        model.addAttribute("certificateInfo",metroCenCertificateListReturn);
        model.addAttribute("taskIngTotalNum",taskIngPagesNum);
        model.addAttribute("LookPage",pageNum);
        return metroCenCertificateListReturn;
    }

    //查找当前登录用户的出具的证书信息 郑小罗 20151123
    public List<MetroCenCertificate>getVerifiedCertificateByUser(Model model,User usr,Integer pageNum){
        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
        int pageSize=20;//记录条数
        List<MetroCenCertificate> metroCenCertificateList=new ArrayList<MetroCenCertificate>();//证书列表
        List<MetroCenCertificate>metroCenCertificateListReturn=new ArrayList<MetroCenCertificate>();//返回证书列表
        //根据用户Id获取改用户所有出具的证书
        Page<MetroCenCertificate> cenCertificates=certificateRepository.getCertificateByVerifiedId(usr.getId(), new PageRequest((pageNum - 1), pageSize));
        metroCenCertificateList=cenCertificates.getContent();
        int taskIngPagesNum=cenCertificates.getTotalPages()==0?1:cenCertificates.getTotalPages();//总页数
        for (MetroCenCertificate certificate:metroCenCertificateList){
            Task task=null;
            String taskName ="已完成";
            if (certificate.getProcessId()!=null){
                task=activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(certificate.getProcessId()));
            }else {
                taskName="异常";
            }
            if (task!=null){
                taskName=task.getName();//获取任务名称
            }
            certificate.setRemark(taskName);//用备注属性存放任务状态
            //使用某个字段存储当前证书相同编号下最大的retroactive，用来与certificatePath相比较，来判断补办任务是否完成
            String aa=certificateRepository.getMaxRetroactiveByCertificateNo(certificate.getCertificateNo()).toString();
            certificate.setConclusion(aa);//用SerialNumber存储同一个证书编号下最大的retroactive
            metroCenCertificateListReturn.add(certificate);
        }
        model.addAttribute("certificateInfo",metroCenCertificateListReturn);
        model.addAttribute("taskIngTotalNum",taskIngPagesNum);
        model.addAttribute("LookPage",pageNum);
        return metroCenCertificateListReturn;
    }

    //查找当前登录用户的已分发的样品信息（distribution）
    public List<MetroCenCertificate>getCurrentCertificateByUser(Model model,User usr,Integer pageNum){
        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
        int pageSize=20;//记录条数
        List<MetroCenCertificate> metroCenCertificateList=new ArrayList<MetroCenCertificate>();//证书列表
        List<MetroCenCertificate>metroCenCertificateListReturn=new ArrayList<MetroCenCertificate>();//返回证书列表
        Page<MetroCenCertificate> cenCertificates=certificateRepository.getCertificateByDistributionId(usr.getId(), new PageRequest((pageNum - 1),pageSize));
        metroCenCertificateList=cenCertificates.getContent();
        int taskIngPagesNum=cenCertificates.getTotalPages()==0?1:cenCertificates.getTotalPages();//总页数
        for (MetroCenCertificate certificate:metroCenCertificateList){
            Task task=null;
            String taskName ="已完成";
            if (certificate.getProcessId()!=null){
                task=activitiService.getCurrentTasksByProcessInstanceId(Long.parseLong(certificate.getProcessId()));
            }else {
                taskName="异常";
            }
            if (task!=null){
                taskName=task.getName();//获取任务名称
            }
            certificate.setRemark(taskName);//用备注属性存放任务状态
            //使用某个字段存储当前证书相同编号下最大的retroactive，用来与certificatePath相比较，来判断补办任务是否完成
            String aa=certificateRepository.getMaxRetroactiveByCertificateNo(certificate.getCertificateNo()).toString();
            certificate.setConclusion(aa);//用SerialNumber存储同一个证书编号下最大的retroactive
            metroCenCertificateListReturn.add(certificate);
        }
        model.addAttribute("certificateInfo",metroCenCertificateListReturn);
        model.addAttribute("taskIngTotalNum",taskIngPagesNum);
        model.addAttribute("LookPage",pageNum);
        return metroCenCertificateListReturn;
    }
    //查找当前登录用户的所有样品任务
    public List<MetroCenCertificate>getCurrentTaskByUser(Model model,User usr,String processDefinitionName,Integer pageNum){
        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
        PageableTaskList result = this.activitiService.getCurrentTasksByUser(usr,processDefinitionName, 20, pageNum);//待办的任务
        List<MetroCenCertificate> metroCenCertificateList=new ArrayList<MetroCenCertificate>();//证书信息列表
        MetroCenCertificate metroCenCertificate;//证书信息

        //根据任务的processId对证书进行查询
        for(Task task:result.getTasklist()){
            String dealCertificateUrl="/MetroCen/certificate/2/";//处理证书链接信息，使用MetroCenCertificate中的remark来接收链接地址
            String taskName=task.getName();//任务名称
            metroCenCertificate=certificateRepository.getCertificateInfoByProcessId(task.getProcessInstanceId());
            if(metroCenCertificate!=null){
                dealCertificateUrl=dealCertificateUrl+task.getProcessInstanceId();
                metroCenCertificate.setRemark(dealCertificateUrl);//使用MetroCenCertificate中的remark来接收链接地址
                metroCenCertificate.setTaskId(taskName);//使用任务ID存放taskName
                metroCenCertificateList.add(metroCenCertificate);

            }
        }
        int taskIngPagesNum=result.getPageTotal()==0?1:result.getPageTotal();
        model.addAttribute("certificateInfo",metroCenCertificateList);
        model.addAttribute("taskIngTotalNum",taskIngPagesNum);
        model.addAttribute("LookPage",pageNum);
        return metroCenCertificateList;
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
