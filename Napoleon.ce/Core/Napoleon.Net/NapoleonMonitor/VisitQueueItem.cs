using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{

   public class VisitType : ObjType
   {
      public string typeName = null;

      public VisitType(TObjType val)
         : base(val)
      {
      }

      public VisitType(string objName)
      {
         if (!FromString(objName))
         {
            val = TObjType.NotVisit;
            typeName = objName;
         }
      }

      public override string ToString()
      {
         if (typeName != null)
            return typeName;
         return base.ToString();
      }

      public virtual bool IsStopType { get { return typeName != null; } }
   }

   public class VisitQueueItem
   {
      public DateTime startTime;
      public Org org;
      public double latitude;
      public double longitude;
      public VisitType objType;

      public DateTime endTime = DateTime.MinValue;
      public string address;

      public string factAddress;
      public bool outOfRange;

      public double sum;

      public string number;
      public string color = "green";
      public VisitQueueItem(DateTime dtVisit, Org org, double latitude, double longitude, VisitType objType)
      {
         this.startTime = dtVisit;
         this.org = org;
         this.latitude = latitude;
         this.longitude = longitude;
         this.objType = objType;
      }

      public VisitQueueItem(BaseDocument doc, VisitType objType)
      {
         this.startTime = doc.created;
         this.org = doc.org;
         this.latitude = doc.latitude;
         this.longitude = doc.longitude;
         this.objType = objType;
      }

      public bool HavePosition { get { return (latitude != 0 || longitude != 0); } }

      public string OrgName 
      { 
         get
         { 
            return org != null ?  org.Name :  ""; 
         }
      }

      public string StopTime
      {
         get
         {
            if (endTime == DateTime.MinValue)
               return "";
            TimeSpan ts = endTime.Subtract(startTime);
            int min = ts.Minutes;
            return (min < 60) ? min + " мин." : (min / 60) + " ч" + (min % 60) + " мин.";
         }
      }
   }

   public class CmpVisitQueueItem : IComparer<VisitQueueItem>
   {

      #region IComparer<VisitQueueItem> Members

      public int Compare(VisitQueueItem x, VisitQueueItem y)
      {
         return x.startTime.CompareTo(y.startTime);
      }

      #endregion
   }
}
