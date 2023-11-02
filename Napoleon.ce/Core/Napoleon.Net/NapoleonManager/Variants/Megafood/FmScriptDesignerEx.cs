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
      DataSet<string, OrgType> types;

      public FmScriptDesignerEx()
      {
         types = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ??
            new DataSet<string, OrgType>(OrgType.OBJECT_NAME, true);

         clmnType = new DataGridViewTextBoxColumn();
         clmnType.DataPropertyName = "Type";
         clmnType.FillWeight = 30F;
         clmnType.HeaderText = "Тип точки";
         clmnType.Name = "clmnType";


         dgvSrcipts.Columns.Insert(1, clmnType);
      }

      protected override void AddData(List<Network.IDataSet> upd)
      {
         upd.Insert(0, types);
      }
   }
}
