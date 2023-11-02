using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class FmQuestItemEditEx : FmQuestItemEdit
   {
      TextBox tbClients;
      TextBox tbAltText;
      List<Org> orgsSelected = new List<Org>();
      List<Org> sel = new List<Org>();

      public FmQuestItemEditEx() 
      {
         Size = new System.Drawing.Size(600, 430);

         Label label = new Label();
         label.Text = "Магазины";
         label.Location = new System.Drawing.Point(7, 250);
         label.AutoSize = true;

         tbClients = new TextBox();
         tbClients.Location = new System.Drawing.Point(7, 270);
         tbClients.Size = new System.Drawing.Size(200, 20);

         Button button = new Button();
         button.Location = new System.Drawing.Point(220, 270);
         button.Size = new System.Drawing.Size(41, 23);
         button.Text = "...";
         button.Click += ShowClients;

         splitContainer1.Panel1.Controls.Add(label);
         splitContainer1.Panel1.Controls.Add(tbClients);
         splitContainer1.Panel1.Controls.Add(button);

         label = new Label();
         label.Text = "Альтернативный текст";
         label.Location = new System.Drawing.Point(7, 300);
         label.AutoSize = true;

         tbAltText = new TextBox();
         tbAltText.Location = new System.Drawing.Point(7, 320);
         tbAltText.Size = new System.Drawing.Size(265, 20);

         splitContainer1.Panel1.Controls.Add(label);
         splitContainer1.Panel1.Controls.Add(tbAltText);
      }

      private void ShowClients(object sender, EventArgs e)
      {
         sel = FmSelectOrgs.DoSelect(orgsSelected);
         if (sel != null || (orgsSelected != null && orgsSelected.Count > 0))
         {
            if (sel != null)
               orgsSelected = sel;
            string text = "";
            foreach (Org o in orgsSelected)
            {
               if (text.Length > 0) text += ",";
               text += o.Name;
               if (text.Length > 150)
               {
                  text += "...";
                  break;
               }
            }

            tbClients.Text = text;
         }
      }

      public override QuestItemType Quest
      {
         get
         {
            QuestItemType result = base.Quest;
            result.altText = tbAltText.Text.Trim();
            result.clients = GetClients();
            return result;
         }
      }

      private string GetClients()
      {
         StringBuilder sb = new StringBuilder();
         foreach (Org o in sel)
         {
            if (sb.Length > 0) sb.Append(",");
            sb.Append(o.id);
         }

         return sb.ToString();
      }

      internal override DialogResult ShowDialog(QuestionItem questionItem)
      {
         tbAltText.Text = questionItem.altText;
         tbClients.Text += GetClients(questionItem.clients);

         return base.ShowDialog(questionItem);
      }

      private string GetClients(string orgs)
      {
         StringBuilder sb = new StringBuilder();   

         string[] ids = orgs.Split(',');

         foreach(string id in ids) 
         {
            if (FmQuestEditEx.dsOrg.ContainsKey(id))
            {
               if (sb.Length > 0) sb.Append(", ");
               Org org = FmQuestEditEx.dsOrg.ContainsKey(id) ? FmQuestEditEx.dsOrg[id] : null;

               if (org != null) {
                  sb.Append(org.name);
                  orgsSelected.Add(org);
               }
            }
         }

         return sb.ToString();
      }
   }

   public partial class QuestItemType
   {
      public string altText = string.Empty;
      public string clients = string.Empty;
   }
}
