package com.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.test.constants.SystemConstants;
import com.test.domain.entity.*;
import com.test.mapper.*;
import com.test.utils.BeanCopyUtils;
import com.test.utils.FindDateStatusUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.test.constants.SystemConstants.*;
import static com.test.utils.FindDateStatusUtil.getWeekDate;


@SpringBootTest
public class SchedulingTest {

    @Autowired
    AiDataMapper aiDataMapper;
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
    NeededMapper neededMapper;
    @Autowired
    InfoNeededMapper infoNeededMapper;


    @Test
    void testBug() throws ParseException {
        test("2023-03-20");
        test("2023-03-21");
        test("2023-03-22");
        test("2023-03-23");
        test("2023-03-24");
        test("2023-03-25");
        test("2023-03-26");
    }


    @Test
    void test(String end) throws ParseException {
        Long storeId = 1L;
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

//        for (SchedulingHelp schedulingHelp : result) {
//            Days days = BeanCopyUtils.copyBean(schedulingHelp, Days.class);
//            days.setDate(endDate);
//            daysMapper.insert(days);
//            userSchedulingMapper.insert(new UserScheduling(schedulingHelp.getUserId(), days.getId()));
//        }
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
