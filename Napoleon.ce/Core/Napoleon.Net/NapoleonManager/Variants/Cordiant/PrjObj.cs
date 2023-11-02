using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class CPlan : DataObject
   {
      public static readonly String OBJECT_NAME = "CPlan";

      public DateTime date = DateTime.MinValue;
      public string userid;

      public double summer = 0.0;
      public double keySummer = 0.0;
      public double d17_18Summer = 0.0;
      public double winter = 0.0;
      public double keyWinter = 0.0;
      public double d17_18Winter = 0.0;
      public double lgsh = 0.0;
   }

   public class CMonitoring : BaseDocument
   {
      public static readonly String OBJECT_NAME = "CMonitoring";

      public partial class Item : DataObject
      {
         [Reference("ManagerPrice,Price", "id", typeof(Price))]
         public Price item = null;
         public string id = string.Empty;

         public double cost = 0.0;
      }

      [ItemType(typeof(Item))]
      public List<Item> items = null;
   }

   public class OrgRegion : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgRegion";

      [KeyField]
      public string id = "";
      public string name = "";
   }

   public class City : DataObject
   {
      public static readonly string OBJECT_NAME = "City";

      [KeyField]
      public string id = "";
      public string name = "";
   }

   public class TypePTT : DataObject
   {
      public static readonly string OBJECT_NAME = "TypePTT";

      [KeyField]
      public string id = "";
      public string name = "";
   }

   public class SpecPTT : DataObject
   {
      public static readonly string OBJECT_NAME = "SpecPTT";

      [KeyField]
      public string id = "";
      public string name = "";
      public string descr = "";
   }

   public class StaffPosition : DataObject
   {
      public static readonly string OBJECT_NAME = "StaffPosition";

      [KeyField]
      public string id = "";
      public string name = "";
   }

   public partial class Org : DataObject
   {
      public string regionID = "";
      public string cityID = "";
      public string nameFakt = "";
      public string phone = "";
      public string web = "";
      public string typepttID = "";
      public string specpttID = "";
      public double avgSell = 0.0;
      public double cordiantPart = 0.0;
      public double faceAll = 0.0;
      public double faceCoordiant = 0.0;
      public string nameYur = "";

      [ItemType(typeof(OrgContact))]
      public List<OrgContact> contacts = new List<OrgContact>();
   }

   public class OrgContact : DataObject
   {
      public string name = string.Empty;
      public string phone = string.Empty;
      public string staffPositionID = "";
   }

    public class Brand : DataObject
    {
        public static readonly string OBJECT_NAME = "Brand";

        [KeyField]
        public string id = "";
        public string name = "";
    }

    public class AgentOrgs : DataObject
    {
        public static readonly string OBJECT_NAME = "AgentOrgs";

        public string id = "";
        public string userid = "";
    }

    public partial class Price
    {
        public static readonly string WR_OBJECT = "PriceToWrite";

        public int width;
        public int wall; // высота
        public int diameter;

        public string brand;
        public string subbrand;
        public string autoType;

        public int season;
        public int studded; // шипованная
        public int keySKU;

        public double cost1;
        public double cost2;
        public double cost3;

        public int docFilter;

        public string model;
    }

    public class TimeSheet : DataObject
    {
        public static readonly string OBJECT_NAME = "TimeSheet";

        public string userid = "";

        [Reference("Agents", "userid")]
        public Agent agent = null;

        public DateTime start = DateTime.Now;

        public int notWorkCount = 0;

        public int day1 = 0;
        public int day2 = 0;
        public int day3 = 0;
        public int day4 = 0;
        public int day5 = 0;
        public int day6 = 0;
        public int day7 = 0;
        public int day8 = 0;
        public int day9 = 0;
        public int day10 = 0;
        public int day11 = 0;
        public int day12 = 0;
        public int day13 = 0;
        public int day14 = 0;
        public int day15 = 0;
        public int day16 = 0;
        public int day17 = 0;
        public int day18 = 0;
        public int day19 = 0;
        public int day20 = 0;
        public int day21 = 0;
        public int day22 = 0;
        public int day23 = 0;
        public int day24 = 0;
        public int day25 = 0;
        public int day26 = 0;
        public int day27 = 0;
        public int day28 = 0;
        public int day29 = 0;
        public int day30 = 0;
        public int day31 = 0;

    }
}
