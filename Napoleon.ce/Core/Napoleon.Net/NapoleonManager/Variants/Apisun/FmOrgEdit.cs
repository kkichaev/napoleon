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
   public partial class FmOrgEdit : Form
   {
      DataSet<string, OrgType> dsOrgType;
      DataSet<string, Dealer> dsDealer;

      public FmOrgEdit()
      {
         InitializeComponent();
         dsOrgType = (DataSet<string, OrgType>) DataModule.Get(OrgType.OBJECT_NAME);
         dsDealer = (DataSet<string, Dealer>)DataModule.Get(Dealer.OBJECT_NAME);
      }

      public static OrgEx EditOrg(OrgEx org)
      {
         OrgEx result = null;
         FmOrgEdit form = new FmOrgEdit();

         List<OrgType> list = new List<OrgType>();
         list.AddRange(form.dsOrgType.Values);
         list.Sort(new Comparison<OrgType>(delegate(OrgType o1, OrgType o2) { return o1.name.CompareTo(o2.name); }));
         form.cbType.Items.AddRange(list.ToArray());

         if (form.cbType.Items.Count > 0)
            form.cbType.SelectedIndex = 0;

         if (org != null)
         {
            form.tbName.Text = org.name;
            form.tbAddress.Text = org.address;
            form.SetOrgType(org.orgType);
            form.SetDealer(org.dealers);
            form.cbLicense.Checked = org.license != 0;
            form.tbCheif.Text = org.cheif;
            form.tbCheifPhone.Text = org.cheifPhone;
            form.tbContact.Text = org.contact;
            form.tbContactPhone.Text = org.contactPhone;
            form.tbAgvTraffic.Text = org.avgTraff.ToString();
            form.tbEmail.Text = org.email;
         }

         if (form.ShowDialog() == DialogResult.OK)
         {
            result = org ?? new OrgEx();

            if(org == null)
               result.id = GRSoft.Network.DataObject.GenId();

            result.name = form.tbName.Text.Trim();
            result.address = form.tbAddress.Text.Trim();
            result.orgType = form.GetOrgTypeCode();
            result.dealers = form.GetDealers();
            result.license = form.cbLicense.Checked ? 1 : 0;
            result.cheif = form.tbCheif.Text.Trim();
            result.cheifPhone = form.tbCheifPhone.Text.Trim();
            result.contact = form.tbContact.Text.Trim();
            result.contactPhone = form.tbContactPhone.Text.Trim();

            try
            {
               result.avgTraff = Int32.Parse(form.tbAgvTraffic.Text);
            }catch(Exception){}

            result.email = form.tbEmail.Text.Trim();
         }

         return result;
      }

      private void SetDealer(List<OrgDealerItem> dealers)
      {
         foreach (OrgDealerItem item in dealers)
            lbDealers.Items.Add(item);

         lbDealers.Sorted = true;
      }

      private void SetOrgType(string p)
      {
         if (dsOrgType.ContainsKey(p))
         {
            OrgType d = dsOrgType[p];
            cbType.SelectedIndex = cbType.Items.IndexOf(d);
         }
      }

      private List<OrgDealerItem> GetDealers()
      {
         List<OrgDealerItem> result = new List<OrgDealerItem>();

         foreach (OrgDealerItem i in lbDealers.Items)
            result.Add(i);

         return result;
      }

      private string GetOrgTypeCode()
      {
         string result = string.Empty;

         OrgType o = cbType.SelectedItem as OrgType;

         if (o != null)
            result = o.id;

         return result;
      }

      private void btnType_Click(object sender, EventArgs e)
      {
         OrgType ot = FmOrgTypeEdit.Edit(null);

         if (ot != null)
         {
            DataSet<string, OrgType> dsOrgNewType = new DataSet<string, OrgType>(OrgType.OBJECT_NAME, false);
            
            dsOrgNewType.Add(ot.id, ot);

            List<IDataSet> wrSet = new List<IDataSet>();

            wrSet.Add(dsOrgNewType);

            if (!DataModule.UpdateDataSet
               (wrSet, null, null, Config.GetConfig().GetConnection()))
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);
            else
            {
               dsOrgType.Add(ot.id, ot);
               cbType.SelectedIndex = cbType.Items.Add(ot);
            }
         }
      }

      private void FmOrgEdit_Load(object sender, EventArgs e)
      {
         
      }

      private void btnDealer_Click(object sender, EventArgs e)
      {
         new FmDealer().Show();
      }

      private void lbDealers_DragDrop(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent(typeof(Dealer)))
         {
            OrgDealerItem item = new OrgDealerItem();
            Dealer d = (Dealer)e.Data.GetData(typeof(Dealer));
            item.id = d.id;
            item.item = d;

            lbDealers.Items.Add(item);
         }
      }

      private void lbDealers_DragEnter(object sender, DragEventArgs e)
      {
         if(e.Data.GetDataPresent(typeof(Dealer)))
            e.Effect = DragDropEffects.Copy;
         else
            e.Effect = DragDropEffects.None;
      }

      private void bntDelDealer_Click(object sender, EventArgs e)
      {
         if (lbDealers.SelectedIndex != -1)
            lbDealers.Items.RemoveAt(lbDealers.SelectedIndex);
      }
   }

   public class OrgEx : Org, TreeData
   {
      public string orgType = string.Empty;
      [ItemType(typeof(OrgDealerItem))]
      public List<OrgDealerItem> dealers = null;
      public int license = 0;
      public string cheif = string.Empty;
      public string cheifPhone = string.Empty;
      public string contact = string.Empty;
      public string contactPhone = string.Empty;
      public string parent = string.Empty;
      public int avgTraff = 0;
      public string email = string.Empty;

      #region ColumnsData Members

      public string[] Data
      {
         get { return new string[] { name, address, cheif, cheifPhone }; }
      }

      #endregion

      #region ColumnsData Members


      public string Id
      {
         get { return id; }
      }

      #endregion

      #region TreeData Members


      public string Parent
      {
         get { return parent; }
      }

      #endregion
   }

   public class OrgDealerItem : GRSoft.Network.DataObject
   {
      [Reference("Dealer", "id", typeof(Dealer))]
      public Dealer item = null;
      public string id = string.Empty;

      public override string ToString()
      {
         return item.name.ToString();
      }
   }
}
