using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Drawing;
using GRSoft.NapoleonManager.Utils;
using System.IO;

namespace GRSoft.NapoleonManager
{

   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      public static string ALL_FIRMS_TEXT = "<все>";

      SimpleDataSet<OrgDogovor> dogovors = new SimpleDataSet<OrgDogovor>(OrgDogovor.OBJECT_NAME, false);
      //SimpleDataSet<OrderAddConfig> firms = new SimpleDataSet<OrderAddConfig>("Firms", false);
      DataSet<string, PriceType> dsPriceTypes = new DataSet<string, PriceType>(PriceType.OBJECT_NAME);

      //SimpleDataSet<DistribOrg> distrib = new SimpleDataSet<DistribOrg>(DistribOrg.OBJECT_NAME, false);
      SimpleDataSet<ReturnRequest> rreq = new SimpleDataSet<ReturnRequest>(ReturnRequest.OBJECT_NAME, true);
      SimpleDataSet<OrderBundle> bundles = new SimpleDataSet<OrderBundle>(OrderBundle.OBJECT_NAME, true);
      SimpleDataSet<RejectAct> rejectActs = new SimpleDataSet<RejectAct>(RejectAct.OBJECT_NAME, true);

      DataSet<string, NoOrderReason> reasons = new DataSet<string, NoOrderReason>(NoOrderReason.OBJECT_NAME);
      DataSet<int, UserLog> dsUserLog;

      ComboBox cbFirmFilter;
      CheckBox cbUsePriceFilter;
      Button btnPriceFilter;
      Dictionary<String, String> dogCache = new Dictionary<string, string>();
      ComboBox cbFirms;
      Button btnSave;
      DataGridViewTextBoxColumn clmnPackQty = new DataGridViewTextBoxColumn();
      DateTimePicker dlvDate = new DateTimePicker();
      DistribDetail distribDetail = new DistribDetail();

      RRItems rritems = new RRItems();
      RejectActOverview raov = new RejectActOverview();

      Dictionary<DateTime, List<DateTime>> orderSended = new Dictionary<DateTime,List<DateTime>>();
      Dictionary<DateTime, List<DateTime>> returnSended = new Dictionary<DateTime, List<DateTime>>();


      bool usePriceFilter = false;

      public FmDetailEx(FmDetailData data) : base(data)
      {
         cbFirmFilter = new ComboBox();
         cbFirmFilter.Size = new System.Drawing.Size(200, 20);
         int cx = label5.Right + 5;
         cbFirmFilter.Location = new System.Drawing.Point(cx, 2);
         cbFirmFilter.Anchor = AnchorStyles.Top | AnchorStyles.Left;
         cbFirmFilter.Name = "cbFirmFilter";
         cbFirmFilter.SelectionChangeCommitted += cbFirmFilter_SelectedIndexChanged;
         cbFirmFilter.ItemHeight = 14;
         cbFirmFilter.Visible = true;

         cbUsePriceFilter = new CheckBox();
         cbUsePriceFilter.Text = "Фильтр номенклатуры";
         cbUsePriceFilter.Location = new System.Drawing.Point(cbFirmFilter.Right + 5, 4);
         cbUsePriceFilter.Size = new Size(150, 17);
         cbUsePriceFilter.Anchor = AnchorStyles.Top | AnchorStyles.Left;
         cbUsePriceFilter.Name = "cbUsePriceFilter";
         cbUsePriceFilter.Visible = true;
         cbUsePriceFilter.ForeColor = Color.White;
         cbUsePriceFilter.Click += SwitchFilter;

         btnPriceFilter = new Button();
         btnPriceFilter.Text = "Выбор товара";
         btnPriceFilter.Location = new System.Drawing.Point(cbUsePriceFilter.Right + 5, 1);
         btnPriceFilter.Size = new Size(100, 22);
         btnPriceFilter.Anchor = AnchorStyles.Top | AnchorStyles.Left;
         btnPriceFilter.Name = "btnPriceFilter";
         btnPriceFilter.Visible = true;
         btnPriceFilter.BackColor = SystemColors.ButtonFace;
         btnPriceFilter.Enabled = false;
         btnPriceFilter.Click += SelectPrice;

         //scCenter.Panel1.Controls.Add(btnPriceFilter);
         //scCenter.Panel1.Controls.Add(cbFirmFilter);
         //scCenter.Panel1.Controls.Add(cbUsePriceFilter);

         btnSave = new Button();
         btnSave.Size = new System.Drawing.Size(80, 23);
         btnSave.Text = "Сохранить";
         btnSave.Name = "btnSave";
         cx = panel3.Right - btnSave.Width - 2;
         btnSave.Location = new System.Drawing.Point(cx, 0);
         btnSave.Anchor = AnchorStyles.Top | AnchorStyles.Right;
         btnSave.Click += btnSave_Click;
         btnSave.Visible = false;
         btnSave.BackColor = SystemColors.ButtonFace;

         cbFirms = new ComboBox();
         cbFirms.Size = new System.Drawing.Size(200, 20);
         cx -= (cbFirms.Width + 2);
         cbFirms.Location = new System.Drawing.Point(cx, 2);
         cbFirms.Anchor = AnchorStyles.Top | AnchorStyles.Right | AnchorStyles.Left;
         cbFirms.Name = "cbFirms";
         cbFirms.MaxDropDownItems = 3;
         cbFirms.ItemHeight = 14;
         cbFirms.Visible = false;

         dlvDate.Size = new System.Drawing.Size(90, 20);
         dlvDate.Format = DateTimePickerFormat.Short;
         cx -= (dlvDate.Width + 2);
         dlvDate.Location = new System.Drawing.Point(cx, 2);
         dlvDate.Name = "dlvDate";
         dlvDate.Visible = false;

         //panel3.Controls.Add(btnSave);
         //panel3.Controls.Add(cbFirms);
         //panel3.Controls.Add(dlvDate);

         DataGridViewCellStyle styleInt = new DataGridViewCellStyle();
         styleInt.Format = "N0";

         this.clmnPackQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnPackQty.DataPropertyName = "PackQty";
         this.clmnPackQty.HeaderText = "Упак.";
         this.clmnPackQty.Name = "dgvOrderItemsQty";
         clmnPackQty.DefaultCellStyle = styleInt;

         DataGridViewTextBoxColumn clmn = new DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "State";
         clmn.HeaderText = "Сост.";
         clmn.Name = "Cell";
         clmn.Width = 70;
         dgvOrderItems.Columns.Insert(dgvOrderItemsItem.DisplayIndex+1, clmn);

         dgvOrderItems.Columns.Insert(dgvOrderItemsQty.DisplayIndex, clmnPackQty);


         clmn = new DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "RouteStepKind";
         clmn.HeaderText = "";
         clmn.Name = "RouteStepKind";
         clmn.Width = 50;
         dgvDetail.Columns.Insert(dgvDetailColumnOrg.DisplayIndex + 1, clmn);

         clmn = new DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "ShortFirmName";
         clmn.HeaderText = "Произв.";
         clmn.Name = "ShortFirmName";
         clmn.Width = 70;
         dgvDetail.Columns.Insert(dgvDetailColumnOrg.DisplayIndex + 1, clmn);

         //dgvDetail.ContextMenuStrip = null;

         ToolStripMenuItem ti = new ToolStripMenuItem("Рабочее время");
         ti.Click += (o, e) => { WorkTimeReport.Do(GetDateForStartPeriod(), dtpEnd.Value.Date, this); }; //, GetSelectedIdAgent()); };
         //tsReportMenu.DropDownItems.Add(ti);

         //ti = new ToolStripMenuItem("Дистрибуция");
         //ti.Click += (o, e) => { FmDistribReport.Do(GetDateForStartPeriod(), dtpEnd.Value.Date, this, GetSelectedAgent()); };
         //tsReportMenu.DropDownItems.Add(ti);

         distribDetail.Visible = false;
         detailPanel.Controls.Add(distribDetail);
         distribDetail.Dock = DockStyle.Fill;

         rritems.Location = new Point();
         rritems.Size = new Size(20, 20);
         rritems.Visible = false;
         rritems.Dock = DockStyle.Fill;
         detailPanel.Controls.Add(rritems);

         raov.Location = new Point();
         raov.Size = new Size(20, 20);
         raov.Visible = false;
         raov.Dock = DockStyle.Fill;
         detailPanel.Controls.Add(raov);

         dsUserLog = (DataSet<int, UserLog>)DataModule.Get(UserLog.OBJECT_NAME) ?? new DataSet<int, UserLog>(UserLog.OBJECT_NAME);

         //documents.Add(new DocumentInfo(distrib, ObjType.TObjType.OrgDistrib));
         documents.Add(new DocumentInfo(rreq, ObjType.TObjType.ReturnRequest));
         documents.Add(new DocumentInfo(rejectActs, ObjType.TObjType.RejectAct));
      }

#if !HTTP_SERVER
      SimpleDataSet<Visit> ackPhoto;
      bool readPhotos = false;
      List<string> ackedPhotos = new List<string>();
      List<Visit> photosCache = new List<Visit>();


      void RefreshPhotos(DateTime dt, String id, String key)
      {
         readPhotos = true;
         ackedPhotos.Add(key);

         string FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" <= ToDate('{2:dd/MM/yyyy} 23:59:59') and \"id\"='{3}'";
         ackPhoto = new SimpleDataSet<Visit>(Visit.OBJECT_NAME, false);
         ackPhoto.Filter = String.Format(FILTER_STR, "date", dt, dt, id);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(ackPhoto);

         FmWait.StdDataRefresh(this, upd, UpdatePhotos);
      }

      void UpdatePhotos()
      {
         foreach (Visit v in ackPhoto.Data)
            photosCache.Add(v);

         readPhotos = false;

         if (dgvDetail.CurrentRow != null)
         {
            OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
            if (odr != null)
               ShowCorrespondingPhoto(odr.DateCreatedDT, odr);
         }
      }

      protected override void ShowCorrespondingPhoto(DateTime date, OrderDetailRepresentation o)
      {
         if (readPhotos)
            return;

         String id = date.Date.ToString() + o.NOrg.id;
         if (ackedPhotos.Contains(id) == false)
         {
            RefreshPhotos(date, o.NOrg.id, id);
            return;
         }

         Visit v = o.StoreObject as Visit;
         if (v != null)
         {
            foreach (Visit rv in photosCache)
            {
               if (rv.created == v.created && rv.id == v.id)
               {
                  AddVisitPhotos(rv);
                  break;
               }
            }
         }
         else
         {
            lvPhoto.Items.Clear();
            imPhoto.Images.Clear();
            List<Image> photos = new List<Image>();
            int pCounter = 0;

            foreach (Visit vis in photosCache)
            {
               if (IsSameDate(vis.date, date) && vis.org.id == o.NOrg.id)
                  AddVisitPhotos(vis, photos, pCounter, out pCounter);
            }
         }
         //base.ShowCorrespondingPhoto(date, o);
      }

      protected override bool IsVisitItem(ScriptDocItem i)
      {
         bool ret = base.IsVisitItem(i);
         if (ret)
         {
            Visit v = i.Document as Visit;
            foreach (Visit rv in photosCache)
            {
               if (rv.created == v.created && rv.id == v.id)
               {
                  i.Document = v;
                  break;
               }
            }
         }
         return ret;
      }

#endif

      protected override bool CanDuplicate(Network.DataObject dataObject)
      {
         return dataObject is ReturnRequest;
      }

      protected override IDataSet GetDuplicate(Network.DataObject dataObject)
      {
         if (dataObject is ReturnRequest)
         {
            SimpleDataSet<ReturnRequest> ord = new SimpleDataSet<ReturnRequest>(ReturnRequest.OBJECT_NAME, false);
            ord.Add((ReturnRequest)dataObject);
            return ord;
         }

         return null;
      }

      void SelectPrice(object sender, EventArgs e)
      {
         Price p;
         if (FmSelectSKUEx.SkuQuery(this, out p) == DialogResult.OK)
         {
            OrdersDetailEx details = oDetail as OrdersDetailEx;
            details.PriceFilterId = p.id;

            ReloadData();
         }
      }

      void SwitchFilter(object sender, EventArgs e)
      {
         usePriceFilter = !usePriceFilter;
         btnPriceFilter.Enabled = usePriceFilter;
         if (!usePriceFilter)
         {
            OrdersDetailEx details = oDetail as OrdersDetailEx;
            details.PriceFilterId = "";

            ReloadData();
         }
      }
	  
	  internal override OrdersDetail CreateOrderDetail() { return new OrdersDetailEx(documents, orderSended, returnSended); }

      protected override void SetOrderItems(Order o)
      {
         int row = 0;
         int clmn = dgvOrderItemsQty.DisplayIndex;

         List<OrderItemEx> loi = new List<OrderItemEx>();
         if (o != null)
            foreach (OrderItem src in o.items)
            {
               loi.Add(new OrderItemEx(src, dgvOrderItems, row++, clmn, null));
            }

         dgvOrderItems.DataSource = loi;
      }

      void btnSave_Click(object sender, EventArgs e)
      {
         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
         if (dr == DialogResult.Yes)
         {
            String firmId = ((Factory)cbFirms.SelectedItem).id;
            Order o = (dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation).StoreObject as Order;
            o.firmCode = ((Factory)cbFirms.SelectedItem).id;
            o.firma = ((Factory)cbFirms.SelectedItem).name;
            o.modify = DateTime.Now;
            o.dlvDate = dlvDate.Value;

            SimpleDataSet<Order> ord = new SimpleDataSet<Order>(Order.OBJECT_NAME, false, true);
            ord.Add(o);

            List<IDataSet> wr = new List<IDataSet>();
            wr.Add(ord);

            if (DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection(), GetSelectedIdAgent()) == false)
               MessageBox.Show("Ошибка при сохранении заявки");
            else
            {
               DailyAgentPlans.RefreshOpened();
               //MessageBox.Show("Заявка успешно сохранена");
            }
         }
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         updSets.Add(dogovors);
         IDataSet firms = DataModule.Get(Factory.OBJECT_NAME);
         if (firms != null)
            updSets.Add(firms);

         if (dsPriceTypes.Count == 0)
            updSets.Add(dsPriceTypes);

         String docFilter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         //distrib.Filter = docFilter;
         //updSets.Add(distrib);

         rreq.Filter = docFilter;
         updSets.Add(rreq);
         updSets.Add(reasons);

         rejectActs.Filter = docFilter;
         updSets.Add(rejectActs);

         bundles.Filter = docFilter;
         updSets.Add(bundles);

         dsUserLog.Filter = String.Format("\"date\" >= ToDate('{0:dd/MM/yyyy}') and \"userid\"='{1}' and (\"objType\"='Order' or \"objType\"='ReturnRequest')", 
            dateBegin, agentID);
         updSets.Add(dsUserLog);

         dogCache.Clear();

         if(GetSelectedAgent().isDsp >0)
         {
            if(dsOrg.Name !=Org.COMMON_OBJECT_NAME)
            {
               DataSet<string, Org> dsc = DataModule.Get(Org.COMMON_OBJECT_NAME) as DataSet<string, Org>;
               if (dsc == null) dsc = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, true);
               updSets.Remove(dsOrg);
               dsOrg = dsc;
               if (dsOrg.Count == 0)
                  updSets.Insert(0, dsOrg);
            }
         }

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

      protected override void AfterRefreshData()
      {
         orderSended.Clear();
         returnSended.Clear();
         foreach(UserLog ul in dsUserLog.Data)
         {
            List<DateTime> sended = null;
            if(ul.objType == "ReturnRequest")
            {
               if(!returnSended.TryGetValue(ul.objDate, out sended))
               {
                  sended = new List<DateTime>();
                  returnSended.Add(ul.objDate, sended);
               }
            }
            else if (ul.objType == "Order")
            {
               if(!orderSended.TryGetValue(ul.objDate, out sended))
               {
                  sended = new List<DateTime>();
                  orderSended.Add(ul.objDate, sended);
               }
            }
            if (sended != null)
               sended.Add(ul.date);
         }
         Invoke(new EmptyParamHandler(InitFilter));
      }

      void InitFilter()
      {
         cbFirmFilter.SuspendLayout();
         cbFirmFilter.Items.Clear();
         cbFirmFilter.Items.Add(ALL_FIRMS_TEXT);
         Factory.GetFactories().ForEach((x) =>
         {
            cbFirmFilter.Items.Add(x);
         });
         cbFirmFilter.SelectedIndex = 0;
         cbFirmFilter.ResumeLayout();
      }

      void cbFirmFilter_SelectedIndexChanged(object sender, EventArgs e)
      {
         OrdersDetailEx details = oDetail as OrdersDetailEx;
         if (details != null)
         {
            details.FirmID = cbFirmFilter.SelectedItem is Factory ? ((Factory)cbFirmFilter.SelectedItem).id : null;
         }
         ReloadData();
      }

      bool HaveFirm(String ido, String firmId)
      {
         if (dogCache.Count == 0)
         {
            foreach (OrgDogovor od in dogovors.Data)
            {
               string value = od.firm + "|";
               if (dogCache.ContainsKey(od.ido))
                  dogCache[od.ido] += value;
               else
                  dogCache.Add(od.ido, value);
            }
         }

         if (dogCache.ContainsKey(ido))
            return dogCache[ido].Contains(firmId);
         return false;
      }

      FmDetail.DocView bundleView = null, rreqView = null, rejectActView = null;
      protected override FmDetail.DocView GetDocView(string docType)
      {
         if (docType == OrderBundle.OBJECT_NAME)
         {
            if (bundleView == null)
               bundleView = new DocView(OrderBundle.OBJECT_NAME, "Заявка", typeof(OrderBundleOverview));
            return bundleView;
         }
         else if (docType == ReturnRequest.OBJECT_NAME)
         {
            if(rreqView == null)
               rreqView = new DocView(ReturnRequest.OBJECT_NAME, "Возврат", typeof(ReturnReqOverview));
            return rreqView;
         }
         else if (docType == RejectAct.OBJECT_NAME)
         {
            if (rejectActView == null)
               rejectActView = new DocView(RejectAct.OBJECT_NAME, "Актирование", typeof(RejectActOverview));
            return rejectActView;
         }
         return base.GetDocView(docType);
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         distribDetail.Visible = false;
         rritems.Visible = false;
         raov.Visible = false;

         DistribOrg dd = odr.StoreObject as DistribOrg;
         if (dd != null)
         {
            distribDetail.SetData(dd);
            distribDetail.Visible = true;
            return distribDetail;
         } else
         {
            ReturnRequest rr = odr.StoreObject as ReturnRequest;
            if(rr != null)
            {
               rritems.SetDocument(rr);
               return rritems;
            }
            RejectAct ra = odr.StoreObject as RejectAct;
            if(ra != null)
            {
               raov.SetData(ra);
               return raov;
            }
         }
         return null;
      }

      protected override void UpdateDetailTable(System.Windows.Forms.DataGridViewRow curRow)
      {
         base.UpdateDetailTable(curRow);

         if (IsScriptMode)
            return;

         bool visible = false;
         if (curRow != null)
         {
            OrderDetailRepresentation ord = curRow.DataBoundItem as OrderDetailRepresentation;
            Order order = ord.StoreObject as Order;
            if (order != null)
            {
               String ido = null;
               if( dsOrg.ContainsKey(order.id) )
                  ido = dsOrg[order.id].ido;

               cbFirms.Items.Clear();

               if (ido != null)
               {
                  Factory.GetFactories().ForEach((x) =>
                  {
                     if (HaveFirm(ido, x.id))
                        cbFirms.Items.Add(x);
                  });

                  foreach (Factory f in cbFirms.Items)
                  {
                     if (f.id == order.firmCode)
                     {
                        cbFirms.SelectedItem = f;
                        break;
                     }
                  }

                  dlvDate.Value = order.dlvDate;
                  visible = true;
               } 
            }
         }
         cbFirms.Visible = visible;
         btnSave.Visible = visible;
         dlvDate.Visible = visible;
      }
   }

   class OrdersDetailEx : ScriptDetail
   {
      Dictionary<DateTime, List<DateTime>> orderSended ;
      Dictionary<DateTime, List<DateTime>> returnSended;

      public OrdersDetailEx(List<DocumentInfo> documents, Dictionary<DateTime, List<DateTime>> orderSended, Dictionary<DateTime, List<DateTime>> returnSended)
         : base(documents) 
      {
         this.orderSended = orderSended;
         this.returnSended = returnSended;
      }

      private string firmId = null;
      public string FirmID { set { firmId = (value == FmDetailEx.ALL_FIRMS_TEXT) ? null : value; } }

      string priceFilterId = "";
      public string PriceFilterId { get { return priceFilterId; } set { priceFilterId = value; } }

      protected override OrderDetailRepresentation CreateOrderRow(Order order, bool oneDay)
      {
         List<DateTime> sends = null;
         orderSended.TryGetValue(order.created, out sends);
         return new OrderDetailRepresentationEx(order, new ObjType(ObjType.TObjType.OtOrder), oneDay, sends);
      }

      protected override OrderDetailRepresentation CreateDocRepr(BaseDocument doc, ObjType.TObjType docType, bool oneDay)
      {
         if (docType == ObjType.TObjType.ReturnRequest)
         {
            List<DateTime> sends = null;
            returnSended.TryGetValue(doc.created, out sends);
            return new OrderDetailRepresentationEx(doc, new ObjType(docType), oneDay, sends);
         }
         return base.CreateDocRepr(doc, docType, oneDay);
      }

      protected override void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
      {
         if (firmId == null && priceFilterId.Length == 0 )
         {
            base.LoadInt(cond, oneDay, checkRoute, agentID, routes);
         }
         else
         {
            IDataSet cdata = DataModule.Get("Order");
            CheckFiltersForDocType(cdata, ObjType.TObjType.OtOrder, filtersAvailable);

            if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtOrder) : true && cdata != null)
            {
               foreach (Order order in cdata.Data)
               {
                  if (priceFilterId.Length > 0 && order.HaveItem(priceFilterId) == false)
                     continue;

                  if ( (firmId != null && order.firmCode != firmId) ||
                     (checkRoute && FmDetailBase.IsCreatedBySelectedAgentRoute(order.org, agentID, order.created)))
                     continue;

                  docCount++;
                  sum += order.DSum;
                  weight += order.Weight;

                  Add(CreateOrderRow(order, oneDay));
                  //Add(new OrderDetailRepresentation(order.Created,
                  //   new ObjType(ObjType.TObjType.OtOrder),
                  //   order.Date, order.Sended, order.org,
                  //   order.DSum, 0, order.Qty, order, oneDay,
                  //   order.remark));
               }
            }
         }
      }
   }

   class OrderDetailRepresentationEx : OrderDetailRepresentation
   {
      List<DateTime> sendedList;
      public OrderDetailRepresentationEx(BaseDocument doc, ObjType doctype, bool oneDay, List<DateTime> sended) :
         base(doc, doctype, oneDay)
      {
         this.sendedList = sended;
         if (sendedList != null)
         {
            bool find = false;
            foreach (DateTime s in sendedList)
            {
               if(s == doc.sended)
               {
                  find = true;
                  break;
               }
            }
            if(!find)
               sendedList.Add(doc.sended);
            sendedList.Sort();
         }
      }

      public override string Sended
      {
         get
         {
            if(sendedList == null || sendedList.Count == 0)
               return base.Sended;
            String snd = "";
            foreach (DateTime dt in sendedList)
            {
               snd += dt.ToString("dd/MM/yyyy HH:mm") + ",";
            }
            return snd.Substring(0, snd.Length-1);
         }
      }
   }
}
