package com.nlf.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.nlf.calendar.util.LunarUtil;
import com.nlf.calendar.util.SolarUtil;

/**
 * 农历日期
 *
 * @author 6tail
 *
 */
public class Lunar{
  /** 农历年 */
  private int year;
  /** 农历月，闰月为负，即闰2月=-2 */
  private int month;
  /** 农历日 */
  private int day;
  /** 对应阳历 */
  private Solar solar;
  /** 相对于基准日的偏移天数 */
  private int dayOffset;
  /** 日对应的天干下标，0-10 */
  private int dayGanIndex;
  /** 日对应的地支下标，0-12 */
  private int dayZhiIndex;

  /**
   * 默认使用当前日期初始化
   */
  public Lunar(){
    this(new Date());
  }

  /**
   * 通过农历年月日初始化
   *
   * @param year 年（农历）
   * @param month 月（农历），1到12，闰月为负，即闰2月=-2
   * @param day 日（农历），1到31
   */
  public Lunar(int year,int month,int day){
    this.year = year;
    this.month = month;
    this.day = day;
    this.dayOffset = LunarUtil.computeAddDays(year,month,day);
    int addDays = (dayOffset + LunarUtil.BASE_DAY_GANZHI_INDEX)%60;
    dayGanIndex = addDays%10;
    dayZhiIndex = addDays%12;
    this.solar = toSolar();
  }

  /**
   * 通过阳历日期初始化
   * @param date 阳历日期
   */
  public Lunar(Date date){
    solar = new Solar(date);
    int y = solar.getYear();
    int m = solar.getMonth();
    int d = solar.getDay();
    int startYear,startMonth,startDay;
    int lunarYear,lunarMonth,lunarDay;
    if(y<2000){
      startYear = SolarUtil.BASE_YEAR;
      startMonth = SolarUtil.BASE_MONTH;
      startDay = SolarUtil.BASE_DAY;
      lunarYear = LunarUtil.BASE_YEAR;
      lunarMonth = LunarUtil.BASE_MONTH;
      lunarDay = LunarUtil.BASE_DAY;
    }else{
      startYear = SolarUtil.BASE_YEAR+99;
      startMonth = 1;
      startDay = 1;
      lunarYear = LunarUtil.BASE_YEAR+99;
      lunarMonth = 11;
      lunarDay = 25;
    }
    int diff = 0;
    for(int i=startYear;i<y;i++){
      diff += 365;
      if(SolarUtil.isLeapYear(i)){
        diff += 1;
      }
    }
    for(int i=startMonth;i<m;i++){
      diff += SolarUtil.getDaysOfMonth(y,i);
    }
    diff += d-startDay;
    lunarDay += diff;
    int lastDate = LunarUtil.getDaysOfMonth(lunarYear,lunarMonth);
    while(lunarDay>lastDate){
      lunarDay -= lastDate;
      lunarMonth = LunarUtil.nextMonth(lunarYear,lunarMonth);
      if(lunarMonth==1){
        lunarYear++;
      }
      lastDate = LunarUtil.getDaysOfMonth(lunarYear,lunarMonth);
    }
    year = lunarYear;
    month = lunarMonth;
    day = lunarDay;
    dayOffset = LunarUtil.computeAddDays(year,month,day);
    int addDays = (dayOffset + LunarUtil.BASE_DAY_GANZHI_INDEX)%60;
    dayGanIndex = addDays%10;
    dayZhiIndex = addDays%12;
  }

  /**
   * 通过指定阳历日期获取农历
   *
   * @param date 阳历日期
   * @return 农历
   */
  public static Lunar fromDate(Date date){
    return new Lunar(date);
  }

  /**
   * 通过指定农历年月日获取农历
   *
   * @param year 年（农历）
   * @param month 月（农历），1到12，闰月为负，即闰2月=-2
   * @param day 日（农历），1到31
   * @return 农历
   */
  public static Lunar fromYmd(int year,int month,int day){
    return new Lunar(year,month,day);
  }

  /**
   * 获取年份的天干
   *
   * @return 天干，如辛
   */
  public String getGan(){
    return LunarUtil.GAN[(year-4)%10+1];
  }

  /**
   * 获取年份的地支
   *
   * @return 地支，如亥
   */
  public String getZhi(){
    return LunarUtil.ZHI[(year-4)%12+1];
  }

  /**
   * 获取生肖
   *
   * @return 生肖，如虎
   */
  public String getShengxiao(){
    return LunarUtil.SHENGXIAO[(year-4)%12+1];
  }

  /**
   * 获取中文的年
   *
   * @return 中文年，如二零零一
   */
  public String getYearInChinese(){
    String y = (year+"");
    StringBuilder s = new StringBuilder();
    for(int i=0,j=y.length();i<j;i++){
      s.append(LunarUtil.NUMBER[y.charAt(i)-'0']);
    }
    return s.toString();
  }

  /**
   * 获取中文的月
   *
   * @return 中文月，如正月
   */
  public String getMonthInChinese(){
    if(month>0){
      return LunarUtil.MONTH[month];
    }else{
      return "闰"+LunarUtil.MONTH[-month];
    }
  }

  /**
   * 获取季节
   * @return 农历季节
   */
  public String getSeason(){
    return LunarUtil.SEASON[Math.abs(month)];
  }

  /**
   * 获取中文日
   *
   * @return 中文日，如初一
   */
  public String getDayInChinese(){
    return LunarUtil.DAY[day];
  }

  /**
   * 获取节
   *
   * @return 节
   */
  public String getJie(){
    String s = "";
    int solarYear = solar.getYear();
    int solarMonth = solar.getMonth();
    int solarDay = solar.getDay();
    int index = 0;
    int ry = solarYear-SolarUtil.BASE_YEAR+1;
    while(ry>=LunarUtil.JIE_YEAR[solarMonth-1][index]){
      index++;
    }
    int term = LunarUtil.JIE_MAP[solarMonth-1][4*index+ry%4];
    if(ry==121&&solarMonth==4){
      term = 5;
    }
    if(ry==132&&solarMonth==4){
      term = 5;
    }
    if(ry==194&&solarMonth==6){
      term = 6;
    }
    if(solarDay==term){
      s = LunarUtil.JIE[solarMonth-1];
    }
    return s;
  }

  /**
   * 获取气
   *
   * @return 气
   */
  public String getQi(){
    String s = "";
    int solarYear = solar.getYear();
    int solarMonth = solar.getMonth();
    int solarDay = solar.getDay();
    int index = 0;
    int ry = solarYear-SolarUtil.BASE_YEAR+1;
    while(ry>=LunarUtil.QI_YEAR[solarMonth-1][index]){
      index++;
    }
    int term = LunarUtil.QI_MAP[solarMonth-1][4*index+ry%4];
    if(ry==171&&solarMonth==3){
      term = 21;
    }
    if(ry==181&&solarMonth==5){
      term = 21;
    }
    if(solarDay==term){
      s = LunarUtil.QI[solarMonth-1];
    }
    return s;
  }

  /**
   * 获取宿
   *
   * @return 宿
   */
  public String getXiu(){
    return LunarUtil.XIU[day-1][Math.abs(month)-1];
  }

  /**
   * 获取政
   *
   * @return 政
   */
  public String getZheng(){
    return LunarUtil.ZHENG.get(getXiu());
  }

  /**
   * 获取动物
   * @return 动物
   */
  public String getAnimal(){
    return LunarUtil.ANIMAL.get(getXiu());
  }

  /**
   * 获取宫
   * @return 宫
   */
  public String getGong(){
    return LunarUtil.GONG.get(getXiu());
  }

  /**
   * 获取兽
   * @return 兽
   */
  public String getShou(){
    return LunarUtil.SHOU.get(getGong());
  }

  /**
   * 获取节日，有可能一天会有多个节日
   *
   * @return 节日列表，如春节
   */
  public List<String> getFestivals(){
    List<String> l = new ArrayList<String>();
    String f = LunarUtil.FESTIVAL.get(month+"-"+day);
    if(null!=f){
      l.add(f);
    }
    return l;
  }

  /**
   * 获取非正式的节日，有可能一天会有多个节日
   *
   * @return 非正式的节日列表，如中元节
   */
  public List<String> getOtherFestivals(){
    List<String> l = new ArrayList<String>();
    List<String> fs = LunarUtil.OTHER_FESTIVAL.get(month+"-"+day);
    if(null!=fs){
      l.addAll(fs);
    }
    return l;
  }

  /**
   * 转换为阳历日期
   *
   * @return 阳历日期
   */
  private Solar toSolar(){
    Calendar c = Calendar.getInstance();
    c.set(SolarUtil.BASE_YEAR,SolarUtil.BASE_MONTH-1,SolarUtil.BASE_DAY);
    c.add(Calendar.DATE,dayOffset);
    return new Solar(c);
  }

  /**
   * 获取干支纪月
   * <p>月天干口诀：甲己丙寅首，乙庚戊寅头。丙辛从庚寅，丁壬壬寅求，戊癸甲寅居，周而复始流。</p>
   * <p>月地支：正月起寅</p>
   *
   * @return 干支纪月，如己卯
   */
  public String getMonthInGanZhi(){
    int m = Math.abs(month)-1;
    int yearGanIndex = (year-4)%10;
    int offset = (yearGanIndex%5+1)*2;
    String monthGan = LunarUtil.GAN[(m+offset)%10+1];
    String monthZhi = LunarUtil.ZHI[(m+LunarUtil.BASE_MONTH_ZHI_INDEX)%12+1];
    return monthGan+monthZhi;
  }

  /**
   * 获取干支纪日
   *
   * @return 干支纪日，如己卯
   */
  public String getDayInGanZhi(){
    return LunarUtil.GAN[dayGanIndex+1]+LunarUtil.ZHI[dayZhiIndex+1];
  }

  /**
   * 获取彭祖百忌天干
   * @return 彭祖百忌天干
   */
  public String getPengZuGan(){
    return LunarUtil.PENGZU_GAN[dayGanIndex+1];
  }

  /**
   * 获取彭祖百忌地支
   * @return 彭祖百忌地支
   */
  public String getPengZuZhi(){
    return LunarUtil.PENGZU_ZHI[dayZhiIndex+1];
  }

  /**
   * 获取喜神方位
   * @return 喜神方位，如艮
   */
  public String getPositionXi(){
    return LunarUtil.POSITION_XI[dayGanIndex+1];
  }

  /**
   * 获取喜神方位描述
   * @return 喜神方位描述，如东北
   */
  public String getPositionXiDesc(){
    return LunarUtil.POSITION_DESC.get(getPositionXi());
  }

  /**
   * 获取阳贵神方位
   * @return 阳贵神方位，如艮
   */
  public String getPositionYangGui(){
    return LunarUtil.POSITION_YANG_GUI[dayGanIndex+1];
  }

  /**
   * 获取阳贵神方位描述
   * @return 阳贵神方位描述，如东北
   */
  public String getPositionYangGuiDesc(){
    return LunarUtil.POSITION_DESC.get(getPositionYangGui());
  }

  /**
   * 获取阴贵神方位
   * @return 阴贵神方位，如艮
   */
  public String getPositionYinGui(){
    return LunarUtil.POSITION_YIN_GUI[dayGanIndex+1];
  }

  /**
   * 获取阴贵神方位描述
   * @return 阴贵神方位描述，如东北
   */
  public String getPositionYinGuiDesc(){
    return LunarUtil.POSITION_DESC.get(getPositionYinGui());
  }

  /**
   * 获取福神方位
   * @return 福神方位，如艮
   */
  public String getPositionFu(){
    return LunarUtil.POSITION_FU[dayGanIndex+1];
  }

  /**
   * 获取福神方位描述
   * @return 福神方位描述，如东北
   */
  public String getPositionFuDesc(){
    return LunarUtil.POSITION_DESC.get(getPositionFu());
  }

  /**
   * 获取财神方位
   * @return 财神方位，如艮
   */
  public String getPositionCai(){
    return LunarUtil.POSITION_CAI[dayGanIndex+1];
  }

  /**
   * 获取财神方位描述
   * @return 财神方位描述，如东北
   */
  public String getPositionCaiDesc(){
    return LunarUtil.POSITION_DESC.get(getPositionCai());
  }

  /**
   * 获取冲
   * @return 冲，如申
   */
  public String getChong(){
    return LunarUtil.CHONG.get(LunarUtil.ZHI[dayZhiIndex+1]);
  }

  /**
   * 获取无情之克的冲天干
   * @return 无情之克的冲天干，如甲
   */
  public String getChongGan(){
    String chong = LunarUtil.GAN[dayGanIndex+1];
    return LunarUtil.CHONG_GAN.get(chong);
  }

  /**
   * 获取有情之克的冲天干
   * @return 有情之克的冲天干，如甲
   */
  public String getChongGanTie(){
    String chong = LunarUtil.GAN[dayGanIndex+1];
    return LunarUtil.CHONG_GAN_TIE.get(chong);
  }

  /**
   * 获取冲生肖
   * @return 冲生肖，如猴
   */
  public String getChongShengXiao(){
    String chong = getChong();
    for(int i=0,j=LunarUtil.ZHI.length;i<j;i++){
      if(LunarUtil.ZHI[i].equals(chong)){
        return LunarUtil.SHENGXIAO[i];
      }
    }
    return "";
  }

  /**
   * 获取冲描述
   * @return 冲描述，如(壬申)猴
   */
  public String getChongDesc(){
    return "("+getChongGan()+getChong()+")"+getChongShengXiao();
  }

  /**
   * 获取刹
   * @return 刹，如北
   */
  public String getSha(){
    return LunarUtil.SHA.get(LunarUtil.ZHI[dayZhiIndex+1]);
  }

  public String toFullString(){
    StringBuilder s = new StringBuilder();
    s.append(toString());
    s.append(" ");
    s.append(getGan());
    s.append(getZhi());
    s.append("(");
    s.append(getShengxiao());
    s.append(")");
    s.append("年");
    s.append(getMonthInGanZhi());
    s.append("月");
    s.append(getDayInGanZhi());
    s.append("日");
    for(String f:getFestivals()){
      s.append(" (");
      s.append(f);
      s.append(")");
    }
    for(String f:getOtherFestivals()){
      s.append(" (");
      s.append(f);
      s.append(")");
    }
    String jq = getJie()+getQi();
    if(jq.length()>0){
      s.append(" [");
      s.append(jq);
      s.append("]");
    }
    s.append(" ");
    s.append(getGong());
    s.append("方");
    s.append(getShou());
    s.append(" ");
    s.append(getXiu());
    s.append(getZheng());
    s.append(getAnimal());
    s.append(" 彭祖百忌[");
    s.append(getPengZuGan());
    s.append(" ");
    s.append(getPengZuZhi());
    s.append("]");
    s.append(" 喜神方位[");
    s.append(getPositionXi());
    s.append("](");
    s.append(getPositionXiDesc());
    s.append(")");
    s.append(" 阳贵神方位[");
    s.append(getPositionYangGui());
    s.append("](");
    s.append(getPositionYangGuiDesc());
    s.append(")");
    s.append(" 阴贵神方位[");
    s.append(getPositionYinGui());
    s.append("](");
    s.append(getPositionYinGuiDesc());
    s.append(")");
    s.append(" 福神方位[");
    s.append(getPositionFu());
    s.append("](");
    s.append(getPositionFuDesc());
    s.append(")");
    s.append(" 财神方位[");
    s.append(getPositionCai());
    s.append("](");
    s.append(getPositionCaiDesc());
    s.append(")");
    s.append(" 冲[");
    s.append(getChongDesc());
    s.append("] 刹[");
    s.append(getSha());
    s.append("]");
    return s.toString();
  }

  @Override
  public String toString(){
    return getYearInChinese()+"年"+getMonthInChinese()+"月"+getDayInChinese();
  }

  /**
   * 获取年份
   *
   * @return 如2015
   */
  public int getYear(){
    return year;
  }

  /**
   * 获取月份
   *
   * @return 1到12，负数为闰月
   */
  public int getMonth(){
    return month;
  }

  /**
   * 获取日期
   *
   * @return 日期
   */
  public int getDay(){
    return day;
  }

  public Solar getSolar(){
    return solar;
  }
}
