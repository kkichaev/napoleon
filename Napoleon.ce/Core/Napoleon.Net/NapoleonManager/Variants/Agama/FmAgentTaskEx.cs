using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;
using GRSoft.Network;
using System.Collections;
using System.Reflection;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   class FmAgentTaskEx : FmAgentTask
   {
      ToolStripComboBox cbOrgs = new ToolStripComboBox();
      ToolStripTextBox tbFilter1 = new ToolStripTextBox();
      ToolStripDropDownButton btnFilter1 = new ToolStripDropDownButton();
      ToolStripTextBox tbFilter2 = new ToolStripTextBox();
      ToolStripDropDownButton btnFilter2 = new ToolStripDropDownButton();
      ToolStripComboBox cbRealClient = new ToolStripComboBox();
      ToolStripButton btnTaskReport = new ToolStripButton();
      ToolStripTextBox tbAddress = new ToolStripTextBox();

      FmFilter fmFilter1 = new FmFilter();
      FmFilter fmFilter2 = new FmFilter();

      SimpleDataSet<OrgTaskInfoEx> dsOrgTask = new SimpleDataSet<OrgTaskInfoEx>("Result", false);

      private int idxOrgs = 0;
      private int idxRealClient = 0;

      public FmAgentTaskEx()
      {
         Size = new Size(900, 450);
         ToolStrip ts = new ToolStrip();
         ts.Name = "FilterStrip";
         ts.TabIndex = 5;
         ts.Dock = DockStyle.Top;

         ToolStripLabel label0 = new ToolStripLabel();
         label0.Text = "Адрес";
         label0.Padding = new Padding(10, 0, 10, 0);

         ToolStripLabel label1 = new ToolStripLabel();
         label1.Text = "И";
         label1.Padding = new Padding(10, 0, 10, 0);
         ToolStripLabel label2 = new ToolStripLabel();
         label2.Text = "И";
         label2.Padding = new Padding(10, 0, 10, 0);
         ToolStripLabel label3 = new ToolStripLabel();
         label3.Text = "Фильтр";
         label3.Padding = new Padding(0, 0, 10, 0);
         ToolStripLabel label4 = new ToolStripLabel();
         label4.Text = "И";
         label4.Padding = new Padding(0, 0, 10, 0);
         ToolStripSeparator sep = new ToolStripSeparator();
         ToolStripButton btnClearFilter = new ToolStripButton();
         btnClearFilter.Click += new EventHandler(btnClearFilter_Click);
         btnClearFilter.Image = Resources.edit_clear_4;
         btnClearFilter.ToolTipText = "Сбросить фильтр";
         ToolStripButton btnCollectiveTask = new ToolStripButton();
         btnCollectiveTask.Image = Resources.quest_doc;
         btnCollectiveTask.Click += new EventHandler(btnCollectiveTask_Click);
         btnCollectiveTask.ToolTipText = "Создать задачи для точек по выбранному фильтру";
         ToolStripButton btnCollectiveDelTask = new ToolStripButton();
         btnCollectiveDelTask.Image = Resources.del;
         btnCollectiveDelTask.Click += new EventHandler(btnCollectiveDelTask_Click);
         btnCollectiveDelTask.ToolTipText = "Удалить задачи";

         tbFilter1.ReadOnly = true;
         tbFilter1.BackColor = Color.White;
         btnFilter1.Click += new EventHandler(btnFilter1_Click);

         tbFilter2.ReadOnly = true;
         tbFilter2.BackColor = Color.White;
         btnFilter2.Click += new EventHandler(btnFilter2_Click);

         ts.Items.AddRange(new ToolStripItem[] {label3, cbOrgs, label0, tbAddress, label1, tbFilter1, btnFilter1,
            label2, tbFilter2, btnFilter2, label4, cbRealClient, sep, 
            btnClearFilter, btnCollectiveTask, btnCollectiveDelTask });         

         Control[] controls = new Control[Controls.Count + 1];
         controls[0] = dgvTask;
         controls[1] = ts;
         Controls.RemoveAt(Controls.IndexOf(dgvTask));

         for (int i = 0; i < Controls.Count; i++)
            controls[i + 2] = Controls[i];

         Controls.Clear();

         for (int i = 0; i < controls.Length; i++)
            Controls.Add(controls[i]);

         DataGridViewTextBoxColumn itemName = new DataGridViewTextBoxColumn();
         itemName.DataPropertyName = "ItemName";
         itemName.HeaderText = "Адрес";
         itemName.Name = "itemName";
         itemName.DisplayIndex = 1;
         itemName.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         DataGridViewTextBoxColumn filter1 = new DataGridViewTextBoxColumn();
         filter1.DataPropertyName = "Filter1";
         filter1.HeaderText = "Фильтр 1";
         filter1.Name = "Filter1";
         filter1.DisplayIndex = 2;
         filter1.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         filter1.FillWeight = 50;
         DataGridViewTextBoxColumn filter2 = new DataGridViewTextBoxColumn();
         filter2.DataPropertyName = "Filter2";
         filter2.HeaderText = "Фильтр 2";
         filter2.Name = "Filter2";
         filter2.DisplayIndex = 3;
         filter2.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         filter2.FillWeight = 50;
         DataGridViewTextBoxColumn realClient = new DataGridViewTextBoxColumn();
         realClient.DataPropertyName = "RealClient";
         realClient.HeaderText = "Реалклиент";
         realClient.Name = "RealClient";
         realClient.DisplayIndex = 4;
         realClient.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         realClient.FillWeight = 50;

         dgvTask.Columns.Add(itemName);
         dgvTask.Columns.Add(filter1);
         dgvTask.Columns.Add(filter2);
         dgvTask.Columns.Add(realClient);

         cbOrgs.SelectedIndexChanged += new EventHandler(cbOrgs_SelectedIndexChanged);
         cbRealClient.SelectedIndexChanged += new EventHandler(cbOrgs_SelectedIndexChanged);

         btnTaskReport.Name = "btnTaskReport";
         btnTaskReport.Click += new EventHandler(btnTaskReport_Click);
         btnTaskReport.Image = Resources.excel;
         btnTaskReport.ToolTipText = "Отчет по задачам Excel";

         toolStrip1.Items.Add(btnTaskReport);

         tbAddress.TextChanged += new EventHandler(tbAddress_TextChanged);
      }

      void tbAddress_TextChanged(object sender, EventArgs e)
      {
         ApplyFilter();
      }

      void btnTaskReport_Click(object sender, EventArgs e)
      {
         new FmTaskReport().Show();
      }

      private void ApplyORFilter(FmFilter filter, ToolStripTextBox textBox)
      {
         if (filter.ShowDialog() == DialogResult.OK)
         {
            textBox.Text = filter.ItemsText;
            ApplyFilter();
         }
      }

      void btnFilter1_Click(object sender, EventArgs e)
      {
         ApplyORFilter(fmFilter1, tbFilter1);
      }

      void btnFilter2_Click(object sender, EventArgs e)
      {
         ApplyORFilter(fmFilter2, tbFilter2);
      }

      private void ApplyFilter()
      {
         BindingListView<OrgTaskInfoEx> bs = (BindingListView<OrgTaskInfoEx>)dgvTask.DataSource;
         if (bs != null)
         {
            bs.ApplyFilter(delegate(OrgTaskInfoEx info)
            {
               bool result = (cbOrgs.SelectedIndex > 0 ? info.Name.Equals(((OrgTaskInfo)cbOrgs.SelectedItem).Name) : true) &&
                  fmFilter1.CheckItem(info.f1) &&
                  fmFilter2.CheckItem(info.f2) &&
                  (cbRealClient.SelectedIndex > 0 ? info.rc.Equals(((OrgTaskInfoEx)cbRealClient.SelectedItem).rc) : true) &&
                  (tbAddress.Text.Trim().Length > 0 ? info.address.ToUpper().Contains(tbAddress.Text.Trim().ToUpper()) : true);

               return result;
            });

            dgvTask.Update();
         }
      }

      void btnCollectiveDelTask_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show("Задачи по установленным параметрам будут удалены, удалить?", "Вопрос",
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            List<string> orgidList = new List<string>();
            List<string> unitidList = new List<string>();

            foreach (DataGridViewRow row in dgvTask.Rows)
            {
               OrgTaskInfoEx info = InflateDataBound(row.DataBoundItem) as OrgTaskInfoEx;
               orgidList.Add(info.ido);
               unitidList.Add(info.id);
            }

            DataSet<string, OrgTaskEx> dsRemOrgTask = new DataSet<string, OrgTaskEx>(OrgTaskEx.OBJECT_NAME, false);
            StringBuilder sbOrg = new StringBuilder();
            if (orgidList.Count > 0)
            {
               sbOrg.Append("\"groupid\" in (");
               for (int i = 0; i < orgidList.Count; i++)
               {
                  sbOrg.Append("'").Append(orgidList[i]).Append("'");

                  if (i < orgidList.Count - 1)
                     sbOrg.Append(",");
               }
               sbOrg.Append(")");
            }

            StringBuilder sbUnits = new StringBuilder();
            if (unitidList.Count > 0)
            {
               sbUnits.Append("\"orgid\" in (");
               for (int i = 0; i < unitidList.Count; i++)
               {
                  sbUnits.Append(unitidList[i]);

                  if (i < unitidList.Count - 1)
                     sbUnits.Append(",");
               }
               sbUnits.Append(")");
            }

            dsRemOrgTask.Filter = sbOrg.ToString() + (sbOrg.ToString().Length > 0 ? " and " : string.Empty) +
               sbUnits.ToString() + ((sbOrg.Length + sbUnits.Length) > 0 ? " and " : string.Empty) +
               " \"userid\" = '" + ((Agent)cbAgent.SelectedItem).id +
               string.Format("' and start >= ToDate('{0:dd/MM/yyyy}') and  finish < ToDate('{1:dd/MM/yyyy}')",
                dtpStart.Value.Date, dtpFinish.Value.Date.AddDays(1));
            DataModule.RemoveDataSet(dsRemOrgTask, Config.GetConfig().GetConnection());
         }
      }

      protected override object InflateDataBound(object dataBoundItem)
      {
         return ((ObjectView<OrgTaskInfoEx>)dataBoundItem).Object;
      }

      void btnCollectiveTask_Click(object sender, EventArgs e)
      {
         OrgTask task = new OrgTask();
         task.start = dtpStart.Value.Date;
         task.finish = dtpFinish.Value.Date;

         task = FmAgentTaskEdit.EditTask(task);
         
         if (task != null && dgvTask.Rows.Count > 0)
         {
            DataSet<string, OrgTaskEx> dsTask = new DataSet<string, OrgTaskEx>(OrgTask.OBJECT_NAME, false);

            foreach (DataGridViewRow row in dgvTask.Rows)
            {
               OrgTaskInfoEx info = InflateDataBound(row.DataBoundItem) as OrgTaskInfoEx;
               OrgTaskEx newTask = CloneTask(task);
               newTask.groupid = info.ido;
               newTask.orgid = info.id;
               newTask.userid = ((Agent)cbAgent.SelectedItem).id;
               dsTask.Add(newTask.id, newTask);
            }

            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsTask);
            if (!DataModule.UpdateDataSet(updSet, null, null, Config.GetConfig().GetConnection()))
               MessageBox.Show("Ошибка записи в базу данных");
         }
      }

      private OrgTaskEx CloneTask(OrgTask src)
      {
         OrgTaskEx result = new OrgTaskEx();

         result.id = Task.GenId();
         result.orgid = src.orgid;
         result.userid = src.userid;
         result.start = src.start;
         result.finish = src.finish;
         result.text = src.text;

         return result;
      }


      protected override void StartRefresh()
      {
         idxOrgs = cbOrgs.SelectedIndex;
         idxRealClient = cbRealClient.SelectedIndex;
      }

      void btnClearFilter_Click(object sender, EventArgs e)
      {
         if (cbOrgs.Items.Count > 0 && cbOrgs.SelectedIndex > 0)
            cbOrgs.SelectedIndex = 0;

         if (cbRealClient.Items.Count > 0 && cbRealClient.SelectedIndex > 0)
            cbRealClient.SelectedIndex = 0;
      
         fmFilter1.SetSelected(false);
         tbFilter1.Text = fmFilter1.ItemsText;

         fmFilter2.SetSelected(false);
         tbFilter2.Text = fmFilter2.ItemsText;

         BindingListView<OrgTaskInfoEx> bs = (BindingListView<OrgTaskInfoEx>)dgvTask.DataSource;
         bs.RemoveFilter();
      }

      void cbOrgs_SelectedIndexChanged(object sender, EventArgs e)
      {
         ApplyFilter();

      }
     
      protected override IDataSet GetResultDataSet()
      {
         return dsOrgTask;
      }

      protected override IList CollectData()
      {
         return CollectDataSource<OrgTaskInfoEx>(new Comparison<OrgTaskInfoEx>(
            delegate(OrgTaskInfoEx r1, OrgTaskInfoEx r2) 
               {
                  int result = 0;
                  result = r1.name.CompareTo(r2.name);

                  if (result == 0)
                     result = r1.address.CompareTo(r1.address);

                  return result;
               }));
      }

      protected override void FillData()
      {
         base.FillData();

         IList data = (IList)dgvTask.DataSource;

         cbOrgs.Items.Clear();
         //cbFilter1.Items.Clear();
         //cbFilter2.Items.Clear();

         List<string> ids = new List<string>();
         List<string> f1 = new List<string>();
         List<string> f2 = new List<string>();
         List<string> rc = new List<string>();

         cbOrgs.Items.Add(new OrgTaskInfoAllEx());
         List<OrgTaskInfo> realClient = new List<OrgTaskInfo>();

         foreach(object o in data)
         {
            ObjectView<OrgTaskInfoEx> obj = (ObjectView<OrgTaskInfoEx>)o;
            OrgTaskInfoEx i = (OrgTaskInfoEx)obj.Object;

            if (!ids.Contains(i.id))
            {
               ids.Add(i.id);
               cbOrgs.Items.Add(i);
            }

            if (!f1.Contains(i.f1) && i.f1.Length > 0)
               f1.Add(i.f1);

            if (!f2.Contains(i.f2) && i.f2.Length > 0)
               f2.Add(i.f2);

            if (!rc.Contains(i.rc) && i.rc.Length > 0)
            {
               rc.Add(i.rc);
               realClient.Add(new OrgTaskInfoRC(i));
            }
         }
         
         //cbFilter1.Items.Add(new OrgTaskInfoAllEx());
         //cbFilter2.Items.Add(new OrgTaskInfoAllEx());
         OrgTaskCmpName comparator = new OrgTaskCmpName();
         f1.Sort();
         fmFilter1.Values = f1;
         tbFilter1.Text = fmFilter1.ItemsText;

         f2.Sort();
         fmFilter2.Values = f2;
         tbFilter2.Text = fmFilter2.ItemsText;
         //cbFilter1.Items.AddRange(filter1.ToArray());
         //cbFilter2.Items.AddRange(filter2.ToArray());

         realClient.Sort(comparator);
         cbRealClient.Items.Clear();
         cbRealClient.Items.Add(new OrgTaskInfoAllEx());
         cbRealClient.Items.AddRange(realClient.ToArray()); 

         if (idxOrgs > -1 && cbOrgs.Items.Count > idxOrgs)
            cbOrgs.SelectedIndex = idxOrgs;
         else
            cbOrgs.SelectedIndex = 0;

         //if (idxFilter1 > -1 && cbFilter1.Items.Count > idxFilter1)
         //   cbFilter1.SelectedIndex = idxFilter1;
         //else
         //   cbFilter1.SelectedIndex = 0;

         if (idxRealClient > -1 && cbRealClient.Items.Count > idxRealClient)
            cbRealClient.SelectedIndex = idxRealClient;
         else
            cbRealClient.SelectedIndex = 0;
      }
   }

   class OrgTaskCmpName : Comparer<OrgTaskInfo>
   {
      public override int Compare(OrgTaskInfo x, OrgTaskInfo y)
      {
         return x.ToString().CompareTo(y.ToString());
      }
   }

   class OrgTaskInfoClonable : OrgTaskInfoEx
   {
      public OrgTaskInfoClonable(OrgTaskInfoEx source)
      { 
         FieldInfo[] src = source.GetType().GetFields(BindingFlags.Public | BindingFlags.Instance);

         foreach (FieldInfo f in src)
         {
            FieldInfo dst = GetType().GetField(f.Name);

            if(dst != null)
               dst.SetValue(this, f.GetValue(source));
         }
      }
   }

   class OrgTaskInfoRC : OrgTaskInfoClonable
   {
      public OrgTaskInfoRC(OrgTaskInfoEx source)
         : base(source)
      {
      }

      public override string ToString()
      {
         return rc;
      }
   }

   class OrgTaskInfoFilter1Ex : OrgTaskInfoClonable
   {
      public OrgTaskInfoFilter1Ex(OrgTaskInfoEx source)
         : base(source)
      {
      }

      public override string ToString()
      {
         return f1;
      }
   }

   class OrgTaskInfoFilter2Ex : OrgTaskInfoClonable
   {
      public OrgTaskInfoFilter2Ex(OrgTaskInfoEx source)
         : base(source)
      {
      }

      public override string ToString()
      {
         return f2;
      }
   }

   class OrgTaskInfoAllEx : OrgTaskInfoEx
   {
      public override string ToString()
      {
         return "Все";
      }
   }

   class OrgTaskInfoEx : OrgTaskInfo
   {
      public string id = string.Empty;
      public string ido = string.Empty;
      public string address = string.Empty;
      public string f1 = string.Empty;
      public string f2 = string.Empty;
      public string rc = string.Empty;

      public string ItemName { get { return address; } }
      public string Filter1 { get { return f1; } }
      public string Filter2 { get { return f2; } }
      public string RealClient { get { return rc; } }

      public override string ToString()
      {
         return Name;
      }
   }

   class OrgTaskEx : OrgTask
   {
      public string groupid = String.Empty;

      public static OrgTaskEx Create(OrgTask source)
      {
         OrgTaskEx result = new OrgTaskEx();
         FieldInfo[] src = source.GetType().GetFields(BindingFlags.Public | BindingFlags.Instance);

         foreach (FieldInfo f in src)
         {
            FieldInfo dst = result.GetType().GetField(f.Name);

            if (dst != null)
               dst.SetValue(result, f.GetValue(source));
         }

         return result;
      }
   }
}

