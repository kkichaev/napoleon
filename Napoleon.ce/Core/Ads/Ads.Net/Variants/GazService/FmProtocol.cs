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
   public partial class FmProtocol : Form
   {
      private DsBrigade dsBrigade;
      private DsProtocol dsProtocol;
      
      public static void ShowInstance()
      {
         FmProtocol fmSertificate = new FmProtocol();
         fmSertificate.Show();
      }

      public FmProtocol()
      {
         InitializeComponent();

         dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
         dsProtocol = (DsProtocol)DataModule.Get(Protocol.OBJECT_NAME) ?? new DsProtocol(true);
         dsProtocol.Filter = "writeof=0";
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsBrigade);
         upd.Add(dsProtocol);

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
         public List<Protocol> protocols;
      }

      void RefreshData()
      {
         List<Brigade> brigades = new List<Brigade>();
         brigades.AddRange(dsBrigade.Values);
         brigades.Sort(new Comparison<Brigade>(
            delegate(Brigade b1, Brigade b2) 
               { return b1.Name.CompareTo(b2.Name); }));

         List<Protocol> protocol = new List<Protocol>();
         protocol.AddRange(dsProtocol.Values);
         protocol.Sort(new Comparison<Protocol>(
            delegate(Protocol p1, Protocol p2) 
            { return p1.brigade.CompareTo(p2.brigade); }));

         Dictionary<Brigade, List<Protocol>> ds = new Dictionary<Brigade,List<Protocol>>();

         foreach (Protocol p in protocol)
         {
            if (p.brigade == null)
               continue;

            if (ds.ContainsKey(p.brigade))
               ds[p.brigade].Add(p);
            else
            {
               List<Protocol> plist = new List<Protocol>();
               plist.Add(p);
               ds.Add(p.brigade, plist);
            }
         }

         foreach (Brigade b in brigades)
            if (!ds.ContainsKey(b))
               ds.Add(b, new List<Protocol>());

         List<Data> data = new List<Data>();
         foreach (KeyValuePair<Brigade, List<Protocol>> i in ds)
         {
            Data d = new Data();
            d.brigada = i.Key;
            d.protocols = i.Value;
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

            Protocol p = FmProtocolEdit.ShowInstance(item.brigada);

            if (p != null)
            {
               if (!CountainProtocol(p))
               {
                  item.protocols.Add(p);
                  lbBrigades_SelectedIndexChanged(null, null);
                  btnSave.Enabled = true;
               }
               else
                  MessageBox.Show("Свидельство уже было добавлено", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
         }
      }

      private bool CountainProtocol(Protocol protocol)
      {
         List<Data> cert = (List<Data>)lbBrigades.DataSource;
         if (cert != null)
         {
            foreach (Data p in cert)
               foreach (Protocol prt in p.protocols)
                  if (prt.Equals(protocol))
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
         List<Protocol> list = new List<Protocol>();

         if (lbBrigades.SelectedItem != null)
         {
            Data item = (Data)lbBrigades.SelectedItem;
            list.AddRange(item.protocols);
         }

         dgvProtocol.DataSource = list;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (lbBrigades.SelectedItem != null)
         {
            Data item = (Data)lbBrigades.SelectedItem;
            DataGridViewRow row = dgvProtocol.CurrentRow;

            if (row != null)
            {
               Protocol c = row.DataBoundItem as Protocol;

               if (c != null)
               {
                  item.protocols.Remove(c);
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
            dsProtocol.Clear();
            foreach (Data pair in cert)
               foreach (Protocol p in pair.protocols)
               {
                  if (!dsProtocol.ContainsKey(p.Number))
                     dsProtocol.Add(p.Number, p);
               }

            List<ReplacedSet> updList = new List<ReplacedSet>();
            updList.Add(new ReplacedSet(dsProtocol));

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
