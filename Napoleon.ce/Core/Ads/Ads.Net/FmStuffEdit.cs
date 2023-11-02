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
   public partial class FmStuffEdit : Form
   {
      private DsProfession dsProfession;
      private Stuff stuff;

      public FmStuffEdit()
      {
         InitializeComponent();
         dsProfession = (DsProfession)DataModule.Get(Profession.OBJECT_NAME) 
            ?? new DsProfession(true);
      }

      public static bool ShowInstance(Stuff stuff)
      {
         FmStuffEdit instance = new FmStuffEdit();
         bool addMode = stuff == null;
         instance.stuff = stuff;

         if (stuff != null)
         {
            instance.tbFirstName.Text = stuff.firstname;
            instance.tbLastName.Text = stuff.lastname;
            instance.tbMiddleName.Text = stuff.middlename;
            instance.tbPhone.Text = stuff.phone;
            instance.tbAddress.Text = stuff.address;
            instance.tbRank.Text = stuff.rank.ToString();
         }

         if (addMode)
            instance.Text = "Создать";
         else
            instance.Text = "Изменить";

         if (instance.ShowDialog() == DialogResult.OK)
         {
            DsStuff dsStuff = new DsStuff(false);
            Stuff person = stuff ?? new Stuff();
            person.firstname = instance.tbFirstName.Text;
            person.lastname = instance.tbLastName.Text;
            person.middlename = instance.tbMiddleName.Text;
            person.address = instance.tbAddress.Text;
            person.phone = instance.tbPhone.Text;
            person.profession = (Profession)instance.cbProfession.SelectedItem;
            Int32.TryParse(instance.tbRank.Text, out person.rank);
            dsStuff.Add(1, person);

            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsStuff);

            bool result = false;

            if (addMode)
               result = DataModule.InsertDataSets(updSet, Config.GetConfig().GetConnection());
            else
               result = DataModule.UpdateDataSet(updSet, null, null, Config.GetConfig().GetConnection());

            if (!result)
               MessageBox.Show("Ошибка!");

            return result;
         }

         return false;
      }

      private void btnProfession_Click(object sender, EventArgs e)
      {
         FmProfession.ShowInstance(new EmptyInvoker(RefreshData));
      }

      private void FmStuffEdit_Load(object sender, EventArgs e)
      {
         if (dsProfession.Count == 0)
         {
            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsProfession);

            DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
               DataModule_OnDataResponceError);
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSet, FmWait.ProgressIndicator);
         }
         else
            RefreshData();
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();

         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         cbProfession.Items.Clear();
         cbProfession.Sorted = true;

         foreach (Profession prof in ((DsProfession)DataModule.Get(Profession.OBJECT_NAME)).Data)
            cbProfession.Items.Add(prof);

         if (cbProfession.Items.Count > 0 && stuff != null 
            && stuff.profession != null)
            foreach(Profession p in cbProfession.Items)
               if (p.Id.Equals(stuff.profession.Id))
                  cbProfession.SelectedItem = p;
      }

      private void FmStuffEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (tbLastName.Text.Trim().Length == 0)
            {
               tbLastName.Focus();
               e.Cancel = true;
               MessageBox.Show("Введите фамилию");
            }
         }
      }

      private void btnKladr_Click(object sender, EventArgs e)
      {
         FmKladr fmKladr = new FmKladr();

         if (fmKladr.ShowDialog() == DialogResult.OK)
            tbAddress.Text = fmKladr.Address;
      }
   }
}
