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
   public partial class FmDistrRepParam : Form
   {
      DataSet<string, Monitor> dsMonitor;
      public FmDistrRepParam()
      {
         InitializeComponent();

         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;

         if (MainForm.Instance.CheckIsMainDataPresents(true) == false)
            return;

         dsMonitor = DataModule.Get(Monitor.OBJECT_NAME) as DataSet<string, Monitor>;
         if (dsMonitor == null)
            return;

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            List<Agent> agents = new List<Agent>();
            foreach (Division.DivisionAgent da in m.Division.GetAllAgents())
            {
               if (da.agent != null)
                  agents.Add(da.agent);
            }

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
            cbDivisions.SelectedIndex = 0;

            DataSet<string, ManagerFolder> ds = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME);

            if (ds != null)
            {
#if EUROASIA_MONTOR
               Manager mgr = CurrentUser.user as Manager;
               if (mgr == null || mgr.src == null)
                  return;

               Monitor mtr;
               if (!dsMonitor.TryGetValue(mgr.src.guid, out mtr))
                  return;

               List<string> rmv = new List<string>();
               foreach (string k in ds.Keys)
                  if (mtr.HaveFolder(k) == false)
                     rmv.Add(k);

               rmv.ForEach(x => { ds.Remove(x); });
#endif
               TreeView tv = new TreeView();
               FolderTree.MakeTree(tv.Nodes, (ICollection<ManagerFolder>)ds.Data);

               List<ManagerFolder> list = new List<ManagerFolder>();
               fillList(list, tv.Nodes);

               list.ForEach((x) => { cbFolder.Items.Add(x); });

               if (cbFolder.Items.Count > 0)
                  cbFolder.SelectedIndex = 0;
            }

         }
      }

      private void fillList(List<ManagerFolder> list, TreeNodeCollection col)
      {
         foreach(TreeNode n in col)
         {
            list.Add((ManagerFolder)n.Tag);

            if (n.Nodes.Count > 0)
               fillList(list, n.Nodes);
         }
      }

      public DateTime Start { get { return dpv.Start.Date; } }
      public DateTime Finish { get { return dpv.Finish.Date; } }
      public string UserIDS { get { return GetUserIDS(); } }
      public string FolderID { get { return GetFolderID(); } }

      private string GetFolderID()
      {
         string res = string.Empty;

         ManagerFolder mf = cbFolder.SelectedItem as ManagerFolder;
         if (mf != null)
            res = mf.id;

         return res;
      }

      private string GetUserIDS()
      {
         string res = string.Empty;

         Division d = cbDivisions.SelectedItem as Division;

         if (d != null)
            foreach (Division.DivisionAgent da in d.GetAllAgents())
            {
               if (res.Length > 0)
                  res += ",";

               res += da.id;
            }
               

         return res;
      }

      public string Division 
      { 
         get 
         {
            string res = string.Empty;

            Division d = cbDivisions.SelectedItem as Division;
            if (d != null)
               res = d.Name;

            return res;
         } 
      }
   }
}
