using GRSoft.Network;
using System.Collections.Generic;
using System.Windows.Forms;
using GRSoft.UILib;
namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      CheckedListBox lbAgentPrice = new CheckedListBox();
      bool loading = true;

      public UserFormEx(Divisions owner) :
         base(owner)
      {
         TabPage tp = new TabPage();
         tp.Text = "“ипы цен";
         tp.Name = "tpAgentPrice";
         tp.Location = new System.Drawing.Point(4, 23);
         tp.Padding = new System.Windows.Forms.Padding(3);
         tp.Size = new System.Drawing.Size(466, 279);
         tp.UseVisualStyleBackColor = true;

         lbAgentPrice.Location = new System.Drawing.Point(0, 0);
         lbAgentPrice.Size = new System.Drawing.Size(466, 279);
         lbAgentPrice.Dock = DockStyle.Fill;
         lbAgentPrice.ItemCheck += lbAgentPrice_ItemCheck;
         lbAgentPrice.CheckOnClick = true;

         tp.Controls.Add(lbAgentPrice);

         userDetails.TabPages.Add(tp);
      }

      void lbAgentPrice_ItemCheck(object sender, ItemCheckEventArgs e)
      {
         if (loading)
            return;

         // строка доолжна содержать последний разделитель на  ѕ  провер€ю название цен на вхожение ÷ена;
         string value = "";
         int index = 0;
         foreach (string str in lbAgentPrice.Items)
         {
            if ((index == e.Index && e.NewValue == CheckState.Checked) || (index != e.Index && lbAgentPrice.GetItemChecked(index)))
               value += str + ";";

            index++;
         }

         string id = Agent.id;
         CommonConfig finded = null;
         foreach (CommonConfig cc in owner.dsCommonConfig.Data)
         {
            if (cc.userid == id && cc.key == "–азрешенные÷ены")
            {
               finded = cc;
               break;
            }
         }
         if( finded == null)
         {
            finded = new CommonConfig();
            finded.key = "–азрешенные÷ены";
            finded.userid = id;
            owner.dsCommonConfig.Add(owner.dsCommonConfig.Count, finded);
         }
         finded.value = value;

         owner.AddWriteSet(owner.dsCommonConfig);
      }

      protected override void AfterControlFilled()
      {
         loading = true;
         string id = Agent.id;

         lbAgentPrice.Items.Clear();
         foreach(OrderAddConfig cfg in owner.dsConfig.Data)
         {
            if(cfg.key == "¬ид÷ены")
            {
               string[] data = cfg.value.Split(new char[] {';'});
               foreach(string prc in data)
                  lbAgentPrice.Items.Add(prc);
            }
         }
         foreach(CommonConfig cc in owner.dsCommonConfig.Data)
         {
            if(cc.userid == id && cc.key=="–азрешенные÷ены")
            {
               string[] data = cc.value.Split(new char[] { ';' });
               foreach(string str in data)
               {
                  int index = lbAgentPrice.Items.IndexOf(str);
                  if (index >= 0)
                     lbAgentPrice.SetItemChecked(index, true);
               }
            }
         }
         loading = false;
      }
   }
}