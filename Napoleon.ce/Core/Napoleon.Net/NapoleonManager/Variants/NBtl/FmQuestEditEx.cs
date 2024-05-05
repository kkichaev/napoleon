using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;
using static GRSoft.NapoleonManager.FmSelectContrAgent;

namespace GRSoft.NapoleonManager
{
   internal class FmQuestEditEx : FmQuestEdit
   {
      public static DataSet<string, Org> dsOrg = (DataSet<string, Org>) DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
      
      public FmQuestEditEx(Question question) : base(question)
      {
         string filter = "\"id\" is null or \"id\" is not null";
         if (dsOrg.Filter != filter)
         {
            dsOrg.Filter = filter;
            dsOrg.Clear();
         }
      }

      protected override void UpdateItem(QuestItemType qyt, QuestionItem qi)
      {
         qi.altText = qyt.altText;
         qi.clients = qyt.clients;
         qi.showInPhoto = qyt.showInPhoto;
         base.UpdateItem(qyt, qi);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         List<IDataSet> upd = new List<IDataSet>();
         if(dsOrg.Count == 0)
            upd.Add(dsOrg);

         if (upd.Count == 0)
            DoLoadData();
         else
            FmWait.StdDataRefresh(this, upd, DoLoadData, null);
      }

      void DoLoadData()
      {
         
      }
   }
}
