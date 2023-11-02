using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      private static string DISCOUNT_KEY = "discount";
      private TextBox tbDiscount;
      public DivisionFormEx()
         : base()
      {
         Label label = new Label();
         label.Text = "Порог скидки(%)";
         label.Location = new System.Drawing.Point(30, 130);

         tbDiscount = new TextBox();
         tbDiscount.Location = new System.Drawing.Point(130, 127);
         tbDiscount.KeyDown += new KeyEventHandler(tbDiscount_KeyDown);

         Controls.Add(label);
         Controls.Add(tbDiscount);

         tabControl1.Top = 160;

         dsCommonConfig = DataModule.Get(CommonConfig.OBJECT_NAME) as DataSet<int, CommonConfig>;
      }

      void tbDiscount_KeyDown(object sender, KeyEventArgs e)
      {
         parent.MarkChanged();
      }

      internal override void DataLoaded()
      {
         foreach(CommonConfig cc in dsCommonConfig.Values)
            if (cc.key.Equals(DISCOUNT_KEY) && cc.userid == string.Empty)
            {
               tbDiscount.Text = cc.value;
            }
      }

      internal override void BeforeUpdate(List<GRSoft.Network.IDataSet> updSet)
      {
         dsCommonConfig.Filter = "";
      }

      internal override bool BeforeWriteChanges(List<IDataSet> wrObj, List<IDataSet> rmvObj, List<ReplacedSet> replaced, DBConnection conn)
      {
         bool b = false;

         foreach (CommonConfig cc in dsCommonConfig.Values)
            if (cc.key.Equals(DISCOUNT_KEY) && cc.userid == string.Empty)
            {
               cc.value = getDiscount();
               b = true;
            }

         if (!b)
         {
            CommonConfig cfg = new CommonConfig();
            cfg.key = DISCOUNT_KEY;
            cfg.value = getDiscount();

            dsCommonConfig.Add(dsCommonConfig.Count, cfg);
         }

         wrObj.Add(dsCommonConfig);

         return true;
      }

      private string getDiscount()
      {
         string result = "30";
         int discount = 30;

         if (tbDiscount.Text.Trim().Length > 0 && 
               Int32.TryParse(tbDiscount.Text.Trim(), out discount))
            result = discount.ToString();

         return result;
      }
   }
}
