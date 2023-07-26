package com.test.utils;

import com.test.domain.entity.AiData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AiDataUtils {

    private AiDataUtils(){}

    public static AiData getWeekData(Date date) {
        AiData aiData = new AiData();
        aiData.setDate(date);
        aiData.setEightFirst(0.00);
        aiData.setEightLast(((int) (Math.random() * 400)) / 100.0);
        aiData.setNineFirst(((int) (Math.random() * 300) + 300) / 100.0);
        setSimpleData(aiData);
        aiData.setTwentyFirst(((int) (Math.random() * 500) + 100) / 100.0);
        aiData.setTwentyLast(((int) (Math.random() * 400)) / 100.0);
        aiData.setTwentyOneFirst(0.00);
        aiData.setTwentyOneLast(0.00);
        return aiData;
    }

    public static AiData getWeekendData(Date date) {
        AiData aiData = new AiData();
        aiData.setDate(date);
        aiData.setEightFirst(0.00);
        aiData.setEightLast(0.00);
        aiData.setNineFirst(((int) (Math.random() * 400)) / 100.0);
        setSimpleData(aiData);
        aiData.setTwentyFirst(((int) (Math.random() * 700) + 100) / 100.0);
        aiData.setTwentyLast(((int) (Math.random() * 700) + 100) / 100.0);
        aiData.setTwentyOneFirst(((int) (Math.random() * 500) + 100) / 100.0);
        aiData.setTwentyOneLast(((int) (Math.random() * 400)) / 100.0);
        return aiData;
    }

    private static void setSimpleData(AiData aiData) {
        aiData.setNineLast(((int) (Math.random() * 200) + 600) / 100.0);
        aiData.setTenFirst(((int) (Math.random() * 800) + 800) / 100.0);
        aiData.setTenLast(((int) (Math.random() * 800) + 1300) / 100.0);
        aiData.setElevenFirst(((int) (Math.random() * 1200) + 1500) / 100.0);
        aiData.setElevenLast(((int) (Math.random() * 1500) + 1500) / 100.0);
        aiData.setTwelveFirst(((int) (Math.random() * 1500) + 1500) / 100.0);
        aiData.setTwelveLast(((int) (Math.random() * 1000) + 2000) / 100.0);
        aiData.setThirteenFirst(((int) (Math.random() * 1000) + 2000) / 100.0);
        aiData.setThirteenLast(((int) (Math.random() * 1500) + 1000) / 100.0);
        aiData.setFourteenFirst(((int) (Math.random() * 1500) + 1500) / 100.0);
        aiData.setFourteenLast(((int) (Math.random() * 1500) + 1500) / 100.0);
        aiData.setFifteenFirst(((int) (Math.random() * 700) + 1500) / 100.0);
        aiData.setFifteenLast(((int) (Math.random() * 900) + 1400) / 100.0);
        aiData.setSixteenFirst(((int) (Math.random() * 1500) + 1000) / 100.0);
        aiData.setSixteenLast(((int) (Math.random() * 1200) + 800) / 100.0);
        aiData.setSeventeenFirst(((int) (Math.random() * 700) + 800) / 100.0);
        aiData.setSeventeenLast(((int) (Math.random() * 1000) + 500) / 100.0);
        aiData.setEighteenFirst(((int) (Math.random() * 700) + 500) / 100.0);
        aiData.setEighteenLast(((int) (Math.random() * 700) + 500) / 100.0);
        aiData.setNineteenFirst(((int) (Math.random() * 600) + 200) / 100.0);
        aiData.setNineteenLast(((int) (Math.random() * 600) + 200) / 100.0);
    }
}
