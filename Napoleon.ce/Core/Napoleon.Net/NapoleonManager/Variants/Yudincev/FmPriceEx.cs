using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmPriceEx : FmPrice
   {
      protected override void save()
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();
         List<ReplacedSet> rpcSet = new List<ReplacedSet>();

         SimpleDataSet<PricePhoto> wr = new SimpleDataSet<PricePhoto>(PricePhoto.OBJECT_NAME, false);
         foreach (PricePhoto pp in dsPhotos.Data)
            if (pp.photo != null)
               wr.Add(pp);
         if (wr.Count > 0)
            wrSet.Add(wr);

         if (dsDelPhotos.Count > 0)
            rmvSet.Add(dsDelPhotos);

         BeforeWrite(wrSet, rmvSet, rpcSet);

         bool result = false;

         result = DataModule.UpdateDataSet
            (wrSet, rmvSet, rpcSet, Config.GetConfig().GetConnection());

         if (!result)
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         else
            btnSave.Enabled = false;

         AfterWrite(result, wrSet, rmvSet, rpcSet);
      }

   }
}
