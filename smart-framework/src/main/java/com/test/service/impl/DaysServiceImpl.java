package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.constants.SystemConstants;
import com.test.domain.ResponseResult;
import com.test.domain.dto.*;
import com.test.domain.entity.*;
import com.test.domain.vo.*;
import com.test.enums.AppHttpCodeEnum;
import com.test.mapper.*;
import com.test.service.DaysService;
import com.test.utils.BeanCopyUtils;
import com.test.utils.FindDateStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.test.constants.SystemConstants.*;
import static com.test.utils.FindDateStatusUtil.getWeekDate;

/**
 * 日排班表(Days)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 18:44:59
 */
@Service("daysService")
public class DaysServiceImpl extends ServiceImpl<DaysMapper, Days> implements DaysService {

    @Autowired
    AiDataMapper aiDataMapper;
    @Autowired
    RestMapper restMapper;
    @Autowired
    UserRestMapper userRestMapper;
    @Autowired
    DaysMapper daysMapper;
    @Autowired
    InfoUserMapper infoUserMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    RuleMapper ruleMapper;
    @Autowired
    InfoMapper infoMapper;
    @Autowired
    SimpleRuleMapper simpleRuleMapper;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    UserSchedulingMapper userSchedulingMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    NeededMapper neededMapper;
    @Autowired
    InfoNeededMapper infoNeededMapper;


    @Override
    public ResponseResult selectOneSchedule(Long id) {
        List<Days> daysList = baseMapper.selectOneSchedule(id);
        List<DaysListVo> daysListVos = changeList(daysList);
        return ResponseResult.okResult(daysListVos);
    }

    @Override
    public ResponseResult getWeekList(ScheduleDto scheduleDto) {
        List<ScheduleInfo> scheduleInfos = scheduleInfoHelp(scheduleDto);
        Map<String, String> weekDate = dateTimeHelp(scheduleDto);
        List<ScheduleInfo> scheduleInfoList = new ArrayList<>();
        for (ScheduleInfo scheduleInfo : scheduleInfos) {
            List<Days> daysList = baseMapper.selectDaysList(scheduleInfo.getId(), weekDate.get("mondayDate"), weekDate.get("sundayDate"));
            if (daysList.size() != 0) {
                for (int i = 0; i < daysList.size(); i++) {
                    ScheduleInfo scheduleInfo1 = exchangeTime(daysList.get(i));
                    scheduleInfo1.setId(scheduleInfo.getId());
                    scheduleInfo1.setRoleName(scheduleInfo.getRoleName());
                    scheduleInfo1.setName(scheduleInfo.getName());
                    scheduleInfoList.add(scheduleInfo1);
                }
            }
        }
        if (StringUtils.hasText(scheduleDto.getName()))
            return ResponseResult.okResult(scheduleInfoList.stream().filter(scheduleInfo -> scheduleInfo.getName().contains(scheduleDto.getName())).toList());
        else
            return ResponseResult.okResult(scheduleInfoList);
    }


    @Override
    public ResponseResult getDayList(ScheduleDto scheduleDto) {
        List<ScheduleInfo> scheduleInfos = scheduleInfoHelp(scheduleDto);
        List<ScheduleInfo> scheduleInfoList = new ArrayList<>();
        for (ScheduleInfo scheduleInfo : scheduleInfos) {
            List<Days> daysList = baseMapper.selectDays(scheduleInfo.getId(), scheduleDto.getDate());
            if (daysList.size() != 0) {
                for (int i = 0; i < daysList.size(); i++) {
                    ScheduleInfo scheduleInfo1 = exchangeTime(daysList.get(i));
                    scheduleInfo1.setId(scheduleInfo.getId());
                    scheduleInfo1.setRoleName(scheduleInfo.getRoleName());
                    scheduleInfo1.setName(scheduleInfo.getName());
                    scheduleInfoList.add(scheduleInfo1);
                }
            }
        }
        if (StringUtils.hasText(scheduleDto.getName()))
            return ResponseResult.okResult(scheduleInfoList.stream().filter(scheduleInfo -> scheduleInfo.getName().contains(scheduleDto.getName())).toList());
        else
            return ResponseResult.okResult(scheduleInfoList);
    }

    @Override
    public ResponseResult deleteSchedule(Days day) {
            Days days1 = baseMapper.selectById(day.getId());
            if (day.getMorningEnd() != null && day.getMorningEnd() != 0) {
                days1.setMorningStart(null);
                days1.setMorningEnd(null);
            }
            if (day.getMorningSecondEnd() != null && day.getMorningSecondEnd() != 0) {
                days1.setMorningSecondStart(null);
                days1.setMorningSecondEnd(null);
            }
            if (day.getAfternoonEnd() != null && day.getAfternoonEnd() != 0) {
                days1.setAfternoonStart(null);
                days1.setAfternoonEnd(null);
            }
            if (day.getAfternoonSecondEnd() != null && day.getAfternoonSecondEnd() != 0) {
                days1.setAfternoonSecondStart(null);
                days1.setAfternoonSecondEnd(null);
            }
            if (day.getEveningEnd() != null && day.getEveningEnd() != 0) {
                days1.setEveningStart(null);
                days1.setEveningEnd(null);
            }
            if (day.getEveningSecondEnd() != null && day.getEveningSecondEnd() == 0) {
                days1.setEveningSecondStart(null);
                days1.setEveningSecondEnd(null);
            }
            if (days1.getMorningEnd() == null &&
                    days1.getMorningSecondEnd() == null &&
                    days1.getAfternoonEnd() == null &&
                    days1.getAfternoonSecondEnd() == null &&
                    days1.getEveningEnd() == null &&
                    days1.getEveningSecondEnd() == null) {
                baseMapper.deleteById(days1);
                LambdaQueryWrapper<UserScheduling> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserScheduling::getSchedulingId, days1.getId());
                userSchedulingMapper.delete(wrapper);
            } else {
                updateById(days1);
            }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult addSchedule(AddScheduleDto addScheduleDto) {
        List<Days> daysList = baseMapper.selectDays(addScheduleDto.getId(), addScheduleDto.getDate());
        double time;
        String replace;
        if (addScheduleDto.getTime().endsWith(":00")) {
            replace = addScheduleDto.getTime().replace(":00", ".0");
        } else {
            replace = addScheduleDto.getTime().replace(":30", ".5");
        }
        time = Double.parseDouble(replace);
        if (daysList.size() != 0) {
            Days days = daysList.get(0);
            if (time < SystemConstants.MID_TIME) {
                if (days.getMorningEnd() == null) {
                    days.setMorningStart(time);
                    days.setMorningEnd(time + addScheduleDto.getWorkTime());
                    baseMapper.updateById(days);
                } else {
                    days.setMorningSecondStart(time);
                    days.setMorningSecondEnd(time + addScheduleDto.getWorkTime());
                    baseMapper.updateById(days);
                }
            } else if (time < SystemConstants.AFTERNOON_TIME) {
                if (days.getAfternoonEnd() == null) {
                    days.setAfternoonStart(time);
                    days.setAfternoonEnd(time + addScheduleDto.getWorkTime());
                    baseMapper.updateById(days);
                } else {
                    days.setAfternoonSecondStart(time);
                    days.setAfternoonSecondEnd(time + addScheduleDto.getWorkTime());
                    baseMapper.updateById(days);
                }
            } else {
                if (days.getEveningEnd() == null) {
                    days.setEveningStart(time);
                    days.setEveningEnd(time + addScheduleDto.getWorkTime());
                    baseMapper.insert(days);
                } else {
                    days.setEveningSecondStart(time);
                    days.setEveningSecondEnd(time + addScheduleDto.getWorkTime());
                    baseMapper.insert(days);
                }
            }
        } else {
            Days days = new Days();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate;
            try {
                endDate = sdf.parse(addScheduleDto.getDate());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            days.setDate(endDate);
            if (time < SystemConstants.MID_TIME) {
                days.setMorningStart(time);
                days.setMorningEnd(time + addScheduleDto.getWorkTime());
                baseMapper.insert(days);
                userSchedulingMapper.insert(new UserScheduling(addScheduleDto.getId(), days.getId()));
            } else if (time < SystemConstants.AFTERNOON_TIME) {
                days.setAfternoonStart(time);
                days.setAfternoonEnd(time + addScheduleDto.getWorkTime());
                baseMapper.insert(days);
                userSchedulingMapper.insert(new UserScheduling(addScheduleDto.getId(), days.getId()));
            } else {
                days.setEveningStart(time);
                days.setEveningEnd(time + addScheduleDto.getWorkTime());
                baseMapper.insert(days);
                userSchedulingMapper.insert(new UserScheduling(addScheduleDto.getId(), days.getId()));
            }
        }
        List<Needed> neededList = neededMapper.selectNeeded(addScheduleDto.getStoreId(), addScheduleDto.getDate(), time, time + addScheduleDto.getWorkTime());
        for (Needed needed : neededList) {
            needed.setNeedMan(needed.getNeedMan() - 1);
            if (needed.getNeedMan() == 0)
                needed.setDelFlag(1);
            neededMapper.updateById(needed);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult generateDaySchedule(GenerateDayDto generateDayDto) {
        // 如果有了就返回失败
        // 先通过门店id获取员工信息 有了员工信息再查排班信息
        LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoUser::getInfoId, generateDayDto.getStoreId());
        List<InfoUser> infoUsers = infoUserMapper.selectList(wrapper);
        for (InfoUser infoUser : infoUsers) {
            List<UserScheduling> userScheduling =
                    userSchedulingMapper.selectData(infoUser.getUserId(), generateDayDto.getDate(), generateDayDto.getDate());
            if (userScheduling.size() > 0) return ResponseResult.errorResult(AppHttpCodeEnum.HASE_DATA);
        }
        generateDaySchedule(generateDayDto.getDate(), generateDayDto.getStoreId());
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult resetWeekSchedule(GenerateWeekDto generateWeekDto) {
        // 先删除
        LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoUser::getInfoId, generateWeekDto.getStoreId());
        List<InfoUser> infoUsers = infoUserMapper.selectList(wrapper);
        for (InfoUser infoUser : infoUsers) {
            List<UserScheduling> userScheduling = userSchedulingMapper.selectData(infoUser.getUserId(), generateWeekDto.getDates().get(0), generateWeekDto.getDates().get(6));
            for (UserScheduling scheduling : userScheduling) {
                daysMapper.realDelete(scheduling.getSchedulingId());
                LambdaQueryWrapper<UserScheduling> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(UserScheduling::getSchedulingId, scheduling.getSchedulingId());
                userSchedulingMapper.delete(wrapper1);
            }
            List<UserRest> userRestList = userRestMapper.selectDate(infoUser.getUserId(), generateWeekDto.getDates().get(0), generateWeekDto.getDates().get(6));
            for (UserRest userRest : userRestList) {
                userMapper.realDelete(userRest.getRestId());
                LambdaQueryWrapper<UserRest> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(UserRest::getRestId, userRest.getRestId());
                userRestMapper.delete(wrapper1);
            }
        }
        List<Long> neededIds = infoNeededMapper.selectData(generateWeekDto.getStoreId(), generateWeekDto.getDates().get(0), generateWeekDto.getDates().get(6));
        for (Long neededId : neededIds) {
            neededMapper.realDelete(neededId);
            LambdaQueryWrapper<InfoNeeded> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(InfoNeeded::getNeededId, neededId);
            infoNeededMapper.delete(wrapper1);
        }
        // 再生成
        for (String date : generateWeekDto.getDates())
            generateDaySchedule(date, generateWeekDto.getStoreId());
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult selectSelfSchedule(Long id) {
        Date date = new Date(System.currentTimeMillis());
        Map<String, String> weekDate = getWeekDate(date);
        String startDay = weekDate.get(MONDAY_DATE);
        String sunday = weekDate.get(SUNDAY_DATE);
        List<Days> daysList = userSchedulingMapper.selectDays(id, startDay, sunday);
        List<ScheduleVo> scheduleVoList = new ArrayList<>();
        for (Days days : daysList) {
            if (days.getMorningEnd() != null) {
                ScheduleVo scheduleVo = new ScheduleVo();
                scheduleVo.setId(days.getId());
                scheduleVo.setDate(days.getDate());
                scheduleVo.setMorningEnd(days.getMorningEnd());
                scheduleVo.setMorningStart(days.getMorningStart());
                scheduleVoList.add(scheduleVo);
            }
            if (days.getMorningSecondEnd() != null) {
                ScheduleVo scheduleVo = new ScheduleVo();
                scheduleVo.setId(days.getId());
                scheduleVo.setDate(days.getDate());
                scheduleVo.setMorningEnd(days.getMorningSecondEnd());
                scheduleVo.setMorningStart(days.getMorningSecondStart());
                scheduleVoList.add(scheduleVo);
            }
            if (days.getAfternoonEnd() != null) {
                ScheduleVo scheduleVo = new ScheduleVo();
                scheduleVo.setId(days.getId());
                scheduleVo.setDate(days.getDate());
                scheduleVo.setMorningEnd(days.getAfternoonEnd());
                scheduleVo.setMorningStart(days.getAfternoonStart());
                scheduleVoList.add(scheduleVo);
            }
            if (days.getAfternoonSecondStart() != null) {
                ScheduleVo scheduleVo = new ScheduleVo();
                scheduleVo.setId(days.getId());
                scheduleVo.setDate(days.getDate());
                scheduleVo.setMorningEnd(days.getAfternoonSecondStart());
                scheduleVo.setMorningStart(days.getAfternoonSecondEnd());
                scheduleVoList.add(scheduleVo);
            }
            if (days.getEveningStart() != null) {
                ScheduleVo scheduleVo = new ScheduleVo();
                scheduleVo.setId(days.getId());
                scheduleVo.setDate(days.getDate());
                scheduleVo.setMorningEnd(days.getEveningStart());
                scheduleVo.setMorningStart(days.getEveningEnd());
                scheduleVoList.add(scheduleVo);
            }
            if (days.getEveningSecondStart() != null) {
                ScheduleVo scheduleVo = new ScheduleVo();
                scheduleVo.setId(days.getId());
                scheduleVo.setDate(days.getDate());
                scheduleVo.setMorningEnd(days.getEveningSecondStart());
                scheduleVo.setMorningStart(days.getEveningSecondEnd());
                scheduleVoList.add(scheduleVo);
            }
        }
        return ResponseResult.okResult(scheduleVoList);
    }

    @Override
    public ResponseResult addApply(AddApplyDto addApplyDto) {
        String workTime = addApplyDto.getApplyTime();
        String[] split = workTime.split("-");
        List<Rest> restList = userRestMapper.selectInfo(addApplyDto.getId(), addApplyDto.getDate());
        for (Rest rest: restList)
            if (rest.getStartTime().equals(split[0]) && rest.getEndTime().equals(split[1]))
                return ResponseResult.errorResult(400, "已经发起过该排班的请假申请了");
        Rest rest = new Rest();
        rest.setDate(addApplyDto.getDate());
        rest.setReason(addApplyDto.getReason());
        rest.setStartTime(split[0]);
        rest.setEndTime(split[1]);
        restMapper.insert(rest);
        userRestMapper.insert(new UserRest(addApplyDto.getId(), rest.getId()));
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult deleteDaysSchedule(GenerateWeekDto generateWeekDto) {
        LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoUser::getInfoId, generateWeekDto.getStoreId());
        List<InfoUser> infoUsers = infoUserMapper.selectList(wrapper);
        for (InfoUser infoUser : infoUsers) {
            List<UserScheduling> userScheduling = userSchedulingMapper.selectData(infoUser.getUserId(), generateWeekDto.getDates().get(0), generateWeekDto.getDates().get(6));
            for (UserScheduling scheduling : userScheduling) {
                daysMapper.realDelete(scheduling.getSchedulingId());
                LambdaQueryWrapper<UserScheduling> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(UserScheduling::getSchedulingId, scheduling.getSchedulingId());
                userSchedulingMapper.delete(wrapper1);
            }
            List<UserRest> userRestList = userRestMapper.selectDate(infoUser.getUserId(), generateWeekDto.getDates().get(0), generateWeekDto.getDates().get(6));
            for (UserRest userRest : userRestList) {
                userMapper.realDelete(userRest.getRestId());
                LambdaQueryWrapper<UserRest> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(UserRest::getRestId, userRest.getRestId());
                userRestMapper.delete(wrapper1);
            }
        }
        List<Long> neededIds = infoNeededMapper.selectData(generateWeekDto.getStoreId(), generateWeekDto.getDates().get(0), generateWeekDto.getDates().get(6));
        for (Long neededId : neededIds) {
            neededMapper.realDelete(neededId);
            LambdaQueryWrapper<InfoNeeded> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(InfoNeeded::getNeededId, neededId);
            infoNeededMapper.delete(wrapper1);
        }

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getDayWork(DayWorkDto dayWorkDto) {
        List<Days> daysList = userSchedulingMapper.selectDays(dayWorkDto.getId(), dayWorkDto.getDate(), dayWorkDto.getDate());
        Days days = daysList.get(0);
        List<DayWorkVo> dayWorkVoList = new ArrayList<>();
        int i = 1;
        String workTime = null;
        if (days.getMorningEnd() != null) {
            if (days.getMorningStart().toString().endsWith(".0")) {
                workTime = days.getMorningStart().toString().replace(".0", ":00");
                if (days.getMorningEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getMorningEnd().toString().replace(".0", ":00");
                } else if (days.getMorningEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getMorningEnd().toString().replace(".5", ":30");
                }
            } else if (days.getMorningStart().toString().endsWith(".5")) {
                workTime = days.getMorningStart().toString().replace(".5", ":30");
                if (days.getMorningEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getMorningEnd().toString().replace(".0", ":00");
                } else if (days.getMorningEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getMorningEnd().toString().replace(".5", ":30");
                }
            }
            dayWorkVoList.add(new DayWorkVo(i, workTime));
            i++;
        }
        if (days.getMorningSecondEnd() != null) {
            if (days.getMorningSecondStart().toString().endsWith(".0")) {
                workTime = days.getMorningSecondStart().toString().replace(".0", ":00");
                if (days.getMorningSecondEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getMorningSecondEnd().toString().replace(".0", ":00");
                } else if (days.getMorningSecondEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getMorningSecondEnd().toString().replace(".5", ":30");
                }
            } else if (days.getMorningSecondStart().toString().endsWith(".5")) {
                workTime = days.getMorningSecondStart().toString().replace(".5", ":30");
                if (days.getMorningSecondEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getMorningSecondEnd().toString().replace(".0", ":00");
                } else if (days.getMorningSecondEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getMorningSecondEnd().toString().replace(".5", ":30");
                }
            }
            dayWorkVoList.add(new DayWorkVo(i, workTime));
            i++;
        }
        if (days.getAfternoonEnd() != null) {
            if (days.getAfternoonStart().toString().endsWith(".0")) {
                workTime = days.getAfternoonStart().toString().replace(".0", ":00");
                if (days.getAfternoonEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getAfternoonEnd().toString().replace(".0", ":00");
                } else if (days.getAfternoonEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getAfternoonEnd().toString().replace(".5", ":30");
                }
            } else if (days.getAfternoonStart().toString().endsWith(".5")) {
                workTime = days.getAfternoonStart().toString().replace(".5", ":30");
                if (days.getAfternoonEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getAfternoonEnd().toString().replace(".0", ":00");
                } else if (days.getAfternoonEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getAfternoonEnd().toString().replace(".5", ":30");
                }
            }
            dayWorkVoList.add(new DayWorkVo(i, workTime));
            i++;
        }
        if (days.getAfternoonSecondEnd() != null) {
            if (days.getAfternoonSecondStart().toString().endsWith(".0")) {
                workTime = days.getAfternoonSecondStart().toString().replace(".0", ":00");
                if (days.getAfternoonSecondEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getAfternoonSecondEnd().toString().replace(".0", ":00");
                } else if (days.getAfternoonSecondEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getAfternoonSecondEnd().toString().replace(".5", ":30");
                }
            } else if (days.getAfternoonSecondStart().toString().endsWith(".5")) {
                workTime = days.getAfternoonSecondStart().toString().replace(".5", ":30");
                if (days.getAfternoonSecondEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getAfternoonSecondEnd().toString().replace(".0", ":00");
                } else if (days.getAfternoonSecondEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getAfternoonSecondEnd().toString().replace(".5", ":30");
                }
            }
            dayWorkVoList.add(new DayWorkVo(i, workTime));
            i++;
        }
        if (days.getEveningEnd() != null) {
            if (days.getEveningStart().toString().endsWith(".0")) {
                workTime = days.getEveningStart().toString().replace(".0", ":00");
                if (days.getEveningEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getEveningEnd().toString().replace(".0", ":00");
                } else if (days.getEveningEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getEveningEnd().toString().replace(".5", ":30");
                }
            } else if (days.getEveningStart().toString().endsWith(".5")) {
                workTime = days.getEveningStart().toString().replace(".5", ":30");
                if (days.getEveningEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getEveningEnd().toString().replace(".0", ":00");
                } else if (days.getEveningEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getEveningEnd().toString().replace(".5", ":30");
                }
            }
            dayWorkVoList.add(new DayWorkVo(i, workTime));
            i++;
        }
        if (days.getEveningSecondEnd() != null) {
            if (days.getEveningSecondStart().toString().endsWith(".0")) {
                workTime = days.getEveningSecondStart().toString().replace(".0", ":00");
                if (days.getEveningSecondEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getEveningSecondEnd().toString().replace(".0", ":00");
                } else if (days.getEveningSecondEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getEveningSecondEnd().toString().replace(".5", ":30");
                }
            } else if (days.getEveningSecondStart().toString().endsWith(".5")) {
                workTime = days.getEveningSecondStart().toString().replace(".5", ":30");
                if (days.getEveningSecondEnd().toString().endsWith(".0")) {
                    workTime = workTime + "-" + days.getEveningSecondEnd().toString().replace(".0", ":00");
                } else if (days.getEveningSecondEnd().toString().endsWith(".5")) {
                    workTime = workTime + "-" + days.getEveningSecondEnd().toString().replace(".5", ":30");
                }
            }
            dayWorkVoList.add(new DayWorkVo(i, workTime));
            i++;
        }

        return ResponseResult.okResult(dayWorkVoList);
    }

    @Override
    public ResponseResult generateWeekSchedule(GenerateWeekDto generateWeekDto) {
        // 如果有数据了就返回失败
        // 先通过门店id获取员工信息 有了员工信息再查排班信息
        LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoUser::getInfoId, generateWeekDto.getStoreId());
        List<InfoUser> infoUsers = infoUserMapper.selectList(wrapper);
        for (InfoUser infoUser : infoUsers) {
            List<UserScheduling> userScheduling =
                    userSchedulingMapper.selectData(infoUser.getUserId(), generateWeekDto.getDates().get(0), generateWeekDto.getDates().get(6));
            if (userScheduling.size() > 0) return ResponseResult.errorResult(AppHttpCodeEnum.HASE_DATA);
        }
        for (String date : generateWeekDto.getDates())
            generateDaySchedule(date, generateWeekDto.getStoreId());
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult neededMan(Long id, String date) {
        List<Needed> neededList = neededMapper.selectDayNeeded(id, date);
        List<NeededListVo> neededListVos = new ArrayList<>();
        for (Needed needed : neededList) {
            NeededListVo neededListVo = new NeededListVo();
            neededListVo.setId(needed.getId());
            neededListVo.setNeedMan(needed.getNeedMan());
            neededListVo.setDate(needed.getDate());
            if (needed.getTime().toString().endsWith(".0"))
                neededListVo.setTime(needed.getTime().toString().replace(".0", ":00"));
            else
                neededListVo.setTime(needed.getTime().toString().replace(".5", ":30"));
            neededListVos.add(neededListVo);
        }
        return ResponseResult.okResult(neededListVos);
    }

    private ScheduleInfo exchangeTime(Days days) {
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setDate(days.getDate());
        if (days.getMorningEnd() != null) {
            String s = String.valueOf(days.getMorningStart());
            String s1 = String.valueOf(days.getMorningEnd());
            if (s.endsWith(".0"))
                scheduleInfo.setMorningStart(s.replace(".0", ":00"));
            else
                scheduleInfo.setMorningStart(s.replace(".5", ":30"));
            if (s1.endsWith(".0"))
                scheduleInfo.setMorningEnd(s1.replace(".0", ":00"));
            else
                scheduleInfo.setMorningEnd(s1.replace(".5", ":30"));
        }
        if (days.getMorningSecondEnd() != null) {
            String s = String.valueOf(days.getMorningSecondStart());
            String s1 = String.valueOf(days.getMorningSecondEnd());
            if (s.endsWith(".0"))
                scheduleInfo.setMorningSecondStart(s.replace(".0", ":00"));
            else
                scheduleInfo.setMorningSecondStart(s.replace(".5", ":30"));
            if (s1.endsWith(".0"))
                scheduleInfo.setMorningSecondEnd(s1.replace(".0", ":00"));
            else
                scheduleInfo.setMorningSecondEnd(s1.replace(".5", ":30"));
        }
        if (days.getAfternoonEnd() != null) {
            String s = String.valueOf(days.getAfternoonStart());
            String s1 = String.valueOf(days.getAfternoonEnd());
            if (s.endsWith(".0"))
                scheduleInfo.setAfternoonStart(s.replace(".0", ":00"));
            else
                scheduleInfo.setAfternoonStart(s.replace(".5", ":30"));
            if (s1.endsWith(".0"))
                scheduleInfo.setAfternoonEnd(s1.replace(".0", ":00"));
            else
                scheduleInfo.setAfternoonEnd(s1.replace(".5", ":30"));
        }
        if (days.getAfternoonSecondEnd() != null) {
            String s = String.valueOf(days.getAfternoonSecondStart());
            String s1 = String.valueOf(days.getAfternoonSecondEnd());
            if (s.endsWith(".0"))
                scheduleInfo.setAfternoonSecondStart(s.replace(".0", ":00"));
            else
                scheduleInfo.setAfternoonSecondStart(s.replace(".5", ":30"));
            if (s1.endsWith(".0"))
                scheduleInfo.setAfternoonSecondEnd(s1.replace(".0", ":00"));
            else
                scheduleInfo.setAfternoonSecondEnd(s1.replace(".5", ":30"));
        }
        if (days.getEveningEnd() != null) {
            String s = String.valueOf(days.getEveningStart());
            String s1 = String.valueOf(days.getEveningEnd());
            if (s.endsWith(".0"))
                scheduleInfo.setEveningStart(s.replace(".0", ":00"));
            else
                scheduleInfo.setEveningStart(s.replace(".5", ":30"));
            if (s1.endsWith(".0"))
                scheduleInfo.setEveningEnd(s1.replace(".0", ":00"));
            else
                scheduleInfo.setEveningEnd(s1.replace(".5", ":30"));
        }
        if (days.getEveningSecondEnd() != null) {
            String s = String.valueOf(days.getEveningSecondStart());
            String s1 = String.valueOf(days.getEveningSecondEnd());
            if (s.endsWith(".0"))
                scheduleInfo.setEveningSecondStart(s.replace(".0", ":00"));
            else
                scheduleInfo.setEveningSecondStart(s.replace(".5", ":30"));
            if (s1.endsWith(".0"))
                scheduleInfo.setEveningSecondEnd(s1.replace(".0", ":00"));
            else
                scheduleInfo.setEveningSecondEnd(s1.replace(".5", ":30"));
        }
        return scheduleInfo;
    }


    @Override
    public ResponseResult readyToWorkMan(Long id, String time, String date) {
        double addTime;
        if (time.endsWith(":00")) {
            String replace = time.replace(":00", ".0");
            addTime = Double.parseDouble(replace);
        } else {
            String replace = time.replace(":30", ".5");
            addTime = Double.parseDouble(replace);
        }
        SimpleRule simpleRule = simpleRuleMapper.selectById(SystemConstants.SIMPLE_RULE_ID);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate;
        try {
            endDate = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int week = FindDateStatusUtil.weekNum(endDate);
        Map<String, String> weekMap = getWeekDate(endDate);
        String mondayDate = weekMap.get(SystemConstants.MONDAY_DATE);
        LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoUser::getInfoId, id);
        List<InfoUser> infoUsers = infoUserMapper.selectList(wrapper);
        List<SchedulingHelp> schedulingHelps = BeanCopyUtils.copyBeanList(infoUsers, SchedulingHelp.class);
        for (SchedulingHelp schedulingHelp : schedulingHelps) {
            schedulingHelp.setRoleId(userRoleMapper.selectOne(new LambdaQueryWrapper<UserRole>()
                            .eq(UserRole::getUserId, schedulingHelp.getUserId()))
                    .getRoleId());
            User user = userMapper.selectById(schedulingHelp.getUserId());
            schedulingHelp.setDayPreference(user.getDayPreference());
            schedulingHelp.setTimePreference(user.getTimePreference());
            schedulingHelp.setDayTimePreference(user.getDayTimePreference());
            schedulingHelp.setWeekTimePreference(user.getWeekTimePreference());
        }
        String localTime;
        if (addTime < SystemConstants.MID_TIME)
            localTime = "1";
        else if (addTime < SystemConstants.AFTERNOON_TIME)
            localTime = "2";
        else
            localTime = "3";
        for (SchedulingHelp schedulingHelp : schedulingHelps) {
            for (StoreWorked storeWorked : daysMapper.selectWorkedTime(schedulingHelp.getUserId(), mondayDate, date)) {
                weekTime(schedulingHelp, storeWorked, simpleRule);
                if (storeWorked.getDate().equals(date)) {
                    double dayTime = 0;
                    if (storeWorked.getMorningEnd() != null) {
                        dayTime = dayTime + storeWorked.getMorningEnd() - storeWorked.getMorningStart();
                    }
                    if (storeWorked.getMorningSecondEnd() != null) {
                        dayTime = dayTime + storeWorked.getMorningSecondEnd() - storeWorked.getMorningSecondStart();
                    }
                    if (storeWorked.getAfternoonEnd() != null) {
                        dayTime = dayTime + storeWorked.getAfternoonEnd() - storeWorked.getAfternoonStart();
                    }
                    if (storeWorked.getAfternoonSecondEnd() != null) {
                        dayTime = dayTime + storeWorked.getAfternoonSecondEnd() - storeWorked.getAfternoonSecondStart();
                    }
                    if (storeWorked.getEveningEnd() != null) {
                        dayTime = dayTime + storeWorked.getEveningEnd() - storeWorked.getEveningStart();
                    }
                    if (storeWorked.getEveningSecondEnd() != null) {
                        dayTime = dayTime + storeWorked.getEveningSecondEnd() - storeWorked.getEveningSecondStart();
                    }
                    if (dayTime > schedulingHelp.getDayTimePreference() - simpleRule.getLongDay())
                        schedulingHelp.setDayStatus(1);
                    else
                        schedulingHelp.setDayTime(dayTime);
                }
            }
            User user = userMapper.selectById(schedulingHelp.getUserId());
            schedulingHelp.setName(user.getName());
        }


        Map<String, List<CouldToWorkVo>> resultMap = new HashMap<>();
        // 1爱好在今天上班 但是不在这个时间段上班的员工
        List<SchedulingHelp> sameDayWorkers = schedulingHelps.stream()
                .filter(schedulingHelp -> schedulingHelp.getTimePreference() == null ||
                        !schedulingHelp.getTimePreference().contains(localTime))
                .filter(schedulingHelp -> schedulingHelp.getWeekStatus() == 0 &&
                                schedulingHelp.getDayStatus() == 0 && (schedulingHelp.getDayPreference() == null ||
                                schedulingHelp.getDayPreference().contains(String.valueOf(week))
                        )
                )
                .toList();
        List<CouldToWorkVo> couldToWorkVoList = new ArrayList<>();
        List<CouldToWorkVo> couldToWorkVoList2 = new ArrayList<>();
        List<CouldToWorkVo> couldToWorkVoList3 = new ArrayList<>();
        List<CouldToWorkVo> couldToWorkVoList4 = new ArrayList<>();
        if (sameDayWorkers.size() != 0) {
            for (SchedulingHelp schedulingHelp : sameDayWorkers) {
                CouldToWorkVo couldToWorkVo = new CouldToWorkVo();
                couldToWorkVo.setUserId(schedulingHelp.getUserId());
                couldToWorkVo.setName(schedulingHelp.getName());
                double dayTime = 0.0;
                if (schedulingHelp.getDayTime() != null)
                    dayTime = schedulingHelp.getDayTime();
                double weekTime = 0.0;
                if (schedulingHelp.getWeekTime() != null)
                    weekTime = schedulingHelp.getWeekTime();
                if (schedulingHelp.getDayTimePreference() - dayTime >= simpleRule.getSingleTop() &&
                        schedulingHelp.getWeekTimePreference() - weekTime >= simpleRule.getSingleTop())
                    couldToWorkVo.setWorkTime(simpleRule.getSingleTop());
                else if (schedulingHelp.getDayTimePreference() - dayTime >= simpleRule.getSingleTop() &&
                        schedulingHelp.getWeekTimePreference() - weekTime < simpleRule.getSingleTop())
                    couldToWorkVo.setWorkTime(schedulingHelp.getWeekTimePreference() - weekTime);
                else if (schedulingHelp.getDayTimePreference() - dayTime < simpleRule.getSingleTop() &&
                        schedulingHelp.getWeekTimePreference() - weekTime >= simpleRule.getSingleTop())
                    couldToWorkVo.setWorkTime(schedulingHelp.getDayTimePreference() - dayTime);
                else if (schedulingHelp.getDayTimePreference() - dayTime < simpleRule.getSingleTop() &&
                        schedulingHelp.getWeekTimePreference() - weekTime < simpleRule.getSingleTop() &&
                        schedulingHelp.getDayTimePreference() - dayTime < schedulingHelp.getWeekTimePreference() - weekTime)
                    couldToWorkVo.setWorkTime(schedulingHelp.getDayTimePreference() - dayTime);
                else if (schedulingHelp.getDayTimePreference() - dayTime < simpleRule.getSingleTop() &&
                        schedulingHelp.getWeekTimePreference() - weekTime < simpleRule.getSingleTop() &&
                        schedulingHelp.getDayTimePreference() - dayTime >= schedulingHelp.getWeekTimePreference() - weekTime)
                    couldToWorkVo.setWorkTime(schedulingHelp.getWeekTimePreference() - weekTime);
                couldToWorkVoList.add(couldToWorkVo);
            }
            resultMap.put(SITUATION_ONE, couldToWorkVoList);
        } else
            resultMap.put(SITUATION_ONE, couldToWorkVoList);
        // 2不爱好在今天上班的员工
        List<SchedulingHelp> differentDayWorkers = schedulingHelps.stream()
                .filter(schedulingHelp -> schedulingHelp.getWeekStatus() == 0)
                .filter(schedulingHelp -> schedulingHelp.getDayPreference() == null ||
                        !schedulingHelp.getDayPreference().contains(String.valueOf(week)))
                .toList();
        if (differentDayWorkers.size() != 0) {
            for (SchedulingHelp schedulingHelp : differentDayWorkers) {
                CouldToWorkVo couldToWorkVo = new CouldToWorkVo();
                couldToWorkVo.setUserId(schedulingHelp.getUserId());
                couldToWorkVo.setName(schedulingHelp.getName());
                double weekTime = 0.0;
                if (schedulingHelp.getWeekTime() != null)
                    weekTime = schedulingHelp.getWeekTime();
                if (schedulingHelp.getWeekTimePreference() - weekTime >= simpleRule.getSingleTop())
                    couldToWorkVo.setWorkTime(simpleRule.getSingleTop());
                else if (schedulingHelp.getWeekTimePreference() - weekTime < simpleRule.getSingleTop())
                    couldToWorkVo.setWorkTime(schedulingHelp.getWeekTimePreference() - weekTime);
                couldToWorkVoList2.add(couldToWorkVo);
            }
            resultMap.put(SITUATION_TWE, couldToWorkVoList2);
        } else
            resultMap.put(SITUATION_TWE, null);
        // 3还是不够可以合理安排员工进行一定加班
        // 1⃣️爱好在今天上班 但是日时长或者周时长满了的
        List<SchedulingHelp> addSameDayWorkers = schedulingHelps.stream()
                .filter(schedulingHelp -> schedulingHelp.getTimePreference() == null ||
                        !schedulingHelp.getTimePreference().contains(localTime))
                .filter(schedulingHelp -> schedulingHelp.getDayPreference() == null || schedulingHelp.getDayPreference().contains(String.valueOf(week)))
                .toList();
        if (addSameDayWorkers.size() != 0) {
            for (SchedulingHelp schedulingHelp : addSameDayWorkers) {
                CouldToWorkVo couldToWorkVo = new CouldToWorkVo();
                couldToWorkVo.setUserId(schedulingHelp.getUserId());
                couldToWorkVo.setName(schedulingHelp.getName());
                couldToWorkVo.setWorkTime(simpleRule.getSingleTop());
                couldToWorkVoList3.add(couldToWorkVo);
            }
            resultMap.put(SITUATION_THREE, couldToWorkVoList3);
        } else
            resultMap.put(SITUATION_THREE, null);
        // 2⃣️不爱好在今天上班 但是周时长满了的员工
        List<SchedulingHelp> addDifferentDayWorkers = schedulingHelps.stream()
                .filter(schedulingHelp -> schedulingHelp.getDayPreference() == null ||
                        !schedulingHelp.getDayPreference().contains(String.valueOf(week)))
                .toList();
        if (addDifferentDayWorkers.size() != 0) {
            for (SchedulingHelp schedulingHelp : addDifferentDayWorkers) {
                CouldToWorkVo couldToWorkVo = new CouldToWorkVo();
                couldToWorkVo.setUserId(schedulingHelp.getUserId());
                couldToWorkVo.setName(schedulingHelp.getName());
                couldToWorkVo.setWorkTime(simpleRule.getSingleTop());
                couldToWorkVoList4.add(couldToWorkVo);
            }
            resultMap.put(SITUATION_FOUR, couldToWorkVoList4);
        } else
            resultMap.put(SITUATION_FOUR, null);
        return ResponseResult.okResult(resultMap);
    }


    private List<DaysListVo> changeList(List<Days> daysList) {
        List<DaysListVo> daysListVos = new ArrayList<>();
        for (Days days : daysList) {
            DaysListVo daysListVo = new DaysListVo();
            daysListVo.setDate(days.getDate());
            daysListVo.setId(days.getId());
            if (days.getMorningEnd() != null) {
                String s = String.valueOf(days.getMorningStart());
                String s1 = String.valueOf(days.getMorningEnd());
                if (s.endsWith(".0"))
                    daysListVo.setMorningStart(s.replace(".0", ":00"));
                else
                    daysListVo.setMorningStart(s.replace(".5", ":30"));
                if (s1.endsWith(".0"))
                    daysListVo.setMorningEnd(s1.replace(".0", ":00"));
                else
                    daysListVo.setMorningEnd(s1.replace(".5", ":30"));
            }
            if (days.getMorningSecondEnd() != null) {
                String s = String.valueOf(days.getMorningSecondStart());
                String s1 = String.valueOf(days.getMorningSecondEnd());
                if (s.endsWith(".0"))
                    daysListVo.setMorningSecondStart(s.replace(".0", ":00"));
                else
                    daysListVo.setMorningSecondStart(s.replace(".5", ":30"));
                if (s1.endsWith(".0"))
                    daysListVo.setMorningSecondEnd(s1.replace(".0", ":00"));
                else
                    daysListVo.setMorningSecondEnd(s1.replace(".5", ":30"));
            }
            if (days.getAfternoonEnd() != null) {
                String s = String.valueOf(days.getAfternoonStart());
                String s1 = String.valueOf(days.getAfternoonEnd());
                if (s.endsWith(".0"))
                    daysListVo.setAfternoonStart(s.replace(".0", ":00"));
                else
                    daysListVo.setAfternoonStart(s.replace(".5", ":30"));
                if (s1.endsWith(".0"))
                    daysListVo.setAfternoonEnd(s1.replace(".0", ":00"));
                else
                    daysListVo.setAfternoonEnd(s1.replace(".5", ":30"));
            }
            if (days.getAfternoonSecondEnd() != null) {
                String s = String.valueOf(days.getAfternoonSecondStart());
                String s1 = String.valueOf(days.getAfternoonSecondEnd());
                if (s.endsWith(".0"))
                    daysListVo.setAfternoonSecondStart(s.replace(".0", ":00"));
                else
                    daysListVo.setAfternoonSecondStart(s.replace(".5", ":30"));
                if (s1.endsWith(".0"))
                    daysListVo.setAfternoonSecondEnd(s1.replace(".0", ":00"));
                else
                    daysListVo.setAfternoonSecondEnd(s1.replace(".5", ":30"));
            }
            if (days.getEveningEnd() != null) {
                String s = String.valueOf(days.getEveningStart());
                String s1 = String.valueOf(days.getEveningEnd());
                if (s.endsWith(".0"))
                    daysListVo.setEveningStart(s.replace(".0", ":00"));
                else
                    daysListVo.setEveningStart(s.replace(".5", ":30"));
                if (s1.endsWith(".0"))
                    daysListVo.setEveningEnd(s1.replace(".0", ":00"));
                else
                    daysListVo.setEveningEnd(s1.replace(".5", ":30"));
            }
            if (days.getEveningSecondEnd() != null) {
                String s = String.valueOf(days.getEveningSecondStart());
                String s1 = String.valueOf(days.getEveningSecondEnd());
                if (s.endsWith(".0"))
                    daysListVo.setEveningSecondStart(s.replace(".0", ":00"));
                else
                    daysListVo.setEveningSecondStart(s.replace(".5", ":30"));
                if (s1.endsWith(".0"))
                    daysListVo.setEveningSecondEnd(s1.replace(".0", ":00"));
                else
                    daysListVo.setEveningSecondEnd(s1.replace(".5", ":30"));
            }
            daysListVos.add(daysListVo);
        }
        return daysListVos;
    }

    private Map<String, String> dateTimeHelp(ScheduleDto scheduleDto) {
        Map<String, String> weekDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(scheduleDto.getDate());
            weekDate = getWeekDate(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return weekDate;
    }

    private List<ScheduleInfo> scheduleInfoHelp(ScheduleDto scheduleDto) {
        List<User> userList = userMapper.selectInfo(scheduleDto.getStoreId());
        List<ScheduleInfo> scheduleInfos = BeanCopyUtils.copyBeanList(userList, ScheduleInfo.class);
        for (ScheduleInfo scheduleInfo : scheduleInfos)
            scheduleInfo.setRoleName(roleMapper.selectRoleName(scheduleInfo.getId()));
        if (scheduleDto.getRoleId() != null && scheduleDto.getRoleId() >= 0) {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getId, scheduleDto.getRoleId());
            Role role = roleMapper.selectOne(wrapper);
            scheduleInfos = scheduleInfos.stream()
                    .filter(scheduleInfo -> scheduleInfo.getRoleName().equals(role.getRoleName()))
                    .toList();
        }
        return scheduleInfos;
    }

    void generateDaySchedule(String end, Long storeId) {
        SimpleRule simpleRule = simpleRuleMapper.selectById(SystemConstants.SIMPLE_RULE_ID);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate;
        try {
            endDate = sdf.parse(end);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> weekMap = getWeekDate(endDate);
        String mondayDate = weekMap.get(SystemConstants.MONDAY_DATE);
        AiData aiData = aiDataMapper.selectData(storeId, end);
        int week = FindDateStatusUtil.weekNum(endDate);
        Info info = infoMapper.selectById(storeId);
        LambdaQueryWrapper<InfoUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InfoUser::getInfoId, storeId);
        List<InfoUser> infoUsers = infoUserMapper.selectList(wrapper);
        List<SchedulingHelp> schedulingHelps = BeanCopyUtils.copyBeanList(infoUsers, SchedulingHelp.class);
        for (SchedulingHelp schedulingHelp : schedulingHelps) {
            schedulingHelp.setRoleId(userRoleMapper.selectOne(new LambdaQueryWrapper<UserRole>()
                            .eq(UserRole::getUserId, schedulingHelp.getUserId()))
                    .getRoleId());
            User user = userMapper.selectById(schedulingHelp.getUserId());
            schedulingHelp.setDayPreference(user.getDayPreference());
            schedulingHelp.setTimePreference(user.getTimePreference());
            schedulingHelp.setDayTimePreference(user.getDayTimePreference());
            schedulingHelp.setWeekTimePreference(user.getWeekTimePreference());
        }
        for (SchedulingHelp schedulingHelp : schedulingHelps)
            for (StoreWorked storeWorked :
                    daysMapper.selectWorkedTime(schedulingHelp.getUserId(), mondayDate, end))
                weekTime(schedulingHelp, storeWorked, simpleRule);
        List<SchedulingHelp> morningWorkers = schedulingHelps.stream()
                .filter(schedulingHelp -> schedulingHelp.getTimePreference() == null || schedulingHelp.getTimePreference().contains(SystemConstants.MORNING))
                .filter(schedulingHelp -> schedulingHelp.getDayPreference() == null || schedulingHelp.getDayPreference().contains(String.valueOf(week)))
                .toList();
        List<SchedulingHelp> afternoonWorkers = schedulingHelps.stream()
                .filter(schedulingHelp -> schedulingHelp.getTimePreference() == null || schedulingHelp.getTimePreference().contains(SystemConstants.AFTERNOON))
                .filter(schedulingHelp -> schedulingHelp.getDayPreference() == null || schedulingHelp.getDayPreference().contains(String.valueOf(week)))
                .toList();
        List<SchedulingHelp> eveningWorkers = schedulingHelps.stream()
                .filter(schedulingHelp -> schedulingHelp.getTimePreference() == null || schedulingHelp.getTimePreference().contains(SystemConstants.EVENING))
                .filter(schedulingHelp -> schedulingHelp.getDayPreference() == null || schedulingHelp.getDayPreference().contains(String.valueOf(week)))
                .toList();
        Rule rule = ruleMapper.selectByStoreId(storeId);
        String startRule = rule.getStartRule();
        String[] start = startRule.split(SystemConstants.COMMA);
        double earlyTime = Double.parseDouble(start[0]);
        int earlyNum = (int) Math.ceil((info.getSize() * 100) / (Double.parseDouble(start[1]) * 100));
        String endRule = rule.getEndRule();
        String[] ends = endRule.split(SystemConstants.COMMA);
        double leaveTime = Double.parseDouble(ends[0]);
        int provisionNum = Integer.parseInt(ends[1]);
        int leaveNum = Math.max((int) Math.ceil(
                (info.getSize() * 100) / (Double.parseDouble(ends[2]) * 100)), provisionNum);
        double startTime;
        double endTime;
        if (week == 6 || week == 7) {
            startTime = simpleRule.getWeekendStart() - earlyTime;
            endTime = leaveTime + simpleRule.getWeekendEnd();
            if (startTime < SystemConstants.MORNING_START)
                throw new RuntimeException("开门时间不能早过8点，请管理员重新制定规则");
            if (startTime >= simpleRule.getWeekendStart())
                throw new RuntimeException("开门时间不能晚于" + simpleRule.getWeekendStart() + "点，请管理员重新制定规则");
        } else {
            startTime = simpleRule.getWeekStart() - earlyTime;
            endTime = leaveTime + simpleRule.getWeekEnd();
            if (startTime < SystemConstants.MORNING_START)
                throw new RuntimeException("开门时间不能早过8点，请管理员重新制定规则");
            if (startTime >= simpleRule.getWeekStart())
                throw new RuntimeException("开门时间不能晚于" + simpleRule.getWeekStart() + "点，请管理员重新制定规则");
        }
        double i = startTime;
        double flowRule = Double.parseDouble(rule.getFlowRule()) * 100;
        int workNum;
        Double singleSmall = simpleRule.getSingleSmall();
        Double singleTop = simpleRule.getSingleTop();
        while (i < SystemConstants.MID_TIME) {
            if (startTime == 9.0) {
                if (i == 9.0) {
                    int nineFirstNum = (int) Math.ceil((aiData.getNineFirst() * 100) / flowRule);
                    workNum = earlyNum + nineFirstNum;
                    if (workNum == 0) {
                        i += 0.5;
                        continue;
                    }
                    dayStart(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, info, workNum, endDate);
                } else if (i == 9.5) {
                    int nineLastNum = (int) Math.ceil((aiData.getNineLast() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : morningWorkers)
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    if (count >= nineLastNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = nineLastNum - count;
                    dayStart(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, info, workNum, endDate);
                }
            } else if (startTime == 9.5) {
                if (i == 9.5) {
                    int nineLastNum = (int) Math.ceil((aiData.getNineLast() * 100) / flowRule);
                    workNum = earlyNum + nineLastNum;
                    if (workNum == 0) {
                        i += 0.5;
                        continue;
                    }
                    dayStart(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, info, workNum, endDate);
                }
            } else {
                if (startTime == 8.0) {
                    if (i == 8.0) {
                        int eightFirstNum = (int) Math.ceil((aiData.getEightFirst() * 100) / flowRule);
                        workNum = earlyNum + eightFirstNum;
                        if (workNum == 0) {
                            i += 0.5;
                            continue;
                        }
                        dayStart(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, info, workNum, endDate);
                    } else if (i == 8.5) {
                        int eightLastNum = (int) Math.ceil((aiData.getEightLast() * 100) / flowRule);
                        int count = 0;
                        for (SchedulingHelp schedulingHelp : morningWorkers)
                            if (schedulingHelp.getStatus() == 1)
                                count += 1;
                        if (count >= eightLastNum) {
                            i += 0.5;
                            continue;
                        }
                        workNum = eightLastNum - count;
                        dayStart(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, info, workNum, endDate);
                    }
                } else if (startTime == 8.5) {
                    if (i == 8.5) {
                        int eightLastNum = (int) Math.ceil((aiData.getEightLast() * 100) / flowRule);
                        workNum = earlyNum + eightLastNum;
                        if (workNum == 0) {
                            i += 0.5;
                            continue;
                        }
                        dayStart(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, info, workNum, endDate);
                    }
                }
                if (i == 9.0) {
                    int nineFirstNum = (int) Math.ceil((aiData.getNineFirst() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : morningWorkers)
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    if (count >= nineFirstNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = nineFirstNum - count;
                    morningSchedule(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
                } else if (i == 9.5) {
                    int nineLastNum = (int) Math.ceil((aiData.getNineLast() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : morningWorkers)
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    if (count >= nineLastNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = nineLastNum - count;
                    morningSchedule(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
                }
            }
            if (i == 10.0) {
                int tenFirstNum = (int) Math.ceil((aiData.getTenFirst() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : morningWorkers) {
                    if (isEndMorning(i, schedulingHelp))
                        schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= tenFirstNum) {
                    i += 0.5;
                    continue;
                }
                workNum = tenFirstNum - count;
                morningSchedule(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 10.5) {
                int tenLastNum = (int) Math.ceil((aiData.getTenLast() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : morningWorkers) {
                    if (isEndMorning(i, schedulingHelp))
                        schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= tenLastNum) {
                    i += 0.5;
                    continue;
                }
                workNum = tenLastNum - count;
                morningToAfternoonSchedule(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 11.0) {
                int elevenFirstNum = (int) Math.ceil((aiData.getElevenFirst() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : morningWorkers) {
                    if (isEndMorning(i, schedulingHelp))
                        schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= elevenFirstNum) {
                    i += 0.5;
                    continue;
                }
                workNum = elevenFirstNum - count;
                morningToAfternoonSchedule(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 11.5) {
                int elevenLastNum = (int) Math.ceil((aiData.getElevenLast() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : morningWorkers) {
                    if (isEndMorning(i, schedulingHelp))
                        schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= elevenLastNum) {
                    i += 0.5;
                    continue;
                }
                workNum = elevenLastNum - count;
                morningToAfternoonSchedule(morningWorkers, afternoonWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            }
            i += 0.5;
        }

        while (i < SystemConstants.AFTERNOON_TIME) {
            if (i == 12.0) {
                int twelveFirstNum = (int) Math.ceil((aiData.getTwelveFirst() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp))
                        schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= twelveFirstNum) {
                    i += 0.5;
                    continue;
                }
                workNum = twelveFirstNum - count;
                afternoonSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 12.5) {
                int twelveLastNum = (int) Math.ceil((aiData.getTwelveLast() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= twelveLastNum) {
                    i += 0.5;
                    continue;
                }
                workNum = twelveLastNum - count;
                afternoonSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 13.0) {
                int thirteenFirstNum = (int) Math.ceil((aiData.getThirteenFirst() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= thirteenFirstNum) {
                    i += 0.5;
                    continue;
                }
                workNum = thirteenFirstNum - count;
                afternoonSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 13.5) {
                int thirteenLastNum = (int) Math.ceil((aiData.getThirteenLast() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= thirteenLastNum) {
                    i += 0.5;
                    continue;
                }
                workNum = thirteenLastNum - count;
                afternoonSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 14.0) {
                int fourteenFirstNum = (int) Math.ceil((aiData.getFourteenFirst() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= fourteenFirstNum) {
                    i += 0.5;
                    continue;
                }
                workNum = fourteenFirstNum - count;
                afternoonSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 14.5) {
                int fourteenLastNum = (int) Math.ceil((aiData.getFourteenLast() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= fourteenLastNum) {
                    i += 0.5;
                    continue;
                }
                workNum = fourteenLastNum - count;
                afternoonSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 15.0) {
                int fifteenFirstNum = (int) Math.ceil((aiData.getFifteenFirst() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= fifteenFirstNum) {
                    i += 0.5;
                    continue;
                }
                workNum = fifteenFirstNum - count;
                afternoonSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 15.5) {
                int fifteenLastNum = (int) Math.ceil((aiData.getFifteenLast() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= fifteenLastNum) {
                    i += 0.5;
                    continue;
                }
                workNum = fifteenLastNum - count;
                afternoonToEveningSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 16.0) {
                int sixteenFirstNum = (int) Math.ceil((aiData.getSixteenFirst() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= sixteenFirstNum) {
                    i += 0.5;
                    continue;
                }
                workNum = sixteenFirstNum - count;
                afternoonToEveningSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 16.5) {
                int sixteenLastNum = (int) Math.ceil((aiData.getSixteenLast() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : afternoonWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= sixteenLastNum) {
                    i += 0.5;
                    continue;
                }
                workNum = sixteenLastNum - count;
                afternoonToEveningSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            } else if (i == 17.0) {
                int seventeenFirstNum = (int) Math.ceil((aiData.getSeventeenFirst() * 100) / flowRule);
                int count = 0;
                for (SchedulingHelp schedulingHelp : eveningWorkers) {
                    if (isEndAfternoon(i, schedulingHelp)) schedulingHelp.setStatus(0);
                    if (schedulingHelp.getStatus() == 1)
                        count += 1;
                }
                if (count >= seventeenFirstNum) {
                    i += 0.5;
                    continue;
                }
                workNum = seventeenFirstNum - count;
                afternoonToEveningSchedule(afternoonWorkers, eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, endDate, info);
            }
            i += 0.5;
        }

        while (i <= endTime) {
            if (week == 6 || week == 7) {
                if (i == 17.5) {
                    int seventeenLastNum = (int) Math.ceil((aiData.getSeventeenLast() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= seventeenLastNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = seventeenLastNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekendEnd(), endDate, info);
                } else if (i == 18.0) {
                    int eighteenFirstNum = (int) Math.ceil((aiData.getEighteenFirst() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= eighteenFirstNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = eighteenFirstNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekendEnd(), endDate, info);
                } else if (i == 18.5) {
                    int eighteenLastNum = (int) Math.ceil((aiData.getEighteenLast() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= eighteenLastNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = eighteenLastNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekendEnd(), endDate, info);
                } else if (i == 19.0) {
                    int nineteenFirstNum = (int) Math.ceil((aiData.getNineteenFirst() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= nineteenFirstNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = nineteenFirstNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekendEnd(), endDate, info);
                } else if (i == 19.5) {
                    int nineteenLastNum = (int) Math.ceil((aiData.getNineteenLast() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= nineteenLastNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = nineteenLastNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekendEnd(), endDate, info);
                } else if (i == 20.0) {
                    int twentyFirstNum = (int) Math.ceil((aiData.getTwentyFirst() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= twentyFirstNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = twentyFirstNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekendEnd(), endDate, info);
                } else if (i == simpleRule.getWeekendEnd()) {
                    scheduleForClean(eveningWorkers, leaveNum, leaveTime, endTime, info, simpleRule, endDate);
                    break;
                }
            } else {
                if (i == 17.5) {
                    int seventeenLastNum = (int) Math.ceil((aiData.getSeventeenLast() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= seventeenLastNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = seventeenLastNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekEnd(), endDate, info);
                } else if (i == 18.0) {
                    int eighteenFirstNum = (int) Math.ceil((aiData.getEighteenFirst() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= eighteenFirstNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = eighteenFirstNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekEnd(), endDate, info);
                } else if (i == 18.5) {
                    int eighteenLastNum = (int) Math.ceil((aiData.getEighteenLast() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= eighteenLastNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = eighteenLastNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekEnd(), endDate, info);
                } else if (i == 19.0) {
                    int nineteenFirstNum = (int) Math.ceil((aiData.getNineteenFirst() * 100) / flowRule);
                    int count = 0;
                    for (SchedulingHelp schedulingHelp : eveningWorkers) {
                        if (isEndEvening(i, schedulingHelp)) schedulingHelp.setStatus(0);
                        if (schedulingHelp.getStatus() == 1)
                            count += 1;
                    }
                    if (count >= nineteenFirstNum) {
                        i += 0.5;
                        continue;
                    }
                    workNum = nineteenFirstNum - count;
                    eveningSchedule(eveningWorkers, singleTop, singleSmall, i, simpleRule, workNum, simpleRule.getWeekEnd(), endDate, info);
                } else if (i == simpleRule.getWeekEnd()) {
                    scheduleForClean(eveningWorkers, leaveNum, leaveTime, endTime, info, simpleRule, endDate);
                    break;
                }
            }
            i += 0.5;
        }

        List<SchedulingHelp> result = schedulingHelps.
                stream().filter(schedulingHelp -> schedulingHelp.getDayPreference() == null || schedulingHelp.getDayPreference().contains(String.valueOf(week)))
                .toList();

        for (SchedulingHelp schedulingHelp : result) {
            Days days = BeanCopyUtils.copyBean(schedulingHelp, Days.class);
            days.setDate(endDate);
            daysMapper.insert(days);
            userSchedulingMapper.insert(new UserScheduling(schedulingHelp.getUserId(), days.getId()));
        }
    }


    private boolean cantTOClean(SchedulingHelp schedulingHelp, double endTime, double leaveTime, SimpleRule simpleRule) {
        if (schedulingHelp.getStatus() == 1)
            return true;
        if (schedulingHelp.getEveningEnd() == null) {
            return false;
        } else if (schedulingHelp.getEveningSecondEnd() == null) {
            if ((schedulingHelp.getEveningEnd() == (endTime - leaveTime))) {
                return (!(schedulingHelp.getEveningEnd() - schedulingHelp.getEveningStart() + leaveTime <= simpleRule.getSingleTop())) ||
                        (!(schedulingHelp.getDayTime() + leaveTime <= schedulingHelp.getDayTimePreference())) ||
                        (!(schedulingHelp.getWeekTime() + leaveTime <= schedulingHelp.getWeekTimePreference()));
            } else {
                return (!(schedulingHelp.getDayTime() + leaveTime <= schedulingHelp.getDayTimePreference())) ||
                        (!(schedulingHelp.getWeekTime() + leaveTime <= schedulingHelp.getWeekTimePreference()));
            }
        } else {
            if ((schedulingHelp.getEveningSecondEnd() == (endTime - leaveTime))) {
                return (!(schedulingHelp.getEveningSecondEnd() - schedulingHelp.getEveningStart() + leaveTime <= simpleRule.getSingleTop())) ||
                        (!(schedulingHelp.getDayTime() + leaveTime <= schedulingHelp.getDayTimePreference())) ||
                        (!(schedulingHelp.getWeekTime() + leaveTime <= schedulingHelp.getWeekTimePreference()));
            }
        }
        return true;
    }

    private boolean haveManToStayForClean(List<SchedulingHelp> eveningWorkers, double endTime, Info info, double leaveTime, SimpleRule simpleRule) {
        List<SchedulingHelp> schedulingHelps = eveningWorkers.stream()
                .filter(eveningWorker -> info.getDoLater().contains(eveningWorker.getRoleId().toString()))
                .toList();
        for (SchedulingHelp schedulingHelp : schedulingHelps) {
            if (schedulingHelp.getStatus() != 1 && schedulingHelp.getWeekStatus() != 1 && schedulingHelp.getDayStatus() != 1) {
                if (schedulingHelp.getEveningEnd() == null) {
                    return true;
                } else if (schedulingHelp.getEveningSecondEnd() == null) {
                    if ((schedulingHelp.getEveningEnd() == (endTime - leaveTime))) {
                        if ((schedulingHelp.getEveningEnd() - schedulingHelp.getEveningStart() + leaveTime <= simpleRule.getSingleTop()) &&
                                (schedulingHelp.getDayTime() + leaveTime <= schedulingHelp.getDayTimePreference()) &&
                                (schedulingHelp.getWeekTime() + leaveTime <= schedulingHelp.getWeekTimePreference())) {
                            return true;
                        }
                    } else {
                        if ((schedulingHelp.getDayTime() + leaveTime <= schedulingHelp.getDayTimePreference()) &&
                                (schedulingHelp.getWeekTime() + leaveTime <= schedulingHelp.getWeekTimePreference())) {
                            return true;
                        }
                    }
                } else {
                    if ((schedulingHelp.getEveningSecondEnd() == (endTime - leaveTime))) {
                        if ((schedulingHelp.getEveningSecondEnd() - schedulingHelp.getEveningStart() + leaveTime <= simpleRule.getSingleTop()) &&
                                (schedulingHelp.getDayTime() + leaveTime <= schedulingHelp.getDayTimePreference()) &&
                                (schedulingHelp.getWeekTime() + leaveTime <= schedulingHelp.getWeekTimePreference())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isNotReadyForEvening(SchedulingHelp eveningWorker, SimpleRule simpleRule, double i) {
        if (eveningWorker.getWeekStatus() != 1 && eveningWorker.getDayStatus() != 1) {
            if (eveningWorker.getStatus() == 1)
                return true;
            if (eveningWorker.getEveningEnd() == null &&
                    eveningWorker.getAfternoonSecondEnd() == null &&
                    eveningWorker.getAfternoonEnd() != null &&
                    (i - eveningWorker.getAfternoonEnd() < simpleRule.getWakeTime()))
                return true;
            if (eveningWorker.getEveningEnd() == null &&
                    eveningWorker.getAfternoonSecondEnd() != null &&
                    (i - eveningWorker.getAfternoonSecondEnd() < simpleRule.getWakeTime()))
                return true;
            return eveningWorker.getEveningEnd() != null &&
                    (i - eveningWorker.getEveningEnd() < simpleRule.getWakeTime());
        }
        return false;
    }

    private boolean haveManToWorkForEvening(List<SchedulingHelp> eveningWorkers, double i, SimpleRule simpleRule) {
        int count = 0;
        for (SchedulingHelp eveningWorker : eveningWorkers) {
            if (eveningWorker.getDayStatus() != 1 && eveningWorker.getWeekStatus() != 1 && eveningWorker.getStatus() != 1) {
                if (eveningWorker.getEveningEnd() == null) {
                    if (eveningWorker.getAfternoonSecondEnd() != null &&
                            (i - eveningWorker.getAfternoonSecondEnd() >= simpleRule.getWakeTime()))
                        count++;
                    else if (eveningWorker.getAfternoonSecondEnd() == null &&
                            eveningWorker.getAfternoonEnd() != null &&
                            (i - eveningWorker.getAfternoonEnd() >= simpleRule.getWakeTime()))
                        count++;
                    else if (eveningWorker.getAfternoonEnd() == null &&
                            eveningWorker.getAfternoonSecondEnd() == null)
                        count++;
                } else {
                    if (i - eveningWorker.getEveningEnd() >= simpleRule.getWakeTime())
                        count++;
                }
            }
        }
        return count > 0;
    }

    private boolean isEndEvening(double i, SchedulingHelp eveningWorkers) {
        if (eveningWorkers.getEveningEnd() == null) {
            if (eveningWorkers.getAfternoonEnd() == null)
                return true;
            if (eveningWorkers.getStatus() == 1 && eveningWorkers.getAfternoonSecondEnd() != null && eveningWorkers.getAfternoonSecondEnd() <= i)
                return true;
            return eveningWorkers.getStatus() == 1 && eveningWorkers.getAfternoonSecondEnd() == null && eveningWorkers.getAfternoonEnd() <= i;
        } else {
            return eveningWorkers.getStatus() == 1 && eveningWorkers.getEveningEnd() <= i;
        }
    }

    private boolean haveManContinueToEvening
            (List<SchedulingHelp> afternoonWorkers, List<SchedulingHelp> eveningWorkers, double i, SimpleRule
                    simpleRule) {
        int count = 0;
        for (SchedulingHelp afternoon : afternoonWorkers)
            for (SchedulingHelp evening : eveningWorkers)
                if (Objects.equals(afternoon.getUserId(), evening.getUserId()) &&
                        afternoon.getStatus() != 1 &&
                        afternoon.getWeekStatus() != 1 &&
                        afternoon.getDayStatus() != 1)
                    if (afternoon.getAfternoonEnd() == null || (afternoon.getAfternoonSecondEnd() == null && i - afternoon.getAfternoonEnd() >= simpleRule.getWakeTime()))
                        count++;
        return count > 0;
    }


    private boolean isNotReadyForAfternoon(SchedulingHelp afternoonWorker, SimpleRule simpleRule, double i) {
        if (afternoonWorker.getWeekStatus() != 1 && afternoonWorker.getDayStatus() != 1) {
            if (afternoonWorker.getStatus() == 1)
                return true;
            if (afternoonWorker.getAfternoonEnd() == null &&
                    afternoonWorker.getMorningSecondEnd() == null &&
                    afternoonWorker.getMorningEnd() != null &&
                    (i - afternoonWorker.getMorningEnd() < simpleRule.getWakeTime()))
                return true;
            if (afternoonWorker.getAfternoonEnd() == null &&
                    afternoonWorker.getMorningSecondEnd() != null &&
                    (i - afternoonWorker.getMorningSecondEnd() < simpleRule.getWakeTime()))
                return true;
            return afternoonWorker.getAfternoonEnd() != null &&
                    (i - afternoonWorker.getAfternoonEnd() < simpleRule.getWakeTime());
        }
        return false;
    }


    private boolean haveManToWorkForAfternoon(List<SchedulingHelp> afternoonWorkers, SimpleRule simpleRule,
                                              double i) {
        int count = 0;
        for (SchedulingHelp afternoonWorker : afternoonWorkers) {
            if (afternoonWorker.getDayStatus() != 1 && afternoonWorker.getWeekStatus() != 1 && afternoonWorker.getStatus() != 1) {
                if (afternoonWorker.getAfternoonEnd() == null) {
                    if (afternoonWorker.getMorningSecondEnd() != null &&
                            (i - afternoonWorker.getMorningSecondEnd() >= simpleRule.getWakeTime()))
                        count++;
                    else if (afternoonWorker.getMorningSecondEnd() == null &&
                            afternoonWorker.getMorningEnd() != null &&
                            (i - afternoonWorker.getMorningEnd() >= simpleRule.getWakeTime()))
                        count++;
                    else if (afternoonWorker.getMorningSecondEnd() == null &&
                            afternoonWorker.getMorningEnd() == null)
                        count++;
                } else {
                    if (i - afternoonWorker.getAfternoonEnd() > simpleRule.getWakeTime())
                        count++;
                }

            }
        }
        return count > 0;
    }


    private boolean isEndAfternoon(double i, SchedulingHelp afternoonWorker) {
        if (afternoonWorker.getAfternoonEnd() == null) {
            if (afternoonWorker.getMorningEnd() == null)
                return true;
            else if (afternoonWorker.getMorningSecondEnd() != null && afternoonWorker.getMorningSecondEnd() <= i)
                return true;
            else
                return afternoonWorker.getMorningSecondEnd() == null && afternoonWorker.getMorningEnd() <= i;
        } else {
            return afternoonWorker.getAfternoonEnd() <= i;
        }
    }


    private boolean isNotReadyForMorning(SchedulingHelp morningWorker, SimpleRule simpleRule, double i) {
        return morningWorker.getStatus() == 1 ||
                (morningWorker.getMorningEnd() != null &&
                        (i - morningWorker.getMorningEnd() < simpleRule.getWakeTime()));
    }

    private boolean isEndMorning(double i, SchedulingHelp morningWorkers) {
        if (morningWorkers.getMorningEnd() == null)
            return true;
        else if (morningWorkers.getMorningSecondStart() == null && morningWorkers.getMorningEnd() <= i)
            return true;
        if (morningWorkers.getMorningSecondEnd() != null)
            return morningWorkers.getMorningSecondEnd() <= i;
        return false;
    }

    private boolean haveManContinueToAfternoon
            (List<SchedulingHelp> morningWorkers, List<SchedulingHelp> afternoonWorkers, double i, SimpleRule
                    simpleRule) {
        int count = 0;
        for (SchedulingHelp morning : morningWorkers)
            for (SchedulingHelp afternoon : afternoonWorkers)
                if (Objects.equals(morning.getUserId(), afternoon.getUserId()) &&
                        morning.getStatus() != 1 &&
                        morning.getWeekStatus() != 1 &&
                        morning.getDayStatus() != 1)
                    if (morning.getMorningEnd() == null || (morning.getMorningSecondEnd() == null && i - morning.getMorningEnd() >= simpleRule.getWakeTime()))
                        count++;
        return count > 0;
    }


    private boolean haveManToWorkForMorning(List<SchedulingHelp> morningWorkers, SimpleRule simpleRule, double i) {
        int count = 0;
        for (SchedulingHelp morningWorker : morningWorkers)
            if (morningWorker.getMorningEnd() == null &&
                    morningWorker.getWeekStatus() != 1 &&
                    morningWorker.getDayStatus() != 1)
                count++;
            else if (morningWorker.getMorningEnd() != null &&
                    (i - morningWorker.getMorningEnd() >= simpleRule.getWakeTime()) &&
                    morningWorker.getWeekStatus() != 1 &&
                    morningWorker.getDayStatus() != 1)
                count++;
        return count > 0;
    }

    private void addWorkTime(SchedulingHelp schedulingHelp, double workLong, SimpleRule simpleRule) {
        Double weekTime = schedulingHelp.getWeekTime();
        if (weekTime != null)
            weekTime += workLong;
        else
            weekTime = workLong;
        schedulingHelp.setWeekTime(weekTime);
        if (weekTime > schedulingHelp.getWeekTimePreference() - simpleRule.getSingleSmall())
            schedulingHelp.setWeekStatus(1);
        Double dayTime = schedulingHelp.getDayTime();
        if (dayTime != null)
            dayTime += workLong;
        else
            dayTime = workLong;
        schedulingHelp.setDayTime(dayTime);
        if (dayTime > schedulingHelp.getDayTimePreference() - simpleRule.getSingleSmall())
            schedulingHelp.setDayStatus(1);
    }

    private boolean isContinuesToWork(List<SchedulingHelp> beforeTimes, List<SchedulingHelp> afterTimes,
                                      int workMan) {
        if (afterTimes == null) return false;
        Long userId = beforeTimes.get(workMan).getUserId();
        for (SchedulingHelp afterTime : afterTimes)
            if (userId.equals(afterTime.getUserId()))
                return true;
        return false;
    }

    private boolean haveManToWorkForEarly(List<SchedulingHelp> morningWorkers, Info info) {
        int i = 0;
        List<SchedulingHelp> schedulingHelps = morningWorkers.stream()
                .filter(schedulingHelp -> info.getDoMorning().contains(schedulingHelp.getRoleId().toString()))
                .toList();
        for (SchedulingHelp morningWorker : schedulingHelps)
            if (morningWorker.getStatus() != 1 && morningWorker.getWeekStatus() != 1 && morningWorker.getDayStatus() != 1)
                i++;
        return i > 0;
    }

    void weekTime(SchedulingHelp schedulingHelp, StoreWorked storeWorked, SimpleRule simpleRule) {
        Double weekTime = schedulingHelp.getWeekTime();
        if (weekTime == null)
            weekTime = 0.00;
        if (storeWorked != null) {
            if (storeWorked.getMorningStart() != null)
                weekTime += (storeWorked.getMorningEnd() - storeWorked.getMorningStart());
            if (storeWorked.getMorningSecondStart() != null)
                weekTime += (storeWorked.getMorningSecondEnd() - storeWorked.getMorningSecondStart());
            if (storeWorked.getAfternoonStart() != null)
                weekTime += (storeWorked.getAfternoonEnd() - storeWorked.getAfternoonStart());
            if (storeWorked.getAfternoonSecondStart() != null)
                weekTime += (storeWorked.getAfternoonSecondEnd() - storeWorked.getAfternoonSecondStart());
            if (storeWorked.getEveningStart() != null)
                weekTime += (storeWorked.getEveningEnd() - storeWorked.getEveningStart());
            if (storeWorked.getEveningSecondStart() != null)
                weekTime += (storeWorked.getEveningSecondEnd() - storeWorked.getEveningSecondStart());
        }

        if (weekTime > schedulingHelp.getWeekTimePreference() - simpleRule.getSingleSmall())
            schedulingHelp.setWeekStatus(1);
        schedulingHelp.setWeekTime(weekTime);
    }


    private double scheduleWorkLong(List<SchedulingHelp> nowWorkers,
                                    List<SchedulingHelp> afterWorkers,
                                    int workMan,
                                    Double singleTop,
                                    Double singleSmall,
                                    double i,
                                    SimpleRule simpleRule,
                                    double surplusTimePlus,
                                    double afterTime) {
        nowWorkers.get(workMan).setStatus(1);
        double surplusTime = afterTime - i;
        if (nowWorkers.get(workMan).getWeekTime() != null)
            surplusTime = simpleRule.getLongWeek() - nowWorkers.get(workMan).getWeekTime();
        double workLong;
        if (isContinuesToWork(nowWorkers, afterWorkers, workMan)) {
            if (surplusTimePlus >= singleTop) {
                if (nowWorkers.get(workMan).getDayTime() != null) {
                    if (nowWorkers.get(workMan).getDayTimePreference() - nowWorkers.get(workMan).getDayTime() >= singleTop)
                        workLong = (int) (Math.random() * ((singleTop -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                    else
                        workLong = (int) (Math.random() * ((nowWorkers.get(workMan).getDayTimePreference() - nowWorkers.get(workMan).getDayTime() -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                } else {
                    if (nowWorkers.get(workMan).getDayTimePreference() >= singleTop)
                        workLong = (int) (Math.random() * ((singleTop -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                    else
                        workLong = (int) (Math.random() * ((nowWorkers.get(workMan).getDayTimePreference() -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                }
            } else {
                if (nowWorkers.get(workMan).getDayTime() != null) {
                    if (nowWorkers.get(workMan).getDayTimePreference() - nowWorkers.get(workMan).getDayTime() >= surplusTimePlus)
                        workLong = (int) (Math.random() * ((surplusTimePlus -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                    else
                        workLong = (int) (Math.random() * ((nowWorkers.get(workMan).getDayTimePreference() - nowWorkers.get(workMan).getDayTime() -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                } else {
                    if (nowWorkers.get(workMan).getDayTimePreference() >= surplusTimePlus)
                        workLong = (int) (Math.random() * ((surplusTimePlus -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                    else
                        workLong = (int) (Math.random() * ((nowWorkers.get(workMan).getDayTimePreference() -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                }
            }
        } else {
            if (nowWorkers.get(workMan).getDayTime() != null) {
                if (surplusTime >= afterTime - i) {
                    if (afterTime - i <= nowWorkers.get(workMan).getDayTimePreference() - nowWorkers.get(workMan).getDayTime())
                        workLong = (int) (Math.random() * ((afterTime - i -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                    else
                        workLong = (int) (Math.random() * ((nowWorkers.get(workMan).getDayTimePreference() - nowWorkers.get(workMan).getDayTime() -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                } else {
                    if (surplusTime <= nowWorkers.get(workMan).getDayTimePreference() - nowWorkers.get(workMan).getDayTime())
                        workLong = (int) (Math.random() * ((surplusTime -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                    else
                        workLong = (int) (Math.random() * ((nowWorkers.get(workMan).getDayTimePreference() - nowWorkers.get(workMan).getDayTime() -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                }
            } else {
                if (surplusTime >= afterTime - i) {
                    if (afterTime - i <= nowWorkers.get(workMan).getDayTimePreference())
                        workLong = (int) (Math.random() * ((afterTime - i -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                    else
                        workLong = (int) (Math.random() * ((nowWorkers.get(workMan).getDayTimePreference() -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                } else {
                    if (surplusTime <= nowWorkers.get(workMan).getDayTimePreference())
                        workLong = (int) (Math.random() * ((surplusTime -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                    else
                        workLong = (int) (Math.random() * ((nowWorkers.get(workMan).getDayTimePreference() -
                                singleSmall) * 2 + 1) +
                                singleSmall * 2) / 2.0;
                }
            }
        }
        return workLong;
    }

    private void scheduleMorning(List<SchedulingHelp> morningWorkers,
                                 List<SchedulingHelp> afternoonWorkers,
                                 int workMan,
                                 Double singleTop,
                                 Double singleSmall,
                                 double i,
                                 SimpleRule simpleRule) {
        double surplusTimePlus;
        if (i == 11.5)
            surplusTimePlus = simpleRule.getLunchTime() - i;
        else
            surplusTimePlus = simpleRule.getLunchEnd() - simpleRule.getLunchTime() - i;
        double workLong = scheduleWorkLong(morningWorkers, afternoonWorkers, workMan, singleTop, singleSmall, i, simpleRule, surplusTimePlus, SystemConstants.MID_TIME);
        if (morningWorkers.get(workMan).getMorningEnd() == null) {
            morningWorkers.get(workMan).setMorningStart(i);
            morningWorkers.get(workMan).setMorningEnd(i + workLong);
        } else {
            morningWorkers.get(workMan).setMorningSecondStart(i);
            morningWorkers.get(workMan).setMorningSecondEnd(i + workLong);
        }
        addWorkTime(morningWorkers.get(workMan), workLong, simpleRule);
    }

    private void scheduleAfternoon(List<SchedulingHelp> afternoonWorkers,
                                   List<SchedulingHelp> eveningWorkers,
                                   int workMan,
                                   Double singleTop,
                                   Double singleSmall,
                                   double i,
                                   SimpleRule simpleRule) {
        double surplusTimePlus = simpleRule.getDinnerEnd() - simpleRule.getDinnerTime() - i;
        double workLong = scheduleWorkLong(afternoonWorkers, eveningWorkers, workMan, singleTop, singleSmall, i, simpleRule, surplusTimePlus, SystemConstants.AFTERNOON_TIME);
        if (afternoonWorkers.get(workMan).getAfternoonEnd() == null) {
            afternoonWorkers.get(workMan).setAfternoonStart(i);
            afternoonWorkers.get(workMan).setAfternoonEnd(i + workLong);
        } else {
            afternoonWorkers.get(workMan).setAfternoonSecondStart(i);
            afternoonWorkers.get(workMan).setAfternoonSecondEnd(i + workLong);
        }
        addWorkTime(afternoonWorkers.get(workMan), workLong, simpleRule);
    }

    private void scheduleEvening(List<SchedulingHelp> eveningWorkers, int workMan, Double singleTop, Double singleSmall, double i, SimpleRule simpleRule, double closeTime) {
        double workLong = scheduleWorkLong(eveningWorkers, null, workMan, singleTop, singleSmall, i, simpleRule, 0.0, closeTime);
        if (eveningWorkers.get(workMan).getEveningEnd() == null) {
            eveningWorkers.get(workMan).setEveningStart(i);
            eveningWorkers.get(workMan).setEveningEnd(i + workLong);
        } else {
            eveningWorkers.get(workMan).setEveningSecondStart(i);
            eveningWorkers.get(workMan).setEveningSecondStart(i + workLong);
        }
        addWorkTime(eveningWorkers.get(workMan), workLong, simpleRule);
    }

    /**
     * 早上来打扫卫生生成的排班，
     * 其中排除了早上可以不来打扫卫生的员工
     *
     * @param morningWorkers   喜欢当日早上来上班的员工信息
     * @param afternoonWorkers 喜欢当日下午来上班的员工信息
     * @param singleTop        单次排班的最高时长
     * @param singleSmall      单次排班的最短时长
     * @param i                当前时间
     * @param simpleRule       固定规则
     * @param info             门店信息，用来读取门店的规则信息
     * @param workNum          随机的排班员工在当前的位号
     */
    private void dayStart(List<SchedulingHelp> morningWorkers,
                          List<SchedulingHelp> afternoonWorkers,
                          Double singleTop,
                          Double singleSmall,
                          double i,
                          SimpleRule simpleRule,
                          Info info,
                          int workNum,
                          Date date) {
        for (int j = 0; j < workNum; j++) {
            if (!haveManToWorkForEarly(morningWorkers, info)) {
                Needed needed = new Needed();
                needed.setTime(i);
                needed.setNeedMan(workNum - j);
                needed.setDate(date);
                neededMapper.insert(needed);
                infoNeededMapper.insert(new InfoNeeded(info.getId(), needed.getId()));
                break;
            }
            int workMan = (int) (Math.random() * morningWorkers.size());
            while (isNotReadyForMorning(morningWorkers.get(workMan), simpleRule, i) ||
                    morningWorkers.get(workMan).getWeekStatus() == 1 ||
                    morningWorkers.get(workMan).getDayStatus() == 1 ||
                    !info.getDoMorning().contains(morningWorkers.get(workMan).getRoleId().toString()))
                workMan = (int) (Math.random() * morningWorkers.size());
            scheduleMorning(morningWorkers, afternoonWorkers, workMan, singleTop, singleSmall, i, simpleRule);
        }
    }

    /**
     * 对早上进行排班，
     * 其中包含了可以连续做到下午的员工和不连续的员工
     *
     * @param morningWorkers   喜欢当日早上来上班的员工信息
     * @param afternoonWorkers 喜欢当日下午来上班的员工信息
     * @param singleTop        单次排班的最高时长
     * @param singleSmall      单次排班的最短时长
     * @param i                当前时间
     * @param simpleRule       固定规则
     * @param workNum          随机的排班员工在当前的位号
     */
    private void morningSchedule(List<SchedulingHelp> morningWorkers,
                                 List<SchedulingHelp> afternoonWorkers,
                                 Double singleTop,
                                 Double singleSmall,
                                 double i,
                                 SimpleRule simpleRule,
                                 int workNum,
                                 Date date,
                                 Info info) {
        for (int j = 0; j < workNum; j++) {
            if (!haveManToWorkForMorning(morningWorkers, simpleRule, i)) {
                Needed needed = new Needed();
                needed.setTime(i);
                needed.setNeedMan(workNum - j);
                needed.setDate(date);
                neededMapper.insert(needed);
                infoNeededMapper.insert(new InfoNeeded(info.getId(), needed.getId()));
                break;
            }
            int workMan = (int) (Math.random() * morningWorkers.size());
            while (isNotReadyForMorning(morningWorkers.get(workMan), simpleRule, i) ||
                    morningWorkers.get(workMan).getWeekStatus() == 1 ||
                    morningWorkers.get(workMan).getDayStatus() == 1)
                workMan = (int) (Math.random() * morningWorkers.size());
            scheduleMorning(morningWorkers, afternoonWorkers, workMan, singleTop, singleSmall, i, simpleRule);
        }
    }

    /**
     * 对早上进行排班，
     * 由于从10点半以后只能给下午也愿意上班的员工进行排班了，
     * 所以这里只有能在上午和下午一起上班的员工中进行选择
     *
     * @param morningWorkers   喜欢当日早上来上班的员工信息
     * @param afternoonWorkers 喜欢当日下午来上班的员工信息
     * @param singleTop        单次排班的最高时长
     * @param singleSmall      单次排班的最短时长
     * @param i                当前时间
     * @param simpleRule       固定规则
     * @param workNum          随机的排班员工在当前的位号
     */
    private void morningToAfternoonSchedule(List<SchedulingHelp> morningWorkers,
                                            List<SchedulingHelp> afternoonWorkers,
                                            Double singleTop,
                                            Double singleSmall,
                                            double i,
                                            SimpleRule simpleRule,
                                            int workNum,
                                            Date date,
                                            Info info) {
        for (int j = 0; j < workNum; j++) {
            if (!haveManContinueToAfternoon(morningWorkers, afternoonWorkers, i, simpleRule)) {
                Needed needed = new Needed();
                needed.setTime(i);
                needed.setNeedMan(workNum - j);
                needed.setDate(date);
                neededMapper.insert(needed);
                infoNeededMapper.insert(new InfoNeeded(info.getId(), needed.getId()));
                break;
            }

            int workMan = (int) (Math.random() * morningWorkers.size());
            while (isNotReadyForMorning(morningWorkers.get(workMan), simpleRule, i) ||
                    morningWorkers.get(workMan).getWeekStatus() == 1 ||
                    morningWorkers.get(workMan).getDayStatus() == 1 ||
                    !isContinuesToWork(morningWorkers, afternoonWorkers, workMan))
                workMan = (int) (Math.random() * morningWorkers.size());
            scheduleMorning(morningWorkers, afternoonWorkers, workMan, singleTop, singleSmall, i, simpleRule);
        }
    }

    private void afternoonSchedule
            (List<SchedulingHelp> afternoonWorkers, List<SchedulingHelp> eveningWorkers, Double singleTop, Double
                    singleSmall, double i, SimpleRule simpleRule, int workNum, Date date, Info info) {
        for (int j = 0; j < workNum; j++) {
            if (!haveManToWorkForAfternoon(afternoonWorkers, simpleRule, i)) {
                Needed needed = new Needed();
                needed.setTime(i);
                needed.setNeedMan(workNum - j);
                needed.setDate(date);
                neededMapper.insert(needed);
                infoNeededMapper.insert(new InfoNeeded(info.getId(), needed.getId()));
                break;
            }
            int workMan = (int) (Math.random() * afternoonWorkers.size());
            while (isNotReadyForAfternoon(afternoonWorkers.get(workMan), simpleRule, i) ||
                    afternoonWorkers.get(workMan).getWeekStatus() == 1 ||
                    afternoonWorkers.get(workMan).getDayStatus() == 1)
                workMan = (int) (Math.random() * afternoonWorkers.size());
            scheduleAfternoon(afternoonWorkers, eveningWorkers, workMan, singleTop, singleSmall, i, simpleRule);
        }
    }


    private void afternoonToEveningSchedule
            (List<SchedulingHelp> afternoonWorkers, List<SchedulingHelp> eveningWorkers, Double singleTop, Double
                    singleSmall, double i, SimpleRule simpleRule, int workNum, Date date, Info info) {
        for (int j = 0; j < workNum; j++) {
            if (!haveManContinueToEvening(afternoonWorkers, eveningWorkers, i, simpleRule)) {
                Needed needed = new Needed();
                needed.setTime(i);
                needed.setNeedMan(workNum - j);
                needed.setDate(date);
                neededMapper.insert(needed);
                infoNeededMapper.insert(new InfoNeeded(info.getId(), needed.getId()));
                break;
            }

            int workMan = (int) (Math.random() * afternoonWorkers.size());
            while (isNotReadyForAfternoon(afternoonWorkers.get(workMan), simpleRule, i) ||
                    afternoonWorkers.get(workMan).getWeekStatus() == 1 ||
                    afternoonWorkers.get(workMan).getDayStatus() == 1 ||
                    !isContinuesToWork(afternoonWorkers, eveningWorkers, workMan))
                workMan = (int) (Math.random() * afternoonWorkers.size());
            scheduleAfternoon(afternoonWorkers, eveningWorkers, workMan, singleTop, singleSmall, i, simpleRule);
        }
    }

    private void eveningSchedule(List<SchedulingHelp> eveningWorkers, Double singleTop, Double
            singleSmall, double i, SimpleRule simpleRule, int workNum, double closeTime, Date date, Info info) {
        for (int j = 0; j < workNum; j++) {
            if (!haveManToWorkForEvening(eveningWorkers, i, simpleRule)) {
                Needed needed = new Needed();
                needed.setTime(i);
                needed.setNeedMan(workNum - j);
                needed.setDate(date);
                neededMapper.insert(needed);
                infoNeededMapper.insert(new InfoNeeded(info.getId(), needed.getId()));
                break;
            }
            int workMan = (int) (Math.random() * eveningWorkers.size());
            while (isNotReadyForEvening(eveningWorkers.get(workMan), simpleRule, i) ||
                    eveningWorkers.get(workMan).getWeekStatus() == 1 ||
                    eveningWorkers.get(workMan).getDayStatus() == 1)
                workMan = (int) (Math.random() * eveningWorkers.size());
            scheduleEvening(eveningWorkers, workMan, singleTop, singleSmall, i, simpleRule, closeTime);
        }
    }


    private void scheduleForClean(List<SchedulingHelp> eveningWorkers, int leaveNum, double leaveTime, double endTime, Info info, SimpleRule simpleRule, Date date) {
        for (SchedulingHelp eveningWorker : eveningWorkers)
            eveningWorker.setStatus(0);
        for (int j = 0; j < leaveNum; j++) {
            if (!haveManToStayForClean(eveningWorkers, endTime, info, leaveTime, simpleRule)) {
                Needed needed = new Needed();
                needed.setTime(endTime - leaveTime);
                needed.setNeedMan(leaveNum - j);
                needed.setDate(date);
                neededMapper.insert(needed);
                infoNeededMapper.insert(new InfoNeeded(info.getId(), needed.getId()));
                break;
            }
            int workMan = (int) (Math.random() * eveningWorkers.size());
            while (!info.getDoLater().contains(eveningWorkers.get(workMan).getRoleId().toString()) ||
                    eveningWorkers.get(workMan).getWeekStatus() == 1 ||
                    eveningWorkers.get(workMan).getDayStatus() == 1 ||
                    cantTOClean(eveningWorkers.get(workMan), endTime, leaveTime, simpleRule))
                workMan = (int) (Math.random() * eveningWorkers.size());
            eveningWorkers.get(workMan).setStatus(1);
            if (eveningWorkers.get(workMan).getEveningEnd() == null) {
                eveningWorkers.get(workMan).setEveningStart(endTime - leaveTime);
                eveningWorkers.get(workMan).setEveningEnd(endTime);
            } else if (eveningWorkers.get(workMan).getEveningSecondEnd() == null) {
                if ((eveningWorkers.get(workMan).getEveningEnd() == (endTime - leaveTime))) {
                    if ((eveningWorkers.get(workMan).getEveningEnd() - eveningWorkers.get(workMan).getEveningStart() + leaveTime <= simpleRule.getSingleTop()) &&
                            (eveningWorkers.get(workMan).getDayTime() + leaveTime <= eveningWorkers.get(workMan).getDayTimePreference()) &&
                            (eveningWorkers.get(workMan).getWeekTime() + leaveTime <= eveningWorkers.get(workMan).getWeekTimePreference())) {
                        eveningWorkers.get(workMan).setEveningEnd(endTime);
                    }
                } else {
                    if ((eveningWorkers.get(workMan).getDayTime() + leaveTime <= eveningWorkers.get(workMan).getDayTimePreference()) &&
                            (eveningWorkers.get(workMan).getWeekTime() + leaveTime <= eveningWorkers.get(workMan).getWeekTimePreference())) {
                        eveningWorkers.get(workMan).setEveningSecondStart(endTime - leaveTime);
                        eveningWorkers.get(workMan).setEveningSecondEnd(endTime);
                    }
                }
            } else {
                if ((eveningWorkers.get(workMan).getEveningSecondEnd() == (endTime - leaveTime))) {
                    if ((eveningWorkers.get(workMan).getEveningSecondEnd() - eveningWorkers.get(workMan).getEveningStart() + leaveTime <= simpleRule.getSingleTop()) &&
                            (eveningWorkers.get(workMan).getDayTime() + leaveTime <= eveningWorkers.get(workMan).getDayTimePreference()) &&
                            (eveningWorkers.get(workMan).getWeekTime() + leaveTime <= eveningWorkers.get(workMan).getWeekTimePreference())) {
                        eveningWorkers.get(workMan).setEveningSecondEnd(endTime);
                    }
                }
            }
            addWorkTime(eveningWorkers.get(workMan), leaveTime, simpleRule);
        }
    }
}
