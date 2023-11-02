using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public delegate void OrgClkcked(object sender, Org org);

   public partial class FmSelectContrAgent : Form
   {
      //private DataGridViewCellStyle styleCell = new DataGridViewCellStyle();
      
      private Org selectedOrg;
      private SearchEngine searchEngine;
      private bool canDragAndDrop;

      Selected checkSelected;
      List<Org> allOrgs = new List<Org>();

      public interface Selected
      {
         bool IsSelected(Org o);
      }

      class SelectedFolder : Selected
      {
         private IDataSet dsOrgFolder;
         public SelectedFolder(IDataSet dsOrgFolder)
         {
            this.dsOrgFolder = dsOrgFolder;
         }

         public bool IsSelected(Org o)
         {
            foreach (OrgFolder of in dsOrgFolder.Data)
            {
               foreach (OrgFolderItem ofi in of.items)
               {
                  if (ofi.name == o.id)
                  {
                     return true;
                  }
               }
            }
            return false;
         }
      }

      private static FmSelectContrAgent instance;

      public FmSelectContrAgent()
      {
         InitializeComponent();
         searchEngine = new SearchEngine(new FindDataGridObject(dgvOrgs, 0));
         dgvOrgs.AutoGenerateColumns = false;
      }

      private void AdjustForm()
      {
         //styleCell.BackColor = Color.GreenYellow;

         //dgvOrgs.SuspendLayout();

         //foreach (DataGridViewRow dgvr in dgvOrgs.Rows)
         //{
         //   if (checkSelected != null && checkSelected.IsSelected(dgvr.DataBoundItem as Org))
         //   {
         //      dgvr.DefaultCellStyle = styleCell;
         //   }
         //}

         //dgvOrgs.ResumeLayout();
      }

      public Org SelectedOrg { get { return selectedOrg; } }

      private static int CmpOrgName(Org _l, Org _r)
      {
         return _l.name.CompareTo(_r.name);
      }

      public static Org SelectOrg(IDataSet dsOrg, IDataSet dsPtnzOrg)
      {
         Org ret = null;

         FmSelectContrAgent a = new FmSelectContrAgent();
         a.dgvOrgs.DataSource = a.MakeDataSource(dsOrg, dsPtnzOrg);
         a.AdjustForm();

         if (a.ShowDialog() == DialogResult.OK)
            ret = a.SelectedOrg;

         return ret;
      }

      private event OrgClkcked OrgDoubleClicked;

      public static FmSelectContrAgent ShowForm(IDataSet dsOrg, IDataSet dsPtnzOrg, IDataSet dsOrgFolder, OrgClkcked clickHandler, Form owner)
      { 
         return ShowForm(dsOrg, dsPtnzOrg, new SelectedFolder(dsOrgFolder), clickHandler, owner);
      }

      public static FmSelectContrAgent ShowForm(IDataSet dsOrg, IDataSet dsPtnzOrg, FmSelectContrAgent.Selected checkSelected, OrgClkcked clickHandler, Form owner)
      {
         if (instance == null)
         {
            instance = createInstance();
            BindingSource bs = instance.MakeDataSource(dsOrg, dsPtnzOrg);
            instance.dgvOrgs.DataSource = bs;
            instance.OrgDoubleClicked = clickHandler;

            instance.checkSelected = checkSelected;
            instance.canDragAndDrop = true;
            instance.Show(owner);
            instance.tcbFilter.SelectedIndex = 0;
            instance.tcbFilter.Visible = (checkSelected != null);
            instance.AdjustForm();
         }

         return instance;
      }

      private static FmSelectContrAgent createInstance()
      {
         Type prcType = FormEntries.GetFormType(typeof(FmSelectContrAgent));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         return (FmSelectContrAgent)ci.Invoke(new object[] { });
      }

      public static FmSelectContrAgent ShowForm(IDataSet dsOrg, IDataSet dsPtnzOrg, bool canDragAndDrop, OrgClkcked clickHandler, Form owner)
      {
         if (instance == null)
         {
            instance = new FmSelectContrAgent();
            BindingSource bs = instance.MakeDataSource(dsOrg, dsPtnzOrg);
            instance.dgvOrgs.DataSource = bs;
            instance.OrgDoubleClicked = clickHandler;

            instance.canDragAndDrop = canDragAndDrop;
            instance.Show(owner);
            instance.AdjustForm();
         }

         return instance;
      }

      private BindingSource MakeDataSource(IDataSet dsOrg, IDataSet dsPtnzOrg)
      {
         BindingSource bs = new BindingSource();
         allOrgs.Clear();
         if (dsOrg != null)
            foreach (Org o in dsOrg.Data)
               allOrgs.Add(o);

         if (dsPtnzOrg != null)
            foreach (Org o in dsPtnzOrg.Data)
               allOrgs.Add(o);
         allOrgs.Sort(CmpOrgName);

         bs.DataSource = allOrgs;
         return bs;
      }

      private void tsbFind_Click(object sender, EventArgs e)
      {
         searchEngine.find(tstbFind.Text, Direction.DOWN);
      }

      private Point mouseDown;
      private void dgvOrgs_MouseMove(object sender, MouseEventArgs e)
      {
         DataGridView.HitTestInfo hti = ((DataGridView)sender).HitTest(e.X, e.Y);

         if (hti.RowIndex == -1 || hti.ColumnIndex == -1)
            return;

         int distance = 0;
         if (mouseDown != null)
         {
            int x = e.X - mouseDown.X;
            int y = e.Y - mouseDown.Y;
            distance = (int)Math.Sqrt(x * x + y * y);
         }

         if (distance > 5 && e.Button == MouseButtons.Left && canDragAndDrop)
         {
            List<Org> orgs = new List<Org>();

            foreach (DataGridViewRow r in dgvOrgs.SelectedRows)
               orgs.Add(r.DataBoundItem as Org);

            if (orgs.Count != 0)
            {
               DragDropObject ddo = new DragDropObject(this, orgs);
               dgvOrgs.DoDragDrop(ddo, DragDropEffects.Copy);
            }
         }

      }
      private void dgvOrgs_MouseDown(object sender, MouseEventArgs e)
      {
         mouseDown = new Point(e.X, e.Y);
      }

      private void tstbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tstbFind.Text, Direction.DOWN);
      }

      private void FmSelectContrAgent_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DrawCell(e);
      }

      protected virtual void DrawCell(DataGridViewCellFormattingEventArgs e)
      {
         DataGridViewRow row = dgvOrgs.Rows[e.RowIndex];
         Org o = row.DataBoundItem as Org;
         if (o != null)
         {
            if (o is PotenzialOrg)
               e.CellStyle.ForeColor = Color.Red;
            if (checkSelected != null && checkSelected.IsSelected(o))
               e.CellStyle.BackColor = Color.LightGray;
         }
      }

      private void dgvOrgs_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         DataGridViewRow row = dgvOrgs.Rows[e.RowIndex];
         selectedOrg = row.DataBoundItem as Org;

         if (OrgDoubleClicked != null)
            OrgDoubleClicked.Invoke(this, selectedOrg);

         DialogResult = DialogResult.OK;
      }

      private void dgvOrgs_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Return)
         {
            DataGridViewSelectedRowCollection sel = dgvOrgs.SelectedRows;
            if (sel.Count > 0)
            {
               DataGridViewRow row = sel[0];
               selectedOrg = row.DataBoundItem as Org;
            }
            if (!canDragAndDrop)
            {
               DialogResult = DialogResult.OK;
            }
            else
            {
               if (OrgDoubleClicked != null)
                  OrgDoubleClicked.Invoke(this, selectedOrg);
            }
         }
      }

      private void tsbFindBack_Click(object sender, EventArgs e)
      {
         searchEngine.find(tstbFind.Text, Direction.UP);
      }

      protected void tcbFilter_SelectedIndexChanged(object sender, EventArgs e)
      {
         BindingSource bs = new BindingSource();
         if (ResetFilter())
            bs.DataSource = allOrgs;
         else
         {
            List<Org> src = new List<Org>();

            foreach(Org o in allOrgs)
            {
               if (CheckFilter(o))
                  src.Add(o);
            }

            bs.DataSource = src;
         }

         dgvOrgs.DataSource = bs;
      }

      protected virtual bool CheckFilter(Org o)
      {
         bool result = tcbFilter.SelectedIndex == 0;

         if (!result)
         {
            bool selected = checkSelected.IsSelected(o);
            result = selected && tcbFilter.SelectedIndex == 1 || !selected & tcbFilter.SelectedIndex == 2;
         }

         return result;
      }

      protected virtual bool ResetFilter()
      {
         return tcbFilter.SelectedIndex == 0 || checkSelected == null;
      }
   }
}