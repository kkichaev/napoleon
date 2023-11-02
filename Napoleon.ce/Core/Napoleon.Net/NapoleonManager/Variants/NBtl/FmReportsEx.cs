using GRSoft.Network;
using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmReportsEx : FmReports
   {
      const string DISTRIB_REPORT = "distrib_report";
      const string QUETS_REP_4 = "quest_rep_4";
      GroupBox gbTimeRep;
      ComboBox cbReportType;
      ContractParams contractParams = new ContractParams();
      RadioButton rbPokaz = new RadioButton();
      RadioButton rbHorizont2 = new RadioButton();
      RadioButton rbVar3 = new RadioButton();
      RadioButton rbVar4 = new RadioButton();
      ComboBox cbSlsnet;
      ComboBox cbCity;


      Utils.RichButton visitDetails = new Utils.RichButton();

      int doReportLocation = 0;
      public FmReportsEx()
      {
         Size = new System.Drawing.Size(1213, 700);

         panel5.Visible = false;
         panel6.Visible = false;

         btnRouteList.Visible = false;
         btnWorkReport.Description = "Анализ посещяемости торговых точек по маршруту";

         panel8.Visible = true;
         panel7.Visible = true;

         btnWorkTime.Visible = false;
         btnVisitTIme.Visible = false;

         GRSoft.NapoleonManager.Utils.RichButton btn = new Utils.RichButton();

         btn.Caption = "Отчет по работе в точке";
         btn.Checked = false;
         btn.Description = "Время пребывания в точке";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         btn.Location = new System.Drawing.Point(495, 3);
         btn.Margin = new System.Windows.Forms.Padding(4);
         btn.Name = "btnTimeRep";
         btn.Padding = new System.Windows.Forms.Padding(1);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.TabIndex = 0;
         btn.Click += TimeReportClick;
         panel4.Controls.Add(btn);

         panel7.Controls.Clear();
         panel7.Height = 135;

         btn = new Utils.RichButton();
         btn.Caption = "Отчет по контрактам";
         btn.Checked = false;
         btn.Description = "Доля полки";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         btn.Location = new System.Drawing.Point(3, 3);
         btn.Margin = new System.Windows.Forms.Padding(4);
         btn.Name = "btnShelfShare";
         btn.Padding = new System.Windows.Forms.Padding(1);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.TabIndex = 0;
         btn.Click += ShelfShareClick;
         panel7.Controls.Add(btn);

         btn = new Utils.RichButton();
         btn.Caption = "Отчет по остаткам товара";
         btn.Checked = false;
         btn.Description = "";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         btn.Location = new System.Drawing.Point(249, 3);
         btn.Margin = new System.Windows.Forms.Padding(4);
         btn.Name = "btnInventory";
         btn.Padding = new System.Windows.Forms.Padding(1);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.TabIndex = 0;
         btn.Click += InventoryClick;
         panel7.Controls.Add(btn);

         btn = new Utils.RichButton();
         btn.Caption = "Отчет по дистрибуции";
         btn.Checked = false;
         btn.Description = "";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         btn.Location = new System.Drawing.Point(495, 3);
         btn.Margin = new System.Windows.Forms.Padding(4);
         btn.Name = "btnDistrib";
         btn.Padding = new System.Windows.Forms.Padding(1);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.TabIndex = 0;
         btn.Click += DistribClick;

         panel7.Controls.Add(btn);
         btn = new Utils.RichButton();
         btn.Caption = "Отчет по мониторингу цен";
         btn.Checked = false;
         btn.Description = "";
         btn.Icon = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         btn.Location = new System.Drawing.Point(3, 67);
         btn.Margin = new System.Windows.Forms.Padding(4);
         btn.Name = "btnDistrib";
         btn.Padding = new System.Windows.Forms.Padding(1);
         btn.Size = new System.Drawing.Size(240, 55);
         btn.TabIndex = 0;
         btn.Click += PriceMonitoringClick;
         panel7.Controls.Add(btn);

         gbTimeRep = new GroupBox();
         gbTimeRep.Location = new System.Drawing.Point(0, 218);
         gbTimeRep.Size = new System.Drawing.Size(351, 132);
         gbTimeRep.Dock = DockStyle.Top;

         contractParams.Dock = DockStyle.Top;
         contractParams.Visible = false;

         Label l = new Label();
         l.AutoSize = true;
         l.Location = new System.Drawing.Point(20, 18);
         l.Text = "Вид отчета";
         gbTimeRep.Controls.Add(l);

         cbReportType = new ComboBox();
         cbReportType.Location = new System.Drawing.Point(118, 14);
         cbReportType.Size = new System.Drawing.Size(228, 21);
         cbReportType.DropDownStyle = ComboBoxStyle.DropDownList;
         cbReportType.Items.AddRange(new object[]
         {
            "1", "2", "3", "4"
         });
         gbTimeRep.Controls.Add(cbReportType);

         l = new Label();
         l.AutoSize = true;
         l.Location = new System.Drawing.Point(20, 54);
         l.Text = "Сеть";
         gbTimeRep.Controls.Add(l);

         DataSet<string, Slsnet> ds = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME);
         cbSlsnet = new ComboBox();
         cbSlsnet.Location = new System.Drawing.Point(118, 52);
         cbSlsnet.Size = new System.Drawing.Size(228, 21);
         cbSlsnet.DropDownStyle = ComboBoxStyle.DropDownList;

         if (ds != null) 
         {
            List<Slsnet> list = (List<Slsnet>)ds.ValueList;
            list.ForEach(x => cbSlsnet.Items.Add(x));
            list.Sort((x, y) => x.Name.CompareTo(y.Name));

            if(cbSlsnet.Items.Count > 0)
               cbSlsnet.SelectedIndex = 0;
         }

         gbTimeRep.Controls.Add(cbSlsnet);

         l = new Label();
         l.AutoSize = true;
         l.Location = new System.Drawing.Point(20, 92);
         l.Text = "Город";
         gbTimeRep.Controls.Add(l);

         DataSet<string, City> dsCity = (DataSet<string, City>)DataModule.Get(City.OBJECT_NAME);
         cbCity = new ComboBox();
         cbCity.Location = new System.Drawing.Point(118, 92);
         cbCity.Size = new System.Drawing.Size(228, 21);
         cbCity.DropDownStyle = ComboBoxStyle.DropDownList;

         if (ds != null) 
         {
            List<City> list = (List<City>)dsCity.ValueList;
            list.ForEach(x => cbCity.Items.Add(x));
            list.Sort((x, y) => x.Name.CompareTo(y.Name));

            if(cbCity.Items.Count > 0)
               cbCity.SelectedIndex = 0;
         }

         gbTimeRep.Controls.Add(cbCity);

         List<Control> ctrls = new List<Control>();
         Control.ControlCollection cc = splitContainer1.Panel2.Controls;
         foreach (Control c in cc)
         {
            ctrls.Add(c);
            if (c == gbTime)
            {
               ctrls.Add(contractParams);
               ctrls.Add(gbTimeRep);
            }
            //if (c == btnDoReport)
            //{
            //   c.Location = new System.Drawing.Point(c.Location.X, c.Location.Y + 40);
            //}
         }
         cc.Clear();
         cc.AddRange(ctrls.ToArray());

         doReportLocation = btnDoReport.Top;


         gbLayout.Size = new System.Drawing.Size(gbLayout.Width, gbLayout.Height + 25);

         rbPokaz.Text = "Показатели";
         rbPokaz.Location = new System.Drawing.Point(242,13);

         rbHorizont2.Text = "Горизотнальный №2";
         rbHorizont2.Location = new System.Drawing.Point(7, 40);
         rbHorizont2.Size = new System.Drawing.Size(140, 18);

         rbVar3.Text = "Вариант 3";
         rbVar3.Location = new System.Drawing.Point(148, 40);
         rbVar3.Size = new System.Drawing.Size(85, 18);

         rbVar4.Text = "Вариант 4";
         rbVar4.Location = new System.Drawing.Point(243, 40);
         rbVar4.Size = new System.Drawing.Size(85, 18);

         gbLayout.Controls.Add(rbPokaz);
         gbLayout.Controls.Add(rbHorizont2);
         gbLayout.Controls.Add(rbVar3);
         gbLayout.Controls.Add(rbVar4);

         visitDetails = new Utils.RichButton();
         visitDetails.Caption = "Детальный отчет по посещениям";
         visitDetails.Checked = false;
         visitDetails.Description = "Детальный отчет по посещениям";
         visitDetails.Icon = global::GRSoft.NapoleonManager.Properties.Resources.remnants_report;
         visitDetails.Location = new System.Drawing.Point(3, 67);
         visitDetails.Margin = new System.Windows.Forms.Padding(4);
         visitDetails.Name = "visitDetailsVisitDetailRep";
         visitDetails.Padding = new System.Windows.Forms.Padding(1);
         visitDetails.Size = new System.Drawing.Size(240, 55);
         visitDetails.TabIndex = 0;
         visitDetails.Click += DetailVisitClick;
         panel2.Controls.Add(visitDetails);

#if NbtlMonitor
         panel8.Visible = false;
         panel7.Visible = false;
         panel3.Visible = false;
         panel4.Visible = false;

         visitDetails.Location = new System.Drawing.Point(3, 6);
         panel2.Controls.Clear();
         panel2.Controls.Add(visitDetails);
         DetailVisitClick(this, EventArgs.Empty);
         gbTimeRep.Visible = false;
#endif
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LayoutControls();
      }

      protected override void BeforeRefresh(List<IDataSet> upd)
      {
         base.BeforeRefresh(upd);
         contractParams.Update(upd, dtpDateFinish.Value.AddMonths(-1), dtpDateFinish.Value);
      }

      protected override void DataLoaded(Manager m)
      {
         base.DataLoaded(m);
         contractParams.DataLoaded();

#if NbtlMonitor
         visitDetails.PerformClick();
#endif
      }

      void UpdateReportType(object[] items)
      {
         int selInd = cbReportType.SelectedIndex;
         cbReportType.Items.Clear();
         cbReportType.Items.AddRange(items);
         if (selInd < 0 || selInd >= cbReportType.Items.Count)
            selInd = 0;
         cbReportType.SelectedIndex = selInd;
         gbTimeRep.Enabled = true;
      }

      void DetailVisitClick(object sender, EventArgs e)
      {
         ResetPanel();
         UpdateParamPanel(false);

         rbAgent.Checked = true;
         gbDivision.Enabled = true;
         btnDoReport.Enabled = true;
         gbDate.Enabled = true;
         selectedReport = "visit_details";
         contractParams.Enabled = true;
         contractParams.ShowMatrix(false);
      }

      private void TimeReportClick(object sender, EventArgs e)
      {
         ResetPanel();
         UpdateReportType(new object[]
         {
            "1", "2", "3", "4"
         });
         rbAgent.Checked = true;
         gbDivision.Enabled = true;
         btnDoReport.Enabled = true;
         gbDate.Enabled = true;
         selectedReport = "scrtime";

         UpdateParamPanel(false);
         cbReportType.SelectedIndexChanged += OnTypeChanged;

         OnTypeChanged(cbReportType, EventArgs.Empty);
      }

      private void OnTypeChanged(object sender, EventArgs e)
      {
         contractParams.Enabled = false;
         int selIndex = cbReportType.SelectedIndex;
         if(selIndex == 0)
         {
            rbAgent.Enabled = true;
            rbAgent.Checked = true;
            rbDiv.Enabled = false;
         } else if(selIndex == 1)
         {
            rbDiv.Checked = true;
            rbDiv.Enabled = true;
            rbAgent.Enabled = false;
         } else if(selIndex == 2)
         {
            contractParams.Enabled = true;
            rbAgent.Enabled = true;
            rbAgent.Checked = true;
            rbDiv.Enabled = false;
         }
         else
         {
            rbAgent.Enabled = true;
            rbAgent.Checked = true;
            rbDiv.Enabled = true;
         }
      }

      void UpdateParamPanel(bool visible)
      {
         gbGSM.Visible = visible;
         gbLayout.Visible = visible;
//         gbField.Visible = visible;
         pnlQuest.Visible = visible;
         gbTime.Visible = visible;

         contractParams.Visible = !visible;
         if(!visible)
            contractParams.ShowMatrix(false);

         btnDoReport.Top = doReportLocation;
      }

      private void ShelfShareClick(object sender, EventArgs e)
      {
         ResetPanel();
         UpdateReportType(new object[]
         {
            "1", "2", "3", "4"
         });
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         contractParams.Enabled = true;

         UpdateParamPanel(false);

         selectedReport = "contract";
      }
      private void InventoryClick(object sender, EventArgs e)
      {
         ResetPanel();
         UpdateReportType(new object[]
         {
            "1", "2"
         });

         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = "remnants_report";
      }

      protected override string UpdateReportName()
      {
         if(selectedReport == "remnants_report" && cbReportType.SelectedIndex == 1)
         {
            return "remnants_report_f2";
         }
         if(selectedReport == "scrtime" && cbReportType.SelectedIndex > 0)
         {
            return selectedReport + (cbReportType.SelectedIndex + 1).ToString();
         }

         if (selectedReport.Equals(QUEST_REPORT))
         {
            if (rbPokaz.Checked)
               return "quest_stats_rep";
            if (rbHorizont2.Checked)
               return "quest_rep_2";
            if (rbVar3.Checked)
               return "quest_rep_3";
            if (rbVar4.Checked)
               return QUETS_REP_4;
         }

         return base.UpdateReportName();
      }

      private void DistribClick(object sender, EventArgs e)
      {
         ResetPanel();

         gbTimeRep.Enabled = true;
         UpdateReportType(new object[]
         {
            "1", "2", "3"
         });

         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         cbSlsnet.Enabled = true;
         selectedReport = "distrib_report";
      }

      public override bool CheckInputData(string report)
      {
         bool res = base.CheckInputData(report);

         if(res)
         {
            if (report.Equals(DISTRIB_REPORT))
            {
               Slsnet sls = cbSlsnet.SelectedItem as Slsnet;

               if (sls == null)
               {
                  MessageBox.Show(this, "Выберите сеть!", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                  res = false;
               }
            }
         }

         return res;
      }

      protected override Network.DataObject CreateParam(string selectedReport)
      {
         if (selectedReport == "contract" || selectedReport == "scrtime3" || selectedReport == "visit_details")
         {

            ContractRepParams res = new ContractRepParams();
            res.version = cbReportType.SelectedIndex + 1;
            res.photo = contractParams.cbPhoto.Checked ? 1 : 0;

            ContractDef cd = contractParams.lbContracts.SelectedItem as ContractDef;
            res.cid = cd.id;
            res.matrix = contractParams.lbMatrix.SelectedIndex <= 0 ? "" : contractParams.lbMatrix.SelectedItem as string;
            res.item = contractParams.lbItems.SelectedIndex <= 0 ? "" : contractParams.lbItems.SelectedItem as string;

            return SetParamObject(res);
         }
         else if (selectedReport == DISTRIB_REPORT)
         {
            RepParamsType res = new RepParamsType();
            res.type = cbReportType.SelectedIndex;
            res.slsnet = ((Slsnet)cbSlsnet.SelectedItem).id;
            return SetParamObject(res);
         }
         else if (selectedReport == QUETS_REP_4)
         {
            RepParamsType res = new RepParamsType();
            res.type = cbReportType.SelectedIndex;
            res.slsnet = ((Slsnet)cbSlsnet.SelectedItem).id;
            res.city = ((City)cbCity.SelectedItem).name;
            return SetParamObject(res);
         }
         return base.CreateParam(selectedReport);
      }

      private void PriceMonitoringClick(object sender, EventArgs e)
      {
         ResetPanel();
         gbDivision.Enabled = true;
         gbDate.Enabled = true;
         rbDiv.Enabled = true;
         rbAgent.Checked = true;
         btnDoReport.Enabled = true;
         selectedReport = "prc_mon_report";
      }

      public override void ResetPanel()
      {
         base.ResetPanel();
         gbDivision.Enabled = true;
         gbTimeRep.Enabled = false;
         cbSlsnet.Enabled = false;

         UpdateParamPanel(true);
         cbReportType.SelectedIndexChanged -= OnTypeChanged;

      }

      class RepParamsType : ReportParam
      {
         public int type = 0;
         public string slsnet = string.Empty;
         public string city = string.Empty;
      }

      class ContractRepParams : ReportParam
      {
         public string cid = string.Empty;
         public string matrix = "";
         public int photo = 0;
         public string item = "";
         public int version = -1;
      }

      protected override void btnQuest_Click(object sender, EventArgs e)
      {
         base.btnQuest_Click(sender, e);
         gbTimeRep.Enabled = true;
         cbSlsnet.Enabled =true;
      }
   }
}
