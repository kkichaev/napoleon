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
      private DataSet<int, Order> dsOrder;
      private String userids = string.Empty;
      private static readonly string COMMA = ",";
      private static readonly string UPPER_COMMA = "'";

      public FmDubDocs()
      {
         InitializeComponent();
         dsOrder = (DataSet<int, Order>)DataModule.Get(Order.OBJECT_NAME) ?? new DataSet<int, Order>(Order.OBJECT_NAME);
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

      private void DoLoadData()
      {
         Dictionary<string, DataSet<int, Order>> userOrder = new Dictionary<string, DataSet<int, Order>>();

         foreach (Order o in dsOrder.Data)
         {
            if (!userOrder.ContainsKey(o.userid))
               userOrder[o.userid] = new DataSet<int, Order>(Order.OBJECT_NAME, false);

            userOrder[o.userid].Add(userOrder[o.userid].Count, o);
         }

         bool result = true;
         DBConnection conn = Config.GetConfig().GetConnection();
         foreach(String userid in userOrder.Keys)
         {
            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(userOrder[userid]);
            bool ret = DataModule.UpdateDataSet(wrSet, null, null, conn, userid);

            if (result)
               result = ret;
         }

         if (result)
            MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK,
               MessageBoxIcon.Information);
         else
            MessageBox.Show("Операция выполнена с ошиками", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);

         Close();
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

   }
}
