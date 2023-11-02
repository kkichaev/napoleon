using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   public class UserFormEx : UserForm
   {
      public UserFormEx(Divisions owner) :
         base(owner)
      {
      }

      protected override void BeforeUpdateData(List<IDataSet> updSets)
      {
         owner.mainArticleFolder.Filter = "";
         owner.mainArticleFolder.Name = "SkladFolder";
         owner.mainArticleFolder.Command = new ServerCommand(Commands.Impersonate(Commands.GET, Agent.id), "SkladFolder");
         updSets.Add(owner.mainArticleFolder);
      }
   }
}