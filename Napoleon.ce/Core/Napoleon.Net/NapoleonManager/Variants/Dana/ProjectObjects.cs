using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
    public class OrgCluster : DataObject
    {
        public static readonly string OBJECT_NAME = "OrgCluster";

        [KeyField]
        public string id = "";

        public string name = "";

        public override string ToString()
        {
            return name;
        }
    }

    public class ActionCount : DataObject
    {
        public static readonly string OBJECT_NAME = "UsedActionCount";
        [KeyField]
        public string id = "";
        public double used = 0;
    }

    public class DanaAction : DataObject
    {
        public static readonly string OBJECT_NAME = "Action";

        public static readonly int GIFT_TYPE = 0;
        public static readonly int DISCOUNT_TYPE = 1;

        [KeyField]
        public string id = "";

        public string name = "";
        public string descr = "";
        public DateTime start = DateTime.Now;
        public DateTime finish = DateTime.Now;

        [Reference("Org,CommonOrgs", "orgId")]
        public Org org = null;

        [Reference("ManagerPrice,Price", "itemId", typeof(Price))]
        public Price item = null;

        [Reference("OrgCluster", "clusterId")]
        public OrgCluster cluster = null;

        public string orgId = "";
        public string clusterId = "";

        public string itemId = "";

        public int creatorDivision = 0;
        public double qty = 0;
        public int type = 0;

        public double discount = 0;

        public int hidden = 0;

        public class Item : DataObject
        {
            [Reference("ManagerPrice,Price", "id", typeof(Price))]
            public Price item = null;
            public string id = "";
            public double qty = 0;

            public double Qty { get { return qty; } set { qty = value; } }
            public string Name { get { return (item == null) ? "" : item.Name; } }
        }

        public List<Item> items = new List<Item>();

        public string Name { get { return name; } }
        public DateTime From { get { return start; } }
        public DateTime Till { get { return finish; } }

        public override string ToString()
        {
            return name;
        }
    }

    public class AgentActions : DataObject
    {
        public static readonly string OBJECT_NAME = "AgentActions";

        public string userid = "";

        public string actionid = "";

        [Reference("Action", "actionid")]
        public DanaAction action = null;
    }

    public class GoodsProjects : DataObject, IComparable<GoodsProjects>
    {
        public static readonly string OBJECT_NAME = "GoodsProjects";

        [KeyField]
        public string id = "";
        public string idOrg = "";

        [DataField("base")]
        public string _base = "";
        public string name = "";

        [Reference("Bases1c", "base")]
        public Bases1c base1c;

        public override string ToString() { return name; }

        public string Name { get { return name; } }
        public string Org { get { return idOrg; } set { idOrg = value; } }
        public String Base { get { return base1c == null ? "" : base1c.name; } }

        public int CompareTo(GoodsProjects other)
        {
           return name.CompareTo(other.name);
        }
    }

    public class Bases1c : DataObject
    {
        public static readonly string OBJECT_NAME = "Bases1c";

        [KeyField]
        public string id = "";
        public string name = "";
    }

    public class AgentProjects : DataObject
    {
        public static readonly string OBJECT_NAME = "AgentProjects";

        [KeyField]
        public string id = "";

        [Reference("GoodsProjects", "id")]
        public GoodsProjects project = null;

        public string userid = "";
    }

   public class Firm : DataObject, IComparable<Firm>
   {
      public static readonly string OBJECT_NAME = "Firm";

      [KeyField]
      public string id = "";

      public string name = "";

      public string Name { get { return name; } }
      public string Id { get { return id; } }

      public int CompareTo(Firm other)
      {
         return name.CompareTo(other.name);
      }
   }
}
