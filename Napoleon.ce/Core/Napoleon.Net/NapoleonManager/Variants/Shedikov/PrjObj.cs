using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class PrjObj
   {
   }

   partial class Incass
   {
      public string unitCode = "";
   }

   public partial class Org
   {
      [ItemType(typeof(OrgUnit))]
      public List<OrgUnit> units = new List<OrgUnit>();
   }

   public class OrgUnit : DataObject 
   {
      public string id = string.Empty;
      public string name = string.Empty;
   }

   public partial class OrgRemnants : BaseDocument
   { 
   	public double ourgrkqty = 0;
   	public double ourvtrqty = 0;
   	public double ourcmnqty = 0;
   	public double cncgrkqty = 0;
   	public double cncvtrqty = 0;
   	public double cnccmnqty = 0;

      [ItemType(typeof(ConcurentItem))]
      public List<ConcurentItem> cncs = new List<ConcurentItem>();
   }

   public class ConcurentItem : DataObject
   {
      public string id = string.Empty;
      public string name = string.Empty;
      public double grk = 0.0;
      public double vtr = 0.0;
      public double cmn = 0.0;

      public string Name { get { return name; } }
      public double Grk { get { return grk; } }
      public double Vtr { get { return vtr; } }
      public double Cmn { get { return cmn; } }
   }
}
