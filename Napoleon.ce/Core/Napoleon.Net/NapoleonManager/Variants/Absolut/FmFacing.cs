using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmFacing : Form
   {
      public DataSet<int, Facing> dsFacing;
      public DataSet<string, Price> dsPrice;
      public DataSet<int, Revised> dsRevised;
      public DataSet<int, Visit> dsVisit;
      
      public DataSet<int, Revised> changed = new DataSet<int, Revised>(Revised.OBJECT_NAME, false);
      public Dictionary<string, Revised> revData = new Dictionary<string, Revised>();
      public Dictionary<string, List<Image>> visData = new Dictionary<string, List<Image>>();
      public List<string> faceprc = new List<string>();
      DataSet<int, Visit> dsv = new DataSet<int, Visit>(Visit.OBJECT_NAME, false);

      public const string FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59')";
      

      public FmFacing()
      {
         InitializeComponent();

         dsFacing = (DataSet<int, Facing>)DataModule.Get(Facing.OBJECT_NAME) ?? new DataSet<int, Facing>(Facing.OBJECT_NAME);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
         dsRevised = (DataSet<int, Revised>)DataModule.Get(Revised.OBJECT_NAME) ?? new DataSet<int, Revised>(Revised.OBJECT_NAME);
         dsVisit = (DataSet<int, Visit>)DataModule.Get(Visit.OBJECT_NAME) ?? new DataSet<int, Visit>(Visit.OBJECT_NAME);

         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;

         grid.AutoGenerateColumns = false;
         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsPrice);
         upd.Add(dsFacing);
         upd.Add(dsRevised);

         if(cbLoadVisit.Checked)
         {
            dsVisit.Filter = String.Format(FILTER_STR, "date", dpv.Start.Date, dpv.Finish.Date);
            upd.Add(dsVisit);
         }

         dsFacing.Filter = string.Format(FILTER_STR, "created", dpv.Start.Date, dpv.Finish.Date);
         dsRevised.Filter = string.Format(FILTER_STR, "facing", dpv.Start.Date, dpv.Finish.Date);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private const string VIS_KEY_FMT = "{0:dd.MM.yyyy}+{1}+{2}"; //created + id + userid;

      private string CreateRevKey(DateTime facing, String userid, String id, String id_i){
         const string REV_KEY_FMT = "{0:dd.MM.yyyy HH:mm:ss}+{1}+{2}+{3}"; //created + userid + id + id_i

         return string.Format(REV_KEY_FMT, facing, userid, id, id_i);
      }

      private void DoLoadData()
      {
         changed.Clear();
         btnSave.Enabled = false;

         revData.Clear();

         List<Revised> revlist = new List<Revised>();
         revlist.AddRange(dsRevised.Values);
         revlist.Sort((x,y) =>{ return RevCmp(x, y);});

         foreach (Revised r in revlist)
         {
            string key = CreateRevKey(r.facing, r.userid, r.id, r.id_i);
            if (!revData.ContainsKey(key))
               revData[key] = r;

            key = CreatePrcKey(r.facing, r.userid, r.id);

            if (!faceprc.Contains(key))
               faceprc.Add(key);
         }

         visData.Clear();

         WalkVisit(dsVisit);

         List<Facing> list = new List<Facing>();
         list.AddRange(dsFacing.Values);
         list.Sort((x, y) => { return Fcmp(x, y); });

         grid.DataSource = list;
      }

      private void WalkVisit(DataSet<int, Visit> visit)
      {
         foreach (Visit v in visit.Data)
            AppendPhoto(v);
      }

      private void AppendPhoto(Visit v)
      {
         string key = String.Format(VIS_KEY_FMT, v.created, v.id, v.userid);

         if (!visData.ContainsKey(key))
            visData[key] = new List<Image>();

         List<Image> imgs = visData[key];

         foreach (Visit.VisitItem i in v.items)
         {
            try
            {
               using (MemoryStream stream = new MemoryStream(i.id))
               {
                  Image image = new Bitmap(stream);
                  imgs.Add(image);
               }
            }
            catch (Exception) { }
         }
      }

      private string CreatePrcKey(DateTime facing, string userid, string id)
      {
         const string KEY = "{0:dd.MM.yyyy HH:mm:ss}+{1}+{2}"; //created + userid + id

         return string.Format(KEY, facing, userid, id);
      }

      private int RevCmp(Revised x, Revised y)
      {
         return x.created.CompareTo(y.created) * -1;
      }

      private int Fcmp(Facing x, Facing y)
      {
         string k1 = CreatePrcKey(x.created, x.userid, x.id);
         string k2 = CreatePrcKey(y.created, y.userid, y.id);

         int res = (Convert.ToInt32(faceprc.Contains(k1))) - (Convert.ToInt32(faceprc.Contains(k2)));

         if (res == 0)
            res = x.created.CompareTo(y.created);

         return res;
      }

      private void RefreshDetail(Facing f)
      {
         List<FacingRev> data = new List<FacingRev>();

         if (f != null)
         {
            foreach (FacingItem i in f.items)
            {
               string key = CreateRevKey(f.created, f.userid, f.id, i.id);
               double qty2 = revData.ContainsKey(key) ? revData[key].qty : 0.0;

               FacingRev fr = new FacingRev();
               fr.name = i.Item;
               fr.qty1 = i.Qty;
               fr.qty2 = qty2;
               fr.id = i.id;

               data.Add(fr);
            }

            RefreshPhoto(f);
         }

         data.Sort((x, y) => { return Icmp(x, y); });
         detail.DataSource = data;
      }

      private void RefreshPhoto(Facing f)
      {
         imageList.Images.Clear();
         listView.Items.Clear();

         string k = string.Format(VIS_KEY_FMT, f.created, f.id, f.userid);

         if (visData.ContainsKey(k))
         {
            List<Image> imgs = visData[k];

            for (int i = 0; i < imgs.Count; i++)
            {
               imageList.Images.Add(imgs[i]);

               ListViewItem it = listView.Items.Add(i.ToString());
               it.ImageIndex = i;
            }
         }

         listView.Refresh();
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Facing f = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Facing;
         RefreshDetail(f);
      }

      private int Icmp(FacingRev x, FacingRev y)
      {
         return x.name.CompareTo(y.name);
      }

      private void detail_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         if(grid.CurrentRow != null && grid.CurrentRow.DataBoundItem != null)
         {
            Facing f = grid.CurrentRow.DataBoundItem as Facing;

            if (f != null)
            {
               FacingRev fr = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as FacingRev;

               if (fr != null)
               {
                  Revised r = new Revised();
                  r.id = f.id;
                  r.id_i = fr.id;
                  r.qty = fr.Qty2;
                  r.facing = f.created;
                  r.created = DateTime.Now;
                  r.userid = f.userid;
                 
                  changed.Add(changed.Count, r);
                  btnSave.Enabled = true;

                  string k = CreatePrcKey(r.facing, r.userid, r.id);

                  if (!faceprc.Contains(k))
                     faceprc.Add(k);

                  k = CreateRevKey(f.created, f.userid, f.id, fr.id);
                  revData[k] = r;

                  grid.Refresh();
               }
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         wrSet.Add(changed);
         if (DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
         {
            btnSave.Enabled = false;
            changed.Clear();
            DialogUtil.SavedGood(this);
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void listView_DoubleClick(object sender, EventArgs e)
      {
         if( grid.CurrentRow != null)
         {
            Facing f = grid.CurrentRow.DataBoundItem as Facing;

            if(f != null)
            {
               string k = string.Format(VIS_KEY_FMT, f.created, f.id, f.userid);

               if(visData.ContainsKey(k))
               {
                  List<Image> lst = visData[k];

                  int idx =  ((ListView)sender).SelectedItems[0].Index;
                  if(idx >= 0 && idx < lst.Count)
                     FmViewPhoto.ShowPhoto(lst[idx], f.created.ToString("dd.MM.yy HH:mm"));
               }
            }
         }
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Facing f = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Facing;

         if (f != null)
         {
            string k = CreatePrcKey(f.created, f.userid, f.id);

            if (faceprc.Contains(k))
               e.CellStyle.BackColor = Color.DarkCyan;
         }
      }

      private void FmFacing_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            btnSave.PerformClick();
      }

      private void btnLoadPhoto_Click(object sender, EventArgs e)
      {
         DataGridViewRow r = grid.CurrentRow;

         if(r != null)
         {
            Facing f = r.DataBoundItem as Facing;

            if (f != null)
            {
               dsv.Clear();
               List<IDataSet> upd = new List<IDataSet>();
               dsv.Filter = String.Format("\"date\" >= ToDate('{0:dd/MM/yyyy}') and \"id\" = '{1}' and \"userid\" = '{2}'",
                  f.created, f.id, f.userid);
               upd.Add(dsv);
               FmWait.StdDataRefresh(this, upd, DoLoadPhoto);
            }
         }
      }

      private void DoLoadPhoto()
      {
          DataGridViewRow r = grid.CurrentRow;

          if (r != null)
          {
             Facing f = r.DataBoundItem as Facing;

             if (f != null)
             {
                string key = String.Format(VIS_KEY_FMT, f.created, f.id, f.userid);

                if (visData.ContainsKey(key))
                   visData.Remove(key);

                WalkVisit(dsv);
                RefreshPhoto(f);
             }
          }
      }
   }

   class FacingRev 
   {
      public string id = string.Empty;
      public string name = string.Empty;
      public double qty1 = 0.0;
      public double qty2 = 0.0;

      public string Name { get { return name;} }
      public double Qty1 { get { return qty1; } }
      public double Qty2 { get { return qty2; } set { qty2 = value; } }
   }
}
