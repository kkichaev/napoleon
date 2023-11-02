using System;
using System.Globalization;
using GRSoft.NapoleonManager.Utils;
namespace GRSoft.NapoleonManager
{
   public partial class OrderDetailRepresentation : ODRComapartor
   {
      //Дата создания заявки
      protected DateTime dateCreated;
      //Дата исполнения заявки
      protected DateTime dateExec;
      //Дата передачи
      protected DateTime sended;
      protected Org nOrg;
      //Организация
      protected string org = "";
      //Сумма заявки
      protected double sum;
      //Сумма инкасации
      protected double isum;
      //Тип документа
      protected ObjType doctype;
      //Адрес организации
      protected string orgAddr = "";
      //Полный объект
      protected GRSoft.Network.DataObject dataObject;
      //Один день
      protected bool oneDay;
      //Заметки
      protected string notes = "";

      protected int qty;

#if Tyapkin
      private const string ONE_DAY_DATE_MAKS = "HH:mm:ss";
      private const string PERIOD_DAY_DATE_MASK = "dd.MM.yy HH:mm:ss";
#else
      private const string ONE_DAY_DATE_MAKS = "HH:mm";
      private const string PERIOD_DAY_DATE_MASK = "dd.MM.yy HH:mm";
#endif

      public OrderDetailRepresentation(BaseDocument doc, ObjType doctype, bool oneDay) :
         this(doc.created, doctype, doc.date, doc.sended, doc.Org, doc.Sum(), 0, doc.Qty, doc, oneDay, doc.remark)
      {

      }

      public OrderDetailRepresentation(DateTime created, ObjType doctype,
         DateTime date, DateTime sended, Org org, double sum, double isum, int qty, 
         GRSoft.Network.DataObject dataObject, bool oneDay)
      {
         this.nOrg = org;

         this.dateCreated = created;
         this.dateExec = date;
         this.sended = sended;
         this.sum = sum;
         this.isum = isum;
         this.doctype = doctype;
         this.dataObject = dataObject;
         this.oneDay = oneDay;
         this.qty = qty;

#if Agama
         if (org != null)
         {
            int uc = -1;
            Order o = dataObject as Order;
            if (o != null) uc = o.unitCode;
            else
            {
               Visit v = dataObject as Visit;
               if (v != null) uc = v.unitCode;
            }

            this.org = org.Name;
            this.orgAddr = (org.address == null) ? "" : org.address;

            if (uc != -1 && org.units != null)
            {
               foreach (Org.UnitItem item in org.units)
               {
                  if (item.id == uc)
                  {
                     this.orgAddr = item.name;
                     this.org += " " + item.name;
                     break;
                  }
               }
            }
         }
#elif DELIVERY_ADDRESS
         Order o = dataObject as Order;

         if (o != null)
         {
            this.orgAddr = o.OrgAddr;
            this.org = Config.GetConfig().isFullOrgName ?
               String.Format("{0} ({1})", org.name, o.OrgAddr) :
               org.name;
         }
         else
         {
            this.org = org.Name;
            this.orgAddr = (org.address == null) ? "" : org.address;
         }
#else
         if (org != null)
         {
            this.org = org.Name;
            this.orgAddr = (org.Address == null) ? "" : org.Address;
         }
#endif
      }

      public OrderDetailRepresentation(DateTime created, ObjType doctype,
         DateTime date, DateTime sended, Org org, double sum, double isum, int qty,
         GRSoft.Network.DataObject dataObject, bool oneDay, string notes)
         : this(created, doctype, date, sended, org, sum, isum, qty,
         dataObject, oneDay)
      {
         this.notes = notes;
      }

      public Org NOrg { get { return nOrg; } }

      public DateTime DateCreatedDT { get { return dateCreated; } }
      public DateTime DateSendedDT { get { return sended; } }
      public String DateCreated 
      { 
         get 
         {
            return dateCreated == DateTime.MinValue || dateCreated == new DateTime(1601, 1, 1) ?
               string.Empty : dateCreated.ToString(DateFormat);
         } 
      }

      public String DateExec 
      { 
         get 
         {
            return dateExec == DateTime.MinValue || dateExec == new DateTime(1601, 1, 1) ?
               string.Empty : dateExec.ToString("dd.MM.yy");
         }
      }

      public String Sended
      {
         get
         {
            return sended == DateTime.MinValue || sended == new DateTime(1601, 1, 1) ?
               string.Empty : sended.ToString(DateFormat);
         }
      }

      public int Qty { get { return qty; } }

      public string Org { get { return org; } }
      public string Sum { get { return sum == 0.0 ? string.Empty : sum.ToString("C", Config.GetCultureInfo()); } }
      public string ISum { get { return isum == 0.0 ? string.Empty : isum.ToString("C", Config.GetCultureInfo()); } }

      public ObjType Doctype { get { return doctype; } }
      public string OrgAddr { get { return orgAddr; } }
      public GRSoft.Network.DataObject StoreObject { get { return dataObject; } }
      private string DateFormat { get { return oneDay ? ONE_DAY_DATE_MAKS : PERIOD_DAY_DATE_MASK; } }

      public double DblSum { get { return sum; } }

      [Compare]
      public static ORDCompareCondition CC = new ORDCompareCondition();

      public string Notes { get { return notes; } }

      public double Weight
      {
         get
         {
            Order o = dataObject as Order;
            return o == null ? 0 : o.Weight;
         } 
      }
   }

   public class ODRComapartor : CmpByField<OrderDetailRepresentation>
   {
      public override int CompareTo(OrderDetailRepresentation other)
      {
         const int LESS = -1;
         const int GREATER = 1;
         if (((OrderDetailRepresentation)this).Doctype.Equals(ObjType.TObjType.NotVisit) &&
            !other.Doctype.Equals(ObjType.TObjType.NotVisit))
            return GREATER;
         else if (!((OrderDetailRepresentation)this).Doctype.Equals(ObjType.TObjType.NotVisit) &&
            other.Doctype.Equals(ObjType.TObjType.NotVisit))
            return LESS;
         else
         {
            ORDCompareCondition cc = (ORDCompareCondition)GetCompareCondition();
            
            if (cc == null)
               throw new NotImplementedException();

            if (cc.Fields.Length == 1)
               return base.CompareTo(other);
            else
            {
               int result = 0;

               for (int i = 0; i < cc.Fields.Length; i++)
               {
                  result = Comparator.CompareItems(this, other, cc.Fields[i], cc.IsAscending);
                  if (result != 0)
                     return result;
               }

               return result;
            }
         }
      }
   }

   public class ORDCompareCondition : CompareCondition
   {
      private string[] fields;

      public void SetCompareCondition(string[] fields, bool isAscending)
      {
         this.fields = fields;
         this.isAscending = isAscending;
         this.fieldName = fields[0];
      }

      public string[] Fields { get { return fields; } }
   }
}