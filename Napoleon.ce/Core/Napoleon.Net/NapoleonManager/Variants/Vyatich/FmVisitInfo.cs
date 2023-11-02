using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmVisitInfo : Form
   {
      DataSet<int, VisitInfo> dsVisit;
      DataSet<string, Org> dsOrg;
      private const string COMMON_FILTER_STR = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy} 23:59:59')";
      private SortableBindingList<VisitInfo> datasource = new SortableBindingList<VisitInfo>();
      private bool loaded = false;
      DataSet<int, Visit> visited = new DataSet<int, Visit>(Visit.OBJECT_NAME, false);
      ImageList imagelist = new ImageList();
      List<Image> nativeImages = new List<Image>();
      
      public FmVisitInfo()
      {
         InitializeComponent();
         dsVisit = (DataSet<int, VisitInfo>)DataModule.Get(VisitInfo.V_OBJECT_NAME) ?? new DataSet<int, VisitInfo>(VisitInfo.V_OBJECT_NAME);
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
         period.Start = DateTime.Now;
         period.Finish = DateTime.Now;
         grid.AutoGenerateColumns = false;
         grid.DataSource = datasource;
         grid.Sort(grid.Columns[0], ListSortDirection.Ascending);
         imagelist.ImageSize = new Size(115, 115);
         list.LargeImageList = imagelist;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Manager dm = CurrentUser.user as Manager;

         List<IDataSet> upd = new List<IDataSet>();
         string uid = DataUtils.MakeFilterFromAgents(null, dm.GetAgents());

         foreach (Agent a in dm.GetAgents().Data)
         {
            DataSet<string, Org> orgs =
               DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            if (orgs.Count == 0)
            {
               orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
               upd.Add(orgs);
            }
         }
         upd.Add(dsVisit);
         dsVisit.Filter = string.Format(COMMON_FILTER_STR, period.Start, period.Finish) + " and " + uid;
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         loaded = false;
         list.Items.Clear();
         imagelist.Images.Clear();

         grid.SuspendLayout();

         datasource.Clear();
         foreach (VisitInfo v in dsVisit.Values)
            if(inSet(v))
               datasource.Add(v);

         grid.ResumeLayout();

         loaded = true;

         if(datasource.Count > 0)
            grid_RowEnter(grid, new DataGridViewCellEventArgs(0, 0));
      }

      private bool inSet(VisitInfo v)
      {
         bool result = true;

         if (cbAction.Checked)
            result = v.stock > 0;

         if (result && cbAgents.SelectedIndex > 0)
            result = v.userid.Equals(((Agent)cbAgents.SelectedItem).id);

         if (result && tbOrg.Text.Trim().Length > 0)
            result = v.OrgName.ToUpper().Contains(tbOrg.Text.Trim().ToUpper());

         return result;
      }

      private void FmVisitInfo_Load(object sender, EventArgs e)
      {
         FillAgents();
         btnRefresh.PerformClick();
      }

      private void FillAgents()
      {
         if (CurrentUser.user == null)
            return;

         cbAgents.Items.Clear();
         List<Agent> list = new List<Agent>();

         foreach (Agent a in CurrentUser.user.GetAgents().Data)
            list.Add(a);

         if (list.Count > 0)
         {
            list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));
         }

         list.Insert(0, new AllAgent());
         cbAgents.Items.AddRange(list.ToArray());
         cbAgents.SelectedIndex = 0;
      }

      class AllAgent : Agent
      {
         public AllAgent()
         {
            name = "Все";
         }

         public override string ToString()
         {
            return name;
         }
      }

      private void cbAction_CheckedChanged(object sender, EventArgs e)
      {
         DoLoadData();
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         if(loaded)
            DoLoadData();
      }

      private void tbOrg_TextChanged(object sender, EventArgs e)
      {
         DoLoadData();
      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         tbOrg.Text = string.Empty;
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (loaded)
         {
            VisitInfo vi = grid.Rows[e.RowIndex].DataBoundItem as VisitInfo;

            if (vi != null)
            {
               visited.Filter = string.Format("\"created\"=ToDate('{0}')", vi.created);
               List<IDataSet> upd = new List<IDataSet>();
               upd.Add(visited);
               FmWait.StdDataRefresh(this, upd, LoadPics);
            }
         }
      }

      private void LoadPics()
      {
         list.Items.Clear();
         imagelist.Images.Clear();
         nativeImages.Clear();

         if (visited.Count > 0)
            for (int i = 0; i < visited[0].items.Count; i++)
            {
               Visit.VisitItem item = visited[0].items[i];
               using (MemoryStream stream = new MemoryStream(item.id))
               {
                  Image image = new Bitmap(stream);
                  nativeImages.Add(image);
                  imagelist.Images.Add(image);

                  list.Items.Add(i.ToString(), i);
               }
            }
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         VisitInfo info = grid.Rows[e.RowIndex].DataBoundItem as VisitInfo;
         if (info != null && info.stock > 0) 
         {
            e.CellStyle.BackColor = Color.Orange;
         }
         else
            e.CellStyle.BackColor = Color.White;
      }

      private void list_MouseDoubleClick(object sender, MouseEventArgs e)
      {
         ListViewItem item = list.GetItemAt(e.X, e.Y);
         VisitInfo info = grid.CurrentRow.DataBoundItem as VisitInfo;

         if (info != null && nativeImages.Count >= item.Index) 
         {
            FmViewPhoto.ShowPhoto(nativeImages[item.Index], info.Created.ToString("dd.MM.yy HH:mm"));
         }
      }
   }
}
