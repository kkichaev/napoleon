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
   public partial class FmDubDocs : Form
   {
      private SimpleDataSet<VandSales> dsOrder;
      private String userids = string.Empty;
      private static readonly string COMMA = ",";
      private static readonly string UPPER_COMMA = "'";

      public FmDubDocs()
      {
         InitializeComponent();
         period.Start = DateTime.Now.Date;
         period.Finish = DateTime.Now.Date;

         dsOrder = new SimpleDataSet<VandSales>(VandSales.OBJECT_NAME, false, true);
         if( MainForm.Instance.CheckIsMainDataPresents(true) )
            userids = CollectUserIds();
      }

      private void btnStart_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();

         const string COMMON_FILTER_STR = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy} 23:59:59') and \"userid\" in({2})";
         dsOrder.Filter = string.Format(COMMON_FILTER_STR, period.Start, period.Finish, userids);
         upd.Add(dsOrder);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void PutReplacedSets(List<ReplacedSet> rpl, SimpleDataSet<VandSales> data, string setName)
      {
         Dictionary<string, ReplacedSet> wr = new Dictionary<string, ReplacedSet>();
         foreach (VandSales o in data.Data)
         {
            ReplacedSet rs = null;
            if (wr.ContainsKey(o.userid))
               rs = wr[o.userid];
            else
            {
               rs = new ReplacedSet(o.userid, new SimpleDataSet<VandSales>(setName, false, true));
               rs.dontRemove = true;
               wr[o.userid] = rs;
            }
            ((SimpleDataSet<VandSales>)rs.data).Add(o);
         }
         foreach (ReplacedSet rs in wr.Values)
            rpl.Add(rs);
      }

      private void DoLoadData()
      {
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         PutReplacedSets(rpl, dsOrder, VandSales.OBJECT_NAME);

         if (rpl.Count > 0)
         {
            DBConnection conn = Config.GetConfig().GetConnection();
            bool ret = DataModule.UpdateDataSet(null, null, rpl, conn);

            if (ret)
            {
               MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK, MessageBoxIcon.Information);
               Close();
            }
            else
               MessageBox.Show("Ошибка при записи", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
         else
            MessageBox.Show("Нет документов для выгрузки", "Информация", MessageBoxButtons.OK, MessageBoxIcon.Information);
      }

      private string CollectUserIds()
      {
         StringBuilder result = new StringBuilder();

         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            Division d = mc.Division;

            if (d != null)
            {
               List<Division.DivisionAgent>.Enumerator iter = d.GetAllAgents().GetEnumerator();


               while (iter.MoveNext())
               {
                  if (result.Length > 0)
                     result.Append(COMMA);

                  result.Append(UPPER_COMMA).Append(iter.Current.id).Append(UPPER_COMMA);
               }
            }
         }

         return result.ToString();
      }

      private void FmDubDocs_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Escape)
            Close();
      }

   }
}
