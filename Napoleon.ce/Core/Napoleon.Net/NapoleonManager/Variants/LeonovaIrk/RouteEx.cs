using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    class RouteEx : Route
    {
        public RouteEx()
        {
            this.tsbSave.Click -= new System.EventHandler(this.tsbSave_Click);
            this.tsbSave.Click += Save;
        }

        void Save(object sender, EventArgs e)
        {
            const string LIMIT_CHANGING = "LimitEditRoute";
            const int LIMIT_DAYS = 13;

            Manager m = CurrentUser.user as Manager;

            if (m != null)
            {
                if (!m.HaveRight(RightTokens.Get(LIMIT_CHANGING), RightActions.Write) && dsRSI.Count > 0)
                {
                    DateTime serverDate = DateTime.Now.Date;
                    DateTime saveDate = DateTime.Now.Date;

                    if (dsServerInfo.Count > 0)
                        serverDate = dsServerInfo[0].time.Date;

                    if (dsRSI.Count > 0)
                        saveDate = dsRSI[0].date.Date;

                    if (serverDate != saveDate && serverDate < saveDate.AddDays(LIMIT_DAYS))
                    {
                        MessageBox.Show(string.Format("Cледующая дата редактирования {0:dd.MM.yyyy}", saveDate.AddDays(LIMIT_DAYS)));
                        return;
                    }
                }
            }

            tsbSave_Click(sender, e);
        }

        SimpleDataSet<ServerInfo> dsServerInfo = new SimpleDataSet<ServerInfo>(ServerInfo.OBJECT_NAME);
        SimpleDataSet<RouteSaveInfo> dsRSI = new SimpleDataSet<RouteSaveInfo>(RouteSaveInfo.OBJECT_NAME);

        public override void UpdateRefreshList(List<Network.IDataSet> list)
        {
            base.UpdateRefreshList(list);
            list.Add(dsServerInfo);
            list.Add(dsRSI);
        }

        public override bool AskToSaveChanges()
        {
            return false;
        }
    }
}
