using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmChatGroupEdit : Form
   {
      public FmChatGroupEdit()
      {
         InitializeComponent();

         Manager m = CurrentUser.user as Manager;
         List<Agent> list = new List<Agent>();

         if (m != null)
            list.AddRange(m.GetAgents().Values);

         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });

         cbAgents.Items.AddRange(list.ToArray());
      }

      public string Title
      {
         get { return tbName.Text.Trim(); }
         set { tbName.Text = value; }
      }

      public List<Agent> Items
      {
         get { return CollectCheckedItems(); }
         set { SetCheckedItems(value); }
      }

      private void SetCheckedItems(List<Agent> list)
      {
         for (int i = 0; i < cbAgents.Items.Count; i++)
         {
            Agent a = cbAgents.Items[i] as Agent;

            if (a != null && list.Contains(a))
               cbAgents.SetItemChecked(i, true);
         }
      }

      private List<Agent> CollectCheckedItems()
      {
         List<Agent> result = new List<Agent>();

         foreach(object o in cbAgents.CheckedItems)
         {
            Agent a = o as Agent;

            if (a != null)
               result.Add(a);
         }

         return result;
      }

      private void FmChatGroupEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if(DialogResult == DialogResult.OK && tbName.Text.Trim().Length == 0)
         {
            MessageBox.Show("Имя группы не может быть пустым!");
            tbName.SelectAll();
            e.Cancel = true;
         }
      }
   }
}
