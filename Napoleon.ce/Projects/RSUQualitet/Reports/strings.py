# -*- coding: cp1251 -*-
import sys;

reload(sys);
sys.setdefaultencoding("cp1251")

class Resources:
  __slots__ = "data, data_ru,locale"
  
  def __init__(self, locale):
    self.locale = locale
    self.data = {
      "report" : "Report",
      "unknown_agent" : "Unknown agent",
      "mileage_report": "Mileage report",
      "report_period_from": "Report period from",
      "performer" : "Worker",
      "distance" : "Distance, km",
      "sun" : "Sunday",
      "mon" : "Monday",
      "tue" : "Tuesday",
      "wed" : "Wednesday",
      "thu" : "Thursday",
      "fri" : "Friday",
      "sut" : "Saturday",
      "result_data" : "Result",
    }
    
    self.data_ru = {
      "report" : "Отчет",
      "unknown_agent" : "Агент с кодом",
      "mileage_report": "Отчет по пробегу",
      "report_period_from": "Отчетный период с",
      "performer" : "Исполнитель",
      "distance" : "Расстояние, км",
      "sun" : "Воскресенье",
      "mon" : "Понедельник",
      "tue" : "Вторник",
      "wed" : "Среда",
      "thu" : "Четверг",
      "fri" : "Пятница",
      "sut" : "Суббота",
      "result_data" : "Итоговые данные",
    }
    
  def getString(self, res):
    ret = ""
    string_base = self.data
    
    if self.locale == "ru_RU":
      string_base = self.data_ru
    
    if res in string_base:
      ret = string_base[res]
  
    print "ret", ret
    return ret