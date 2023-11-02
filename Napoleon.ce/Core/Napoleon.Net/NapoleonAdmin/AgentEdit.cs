using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonAdmin
{
   public partial class AgentEdit : Form
   {
      public class AgentEditInfo
      {
         public string name = string.Empty;
         public string id = string.Empty;

         public AgentEditInfo() { }

         public AgentEditInfo(UserDataItem udi)
         {
            this.name = udi.Name;
            this.id = udi.Id;
         }

         public Agent CreateAgent()
         {
            Agent result = new Agent();
            
            result.id = id;
            result.name = name;

            return result;
         }
      }

      public AgentEdit()
      {
         InitializeComponent();
      }

      public static AgentEditInfo EditAgent(IWin32Window owner, AgentEditInfo info)
      {
         AgentEdit instance = new AgentEdit();

         if (info != null)
         {
            instance.tbName.Text = info.name;
            instance.tbId.Text = info.id;
#if EDIT_USER_NAME
            instance.tbId.Enabled = false;
#endif
         }
         
         AgentEditInfo result = null;

         if (instance.ShowDialog(owner) == DialogResult.OK)
         {
            result = info == null ? new AgentEditInfo() : info;
            result.id = instance.tbId.Text.Trim();
            result.name = instance.tbName.Text.Trim();
         }

         return result;
      }

      private void AgentEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if(DialogResult == DialogResult.OK)
            foreach (Control c in Controls)
            {
               TextBox tb = c as TextBox;
               if (tb != null && tb.Text.Trim().Length == 0)
               {
                  tb.Focus();
                  e.Cancel = true;
                  MessageBox.Show("Поле не может быть пустым",
                     "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                  break;
               }
            }
      }
   }
}
