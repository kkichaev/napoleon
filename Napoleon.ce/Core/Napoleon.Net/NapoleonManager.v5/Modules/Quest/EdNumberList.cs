using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class EdNumberList : UserControl, IQuestItem
   {
      public EdNumberList()
      {
         InitializeComponent();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (tbText.Text.Trim().Length > 0)
         {
            lbValue.Items.Add(tbText.Text);
            tbText.Text = string.Empty;
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         if (tbText.Text.Trim().Length > 0 && lbValue.SelectedItem != null)
            lbValue.Items[lbValue.SelectedIndex]  = tbText.Text;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (lbValue.SelectedItem != null)
            lbValue.Items.Remove(lbValue.SelectedItem);
      }

      private void lbValue_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (lbValue.SelectedItem != null)
            tbText.Text = lbValue.SelectedItem as string;
      }

      public List<QuestionItemValue> GetValues()
      {
         List<QuestionItemValue> result = new List<QuestionItemValue>();

         foreach (string item in lbValue.Items)
         {
            QuestionItemValue v = new QuestionItemValue();
            v.value = item;
            result.Add(v);
         }

         return result;
      }

      public void SetValues(List<QuestionItemValue> list)
      {
         lbValue.Items.Clear();

         if (list.Count > 0)
            foreach (QuestionItemValue qiv in list)
               lbValue.Items.Add(qiv.value);
      }

      private void tbText_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            btnAdd_Click(null, null);
      }
   }
}
