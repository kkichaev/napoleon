using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public partial class Incass
   {
	   [DataField("agent")]
	   public String agentRcvd = "";
   }

    public class ExchDoc : BaseDocument
    {
        public static readonly string OBJECT_NAME = "ExchDoc";

        public List<Item> items = new List<Item>();

        public class Item : DataObject
        {
            [Reference("ManagerPrice,Price", "id", typeof(Price))]
            public Price price = null;

            public string id = "";
            public double qty = 0;
            public DateTime date = DateTime.Now;

            public string Name { get { return price == null ? "Товар с кодом <" + id + ">" : price.name; } }
            public double Qty { get { return qty; } }
            public DateTime Date { get { return date; } }
        }
    }
}
