package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.*;
import com.google.common.collect.ImmutableMap;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by sqhe on 14-8-15.
 * The helper Class to access Activiti
 */
@Component
public class ActivitiService {
    @Value("${spring.main.missiveUrl}")
    String missiveUrl;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private SimpleMailSender mailSender;
    @Autowired
    private SendMsgService msgS;
    @Autowired
    private  FullSearchService fullsearchR;
    @Autowired
    private FaxCableRepository faxR;
    @Autowired
    private MissivePublishRepository misR;
    @Autowired
    private MissiveSignRespository misSR;
    @Autowired
    private MissiveRecSeeCardRepository misRecR;
    @Autowired
    private UserRepository usrR;



    /**
    * start a activiti process by ProcessDefId and BusinessId
    */
    public String startProcess(String porcessDefId,Long businessId,User user)
    {


        identityService.setAuthenticatedUserId(user.getUserName());
        ImmutableMap<String,Object> var = ImmutableMap.of(getProcessFirstAssignee(porcessDefId),(Object)user.getUsername());
        ProcessInstance process  = this.runtimeService.startProcessInstanceById(porcessDefId,businessId.toString(),var);
        if (process!=null)
            return process.getId();
        else
            return null;

    }

    public void delProcessInstance(String ProcessInstanceID,String deleteReason)
    {

        this.runtimeService.deleteProcessInstance(ProcessInstanceID,deleteReason);
        this.historyService.deleteHistoricProcessInstance(ProcessInstanceID);
    }

    public String getPreviousTaskIDByCurrentTaskID(String taskID)
    {
        Task temptask = this.taskService.createTaskQuery().taskId(taskID).singleResult();
        String processid = temptask.getProcessInstanceId();
        List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(processid).orderByHistoricTaskInstanceEndTime().desc().list();
        return previousTasklist.get(0).getId();
    }




    public String get(String porcessDefId,Long businessId,User user)
    {

        identityService.setAuthenticatedUserId(user.getUserName());
        ImmutableMap<String,Object> var = ImmutableMap.of(getProcessFirstAssignee(porcessDefId),(Object)user.getUsername());
        ProcessInstance process  = this.runtimeService.startProcessInstanceById(porcessDefId,businessId.toString(),var);
        if (process!=null)
            return process.getId();
        else
            return null;

    }


    public String getProcessFirstAssignee(String porcessDefId){

        BpmnModel model= this.repositoryService.getBpmnModel(porcessDefId);
        List<FlowElement> flowElementList = (List<FlowElement>)model.getMainProcess().getFlowElements();
        if (flowElementList !=null && flowElementList.size()>=2 && flowElementList.get(1).getClass()==UserTask.class)
        {
            String assigneeName =  ((UserTask)flowElementList.get(1)).getAssignee();
            return assigneeName.substring(2,assigneeName.length()-1);

        }else
            return null;

        // return flowElementList.size();
    }
    //获取最新流程定义
    public List<ProcessDefinition> getAllProcessD()
    {
        List<ProcessDefinition> result = this.repositoryService.createProcessDefinitionQuery().latestVersion().list();

        return result;
    }


    public List<Map<String,? extends Object>> getNextTaskInfo(String processIstanceId,int taskId){
        ProcessInstance process = this.runtimeService.createProcessInstanceQuery().processInstanceId(processIstanceId).singleResult();
        if(process!=null){
            BpmnModel model= this.repositoryService.getBpmnModel(process.getProcessDefinitionId());
            Task task = this.getCurrentTask(taskId);
            if(task!=null){
                FlowElement flowTask = model.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

                if (flowTask !=null &&  flowTask.getClass()==UserTask.class)
                {
                    UserTask ut = ((UserTask)flowTask);
                    String nextElementName = ut.getOutgoingFlows().get(0).getTargetRef();
                    FlowElement nextElement = model.getMainProcess().getFlowElement(nextElementName);
                    if (nextElement !=null &&  nextElement.getClass()== ExclusiveGateway.class)
                    {
                        ExclusiveGateway gateway = (ExclusiveGateway)nextElement;
                        List<SequenceFlow> flowList = gateway.getOutgoingFlows();
                        List<Map<String,? extends Object>> resultList = new ArrayList<Map<String,? extends Object>>();
                        for(SequenceFlow flow :flowList)
                        {
                            if (model.getMainProcess().getFlowElement(flow.getTargetRef()).getClass()!=EndEvent.class)
                            {
                                ImmutableMap<String, ? extends Object> temp = ImmutableMap.of("name", flow.getName(),
                                        "condition", flow.getConditionExpression().split("\"")[1],
                                        "assignee", ((UserTask) model.getMainProcess().getFlowElement(flow.getTargetRef())).getAssignee().replace("${","").replace("}",""),
                                        "multi", haveMultiInstanceTask((UserTask) model.getMainProcess().getFlowElement(flow.getTargetRef())),
                                        "multiAssignee", getMultiInstanceAssignee((UserTask) model.getMainProcess().getFlowElement(flow.getTargetRef()))
                                );
                                resultList.add(temp);
                            }
                            else
                            {
                                ImmutableMap<String, ? extends Object> temp = ImmutableMap.of("name", flow.getName(),
                                        "condition", flow.getConditionExpression().split("\"")[1]
                                );
                                resultList.add(temp);
                            }




                        }

                        return resultList;
                    }else if(nextElement !=null &&  nextElement.getClass()== UserTask.class){

                        UserTask nextTask =(UserTask)nextElement;
                        List<Map<String,? extends Object>> resultList = new ArrayList<Map<String,? extends Object>>();
                        ImmutableMap<String, ? extends Object> temp = ImmutableMap.of("name", nextTask.getName(),
                                "condition", "",
                                "assignee", nextTask.getAssignee().replace("${","").replace("}",""),
                                "multi", false,
                                "multiAssignee", "");
                        resultList.add(temp);
                        return resultList;
                    }
                    return null;
                }else
                    return null;

            }else
                return null;

        }else
            return null;

        // return flowElementList.size();
    }

    //任务委托 taskUser:任务所属用户，delegateUser被委托用户，vars任务完成变量
    public void DelegateTask(String taskId,String taskUser,String delegateUser,Map<String,Object> vars){
        taskService.claim(taskId,taskUser);//任务签收
        taskService.delegateTask(taskId,delegateUser);//任务委托
        taskService.resolveTask(taskId);//完成委派任务

    }

    public Task getCurrentTask(int taskId)
    {

        Task task = this.taskService.createTaskQuery().taskId(String.valueOf(taskId)).singleResult();
        return task;
    }

    public boolean haveMultiInstanceTask(UserTask task)
    {
        MultiInstanceLoopCharacteristics loop = task.getLoopCharacteristics();
        if (loop==null)
            return false;
            else
            return true;
    }

    public String getMultiInstanceAssignee(UserTask task)
    {

        MultiInstanceLoopCharacteristics loop = task.getLoopCharacteristics();
        if (loop==null)
            return "";
        else
            return loop.getInputDataItem();
    }

    //根据用户名获取该用户的所有任务
    public List<Task> getCurrentTasksByUser(User user)
    {
        List<Task> tasklist = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).list();
        return tasklist;
    }
    //根据用户名以及流程定义名称，获取该用户的相关类型的流程任务任务
    public List<Task> getCurrentTasksByUser(User user,String processDefinitionName)
    {
        List<Task> tasklist = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).processDefinitionNameLike(processDefinitionName).list();
        return tasklist;
    }
    //分页获取指定用户当前任务,获取该用户的相关类型的流程任务任务
    public PageableTaskList getCurrentTasksByUser(User user,String processDefinitionName,int pageSize,int pageNum)
    {

        List<Task> tasklist = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).processDefinitionNameLike(processDefinitionName).orderByTaskCreateTime().desc()
                .listPage((pageNum - 1) * pageSize, pageNum * pageSize) ;
        long taskTotal = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).processDefinitionNameLike(processDefinitionName).count();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableTaskList result = new PageableTaskList((int)taskTotal,tasklist,pageNum,pageSize,(int)pageTotal);
        return result;
    }
    //分页获取指定用户当前任务
    public PageableTaskList getCurrentTasksByUser(User user,int pageSize,int pageNum)
    {

        List<Task> tasklist = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).orderByTaskCreateTime().desc()
                .listPage((pageNum - 1) * pageSize, pageNum * pageSize) ;
        long taskTotal = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).count();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableTaskList result = new PageableTaskList((int)taskTotal,tasklist,pageNum,pageSize,(int)pageTotal);
        return result;
    }

    //对当前的所有任务进行催办督办处理
    public void dealCurrentAllTasksList() throws IOException {
        List<Task> tasklist = this.taskService.createTaskQuery().active().list();
        for (Task tsk:tasklist)
        {
            dealSupervision(tsk);//对当前的任务进行催办督办处理
        }
    }
    //获取所有的当前任务
    public PageableTaskList getAllCurrentTaskList(int pageSize,int pageNum){
        List<Task>taskList=this.taskService.createTaskQuery().active().orderByProcessInstanceId().desc()
                .listPage((pageNum - 1) * pageSize, pageNum * pageSize);
        long taskTotal = this.taskService.createTaskQuery().active().count();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableTaskList result = new PageableTaskList((int)taskTotal,taskList,pageNum,pageSize,(int)pageTotal);

        return result;
    }


    public Task getCurrentTasksByProcessInstanceId(Long id)
    {
        Task task = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(id)).singleResult();
        return task;
    }

    /**
        获取所有最新的流程定义
     */
    public List<ProcessDefinition> getAllLastestProcessDefinition()
    {
        List<ProcessDefinition> result = this.repositoryService.createProcessDefinitionQuery().latestVersion().list();
        return result;
    }


    /**
     获取所有历史流程定义,不包含exclusiveGateway
     */
    public List<HistoricActivityInstance> getHistoryByProcessId(Long id)
    {
        List<HistoricActivityInstance> result =this.historyService.createHistoricActivityInstanceQuery().processInstanceId(id.toString()).list();
        /*for (HistoricActivityInstance instance : result)
        {
            if (instance.getActivityType().equals("exclusiveGateway"))
                result.remove(instance);
        }*/
        Iterator<HistoricActivityInstance> activityListIterator = result.iterator();
        while(activityListIterator.hasNext()){
            HistoricActivityInstance instance = activityListIterator.next();
            if (instance.getActivityType().equals("exclusiveGateway"))
                activityListIterator.remove();
        }
        return result;
    }



    /**
     获取流程定义中的说明
     */
    public String getDocumentationByProcessId(Long id)
    {
        ProcessInstance instance = this.runtimeService.createProcessInstanceQuery().processInstanceId(id.toString()).singleResult();
        try{
            String processDefId = instance.getProcessDefinitionId();
            BpmnModel model= this.repositoryService.getBpmnModel(processDefId);
            return model.getMainProcess().getDocumentation();
        }catch (Exception ex){
            return "";
        }




    }
    /**
     获取流程定义中task是否是能够在pad端处理的
     */
    public boolean isPadTask(String processDefId,String taskId)
    {

        BpmnModel model= this.repositoryService.getBpmnModel(processDefId);
        FlowElement element = model.getFlowElement(taskId);
        if (element!=null && element.getClass()==UserTask.class)
        {
            if (((UserTask)element).getDocumentation().equals("pdf"))
                return true;
        }
        return false;
    }

    public void completeTask(Long taskID,Map<String,Object> vars)
    {
        this.taskService.complete(taskID.toString(),vars);

    }
    public void completeTask(Long taskID,Map<String,Object> vars,String type,Long processID)
    {
        this.taskService.complete(taskID.toString(),vars);
        //完成每一步的时候执行插入全文检索
        boolean isFishedProcess=isProcessFinished(Long.toString(processID));
       // isFishedProcess=true;//调试全文索引使用，后面一定要去掉
        if (isFishedProcess) {
            fullsearchR.setSearchContent(type, Long.toString(taskID), Long.toString(processID));
        }
    }
    //获取完成的任务
    public List<String> getProcessDoneByUserName(String username){
        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(username).orderByProcessInstanceId().desc().list();
        List<String> result = new ArrayList<String>();
        for(HistoricTaskInstance task:historicTaskInstanceList)
        {
            result.add(task.getProcessDefinitionId()+","+task.getProcessInstanceId()+","+task.getId());

        }

        return result;
    }
    //根据processID获取到所有经手人
    public List<String> getProcessUsersByProcessID(String processID){

        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processID).processFinished().orderByProcessInstanceId().desc().list();
        List<String> result = new ArrayList<String>();
        for(HistoricTaskInstance task:historicTaskInstanceList)
        {
            result.add(task.getAssignee());//获取流程参与者
        }
        return result;
    }
    //根据processID获取该流程是不是结束
    public boolean isProcessFinished(String ProcessId)
    {
        long result= this.historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(ProcessId).finished().count();
        if(result==0)
        {
            return false;
        }else
        {
            return true;
        }
    }
    //发送邮件提醒
    public boolean emailSender(String type,User usr,String missiveTitle,String taskID,String processID){
        if (usr!=null) {
            boolean isSend = usr.getEmailSend();//获取是否需要发送邮件
            String missiveUrlStr="";
            String emailAddress=usr.getEmail()==null?"":usr.getEmail();//判断用户是否存在
            String[] nextUsers={emailAddress};
            missiveUrlStr=getMissiveUrlStr(type,taskID,processID);
            if (isSend) {//如果需要发送邮件，调用发送函数
                mailSender.HTMLMailSend(missiveTitle, missiveUrlStr, nextUsers);//发送邮件
                return true;
            }
        }
        return  false;
    }
    //催办督办发送邮件提醒
    public boolean emailSuperSender(String type,User usr,String missiveTitle,String taskID,String processID){
        if (usr!=null) {
            String missiveUrlStr="";
            String emailAddress=usr.getEmail()==null?"":usr.getEmail();//判断用户是否存在
            String[] nextUsers={emailAddress};
            missiveUrlStr=getMissiveUrlStr(type,taskID,processID);
            mailSender.HTMLMailSend(missiveTitle, missiveUrlStr, nextUsers);//发送邮件
            return true;
        }
        return  false;
    }
    //发送短信提醒
    public boolean msgSender(User usr,String smsTextStr ) throws IOException {
        String smsMobNum="";
        if (usr!=null){
            if (usr.getMsgSend()){
                smsMobNum=usr.getTel();//获取手机号码
                msgS.sendMsg(smsMobNum,smsTextStr);
                return true;
            }
        }
        return false;
    }
    //催办督办发送短信提醒
    public boolean msgSuperSender(String tel,String smsTextStr ) throws IOException {
        if (!tel.equals("") && !smsTextStr.equals("")){
            msgS.sendMsg(tel,smsTextStr);
            return true;
        }
        return false;
    }
    //根据公文类型，返回公文类别字符串
    public String getMissiveUrlStr(String type,String taskID,String processID){
        String missiveUrlStr=missiveUrl;
        if (type.equals("faxCable")){
            missiveUrlStr=missiveUrlStr+"FaxCable/"+taskID+"/"+processID;
        }else if (type.equals("missivePublish")){

            missiveUrlStr=missiveUrlStr+"MissivePublish/"+taskID+"/"+processID;

        }else if (type.equals("missiveSign")){

            missiveUrlStr=missiveUrlStr+"MissiveSign/"+taskID+"/"+processID;

        }else if (type.equals("missiveReceive")){

            missiveUrlStr=missiveUrlStr+"missiveReceive/"+taskID+"/"+processID;
        }

        return missiveUrlStr;
    }
    //根据任务，处理督办相关事宜
    private void dealSupervision(Task task) throws IOException {
        if (!task.equals(null)) {
            String taskType = task.getProcessDefinitionId();//获取任务类别
            Date missiveCreateTime = task.getCreateTime();//获取任务创建时间
            String taskName = task.getName();
            String taskID=task.getId();//任务ID
            String processID = task.getProcessInstanceId();//获取流程processID
            String assine = task.getAssignee();//获取下一步用户
            //根据用户名获取用户信息
            User usr=usrR.getUserInfoByUserName(assine);
            //设定提醒值
            int delayNum=usr.getDelaynum();
            String delayWarm=usr.getDelayWarm();//发送消息形式，m表示短信，e表示邮件,n表示不提醒
            String tel=usr.getTel();//获取用户电话号码
            //获取当前系统时间
            Date currentDate=new Date(System.currentTimeMillis());//获取当前时间
            //根据创建时间与现在时间差
            boolean isPhase=this.isExcee(missiveCreateTime,currentDate,delayNum);//判断是否超过限定值
            if (isPhase) {
                //公文标题及类别提示语
                String missiveTitleAndType = getMissiveTypeByProcessDefID(taskType, processID);
                String[] titleAndType = missiveTitleAndType.split(",");//拆分字符串
                if (titleAndType.length==3) {//严格等于3，超过或是少于3都不正常
                    String missiveTitle = titleAndType[2];//公文标题
                    String missiveTypeName = titleAndType[0];//公文类别中文名称
                    String missiveType = titleAndType[1];//公文类别
                    if (delayWarm.equals("m")){
                        //String sendMsgTxt=missiveTypeName+"："+missiveTitle+"需要"+taskName+"，请您及时处理。";
                        String sendMsgTxt="您有一份公文需要处理，请注意及时处理。";//由于测试，所有短信内容使用统一格式，后面需要使用正式的短信内容
                        this.msgSuperSender(tel,sendMsgTxt);//发送短信
                    }else if (delayWarm.equals("e")){
                        this.emailSuperSender(missiveType,usr,missiveTitle,taskID,processID);//发送邮件
                    }
                }
            }


        }
    }
    //根据公文类型，返回字符串
    private String getMissiveTypeByProcessDefID(String processDefID,String processID){
        String faxCabel="FaxId:1:";//传真电报
        String missivePub="PublishMissiveId:1:";//发文
        String missiveSign="SignId:1";//签报
        String missiveRec="ReceiptId:1";//收文
        String missiveTitle="";//公文标题
        if (!processDefID.equals("") && !processID.equals("")){

            if (processDefID.contains(faxCabel)){
                FaxCablePublish fax=faxR.getFaxCableByProcessID(Long.parseLong(processID));
                missiveTitle=fax.getMissiveTittle();
                return "传真电报,faxCable,"+missiveTitle;
            }else if (processDefID.contains(missivePub)){
                MissivePublish misPub=misR.findByProcessID(Long.parseLong(processID));
                missiveTitle=misPub.getMissiveTittle();
                return "发文,missivePublish,"+missiveTitle;
            }else if (processDefID.contains(missiveRec)){
                MissiveRecSeeCard misCard=misRecR.getMissData(processID);
                missiveTitle=misCard.getTitle();
                return "收文,missiveReceive,"+missiveTitle;
            } else if (processDefID.contains(missiveSign)){
                MissiveSign misSign=misSR.findByProcessID(Long.parseLong(processID));
                return "签报,missiveSign,"+missiveTitle;
            }
        }
        return "";
    }
    //获取当前时间与创建时间进行对照，是否超出设定时间，如果超出则发短信或是邮件提醒
    private boolean isExcee(Date createDate,Date currentDate,int delayNum){
        long l=currentDate.getTime()-createDate.getTime();
        long day=l/(24*60*60*1000);//相差的天数
        long hour=l/(60*60*1000);//相差的小时
        long phase=hour-delayNum;//相差的小时与设定值相减，如果大于0，说明超过设定值，否还没有到达设定值
        if (phase>0){
            return true;
        }else {
            return false;
        }
    }


}
