using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Text.RegularExpressions;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Controls.Ribbon;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Threading;

namespace Napoleon
{
    public partial class PriceWindow : RibbonWindow, Update.IDataLoadProcess
    {
        public static DependencyProperty ResultQtyProperty = 
            DependencyProperty.Register("ResultQty", typeof(double), typeof(PriceWindow));

        public static DependencyProperty ResultSumProperty =
            DependencyProperty.Register("ResultSum", typeof(double), typeof(PriceWindow));

        public static DependencyProperty ResultSelProperty =
            DependencyProperty.Register("ResultSel", typeof(double), typeof(PriceWindow));

        public static DependencyProperty ResultRemnProperty =
            DependencyProperty.Register("ResultRemn", typeof(double), typeof(PriceWindow));

        public static DependencyProperty FirmIDProperty =
            DependencyProperty.Register("FirmID", typeof(string), typeof(PriceWindow));

        public static DependencyProperty DlvDateProperty =
            DependencyProperty.Register("DlvDate", typeof(DateTime), typeof(PriceWindow));
        public static DependencyProperty PlanDateProperty =
            DependencyProperty.Register("PlanDate", typeof(DateTime), typeof(PriceWindow));

        private ObservableCollection<DataNode> data = new ObservableCollection<DataNode>();

        //private bool clearing = false;
        public IWindowListener WindowListener { get; set; }
        public string OrgID { get; set; }
        //public Order Document { get; set; }
        public List<Firms> FirmList { get; set; }
        bool apply = false;
        Dictionary<string, LastSalesItems> lastSales;

        public DateTime PlanDate
        {
            get => (DateTime)GetValue(PlanDateProperty);
            set
            {
                SetValue(PlanDateProperty, value);
            }
        }
        public DateTime DlvDate
        {
            get => (DateTime)GetValue(DlvDateProperty);
            set
            {
                SetValue(DlvDateProperty, value);
            }
        }

        List<DataNode> allNodes;

        Dictionary<string, List<string>> orgMatrix = new Dictionary<string, List<string>>();
        Dictionary<string, PlanNew> plans = new Dictionary<string, PlanNew>();
        Dictionary<string, bool> mml = new Dictionary<string, bool>();
        Dictionary<string, bool> mustbe = new Dictionary<string, bool>();

        DataFilterManager dfManager = null;
        DataGridColumnHeader curColumn = null;

        List<Order> documents = new List<Order>();
        List<Order> deletedDocuments = new List<Order>();
        //public DateTime deliveryDate;
        long linkedValue;

        public string FirmID
        {
            get { return (string)GetValue(FirmIDProperty); }
            set { SetValue(FirmIDProperty, value); }
        }

        public double ResultQty
        {
            get { return (double)GetValue(ResultQtyProperty); }
            set { SetValue(ResultQtyProperty, value); }
        }

        public double ResultSum
        {
            get { return (double)GetValue(ResultSumProperty); }
            set { SetValue(ResultSumProperty, value); }
        }

        public double ResultRemn
        {
            get { return (double)GetValue(ResultRemnProperty); }
            set { SetValue(ResultRemnProperty, value); }
        }

        public double ResultSell
        {
            get { return (double)GetValue(ResultSelProperty); }
            set { SetValue(ResultSelProperty, value); }
        }

        public PriceWindow(string id, long linked, DateTime dlvDate)
        {
            InitializeComponent();

            WinSizeHelper.Resotre(this, grid);

            OrgID = id;
            DlvDate = dlvDate;
            PlanDate = dlvDate.AddDays(-1);

            linkedValue = linked;

            grid.ItemsSource = data;

            FirmList = Update.GetStoredList<Firms>(Firms.OBJECT_NAME);


            grid.AddHandler(CommandManager.PreviewExecutedEvent,
                (ExecutedRoutedEventHandler)((sender, args) =>
                {
                    if (args.Command == DataGrid.BeginEditCommand)
                    {
                        if (((DataGrid)sender).SelectedItem is FolderNode)
                            args.Handled = true;
                    }
                }));

            RefreshPrice(id);

            grid.Focus();
        }

        private void RefreshPrice(string id)
        {
            Dictionary<string, Org> orgs = Update.GetStoredDictionary<Org>(Org.COMMON_OBJECT_NAME);

            Dictionary<string, double> orderData = new Dictionary<string, double>();
            if (allNodes != null)
            {
                foreach (DataNode d in allNodes)
                {
                    if (d.Order > 0 && d is PriceNode pd)
                    {
                        string pid = pd.FirmN.ID + "|" + pd.ID;
                        orderData[pid] = d.Order;
                    }
                }
            }

            if (orgs.ContainsKey(id))
            {
                Org o = orgs[id];
                Title = string.Format("{0}, {1}", o.Name, o.Address);

                List<string> availFirms = new List<string>();
                Update.GetStoredList<OrgDogovor>(OrgDogovor.OBJECT_NAME).ForEachFilter((x) => availFirms.Add(x.firm), (x) => x.ido == o.ido);

                Dictionary<string, OrgMatrix> mtx = Update.GetStoredDictionary<OrgMatrix>(OrgMatrix.OBJECT_NAME);
                foreach (string firm in availFirms)
                {
                    string name = getMatrixName(o, firm);
                    OrgMatrix omx;
                    if (mtx.TryGetValue(name, out omx) && omx.items.Count > 0)
                    {
                        List<string> items;
                        if (!orgMatrix.TryGetValue(firm, out items))
                        {
                            items = new List<string>();
                            orgMatrix[firm] = items;
                        }
                        foreach (OrgMatrix.Item oi in omx.items)
                        {
                            if (items.Contains(oi.id) == false)
                                items.Add(oi.id);
                            if (oi.mustBe > 0)
                                mustbe[oi.id] = true;
                        }
                    }
                }

                bool needCreateMatrix = mtx.Count == 0;
                foreach (PlanNew p in Update.GetStoredList<PlanNew>(PlanNew.OBJECT_NAME))
                {
                    if (p.date != PlanDate)
                        continue;
                    if (!needCreateMatrix && !mtx.ContainsKey(p.firm))
                        continue;
                    plans[p.firm] = p;
                    if (needCreateMatrix)
                    {
                        List<string> items = new List<string>();
                        foreach (PlanNew.Item pi in p.items)
                            items.Add(pi.id);
                    }
                }

                foreach (MMLFeatures f in Update.GetStoredList<MMLFeatures>(MMLFeatures.OBJECT_NAME))
                {
                    if (f.IsOrgType && f.id == o.formatTT)
                    {
                        foreach (MMLFeatures.Item mmi in f.items)
                            mml[mmi.id] = true;
                        break;
                    }
                }

                Dictionary<string, PriceActionData> actions = new Dictionary<string, PriceActionData>();
                foreach (TradeAction td in Update.GetStoredList<TradeAction>(TradeAction.OBJECT_NAME))
                {
                    if (td.IsActive(PlanDate, o))
                    {
                        foreach (TradeAction.ActionItem ai in td.items)
                        {
                            actions[ai.id] = new PriceActionData(td, ai);
                        }
                    }
                }

                allNodes = new PriceDataHelper().CreatePrice(o, orgMatrix, plans, mml, mustbe, actions);
                data.Clear();
                allNodes.ForEach((i) => {
                    if(i is PriceNode pd)
                    {
                        string pid = pd.FirmN.ID + "|" + pd.ID;
                        double qty;
                        if(orderData.TryGetValue(pid, out qty))
                        {
                            i.Order = qty;
                        }
                    }
                    data.Add(i);
                });
            }

            dfManager = new DataFilterManager(allNodes);
            dfManager.DataFiltred += DfManager_DataFiltred;
        }

        protected override void OnClosing(CancelEventArgs e)
        {
            WinSizeHelper.Save(this, grid);
            base.OnClosing(e);
        }

        private string getMatrixName(Org o, string firm)
        {
            foreach(IDMTX idm in Update.GetStoredList<IDMTX>(IDMTX.IDMTX_OBJ_NAME))
            {
                if (idm.firm == firm && idm.id == o.id)
                    return idm.mtx;
            }

            string[] matrix = new string[] {
                "", ObjectMatrix.ORG_OBJ, ObjectMatrix.ORG_TYPE_OBJ, ObjectMatrix.RETAIL_OBJ, ObjectMatrix.CHANNEL_OBJ,
            };
            string[] ids = new string[] {
                o.ido, o.ido, o.formatTT, o.idRetailer, o.idChannel,
            };
            int idx = 0;
            foreach (string mn in matrix)
            {
                foreach (ObjectMatrix obi in Update.GetStoredList<ObjectMatrix>(ObjectMatrix.IDOMTX_OBJ_NAME))
                {
                    if (obi.id == ids[idx] && obi.objectType == mn && obi.firm == firm)
                        return obi.mtx;
                }
                idx++;
            }
            return "";
        }

        private void Grid_LoadingRow(object sender, DataGridRowEventArgs e)
        {
            e.Row.Background = new SolidColorBrush(e.Row.DataContext is PriceNode ? Colors.White : Colors.Gray);
        }

        private void OnPreviewTextInput(object sender, TextCompositionEventArgs e)
        {
            e.Handled = !new Regex(@"^[0-9]*(?:\.[0-9]*)?$").IsMatch(e.Text);
        }

        private void BtnSave_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            grid.CommitEdit();
            dlvDate.SelectedDateChanged -= DlvDate_SelectedDateChanged;
            DateTime check = DateTime.Now.AddDays(1);
            if (DlvDate < check)
            {
                DlvDate = check;
                PlanDate = DlvDate.AddDays(-1);
            }

            SaveDocument();
            dlvDate.SelectedDateChanged += DlvDate_SelectedDateChanged;
        }

        Dictionary<string, Order> PrepareDocuments()
        {
            Dictionary<string, Price> price = Update.GetStoredDictionary<Price>(Price.OBJECT_NAME);

            DateTime created;
            Dictionary<string, Order> docs = new Dictionary<string, Order>();

            if (documents.Count == 0)
                created = DateTime.Now;
            else
            {
                created = DateTime.MinValue;

                documents.ForEach(x => {
                    docs[x.firmCode] = x;
                    x.items.Clear();
                    if (x.created > created)
                        created = x.created;
                });

                created = created.AddSeconds(1);
            }


            String uid = Properties.Settings.Default.Login;

            foreach (DataNode d in allNodes)
            {
                if (d.Order > 0 && d is PriceNode pd)
                {
                    Order o;
                    if (!docs.TryGetValue(pd.FirmN.id, out o))
                    {
                        o = new Order();
                        o.id = OrgID;
                        o.date = PlanDate;
                        o.linked = linkedValue;
                        //o.dlvDate = DlvDate;
                        o.userid = uid;
                        o.created = created;
                        o.modify = created;
                        o.firmCode = pd.FirmN.id;
                        o.sumType = pd.CostType;

                        created = created.AddSeconds(1);
                        docs[pd.FirmN.id] = o;
                    }

                    OrderItem oi = new OrderItem()
                    {
                        id = pd.ID,
                        qty = pd.Order,
                        sum = pd.Order * pd.Cost,
                        cost = pd.Cost,
                        item = price.ContainsKey(pd.ID) ? price[pd.ID] : null,
                        flags = OrderItem.IN_PACK,
                    };
                    o.items.Add(oi);
                    o.dlvDate = DlvDate;
                }
            }

            return docs;
        }

        private void SaveDocument()
        {
            Dictionary<string, Order> docs = PrepareDocuments();
            deletedDocuments.Clear();

            foreach(Order src in docs.Values)
                if(src.items.Count == 0)
                    deletedDocuments.Add(src);

            deletedDocuments.ForEach(x => docs.Remove(x.firmCode));
            documents.Clear();
            documents.AddRange(docs.Values);

            Thread t = new Thread(Save);
            t.Start();
        }

        private void Save(object obj)
        {
            UpdateCollection upd = null;
            UpdateCollection rmv = null;

            if (documents.Count > 0)
            {
                upd = new UpdateCollection();
                upd.Add(Order.OBJECT_NAME).AddRange(documents);
            }

            if (deletedDocuments.Count > 0)
            {
                rmv = new UpdateCollection();
                rmv.Add(Order.OBJECT_NAME).AddRange(deletedDocuments);
            }
            if (rmv == null && upd == null)
                return;

            Dispatcher.Invoke(new Action(() =>
            {
                progressLayout.Visibility = System.Windows.Visibility.Visible;
            }));

            bool result = Update.WriteObjects(upd, rmv);

            Dispatcher.Invoke(new Action(() =>
            {
                progressLayout.Visibility = System.Windows.Visibility.Hidden;

                if (result)
                {
                    apply = true;
                    //Update.PutStored(Document);
                    Close();
                }
                else
                    StdDialog.UpdateErrMsg(this);
            }));
        }
        
        private void RibbonWindow_Closed(object sender, EventArgs e)
        {
            FireWindowClosed();
        }

        private void FireWindowClosed()
        {
            if (WindowListener != null)
                WindowListener.Closed(this, apply);
        }

        private void RibbonWindow_Loaded(object sender, System.Windows.RoutedEventArgs e)
        {
            LoadDocuments(linkedValue);
        }


        void LoadDocuments(long linked)
        {
            Update.QueryList upd = new Update.QueryList();
            upd.Add(Order.OBJECT_NAME, "\"linked\" = " + linked.ToString());
            upd.Add(LastSalesItems.OBJECT_NAME, OrgID);

            Update.StdDataRefresh(upd, this);
        }

        public void DoLoadData(Update.UpdateResult res)
        {
            //Dictionary<string, Dictionary<string, double>> qtys = new Dictionary<string, Dictionary<string, double>>();
            documents.Clear();
            lastSales = res.GetDictionary<LastSalesItems>(LastSalesItems.OBJECT_NAME);

            res.GetList<Order>(Order.OBJECT_NAME).ForEach((i) =>
            {
                //foreach (OrderItem oi in i.items)
                //{
                //    Dictionary<string, double> qd;
                //    if (!qtys.TryGetValue(i.firmCode, out qd))
                //    {
                //        qd = new Dictionary<string, double>();
                //        qtys[i.firmCode] = qd;
                //    }
                //    qd[oi.id] = oi.qty;
                //}

                documents.Add(i);
                // не даем сохранять чужие документы
                if (i.agent != null && i.agent.id != i.agent.userid)
                {
                    btnSave.IsEnabled = false;
                }
            });

            if (documents.Count > 0)
            {
                DlvDate = documents[0].dlvDate;
            }
            //PlanDate = (documents.Count > 0) ? documents[0].date : DateTime.Now;
            UpdateLastSales();
        }

        private void UpdateLastSales()
        {
            if (lastSales == null)
                return;

            Dictionary<string, Dictionary<string, double>> qtys = new Dictionary<string, Dictionary<string, double>>();
            foreach (Order i in documents)
            {
                foreach (OrderItem oi in i.items)
                {
                    Dictionary<string, double> qd;
                    if (!qtys.TryGetValue(i.firmCode, out qd))
                    {
                        qd = new Dictionary<string, double>();
                        qtys[i.firmCode] = qd;
                    }
                    qd[oi.id] = oi.qty;
                }
            }

            foreach (DataNode ms in allNodes)
            {
                PriceNode pn = ms as PriceNode;
                if (pn != null)
                {
                    double val;
                    Dictionary<string, double> firmItems;
                    if (qtys.TryGetValue(pn.FirmN.id, out firmItems) && firmItems.TryGetValue(pn.ID, out val))
                        pn.Order = val;

                    LastSalesItems ls;
                    if (lastSales.TryGetValue(pn.ID, out ls))
                    {
                        pn.SellDate = ls.date.ToShortDateString();
                        pn.SellD = ls.qty;
                        pn.Sell = pn.SellD.ToString("N3");
                    }
                    else
                    {
                        pn.SellD = 0;
                        pn.SellDate = "";
                        pn.Sell = "";
                    }
                }
            }

            grid.Items.Refresh();
            CalcResult();
        }

        private void CalcResult()
        {
            ResultQty = 0;
            ResultSum = 0;
            ResultRemn = 0;
            ResultSell = 0;

            foreach (DataNode d in data)
            {
                if (d is PriceNode pd )
                {
                    ResultQty += pd.Order;
                    ResultSum += pd.Order * pd.Cost;
                    //ResultRemn += pd.RemnantsD;
                    ResultSell += pd.SellD;
                }
            }
        }

        private void Grid_RowEditEnding(object sender, DataGridRowEditEndingEventArgs e)
        {
            if (e.EditAction == DataGridEditAction.Commit && e.Row.Item is PriceNode p)
            {
                CalcResult();
                p.Sum = p.Cost * p.Order;
                //grid.Dispatcher.BeginInvoke(new Action(() => grid.Items.Refresh()), System.Windows.Threading.DispatcherPriority.Background);
            }
        }

        DataGridColumnHeader FindClickedColumn(MouseButtonEventArgs e)
        {
            DataGridColumnHeader clmn = null;
            DependencyObject dep = (DependencyObject)e.OriginalSource;
            while (dep != null)
            {
                clmn = dep as DataGridColumnHeader;
                if (clmn != null)
                    break;

                dep = VisualTreeHelper.GetParent(dep);
            }

            return clmn;
        }

        private void grid_PreviewMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            DataGridColumnHeader clmn = FindClickedColumn(e);
            curColumn = null;
            if (clmn != null && clmn.DisplayIndex == clmnFirm.DisplayIndex)
            {
                curColumn = clmn;
                dfManager.DoFilter(DataFilterManager.FilterType.Firm);
            } else if (clmn != null && clmn.DisplayIndex == clmnBrand.DisplayIndex)
            {
                curColumn = clmn;
                dfManager.DoFilter(DataFilterManager.FilterType.Brand);
            } else if (clmn != null && clmn.DisplayIndex == clmnPrefix.DisplayIndex)
            {
                curColumn = clmn;
                dfManager.DoFilter(DataFilterManager.FilterType.Prefix);
            }
            else if (clmn != null && clmn.DisplayIndex == clmnName.DisplayIndex)
            {
                curColumn = clmn;
                dfManager.DoFilter(DataFilterManager.FilterType.Folder);
            }
        }

        private void DfManager_DataFiltred(object src, List<DataNode> newList)
        {
            data.Clear();
            newList.ForEach(x => data.Add(x));
            CalcResult();
            grid.Items.Refresh();
            if(curColumn != null)
            {
                curColumn.FontWeight = FontWeights.Bold;
                curColumn = null;
            }
        }

        private void grid_PreviewMouseRightButtonDown(object sender, MouseButtonEventArgs e)
        {
            DataGridColumnHeader clmn = FindClickedColumn(e);
            if (clmn != null && clmn.DisplayIndex == clmnFirm.DisplayIndex)
            {
                dfManager.ClearFilter(DataFilterManager.FilterType.Firm);
                clmn.FontWeight = FontWeights.Normal;
            }
            else if (clmn != null && clmn.DisplayIndex == clmnBrand.DisplayIndex)
            {
                dfManager.ClearFilter(DataFilterManager.FilterType.Brand);
                clmn.FontWeight = FontWeights.Normal;
            }
            else if (clmn != null && clmn.DisplayIndex == clmnPrefix.DisplayIndex)
            {
                dfManager.ClearFilter(DataFilterManager.FilterType.Prefix);
                clmn.FontWeight = FontWeights.Normal;
            }
            else if (clmn != null && clmn.DisplayIndex == clmnName.DisplayIndex)
            {
                dfManager.ClearFilter(DataFilterManager.FilterType.Folder);
                clmn.FontWeight = FontWeights.Normal;
            }
        }

        private void DlvDate_SelectedDateChanged(object sender, SelectionChangedEventArgs e)
        {
            if(DlvDate < DateTime.Now.Date.AddDays(1))
            {
                DlvDate = DateTime.Now.Date.AddDays(1);
                return;
            }
            PlanDate = DlvDate.AddDays(-1);
            RefreshPrice(OrgID);
            UpdateLastSales();
        }
    }

    public class Prefix : IDataFiltrable
    {
        public string id = "";
        public string name = "";

        public Prefix(string id, string name)
        {
            this.id = id;
            this.name = name;
        }

        public string GetId { get { return id; } }
        public string GetName { get { return name; } }
    }

    abstract public class DataNodeFilter
    {
        protected List<IDataFiltrable> source = new List<IDataFiltrable>();
        protected List<string> filter = new List<string>();

        abstract public bool InSet(FolderNode f, PriceNode p);
        public virtual void Clear() { filter.Clear(); }

        public bool ShowDialog()
        {
            List<string> newfltr = DataFilterDialog.Filtering(source, filter);
            if (newfltr == null)
                return false;

            filter = newfltr;
            return true;
        }
    }


    public delegate void DataFiltredHandle(object src, List<DataNode> newList);
    public class DataFilterManager
    {
        List<DataNode> allData;
        Dictionary<FilterType, DataNodeFilter> filters = new Dictionary<FilterType, DataNodeFilter>();

        FirmFilter firmFilter = new FirmFilter();
        BrandFilter brandFilter = new BrandFilter();
        PrefixFilter prefixFilter = new PrefixFilter();
        FolderFilter folderFilter;

        public enum FilterType { Firm, Brand, Prefix, Folder };

        public DataFilterManager(List<DataNode> allData)
        {
            this.allData = allData;
            folderFilter = new FolderFilter(allData);
        }

        public void DoFilter(FilterType filter)
        {
            DataNodeFilter cf = null;

            if (filter == FilterType.Firm)
                cf = firmFilter;
            else if (filter == FilterType.Brand)
                cf = brandFilter;
            else if (filter == FilterType.Prefix)
                cf = prefixFilter;
            else if (filter == FilterType.Folder)
                cf = folderFilter;

            if (cf != null)
            {
                if(cf.ShowDialog())
                {
                    filters[filter] = cf;
                    RefreshNodes();
                }
            }
        }

        private void RefreshNodes()
        {
            List<DataNode> newNodes = new List<DataNode>();
            foreach(DataNode dn in allData)
            {
                FolderNode fn = dn as FolderNode;
                if (fn == null)
                    continue;

                FolderNode dest = null;
                foreach(DataNode pnd in fn.Items)
                {
                    PriceNode pn = pnd as PriceNode;
                    if (pnd == null)
                        continue;
                    if(InSet(fn, pn))
                    {
                        if (dest == null)
                        {
                            dest = new FolderNode(fn);
                            newNodes.Add(dest);
                        }
                        dest.Items.Add(pn);
                        newNodes.Add(pn);
                    }
                }
            }

            if(DataFiltred != null)
            {
                DataFiltred.Invoke(this, newNodes);
            }
        }

        bool InSet(FolderNode fn, PriceNode pn)
        {
            if (filters.Count == 0)
                return true;

            foreach (DataNodeFilter dnf in filters.Values)
                if (dnf.InSet(fn, pn) == false)
                    return false;

            return true;
        }

        public void ClearFilter(FilterType filter)
        {
            filters.Remove(filter);
            RefreshNodes();
        }

        public event DataFiltredHandle DataFiltred;
    }

    class FirmFilter : DataNodeFilter
    {
        public FirmFilter()
        {
            Update.GetStoredList<Firms>(Firms.OBJECT_NAME).ForEach(x => source.Add(x));
        }

        public override bool InSet(FolderNode f, PriceNode p)
        {
            return filter.Contains(p.FirmN.id);
        }
    }

    class BrandFilter : DataNodeFilter
    {
        public BrandFilter()
        {
            Update.GetStoredList<Brands>(Brands.OBJECT_NAME).ForEach(x => source.Add(x));
        }

        public override bool InSet(FolderNode f, PriceNode p)
        {
            return filter.Contains(p.BrandN.id);
        }
    }

    class PrefixFilter : DataNodeFilter
    {
        public PrefixFilter()
        {
            source.Add(new Prefix("", ""));
            source.Add(new Prefix("A", "A"));
            source.Add(new Prefix("!", "!"));
            source.Add(new Prefix("M", "M"));
        }

        public override bool InSet(FolderNode f, PriceNode p)
        {
            return filter.Contains(p.Prefix);
        }
    }

    class FolderFilter : DataNodeFilter
    {
        public FolderFilter(List<DataNode> nodes)
        {
            Dictionary<string, bool> loaded = new Dictionary<string, bool>();
            Dictionary<string, Folder> flds = Update.GetStoredDictionary<Folder>(Folder.OBJECT_NAME);
            foreach(DataNode dn in nodes)
            {
                FolderNode fn = dn as FolderNode;
                if (fn == null)
                    continue;

                if (loaded.ContainsKey(fn.ID))
                    continue;

                loaded[fn.ID] = true;
                Folder f;
                if(flds.TryGetValue(fn.ID, out f))
                    source.Add(f);
            }
        }

        public override bool InSet(FolderNode f, PriceNode p)
        {
            return filter.Contains(f.ID);
        }
    }
}
