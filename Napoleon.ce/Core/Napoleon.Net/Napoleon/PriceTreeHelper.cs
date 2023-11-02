using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Data;

namespace Napoleon
{
    public class PriceTreeHelper
    {
        //public FolderNode CreateTree()
        //{
        //    FolderNode result = new FolderNode((Folder)null);

        //    int lvl = -1;
        //    FolderNode parent = null;
        //    FolderNode prevNode = null;

        //    Dictionary<int, List<Price>> priceMap = CollectPrice();

        //    foreach (Folder mFolder in Update.GetStoredList<Folder>(Folder.OBJECT_NAME))
        //    {
        //        try
        //        {
        //            FolderNode node = new FolderNode(mFolder);

        //            if (lvl == -1)
        //            {
        //                result = node;
        //            }
        //            else if (lvl == mFolder.level)
        //            {
        //                ((FolderNode)parent).Items.Add(node);
        //                node.Parent = parent;
        //            }
        //            else if (lvl < mFolder.level)
        //            {
        //                parent = prevNode;
        //                ((FolderNode)parent).Items.Add(node);
        //                node.Parent = parent;
        //            }
        //            else if (lvl > mFolder.level)
        //            {
        //                FolderNode leftNode = prevNode.Parent;

        //                if (leftNode == null)
        //                    break;

        //                int reqLvl = mFolder.level;

        //                while (leftNode.Parent != null && reqLvl < (leftNode.Folder).level)
        //                {
        //                    leftNode = leftNode.Parent;
        //                }

        //                if (reqLvl > (leftNode.Folder).level)
        //                    parent = leftNode;
        //                else
        //                    parent = leftNode.Parent;

        //                parent.Items.Add(node);
        //                node.Parent = parent;
        //            }

        //            prevNode = node;
        //            lvl = mFolder.level;

        //            if (priceMap.ContainsKey(mFolder.id))
        //            {
        //                foreach (Price p in priceMap[mFolder.id])
        //                {
        //                    PriceNode n = new PriceNode(p)
        //                    {
        //                        Parent = node,
        //                        PackName = p.packName,
        //                    };

        //                    node.Items.Add(n);
        //                }
        //            }
        //        }
        //        catch (Exception)
        //        {
                       
        //        }
        //    }

        //    return result;
        //}

        //private Dictionary<int, List<Price>> CollectPrice()
        //{
        //    Dictionary<int, List<Price>> result = new Dictionary<int, List<Price>>();

        //    foreach (Price p in Update.GetStoredList<Price>(Price.OBJECT_NAME))
        //    {
        //        if (!result.ContainsKey(p.folderID))
        //            result[p.folderID] = new List<Price>();

        //        result[p.folderID].Add(p);
        //    }

        //    return result;
        //}
    }

    public class OrderValueConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if(targetType == typeof(string))
            {
                double val = (double)value;
                value = val.ToString("#.###;#.###;");
            } else if(targetType == typeof(double))
            {
                string s = value as string;
                double result = 0;
                if( s != null )
                    Double.TryParse(s.Replace(",", "."), NumberStyles.AllowDecimalPoint, CultureInfo.InvariantCulture, out result);
                value = result;
            }

            return value;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            return Convert(value, targetType, parameter, culture);
        }
    }

    public abstract class DataNode : IComparable<DataNode>
    {
        public virtual string Name { get { return string.Empty; } }
        public double Order { get; set; }
        public FolderNode Parent { get; set; }

        public int CompareTo(DataNode other)
        {
            return Name.CompareTo(other.Name);
        }
    }

    public class PriceActionData
    {
        public DateTime start;
        public DateTime end;
        public DateTime startAction;
        public DateTime endAction;

        public double cost;

        public PriceActionData(TradeAction td, TradeAction.ActionItem ai)
        {
            start = td.start;
            end = td.end;
            startAction = td.startAction;
            endAction = td.endAction;
            cost = ai.cost;
        }
    }

    public class PriceNode : DataNode, INotifyPropertyChanged
    {
        Price price;
        int costype;
        double cost;
        double inpack;
        PriceActionData action;
        string prefix = "";

        public PriceNode(Price p, int costype) : this(p, costype, null)
        {
        }

        public PriceNode(Price p, int costype, PriceActionData action)
        {
            price = p;
            inpack = p.inPack;
            this.costype = costype;
            Cost = costype < price.cost.Length ? price.cost[costype] : 0;
            this.action = action;
        }

        public string ID { get { return price.id; } }
        public string Manufacture { get => FirmN == null ? "" : FirmN.Name; }
        public string Brend { get => BrandN == null ? "" : BrandN.Name; }
        public double Qty { get { return Order / inpack; }  set { Order = value * inpack; } }

        public string DocNumber { get; set; }
        public string DocNumberInt { get; set; }
        public DateTime DocDate { get; set; }
        public string Party { get; set; }
        public double DlvQty { get; set; }
        public DateTime Expired { get; set; }

        public Firms FirmN { get; set; }
        public Brands BrandN { get; set; }

        public string Prefix
        {
            get => action == null ? prefix : "A";
            set => prefix = value;
        }

        //public string Remnants { get; set; }
        //public double RemnantsD { get; set; }
        public string Sell { get; set; }
        public double SellD { get; set; }
        public string SellDate { get; set; }

        public string DscPeriod
        {
            get => (action == null) ? "" : String.Format("{0:dd.MM} - {1:dd.MM}", action.start, action.end);
        }

        public string ActPeriod
        {
            get => (action == null) ? "" : String.Format("{0:dd.MM} - {1:dd.MM}", action.startAction, action.endAction);
        }

        public string Plan { get; set; }
        public double PlanD { get; set; }
        public string Fact { get; set; }
        public double Cost
        {
            get => action == null ? cost : action.cost;
            set => cost = value;
        }
        public double Weight { get { return price.weight; } }
        public Price Price => price;
        public override string Name => price != null ? price.name : base.Name;
        public string PackName { get; set; }
        public double InPack { get => inpack; set => inpack = value; }

        public double Sum {
            set { OnPropertyChanged("Sum"); }
            get { return Cost * Order; }
        }

        public int CostType { get => costype; }

        public event PropertyChangedEventHandler PropertyChanged;

        protected void OnPropertyChanged(string name)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));
        }
    }

    public class FolderNode : DataNode
    {
        public List<DataNode> Items { get; set; }
        Folder folder;

        public FolderNode(Folder f)
        {
            Items = new List<DataNode>();
            folder = f;
        }

        public FolderNode(FolderNode src)
        {
            Items = new List<DataNode>();
            folder = src.folder;
        }
        public string ID { get => (folder == null ? "" : folder.fid); }

        public Folder Folder => folder;
        public override string Name => folder != null ? folder.name : base.Name;
    }
}
