package com.studyhub.util;


import java.time.LocalDate;

/*
    *SM-2间隔重复算法
    * 输入：打分 0-5，输出：新的ED / 间隔 / 下次复习时间
 */
public class SM2Calculator {

    private static final double MIN_EF = 1.3; //难度下限（太难的兜底）
    private static final double MAX_EF = 2.5; // 难度上限
    private static final int MAX_INTERVAL = 180;

    /*
     *根据打分调整EF，
     * 答得好（5分） EF微涨；答得差EF降；下线1.3兜底
     */
    public static double updateEF(double ef,int quality){

        // 公式： EF' = EF + (0.1 - (5-q)*(0.08+(5-q)*0.02))
        double newEf = ef +(0.1 -(5 - quality) * (0.08 + (5 - quality) * 0.02));
        if(newEf <MIN_EF) newEf = MIN_EF;
        if(newEf > MAX_EF) newEf = MAX_EF;
        return newEf;
    }

    /*
    * 计算下一复习间隔
    * 答得差（quality<3）；重置为1天 重新学
    * 第一次复习在（reviewCount==0） 1天
    * 答得好： 间隔 = 上次间隔 ×EF （翻倍式增长）
    * */
    public static int nextInterval(int intervalDays,double ef,int quality,int reviewCount){
        if(quality < 3){
            return 1;  // 忘了+重置
        }
        if (reviewCount == 0){
            return 1;  //第一次复习后 +1 天
        }
        return Math.min((int) Math.round(intervalDays * ef),MAX_INTERVAL);
    }


    /*
    * 下次复习日期 = 今天 +间隔
    * */
    public static LocalDate nextReviewDate(LocalDate today,int intervalDays){
        return today.plusDays(intervalDays);
    }

}