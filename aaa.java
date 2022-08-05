package com.yiyuan.core.service.impl;

import com.yiyuan.core.ding.entity.FileAttachment;
import com.yiyuan.core.ding.service.FileAttachmentService;
import com.yiyuan.core.dto.FlowActionResultDTO;
import com.yiyuan.core.dto.FlowViewDTO;
import com.yiyuan.core.entity.*;
import com.yiyuan.core.oss.OSSClientEnum;
import com.yiyuan.core.oss.util.OSSClientConstants;
import com.yiyuan.core.repository.FlowViewRepository;
import com.yiyuan.core.service.*;
import com.yiyuan.core.util.*;
import com.yiyuan.core.vo.FlowActionResultVO;
import com.yiyuan.core.vo.FlowViewVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author wang yu
 * @Description:
 * @Date 2018/11/15
 */
@Service
public class FlowViewServiceImpl  implements FlowViewService {

    @Autowired
    private FlowViewRepository flowViewRepository;
    @Autowired
    private FlowActionResultService flowActionResultService;

    @Autowired
    private DicFlowService dicFlowService;

    @Autowired
    private FlowPermissionService flowPermissionService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FileAttachmentService fileAttachmentService;

    @Override
    public List<FlowViewDTO> findByFlowId(String flowId) {
        return flowViewRepository.findByFlowId(flowId);
    }

    @Override
    public List<FlowViewDTO> findByFlowIdAndActionId(String flowId, String actionId) {
        return flowViewRepository.findByFlowIdAndActionId(flowId,actionId);
    }

    @Override
    public List<FlowViewDTO> findByFlowIdAndActionIdAndResultId(String flowId, String actionId, String resultId) {
        return flowViewRepository.findByFlowIdAndActionIdAndResultId(flowId,actionId,resultId);
    }

    @Override
    public List<FlowViewDTO> findByExecutors(String executors) {
        return flowViewRepository.findByExecutors(executors);
    }

    @Override
    public List<FlowViewDTO> findByViewers(String viewers) {
        return flowViewRepository.findByViewers(viewers);
    }

    @Override
    public List<FlowViewDTO> findByExecutorsAndViewers(String executors, String viewers) {
        return flowViewRepository.findByExecutorsAndViewers(executors,viewers);
    }

    @Override
    public List<FlowViewDTO> findByFlowIdAndExecutorsAndViewers(String flowId, String executors, String viewer) {
        return flowViewRepository.findByFlowIdAndExecutorsAndViewers(flowId,executors,viewer);
    }

    /**
     * 构建流程对象并保存
     * @param flowId 流程ID
     * @param actionId 环节ID
     * @param resultId 操作结果ID
     * @param memo 备注
     * @param attachments 附件
     * @param vId 逻辑表主键ID
     * @param vName 逻辑表中文描述
     * @return -1代表流程数据不存在，0代表流程已结束，1代表流程未结束
     */
    @Override
    public FlowView saveFlow(String flowId, String actionId, String resultId, String memo, String attachments, String vId, String vName) throws Exception {
        FlowActionResult flowActionResultNow;
        FlowView flowViewNow = new FlowView();
        FlowView flowViewNext = null;
        //判断是否为第一步流程
        if(StringUtils.isBlank(resultId) && StringUtils.isBlank(actionId)){
            flowActionResultNow = flowActionResultService.isStartFlowByFlowId(flowId);
            //处理权限
            List<FlowPermission> flowPermissionList = flowPermissionService.findByFlowIdAndActionIdAndVId(flowActionResultNow.getFlowId(),flowActionResultNow.getActionId(),vId);
            FlowPermission flowPermission = new FlowPermission();
            if(flowPermissionList !=null && flowPermissionList.size() > 0){
                flowPermission = flowPermissionList.get(0);
            }
            actionId = flowActionResultNow.getActionId();
            resultId = flowActionResultNow.getResultId();
            String statusId = flowActionResultNow.getStatusId();
            flowViewNow.setActionId(actionId);
            flowViewNow.setFlowId(flowId);
            flowViewNow.setCreator(UserRequest.getCurrentUser().getId());
            flowViewNow.setMemo(memo);
            flowViewNow.setResultId(resultId);
            flowViewNow.setStatusId(statusId);
            flowViewNow.setVId(vId);
            flowViewNow.setVName(vName);
            flowViewNow.setExecutors(flowPermission.getExecutors());
            flowViewNow.setExecuteDept(flowPermission.getExecuteDept());
            flowViewNow.setViewers(flowPermission.getViewers());
            flowViewNow.setAttachment(attachments);
            flowViewNow.setFinishStatus("2");
            flowViewNow.setCreateTime(new Date());
            flowViewNow.setUpdateTime(new Date());
            save(flowViewNow);
        }else{
            flowActionResultNow = flowActionResultService.findByFlowIdAndActionIdAndResultId(flowId,actionId,resultId);

            flowViewNow = findNoFinishByFlowIdAndActionIdAndVId(flowId,actionId,vId);
            //当前操作流程不匹配
            if(flowActionResultNow == null || flowViewNow == null) throw new Exception("当前操作流程不匹配");
            flowViewNow.setFinishStatus("2");
            flowViewNow.setCreator(UserRequest.getCurrentUser().getId());
            flowViewNow.setResultId(flowActionResultNow.getResultId());
            flowViewNow.setMemo(memo);
            flowViewNow.setAttachment(attachments);
            flowViewNow.setStatusId(flowActionResultNow.getStatusId());
            flowViewNow.setUpdateTime(new Date());
            save(flowViewNow);
        }
        //创建下一步操作流程
        if(!flowActionResultNow.getNextActionId().equals("end")){//流程未结束
            List<FlowActionResult> flowActionResultNextList = flowActionResultService.findByFlowIdAndActionId(flowActionResultNow.getFlowId(),flowActionResultNow.getNextActionId());
            FlowActionResult FlowActionResultNext;
            if(flowActionResultNextList != null && flowActionResultNextList.size() > 0){
                FlowActionResultNext = flowActionResultNextList.get(0);
                //处理权限
                List<FlowPermission> flowPermissionList = flowPermissionService.findByFlowIdAndActionIdAndVId(flowId,FlowActionResultNext.getActionId(),vId);
                FlowPermission flowPermission = new FlowPermission();
                if(flowPermissionList !=null && flowPermissionList.size() > 0){
                    flowPermission = flowPermissionList.get(0);
                }
                flowViewNext = new FlowView();
                flowViewNext.setActionId(FlowActionResultNext.getActionId());
                flowViewNext.setFlowId(flowId);
                flowViewNext.setCreator(UserRequest.getCurrentUser().getId());
                flowViewNext.setExecutors(flowPermission.getExecutors());
                flowViewNext.setVId(vId);
                if(StringUtils.isBlank(vName)) {
                    flowViewNext.setVName(flowViewNow.getVName());
                }else{
                    flowViewNext.setVName(vName);
                }
                flowViewNext.setViewers(flowPermission.getViewers());
                flowViewNext.setExecuteDept(flowPermission.getExecuteDept());
                flowViewNext.setFinishStatus("0");
                flowViewNext.setCreateTime(new Date());
                flowViewNext.setUpdateTime(DateUtil.addSecond(new Date(),1));
                save(flowViewNext);
            }
        }
        return flowViewNext == null?new FlowView(flowViewNow.getStatusId()):flowViewNow;
    }

    /**
     * 查找下一步流程记录
     * @param flowId
     * @param actionId
     * @param vId
     * @return
     */
    private FlowView findNoFinishByFlowIdAndActionIdAndVId(String flowId, String actionId, String vId) {
        return flowViewRepository.findNoFinishByFlowIdAndActionIdAndVId(flowId,actionId,vId);
    }

    @Override
    public List<FlowViewDTO> findByVId(String vId) {
        return flowViewRepository.findByVId(vId);
    }

    @Override
    public FlowView findNoFinishByFlowIdAndVId(String flowId, String taskId, String userIdLike) {
        return flowViewRepository.findNoFinishByFlowIdAndVId(flowId,taskId,userIdLike);
    }

    @Override
    public String tarnsFlowId(String flowId) {
        return dicFlowService.getDicFlowMap().get("flow_id").get(flowId);
    }

    @Override
    public String tarnsActionId(String actionId) {
        return dicFlowService.getDicFlowMap().get("action_id").get(actionId);
    }

    @Override
    public String tarnsResultId(String resultId) {
        return dicFlowService.getDicFlowMap().get("result_id").get(resultId);
    }

    @Override
    public String tarnsStatusId(String statusId) {
        return dicFlowService.getDicFlowMap().get("status_id").get(statusId);
    }

    @Override
    public List<FlowView> findFlowListByFlowIdAndVId(String flowId, String taskId) {
        return flowViewRepository.findFlowListByFlowIdAndVId(flowId,taskId);
    }

    @Override
    public FlowView findFlowNewBest(String flowId, Integer id) {
        return flowViewRepository.findFlowNewBest(flowId,id);
    }

    @Override
    public void tarnsNameView(List<FlowViewVO> flowViewVOList) {
        for (FlowViewVO flowViewVO : flowViewVOList) {
            flowViewVO.setFlowName(tarnsFlowId(flowViewVO.getFlowId()));
            flowViewVO.setActionName(tarnsActionId(flowViewVO.getActionId()));
            flowViewVO.setResultName(tarnsResultId(flowViewVO.getResultId()));
            flowViewVO.setStatusName(tarnsStatusId(flowViewVO.getStatusId()));
        }
    }

    @Override
    public void tarnsNameViewResult(List<FlowActionResultVO> flowActionResultVOList) {
        for (FlowActionResultVO flowActionResultVO : flowActionResultVOList) {
            flowActionResultVO.setFlowName(tarnsFlowId(flowActionResultVO.getFlowId()));
            flowActionResultVO.setActionName(tarnsActionId(flowActionResultVO.getActionId()));
            flowActionResultVO.setResultName(tarnsResultId(flowActionResultVO.getResultId()));
            flowActionResultVO.setStatusName(tarnsStatusId(flowActionResultVO.getStatusId()));
        }
    }

    @Override
    public void tarnsMapFlowViewList(List<FlowViewVO> flowViewVOList) {
        Map<String,Employee> employeeMap = employeeService.findAllForMap();
        flowViewVOList.forEach(item->{
            item.getEmployeeMap().put("id",employeeMap.get("id"));
            item.getEmployeeMap().put("fullName",employeeMap.get(String.valueOf(item.getCreator()))!=null?employeeMap.get(String.valueOf(item.getCreator())).getFullName():"");
            item.getEmployeeMap().put("imgUrl",employeeMap.get(String.valueOf(item.getCreator()))!=null?employeeMap.get(String.valueOf(item.getCreator())).getImgUrl():"");
        });
    }

    @Override
    public List<FlowActionResultDTO> getFlowActionResultDTOByFlowIdAndTaskId(String flowId, String taskId, int id) {
        FlowView flowView = findNoFinishByFlowIdAndVId(flowId,taskId,"%"+id+"%");
        if(flowView == null) return null;
        List<FlowActionResult> flowActionResultList = flowActionResultService.findByFlowIdAndActionId(flowView.getFlowId(),flowView.getActionId());
        List<FlowActionResultDTO> flowActionResultDTOList = BeanUtil.batchTransform(FlowActionResultDTO.class,flowActionResultList);
        return flowActionResultDTOList;
    }

    @Override
    public FlowView saveFlowAndAttachment(String flowId, String actionId, String resultId, String id, String[] fileNames, String memo) throws Exception {
        String fileNameStr = "";
        if(fileNames != null && fileNames.length > 0){
            fileNameStr = String.join(",",fileNames);
        }
        //保存附件
        fileAttachmentService.saveFileAttachmentBase(fileNames,getTableNameByFlowId(),id);
        return saveFlow(flowId,actionId,resultId,memo,fileNameStr,String.valueOf(id),null);
    }

    /**
     * 查询待办
     * @param flowId
     * @param id
     * @return
     */
    @Override
    public List<FlowView> todoList(String flowId, Integer id) {
        return flowViewRepository.todoList(flowId,"%"+id+"%");
    }

    /**
     * 查询已办
     * @param flowId
     * @param id
     * @return
     */
    @Override
    public List<FlowView> todoDoneList(String flowId, Integer id) {
        return flowViewRepository.todoDoneList(flowId,"%"+id+"%");
    }

    private String getTableNameByFlowId() {
        return "test";
    }


    @Override
    public FlowView save(FlowView flowView) {
        return flowViewRepository.save(flowView);
    }

    @Override
    public void delete(String id) {
        flowViewRepository.delete(id);
    }

    @Override
    public FlowView update(FlowView flowView) {
        return flowViewRepository.save(flowView);
    }

    @Override
    public List<FlowView> find(FlowView flowView) {
        return null;
    }

    @Override
    public List<FlowView> findAll() {
        return flowViewRepository.findAll();
    }

    @Override
    public FlowView findById(String id) {
        return flowViewRepository.findOne(id);
    }
}
