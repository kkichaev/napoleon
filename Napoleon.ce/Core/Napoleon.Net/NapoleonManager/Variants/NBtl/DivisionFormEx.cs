using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      readonly static String EXIT_PROPERTY_NAME = "ExitControl";
      public DivisionFormEx()
      {
         DataGridViewCheckBoxColumn discount = new DataGridViewCheckBoxColumn();
         discount.HeaderText = "Выход из спценария";
         discount.DataPropertyName = EXIT_PROPERTY_NAME;
         childUserList.Columns.Add(discount);
      }

      protected override DivisionForm.DataItem CreateItem(Agent a, DivisionForm form)
      {
         return new DataItemEx(a, form);
      }

      private void MarkChanged()
      {
         parent.MarkChanged();
      }

      class ExitControlVal
      {
         public bool val = false;
      }

      class DataItemEx : DataItem
      {
         ExitControlVal exitControl;

         public bool ExitControl
         {
            get 
            {
               if (exitControl == null)
               {
                  exitControl = new ExitControlVal();
                  exitControl.val = GetExitVal();
               }

               return exitControl.val;
            } 

            set 
            {
               if (exitControl == null)
               {
                  exitControl = new ExitControlVal();
                  exitControl.val = GetExitVal();
               }

               exitControl.val = value;
               ((DivisionFormEx)owner).MarkChanged();
            } 
         }

         public DataItemEx(Agent a, DivisionForm o)
            : base(a, o)
         {
         }

         private bool GetExitVal()
         {
            bool result = false;
            try
            {
               foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
                  if (serverConfig.userid.Equals(agent.id) &&
                        serverConfig.key.Equals(EXIT_PROPERTY_NAME))
                  {
                     result = Boolean.Parse(serverConfig.value);
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

         foreach (DataItemEx item in (List<object>)((RefreshableSource) childUserList.DataSource).DataSource)
         {
            CommonConfig cfg = new CommonConfig();
            cfg.key = EXIT_PROPERTY_NAME;
            cfg.userid = item.agent.id;
            cfg.value = item.ExitControl.ToString();
            addCfg[ctr++] = cfg;
         }

         if (addCfg.Count > 0)
            wrObj.Add(addCfg);

         return true;
      }

      protected override bool IsAllowCommit(DataGridViewCell cell)
      {
         bool result = base.IsAllowCommit(cell);

         if (!result)
            result = cell != null && childUserList.Columns[cell.ColumnIndex].DataPropertyName == EXIT_PROPERTY_NAME;

         return result;
      }
   }
}
