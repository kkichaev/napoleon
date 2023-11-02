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
   public partial class FmOrgEdit : Form
   {
      private DataSet<string, Slsnet> dsSlsnet;
      private DataSet<string, City> dsCity;
      private DataSet<string, GoodsMatrix> dsGoodsMatrix;

      public FmOrgEdit()
      {
         InitializeComponent();

         dsSlsnet = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME) ?? new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);
         dsCity = (DataSet<string, City>) DataModule.Get(City.OBJECT_NAME) ?? new DataSet<string, City>(City.OBJECT_NAME);
         dsGoodsMatrix = (DataSet<string, GoodsMatrix>) DataModule.Get(GoodsMatrix.OBJECT_NAME) ?? new DataSet<string, GoodsMatrix>(GoodsMatrix.OBJECT_NAME);

         LoadComboBox(cbSlsnet, dsSlsnet, new Slsnet());
         LoadComboBox(cbCity, dsCity, new City());
         LoadComboBox(cbMatrix, dsGoodsMatrix, new GoodsMatrix());
      }

      private void btnAddSlsnet_Click(object sender, EventArgs e)
      {
         FmSlsnet dialog = new FmSlsnet();
         dialog.OnSlsnetRefresh += OnSlsnetRefresh;
         dialog.Show();
      }

      void OnSlsnetRefresh(string id)
      {
         if (!FocusValueName(cbSlsnet, id) && dsSlsnet.ContainsKey(id))
            cbSlsnet.SelectedIndex = cbSlsnet.Items.Add(dsSlsnet[id]);
      }

      private void LoadComboBox(ComboBox combobox, IDataSet dataset, GRSoft.Network.DataObject empty)
      {
         List<GRSoft.Network.DataObject> list = new List<GRSoft.Network.DataObject>();

         foreach (GRSoft.Network.DataObject so in dataset.Data)
            list.Add(so);

         list.Sort((lhs, rhs) => {
            String x = lhs.GetType().GetField("name").GetValue(lhs).ToString();
            String y = rhs.GetType().GetField("name").GetValue(rhs).ToString();
            return x.CompareTo(y); 
         });

         combobox.Items.Add(empty);
         combobox.Items.AddRange(list.ToArray());
      }

      private void btnAddCity_Click(object sender, EventArgs e)
      {
         FmCity dialog = new FmCity();
         dialog.OnCityRefresh += dialog_OnCityRefresh;
         dialog.Show();
      }

      void dialog_OnCityRefresh(string id)
      {
         if (!FocusValueName(cbCity, id) && dsCity.ContainsKey(id))
            cbCity.SelectedIndex = cbCity.Items.Add(dsCity[id]);
      }

      private Control GetInvalidControl()
      {
         if (tbName.Text.Trim().Length == 0)
            return tbName;
         else if (cbSlsnet.SelectedIndex <= 0)
            return cbSlsnet;
         else if (cbCity.SelectedIndex <= 0)
            return cbCity;
         else if (tbAddress.Text.Trim().Length == 0)
            return tbAddress;
         else
            return null;
      }

      private void FmOrgEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         Control inv = GetInvalidControl();
         if(DialogResult == System.Windows.Forms.DialogResult.OK && inv != null)
         {
            e.Cancel = true;
            inv.Focus();
            DialogUtil.HaveToValueMsg(this);
         }
      }

      public string Org { get { return tbName.Text.Trim();} set{ tbName.Text = value;} }
      public string Address { get { return tbAddress.Text.Trim(); } set { tbAddress.Text = value; } }
      public Slsnet Slsnet { get { return (Slsnet)cbSlsnet.SelectedItem; } set { FocusValue(cbSlsnet, value); } }
      public string CityName { get { return cbCity.SelectedItem.ToString(); } set { FocusValue(cbCity, value); } }
      public string MatrixName { get { return cbMatrix.SelectedItem.ToString(); } set { FocusValue(cbMatrix, value); } }
      public string NameLPR { get { return tbNameLPR.Text.Trim(); } set { tbNameLPR.Text = value; } }
      public string ContactsLPR { get { return tbContactsLPR.Text.Trim(); } set { tbContactsLPR.Text = value; } }
      public string BithdayLPR { get { return tbBithdayLPR.Text.Trim(); } set { tbBithdayLPR.Text = value; } }
      public string VisitDays { get { return tbVisitDays.Text.Trim(); } set { tbVisitDays.Text = value; } }
      public string Responsible { get { return tbResponsible.Text.Trim(); } set { tbResponsible.Text = value; } }
      public string Equipment { get { return tbEquipment.Text.Trim(); } set { tbEquipment.Text = value; } }
      public string MatrixSku { get { return tbMatrixsku.Text.Trim(); } set { tbMatrixsku.Text = value; } }
      public string OrderDay { get { return tbOrderday.Text.Trim(); } set { tbOrderday.Text = value; } }
      public string PromoPlan { get { return tbPromoplan.Text.Trim(); } set { tbPromoplan.Text = value; } }
      public string ProviderNumber { get { return tbProviderNumber.Text.Trim(); } set { tbProviderNumber.Text = value; } }
      public string Code { get { return tbCode.Text.Trim(); } set { tbCode.Text = value; } }

      private bool FocusValue(ComboBox cb, string name)
      {
         return FocusValueName(cb, name);
      }

      private bool FocusValue(ComboBox cb, SimpleObject so)
      {
         bool result = false;

         if (so == null)
            result = false;
         else
            result = FocusValueId(cb, so.id);

         return result;
      }

      private bool FocusValueId(ComboBox cb, string id)
      {
         int i = 0;
         for (; i < cb.Items.Count; i++)
         {
            SimpleObject so = (SimpleObject)cb.Items[i];
            if (id.Equals(so.id))
            {
               cb.SelectedIndex = i;
               break;
            }
         }

         return i < cb.Items.Count;
      }

      private bool FocusValueName(ComboBox cb, string id)
      {
         int i = 0;
         for (; i < cb.Items.Count; i++)
         {
            object so = cb.Items[i];

            string name = so.GetType().GetField("name").GetValue(so).ToString();

            if (name != null && id.Equals(name))
            {
               cb.SelectedIndex = i;
               break;
            }
         }

         return i < cb.Items.Count;
      }
   }
}
