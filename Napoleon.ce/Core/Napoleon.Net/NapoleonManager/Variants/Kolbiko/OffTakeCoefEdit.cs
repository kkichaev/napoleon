using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Threading;
using System.Globalization;

namespace GRSoft.NapoleonManager
{
   //"OffTakeCoef";
   public partial class OffTakeCoefEdit : Form
   {
      DataSet<string, FolderCoef> dsFoldersCoef = new DataSet<string, FolderCoef>(FolderCoef.OBJECT_NAME, false);
      DataSet<string, ManagerFolder> dsFolders;
      Dictionary<string, double> agentCoef = new Dictionary<string, double>();

      static string OFF_TAKE_KEY = "OffTakeCoef";

      bool dirty;

      public OffTakeCoefEdit()
      {
         InitializeComponent();

         ReceiveData();
         FillGrids();

         dirty = false;
      }

      public void MarkDirty()
      {
         dirty = true;
         btnSave.Enabled = true;
      }

      private void FillGrids()
      {
         DataSet<string, DivisionManager> dsManager = DataModule.Get(DivisionManager.OBJECT_NAME) as DataSet<string, DivisionManager>;
         if (dsManager == null || dsManager.Count == 0)
         {
            MessageBox.Show("Текущий пользователь не является менеджером");
            return;
         }

         DataSet<int, CommonConfig> dsConfig = DataModule.Get(CommonConfig.OBJECT_NAME) as DataSet<int, CommonConfig>;
         if (dsConfig != null)
         {
            NumberFormatInfo nfi = new CultureInfo("en-US", false).NumberFormat;
            foreach (CommonConfig cc in dsConfig.Data)
            {
               if (cc.key.CompareTo(OFF_TAKE_KEY) == 0 && cc.userid != null && cc.userid.Length > 0)
               {
                  double v;
                  if (!Double.TryParse(cc.value, NumberStyles.Any, nfi, out v))
                     v = 1.2;
                  agentCoef.Add(cc.userid, v);
               }
            }
         }

         List<Division.DivisionAgent> agents = new List<Division.DivisionAgent>();
         foreach (DivisionManager dm in dsManager.Data)
         {
            if (dm.division != 0)
            {
               DivisionList dl = DivisionList.GetDataSet();
               Division d = dl[dm.division];
               agents = d.GetAllAgents();
            }
            break;
         }

         Dictionary<string, bool> keys = new Dictionary<string, bool>();
         List<CoefAgentData> CoefAgentData = new List<CoefAgentData>();
         foreach (Division.DivisionAgent a in agents)
         {
            Agent agent = a.agent;
            if (agent != null && keys.ContainsKey(agent.id) == false)
            {
               keys.Add(agent.id, true);
               CoefAgentData.Add(new CoefAgentData(agent, GetCoef(agent), this));
            }
         }
         CoefAgentData.Sort();
         dgvAgents.DataSource = CoefAgentData;

         keys.Clear();
         List<FolderData> folderData = new List<FolderData>();
         foreach (FolderCoef fc in dsFoldersCoef.Data)
         {
            ManagerFolder mf = fc.folder;
            if( mf != null && keys.ContainsKey(mf.id) == false )
            {
               keys.Add(mf.id, true);
               folderData.Add(new FolderData(mf, fc.coef, this));
            }
         }
         dgvFolders.DataSource = folderData;
      }

      private double GetCoef(Agent a)
      {
         if (agentCoef.ContainsKey(a.id))
            return agentCoef[a.id];

         return 1.2;
      }

      private void ReceiveData()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         Config cfg = Config.GetConfig();

         Agents agents = DataModule.Get("Agents") as Agents;
         if (agents == null)
         {
            agents = new Agents();
            updSets.Add(agents);
            updSets.Add(DivisionList.GetDataSet());
         }

         updSets.Add(dsFoldersCoef);

         DataSet<string, DivisionManager> dsManager = DataModule.Get(DivisionManager.OBJECT_NAME) as DataSet<string, DivisionManager>;
         if( dsManager == null )
            dsManager = new DataSet<string,DivisionManager>(DivisionManager.OBJECT_NAME);
         if (dsManager.Count == 0)
         {
            dsManager.Filter = "login = '" + cfg.login + "' and password = '" + cfg.password + "'";
            updSets.Add(dsManager);
         }

         DataSet<int, CommonConfig> dsConfig = DataModule.Get(CommonConfig.OBJECT_NAME) as DataSet<int, CommonConfig>;
         if (dsConfig == null)
            dsConfig = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);

         dsConfig.Filter = "(not (userid is null)) or userid is null";
         updSets.Add(dsConfig);

         dsFolders = DataModule.Get("ManagerFolder") as DataSet<string, ManagerFolder>;
         if (dsFolders == null || dsFolders.Count == 0)
         {
            dsFolders = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
            dsFolders.Filter = DataUtils.USERID_IS_NULL_STR;
            updSets.Add(dsFolders);
         }

         DataModule.OnDataResponceError += new EventDataResponseError(DataConnectionError);

         Thread j = DataModule.RefreshGiveSets(cfg.GetConnection(), updSets, null);
         FmWait.ShowForm(this, j);
         j.Join();

         DataModule.ClearEvents();
         FmWait.CloseForm();
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
         FmWait.CloseForm(true);
      }

      bool SaveData()
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         DataSet<int, CommonConfig> wr = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
         wrSet.Add(wr);

         CultureInfo en = new CultureInfo("en-US", false);
         if (dgvAgents.DataSource != null)
         {
            foreach (CoefAgentData ad in dgvAgents.DataSource as List<CoefAgentData>)
            {
               CommonConfig cc = new CommonConfig();
               cc.key = OFF_TAKE_KEY;
               cc.userid = ad.agent.id;
               cc.value = ad.coef.ToString(en.NumberFormat);
               wr.Add(wr.Count, cc);
            }
         }

         List<ReplacedSet> rpl = new List<ReplacedSet>();
         if (dgvFolders.DataSource != null)
         {
            DataSet<int, FolderCoef> fs = new DataSet<int, FolderCoef>(FolderCoef.OBJECT_NAME, false);
            ReplacedSet rs = new ReplacedSet(fs);
            rpl.Add(rs);
            foreach (FolderData fd in dgvFolders.DataSource as List<FolderData>)
            {
               FolderCoef fc = new FolderCoef();
               fc.fid = fd.folder.id;
               fc.coef = fd.coef;
               fs.Add(fs.Count, fc);
            }
         }
         return DataModule.UpdateDataSet(wrSet, null, rpl, Config.GetConfig().GetConnection());
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (SaveData())
         {
            btnSave.Enabled = false;
            dirty = false;
         }
      }

      private void OffTakeCoefEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (dirty)
         {
            DialogResult r = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (r == DialogResult.Cancel)
            {
               e.Cancel = true;
               return;
            }

            if (r == DialogResult.Yes)
               SaveData();
         }
      }

      private void btnNew_Click(object sender, EventArgs e)
      {
         List<FolderData> f = FolderSelector.SelectFolders(
            dsFolders.Data as ICollection<ManagerFolder>, 
            dgvFolders.DataSource as ICollection<FolderData>,
            this);

         if (f != null)
         {
            dgvFolders.DataSource = f;
            MarkDirty();
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvFolders.SelectedCells.Count > 0)
         {
            if (MessageBox.Show("Удалить данные?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
            {
               List<FolderData> fd = (List<FolderData>)dgvFolders.DataSource;
               List<FolderData> newFd = new List<FolderData>(fd);
               foreach (DataGridViewCell c in dgvFolders.SelectedCells)
               {
                  DataGridViewRow r = dgvFolders.Rows[c.RowIndex];
                  newFd.Remove(r.DataBoundItem as FolderData);
               }

               dgvFolders.DataSource = newFd;
               MarkDirty();
            }
         }
      }
   }

   class CoefAgentData : IComparable<CoefAgentData>
   {
      OffTakeCoefEdit owner;

      public CoefAgentData(Agent a, double c, OffTakeCoefEdit o) { agent = a; coef = c; owner = o; }

      public Agent agent;
      public double coef;

      public string Agent { get { return agent.Name; } }
      public double Coef 
      { 
         get { return coef; } 
         set { coef = value; owner.MarkDirty(); }
      }

      #region Члены IComparable<CoefAgentData>

      public int CompareTo(CoefAgentData other)
      {
         return Agent.CompareTo(other.Agent);
      }

      #endregion
   }

   class FolderData : IComparable<FolderData>
   {
      OffTakeCoefEdit owner;

      public FolderData(ManagerFolder f, double c, OffTakeCoefEdit o) { folder = f; coef = c; owner = o; }

      public ManagerFolder folder;
      public double coef;

      public string Folder { get { return folder.name; } }
      public double Coef
      { 
         get { return coef; }
         set { coef = value; owner.MarkDirty(); } 
      }

      #region Члены IComparable<FolderData>

      public int CompareTo(FolderData other)
      {
         return folder.name.CompareTo(other.folder.name);
      }

      #endregion
   }

   public class FolderCoef : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "FolderCoef";

      [KeyField]
      public string fid;

      [Reference("ManagerFolder", "fid", typeof(ManagerFolder))]
      public ManagerFolder folder;

      public double coef;
   }
}
