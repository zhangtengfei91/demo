package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.ActivitiService;
import cn.edu.shou.missive.service.MetroCenCertificateRepository;
import cn.edu.shou.missive.service.MetroCenSampleRepository;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Created by sui on 2015/1/20.
 */
@RequestMapping(value="MetroCen")
@SessionAttributes(value = {"userbase64","user"})
@Controller
public class MetroCenIndexColumnsController {

    @Autowired
    private MetroCenSampleRepository sampleRepository;
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private MetroCenCertificateRepository certificateRepository;



    @RequestMapping(value = "/index/Sample/{pagenum}")
    public String getColumns(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pagenum){
        model.addAttribute("user", currentUser);
        getCurrentTaskByUser(model, currentUser, "SampleDistr", pagenum);//样品任务
        getCurrentCertificateTaskByUser(model, currentUser, "SampleCerti", pagenum);//证书任务

        return "MetroCenIndexColumn";
    }

    //查找当前登录用户的所有样品任务
    public List<MetroCenSample>getCurrentTaskByUser(Model model,User usr,String processDefinitionName,Integer pageNum){
        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
        PageableTaskList result = this.activitiService.getCurrentTasksByUser(usr,processDefinitionName, 10, pageNum);//待办的任务
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
    //查找当前登录用户的所有证书任务
    public List<MetroCenCertificate>getCurrentCertificateTaskByUser(Model model,User usr,String processDefinitionName,Integer pageNum){
        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
        PageableTaskList result = this.activitiService.getCurrentTasksByUser(usr,processDefinitionName, 10, pageNum);//待办的任务
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

}
