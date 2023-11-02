using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    public partial class FmTimeSheet : Form
    {
        List<IntStringData> days;
        DateTime curMonth;
        int fixedCells = 1;

        List<Agent> agents;
        string agentFilter = "";
        SimpleDataSet<TimeSheet> dsSheduler = new SimpleDataSet<TimeSheet>(TimeSheet.OBJECT_NAME, false);

        public FmTimeSheet()
        {
            InitializeComponent();

            dgvItems.AutoGenerateColumns = false;

            days = new List<IntStringData>(DayValues());

            fixedCells = dgvItems.Columns.IndexOf(clmnDay1);
            clmnDay1.DataSource = days;
            clmnDay1.ValueMember = "ID";
            clmnDay1.DisplayMember = "Name";

            agents = new List<Agent>();
            foreach (Agent a in (CurrentUser.user as Manager).GetAgents().Data)
            {
                agents.Add(a);

                if(agentFilter.Length > 0) agentFilter += ",";
                agentFilter += "'" + a.id + "'";
            }

            agents.Sort();
            agentFilter = "userid in (" + agentFilter + ")";

            dtpDate.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
            OnMonthChanged(dtpDate.Value);
        }

        void RefreshData()
        {
            string filter = agentFilter + string.Format(" and start >= ToDate('01/{0:MM/yyyy}') and start < ToDate('02/{0:MM/yyyy}') ", curMonth);
            dsSheduler.Filter = filter;

            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsSheduler);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
        }

        void DoLoadData()
        {
            Dictionary<String, TimeSheet> agentData = new Dictionary<String, TimeSheet>();
            foreach (TimeSheet ts in dsSheduler.Data)
                if(ts.agent != null)
                    agentData.Add(ts.userid, ts);

            dgvItems.SuspendLayout();

            while (dgvItems.Columns.Count > 2)
                dgvItems.Columns.RemoveAt(dgvItems.Columns.Count - 1);

            int dm = DateTime.DaysInMonth(curMonth.Year, curMonth.Month);

            for (int i = fixedCells + 1; i <= dm; i++)
            {
                DataGridViewComboBoxColumn clmn = new DataGridViewComboBoxColumn();

                string day = i.ToString();

                clmn.DataPropertyName = "Day" + day;
                clmn.DisplayStyle = clmnDay1.DisplayStyle;
                clmn.FillWeight = clmnDay1.FillWeight;
                clmn.FlatStyle = clmnDay1.FlatStyle;
                clmn.HeaderText = day;
                clmn.Name = "clmnDay" + day;
                clmn.Width = clmnDay1.Width;

                clmn.DataSource = clmnDay1.DataSource;
                clmn.ValueMember = clmnDay1.ValueMember;
                clmn.DisplayMember = clmnDay1.DisplayMember;
                dgvItems.Columns.Add(clmn);
            }

            DataGridViewTextBoxColumn clmn1 = new DataGridViewTextBoxColumn();
            clmn1.DataPropertyName = "NotWorkCount";
            clmn1.FillWeight = clmnDay1.FillWeight;
            clmn1.HeaderText = "вых. и праздничные";
            clmn1.Name = "NotWorkCount";
            clmn1.Width = clmnDay1.Width;
            dgvItems.Columns.Add(clmn1);

            List<RowData> data = new List<RowData>();
            foreach (Agent a in agents)
            {
                bool isNewRow = false;
                TimeSheet ts;
                if (!agentData.TryGetValue(a.id, out ts))
                {
                    ts = new TimeSheet();
                    ts.agent = a;
                    ts.userid = a.id;
                    ts.start = new DateTime(curMonth.Year, curMonth.Month, 1);
                    isNewRow = true;
                }

                RowData rd = new RowData(ts);
                if (isNewRow)
                {
                    Type rowType = rd.GetType();
                    for (int i = 0; i < dm; i++)
                    {
                        PropertyInfo pi = rowType.GetProperty("Day" + (i + 1).ToString());
                        int value = (int)pi.GetValue(rd, null);
                        if (value == 0)
                        {
                            value = IsWeekend(i) ? -2 : 8;
                            pi.SetValue(rd, value, null);
                        }
                    }
                }
                data.Add(rd);
            }

            dgvItems.DataSource = data;
            dgvItems.ResumeLayout();
            tsbSave.Enabled = false;
        }

        void OnMonthChanged(DateTime newMonth)
        {
            curMonth = newMonth;
            RefreshData();
        }

        bool IsWeekend(int day)
        {
            DateTime dt = curMonth.AddDays(day);
            return dt.DayOfWeek == DayOfWeek.Saturday || dt.DayOfWeek == DayOfWeek.Sunday;
        }

        protected IntStringData[] DayValues()
        {
            return new IntStringData[] {
                new IntStringData(-5, "Б"),
                new IntStringData(-4, "о"),
                new IntStringData(-3, "а"),
                new IntStringData(-2, "в"),
                new IntStringData(-1, "К"),
                new IntStringData(0, ""),
                new IntStringData(1, "1"),
                new IntStringData(2, "2"),
                new IntStringData(3, "3"),
                new IntStringData(4, "4"),
                new IntStringData(5, "5"),
                new IntStringData(6, "6"),
                new IntStringData(7, "7"),
                new IntStringData(8, "8"),
                new IntStringData(9, "9"),
                new IntStringData(10, "10"),
                new IntStringData(11, "11"),
                new IntStringData(12, "12"),
                new IntStringData(13, "13"),
                new IntStringData(14, "14"),
                new IntStringData(15, "15"),
                new IntStringData(16, "16"),
                new IntStringData(17, "17"),
                new IntStringData(18, "18"),
                new IntStringData(19, "19"),
             };
        }

        private void dtpDate_ValueChanged(object sender, EventArgs e)
        {
            DateTime ct = dtpDate.Value;
            if(ct.Year != curMonth.Year || ct.Month != curMonth.Month)
            {
                OnMonthChanged(new DateTime(ct.Year, ct.Month, 1));
            }
        }

        private void dgvItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
        {
            if(e.ColumnIndex >= fixedCells)
            {
                e.CellStyle.BackColor = IsWeekend(e.ColumnIndex - fixedCells) ? Color.LightBlue : Color.White;
            }
        }

        public class RowData
        {
            TimeSheet src;

            public RowData(TimeSheet src)
            {
                this.src = src;
            }

            public TimeSheet Src { get { return src; } }

            public string Name { get { return src.agent != null ? src.agent.Name : src.userid;  } }

            public int Day1 { get { return src.day1; } set { src.day1 = value; } }
            public int Day2 { get { return src.day2; } set { src.day2 = value; } }
            public int Day3 { get { return src.day3; } set { src.day3 = value; } }
            public int Day4 { get { return src.day4; } set { src.day4 = value; } }
            public int Day5 { get { return src.day5; } set { src.day5 = value; } }
            public int Day6 { get { return src.day6; } set { src.day6 = value; } }
            public int Day7 { get { return src.day7; } set { src.day7 = value; } }
            public int Day8 { get { return src.day8; } set { src.day8 = value; } }
            public int Day9 { get { return src.day9; } set { src.day9 = value; } }

            public int Day10 { get { return src.day10; } set { src.day10 = value; } }
            public int Day11 { get { return src.day11; } set { src.day11 = value; } }
            public int Day12 { get { return src.day12; } set { src.day12 = value; } }
            public int Day13 { get { return src.day13; } set { src.day13 = value; } }
            public int Day14 { get { return src.day14; } set { src.day14 = value; } }
            public int Day15 { get { return src.day15; } set { src.day15 = value; } }
            public int Day16 { get { return src.day16; } set { src.day16 = value; } }
            public int Day17 { get { return src.day17; } set { src.day17 = value; } }
            public int Day18 { get { return src.day18; } set { src.day18 = value; } }
            public int Day19 { get { return src.day19; } set { src.day19 = value; } }

            public int Day20 { get { return src.day20; } set { src.day20 = value; } }
            public int Day21 { get { return src.day21; } set { src.day21 = value; } }
            public int Day22 { get { return src.day22; } set { src.day22 = value; } }
            public int Day23 { get { return src.day23; } set { src.day23 = value; } }
            public int Day24 { get { return src.day24; } set { src.day24 = value; } }
            public int Day25 { get { return src.day25; } set { src.day25 = value; } }
            public int Day26 { get { return src.day26; } set { src.day26 = value; } }
            public int Day27 { get { return src.day27; } set { src.day27 = value; } }
            public int Day28 { get { return src.day28; } set { src.day28 = value; } }
            public int Day29 { get { return src.day29; } set { src.day29 = value; } }

            public int Day30 { get { return src.day30; } set { src.day30 = value; } }
            public int Day31 { get { return src.day31; } set { src.day31 = value; } }

            public int NotWorkCount { get { return src.notWorkCount; } set { src.notWorkCount = value; } }
        }

        public class IntStringData
        {
            int id;
            string name;

            public IntStringData(int id, string name)
            {
                this.id = id;
                this.name = name;
            }

            public int ID { get { return id; } }
            public string Name { get { return name; } }
        }

        private void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
        {
            dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
            tsbSave.Enabled = true;
        }

        bool CheckChanges()
        {
            if (!tsbSave.Enabled)
                return true;

            DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (dr == DialogResult.No)
                return true;
            if (dr == DialogResult.Cancel)
                return false;

            return SaveChanges(false);
        }

        private bool SaveChanges(bool showDialog)
        {
            SimpleDataSet<TimeSheet> wrset = new SimpleDataSet<TimeSheet>(TimeSheet.OBJECT_NAME, false);
            foreach(RowData rd in (List<RowData>)dgvItems.DataSource)
            {
                wrset.Add(rd.Src);
            }

            bool ret = true;
            if (wrset.Count > 0)
            {
                List<IDataSet> wr = new List<IDataSet>();
                wr.Add(wrset);

                ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
                if (showDialog)
                    MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
            }
            return ret;
        }

        private void tsbSave_Click(object sender, EventArgs e)
        {
            tsbSave.Enabled = !SaveChanges(true);
        }


        private void btnTabel_Click(object sender, EventArgs e)
        {
           const string repName = "timesheet_report";
           ReportResult.DoReport(repName, repName, CreateParam(), this);
        }

        class ReportParam : GRSoft.Network.DataObject
        {
           public DateTime start = DateTime.MinValue;
        }
        
        private Network.DataObject CreateParam()
        {
           ReportParam p = new ReportParam();
           p.start = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, 1);

           return p;
        }
    }
}
