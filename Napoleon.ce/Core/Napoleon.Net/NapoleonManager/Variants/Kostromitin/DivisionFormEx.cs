using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      readonly static String DISCOUNT_PROPERTY_NAME = "МаксимальнаяСкидка";
      public DivisionFormEx()
      {
         DataGridViewTextBoxColumn discount = new DataGridViewTextBoxColumn();
         discount.HeaderText = "Макс.скидка";
         discount.DataPropertyName = "Discount";
         discount.DefaultCellStyle.Format = "N2";
         discount.DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleRight;
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
         public double val = 5.0;
      }

      class DataItemEx : DataItem
      {
         DiscountVal discount;

         public double Discount
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

         private double GetDiscount()
         {
            double ret = 5.0;
            try
            {
               foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
                  if (serverConfig.userid.Equals(agent.id) &&
                        serverConfig.key.Equals(DISCOUNT_PROPERTY_NAME))
                  {
                     ret = Double.Parse(serverConfig.value);
                     break;
                  }
            }
            catch (Exception) { }

            return ret;
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
