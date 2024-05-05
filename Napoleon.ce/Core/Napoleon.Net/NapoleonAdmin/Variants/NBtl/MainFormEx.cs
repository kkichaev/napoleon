using GRSoft.Network;
using System.Collections.Generic;
using System.ComponentModel;
using System.Reflection;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataSet<string, Contracts> dsConstracts = new DataSet<string, Contracts>(Contracts.OBJECT_NAME, false);
      DataSet<string, NBTLViewer> dsViewers = new DataSet<string, NBTLViewer>(NBTLViewer.OBJECT_NAME, false);

      SimpleDataSet<WholesaleNetwork> whNet = new SimpleDataSet<WholesaleNetwork>(WholesaleNetwork.OBJECT_NAME, false);

      DataGridViewButtonColumn clmnLicense;
      DataGridViewButtonColumn clmnWhNet;
      DataGridViewComboBoxColumn clmnDivision;

      DataGridViewCheckBoxColumn clmnCanMnageContracts;
      DataGridViewCheckBoxColumn clmnViewReports;

      public static RightToken ViewReports = RightTokens.Get("FmReports");

      public MainFormEx()
      {
         RightTokens.Tokens.Add(ViewReports);

         cbUserType.Items.Add("Зрители");

         clmnLicense = new DataGridViewButtonColumn();
         clmnLicense.Text = "Контракты";
         clmnLicense.HeaderText = "Контракты";
         clmnLicense.Visible = false;
         clmnLicense.Width = 80;

         clmnWhNet = new DataGridViewButtonColumn();
         clmnWhNet.Text = "Сети";
         clmnWhNet.HeaderText = "Сети";
         clmnWhNet.Visible = false;
         clmnWhNet.Width = 70;

         clmnDivision = new DataGridViewComboBoxColumn();
         clmnDivision.HeaderText = "Подразделение";
         clmnDivision.DataPropertyName = "DivisionNative";
         clmnDivision.Visible = false;
         clmnDivision.ValueMember = "Self";
         clmnDivision.DisplayMember = "DivisionName";
         clmnDivision.Width = 100;

         usersView.Columns.Insert(5, clmnDivision);
         usersView.Columns.Add(clmnLicense);
         usersView.Columns.Add(clmnWhNet);
         usersView.CellContentClick += usersView_CellContentClick;

         clmnCanMnageContracts = new DataGridViewCheckBoxColumn();
         clmnCanMnageContracts.DataPropertyName = "CanManageContracts";
         clmnCanMnageContracts.HeaderText = "Редактор посещений";
         clmnCanMnageContracts.Name = "clmnCanMnageContracts";
         clmnCanMnageContracts.Visible = false;
         clmnCanMnageContracts.Width = 90;

         usersView.Columns.Add(clmnCanMnageContracts);

         clmnViewReports = new DataGridViewCheckBoxColumn();
         clmnViewReports.DataPropertyName = "CanViewReports";
         clmnViewReports.HeaderText = "Просмотр отчетов";
         clmnViewReports.Name = "clmnViewReports";
         clmnViewReports.Visible = false;
         clmnViewReports.Width = 90;

         usersView.Columns.Add(clmnViewReports);

         btnAdd.Visible = true;
         btnDel.Visible = true;
      }

      bool HaveSameLogin(string login, UserDataItem check)
      {
         foreach (Agent a in dsAgents.Data)
            if (a.login == login)
               return true;

         foreach (DivisionManager m in dsManagers.Data)
            if (m.login == login)
               return true;

         foreach(UserDataItem udi in userData)
            if (check.refObject != udi && udi.Login == login)
               return true;

         return false;
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);
         if(clmnCanMnageContracts != null)
         {
            clmnCanMnageContracts.Visible = !agentView;
         }
      }

      protected override void OnUserDataChanging(Resolver resolver)
      {
         bool selectViewer = cbUserType.SelectedIndex == 2;
         if (selectViewer)
         {
            if (resolver.Field == "Login")
            {
               if( HaveSameLogin((string)resolver.NewValue, resolver.item))
               {
                  MessageBox.Show("Такой логин уже существует");
                  resolver.Respond = Resolver.RespondType.CANCEL;
                  return;
               }
            }
         }
         base.OnUserDataChanging(resolver);
      }

      protected override void btnDel_Click(object sender, System.EventArgs e)
      {
         DataGridViewCell cell = usersView.CurrentCell;
         if (cell != null && MessageBox.Show("Удалить запись?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.Yes)
         {
            BindingSource bs = usersView.DataSource as BindingSource;
            bs.RemoveAt(cell.RowIndex);
            userChangesSave.Enabled = true;
         }
      }

      protected override void btnAdd_Click(object sender, System.EventArgs e)
      {
         DivisionManager dm = new DivisionManager();
         UserDataItem udi = AddManager(dm);

         udi.refObject = new NBTLViewer();
         userData.Add(udi);
         userChangesSave.Enabled = true;

         makeDataSource(userData);
      }

      void usersView_CellContentClick(object sender, DataGridViewCellEventArgs e)
      {
         if(cbUserType.SelectedIndex == 2 && usersView.CurrentRow != null)
         {
            UserDataItem udi = usersView.CurrentRow.DataBoundItem as UserDataItem;
            NBTLViewer v = udi.refObject as NBTLViewer;
            
            if (e.ColumnIndex == clmnWhNet.Index)
            {
               List<string> used = new List<string>();
               v.whnetwork.ForEach(x => { used.Add(x.id); });
               List<WhNetEx> list = new List<WhNetEx>();
               foreach (WholesaleNetwork c in whNet.Data)
                  list.Add(new WhNetEx(c, used.Contains(c.id)));

               SetWhNet sc = new SetWhNet();
               sc.Data = list;
               if (sc.ShowDialog() == System.Windows.Forms.DialogResult.OK)
               {
                  v.whnetwork.Clear();
                  foreach (WhNetEx el in sc.Data)
                  {
                     if (el.Used)
                        v.whnetwork.Add(new NBTLViewer.Item(el.ID));
                  }

                  userChangesSave.Enabled = true;
               }
            } else if (e.ColumnIndex == clmnLicense.Index)
            {
               List<string> usedContracts = new List<string>();
               v.contracts.ForEach(x => { usedContracts.Add(x.id); });
               List<ContractEx> contracts = new List<ContractEx>();
               foreach (Contracts c in dsConstracts.Data)
                  contracts.Add(new ContractEx(c, usedContracts.Contains(c.id)));

               SetContracts sc = new SetContracts();
               sc.Contracts = contracts;
               if (sc.ShowDialog() == System.Windows.Forms.DialogResult.OK)
               {
                  v.contracts.Clear();
                  foreach (ContractEx c in sc.Contracts)
                  {
                     if (c.Used)
                        v.contracts.Add(new NBTLViewer.Item(c.ID));
                  }

                  userChangesSave.Enabled = true;
               }
            }
         }
      }

      protected override void SetStatusText()
      {
         base.SetStatusText();
      }

      protected override bool HaveLogin(string login)
      {
         return dsViewers.ContainsKey(login) || base.HaveLogin(login);
      }

      protected override void RefreshUserData()
      {
         bool selectViewer = cbUserType.SelectedIndex == 2;
         clmnId.Visible = !selectViewer;
         user.ReadOnly = !selectViewer;
         if (clmnDivision != null)
            clmnDivision.Visible = selectViewer;
         if(clmnViewReports != null)
            clmnViewReports.Visible = selectViewer;
         if (clmnWhNet != null)
            clmnWhNet.Visible = selectViewer;
         if (clmnLicense != null)
            clmnLicense.Visible = selectViewer;

         if (selectViewer)
         {
            tracking.Visible = false;
            clmnViewReports.Visible = true;
            user.HeaderText = "Наименование";

            usersView.SuspendLayout();

            userData.Clear();
            foreach(NBTLViewer view in dsViewers.Data)
            {
               DivisionManager dm = new DivisionManager();
               dm.division = -1;
               dm.login = view.id;
               dm.password = view.password;
               dm.name = view.name;
               dm.division = view.division;
               dm.rights = view.rights;

               UserDataItem udi = AddManager(dm);
               userData.Add(udi);
               udi.Name = view.name;

               udi.refObject = view;
            }
            makeDataSource(userData);

            usersView.ResumeLayout();
            userChangesSave.Enabled = false;
         }
         else
         {
            base.RefreshUserData();
         }

         btnAdd.Enabled = selectViewer;
         btnDel.Enabled = selectViewer;
      }
      
      protected override bool SaveChanges()
      {
         if (cbUserType.SelectedIndex == 2)
         {
            List<string> usedViwers = new List<string>();
            foreach (NBTLViewer nv in dsViewers.Data)
               usedViwers.Add(nv.id);

            SimpleDataSet<NBTLViewer> saveUsers = new SimpleDataSet<NBTLViewer>(NBTLViewer.OBJECT_NAME, false);
            List<LicensingUsersData> licUsers = new List<LicensingUsersData>();

            foreach(UserDataItem udi in userData)
            {
               if (udi.Name.Length == 0 && udi.Login.Length == 0)
                  continue;

               dsLicensingUsersData.Remove(udi.Login);
               if (dsLicenseTypes.ContainsKey(udi.License.Type))
               {
                  LicensingUsersData lud = new LicensingUsersData();
                  lud.login = udi.Login;
                  lud.licenseID = udi.License.licenseID;
                  licUsers.Add(lud);
               }
               
               NBTLViewer nv = new NBTLViewer();
               nv.id = udi.Login;
               nv.password = udi.Passw;
               nv.name = udi.Name;
               nv.division = udi.Division;
               nv.contracts = ((NBTLViewer)udi.refObject).contracts;
               nv.whnetwork = ((NBTLViewer)udi.refObject).whnetwork;
               nv.rights = udi.Manager.rights;

               usedViwers.Remove(nv.id);
               saveUsers.Add(nv);
            }

            List<IDataSet> wrObj = new List<IDataSet>();
            List<IDataSet> rmvObj = new List<IDataSet>();

            foreach (LicensingUsersData lud in licUsers)
               dsLicensingUsersData[lud.login] = lud;
            
            wrObj.Add(dsLicensingUsersData);
            if (saveUsers.Count > 0)
               wrObj.Add(saveUsers);

            if( usedViwers.Count > 0 )
            {
               SimpleDataSet<NBTLViewer> rmvUsers = new SimpleDataSet<NBTLViewer>(NBTLViewer.OBJECT_NAME, false);
               foreach (string id in usedViwers)
                  rmvUsers.Add(dsViewers[id]);

               rmvObj.Add(rmvUsers);
            }

            if (!DataModule.UpdateDataSet(wrObj, rmvObj, null, config.GetConnection()))
               return false;

            dsViewers.Clear();
            foreach (NBTLViewer nv in saveUsers.Data)
               dsViewers[nv.id] = nv;
            return true;
         }
         return base.SaveChanges();
      }

      protected override void AddUpdDataSet(List<Network.IDataSet> upd)
      {
         dsConstracts.Filter = "not \"id\" is null";
         upd.Add(dsConstracts);
         upd.Add(dsViewers);
         upd.Add(whNet);
      }

      class TreeNode
      {
         public Division d;
         public List<TreeNode> childs = new List<TreeNode>();
         public TreeNode(Division d) { this.d = d; }

         public void AddChilds(DataSet<int, Division> divisions)
         {
            foreach(KeyValuePair<int, Division> kv in divisions)
            {
               if(kv.Value.parent == d.id)
               {
                  TreeNode tn = new TreeNode(kv.Value);
                  childs.Add(tn);

                  tn.AddChilds(divisions);
               }
            }
         }

         public void Sort()
         {
            childs.Sort(CmpDivision);
            foreach (TreeNode tn in childs)
               tn.Sort();
         }

         int CmpDivision(TreeNode l, TreeNode r) { return l.d.name.CompareTo(r.d.name); }

         internal void AddTo(DataGridViewComboBoxCell.ObjectCollection divisions)
         {
            divisions.Add(d);
            foreach (TreeNode tn in childs)
               tn.AddTo(divisions);
         }
      }

      protected override void UpdateLoadedData()
      {
         TreeNode root = null;
         foreach (KeyValuePair<int, Division> kv in dsDivision)
         {
            if (kv.Value.parent == 0)
            {
               root = new TreeNode(kv.Value);
               root.AddChilds(dsDivision);
               break;
            } 
         }
         clmnDivision.Items.Clear();
         if (root != null)
         {
            root.Sort();
            root.AddTo(clmnDivision.Items);
         }
      }
   }
}