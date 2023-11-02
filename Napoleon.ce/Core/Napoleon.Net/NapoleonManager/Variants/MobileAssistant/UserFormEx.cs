using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      private DataSet<string, PotenzialOrg> dsPotenzialOrg;

      public UserFormEx(Divisions owner) : base(owner)
      {
         userDetails.TabPages[0].Text = "Задачи";
         userDetails.TabPages.Remove(userDetails.TabPages[1]);
         userDetails.TabPages.Remove(userDetails.TabPages[1]);

         dsPotenzialOrg = (DataSet<string, PotenzialOrg>)DataModule.Get(PotenzialOrg.OBJECT_NAME) ??
            new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME);

         btnEditRoute.Text = "Распределение задач";
         btnEditRoute.Width = 200;
      }

      protected override void FillListOrgs()
      {
         List<Org> orgs = new List<Org>();
         foreach (Org o in dsPotenzialOrg.Data)
            orgs.Add(o);

         orgs.Sort();
         dgvOrgs.DataSource = orgs;
      }

      protected override void BeforeUpdateData(string userid, List<IDataSet> updSets)
      {
         dsPotenzialOrg.Filter = "\"userid\"='" + userid + "'";
         updSets.Add(dsPotenzialOrg);
      }
   }
}
