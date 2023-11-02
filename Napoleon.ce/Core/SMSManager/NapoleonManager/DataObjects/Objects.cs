/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объекты данных
 * 
 * ert   21/04/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using GRSoft.Network;
using System.Collections;
using System.Globalization;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager.DataObjects
{
   delegate void EmptyParamHandler();

   //Исключение когда пришел объет не того типа что ожидали (23.09.2010 kki)
   class EIncompatibilityObject : Exception { }

   //Исключение когда данные неправильны или пришли не все
   class EDataCorrupted : Exception { }

   public class Locality : DataObject
   {
      public static readonly string OBJECT_NAME = "Locality";

      [KeyField]
      public Int32 id = 0;
      public string name = "";
   }

   public class Agent : DataObject
   {
      public static readonly string OBJECT_NAME = "Agents";
      public static readonly string MANAGER_ID = "\\x1#$%f1";

      [KeyField]
      public string id = String.Empty;
      public string name = String.Empty;
      public string login = String.Empty;
      public string password = String.Empty;
      public string phone = String.Empty;

      public string Name { get { return name; } }

      public override string ToString()
      {
         return name;
      }

      public bool Equals(Agent agent)
      {
         return this.id == agent.id;
      }
   }

   public class Division : DataObject
   {
      public class DivisionAgent : DataObject
      {
         [Reference("Agents", "id")]
         public Agent agent = null;

         public string AgentName
         {
            get { return (agent == null) ? "?" : agent.name; }
         }
      }

      [KeyField]
      public int id = 0;

      public string name = "";
      public string description = "";

      [Reference("Agents", "cheif")]
      public Agent cheif = null;

      [ItemType(typeof(DivisionAgent))]
      public List<DivisionAgent> agents = new List<DivisionAgent>();

      public int parent = 0;
   }

   public class SchoolEntity : DataObject
   {
      public static readonly string OBJECT_NAME = "SchoolEntity";
      public static readonly int SHOOL_PARENT = 0;

      [KeyField]
      public Int32 id = 0;
      public int locality = 0;
      public int parent = 0;
      public String number = String.Empty;
      public String address = String.Empty;

      [ItemType(typeof(Contact))]
      public List<Contact> contacts = null;
   }

   public class Contact : DataObject
   {
      public String name = String.Empty;
      public String phone = String.Empty;
      public String remark = String.Empty;
   }

   public class Student : DataObject
   {
      public static readonly string OBJECT_NAME = "Student";

      [KeyField]
      public Int32 id = 0;
      public int group = 0;
      public string name = String.Empty;
   }

   public class Parent : DataObject
   {
      public static readonly string OBJECT_NAME = "Parent";

      [KeyField]
      public Int32 id = 0;
      public string name = String.Empty;

      [ItemType(typeof(Phone))]
      public List<Phone> phones = null; 
   }

   public class Phone : DataObject
   {
      public string phone = String.Empty;
      public string remark = String.Empty;
   }

   public class Dogovor : DataObject
   {
      public static readonly string OBJECT_NAME = "Dogovor";
      public static string[] Types = { "Тип 1", "Тип 2", "Тип 3" };

      [KeyField]
      public Int32 id = 0;
      public string number = String.Empty;
      public DateTime start = DateTime.MinValue;
      public DateTime end = DateTime.MinValue;
      public int type = 0;

      [ItemType(typeof(DogStudent))]
      public List<DogStudent> students = null;

      [ItemType(typeof(DogParent))]
      public List<DogParent> parents = null;

      internal static object TypeToString(int p)
      {
         if (p < 0 || p >= Types.Length)
            return "Unknown type";

         return Types[p];
      }
   }

   public class DogStudent : DataObject
   {
      public int id = 0;
   }

   public class DogParent : DataObject
   {
      public int id = 0;
   }

   public class SchoolSubject : DataObject
   {
      public static readonly string OBJECT_NAME = "SchoolSubject";

      [KeyField]
      public Int32 id = 0;
      public string name = String.Empty;
      public string secondName = String.Empty;
   }

   public class Schedule : DataObject
   {
      public static readonly string OBJECT_NAME = "Schedule";

      [KeyField]
      public Int32 id = 0;
      public int group = 0;
      [ItemType(typeof(ScheduleItem))]
      public List<ScheduleItem> subjects = null;
      public int day = 0;
   }

   public class ScheduleItem : DataObject
   {
      public int id = 0;
      public int period = 0;

      public string PeriodToStr()
      {
         switch (period)
         {
            case 1: return "н";
            case 2: return "ч";
            default: return String.Empty;
         }
      }
   }

   public class SchoolFolder : DataObject
   {
      public static readonly string OBJECT_NAME = "SchoolFolder";

      [KeyField]
      public Int32 id = 0;
      public string name = String.Empty;
      public string userid = string.Empty;

      [ItemType(typeof(SchoolFolderItem))]
      public List<SchoolFolderItem> items = null;
   }

   public class SchoolFolderItem : DataObject
   {
      public int id = 0;
   }

   public class Lesson : DataObject
   {
      public static readonly string OBJECT_NAME = "Lesson";

      public DateTime date = DateTime.MinValue;
      public int classID = 0;
      public int subjectID = 0;
      public int order = 0;
      public string task = string.Empty;
      public string userid = string.Empty;

      [ItemType(typeof(LessonItem))]
      public List<LessonItem> items = null;
   }

   public class LessonItem : DataObject
   { 
      public int studentID = 0;
      public int mark = 0;
      public string task = string.Empty;
      public string behavior = string.Empty;
      public string remark = string.Empty;
   }

   public class Annonce : DataObject
   {
       public static readonly string OBJECT_NAME = "Annonce";

       public DateTime date = DateTime.MinValue;
       public int id = 0;
       public string message = string.Empty;
       public string userid = string.Empty;
   }
}
