using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   public partial class FmCertificate : Form
   {
      private DsBrigade dsBrigade;
      private DsCertificate dsCertificate;
      
      public static void ShowInstance()
      {
         FmCertificate fmSertificate = new FmCertificate();
         fmSertificate.Show();
      }

      public FmCertificate()
      {
         InitializeComponent();

         dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
         dsCertificate = (DsCertificate)DataModule.Get(Certificate.OBJECT_NAME) ?? new DsCertificate(true);
         dsCertificate.Filter = "writeof=0";
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsBrigade);
         upd.Add(dsCertificate);

         DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
         DataModule.DataProcessed += new EventHandler(DataModule_DataProcessed);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            upd, FmWait.ProgressIndicator));
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

      class Data
      {
         public Brigade brigada;
         public List<Certificate> certificates;
      }

      void RefreshData()
      {
         List<Brigade> brigades = new List<Brigade>();
         brigades.AddRange(dsBrigade.Values);
         brigades.Sort(new Comparison<Brigade>(
            delegate(Brigade b1, Brigade b2) 
               { return b1.Name.CompareTo(b2.Name); }));

         List<Certificate> certificates = new List<Certificate>();
         foreach (Certificate c in dsCertificate.Values)
            if (c.brigade != null)
               certificates.Add(c);
         //certificates.AddRange(dsCertificate.Values);
         certificates.Sort(new Comparison<Certificate>(
            delegate(Certificate c1, Certificate c2) 
            {
               //if (c1.brigade == null)
               //   return (c2.brigade == null) ? 0 : -1;
               //if (c2.brigade == null)
               //   return 1;
               return c1.brigade.CompareTo(c2.brigade);
            }));

         Dictionary<Brigade, List<Certificate>> ds = new Dictionary<Brigade,List<Certificate>>();

         foreach (Certificate c in certificates)
         {
            if (c.brigade == null)
               continue;

            if (ds.ContainsKey(c.brigade))
               ds[c.brigade].Add(c);
            else
            {
               List<Certificate> clist = new List<Certificate>();
               clist.Add(c);
               ds.Add(c.brigade, clist);
            }
         }

         foreach (Brigade b in brigades)
            if (!ds.ContainsKey(b))
               ds.Add(b, new List<Certificate>());

         List<Data> data = new List<Data>();
         foreach (KeyValuePair<Brigade, List<Certificate>> i in ds)
         {
            Data d = new Data();
            d.brigada = i.Key;
            d.certificates = i.Value;
            data.Add(d);
         }

         lbBrigades.DataSource = data;
         btnSave.Enabled = false;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (lbBrigades.SelectedItem != null)
         {
            Data item = (Data)lbBrigades.SelectedItem;

            Certificate c = FmCertificateEdit.ShowInstance(item.brigada);

            if (c != null)
            {
               if (!CountainCert(c))
               {
                  item.certificates.Add(c);
                  lbBrigades_SelectedIndexChanged(null, null);
                  btnSave.Enabled = true;
               }
               else
                  MessageBox.Show("Свидельство уже было добавлено", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
         }
      }

      private bool CountainCert(Certificate certificate)
      {
         List<Data> cert = (List<Data>)lbBrigades.DataSource;
         if (cert != null)
         {
            foreach (Data p in cert)
               foreach (Certificate c in p.certificates)
                  if (c.Equals(certificate))
                     return true;
         }

         return false;
      }

      private void lbBrigades_Format(object sender, ListControlConvertEventArgs e)
      {
         Data item = (Data)e.ListItem;
         e.Value = item.brigada.Name;
      }

      private void lbBrigades_SelectedIndexChanged(object sender, EventArgs e)
      {
         List<Certificate> list = new List<Certificate>();

         if (lbBrigades.SelectedItem != null)
         {
            Data item = (Data)lbBrigades.SelectedItem;
            list.AddRange(item.certificates);
         }

         dgvCertificates.DataSource = list;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (lbBrigades.SelectedItem != null)
         {
            Data item = (Data)lbBrigades.SelectedItem;
            DataGridViewRow row = dgvCertificates.CurrentRow;

            if (row != null)
            {
               Certificate c = row.DataBoundItem as Certificate;

               if (c != null)
               {
                  item.certificates.Remove(c);
                  lbBrigades_SelectedIndexChanged(null, null);
                  btnSave.Enabled = true;
               }
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<Data> cert = (List<Data>)lbBrigades.DataSource;
         if (cert != null)
         {
            dsCertificate.Clear();
            foreach (Data pair in cert)
               foreach (Certificate c in pair.certificates)
               {
                  if (!dsCertificate.ContainsKey(c.Number))
                     dsCertificate.Add(c.Number, c);
               }

            List<ReplacedSet> updList = new List<ReplacedSet>();
            updList.Add(new ReplacedSet(dsCertificate));

            if (!DataModule.UpdateDataSet(null, null, updList, Config.GetConfig().GetConnection()))
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            else
               btnSave.Enabled = false;
         }
      }

      private void FmSertificate_Load(object sender, EventArgs e)
      {
         btnSave.Enabled = false;
      }
   }
}
