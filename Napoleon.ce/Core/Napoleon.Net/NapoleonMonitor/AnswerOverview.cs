using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class AnswerOverview : UserControl, DataObjectViewer
   {
      protected DataSet<string, Price> dsPrice;// = new DataSet<string, Price>("Price");
      protected DataSet<string, Org> dsOrg;// = new DataSet<string, Org>("Org");
      private DataSet<string, PotenzialOrg> dsPotenzailOrg;

      public AnswerOverview()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;

         dsPrice = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price>;
         dsOrg = DataModule.Get(Org.OBJECT_NAME) as DataSet<string, Org>;
         dsPotenzailOrg = DataModule.Get(PotenzialOrg.OBJECT_NAME) as DataSet<string, PotenzialOrg>;
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         Answer a = dataObject as Answer;
         if (a != null)
         {
            dgvItems.DataSource = a.items;
            dgvItems.CellFormatting += new DataGridViewCellFormattingEventHandler(dgvItems_CellFormatting);
         }
      }

      void dgvItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if (e.ColumnIndex == 1)
         {
            DataGridViewRow row = dgvItems.Rows[e.RowIndex];
            if (row != null)
            {
               AnswerItem answerItem = row.DataBoundItem as AnswerItem;

               if (answerItem != null &&
                  answerItem.type == QuestionItem.DATASET)
               {
                  if (answerItem.remark.Equals("Прайс"))
                  {
                     if (dsPrice != null && dsPrice.ContainsKey(answerItem.answer))
                        e.Value = dsPrice[answerItem.answer].name;
                     else
                        e.Value = String.Format("Код объекта <{0}> не найден", answerItem.answer);
                  }
                  else if (answerItem.remark.Equals("Организация"))
                  {
                     if (dsOrg != null && dsOrg.ContainsKey(answerItem.answer))
                        e.Value = dsOrg[answerItem.answer].name;
                     else if (dsPotenzailOrg != null && dsPotenzailOrg.ContainsKey(answerItem.answer))
                        e.Value = dsPotenzailOrg[answerItem.answer].name;
                     else
                        e.Value = String.Format("Код объекта <{0}> не найден", answerItem.answer);
                  }
               }
            }
         }
      }
   }
}
