package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import cn.edu.shou.missive.domain.missiveDataForm.UserFrom;
import cn.edu.shou.missive.service.*;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by seky on 14/12/9.
 */
@Controller
public class SuperVisorContoller {

    @Autowired
    private ActivitiService actService;
    @Autowired
    private ActivitiService actS;
    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissivePublishRepository mpr;
    @Autowired
    private MissiveSignRespository msr;
    @Autowired
    private FaxCableRepository fcr;
    @Autowired
    private FileUploadController fileupc;
    @Autowired
    private UserRepository usrR;

    @RequestMapping(value="/cbdb/{pageNum}")
    public String html2dbzx(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pageNum){

        model.addAttribute("user", currentUser);

        if(pageNum==null){
            pageNum=1;
        }
        PageableTaskList result = this.actService.getAllCurrentTaskList(10, pageNum);//待办的任务
        List<TaskForm> ltfIng=new ArrayList<TaskForm>();
        if (result.getTasklist() != null) {
            ltfIng = getTaskFormByTask(result.getTasklist());
        }
        int taskIngPagesNum=result.getPageTotal();
        model.addAttribute("tasksIng", ltfIng);
        model.addAttribute("taskIngTotalNum",taskIngPagesNum);
        model.addAttribute("LookPage",pageNum);

        return "/superVisor";
    }

    public List<TaskForm> getTaskFormByTask(List<Task> tasks){
        List<TaskForm> tfs =new ArrayList<TaskForm>();
        User usrInfo=null;
        if (tasks != null) {
            for (Task task : tasks) {
                TaskForm tf = new TaskForm();
                usrInfo=usrR.getUserInfoByUserName(task.getAssignee());
                String processDeId = task.getProcessDefinitionId();
                String taskName = task.getName();
                String assignee=usrInfo.getName();//获取用户姓名
                Long instanceId = Long.parseLong(task.getProcessInstanceId());
                int delayNum=usrInfo.getDelaynum();//获取延误设定值，单位小时
                String delayWarm=usrInfo.getDelayWarm();//获取延误设定类型
                Long taskId = Long.parseLong(task.getId());
                //获取前面的任务信息
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String taskStartTime = String.valueOf(format.format(task.getCreateTime()));
                String nowTime=String.valueOf(format.format(new Date()));
                String intelTime="";
                try {
                    Date date = format.parse(taskStartTime);
                    Date now = format.parse(nowTime);
                    long l = now.getTime() - date.getTime();
                    //long day = l / (24 * 60 * 60 * 1000);
                    long hour = l / (60 * 60 * 1000);//相差小时
                    //long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                    //long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                    intelTime="" + hour + "小时";
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
                //查询当前任务名称
                tf.setId(taskId);
                tf.setProcessInstanceId(instanceId);
                String instanceType;
                String verType="";
                String verTypeTitle="";
                String missiveTitle="";
                if(processDeId.contains("ReceiptId")){
                    instanceType="missiveReceive";
                    verTypeTitle ="收文流程";
                    MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
                    missiveTitle=mrsc.getTitle();
                }
                else if(processDeId.contains("PublishMissiveId")){
                    instanceType="missivePublish";
                    verTypeTitle="发文流程";
                    MissivePublish mp =mpr.findByProcessID(instanceId);
                    if(mp!=null&&mp.getMissiveTittle()!=null){
                        missiveTitle=mp.getMissiveTittle();
                    }
                }
                else if(processDeId.contains("SignId")){
                    instanceType="missiveSign";
                    verTypeTitle="签报流程";
                    MissiveSign ms = msr.findByProcessID(instanceId);
                    if(ms!=null&&ms.getMissiveTittle()!=null){
                        missiveTitle=ms.getMissiveTittle();
                    }
                }
                else if(processDeId.contains("FaxId")){
                    instanceType="faxCablePublish";
                    verTypeTitle="传真电报";
                    FaxCablePublish fcp = fcr.getMissiveByProcessID(instanceId);
                    if(fcp!=null&&fcp.getMissiveTittle()!=null){
                        missiveTitle=fcp.getMissiveTittle();
                    }
                }
                else{
                    instanceType="";
                }
                tf.setIntelTime(intelTime);
                tf.setName(taskName);
                tf.setTaskAssName(assignee);
                tf.setDelayNum(delayNum+"小时");
                tf.setDelayWarm(getNameByDelayWarm(delayWarm));
                tf.setTaskStartTime(taskStartTime);
                tf.setTypeTitle(verTypeTitle);
                tf.setProcessDefinitionId(processDeId);
                tf.setMissiveType(instanceType);
                tf.setMissiveTitle(missiveTitle);
                tfs.add(tf);
            }
        }
        return  tfs;
    }
    //根据延误类型返回延误类型名称
    private String getNameByDelayWarm(String delayWarm){
        if (delayWarm.equals("m")){
            return "短信提醒";
        }else if (delayWarm.equals("e")){
            return "邮件提醒";
        }else if (delayWarm.equals("n")){
            return "请电话联系";
        }
        return "";
    }
}
