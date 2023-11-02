using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
    class ContractMatrix : DataObject, IComparable<ContractMatrix>
    {
        public static readonly string OBJECT_NAME = "ContractMatrix";

        public string name = "";

        public class Item : DataObject
        {
            public string id = "";

            [Reference("ManagerPrice,Price", "id", typeof(Price))]
            public Price price = null;
        }

        public override string ToString()
        {
            return name;
        }

        public int CompareTo(ContractMatrix other)
        {
            return name.CompareTo(other.name);
        }
    }

    class OrgContracts : DataObject
    {
        public static readonly string OBJECT_NAME = "OrgContracts";

        public string name = "";
        public string id = "";

        public string Name { get { return name; } }
    }

    public class MerchDoc : BaseDocument
    {
        public static readonly string OBJECT_NAME = "Merch";

        public class Item : DataObject
        {
            public string id = "";
            public double qty = 0;
            public DateTime bestBefore = DateTime.Now;

            [Reference("ManagerPrice,Price", "id", typeof(Price))]
            public Price item = null;

            public double Qty { get { return qty; } }
            public DateTime BestBefore { get { return bestBefore; } }
            public string Name { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }

        }

        public List<Item> items = new List<Item>();
    }
}
