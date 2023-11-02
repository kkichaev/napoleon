using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      readonly static String DELAY_PROPERTY_NAME = "Delay";
      private TextBox tbDelay;

      public DivisionFormEx()
         : base()
      {
         Label label = new Label();
         label.Text = "Допустимое число дней просрочки";
         label.Location = new System.Drawing.Point(30, 130);
         label.Size = new Size(200, 13);

         tbDelay = new TextBox();
         tbDelay.Location = new System.Drawing.Point(250, 127);
         tbDelay.KeyDown += new KeyEventHandler(tbDiscount_KeyDown);

         Controls.Add(label);
         Controls.Add(tbDelay);

         tabControl1.Top = 160;

         DataGridViewTextBoxColumn discount = new DataGridViewTextBoxColumn();
         discount.HeaderText = "Допустимое число дней просрочки";
         discount.DataPropertyName = DELAY_PROPERTY_NAME;
         childUserList.Columns.Add(discount);

      }

      protected override void PostDivisionChanged()
      {
         tbDelay.Text = Division.delay;
      }

      protected override DivisionForm.DataItem CreateItem(Agent a, DivisionForm form)
      {
         return new DataItemEx(a, form);
      }

      private void MarkChanged()
      {
         parent.MarkChanged();
      }


      void tbDiscount_KeyDown(object sender, KeyEventArgs e)
      {
         parent.MarkChanged();
      }

      protected override bool CheckChildChanges()
      {
         bool res = false;

         string delay = tbDelay.Text.ToString().Trim();
         int delayVal;

         if (!delay.Equals(Division.delay) && (delay.Length == 0 || delay.Length > 0 && Int32.TryParse (delay, out delayVal))) 
         {
            Division.delay = delay;
            res = true;
         }

         return res;
      }

      class DataItemEx : DataItem
      {
         public string Delay
         {
            get 
            { 
               return GetDelay();
            }

            set
            {
               SetDelay(value);
               ((DivisionFormEx)owner).MarkChanged();
            }
         }

         public DataItemEx(Agent a, DivisionForm o)
            : base(a, o)
         {
         }

         private void SetDelay(String value)
         {
            foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
               if (serverConfig.userid.Equals(agent.id) &&
                     serverConfig.key.Equals(DELAY_PROPERTY_NAME))
               {
                  serverConfig.value = value;
                  break;
               }
         }

         private string GetDelay()
         {
            string result = string.Empty;
            try
            {
               foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
                  if (serverConfig.userid.Equals(agent.id) &&
                        serverConfig.key.Equals(DELAY_PROPERTY_NAME))
                  {
                     result = serverConfig.value;
                     break;
                  }
            }
            catch (Exception) { }

            return result;
         }
      }

      internal override bool BeforeWriteChanges(List<GRSoft.Network.IDataSet> wrObj, List<GRSoft.Network.IDataSet> rmvObj, List<GRSoft.Network.ReplacedSet> replaced, GRSoft.Network.DBConnection conn)
      {
         int ctr = 0;
         DataSet<int, CommonConfig> addCfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);

         foreach (DataItemEx item in (List<object>)((RefreshableSource)childUserList.DataSource).DataSource)
         {
            CommonConfig cfg = new CommonConfig();
            cfg.key = DELAY_PROPERTY_NAME;
            cfg.userid = item.agent.id;
            cfg.value = item.Delay == null ? string.Empty : item.Delay.ToString();
            addCfg[ctr++] = cfg;
         }

         wrObj.Add(addCfg);

         return true;
      }
   }
}
