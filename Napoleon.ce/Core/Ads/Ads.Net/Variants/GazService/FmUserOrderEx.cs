using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   class FmUserOrderEx : FmUserOrder
   {
      private DsWorkType dsWorkType;

      public FmUserOrderEx()
      {
         dsWorkType = (DsWorkType)DataModule.Get(WorkType.OBJECT_NAME) ?? new DsWorkType(true);

         DataGridViewTextBoxColumn dgvOrderCert = new DataGridViewTextBoxColumn();
         dgvOrderCert.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         dgvOrderCert.DataPropertyName = "Certificate";
         dgvOrderCert.FillWeight = 100F;
         dgvOrderCert.HeaderText = "Свид-во";
         dgvOrderCert.Name = "dgvOrderCertName";

         DataGridViewTextBoxColumn dgvOrderCounter = new DataGridViewTextBoxColumn();
         dgvOrderCounter.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         dgvOrderCounter.DataPropertyName = "Counter";
         dgvOrderCounter.FillWeight = 100F;
         dgvOrderCounter.HeaderText = "Счетчик";
         dgvOrderCounter.Name = "dgvOrderCounter";

         DataGridViewTextBoxColumn dgvOrderNumCounter = new DataGridViewTextBoxColumn();
         dgvOrderNumCounter.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         dgvOrderNumCounter.DataPropertyName = "NumCtr";
         dgvOrderNumCounter.FillWeight = 100F;
         dgvOrderNumCounter.HeaderText = "Номер сч.";
         dgvOrderNumCounter.Name = "dgvOrderNumCounter";


         DataGridViewTextBoxColumn dgvOrderDataCounter = new DataGridViewTextBoxColumn();
         dgvOrderDataCounter.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         dgvOrderDataCounter.DataPropertyName = "Datactr";
         dgvOrderDataCounter.FillWeight = 100F;
         dgvOrderDataCounter.HeaderText = "Показания";
         dgvOrderDataCounter.Name = "dgvOrderDataCounter";

         DataGridViewTextBoxColumn dgvOrderProtocol = new DataGridViewTextBoxColumn();
         dgvOrderProtocol.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         dgvOrderProtocol.DataPropertyName = "Protocol";
         dgvOrderProtocol.FillWeight = 100F;
         dgvOrderProtocol.HeaderText = "Протокол";
         dgvOrderProtocol.Name = "dgvOrderProtocol";

         DataGridViewTextBoxColumn dgvOrderWorkType = new DataGridViewTextBoxColumn();
         dgvOrderWorkType.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         dgvOrderWorkType.DataPropertyName = "WorkType";
         dgvOrderWorkType.FillWeight = 100F;
         dgvOrderWorkType.HeaderText = "Тип";
         dgvOrderWorkType.Name = "dgvOrderWorkType";

         DataGridViewTextBoxColumn dgvOrderContact = new DataGridViewTextBoxColumn();
         dgvOrderContact.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         dgvOrderContact.DataPropertyName = "Contact";
         dgvOrderContact.FillWeight = 100F;
         dgvOrderContact.HeaderText = "Клиент";
         dgvOrderContact.Name = "dgvOrderContact";

         DataGridViewTextBoxColumn dgvOrderPhone = new DataGridViewTextBoxColumn();
         dgvOrderPhone.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         dgvOrderPhone.DataPropertyName = "Phone";
         dgvOrderPhone.FillWeight = 100F;
         dgvOrderPhone.HeaderText = "Телефон";
         dgvOrderPhone.Name = "dgvOrderPhone";

         dgvOrder.Columns.AddRange(new DataGridViewColumn[] {
            dgvOrderCert,
            dgvOrderCounter,
            dgvOrderNumCounter, 
            dgvOrderDataCounter,
            dgvOrderProtocol, 
            dgvOrderWorkType, 
            dgvOrderContact, 
            dgvOrderPhone});
      }

      protected override GRSoft.Network.IDataSet createDsUserOrder()
      {
         return new DsUserOrderEx(true);
      }

      protected override System.Collections.IList createDataSource()
      {
         return new List<UserOrderEx>();
      }

      protected override void fillUpdateList(List<GRSoft.Network.IDataSet> list)
      {
         base.fillUpdateList(list);

         if (list != null)
            list.Insert(0, dsWorkType);
      }
   }
}
