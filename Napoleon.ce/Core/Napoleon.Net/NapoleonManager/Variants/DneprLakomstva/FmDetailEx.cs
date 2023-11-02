using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Globalization;
using System.Drawing;

namespace GRSoft.NapoleonManager
{ 
[System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
  public  class FmDetailEx : FmDetail
   {
      SimpleDataSet<DocsDlvResult> deliveryDocs = new SimpleDataSet<DocsDlvResult>(DocsDlvResult.OBJECT_NAME, false);
      SimpleDataSet<DocsPayResult> incassDocs = new SimpleDataSet<DocsPayResult>(DocsPayResult.OBJECT_NAME, false);
      DataSet<DateTime, GPSPos> dsGPSPos;

      //Font italic = null;
      Font boldFont;
      ToolStripComboBox cbFilterKind;

      DataGridViewLinkColumn linkColumn;
      FmAddrShow fmInstance = null;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {

         dsGPSPos = DataModule.Get("GPSPos") == null ? new DataSet<DateTime, GPSPos>("GPSPos") : (DataSet<DateTime, GPSPos>)DataModule.Get("GPSPos");

         DataGridViewTextBoxColumn clmn;

         clmn = new DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "DlvQty";
         clmn.FillWeight = 35F;
         clmn.HeaderText = "Кол-во 1с";
         clmn.Name = "DlvQty";
         clmn.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         dgvOrderItems.Columns.Insert(2, clmn);

         tslFilter.Visible = false;
         cbFilterKind = new ToolStripComboBox();
         cbFilterKind.Size = new Size(tslFilter.Width + 10, tslFilter.Height);
         cbFilterKind.Alignment = ToolStripItemAlignment.Right;
         cbFilterKind.Margin = new Padding(tslFilter.Margin.Left, tslFilter.Margin.Top, tslFilter.Margin.Right-10, tslFilter.Margin.Bottom);

         cbFilterKind.Items.AddRange(new string[] { "Фильтр", "Маршрут" });
         cbFilterKind.SelectedIndex = 0;
         toolStrip1.Items.Insert(toolStrip1.Items.IndexOf(tslFilter), cbFilterKind);

         cbFilterKind.SelectedIndexChanged += cbFilterKind_SelectedIndexChanged;
         ((OrdersDetailEx)oDetail).SetFilter(cbFilterKind);

         dgvDetailColumnNumber.Visible = true;

         linkColumn = new DataGridViewLinkColumn();
         linkColumn.SortMode = DataGridViewColumnSortMode.Automatic;
         linkColumn.ActiveLinkColor = linkColumn.LinkColor;
         linkColumn.VisitedLinkColor = linkColumn.LinkColor;
         linkColumn.DataPropertyName = "DocDistance";
         linkColumn.HeaderText = "Коорд.";
         linkColumn.Name = "linkColumn";
         dgvDetail.Columns.Insert(dgvDetail.Columns.IndexOf(clmnRouteOrder) + 1, linkColumn);
         dgvDetail.CellContentClick += dgvDetail_CellContentClick;

         DataGridViewTextBoxColumn clmnCb = new DataGridViewTextBoxColumn();
         clmnCb.SortMode = DataGridViewColumnSortMode.Automatic;
         clmnCb.DataPropertyName = "Category";
         clmnCb.HeaderText = "Категория";
         clmnCb.Name = "linkColumn";
         dgvDetail.Columns.Insert(dgvDetail.Columns.IndexOf(dgvDetailColumnOrg) + 1, clmnCb);

         Width += 50;
      }

      void dgvDetail_CellContentClick(object sender, DataGridViewCellEventArgs e)
      {
         if( e.ColumnIndex == linkColumn.DisplayIndex && e.RowIndex >= 0 && e.RowIndex < dgvDetail.Rows.Count)
         {
            OrderDetailRepresentation odr = dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation;
            string text = Maps.MapEngine.ShowDocLocation(odr.DocLocation, odr.OrgLocation);
            if (fmInstance == null)
            {
               fmInstance = new FmAddrShow();
               fmInstance.FormClosed += (x, y) => { fmInstance = null; };
               fmInstance.Show();
            }

            fmInstance.ShowMap(text);
         }
      }

      void cbFilterKind_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (cbFilter.SelectedIndex != 0)
            UpdateGrid(false);
      }

      protected override void UpdateDetailTable(DataGridViewRow curRow)
      {
         label6.Text = "Содержание";
         //OrderDetailRepresentation odr = curRow.DataBoundItem as OrderDetailRepresentation;
         //if( odr != null )
         //{
         //   BaseDocument bd = odr.StoreObject as BaseDocument;
         //   llDocLocation.SetLocation(bd);
         //}
         base.UpdateDetailTable(curRow);
      }


      protected override void SetOrderItems(Order o)
      {
         List<OrderItem> loi = new List<OrderItem>();
         loi.Add(new OrderItemTotalEx(o.items));
         loi.AddRange(o.items);
         dgvOrderItems.DataSource = loi;
         dgvOrderItems.ClearSelection();
         dgvOrderItems.Rows[0].Frozen = true;

         String text = "Содержание ";
         if (o.dlvNumber.Length > 0)
            text += "накладная № " + o.dlvNumber;
         else
            text += "нет накладной";
         label6.Text = text;
      }

      protected override void MakePKOText(RichTextBox tb, object p)
      {
         Incass doc = p as Incass;
         if(doc != null)
         {
            String text="Номер\t" + doc.incassNumber + "\r\nСумма\t" + doc.incassSum.ToString("C", Config.GetCultureInfo()) + "\r\n";
            tb.Text = text;
            if (doc.refDoc != null)
            {
               text = "ПКО из заказа";
               tb.AppendText(text);
            }
         } else
            base.MakePKOText(tb, p);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" <= ToDate('{2:dd/MM/yyyy} 23:59:59') and \"userid\" = '{3}'";
         dsGPSPos.Filter = string.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);
         updSets.Add(dsGPSPos);

         GetDocsReportParam param = new GetDocsReportParam();
         param.start = dateBegin.Date;
         param.end = dateEnd.Date.AddDays(1);
         param.detailed = 1;
         param.users.Add(GetSelectedAgent());

         List<IDataSet> res = new List<IDataSet>(new IDataSet[] { deliveryDocs, incassDocs });
         Report rpt = new Report("get_docs", Report.CreateSimplDataSet(param), res);
         updSets.Add(rpt);
      }

      Dictionary<DateTime, BaseDocument> PutDocs(IDataSet docs)
      {
         Dictionary<DateTime, BaseDocument> ret = new Dictionary<DateTime,BaseDocument>();
         foreach(BaseDocument bd in docs.Data)
            ret[bd.created] = bd;

         return ret;
      }

      protected override void AfterRefreshData()
      {
         Dictionary<DateTime, BaseDocument> orders = UpdateOrders();
         UpdateIncass(orders);
      }

      protected override void CellFormatting(DataGridViewCellFormattingEventArgs e)
      {
         base.CellFormatting(e);

         OrderDetailRepresentation odr = dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation;
         if (e.ColumnIndex == linkColumn.DisplayIndex)
         {
            Color clr = (odr.DocDistanceDouble < 100) ? Color.Blue :  Color.Red;
            DataGridViewLinkCell cell = ((DataGridViewLinkCell)dgvDetail.Rows[e.RowIndex].Cells[e.ColumnIndex]);
            cell.LinkColor = clr;
            cell.ActiveLinkColor = clr;
            cell.VisitedLinkColor = clr;

            e.FormattingApplied = true;

         }

         object stObj = odr.StoreObject;
         Incass idoc = stObj as Incass;
         if (idoc != null)
         {
            if (idoc.refDoc != null)
            {
               //if (italic == null)
               //   italic = new System.Drawing.Font(e.CellStyle.Font, FontStyle.Italic);
               //e.CellStyle.Font = italic;
               e.CellStyle.BackColor = Color.LightGreen;
            }
            if( idoc.incassNumber.Length == 0 )
            {
               if (boldFont == null)
                  boldFont = new Font(e.CellStyle.Font, FontStyle.Bold);
               e.CellStyle.Font = boldFont;
            }
         } else 
         {
            Order o = stObj as Order;
            if( o != null && o.dlvNumber.Length == 0 )
            {
               if (boldFont == null)
                  boldFont = new Font(e.CellStyle.Font, FontStyle.Bold);
               e.CellStyle.Font = boldFont;
            }
         }
      }

      private void UpdateIncass(Dictionary<DateTime, BaseDocument> orders)
      {
         List<Incass> added = new List<Incass>();
         Dictionary<DateTime, BaseDocument> incass = PutDocs(dsIncass);
         foreach (DocsPayResult dpr in incassDocs.Data)
         {
            Incass doc = null;
            if (dpr.created.Length > 0 && dpr.created != "None")
            {
               try
               {
                  DateTime created = DateTime.ParseExact(dpr.created, "yyyyMMddHHmmss", CultureInfo.InvariantCulture);
                  if (incass.ContainsKey(created))
                  {
                     doc = (Incass)incass[created];
                     doc.sum = dpr.sum;
                     doc.incassNumber = dpr.number;
                     doc.incassSum = dpr.sum;
                     incass.Remove(created);
                  }
                  else if (orders.ContainsKey(created))
                  {
                     Order order = (Order)orders[created];
                     order.incassNumber = dpr.number;
                     order.incassSum = dpr.sum;
                     doc = CreateIncass(dpr);
                     doc.created = order.created;
                     doc.sended = order.sended;
                     doc.refDoc = order;
                     added.Add(doc);
                  }
               }
               catch (Exception)
               {
               }
            }
            if (doc == null)
               added.Add(CreateIncass(dpr));
         }

         foreach (BaseDocument bd in incass.Values)
            ((Incass)bd).sum = 0;

         int ctr = dsIncass.Count;
         foreach (Incass o in added)
            dsIncass.Add(ctr++, o);
      }

      private Dictionary<DateTime, BaseDocument> UpdateOrders()
      {
         Dictionary<DateTime, BaseDocument> orders = PutDocs(dsOrder);
         Dictionary<DateTime, BaseDocument> ret = new Dictionary<DateTime, BaseDocument>(orders);

         List<Order> added = new List<Order>();
         foreach (DocsDlvResult ddr in deliveryDocs.Data)
         {
            Order doc = null;
            if (ddr.created.Length > 0 && ddr.created != "None")
            {
               try
               {
                  DateTime created = DateTime.ParseExact(ddr.created, "yyyyMMddHHmmss", CultureInfo.InvariantCulture);
                  if (orders.ContainsKey(created))
                  {
                     doc = (Order)orders[created];
                     doc.dlvNumber = ddr.number;
                     CheckOrderItems(doc, ddr);
                     orders.Remove(created);
                  }
               }
               catch (Exception)
               {
               }
            }
            if (doc == null)
               added.Add(CreateOrderFromDoc(ddr));
         }

         foreach (BaseDocument bd in orders.Values)
            foreach (OrderItem oi in ((Order)bd).items)
            {
               oi.cost = 0;
               oi.sum = 0;
            }

         int ctr = dsOrder.Count;
         foreach (Order o in added)
            dsOrder.Add(ctr++, o);

         return ret;
      }

      Org FindOrg(string id)
      {
         if (dsOrg.ContainsKey(id))
            return dsOrg[id];

         Org o = Org.GetEmpty(id);
         dsOrg[id] = o;
         return o;
      }

      Price FindPrice(string id)
      {
         if (dsPrice.ContainsKey(id))
            return dsPrice[id];

         Price o = Price.GetEmpty(id);
         dsPrice[id] = o;
         return o;
      }

      private Incass CreateIncass(DocsPayResult dpr)
      {
         Incass doc = new Incass();

         doc.id = dpr.id;
         doc.org = FindOrg(dpr.id);
         doc.incassNumber = dpr.number;
         doc.date = dpr.date;
         doc.created = dpr.date;
         doc.sended = DateTime.MinValue;
         doc.sum = dpr.sum;
         doc.incassSum = dpr.sum;
         doc.userid = dpr.userid;
         doc.agent = Agents.GetDataSet()[dpr.userid];

         return doc;
      }

      private Order CreateOrderFromDoc(DocsDlvResult ddr)
      {
         Order doc = new Order();

         doc.id = ddr.id;
         doc.org = FindOrg(ddr.id);
         doc.incassNumber = ddr.number;
         doc.date = ddr.date;
         doc.created = ddr.date;
         doc.sended = DateTime.MinValue;
         doc.dlvNumber = ddr.number;
         doc.userid = ddr.userid;
         doc.agent = Agents.GetDataSet()[ddr.userid];

         CheckOrderItems(doc, ddr);

         return doc;
      }

      private void CheckOrderItems(Order doc, DocsDlvResult ddr)
      {
         List<DocsDlvResult.Item> items = new List<DocsDlvResult.Item>(ddr.items);
         foreach(OrderItem oi in doc.items)
         {
            bool found = false;
            foreach(DocsDlvResult.Item src in items)
            {
               if( oi.id == src.id)
               {
                  oi.dlvqty = src.qty;
                  oi.sum = src.sum;

                  items.Remove(src);
                  found = true;
                  break;
               }
            }

            if( !found )
            {
               oi.cost = 0;
               oi.sum = 0;
            }
         }

         foreach(DocsDlvResult.Item src in items)
         {
            OrderItem oi = new OrderItem();
            oi.id = src.id;
            oi.item = FindPrice(src.id);
            //oi.qty = src.qty;
            oi.sum = src.sum;
            oi.dlvqty = src.qty;

            doc.items.Add(oi);
         }
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new OrdersDetailEx(documents);
      }
   }

   class OrdersDetailEx : OrdersDetail
   {
      ToolStripComboBox filterKind;
      public OrdersDetailEx(List<DocumentInfo> documents) : base(documents)
      {
      }

      public void SetFilter(ToolStripComboBox filterKind) { this.filterKind = filterKind; }

      protected override bool NeedAddNotVisited(FmDetailData cond, bool checkRoute, List<Org> routes)
      {
         if (filterKind != null && routes != null && filterKind.SelectedIndex == 1 && cond != null && cond.OrderType != null && !cond.OrderType.Equals(ObjType.TObjType.OutRoute))
            return true;
         return base.NeedAddNotVisited(cond, checkRoute, routes);
      }

      protected override void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
      {
         base.LoadInt(cond, oneDay, checkRoute, agentID, routes);
         
         foreach(OrderDetailRepresentation odr in this)
            odr.CheckDocLocation((DataSet<DateTime, GPSPos>)DataModule.Get("GPSPos"));
      }
   }

   public partial class OrderDetailRepresentation : ODRComapartor
   {
      public string Category { get { return nOrg.category + "," + nOrg.segment; } }
   }
}
