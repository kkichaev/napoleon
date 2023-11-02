using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Collections;

namespace GRSoft.Ads
{
   class FmOrderEx : FmOrder
   {
      public FmOrderEx()
      {
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

         dgvOrder.Columns.AddRange(new DataGridViewColumn[] {
            dgvOrderCert,
            dgvOrderCounter,
            dgvOrderNumCounter, 
            dgvOrderDataCounter,
            dgvOrderProtocol});
      }

      protected override GRSoft.Network.IDataSet CreateDsOrder()
      {
         return new DsOrderRcvEx(true);
      }

      protected override IList createDataSource()
      {
         return new List<OrderRcvEx>();
      }
      
   }
}
