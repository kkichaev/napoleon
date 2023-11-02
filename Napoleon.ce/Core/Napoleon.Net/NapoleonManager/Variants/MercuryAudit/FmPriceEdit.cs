using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPriceEdit : Form
   {
      private BindingList<PriceView> data = new BindingList<PriceView>();
      private BindingList<PriceView> filtered = new BindingList<PriceView>();
      private DataSet<string, Price> dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
      private DataSet<string, Price> changed = new DataSet<string, Price>(Price.OBJECT_NAME, false);
      private System.Threading.Timer textWait = null;
      private bool loading = true;

      public FmPriceEdit()
      {
         InitializeComponent();

         grid.AutoGenerateColumns = false;
         grid.DataSource = data;

         cbFilter.SelectedIndex = 0;
      }

      public void SetDirty(Price p)
      {
         if (!changed.ContainsKey(p.id))
            changed.Add(p.id, p);

         btnSave.Enabled = true;
      }
      
      public class PriceView
      {
         public Price price;
         private FmPriceEdit owner;

         public PriceView(FmPriceEdit owner)
         {
            this.owner = owner;
            price = new Price();
            price.id = Price.GenId();
         }

         public PriceView(FmPriceEdit owner, Price price)
         {
            this.owner = owner;
            this.price = price;
         }

         public string Name
         {
            get { return price.Name; }
            set 
            { 
               price.name = value;
               owner.SetDirty(price);
            }
         }

         public int Own
         {
            get { return price.own; }
            set 
            { 
               price.own = value;
               owner.SetDirty(price);
            }
         }

         public int Rem 
         {
            get { return price.rem; }
            set 
            { 
               price.rem = value;
               owner.SetDirty(price);
            }
         }

         public int Pos
         {
            get { return price.pos; }
            set 
            { 
               price.pos = value;
               owner.SetDirty(price);
            }
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsPrice);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         loading = true;

         List<PriceView> list = new List<PriceView>();

         foreach (Price p in dsPrice.Values)
            list.Add(new PriceView(this, p));

         list.Sort(PriceSort);

         data.Clear();

         foreach (PriceView p in list)
            data.Add(p);

         tbSearch.Text = "";
         cbFilter.SelectedIndex = 0;
         grid.DataSource = data;

         loading = false;
      }

      private int PriceSort(PriceView x, PriceView y)
      {
         int res = x.Pos - y.Pos;

         if (res == 0)
            res = x.Name.ToUpper().CompareTo(y.Name.ToUpper());

         return res;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         data.Add(new PriceView(this));
         grid.CurrentCell = grid.Rows[data.Count - 1].Cells[0];
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         DataGridViewRow r = grid.CurrentRow;

         if (r != null)
         {
            PriceView p = (PriceView)r.DataBoundItem;
            p.Rem = 1;
            data.Remove(p);
            SetDirty(p.price);
         }
      }

      private void cbFilter_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (!loading)
         {
            filtered.Clear();
            string cond = tbSearch.Text.Trim().ToUpper();

            foreach (PriceView p in data)
               if ((cond.Length == 0 || p.Name.ToUpper().Contains(cond)) && CheckFilter(p))
                  filtered.Add(p);

            grid.DataSource = filtered;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }

      private void SaveChanges()
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         if (changed.Count > 0)
         {
            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(changed);

            bool result = DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection());
            if (!result)
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);

            if (result)
            {
               changed.Clear();
               btnSave.Enabled = false;
            }
         }
      }

      private void FmPriceEdit_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void FmPriceEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && 
            (MessageBox.Show(this, "Сохранить изменения?", "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question)
               == DialogResult.OK))
            SaveChanges();
      }

      private void tbSearch_TextChanged(object sender, EventArgs e)
      {
         if (!loading)
         {
            if (textWait != null)
               textWait.Dispose();
            textWait = new System.Threading.Timer(new TimerCallback(TimePassed), ((ToolStripTextBox)sender).Text, 500, 0);
         }
      }

      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FmPriceEdit");
            if (m.WaitOne(0))
               grid.Invoke(new InvokeParamHandler(delegate(object param) { DoSearch((string)param); }), new object[] { o });
            m.ReleaseMutex();
         }
         catch (Exception) { }
      }

      private void DoSearch(string filter)
      {
         if (filter.Trim().Length == 0)
            grid.DataSource = data;
         else
         {
            filtered.Clear();
            filter = filter.Trim().ToUpper();
            foreach (PriceView p in data)
            {
               if (p.Name.ToUpper().Contains(filter) && CheckFilter(p))
                  filtered.Add(p);
            }

            grid.DataSource = filtered;
         }
      }

      private bool CheckFilter(PriceView p)
      {
         return cbFilter.SelectedIndex == 0 || 
            p.Own == 1 && cbFilter.SelectedIndex == 1 || 
            p.Own == 0 && cbFilter.SelectedIndex == 2;
      }

      private void grid_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }
   }
}
