using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      readonly static String DISCOUNT_PROPERTY_NAME = "Discount";
      public DivisionFormEx()
      {
         DataGridViewCheckBoxColumn discount = new DataGridViewCheckBoxColumn();
         discount.HeaderText = "Скидка";
         discount.DataPropertyName = DISCOUNT_PROPERTY_NAME;
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

      class DiscountVal
      {
         public bool val = false;
      }

      class DataItemEx : DataItem
      {
         DiscountVal discount;

         public bool Discount
         {
            get 
            {
               if (discount == null)
               {
                  discount = new DiscountVal();
                  discount.val = GetDiscount();
               }

               return discount.val;
            } 

            set 
            {
               if (discount == null)
               {
                  discount = new DiscountVal();
                  discount.val = GetDiscount();
               }

               discount.val = value;
               ((DivisionFormEx)owner).MarkChanged();
            } 
         }

         public DataItemEx(Agent a, DivisionForm o)
            : base(a, o)
         {
         }

         private bool GetDiscount()
         {
            bool result = false;
            try
            {
               foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
                  if (serverConfig.userid.Equals(agent.id) &&
                        serverConfig.key.Equals(DISCOUNT_PROPERTY_NAME))
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
            cfg.key = DISCOUNT_PROPERTY_NAME;
            cfg.userid = item.agent.id;
            cfg.value = item.Discount.ToString();
            addCfg[ctr++] = cfg;
         }

         wrObj.Add(addCfg);

         return true;
      }

      protected override bool IsAllowCommit(DataGridViewCell cell)
      {
         bool result = base.IsAllowCommit(cell);

         if (!result)
            result = cell != null && childUserList.Columns[cell.ColumnIndex].DataPropertyName == DISCOUNT_PROPERTY_NAME;

         return result;
      }
   }
}
