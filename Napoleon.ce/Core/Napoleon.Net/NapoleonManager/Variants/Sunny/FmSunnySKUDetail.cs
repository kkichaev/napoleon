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
   public partial class FmSunnySKUDetail : Form
   {
      public FmSunnySKUDetail()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      void SetData(FmSunnySKUReport.ReportData data, List<DateTime> weeks)
      {
         Text = "Ассортимент " + data.Name;

         DateTime ed = DateTime.Now;
         DataGridViewColumn[] clmns = new DataGridViewColumn[] {clmnW5, clmnW4, clmnW3, clmnW2, clmnW1};
         for(int i=0; i<4; i++)
         {
            DateTime dt = weeks[i];
            string text = string.Format("{0:dd/MM/yyyy} - {1:dd/MM/yyyy}", dt, ed);
            clmns[i].HeaderText = text;

            ed = dt.AddDays(-1);
         }

         clmns[4].HeaderText = string.Format("{0:dd/MM/yyyy} - {1:dd/MM/yyyy}", ed.AddDays(-7), ed);


         Dictionary<Price, RowData> dic = new Dictionary<Price, RowData>();
         for(int i=0; i<5; i++)
         {
            foreach(Price p in data.sold[i].Keys)
            {
               if (dic.ContainsKey(p))
                  dic[p].MarkSold(i);
               else
                  dic.Add(p, new RowData(p, i));
            }
         }

         dgvItems.DataSource = new List<RowData>(dic.Values);
      }

      public static void Open(FmSunnySKUReport.ReportData data, List<DateTime> weeks)
      {
         FmSunnySKUDetail form = new FmSunnySKUDetail();
         form.SetData(data, weeks);
         form.Show();
      }

      class RowData : IComparable<RowData>
      {
         bool[] sold = new bool[5];
         Price p;

         public RowData(Price p, int index)
         {
            this.p = p;
            sold[index] = true;
         }

         public void MarkSold(int index) { sold[index] = true;}

         public string W1 { get { return sold[4] ? p.Name : ""; } }
         public string W2 { get { return sold[3] ? p.Name : ""; } }
         public string W3 { get { return sold[2] ? p.Name : ""; } }
         public string W4 { get { return sold[1] ? p.Name : ""; } }
         public string W5 { get { return sold[0] ? p.Name : ""; } }

         public bool IsGrow(int index)
         {
            if (index < 0 || index >= 4 || !sold[index++])
               return false;

            for (; index <= 4; index++)
               if (sold[index])
                  return false;
            return true;
         }

         public int CompareTo(RowData other)
         {
            return p.Name.CompareTo(other.p.Name);
         }
      }

      private void dgvItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if( e.ColumnIndex > 0 )
         {
            RowData rd = dgvItems.Rows[e.RowIndex].DataBoundItem as RowData;
            e.CellStyle.BackColor = rd.IsGrow(4 - e.ColumnIndex) ? Color.Yellow : dgvItems.DefaultCellStyle.BackColor;
         }
      }
   }

}
