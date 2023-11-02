using GRSoft.NapoleonManager.Properties;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   partial class ScriptDocuments
   {
      static void InitDocuments()
      {
         docs = new List<ScriptDocument>();
         docs.Add(new OrderDoc());
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
         docs.Add(new MerchBeginDoc());
         docs.Add(new MerchEndDoc());
         docs.Add(new TaskBeginDoc());
         docs.Add(new TaskEndDoc());
         docs.Add(new ActGSDoc());
         docs.Add(new FacingDoc());
      }
   }

   internal class MerchBeginDoc : ScriptDocument
   {
      internal MerchBeginDoc()
         : base("MerchBegin", "Мерч начало", Resources.merch)
      {
      }
   }

   internal class MerchEndDoc : ScriptDocument
   {
      internal MerchEndDoc()
         : base("MerchEnd", "Мерч конец", Resources.merch)
      {
      }
   }

   internal class TaskBeginDoc : ScriptDocument
   {
      internal TaskBeginDoc()
         : base("TaskBegin", "Задачи начало", Resources.taskdoc)
      { 
      }
   }

   internal class TaskEndDoc : ScriptDocument
   {
      internal TaskEndDoc()
         : base("TaskEnd", "Задачи конец", Resources.taskdoc)
      {
      }
   }

   internal class ActGSDoc : ScriptDocument
   {
      internal ActGSDoc()
         :base("ActGSDoc", "Акция золотая полка", Resources.actiongs_doc)
      {
      }
   }

   internal class FacingDoc : ScriptDocument
   {
      internal FacingDoc()
         : base("Facing", "Фейсинг", Resources.facing_doc)
      { 
      }
   }

}
