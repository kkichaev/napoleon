using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;

namespace GRSoft.Ads
{
   public partial class FmOrderMap : Form
   {
      private Client client;
      private DsSchedule dsSchedule;
      private DsBrigade dsBrigade;
      private List<BrigadeInfo> brigadeInfoList = new List<BrigadeInfo>();
      private Comparison<BrigadeInfo> bilComparer = new Comparison<BrigadeInfo>(
         delegate(BrigadeInfo b1, BrigadeInfo b2) { return b1.distance.CompareTo(b2.distance); });
      private DsOrderRcv dsOrder;

      public FmOrderMap(Client client, DateTime date)
      {
         InitializeComponent();
         this.client = client;
         dtpDate.Value = date.Date;
         dsSchedule = new DsSchedule(false);
         dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
         dsOrder = (DsOrderRcv)DataModule.Get(Order.OBJECT_NAME) ?? new DsOrderRcv(true);
      }

      private void FmOrderMap_Load(object sender, EventArgs e)
      {
         DateTime startTime = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, dtpDate.Value.Day, 8, 0, 0);
         DateTime endTime = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, dtpDate.Value.Day, 20, 0, 0);   

         while (startTime <= endTime)
         {
            ListViewItem lvi = new ListViewItem(startTime.ToShortTimeString());
            lvi.Tag = startTime;
            lvDetail.Items.Add(lvi);

            for(int i = 0; i < lvDetail.Columns.Count; i++)
               lvi.SubItems.Add("");

            startTime = startTime.AddMinutes(FmMain.INTERVAL_IN_MIN);
         }

         btnRefresh_Click(null, null);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         dsSchedule.Filter = string.Format("date = ToDate('{0}')", dtpDate.Value);
         DateTime end = dtpDate.Value.AddDays(1);
         dsOrder.Filter = string.Format("planbegin >= ToDate('{0}') and planbegin < ToDate('{1}')",
            dtpDate.Value, end);

         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(dsSchedule);
         updSet.Add(dsBrigade);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
               DataModule_OnDataResponceError);
            FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSet, FmWait.ProgressIndicator));
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      private Comparison<OrderInfo> orderCmp = 
         new Comparison<OrderInfo>(delegate(OrderInfo o1, OrderInfo o2) 
            { return o1.order.planbegin.CompareTo(o2.order.planbegin); });

      private void RefreshData()
      {
         foreach (ListViewItem it in lvDetail.Items)
         {
            DateTime dt = (DateTime)it.Tag;
            it.Tag = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month,
               dtpDate.Value.Day, dt.Hour, dt.Minute, 0);
         }

         Location clientLocation = FmBrigadeAddress.GetLocation(client.Address.Replace("Респ.", ""));

         if (clientLocation != null)
         {
            brigadeInfoList.Clear();

            foreach (Schedule sc in dsSchedule.Data)
            {
               if (sc.longitude != 0 && sc.longitude != 0)
               {
                  List<OrderInfo> orderList = new List<OrderInfo>();

                  foreach (Order o in dsOrder.Data)
                  {
                     if (o.brigade != null && o.brigade.Equals(sc.brigade) && o.client != null)
                     {
                        Location orderLocation = FmBrigadeAddress.GetLocation(o.client.Address);

                        if (orderLocation != null)
                           orderList.Add(new OrderInfo(o, orderLocation));
                     }
                  }
                  orderList.Sort(orderCmp);

                  double d = Coordutils.Distance(clientLocation.Latitude, clientLocation.Longitude,
                     sc.latitude, sc.longitude);
                  BrigadeInfo bi = new BrigadeInfo(sc.brigade, d,
                     new Location(sc.latitude, sc.longitude),
                     orderList);
                  brigadeInfoList.Add(bi);
               }
            }

            brigadeInfoList.Sort(bilComparer);

            Color[] colors = new Color[] {Color.Green, Color.Yellow, Color.Red };

            for (int i = 0; i < colors.Length; i++)
            {
               if (brigadeInfoList.Count > i)
               {
                  brigadeInfoList[i].color = colors[i];
                  lvDetail.Columns[i+1].Text = brigadeInfoList[i].brigade.Name;
                  lvDetail.Columns[i + 1].Tag = brigadeInfoList[i];
               }
               else
                  break;
            }
         }

         string text = MapEngine.OrderMap(Config.GetConfig().mapSource, brigadeInfoList, 
            new ClientInfo(client, clientLocation));
         text = text.Replace("\r", " ");
         text = text.Replace("\n", " ");
         //File.WriteAllText("ordermap.html", text);
         wb.DocumentText = text;

         lvDetail.Refresh();
      }

     // private SolidBrush redBrush = new SolidBrush(Color.Red); 
      private void lvDetail_DrawSubItem(object sender, DrawListViewSubItemEventArgs e)
      {
         if(e != null)
         {
            e.DrawBackground();
            ColumnHeader ch = e.Header;

            if (ch.Index > 0)
            {
               ListViewItem item = e.Item;
               BrigadeInfo b = ch.Tag as BrigadeInfo;
               DateTime dt = (DateTime)item.Tag;

               if (b != null)
               {
                  Order o = FmMain.GetOrder(dsOrder, b.brigade, dt);

                  if (o != null)
                  {
                     e.Graphics.FillRectangle(new SolidBrush(b.color), e.Bounds);

                     int index = 1;

                     foreach (OrderRcv ord in dsOrder.Data)
                     {
                        if (ord.Equals(o))
                           break;
                        else if (ord.brigade.Equals(o.brigade))
                           index++;
                     }
                     Rectangle bounds = e.Bounds;
                     bounds.X = bounds.X + bounds.Width / 2 - 2;
                     e.Graphics.DrawString(index.ToString(), 
                        lvDetail.Font, new SolidBrush(Color.Blue), bounds);
                  }
               }
            }

            e.DrawText();
         }
      }

      private void lvDetail_DrawColumnHeader(object sender, DrawListViewColumnHeaderEventArgs e)
      {
         e.DrawBackground();
         e.DrawText();
      }
   }

   public class BrigadeInfo
   {
      public Brigade brigade;
      public double distance;
      public Location location;
      public List<OrderInfo> order;
      public Color color;

      public BrigadeInfo(Brigade brigade, 
         double distance, Location location, 
         List<OrderInfo> order)
      {
         this.brigade = brigade;
         this.distance = distance;
         this.location = location;
         this.order = order;
         this.color = Color.Blue;
      }
   }

   public class ClientInfo
   {
      public Client client;
      public Location location;

      public ClientInfo(Client client, Location location)
      {
         this.client = client;
         this.location = location;
      }
   }

   public class OrderInfo
   {
      public Order order;
      public Location location;

      public OrderInfo(Order order, Location location)
      {
         this.order = order;
         this.location = location;
      }
   }
}
