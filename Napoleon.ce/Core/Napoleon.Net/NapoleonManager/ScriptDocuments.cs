using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   partial class ScriptDocuments
   {
      static List<ScriptDocument> docs = null;

      public static ScriptDocument[] Documents
      {
         get
         {
            if (docs == null)
               InitDocuments();

            ScriptDocument[] ret = new ScriptDocument[docs.Count];
            docs.CopyTo(ret);
            return ret;
         }
      }

      public static String GetName(String docType)
      {
         if( docs == null )
            InitDocuments();

         foreach (ScriptDocument sd in docs)
         {
            if (sd.type == docType)
               return sd.name;
         }
         return "";
      }
   }

   internal class ScriptDocument
   {
      public string type;
      public string name;
      public Image icon;
      public string condParam;

      protected ScriptDocument(String t, String n, Image i)
         : this(t, n, i, "")
      {
      }

      protected ScriptDocument(String t, String n, Image i, string a)
      {
         type = t;
         name = n;
         icon = i;
         condParam = a;
      }

      public override string ToString()
      {
         return String.Format("{0} {1}", name, condParam);
      }
   }

   internal class OrderDoc : ScriptDocument
   {
      internal OrderDoc()
         : base("Order", "Заявка", Resources.order_doc)
      { 
      }

      internal OrderDoc(string objName)
         : base(objName, "Заявка", Resources.order_doc)
      {
      }
   }

   internal class RemnantsDoc : ScriptDocument
   {
      internal RemnantsDoc()
         : base("OrgRemnants", "Остатки", Resources.remnants_doc)
      {
      }
   }

   internal class VisitDoc : ScriptDocument
   {
      internal VisitDoc()
         : base("Visit", "Посещение", Resources.visit_doc)
      {
      }
   }

   internal class ReturnDoc : ScriptDocument
   {
      internal ReturnDoc()
         : base("Returns", "Возвраты", Resources.return_doc)
      {
      }
      internal ReturnDoc(string name)
         : base(name, "Возвраты", Resources.return_doc)
      {
      }
   }

   internal class IncassDoc : ScriptDocument
   {
      internal IncassDoc()
         : base("Incass", "Инкассация", Resources.incass_doc)
      {
      }
   }

   internal class MonitoringDoc : ScriptDocument
   {
      internal MonitoringDoc()
         : base("Monitoring", "Мониторинг", Resources.monitor_doc)
      { 
      }
   }

   internal class SalesDoc : ScriptDocument
   {
      internal SalesDoc()
         : base("Sales", "Продажи", Resources.sales_doc)
      {
      }
   }

   internal class QuestionDoc : ScriptDocument
   {
      internal QuestionDoc(String id)
         : base("Answer", "Анкеты", Resources.quest_doc, id)
      {
      }
   }

   internal class WSOrder : ScriptDocument
   {
      internal WSOrder()
         : base("OrderW", "Заявка(сети)", Resources.wsorder_doc)
      {
      }
   }

   internal class DefectDoc : ScriptDocument
   {
      internal DefectDoc()
         : base("Defect", "Брак", Resources.defects_doc)
      {
      }
   }

   internal class CommonAuditDoc : ScriptDocument
   {
      internal CommonAuditDoc()
         : base("CommonAudit", "Общий аудит", Resources.defects_doc)
      {
      }
   }

   internal class PromoAuditDoc : ScriptDocument
   {
      internal PromoAuditDoc()
         : base("PromoAudit", "Аудит акций", Resources.defects_doc)
      {
      }
   }

   internal class DistributionDoc : ScriptDocument
   {
      internal DistributionDoc()
         : base("DistrDoc", "Дистриб.", Resources.distrib_doc)
      {
      }
   }

   internal class VandSelDoc : ScriptDocument
   {
      internal VandSelDoc()
         : base("VandSell", "Продажа", Resources.order_doc)
      { 
      }
   }

   internal class VandAuditDoc : ScriptDocument
   {
      internal VandAuditDoc()
         : base("Audit", "Ревизия", Resources.audit_doc)
      {
      }
   }

   internal class VandReloadDoc : ScriptDocument
   {
      internal VandReloadDoc()
         : base("VandReload", "Перезагрузка", Resources.reload_doc)
      {
      }
   }

   internal class SmartTaskStartDoc : ScriptDocument
   {
      internal SmartTaskStartDoc()
         : base("SmartTaskStart", "Задачи просмотр", Resources.reload_doc)
      { 
      }
   }

   internal class SmartTaskEndDoc : ScriptDocument
   {
      internal SmartTaskEndDoc()
         : base("SmartTaskEnd", "Задачи редактирование", Resources.reload_doc)
      {
      }
   }

   internal class ScanLocationDoc : ScriptDocument
   {
      internal ScanLocationDoc()
         : base("ScanLocationDoc", "Запрос координат", GRSoft.NapoleonManager.Properties.Resources.ic_add_location)
      {
      }
   }
}
