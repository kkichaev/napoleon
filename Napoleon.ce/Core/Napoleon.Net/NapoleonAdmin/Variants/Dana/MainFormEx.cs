using System.Collections.Generic;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonAdmin
{
    public class MainFormEx : MainForm
    {
        SimpleDataSet<ProgramSettings> prgSettings = new SimpleDataSet<ProgramSettings>(ProgramSettings.OBJECT_NAME);
        ToolStripButton tsbSettings;
        DataGridViewCheckBoxColumn clmnDisableSave;
        DataGridViewCheckBoxColumn[] rightColumns;

        public MainFormEx()
        {
            tsbSettings = new ToolStripButton();

            tsbSettings.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            //tsb.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.add;
            //tsb.ImageTransparentColor = System.Drawing.Color.Magenta;
            tsbSettings.Name = "btnSettings";
            tsbSettings.Size = new System.Drawing.Size(23, 22);
            tsbSettings.Text = "Настройки программы";
            tsbSettings.Click += ShowSettings;

            toolStrip1.Items.Add(tsbSettings);

            clmnDisableSave = new DataGridViewCheckBoxColumn();
            clmnDisableSave.DataPropertyName = "DisableSave";
            clmnDisableSave.HeaderText = "Сценарии и матрицы";
            clmnDisableSave.Name = "clmnCanDisableFirms";
            clmnDisableSave.Visible = false;
            clmnDisableSave.Width = 90;

            rightColumns = new DataGridViewCheckBoxColumn[] { clmnDisableSave };
            usersView.Columns.AddRange(rightColumns);
        }

        protected override void usersView_CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
        {
            base.usersView_CurrentCellDirtyStateChanged(sender, e);
            foreach (DataGridViewCheckBoxColumn c in rightColumns)
                if (usersView.CurrentCell.ColumnIndex == c.DisplayIndex)
                {
                    usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
                    break;
                }
        }

        private void ShowSettings(object sender, System.EventArgs e)
        {
            DataGridViewRow row = usersView.CurrentRow;
            if (row != null)
            {
                UserDataItem udi = (UserDataItem)row.DataBoundItem;
                FmProgSettings form = new FmProgSettings();
                form.SetAgent(udi.Agent, prgSettings, config.GetConnection());
                form.Show();
            }
        }

        protected override void PrepareViewComponents(bool agentView)
        {
            base.PrepareViewComponents(agentView);

            if(tsbSettings != null)
                tsbSettings.Enabled = agentView;

            if (clmnDisableSave != null)
            {
                clmnDisableSave.Visible = !agentView;
            }
        }

        protected override void AddUpdDataSet(List<IDataSet> upd)
        {
            prgSettings.Filter = "not \"userid\" is null";
            upd.Add(prgSettings);
        }
    }

    public class ProgramSettings : Network.DataObject
    {
        public static string OBJECT_NAME = "ProgramSettings";

        public string id = "";
        public string type = "";
        public string userid = "";
        public string value = "";
    }
}