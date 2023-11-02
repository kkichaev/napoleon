using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class FmReportsEx : FmReports
   {
      readonly static string AGENT_ORDER_REPORT = "agent_ordes_report";
      ComboBox cbFirms, cbThermal, cbGroupBy;
      Label label1, label3, label4;
      CheckBox cbPackets, cbPiece, cbTotal;
      GroupBox gbData;

      public FmReportsEx()
      {
         AddControls();
         LayoutControls();
      }

      public override void ResetPanel()
      {
         base.ResetPanel();

         cbFirms.Enabled = false;
         cbThermal.Enabled = false;
      }

      protected override void btnOrder_Click(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         rbDiv.Enabled = true;
         rbDiv.Checked = true;
         gbDate.Enabled = true;
         gbField.Enabled = true;
         btnDoReport.Enabled = true;
         cbFirms.Enabled = true;
         cbThermal.Enabled = true;

         gbData.Enabled = true;

         selectedReport = AGENT_ORDER_REPORT;
      }

      protected override void BeforeRefresh(List<IDataSet> upd)
      {         
      }

      protected override void DataLoaded(Manager m)
      {
         cbFirms.Items.Clear();
         cbFirms.SelectedIndex = cbFirms.Items.Add("<Все>");
         Factory.GetFactories().ForEach((x) =>
         {
            cbFirms.Items.Add(x);
         });
      }

      void AddControls()
      {
         cbFirms = new ComboBox();
         cbThermal = new ComboBox();
         label1 = new Label();
         label3 = new Label();

         this.cbFirms.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
      | System.Windows.Forms.AnchorStyles.Right)));
         this.cbFirms.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbFirms.FormattingEnabled = true;
         this.cbFirms.Location = new System.Drawing.Point(117, 68);
         this.cbFirms.Name = "cbFirms";
         this.cbFirms.Size = new System.Drawing.Size(228, 21);

         this.cbThermal.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbThermal.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbThermal.FormattingEnabled = true;
         this.cbThermal.Items.AddRange(new object[] {
            "<Все>",
            "Охл",
            "Зам"});
         this.cbThermal.Location = new System.Drawing.Point(117, 95);
         this.cbThermal.Name = "cbThermal";
         this.cbThermal.Size = new System.Drawing.Size(228, 21);

         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(6, 71);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(54, 13);
         this.label1.TabIndex = 41;
         this.label1.Text = "Фабрика";

         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(6, 98);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(61, 13);
         this.label3.TabIndex = 42;
         this.label3.Text = "Состояние";


         gbDivision.Controls.AddRange(new Control[]
         {
            cbFirms, cbThermal, label1, label3,
         });

         gbDivision.Height += 80;

         gbData = new GroupBox();
         this.label4 = new System.Windows.Forms.Label();
         this.cbGroupBy = new System.Windows.Forms.ComboBox();
         this.cbTotal = new System.Windows.Forms.CheckBox();
         this.cbPackets = new System.Windows.Forms.CheckBox();
         this.cbPiece = new System.Windows.Forms.CheckBox();

         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(4, 55);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(92, 13);
         this.label4.TabIndex = 4;
         this.label4.Text = "Группировать по";

         this.cbGroupBy.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbGroupBy.FormattingEnabled = true;
         this.cbGroupBy.Items.AddRange(new object[] {
            "Контрагентам",
            "Торговым агентам"});
         this.cbGroupBy.Location = new System.Drawing.Point(102, 52);
         this.cbGroupBy.Name = "cbGroupBy";
         this.cbGroupBy.Size = new System.Drawing.Size(190, 21);
         this.cbGroupBy.TabIndex = 3;
         // 
         // cbTotal
         // 
         this.cbTotal.AutoSize = true;
         this.cbTotal.Location = new System.Drawing.Point(161, 20);
         this.cbTotal.Name = "cbTotal";
         this.cbTotal.Size = new System.Drawing.Size(94, 17);
         this.cbTotal.TabIndex = 2;
         this.cbTotal.Text = "Только итоги";
         this.cbTotal.UseVisualStyleBackColor = true;
         // 
         // cbPackets
         // 
         this.cbPackets.AutoSize = true;
         this.cbPackets.Checked = true;
         this.cbPackets.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPackets.Location = new System.Drawing.Point(76, 20);
         this.cbPackets.Name = "cbPackets";
         this.cbPackets.Size = new System.Drawing.Size(76, 17);
         this.cbPackets.TabIndex = 1;
         this.cbPackets.Text = "Упаковки";
         this.cbPackets.UseVisualStyleBackColor = true;
         // 
         // cbPiece
         // 
         this.cbPiece.AutoSize = true;
         this.cbPiece.Checked = true;
         this.cbPiece.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPiece.Location = new System.Drawing.Point(7, 20);
         this.cbPiece.Name = "cbPiece";
         this.cbPiece.Size = new System.Drawing.Size(57, 17);
         this.cbPiece.TabIndex = 0;
         this.cbPiece.Text = "Штуки";
         this.cbPiece.UseVisualStyleBackColor = true;

         this.gbData.Controls.Add(this.label4);
         this.gbData.Controls.Add(this.cbGroupBy);
         this.gbData.Controls.Add(this.cbTotal);
         this.gbData.Controls.Add(this.cbPackets);
         this.gbData.Controls.Add(this.cbPiece);
         this.gbData.Location = new System.Drawing.Point(0, 360);
         this.gbData.Name = "gbData";
         this.gbData.Size = new System.Drawing.Size(351, 88);
         this.gbData.TabIndex = 33;
         this.gbData.TabStop = false;
         this.gbData.Text = "Данные";
         gbData.Dock = System.Windows.Forms.DockStyle.Top;

         List<Control> ctrls = new List<Control>();
         Control.ControlCollection cc = splitContainer1.Panel2.Controls;
         foreach(Control c in cc)
         {
            ctrls.Add(c);
            if (c == gbField)
            {
               //gbField.Visible = true;
               //gbData.Location = new System.Drawing.Point(0, gbField.Bottom);
               ctrls.Add(gbData);
            }
            if(c == btnDoReport)
            {
               c.Location = new System.Drawing.Point(c.Location.X, c.Location.Y + 100);
            }
         }
         cc.Clear();
         cc.AddRange(ctrls.ToArray());

         Height += 100;
      }

      protected override Network.DataObject CreateParam(string selectedReport)
      {
         if(selectedReport == AGENT_ORDER_REPORT)
         {
            AgentOrderReportData data = new AgentOrderReportData();
            if (rbDiv.Checked)
            {
               Division sel = (Division)cbDivision.SelectedItem;
               data.division = sel.id;
               data.name = sel.name;
            }
            else
            {
               foreach (System.Object i in cbAgent.CheckedItems)
               {
                  Division.DivisionAgent a = i as Division.DivisionAgent;
                  if (a != null)
                  {
                     data.agent = a.id;
                     data.name = a.AgentName;
                     break;
                  }
               }
            }
            if (cbFirms.SelectedIndex > 0)
            {
               Factory fabric = (Factory)cbFirms.SelectedItem;
               data.firm = fabric.id;
               data.firmName = fabric.name;
            }
            if (cbThermal.SelectedIndex > 0)
               data.thState = (String)cbThermal.SelectedItem;

            data.start = dtpDateStart.Value.Date;
            data.end = dtpDateFinish.Value.Date;
            if(data.end < data.start)
            {
               DateTime d = data.start;
               data.start = data.end;
               data.end = d;
            }

            //if (rbByCreatedFld.Checked)
            //   data.docsFromCreated = 1;

            if (cbPiece.Checked)
               data.useQty = 1;
            if (cbPackets.Checked)
               data.usePack = 1;
            if (cbTotal.Checked)
               data.totals = 1;

            if ((data.useQty + data.usePack + data.totals) == 0)
               data.useQty = 1;

            data.groupBy = cbGroupBy.SelectedIndex;
            if (Config.GetConfig().isFullOrgName)
               data.fullOrgName = 1;

            return data;
         }
         return base.CreateParam(selectedReport);
      }
   }

   class AgentOrderReportData : GRSoft.Network.DataObject
   {
      public int division = -1;
      public String agent = "";
      public String name = "";
      public String firm = "";
      public String thState = "";
      public String firmName = "";

      public DateTime start = DateTime.Now;
      public DateTime end = DateTime.Now;

      public int docsFromCreated = 0;
      public int usePack = 0;
      public int useQty = 0;
      public int totals = 0;

      // 0 - byOrgs, 1 - byAgents
      public int groupBy = 0;
      public int fullOrgName = 0;
   }
}
