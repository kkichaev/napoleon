using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmGoodsValues : Form
   {
      DataSet<string, GoodsValues> dsGoodsValues = new DataSet<string, GoodsValues>(GoodsValues.OBJECT_NAME, false);
      private DataSet<string, Price> dsPrice;
      private DataSet<string, GoodsMatrix> dsGoodsMatrix;
      bool clearing = false;
      List<RowData> allRows = new List<RowData>();
      DataSet<string, Org> orgs = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME);


      public FmGoodsValues()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;
         dgvOrgs.AutoGenerateColumns = false;

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsGoodsMatrix = (DataSet<string, GoodsMatrix>)DataModule.Get(GoodsMatrix.OBJECT_NAME) ?? new DataSet<string, GoodsMatrix>(GoodsMatrix.OBJECT_NAME);
         orgs = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();

         upd.Add(orgs);

         if (dsPrice.Filter != Price.GOODS_FILTER || dsPrice.Count == 0)
         {
            dsPrice.Filter = Price.GOODS_FILTER;
            upd.Add(dsPrice);
         }

         if (dsGoodsMatrix.Count == 0)
         {
            dsGoodsMatrix.Filter = "\"name\" is null or \"name\" is not null";
            upd.Add(dsGoodsMatrix);
         }

         upd.Add(dsGoodsValues);
         FmWait.StdDataRefresh(this, upd, DoLoadData, null);
      }

      void DoLoadData()
      {
         DataSet<string, ManagerFolder> dsFolders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ?? 
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);

         allRows.Clear();

         List<Org> oSrc = new List<Org>((IEnumerable<Org>)orgs.Data);
         //Org empty = new Org();
         //empty.id = "";
         //empty.name = "<Общие значения>";
         //oSrc.Add(empty);

         foreach(Org o in oSrc)
         {
            if (dsGoodsValues.ContainsKey(o.id))
            {
               GoodsValues data = dsGoodsValues[o.id];
               if (data.org != null)
                  allRows.Add(new RowData(data));
            }
            else
               allRows.Add(new RowData(new GoodsValues(o)));
         }

         allRows.Sort();
         dgvOrgs.DataSource = new SortableBindingList<RowData>(allRows);
      }

      class RowData : IComparable<RowData>
      {
         GoodsValues data;

         public RowData(GoodsValues data)
         {
            this.data = data;
         }

         public int CompareTo(RowData other)
         {
            return data.org.CompareTo(other.data.org);
         }

         public List<GoodsValues.Item> Items { get { return data.items; } }

         public string Name { get { return data.org.Name; } }
         public string Address { get { return data.org.Address; } }
         //public string ID { get { return data.org.id; } }
         public string Matrix { get { return data.org.goodsMatrix; } }

         public GoodsValues Data { get { return data; } }
      }

      private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         RowData rd = dgvOrgs.Rows[e.RowIndex].DataBoundItem as RowData;
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
         dgvItems.DataSource = new SortableBindingList<GoodsValues.Item>(rd.Items);
      }

      private bool SaveChanges(bool showDialog)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         List<ReplacedSet> rpl = new List<ReplacedSet>();
         SimpleDataSet<GoodsValues> gv = new SimpleDataSet<GoodsValues>(GoodsValues.OBJECT_NAME, false);

         foreach(RowData rd in allRows)
         {
            if (rd.Items.Count > 0)
               gv.Add(rd.Data);
         }

         ReplacedSet rs = new ReplacedSet(gv);
         rpl.Add(rs);
         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         SaveChanges(true);
      }

      private void tsbDel_Click(object sender, EventArgs e)
      {
         if(dgvOrgs.CurrentRow == null)
            return;

         RowData rd = dgvOrgs.CurrentRow.DataBoundItem as RowData;
         rd.Items.Clear();
         dgvItems.DataSource = null;
         dgvItems.DataSource = rd.Items;
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         if (dgvOrgs.CurrentRow == null)
            return;

         List<Price> price = new List<Price>();
         RowData rd = dgvOrgs.CurrentRow.DataBoundItem as RowData;
         if(dsGoodsMatrix.ContainsKey(rd.Matrix) == false)
         {
            foreach (Price p in dsPrice.Data)
            {
               if (p.my == 1)
                  price.Add(p);
            }
         }
         else
         {
            GoodsMatrix gm = dsGoodsMatrix[rd.Matrix];
            foreach (MatrixItem i in gm.items)
               if (i.price != null && i.price.my == 1)
                  price.Add(i.price);
         }

         List<GoodsValues.Item> removed = new List<GoodsValues.Item>();
         foreach (GoodsValues.Item i in rd.Items)
         {
            if (price.Contains(i.price))
               price.Remove(i.price);
            else
               removed.Add(i);
         }

         removed.ForEach(x => rd.Items.Remove(x));
         foreach (Price p in price)
         {
            GoodsValues.Item item = new GoodsValues.Item();
            item.id = p.id;
            item.price = p;

            rd.Items.Add(item);
         }

         dgvItems.DataSource = null;
         dgvItems.DataSource = rd.Items;
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            btnClearFind_Click(sender, e);
      }

      private void btnClearFind_Click(object sender, EventArgs e)
      {
         timer1.Stop();
         clearing = true;
         tbFind.Clear();

         dgvOrgs.DataSource = new SortableBindingList<RowData>(allRows);

         clearing = false;
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string p)
      {
         p = p.ToUpper();

         List<RowData> src = new List<RowData>();
         foreach (RowData mrd in allRows)
         {
            if (mrd.Name.ToUpper().Contains(p) || mrd.Address.ToUpper().Contains(p))
               src.Add(mrd);
         }

         dgvOrgs.DataSource = new SortableBindingList<RowData>(src);
      }
   }
}
