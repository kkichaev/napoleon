using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmScriptDesignerEx : FmScriptDesigner
   {
      DataGridViewTextBoxColumn clmnType;
      DataSet<string, SalesChannel> salesChannels;

      public FmScriptDesignerEx()
      {
         clmnType = new DataGridViewTextBoxColumn();
         clmnType.DataPropertyName = "Kind";
         clmnType.FillWeight = 30F;
         clmnType.HeaderText = "Тип визита";
         clmnType.Name = "clmnType";

         salesChannels = (DataSet<string, SalesChannel>)DataModule.Get(SalesChannel.OBJECT_NAME) ?? new DataSet<string, SalesChannel>(SalesChannel.OBJECT_NAME);

         dgvSrcipts.Columns.Insert(1, clmnType);
      }

      protected override void AddData(List<IDataSet> upd)
      {
         upd.Add(salesChannels);
      }
   }
}
