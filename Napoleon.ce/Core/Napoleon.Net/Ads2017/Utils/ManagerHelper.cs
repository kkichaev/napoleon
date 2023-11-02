using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Ads2017
{
    class ManagerHelper
    {
        private static ManagerHelper instance = new ManagerHelper();

        string agentUids = "";
        List<string> uids = new List<string>();

        public static ManagerHelper Instance { get { return instance; } }

        public bool SetCurrentUserData(Update.UpdateResult data)
        {
            List<DivisionManager> list = data.GetList<DivisionManager>(DivisionManager.OBJECT_NAME);

            if (list.Count == 0)
                return false;

            List<Division> divisions = data.GetList<Division>(Division.OBJECT_NAME);
            Division root = Division.PrepareTree(divisions, list);

            this.uids.Clear();
            agentUids = "";

            CurrentUser = list[0];
            int curDivision = CurrentUser.division;
            foreach(Division d in divisions)
            {
                if(d.id == curDivision)
                {
                    CurrentDivision = d;
                    break;
                }
            }

            if (CurrentDivision == null)
                return false;

            string uids = "";
            foreach(Division.DivisionAgent da in CurrentDivision.GetAllAgents())
            {
                uids += "'" + da.id + "',";
                if(!this.uids.Contains(da.id))
                    this.uids.Add(da.id);
            }
            agentUids = uids.Length == 0 ? "" : uids.Substring(0, uids.Length - 1);
            return true;

        }

        public DivisionManager CurrentUser { get; set; }
        public string AgentsWhere(bool addField)
        {
            if(addField)
                return "\"userid\" in (" + agentUids + ")";
            return agentUids;
        }

        public Division CurrentDivision { get; set; }
        public List<string> Agents { get => uids; }

        public bool CanEdit(string owner)
        {
            return CurrentUser.CanWriteTask && (!CurrentUser.RejectForeignTaskEdit || CurrentUser.RejectForeignTaskEdit && owner.Equals(CurrentUser.id));
        }

        internal bool HaveAgent(string userid)
        {
            return uids.Contains(userid);
        }
    }
}
