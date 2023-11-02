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
    public partial class ReturnPriceWindow : RibbonWindow, Update.IDataLoadProcess
    {
        public static DependencyProperty ResultQtyProperty = 
            DependencyProperty.Register("ResultQty", typeof(double), typeof(ReturnPriceWindow));

        public static DependencyProperty ResultSumProperty =
            DependencyProperty.Register("ResultSum", typeof(double), typeof(ReturnPriceWindow));

        public static DependencyProperty ReturnLimitProperty =
            DependencyProperty.Register("DocReturnLimit", typeof(string), typeof(ReturnPriceWindow));

        private ObservableCollection<DataNode> data = new ObservableCollection<DataNode>();

        //private bool clearing = false;
        public string OrgID { get; set; }

        List<DataNode> allNodes;
        //Dictionary<string, ReturnRequest> activeDocs = new Dictionary<string, ReturnRequest>();
        ReturnRequest curDoc = null;
        Firms curFirm = null;
        bool dirty = false;
        Dictionary<Price, ReturnCause> causeMap = new Dictionary<Price, ReturnCause>();

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

        public string DocReturnLimit
        {
            get { return (string)GetValue(ReturnLimitProperty); }
            set { SetValue(ReturnLimitProperty, value); }
        }

        public ReturnPriceWindow(string id, ReturnRequest doc)
        {
            InitializeComponent();

            WinSizeHelper.Resotre(this, grid);

            OrgID = id;
            DateTime check = DateTime.Now.AddDays(1);
            
            grid.ItemsSource = data;

            grid.AddHandler(CommandManager.PreviewExecutedEvent,
                (ExecutedRoutedEventHandler)((sender, args) =>
                {
                    if (args.Command == DataGrid.BeginEditCommand)
                    {
                        if (((DataGrid)sender).SelectedItem is FolderNode)
                            args.Handled = true;
                    }
                }));

            Dictionary<string, Org> orgs = Update.GetStoredDictionary<Org>(Org.COMMON_OBJECT_NAME);

            allNodes = new List<DataNode>();

            if (orgs.ContainsKey(id))
            {
                Org o = orgs[id];
                Title = string.Format("Заявка на возврат {0}, {1}", o.Name, o.Address);
            }

            //dfManager = new DataFilterManager(allNodes);
            //dfManager.DataFiltred += DfManager_DataFiltred;

            grid.Focus();

            if (doc != null)
                curDoc = doc;
        }

        protected override void OnClosing(CancelEventArgs e)
        {
            WinSizeHelper.Save(this, grid);
            if (CheckSaveDoc() == false)
                return;
            base.OnClosing(e);
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
            SaveCurDocument();
        }

        private void RibbonWindow_Loaded(object sender, System.Windows.RoutedEventArgs e)
        {
            LoadDocuments();
        }

        void LoadDocuments()
        {
            Update.QueryList upd = new Update.QueryList();

            string now = DateTime.Now.ToString("dd/MM/yyyy");
            string where = string.Format("\"created\" >= ToDate('" + now + "')");
            //upd.Add(OrderProceeded.OBJECT_NAME, where);
            upd.Add(ReturnRequest.OBJECT_NAME, "\"id\" = '" + OrgID + "' and " + where + " and \"userid\"='$CURRENT_USERID' and \"created\" >= ToDate('" + 
                DateTime.Now.AddMonths(-1).ToString("dd.MM.YYYY") + "')");
            upd.Add(ReturnCause.OBJECT_NAME);
            upd.Add(ReturnLimit.OBJECT_NAME);

            //now = "01/01/2017";
            string expired = DateTime.Now.AddDays(2).ToString("dd/MM/yyyy");
            upd.Add(NotExpiredItems.OBJECT_NAME, OrgID + ";" + expired);

            Update.StdDataRefresh(upd, this);
        }

        double CountLimit(DataNode curNode, out ReturnLimit cur)
        {
            cur = null;
            PriceNode pn = curNode as PriceNode;
            if (pn == null)
                return 0;

            double ret = 0;
            foreach (ReturnLimit rl in Update.GetStoredList<ReturnLimit>(ReturnLimit.OBJECT_NAME))
                if (rl.Active && rl.priceType == pn.Price.idType)
                {
                    cur = rl;
                    ret = cur.limit;
                    break;
                }

            if(cur != null)
            {
                foreach (DataNode dn in allNodes)
                {
                    PriceNode prn = dn as PriceNode;
                    if (prn == null || prn.Order == 0 || prn.Price.idType != cur.priceType)
                        continue;
                    ret -= cur.GetNodeValue(prn);
                }

                foreach (ReturnRequest rr in Update.GetStoredList<ReturnRequest>(ReturnRequest.OBJECT_NAME))
                {
                    if (cur.InPeriod(rr))
                    {
                        ret -= cur.CountValue(rr);
                    }
                }
            }

            return ret;
        }

        void RefreshLimit(DataNode curNode)
        {
            ReturnLimit rl;
            double ret = CountLimit(curNode, out rl);
            string text = "";

            if(rl != null)
                if (rl.canOverlimit > 0)
                {
                    text = "Без ограничений";
                }
                else
                {
                    text = rl.limitType == ReturnLimit.LIMIT_SUM ? ret.ToString("N2") + " руб." : ret.ToString("N3") + " кг";
                }
            DocReturnLimit = text;
        }

        bool CheckLimit(PriceNode node)
        {
            ReturnLimit rl;
            double ret = CountLimit(node, out rl);
            return ret >= 0;
        }

        public void DoLoadData(Update.UpdateResult res)
        {
            //Dictionary<OPKey, bool> oldDocs = new Dictionary<OPKey, bool>();
            //res.GetList<OrderProceeded>(OrderProceeded.OBJECT_NAME).ForEach(x => oldDocs.Add(new OPKey(x), true));
            //res.GetList<ReturnRequest>(ReturnRequest.OBJECT_NAME).ForEach(x => { 
            //    if(oldDocs.ContainsKey(new OPKey(x)) == false) {
            //        activeDocs[x.firmCode] = x;
            //    }
            //});

            data.Clear();

            List<Firms> firms = new List<Firms>();
            allNodes = new PriceDataHelper().CreateReturnPrice(OrgID, res.GetList<NotExpiredItems>(NotExpiredItems.OBJECT_NAME));
            foreach(DataNode dn in allNodes)
            {
                PriceNode pn = dn as PriceNode;
                if(pn != null && firms.Contains(pn.FirmN) == false)
                {
                    firms.Add(pn.FirmN);
                }
            }
            firms.Sort();
            cbFactory.ItemsSource = firms;
            if (firms.Count > 0)
            {
                cbFactory.SelectionChanged -= cbFactory_SelectionChanged;
                cbFactory.SelectedIndex = 0;
                LoadFirmData(firms[0]);
                cbFactory.SelectionChanged += cbFactory_SelectionChanged;
            }

            CalcResult();
        }

        private void CalcResult()
        {
            ResultQty = 0;
            ResultSum = 0;

            foreach (DataNode d in data)
            {
                if (d is PriceNode pd )
                {
                    ResultQty += pd.Order;
                    ResultSum += pd.Order * pd.Cost;
                }
            }
        }

        private void Grid_RowEditEnding(object sender, DataGridRowEditEndingEventArgs e)
        {
            if (e.EditAction == DataGridEditAction.Commit && e.Row.Item is PriceNode p)
            {
                dirty = true;
                CalcResult();
                p.Sum = p.Cost * p.Order;
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

        bool CheckSaveDoc()
        {
            if(dirty)
            {
                MessageBoxResult res = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButton.YesNoCancel, MessageBoxImage.Question);
                if (res == MessageBoxResult.Cancel)
                    return false;
                if (res == MessageBoxResult.OK)
                    return SaveCurDocument();
            }
            return true;
        }

        bool SaveCurDocument()
        {
            curDoc.items.Clear();
            string id = "";
            ReturnRequest.RRItem curItem = null;
            foreach (DataNode dn in allNodes)
            {
                PriceNode pn = dn as PriceNode;
                if (pn == null)
                    continue;

                if(pn.Order > 0)
                {
                    if (id != pn.ID)
                    {
                        if (curItem != null)
                        {
                            curItem.qty = curItem.Qty;
                            curDoc.items.Add(curItem);
                        }

                        if (pn.DlvQty < pn.Order)
                        {
                            grid.SelectedItem = dn;
                            grid.CurrentItem = dn;
                            MessageBox.Show("Количество в возврате больше чем в накладной", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Stop);
                            return false;
                        }

                        if (CheckLimit(pn) == false)
                        {
                            grid.SelectedItem = dn;
                            grid.CurrentItem = dn;
                            MessageBox.Show("Превышен лимит возврата товара", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Stop);
                            return false;
                        }

                        ReturnCause rc;
                        if (!causeMap.TryGetValue(pn.Price, out rc))
                        {
                            grid.SelectedItem = dn;
                            grid.CurrentItem = dn;
                            LoadCause(pn);
                            MessageBox.Show("Не выбрана причина возврата", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Stop);
                            return false;
                        }

                        curItem = new ReturnRequest.RRItem();
                        curItem.id = pn.ID;
                        curItem.cause = rc.id;
                        curItem.uid = Guid.NewGuid().ToString().Replace("-", "");

                        id = pn.ID;
                    }
                    ReturnRequest.ReturnDlv rdi = new ReturnRequest.ReturnDlv();
                    rdi.qty = pn.Order;
                    rdi.number = pn.DocNumberInt;
                    rdi.date = pn.DocDate;
                    rdi.party = pn.Party;
                    rdi.cost = pn.Cost;

                    curItem.items.Add(rdi);
                }
            }
            if (curItem != null)
            {
                curItem.qty = curItem.Qty;
                curDoc.items.Add(curItem);
            }

            dirty = false;

            UpdateCollection upd = new UpdateCollection();
            upd.Add(ReturnRequest.OBJECT_NAME).Add(curDoc);

            Update.WriteObjects(upd, null);

            return true;
        }

        private void cbFactory_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            Firms sel = cbFactory.SelectedItem as Firms;
            if (sel != null && curFirm != sel)
            {
                if(!CheckSaveDoc())
                {
                    cbFactory.SelectedItem = curFirm;
                    return;
                }
                curFirm = null;
                LoadFirmData(sel);
            }
        }

        private void LoadFirmData(Firms firm)
        {
            dirty = false;
            curFirm = firm;
            data.Clear();

            if(curDoc == null)
            {
                curDoc = new ReturnRequest();
                curDoc.id = OrgID;
            } else
            {
            }
            curDoc.firmCode = firm.id;

            foreach (DataNode dn in allNodes)
            {
                FolderNode fn = dn as FolderNode;
                if (fn == null)
                    continue;
                FolderNode destFolder = null;
                foreach (DataNode cn in fn.Items)
                {
                    PriceNode pn = cn as PriceNode;
                    if (pn == null)
                        continue;
                    if(pn.FirmN.id == firm.id)
                    {
                        if(destFolder == null)
                        {
                            destFolder = new FolderNode(fn);
                            data.Add(destFolder);
                        }
                        destFolder.Items.Add(pn);
                        pn.Qty = curDoc.GetQty(pn);
                        data.Add(pn);
                    }
                }
            }

            causeMap.Clear();
            List<ReturnCause> rcList = Update.GetStoredList<ReturnCause>(ReturnCause.OBJECT_NAME);
            foreach (ReturnRequest.RRItem i in curDoc.items)
            {
                foreach (ReturnCause rci in rcList)
                {
                    if (rci.id == i.cause && rci.firm == curDoc.firmCode && rci.idType == i.item.idType)
                    {
                        causeMap[i.item] = rci;
                        break;
                    }
                }
            }
        }

        private void grid_CurrentCellChanged(object sender, EventArgs e)
        {
            PriceNode pn = grid.CurrentItem as PriceNode;
            LoadCause(pn);
            RefreshLimit(grid.CurrentItem as DataNode);
        }

        private void LoadCause(PriceNode pn)
        {
            Firms selFirm = cbFactory.SelectedItem as Firms;
            List<ReturnCause> list = new List<ReturnCause>();
            ReturnCause selected = null;

            if (pn != null && selFirm != null && selFirm.id !="")
            {
                string type = pn.Price.idType;
                foreach(ReturnCause rc in Update.GetStoredList<ReturnCause>(ReturnCause.OBJECT_NAME))
                {
                    if(rc.firm == selFirm.id && rc.idType == type)
                    {
                        list.Add(rc);
                    }
                }
                causeMap.TryGetValue(pn.Price, out selected);
            }

            cbCause.SelectionChanged -= CbCause_SelectionChanged;

            list.Sort();
            list.Insert(0, new ReturnCause());
            cbCause.ItemsSource = list;
            cbCause.SelectedItem = selected;

            cbCause.SelectionChanged += CbCause_SelectionChanged;
        }

        private void CbCause_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            ReturnCause rc = cbCause.SelectedItem as ReturnCause;
            PriceNode pn = grid.CurrentItem as PriceNode;
            if(pn != null && rc != null)
            {
                if (rc.id == "")
                    causeMap.Remove(pn.Price);
                else
                    causeMap[pn.Price] = rc;
            }
        }
    }
}
