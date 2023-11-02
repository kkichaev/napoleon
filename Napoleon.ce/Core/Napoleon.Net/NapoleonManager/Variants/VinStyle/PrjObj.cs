using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class PrjObj
   {
   }

   class InvFrgSt1 : Visit
   {
      public static readonly new string OBJECT_NAME = "InvFrgSt1";
   }

   class InvFrgSt2 : Visit
   {
      public static readonly new string OBJECT_NAME = "InvFrgSt2";
   }

   class InvFrgSt3 : Visit
   {
      public static readonly new string OBJECT_NAME = "InvFrgSt3";
   }

   class RejectCause : DataObject
   {
      public static readonly String OBJECT_NAME = "RejectCause";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public int rem = 0;

      public string Text { get { return text; } set { text = value; } }
   }

   class Distrib : BaseDocument
   {
      public static readonly String OBJECT_NAME = "OrgDistrib";

      public partial class DistribRemark : DataObject
      {
         public string remark = string.Empty;

         [Reference("ManagerPrice,Price", "id", typeof(Price))]
         public Price item = null;
         public string id = string.Empty;
      }

      [ItemType(typeof(DistribRemark))]
      public List<DistribRemark> items = null;
   }

   class DMPType : DataObject
   {
      public static readonly String OBJECT_NAME = "DMPType";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public int rem = 0;

      public string Text { get { return text; } set { text = value; } }
   }

   class DMP : BaseDocument
   {
      public static readonly String OBJECT_NAME = "DMP";
   }

   public partial class Visit : VisitInfo
   {
      public partial class VisitItem : DataObject 
      {
         public string dmpId = string.Empty;
         public string key = string.Empty;
      }
   }

   public class DistrCheck : DataObject
   {
      public static readonly String OBJECT_NAME = "DistrCheck";

      public string agentid = string.Empty;
      public DateTime doccreated = DateTime.MinValue;
      public DateTime created = DateTime.MinValue;
      public string id = string.Empty;
      public string dmpid = string.Empty;
      public int photos = 0;
      public string key = string.Empty;
   }

}
