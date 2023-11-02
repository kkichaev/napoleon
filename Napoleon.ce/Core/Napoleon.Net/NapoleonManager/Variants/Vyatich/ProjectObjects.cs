using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
    public partial class VisitInfo
    {
        public int stock = 0;
    }

    public partial class ReturnItem
    {
        static DateTime MinDate = new DateTime(1980, 2, 1);

        public DateTime production = DateTime.MinValue;
        public DateTime expired = DateTime.MinValue;

        public string ProdDate { get { return production.CompareTo(MinDate) < 0 ? "-" : production.ToString("dd/MM/yy"); } }
        public string ExpDate { get { return expired.ToString("dd/MM/yy"); } }
    }

    public class Planograms : DataObject
    {
        public static string OBJECT_NAME = "Planograms";

        [KeyField]
        public string id = Guid.NewGuid().ToString().Replace("-", "");

        public string name = "";
        public string path = "";
        public byte[] photo = null;
    }


    public class PlanogramEdit : BaseDocument
    {
        public static string OBJECT_NAME = "PlanogramDoc";

        public string planogram = "";
        public string planogramTitle = "";
    }

    public class PriceMovie : DataObject
    {
        public static string OBJECT_NAME = "PriceMovie";

        [KeyField]
        public string id = "";

        public string url = "";
    }

    public class InvFrg : BaseDocument
    {
        public static readonly string OBJECT_NAME = "InvFrg";

        [ItemType(typeof(InvFrgItem))]
        public List<InvFrgItem> items = new List<InvFrgItem>();
    }

    public class InvFrgItem : DataObject
    {
        public string id = string.Empty;
        public string barcode = string.Empty;
        public string number = string.Empty;
        public string name = string.Empty;
        public int exist = 0;
        public int isnew = 0;


        public string Item { get { return name; } }
        public string Barcode { get { return barcode; } }
        public string Number { get { return number; } }
    }
}
