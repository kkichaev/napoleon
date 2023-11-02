using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.Ads
{
   internal class DsCertificate
      : StrKeyDataSet<Certificate>
   {
      public DsCertificate(bool add)
         : base(Certificate.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsProtocol
      : StrKeyDataSet<Protocol>
   {
      public DsProtocol(bool add)
         : base(Protocol.OBJECT_NAME, add)
      {
      }
   }

   public class DsOrderRcvEx : DataSet<Int32, OrderRcvEx>
   {
      public DsOrderRcvEx(bool add)
         : base(OrderRcv.OBJECT_NAME, add)
      {
      }
   }

   public class DsUserOrderEx : DataSet<int, UserOrderEx>
   {
      public DsUserOrderEx(bool add)
         : base(UserOrder.OBJECT_NAME, add)
      {
      }
   }

   public class Certificate : DataObject
   {
      [Reference("Agents", "userid")]
      public Brigade brigade;
      public static readonly string OBJECT_NAME = "Certificate";

      [KeyField]
      public string number = string.Empty;
      public DateTime assigned = DateTime.MinValue;
      public int writeof = 0;

      public string Number { get { return number; } }
      public string AssignedStr { get { return assigned.ToString("dd.MM.yyyy"); } }

      public override bool Equals(object obj)
      {
         return number.Equals(((Certificate)obj).number);
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }

      public Brigade Brigade { get { return brigade; }}
   }

   public class Protocol : DataObject
   {
      [Reference("Agents", "userid")]
      public Brigade brigade;
      public static readonly string OBJECT_NAME = "Protocol";

      [KeyField]
      public string number = string.Empty;
      public DateTime assigned = DateTime.MinValue;
      public int writeof = 0;

      public string Number { get { return number; } }
      public string AssignedStr { get { return assigned.ToString("dd.MM.yyyy"); } }

      public override bool Equals(object obj)
      {
         return number.Equals(((Protocol)obj).number);
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }

   public class OrderRcvEx : OrderRcv
   { 
      public string counter = string.Empty;
      public string numctr = string.Empty;
      public string protocol = string.Empty;
      public string certificate = string.Empty;
      public double datactr = 0.0;

      public string Counter { get { return counter; } }
      public string NumCtr { get { return numctr; } }
      public string Protocol { get { return protocol; } }
      public string Certificate { get { return certificate; } }
      public double Datactr { get { return datactr; } }
   }

   public class UserOrderEx : UserOrder
   {
      public int worktype = 0;
      public string contact = string.Empty;
      public string phone = string.Empty;
      public double latitude = 0;
      public double longitude = 0;
      public string counter = string.Empty;
      public string numctr = string.Empty;
      public string protocol = string.Empty;
      public string certificate = string.Empty;
      public double datactr = 0.0;

      [Reference("WorkType", "worktype")]
      public WorkType workTypeRef = null;

      public WorkType WorkType { get { return workTypeRef; } }
      public string Contact { get { return contact; } }
      public string Phone { get { return phone; } }
      public string Counter { get { return counter; } }
      public string NumCtr { get { return numctr; } }
      public string Protocol { get { return protocol; } }
      public string Certificate { get { return certificate; } }
      public double Datactr { get { return datactr; } }
   }
}
