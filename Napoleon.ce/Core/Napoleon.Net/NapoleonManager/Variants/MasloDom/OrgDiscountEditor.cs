using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.UILib;
using System.Globalization;

namespace GRSoft.NapoleonManager
{
   public partial class OrgDiscountEditor : Form
   {
      static OrgDiscountEditor instance = null;

      SimpleDataSet<OrgDiscountData> discounts = new SimpleDataSet<OrgDiscountData>(OrgDiscountData.OBJECT_NAME, false);
      SimpleDataSet<Org> orgs = new SimpleDataSet<Org>(Org.COMMON_OBJECT_NAME, false);
      DataSet<string, ManagerFolder> folders;
      bool dataReloaded = false;
      bool clearing = false;

      public OrgDiscountEditor()
      {
         InitializeComponent();
         dgvOrgs.AutoGenerateColumns = false;
         dgvFolders.AutoGenerateColumns = false;

         DataGridViewCellStyle style = new DataGridViewCellStyle();
         style.Format = "N2";
         clmnDiscount.DefaultCellStyle = style;
         this.dgvFolders.CellValueChanged += dgvFolders_CellValueChanged;
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new OrgDiscountEditor();
            instance.Show();
         }
         else
         {
            instance.BringToFront();
         }
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         RefreshData();
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         SaveData(true);
      }

      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();

         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            tsbRefresh.Enabled = true;
            MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }));
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();

         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();

            ReloadData();
            tsbRefresh.Enabled = true;
         }));

      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         tsbRefresh.Enabled = false;

         folders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ??
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
         
         if( folders.Count == 0 )
         {
            folders.Filter = DataUtils.USERID_IS_NULL_STR;
            upd.Add(folders);
         }

         upd.Add(discounts);
         upd.Add(orgs);

         DBConnection conn = Config.GetConfig().GetConnection();
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, upd, FmWait.ProgressIndicator));
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);
      }

      void SaveData(bool showDlg)
      {
         ReplacedSet rs = new ReplacedSet(discounts);
         rs.haveUserID = false;
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         rpl.Add(rs);
         if( DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection()) )
         {
            tsbSave.Enabled = false;

            if( showDlg )
               MessageBox.Show("Изменения сохранены");
         } else
         {
            if (showDlg)
               MessageBox.Show("Ошикба сохранения");
         }
      }

      void ReloadData()
      {
         dataReloaded = true;

         List<Org> lo = new List<Org>();
         lo.AddRange((IEnumerable<Org>)orgs.Data);
         lo.Sort();
         dgvOrgs.DataSource = lo;

         dgvFolders.Nodes.Clear();
         dgvFolders.Rows.Clear();

         int lvl = -1;
         TreeGridNode parent = null;
         TreeGridNode prevNode = null;

         foreach (ManagerFolder folder in folders.Data)
         {
            object[] values = new object[] { folder.name, 0.0 };
            TreeGridNode node = null;
            if (lvl == -1 || (lvl == folder.level && parent == null))
               node = dgvFolders.Nodes.Add(values);
            else if (lvl == folder.level)
               node = parent.Nodes.Add(values);
            else if (lvl < folder.level)
            {
               parent = prevNode;
               node = parent.Nodes.Add(values);
            }
            else if (lvl > folder.level)
            {
               TreeGridNode leftNode = prevNode.Parent;

               if (leftNode == null)
               {
                  MessageBox.Show("Некорректный объект Folder", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
                  break;
               }

               if (!(leftNode.Tag is ManagerFolder))
               {
                  MessageBox.Show("Неправильная иерархия объектов, прайс не может быть владельцем прайса",
                     "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
                  break;
               }

               int reqLvl = folder.level;

               while (leftNode.Parent != null && reqLvl < (leftNode.Tag as ManagerFolder).level)
               {
                  leftNode = leftNode.Parent;
               }

               if (reqLvl > (leftNode.Tag as ManagerFolder).level)
                  parent = leftNode;
               else
                  parent = leftNode.Parent;

              node = parent.Nodes.Add(values);
            }

            node.Tag = folder;
            prevNode = node;
            lvl = folder.level;
         }


         if (lo.Count > 0)
            OnOrgChanged(lo[0]);

         dataReloaded = false;
      }

      private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Org curOrg = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;
         OnOrgChanged(curOrg);
      }

      void OnOrgChanged(Org curOrg)
      {
         bool prevReload = dataReloaded;
         dataReloaded = true;

         Dictionary<string, double> dsc = new Dictionary<string,double>();
         foreach (OrgDiscountData odd in discounts.Data)
         {
            if (curOrg.id == odd.id)
               dsc[odd.id_f] = odd.discount;
         }

         foreach (TreeGridNode tgn in dgvFolders.Nodes)
         {
            String id = ((ManagerFolder)tgn.Tag).id;
            tgn.Cells[1].Value = dsc.ContainsKey(id) ? dsc[id] : 0.0;
         }

         dataReloaded = prevReload;
      }

      private void dgvFolders_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
//         TreeGridNode row = dgvFolders.CurrentRow;

         if (e.ColumnIndex == clmnDiscount.DisplayIndex && !dataReloaded)// && row.Tag != null)
         {
            TreeGridNode row = dgvFolders.CurrentRow;
            Org curOrg = dgvOrgs.CurrentRow.DataBoundItem as Org;
            object cv = row.Cells[e.ColumnIndex].Value;
            double value = 0;
            if (cv is string)
            {
               String val = (cv as String).Replace(',', '.');
               IFormatProvider fm = CultureInfo.InvariantCulture;
               Double.TryParse(cv as string, NumberStyles.Number | NumberStyles.AllowDecimalPoint, fm, out value);
            }
            else 
               value = (double)(cv);
            String fid = (row.Tag as ManagerFolder).id;

            bool finded = false;
            foreach (OrgDiscountData odd in discounts.Data)
            {
               if (curOrg.id == odd.id && odd.id_f == fid)
               {
                  finded = true;
                  if (odd.discount != value)
                     tsbSave.Enabled = true;
                  break;
               }
            }

            if (!finded)
            {
               tsbSave.Enabled = true;
               OrgDiscountData odd = new OrgDiscountData();
               odd.id_f = fid;
               odd.id = curOrg.id;
               odd.discount = value;

               discounts.Add(odd);
            }
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (tsbSave.Enabled)
         {
            DialogResult res = MessageBox.Show("Сохранить изменения?", "Данные не сохранены", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (res == DialogResult.Cancel)
            {
               e.Cancel = true;
               return;
            }
            if (res == DialogResult.Yes)
               SaveData(false);
         }
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            ClearFind(this, EventArgs.Empty);
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();

         String text = tbFind.Text.ToUpper();
         List<Org> lo = new List<Org>();
         foreach (Org org in orgs.Data)
         {
            if (org.name.ToUpper().Contains(text))
               lo.Add(org);
         }
         lo.Sort();
         dgvOrgs.DataSource = lo;
      }

      void ClearFind(object sender, EventArgs e)
      {
         clearing = true;
         tbFind.Clear();

         List<Org> lo = new List<Org>();
         lo.AddRange((IEnumerable<Org>)orgs.Data);
         lo.Sort();
         dgvOrgs.DataSource = lo;
      }
   }

   public class OrgDiscountData : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OrgDiscounts";

      public string id = "";
      public string id_f = "";
      public double discount = 0;
   }
}
