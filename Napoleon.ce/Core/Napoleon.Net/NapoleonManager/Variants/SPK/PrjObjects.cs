using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class Layout : BaseDocument
   {
      public static string OBJECT_NAME = "Layout";

      [ItemType(typeof(LayotItem))]
      public List<LayotItem> items = new List<LayotItem>();

      public class LayotItem : DataObject
      {
         public string grid = string.Empty;
         public string grname = string.Empty;
         public string itid = string.Empty;
         public string itname = string.Empty;
         public int grpos = 0;
         public double qty = 0.0;
         public string cause = string.Empty;
         public string remark = string.Empty;

         public void SetFrom(LayotItem x)
         {
            grid = x.grid;
            grname = x.grname;
            grpos = x.grpos;
            itid = x.itid;
            itname = x.itname;
            qty = x.qty;
         }
      }

      public bool IsEmpty
      {
         get
         {
            foreach (LayotItem i in items)
               if (i.qty != 0)
                  return false;

            return true;
         }
      }
   }

   public class LayoutApprove : Layout
   {
      public static string OBJECT_NAME_APPROVE = "ApproveLayout";
      public int aprType = 0;
      public string aprRemark = "";
      public DateTime modify = DateTime.Now;
   }

   public class LayoutActionCause : DataObject
   {
      public static readonly string OBJECT_NAME = "LayoutActionCause";

      public static readonly int APPROVE = 0;
      public static readonly int REJECT = 1;
      public static readonly string ApproveTitle = "утвердить";
      public static readonly string RejectTitle = "отколнить";

      public int action = 0;
      public string name;
   }

   public class LayoutFailureCause : DataObject
   {
      public static readonly string OBJECT_NAME = "LayoutFailureCause";
      public string name;
   }

   public class ApproveLog : DataObject
   {
      public static readonly string OBJECT_NAME = "LayoutApproveLog";

      public string userid = "";
      public int aprType = 0;
      public string aprRemark = "";
      public DateTime aprDate = DateTime.Now;
      public string id = "";
      public DateTime created = DateTime.Now;
      public DateTime committed = DateTime.Now;
      public string agentid = string.Empty;
   }

   public class OrgDisablePhoto : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgDisablePhoto";

      [KeyField]
      public string id = "";
      public string userid = "";
   }

   public partial class Question
   {
      public int type2 = 0;
   }
}
