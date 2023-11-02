using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class OrgMatrix : Form
   {
      DataSet<int, OrgMatrixData> dsMatrix;
      DataSet<string, Org> dsOrgs;
      Agent current;
      bool loaded = false;
      List<OrgMatrixData> curMatrix;
      Org currentOrg;

      public OrgMatrix()
      {
         InitializeComponent();

         dgvMatrix.AutoGenerateColumns = false;
         dgvOrgs.AutoGenerateColumns = false;

         List<Agent> la = new List<Agent>();

         foreach (Agent a in DataModule.Get("Agents").Data)
            tbAgents.Items.Add(a);
      }

      protected override void OnHandleCreated(EventArgs e)
      {
         base.OnHandleCreated(e);
         loaded = true;
         if (current != null)
            SetCurrentAgent(current);
      }

      public void SetCurrentAgent(Agent a)
      {
         if (!loaded)
         {
            current = a;
            return;
         }

         Agent sel = null;
         foreach(Agent ca in tbAgents.Items)
            if (ca.id == a.id)
            {
               sel = ca;
               break;
            }

         tbAgents.SelectedItem = sel;
         current = a;
         dsOrgs = (DataSet<string, Org>)DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>));
         dsMatrix = (DataSet<int, OrgMatrixData>)DataModule.GetUserDataSet(a.id, OrgMatrixData.OBJECT_NAME, typeof(DataSet<int, OrgMatrixData>));
         DataSet<string, Price> dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
            new DataSet<string, Price>(Price.OBJECT_NAME);

         string filter = String.Format("userid in ('{0}')", current.id);
         dsMatrix.Filter = filter;

         List<IDataSet> updSets = new List<IDataSet>();
         if (dsOrgs.Count == 0)
            updSets.Add(dsOrgs);
         if (dsMatrix.Count == 0)
            updSets.Add(dsMatrix);
         if (dsPrice.Count == 0)
            updSets.Add(dsPrice);

         if (updSets.Count > 0)
         {
            Config c = Config.GetConfig();
            if (c.CheckLogin() == false)
               return;

            DBConnection conn = c.GetConnection();
            DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
            DataModule.DataProcessed += new EventHandler(DataModule_DataProcessed);
            FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
         }
         else
         {
            RefreshData();
         }
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         BeginInvoke(new EmptyParamHandler(RefreshData));
      }

      void RefreshData()
      {
         List<Org> orgs = new List<Org>();
         foreach(Org o in dsOrgs.Data)
            orgs.Add(o);
         orgs.Sort();

         dgvOrgs.DataSource = orgs;
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         MessageBox.Show(e.Msg, "Ошибка");
      }

      private void tbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         Agent a = (Agent)tbAgents.SelectedItem;
         if (a != null)
            SetCurrentAgent(a);
      }
   
      private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (IsDirty)
         {
            if (MessageBox.Show("Сохранить изменения", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
            {
               SaveChanges();
            }
         }
         currentOrg = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;
         curMatrix = new List<OrgMatrixData>();
         foreach (OrgMatrixData md in dsMatrix.Data)
         {
            if (md.id == currentOrg.id)
               curMatrix.Add(md);
         }

         dgvMatrix.DataSource = curMatrix;
      }

      private void SaveChanges()
      {
         if (currentOrg == null)
            return;

         int maxI = 0;
         List<int> rmvd = new List<int>();
         foreach (KeyValuePair<int,OrgMatrixData> md in dsMatrix)
         {
            if (md.Value.id == currentOrg.id)
               rmvd.Add(md.Key);
            if (maxI < md.Key)
               maxI = md.Key;
         }

         foreach (int val in rmvd)
            dsMatrix.Remove(val);

         foreach (OrgMatrixData odm in curMatrix)
            dsMatrix.Add(++maxI, odm);

         Config c = Config.GetConfig();
         DBConnection conn = c.GetConnection();
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         ReplacedSet rs = new ReplacedSet(current.id, dsMatrix);
         rpl.Add(rs);
         if (DataModule.UpdateDataSet(null, null, rpl, conn))
            MarkDirty(false);
      }

      private void tbAdd_Click(object sender, EventArgs e)
      {
         PriceSelector ps = new PriceSelector();
         ps.ItemSelected += new ItemSelected(ps_ItemSelected);
         ps.Show();
      }

      void ps_ItemSelected(object sender, Price item)
      {
         AddItem(item);
      }

      private void AddItem(Price item)
      {
         if( currentOrg == null )
            return;

         foreach (OrgMatrixData md in curMatrix)
         {
            if (md.id_i == item.id)
               return;
         }
         
         OrgMatrixData odm = new OrgMatrixData();
         odm.id = currentOrg.id;
         odm.id_i = item.id;
         odm.price = item;

         curMatrix.Add(odm);
         dgvMatrix.DataSource = null;
         dgvMatrix.DataSource = curMatrix;

         MarkDirty(true);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (e.Cancel == false && IsDirty)
         {
            DialogResult r = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (r == DialogResult.Cancel)
               e.Cancel = true;
            else if (r == DialogResult.Yes)
               SaveChanges();
         }
      }

      private void MarkDirty(bool dirty)
      {
         tbSave.Enabled = dirty;
      }

      bool IsDirty { get { return tbSave.Enabled; } }

      private void tbSave_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }

      private void dgvMatrix_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy;
      }

      private void dgvMatrix_DragDrop(object sender, DragEventArgs e)
      {
         DragDropObject ddo = e.Data.GetData(typeof(DragDropObject)) as DragDropObject;
         if (ddo != null)
         {
            Price item = ddo.Data as Price;
            if (item != null)
               AddItem(item);
         }
      }

      private void tbDel_Click(object sender, EventArgs e)
      {
         List<OrgMatrixData> rmv = new List<OrgMatrixData>();
         foreach (DataGridViewCell c in dgvMatrix.SelectedCells)
         {
            OrgMatrixData odm = dgvMatrix.Rows[c.RowIndex].DataBoundItem as OrgMatrixData;
            if (odm != null && !rmv.Contains(odm))
               rmv.Add(odm);
         }

         foreach (OrgMatrixData cm in rmv)
            curMatrix.Remove(cm);

         dgvMatrix.DataSource = null;
         dgvMatrix.DataSource = curMatrix;

         MarkDirty(true);
      }

      private void tbUp_Click(object sender, EventArgs e)
      {
         if (dgvMatrix.SelectedCells.Count > 0)
         {
            DataGridViewCell c = dgvMatrix.SelectedCells[0];
            OrgMatrixData odm = dgvMatrix.Rows[c.RowIndex].DataBoundItem as OrgMatrixData;
            int row = curMatrix.IndexOf(odm);
            if (row > 0)
            {
               curMatrix.RemoveAt(row);
               curMatrix.Insert(row - 1, odm);

               dgvMatrix.DataSource = null;
               dgvMatrix.DataSource = curMatrix;
               dgvMatrix.CurrentCell = dgvMatrix.Rows[row - 1].Cells[0];

               MarkDirty(true);
            }
         }
      }

      private void tbDown_Click(object sender, EventArgs e)
      {
         if (dgvMatrix.SelectedCells.Count > 0)
         {
            DataGridViewCell c = dgvMatrix.SelectedCells[0];
            OrgMatrixData odm = dgvMatrix.Rows[c.RowIndex].DataBoundItem as OrgMatrixData;
            int row = curMatrix.IndexOf(odm);
            if (row < curMatrix.Count - 1)
            {
               curMatrix.RemoveAt(row);
               curMatrix.Insert(row + 1, odm);

               dgvMatrix.DataSource = null;
               dgvMatrix.DataSource = curMatrix;
               dgvMatrix.CurrentCell = dgvMatrix.Rows[row + 1].Cells[0];

               MarkDirty(true);
            }
         }
      }
   }

   public class OrgMatrixData : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OrgMatrix";

      [Reference("ManagerPrice,Price", "id_i")]
      public Price price;
      public string id;
      public string id_i;

      public string Name
      {
         get
         {
            return (price != null) ? price.name : "Не найден товар с кодом <" + id_i + ">";
         }
      }
   }

   delegate void ItemSelected(object sender, Price item);

   class PriceSelector : FmSelectSKU
   {
      private Point mouseDown;
      Price dragged;
      public PriceSelector()
      {
         tsbOK.Visible = false;
         tsbCancel.Visible = false;

         tvArticles.NodeMouseDoubleClick += new TreeNodeMouseClickEventHandler(tvArticles_NodeMouseDoubleClick);
         tvArticles.MouseDown += new MouseEventHandler(tvArticles_MouseDown);
         tvArticles.MouseMove += new MouseEventHandler(tvArticles_MouseMove);
      }

      void tvArticles_MouseMove(object sender, MouseEventArgs e)
      {
         int distance = 0;
         if (mouseDown != null && dragged != null)
         {
            int x = e.X - mouseDown.X;
            int y = e.Y - mouseDown.Y;
            distance = (int)Math.Sqrt(x * x + y * y);
         }

         if (distance > 5 && e.Button == MouseButtons.Left)
         {
            DragDropObject ddo = new DragDropObject(this, dragged);
            tvArticles.DoDragDrop(ddo, DragDropEffects.Copy);
         }
      }

      void tvArticles_MouseDown(object sender, MouseEventArgs e)
      {
         TreeNode tn = tvArticles.GetNodeAt(e.X, e.Y);
         if (tn != null)
            dragged = tn.Tag as Price;
         mouseDown = new Point(e.X, e.Y);
      }

      void tvArticles_NodeMouseDoubleClick(object sender, TreeNodeMouseClickEventArgs e)
      {
         Price p = e.Node.Tag as Price;
         if (p != null && ItemSelected != null)
            ItemSelected(this, p);

      }

      public event ItemSelected ItemSelected;
   }
}
